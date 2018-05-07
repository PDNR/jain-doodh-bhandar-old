package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.NetworkInfo;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Picasso.Priority;
import com.squareup.picasso.RequestHandler.Result;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

class BitmapHunter implements Runnable {
    private static final Object DECODE_LOCK = new Object();
    private static final RequestHandler ERRORING_HANDLER = new RequestHandler() {
        public boolean canHandleRequest(Request request) {
            return true;
        }

        public Result load(Request request, int i) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unrecognized type of request: ");
            stringBuilder.append(request);
            throw new IllegalStateException(stringBuilder.toString());
        }
    };
    private static final ThreadLocal<StringBuilder> NAME_BUILDER = new ThreadLocal<StringBuilder>() {
        protected StringBuilder initialValue() {
            return new StringBuilder("Picasso-");
        }
    };
    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();
    Action action;
    List<Action> actions;
    final Cache cache;
    final Request data;
    final Dispatcher dispatcher;
    Exception exception;
    int exifRotation;
    Future<?> future;
    final String key;
    LoadedFrom loadedFrom;
    final int memoryPolicy;
    int networkPolicy;
    final Picasso picasso;
    Priority priority;
    final RequestHandler requestHandler;
    Bitmap result;
    int retryCount;
    final int sequence = SEQUENCE_GENERATOR.incrementAndGet();
    final Stats stats;

    private static boolean shouldResize(boolean z, int i, int i2, int i3, int i4) {
        if (z && i <= i3) {
            if (i2 <= i4) {
                return false;
            }
        }
        return true;
    }

    BitmapHunter(Picasso picasso, Dispatcher dispatcher, Cache cache, Stats stats, Action action, RequestHandler requestHandler) {
        this.picasso = picasso;
        this.dispatcher = dispatcher;
        this.cache = cache;
        this.stats = stats;
        this.action = action;
        this.key = action.getKey();
        this.data = action.getRequest();
        this.priority = action.getPriority();
        this.memoryPolicy = action.getMemoryPolicy();
        this.networkPolicy = action.getNetworkPolicy();
        this.requestHandler = requestHandler;
        this.retryCount = requestHandler.getRetryCount();
    }

    static Bitmap decodeStream(InputStream inputStream, Request request) throws IOException {
        InputStream markableInputStream = new MarkableInputStream(inputStream);
        long savePosition = markableInputStream.savePosition(65536);
        inputStream = RequestHandler.createBitmapOptions(request);
        boolean requiresInSampleSize = RequestHandler.requiresInSampleSize(inputStream);
        boolean isWebPFile = Utils.isWebPFile(markableInputStream);
        markableInputStream.reset(savePosition);
        if (isWebPFile) {
            byte[] toByteArray = Utils.toByteArray(markableInputStream);
            if (requiresInSampleSize) {
                BitmapFactory.decodeByteArray(toByteArray, 0, toByteArray.length, inputStream);
                RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, inputStream, request);
            }
            return BitmapFactory.decodeByteArray(toByteArray, 0, toByteArray.length, inputStream);
        }
        if (requiresInSampleSize) {
            BitmapFactory.decodeStream(markableInputStream, null, inputStream);
            RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, inputStream, request);
            markableInputStream.reset(savePosition);
        }
        inputStream = BitmapFactory.decodeStream(markableInputStream, null, inputStream);
        if (inputStream != null) {
            return inputStream;
        }
        throw new IOException("Failed to decode stream.");
    }

    public void run() {
        try {
            updateThreadName(this.data);
            if (this.picasso.loggingEnabled) {
                Utils.log("Hunter", "executing", Utils.getLogIdsForHunter(this));
            }
            this.result = hunt();
            if (this.result == null) {
                this.dispatcher.dispatchFailed(this);
            } else {
                this.dispatcher.dispatchComplete(this);
            }
        } catch (Exception e) {
            if (!(e.localCacheOnly && e.responseCode == 504)) {
                this.exception = e;
            }
            this.dispatcher.dispatchFailed(this);
        } catch (Exception e2) {
            this.exception = e2;
            this.dispatcher.dispatchRetry(this);
        } catch (Exception e22) {
            this.exception = e22;
            this.dispatcher.dispatchRetry(this);
        } catch (Throwable e3) {
            Writer stringWriter = new StringWriter();
            this.stats.createSnapshot().dump(new PrintWriter(stringWriter));
            this.exception = new RuntimeException(stringWriter.toString(), e3);
            this.dispatcher.dispatchFailed(this);
        } catch (Exception e222) {
            this.exception = e222;
            this.dispatcher.dispatchFailed(this);
        } catch (Throwable th) {
            Thread.currentThread().setName("Picasso-Idle");
        }
        Thread.currentThread().setName("Picasso-Idle");
    }

    Bitmap hunt() throws IOException {
        Bitmap bitmap;
        if (MemoryPolicy.shouldReadFromMemoryCache(this.memoryPolicy)) {
            bitmap = this.cache.get(this.key);
            if (bitmap != null) {
                this.stats.dispatchCacheHit();
                this.loadedFrom = LoadedFrom.MEMORY;
                if (this.picasso.loggingEnabled) {
                    Utils.log("Hunter", "decoded", this.data.logId(), "from cache");
                }
                return bitmap;
            }
        }
        bitmap = null;
        this.data.networkPolicy = this.retryCount == 0 ? NetworkPolicy.OFFLINE.index : this.networkPolicy;
        Result load = this.requestHandler.load(this.data, this.networkPolicy);
        if (load != null) {
            this.loadedFrom = load.getLoadedFrom();
            this.exifRotation = load.getExifOrientation();
            bitmap = load.getBitmap();
            if (bitmap == null) {
                InputStream stream = load.getStream();
                try {
                    Bitmap decodeStream = decodeStream(stream, this.data);
                    bitmap = decodeStream;
                } finally {
                    Utils.closeQuietly(stream);
                }
            }
        }
        if (bitmap != null) {
            if (this.picasso.loggingEnabled) {
                Utils.log("Hunter", "decoded", this.data.logId());
            }
            this.stats.dispatchBitmapDecoded(bitmap);
            if (this.data.needsTransformation() || this.exifRotation != 0) {
                synchronized (DECODE_LOCK) {
                    if (this.data.needsMatrixTransform() || this.exifRotation != 0) {
                        bitmap = transformResult(this.data, bitmap, this.exifRotation);
                        if (this.picasso.loggingEnabled) {
                            Utils.log("Hunter", "transformed", this.data.logId());
                        }
                    }
                    if (this.data.hasCustomTransformations()) {
                        bitmap = applyCustomTransformations(this.data.transformations, bitmap);
                        if (this.picasso.loggingEnabled) {
                            Utils.log("Hunter", "transformed", this.data.logId(), "from custom transformations");
                        }
                    }
                }
                if (bitmap != null) {
                    this.stats.dispatchBitmapTransformed(bitmap);
                }
            }
        }
        return bitmap;
    }

    void attach(Action action) {
        boolean z = this.picasso.loggingEnabled;
        Request request = action.request;
        if (this.action == null) {
            this.action = action;
            if (z) {
                if (this.actions != null) {
                    if (this.actions.isEmpty() == null) {
                        Utils.log("Hunter", "joined", request.logId(), Utils.getLogIdsForHunter(this, "to "));
                    }
                }
                Utils.log("Hunter", "joined", request.logId(), "to empty hunter");
            }
            return;
        }
        if (this.actions == null) {
            this.actions = new ArrayList(3);
        }
        this.actions.add(action);
        if (z) {
            Utils.log("Hunter", "joined", request.logId(), Utils.getLogIdsForHunter(this, "to "));
        }
        action = action.getPriority();
        if (action.ordinal() > this.priority.ordinal()) {
            this.priority = action;
        }
    }

    void detach(Action action) {
        boolean z;
        if (this.action == action) {
            this.action = null;
            z = true;
        } else {
            z = this.actions != null ? this.actions.remove(action) : false;
        }
        if (z && action.getPriority() == this.priority) {
            this.priority = computeNewPriority();
        }
        if (this.picasso.loggingEnabled) {
            Utils.log("Hunter", "removed", action.request.logId(), Utils.getLogIdsForHunter(this, "from "));
        }
    }

    private Priority computeNewPriority() {
        Priority priority = Priority.LOW;
        int i = 0;
        int i2 = 1;
        int i3 = (this.actions == null || this.actions.isEmpty()) ? 0 : 1;
        if (this.action == null) {
            if (i3 == 0) {
                i2 = 0;
            }
        }
        if (i2 == 0) {
            return priority;
        }
        if (this.action != null) {
            priority = this.action.getPriority();
        }
        if (i3 != 0) {
            i3 = this.actions.size();
            while (i < i3) {
                Priority priority2 = ((Action) this.actions.get(i)).getPriority();
                if (priority2.ordinal() > priority.ordinal()) {
                    priority = priority2;
                }
                i++;
            }
        }
        return priority;
    }

    boolean cancel() {
        if (this.action != null) {
            return false;
        }
        if ((this.actions == null || this.actions.isEmpty()) && this.future != null && this.future.cancel(false)) {
            return true;
        }
        return false;
    }

    boolean isCancelled() {
        return this.future != null && this.future.isCancelled();
    }

    boolean shouldRetry(boolean z, NetworkInfo networkInfo) {
        if ((this.retryCount > 0 ? 1 : false) == 0) {
            return false;
        }
        this.retryCount--;
        return this.requestHandler.shouldRetry(z, networkInfo);
    }

    boolean supportsReplay() {
        return this.requestHandler.supportsReplay();
    }

    Bitmap getResult() {
        return this.result;
    }

    String getKey() {
        return this.key;
    }

    int getMemoryPolicy() {
        return this.memoryPolicy;
    }

    Request getData() {
        return this.data;
    }

    Action getAction() {
        return this.action;
    }

    Picasso getPicasso() {
        return this.picasso;
    }

    List<Action> getActions() {
        return this.actions;
    }

    Exception getException() {
        return this.exception;
    }

    LoadedFrom getLoadedFrom() {
        return this.loadedFrom;
    }

    Priority getPriority() {
        return this.priority;
    }

    static void updateThreadName(Request request) {
        request = request.getName();
        StringBuilder stringBuilder = (StringBuilder) NAME_BUILDER.get();
        stringBuilder.ensureCapacity("Picasso-".length() + request.length());
        stringBuilder.replace("Picasso-".length(), stringBuilder.length(), request);
        Thread.currentThread().setName(stringBuilder.toString());
    }

    static BitmapHunter forRequest(Picasso picasso, Dispatcher dispatcher, Cache cache, Stats stats, Action action) {
        Request request = action.getRequest();
        List requestHandlers = picasso.getRequestHandlers();
        int size = requestHandlers.size();
        for (int i = 0; i < size; i++) {
            RequestHandler requestHandler = (RequestHandler) requestHandlers.get(i);
            if (requestHandler.canHandleRequest(request)) {
                return new BitmapHunter(picasso, dispatcher, cache, stats, action, requestHandler);
            }
        }
        return new BitmapHunter(picasso, dispatcher, cache, stats, action, ERRORING_HANDLER);
    }

    static Bitmap applyCustomTransformations(List<Transformation> list, Bitmap bitmap) {
        int size = list.size();
        int i = 0;
        while (i < size) {
            final Transformation transformation = (Transformation) list.get(i);
            try {
                Bitmap transform = transformation.transform(bitmap);
                if (transform == null) {
                    bitmap = new StringBuilder();
                    bitmap.append("Transformation ");
                    bitmap.append(transformation.key());
                    bitmap.append(" returned null after ");
                    bitmap.append(i);
                    bitmap.append(" previous transformation(s).\n\nTransformation list:\n");
                    for (Transformation key : list) {
                        bitmap.append(key.key());
                        bitmap.append('\n');
                    }
                    Picasso.HANDLER.post(new Runnable() {
                        public void run() {
                            throw new NullPointerException(bitmap.toString());
                        }
                    });
                    return null;
                } else if (transform == bitmap && bitmap.isRecycled()) {
                    Picasso.HANDLER.post(new Runnable() {
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Transformation ");
                            stringBuilder.append(transformation.key());
                            stringBuilder.append(" returned input Bitmap but recycled it.");
                            throw new IllegalStateException(stringBuilder.toString());
                        }
                    });
                    return null;
                } else if (transform == bitmap || bitmap.isRecycled() != null) {
                    i++;
                    bitmap = transform;
                } else {
                    Picasso.HANDLER.post(new Runnable() {
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Transformation ");
                            stringBuilder.append(transformation.key());
                            stringBuilder.append(" mutated input Bitmap but failed to recycle the original.");
                            throw new IllegalStateException(stringBuilder.toString());
                        }
                    });
                    return null;
                }
            } catch (final List<Transformation> list2) {
                Picasso.HANDLER.post(new Runnable() {
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Transformation ");
                        stringBuilder.append(transformation.key());
                        stringBuilder.append(" crashed with exception.");
                        throw new RuntimeException(stringBuilder.toString(), list2);
                    }
                });
                return null;
            }
        }
        return bitmap;
    }

    static Bitmap transformResult(Request request, Bitmap bitmap, int i) {
        int i2;
        int i3;
        int i4;
        Bitmap createBitmap;
        Bitmap bitmap2;
        Request request2 = request;
        int i5 = i;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        boolean z = request2.onlyScaleDown;
        Matrix matrix = new Matrix();
        int i6 = 0;
        if (request.needsMatrixTransform()) {
            int i7 = request2.targetWidth;
            i2 = request2.targetHeight;
            float f = request2.rotationDegrees;
            if (f != 0.0f) {
                if (request2.hasRotationPivot) {
                    matrix.setRotate(f, request2.rotationPivotX, request2.rotationPivotY);
                } else {
                    matrix.setRotate(f);
                }
            }
            float f2;
            float f3;
            if (request2.centerCrop) {
                int i8;
                f2 = (float) i7;
                f = (float) width;
                f3 = f2 / f;
                float f4 = (float) i2;
                float f5 = (float) height;
                float f6 = f4 / f5;
                if (f3 > f6) {
                    int ceil = (int) Math.ceil((double) (f5 * (f6 / f3)));
                    i3 = (height - ceil) / 2;
                    f6 = f4 / ((float) ceil);
                    i8 = ceil;
                    f2 = f3;
                    i4 = width;
                } else {
                    i3 = (int) Math.ceil((double) (f * (f3 / f6)));
                    f2 /= (float) i3;
                    i8 = height;
                    int i9 = i3;
                    i3 = 0;
                    i6 = (width - i3) / 2;
                    i4 = i9;
                }
                if (shouldResize(z, width, height, i7, i2)) {
                    matrix.preScale(f2, f6);
                }
                i2 = i3;
                i3 = i4;
                i4 = i8;
                if (i5 != 0) {
                    matrix.preRotate((float) i5);
                }
                createBitmap = Bitmap.createBitmap(bitmap, i6, i2, i3, i4, matrix, true);
                bitmap2 = bitmap;
                if (createBitmap != bitmap2) {
                    return bitmap2;
                }
                bitmap.recycle();
                return createBitmap;
            } else if (request2.centerInside) {
                f2 = ((float) i7) / ((float) width);
                f = ((float) i2) / ((float) height);
                if (f2 >= f) {
                    f2 = f;
                }
                if (shouldResize(z, width, height, i7, i2)) {
                    matrix.preScale(f2, f2);
                }
            } else if (!((i7 == 0 && i2 == 0) || (i7 == width && i2 == height))) {
                if (i7 != 0) {
                    f2 = (float) i7;
                    f = (float) width;
                } else {
                    f2 = (float) i2;
                    f = (float) height;
                }
                f2 /= f;
                if (i2 != 0) {
                    f = (float) i2;
                    f3 = (float) height;
                } else {
                    f = (float) i7;
                    f3 = (float) width;
                }
                f /= f3;
                if (shouldResize(z, width, height, i7, i2)) {
                    matrix.preScale(f2, f);
                }
            }
        }
        i3 = width;
        i4 = height;
        i2 = 0;
        if (i5 != 0) {
            matrix.preRotate((float) i5);
        }
        createBitmap = Bitmap.createBitmap(bitmap, i6, i2, i3, i4, matrix, true);
        bitmap2 = bitmap;
        if (createBitmap != bitmap2) {
            return bitmap2;
        }
        bitmap.recycle();
        return createBitmap;
    }
}
