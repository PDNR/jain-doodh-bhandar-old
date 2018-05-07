package android.support.v4.app;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.Loader.OnLoadCanceledListener;
import android.support.v4.content.Loader.OnLoadCompleteListener;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;

/* compiled from: LoaderManager */
class LoaderManagerImpl extends LoaderManager {
    static boolean DEBUG = false;
    static final String TAG = "LoaderManager";
    boolean mCreatingLoader;
    FragmentHostCallback mHost;
    final SparseArrayCompat<LoaderInfo> mInactiveLoaders = new SparseArrayCompat();
    final SparseArrayCompat<LoaderInfo> mLoaders = new SparseArrayCompat();
    boolean mRetaining;
    boolean mRetainingStarted;
    boolean mStarted;
    final String mWho;

    /* compiled from: LoaderManager */
    final class LoaderInfo implements OnLoadCompleteListener<Object>, OnLoadCanceledListener<Object> {
        final Bundle mArgs;
        LoaderCallbacks<Object> mCallbacks;
        Object mData;
        boolean mDeliveredData;
        boolean mDestroyed;
        boolean mHaveData;
        final int mId;
        boolean mListenerRegistered;
        Loader<Object> mLoader;
        LoaderInfo mPendingLoader;
        boolean mReportNextStart;
        boolean mRetaining;
        boolean mRetainingStarted;
        boolean mStarted;

        public LoaderInfo(int i, Bundle bundle, LoaderCallbacks<Object> loaderCallbacks) {
            this.mId = i;
            this.mArgs = bundle;
            this.mCallbacks = loaderCallbacks;
        }

        void start() {
            if (this.mRetaining && this.mRetainingStarted) {
                this.mStarted = true;
            } else if (!this.mStarted) {
                this.mStarted = true;
                if (LoaderManagerImpl.DEBUG) {
                    String str = LoaderManagerImpl.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("  Starting: ");
                    stringBuilder.append(this);
                    Log.v(str, stringBuilder.toString());
                }
                if (this.mLoader == null && this.mCallbacks != null) {
                    this.mLoader = this.mCallbacks.onCreateLoader(this.mId, this.mArgs);
                }
                if (this.mLoader != null) {
                    if (!this.mLoader.getClass().isMemberClass() || Modifier.isStatic(this.mLoader.getClass().getModifiers())) {
                        if (!this.mListenerRegistered) {
                            this.mLoader.registerListener(this.mId, this);
                            this.mLoader.registerOnLoadCanceledListener(this);
                            this.mListenerRegistered = true;
                        }
                        this.mLoader.startLoading();
                    } else {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Object returned from onCreateLoader must not be a non-static inner member class: ");
                        stringBuilder2.append(this.mLoader);
                        throw new IllegalArgumentException(stringBuilder2.toString());
                    }
                }
            }
        }

        void retain() {
            if (LoaderManagerImpl.DEBUG) {
                String str = LoaderManagerImpl.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("  Retaining: ");
                stringBuilder.append(this);
                Log.v(str, stringBuilder.toString());
            }
            this.mRetaining = true;
            this.mRetainingStarted = this.mStarted;
            this.mStarted = false;
            this.mCallbacks = null;
        }

        void finishRetain() {
            if (this.mRetaining) {
                if (LoaderManagerImpl.DEBUG) {
                    String str = LoaderManagerImpl.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("  Finished Retaining: ");
                    stringBuilder.append(this);
                    Log.v(str, stringBuilder.toString());
                }
                this.mRetaining = false;
                if (!(this.mStarted == this.mRetainingStarted || this.mStarted)) {
                    stop();
                }
            }
            if (this.mStarted && this.mHaveData && !this.mReportNextStart) {
                callOnLoadFinished(this.mLoader, this.mData);
            }
        }

        void reportStart() {
            if (this.mStarted && this.mReportNextStart) {
                this.mReportNextStart = false;
                if (this.mHaveData && !this.mRetaining) {
                    callOnLoadFinished(this.mLoader, this.mData);
                }
            }
        }

        void stop() {
            if (LoaderManagerImpl.DEBUG) {
                String str = LoaderManagerImpl.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("  Stopping: ");
                stringBuilder.append(this);
                Log.v(str, stringBuilder.toString());
            }
            this.mStarted = false;
            if (!this.mRetaining && this.mLoader != null && this.mListenerRegistered) {
                this.mListenerRegistered = false;
                this.mLoader.unregisterListener(this);
                this.mLoader.unregisterOnLoadCanceledListener(this);
                this.mLoader.stopLoading();
            }
        }

        boolean cancel() {
            if (LoaderManagerImpl.DEBUG) {
                String str = LoaderManagerImpl.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("  Canceling: ");
                stringBuilder.append(this);
                Log.v(str, stringBuilder.toString());
            }
            if (!this.mStarted || this.mLoader == null || !this.mListenerRegistered) {
                return false;
            }
            boolean cancelLoad = this.mLoader.cancelLoad();
            if (!cancelLoad) {
                onLoadCanceled(this.mLoader);
            }
            return cancelLoad;
        }

        void destroy() {
            if (LoaderManagerImpl.DEBUG) {
                String str = LoaderManagerImpl.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("  Destroying: ");
                stringBuilder.append(this);
                Log.v(str, stringBuilder.toString());
            }
            this.mDestroyed = true;
            boolean z = this.mDeliveredData;
            this.mDeliveredData = false;
            if (this.mCallbacks != null && this.mLoader != null && this.mHaveData && z) {
                if (LoaderManagerImpl.DEBUG) {
                    str = LoaderManagerImpl.TAG;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("  Resetting: ");
                    stringBuilder2.append(this);
                    Log.v(str, stringBuilder2.toString());
                }
                if (LoaderManagerImpl.this.mHost != null) {
                    str = LoaderManagerImpl.this.mHost.mFragmentManager.mNoTransactionsBecause;
                    LoaderManagerImpl.this.mHost.mFragmentManager.mNoTransactionsBecause = "onLoaderReset";
                } else {
                    str = null;
                }
                try {
                    this.mCallbacks.onLoaderReset(this.mLoader);
                } finally {
                    if (LoaderManagerImpl.this.mHost != null) {
                        LoaderManagerImpl.this.mHost.mFragmentManager.mNoTransactionsBecause = str;
                    }
                }
            }
            this.mCallbacks = null;
            this.mData = null;
            this.mHaveData = false;
            if (this.mLoader != null) {
                if (this.mListenerRegistered) {
                    this.mListenerRegistered = false;
                    this.mLoader.unregisterListener(this);
                    this.mLoader.unregisterOnLoadCanceledListener(this);
                }
                this.mLoader.reset();
            }
            if (this.mPendingLoader != null) {
                this.mPendingLoader.destroy();
            }
        }

        public void onLoadCanceled(Loader<Object> loader) {
            if (LoaderManagerImpl.DEBUG != null) {
                loader = LoaderManagerImpl.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onLoadCanceled: ");
                stringBuilder.append(this);
                Log.v(loader, stringBuilder.toString());
            }
            if (this.mDestroyed != null) {
                if (LoaderManagerImpl.DEBUG != null) {
                    Log.v(LoaderManagerImpl.TAG, "  Ignoring load canceled -- destroyed");
                }
            } else if (LoaderManagerImpl.this.mLoaders.get(this.mId) != this) {
                if (LoaderManagerImpl.DEBUG != null) {
                    Log.v(LoaderManagerImpl.TAG, "  Ignoring load canceled -- not active");
                }
            } else {
                loader = this.mPendingLoader;
                if (loader != null) {
                    if (LoaderManagerImpl.DEBUG) {
                        String str = LoaderManagerImpl.TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("  Switching to pending loader: ");
                        stringBuilder2.append(loader);
                        Log.v(str, stringBuilder2.toString());
                    }
                    this.mPendingLoader = null;
                    LoaderManagerImpl.this.mLoaders.put(this.mId, null);
                    destroy();
                    LoaderManagerImpl.this.installLoader(loader);
                }
            }
        }

        public void onLoadComplete(Loader<Object> loader, Object obj) {
            if (LoaderManagerImpl.DEBUG) {
                String str = LoaderManagerImpl.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onLoadComplete: ");
                stringBuilder.append(this);
                Log.v(str, stringBuilder.toString());
            }
            if (this.mDestroyed) {
                if (LoaderManagerImpl.DEBUG != null) {
                    Log.v(LoaderManagerImpl.TAG, "  Ignoring load complete -- destroyed");
                }
            } else if (LoaderManagerImpl.this.mLoaders.get(this.mId) != this) {
                if (LoaderManagerImpl.DEBUG != null) {
                    Log.v(LoaderManagerImpl.TAG, "  Ignoring load complete -- not active");
                }
            } else {
                LoaderInfo loaderInfo = this.mPendingLoader;
                if (loaderInfo != null) {
                    if (LoaderManagerImpl.DEBUG != null) {
                        loader = LoaderManagerImpl.TAG;
                        obj = new StringBuilder();
                        obj.append("  Switching to pending loader: ");
                        obj.append(loaderInfo);
                        Log.v(loader, obj.toString());
                    }
                    this.mPendingLoader = null;
                    LoaderManagerImpl.this.mLoaders.put(this.mId, null);
                    destroy();
                    LoaderManagerImpl.this.installLoader(loaderInfo);
                    return;
                }
                if (!(this.mData == obj && this.mHaveData)) {
                    this.mData = obj;
                    this.mHaveData = true;
                    if (this.mStarted) {
                        callOnLoadFinished(loader, obj);
                    }
                }
                LoaderInfo loaderInfo2 = (LoaderInfo) LoaderManagerImpl.this.mInactiveLoaders.get(this.mId);
                if (!(loaderInfo2 == null || loaderInfo2 == this)) {
                    loaderInfo2.mDeliveredData = null;
                    loaderInfo2.destroy();
                    LoaderManagerImpl.this.mInactiveLoaders.remove(this.mId);
                }
                if (LoaderManagerImpl.this.mHost != null && LoaderManagerImpl.this.hasRunningLoaders() == null) {
                    LoaderManagerImpl.this.mHost.mFragmentManager.startPendingDeferredFragments();
                }
            }
        }

        void callOnLoadFinished(Loader<Object> loader, Object obj) {
            if (this.mCallbacks != null) {
                String str = null;
                if (LoaderManagerImpl.this.mHost != null) {
                    str = LoaderManagerImpl.this.mHost.mFragmentManager.mNoTransactionsBecause;
                    LoaderManagerImpl.this.mHost.mFragmentManager.mNoTransactionsBecause = "onLoadFinished";
                }
                try {
                    if (LoaderManagerImpl.DEBUG) {
                        String str2 = LoaderManagerImpl.TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("  onLoadFinished in ");
                        stringBuilder.append(loader);
                        stringBuilder.append(": ");
                        stringBuilder.append(loader.dataToString(obj));
                        Log.v(str2, stringBuilder.toString());
                    }
                    this.mCallbacks.onLoadFinished(loader, obj);
                    this.mDeliveredData = true;
                } finally {
                    if (LoaderManagerImpl.this.mHost != null) {
                        LoaderManagerImpl.this.mHost.mFragmentManager.mNoTransactionsBecause = str;
                    }
                }
            }
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(64);
            stringBuilder.append("LoaderInfo{");
            stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
            stringBuilder.append(" #");
            stringBuilder.append(this.mId);
            stringBuilder.append(" : ");
            DebugUtils.buildShortClassTag(this.mLoader, stringBuilder);
            stringBuilder.append("}}");
            return stringBuilder.toString();
        }

        public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.print(str);
            printWriter.print("mId=");
            printWriter.print(this.mId);
            printWriter.print(" mArgs=");
            printWriter.println(this.mArgs);
            printWriter.print(str);
            printWriter.print("mCallbacks=");
            printWriter.println(this.mCallbacks);
            printWriter.print(str);
            printWriter.print("mLoader=");
            printWriter.println(this.mLoader);
            if (this.mLoader != null) {
                Loader loader = this.mLoader;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("  ");
                loader.dump(stringBuilder.toString(), fileDescriptor, printWriter, strArr);
            }
            if (this.mHaveData || this.mDeliveredData) {
                printWriter.print(str);
                printWriter.print("mHaveData=");
                printWriter.print(this.mHaveData);
                printWriter.print("  mDeliveredData=");
                printWriter.println(this.mDeliveredData);
                printWriter.print(str);
                printWriter.print("mData=");
                printWriter.println(this.mData);
            }
            printWriter.print(str);
            printWriter.print("mStarted=");
            printWriter.print(this.mStarted);
            printWriter.print(" mReportNextStart=");
            printWriter.print(this.mReportNextStart);
            printWriter.print(" mDestroyed=");
            printWriter.println(this.mDestroyed);
            printWriter.print(str);
            printWriter.print("mRetaining=");
            printWriter.print(this.mRetaining);
            printWriter.print(" mRetainingStarted=");
            printWriter.print(this.mRetainingStarted);
            printWriter.print(" mListenerRegistered=");
            printWriter.println(this.mListenerRegistered);
            if (this.mPendingLoader != null) {
                printWriter.print(str);
                printWriter.println("Pending Loader ");
                printWriter.print(this.mPendingLoader);
                printWriter.println(":");
                LoaderInfo loaderInfo = this.mPendingLoader;
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("  ");
                loaderInfo.dump(stringBuilder.toString(), fileDescriptor, printWriter, strArr);
            }
        }
    }

    LoaderManagerImpl(String str, FragmentHostCallback fragmentHostCallback, boolean z) {
        this.mWho = str;
        this.mHost = fragmentHostCallback;
        this.mStarted = z;
    }

    void updateHostController(FragmentHostCallback fragmentHostCallback) {
        this.mHost = fragmentHostCallback;
    }

    private LoaderInfo createLoader(int i, Bundle bundle, LoaderCallbacks<Object> loaderCallbacks) {
        LoaderInfo loaderInfo = new LoaderInfo(i, bundle, loaderCallbacks);
        loaderInfo.mLoader = loaderCallbacks.onCreateLoader(i, bundle);
        return loaderInfo;
    }

    private LoaderInfo createAndInstallLoader(int i, Bundle bundle, LoaderCallbacks<Object> loaderCallbacks) {
        try {
            this.mCreatingLoader = true;
            i = createLoader(i, bundle, loaderCallbacks);
            installLoader(i);
            return i;
        } finally {
            this.mCreatingLoader = false;
        }
    }

    void installLoader(LoaderInfo loaderInfo) {
        this.mLoaders.put(loaderInfo.mId, loaderInfo);
        if (this.mStarted) {
            loaderInfo.start();
        }
    }

    public <D> Loader<D> initLoader(int i, Bundle bundle, LoaderCallbacks<D> loaderCallbacks) {
        if (this.mCreatingLoader) {
            throw new IllegalStateException("Called while creating a loader");
        }
        LoaderInfo loaderInfo = (LoaderInfo) this.mLoaders.get(i);
        if (DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("initLoader in ");
            stringBuilder.append(this);
            stringBuilder.append(": args=");
            stringBuilder.append(bundle);
            Log.v(str, stringBuilder.toString());
        }
        if (loaderInfo == null) {
            loaderInfo = createAndInstallLoader(i, bundle, loaderCallbacks);
            if (DEBUG != 0) {
                i = TAG;
                bundle = new StringBuilder();
                bundle.append("  Created new loader ");
                bundle.append(loaderInfo);
                Log.v(i, bundle.toString());
            }
        } else {
            if (DEBUG != 0) {
                i = TAG;
                bundle = new StringBuilder();
                bundle.append("  Re-using existing loader ");
                bundle.append(loaderInfo);
                Log.v(i, bundle.toString());
            }
            loaderInfo.mCallbacks = loaderCallbacks;
        }
        if (!(loaderInfo.mHaveData == 0 || this.mStarted == 0)) {
            loaderInfo.callOnLoadFinished(loaderInfo.mLoader, loaderInfo.mData);
        }
        return loaderInfo.mLoader;
    }

    public <D> Loader<D> restartLoader(int i, Bundle bundle, LoaderCallbacks<D> loaderCallbacks) {
        if (this.mCreatingLoader) {
            throw new IllegalStateException("Called while creating a loader");
        }
        LoaderInfo loaderInfo = (LoaderInfo) this.mLoaders.get(i);
        if (DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("restartLoader in ");
            stringBuilder.append(this);
            stringBuilder.append(": args=");
            stringBuilder.append(bundle);
            Log.v(str, stringBuilder.toString());
        }
        if (loaderInfo != null) {
            LoaderInfo loaderInfo2 = (LoaderInfo) this.mInactiveLoaders.get(i);
            if (loaderInfo2 == null) {
                if (DEBUG) {
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("  Making last loader inactive: ");
                    stringBuilder.append(loaderInfo);
                    Log.v(str, stringBuilder.toString());
                }
                loaderInfo.mLoader.abandon();
                this.mInactiveLoaders.put(i, loaderInfo);
            } else if (loaderInfo.mHaveData) {
                if (DEBUG) {
                    String str2 = TAG;
                    r3 = new StringBuilder();
                    r3.append("  Removing last inactive loader: ");
                    r3.append(loaderInfo);
                    Log.v(str2, r3.toString());
                }
                loaderInfo2.mDeliveredData = false;
                loaderInfo2.destroy();
                loaderInfo.mLoader.abandon();
                this.mInactiveLoaders.put(i, loaderInfo);
            } else if (loaderInfo.cancel()) {
                if (DEBUG) {
                    Log.v(TAG, "  Current loader is running; configuring pending loader");
                }
                if (loaderInfo.mPendingLoader != null) {
                    if (DEBUG) {
                        str = TAG;
                        r3 = new StringBuilder();
                        r3.append("  Removing pending loader: ");
                        r3.append(loaderInfo.mPendingLoader);
                        Log.v(str, r3.toString());
                    }
                    loaderInfo.mPendingLoader.destroy();
                    loaderInfo.mPendingLoader = null;
                }
                if (DEBUG) {
                    Log.v(TAG, "  Enqueuing as new pending loader");
                }
                loaderInfo.mPendingLoader = createLoader(i, bundle, loaderCallbacks);
                return loaderInfo.mPendingLoader.mLoader;
            } else {
                if (DEBUG) {
                    Log.v(TAG, "  Current loader is stopped; replacing");
                }
                this.mLoaders.put(i, null);
                loaderInfo.destroy();
            }
        }
        return createAndInstallLoader(i, bundle, loaderCallbacks).mLoader;
    }

    public void destroyLoader(int i) {
        if (this.mCreatingLoader) {
            throw new IllegalStateException("Called while creating a loader");
        }
        if (DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("destroyLoader in ");
            stringBuilder.append(this);
            stringBuilder.append(" of ");
            stringBuilder.append(i);
            Log.v(str, stringBuilder.toString());
        }
        int indexOfKey = this.mLoaders.indexOfKey(i);
        if (indexOfKey >= 0) {
            LoaderInfo loaderInfo = (LoaderInfo) this.mLoaders.valueAt(indexOfKey);
            this.mLoaders.removeAt(indexOfKey);
            loaderInfo.destroy();
        }
        i = this.mInactiveLoaders.indexOfKey(i);
        if (i >= 0) {
            LoaderInfo loaderInfo2 = (LoaderInfo) this.mInactiveLoaders.valueAt(i);
            this.mInactiveLoaders.removeAt(i);
            loaderInfo2.destroy();
        }
        if (this.mHost != 0 && hasRunningLoaders() == 0) {
            this.mHost.mFragmentManager.startPendingDeferredFragments();
        }
    }

    public <D> Loader<D> getLoader(int i) {
        if (this.mCreatingLoader) {
            throw new IllegalStateException("Called while creating a loader");
        }
        LoaderInfo loaderInfo = (LoaderInfo) this.mLoaders.get(i);
        if (loaderInfo == null) {
            return 0;
        }
        if (loaderInfo.mPendingLoader != null) {
            return loaderInfo.mPendingLoader.mLoader;
        }
        return loaderInfo.mLoader;
    }

    void doStart() {
        if (DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Starting in ");
            stringBuilder.append(this);
            Log.v(str, stringBuilder.toString());
        }
        if (this.mStarted) {
            Throwable runtimeException = new RuntimeException("here");
            runtimeException.fillInStackTrace();
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Called doStart when already started: ");
            stringBuilder2.append(this);
            Log.w(str2, stringBuilder2.toString(), runtimeException);
            return;
        }
        this.mStarted = true;
        for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
            ((LoaderInfo) this.mLoaders.valueAt(size)).start();
        }
    }

    void doStop() {
        if (DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Stopping in ");
            stringBuilder.append(this);
            Log.v(str, stringBuilder.toString());
        }
        if (this.mStarted) {
            for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
                ((LoaderInfo) this.mLoaders.valueAt(size)).stop();
            }
            this.mStarted = false;
            return;
        }
        Throwable runtimeException = new RuntimeException("here");
        runtimeException.fillInStackTrace();
        String str2 = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Called doStop when not started: ");
        stringBuilder2.append(this);
        Log.w(str2, stringBuilder2.toString(), runtimeException);
    }

    void doRetain() {
        if (DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Retaining in ");
            stringBuilder.append(this);
            Log.v(str, stringBuilder.toString());
        }
        if (this.mStarted) {
            this.mRetaining = true;
            this.mStarted = false;
            for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
                ((LoaderInfo) this.mLoaders.valueAt(size)).retain();
            }
            return;
        }
        Throwable runtimeException = new RuntimeException("here");
        runtimeException.fillInStackTrace();
        String str2 = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Called doRetain when not started: ");
        stringBuilder2.append(this);
        Log.w(str2, stringBuilder2.toString(), runtimeException);
    }

    void finishRetain() {
        if (this.mRetaining) {
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Finished Retaining in ");
                stringBuilder.append(this);
                Log.v(str, stringBuilder.toString());
            }
            this.mRetaining = false;
            for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
                ((LoaderInfo) this.mLoaders.valueAt(size)).finishRetain();
            }
        }
    }

    void doReportNextStart() {
        for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
            ((LoaderInfo) this.mLoaders.valueAt(size)).mReportNextStart = true;
        }
    }

    void doReportStart() {
        for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
            ((LoaderInfo) this.mLoaders.valueAt(size)).reportStart();
        }
    }

    void doDestroy() {
        int size;
        if (!this.mRetaining) {
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Destroying Active in ");
                stringBuilder.append(this);
                Log.v(str, stringBuilder.toString());
            }
            for (size = this.mLoaders.size() - 1; size >= 0; size--) {
                ((LoaderInfo) this.mLoaders.valueAt(size)).destroy();
            }
            this.mLoaders.clear();
        }
        if (DEBUG) {
            str = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Destroying Inactive in ");
            stringBuilder.append(this);
            Log.v(str, stringBuilder.toString());
        }
        for (size = this.mInactiveLoaders.size() - 1; size >= 0; size--) {
            ((LoaderInfo) this.mInactiveLoaders.valueAt(size)).destroy();
        }
        this.mInactiveLoaders.clear();
        this.mHost = null;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(128);
        stringBuilder.append("LoaderManager{");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append(" in ");
        DebugUtils.buildShortClassTag(this.mHost, stringBuilder);
        stringBuilder.append("}}");
        return stringBuilder.toString();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String stringBuilder;
        int i = 0;
        if (this.mLoaders.size() > 0) {
            printWriter.print(str);
            printWriter.println("Active Loaders:");
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append("    ");
            stringBuilder = stringBuilder2.toString();
            for (int i2 = 0; i2 < this.mLoaders.size(); i2++) {
                LoaderInfo loaderInfo = (LoaderInfo) this.mLoaders.valueAt(i2);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(this.mLoaders.keyAt(i2));
                printWriter.print(": ");
                printWriter.println(loaderInfo.toString());
                loaderInfo.dump(stringBuilder, fileDescriptor, printWriter, strArr);
            }
        }
        if (this.mInactiveLoaders.size() > 0) {
            printWriter.print(str);
            printWriter.println("Inactive Loaders:");
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append("    ");
            stringBuilder = stringBuilder2.toString();
            while (i < this.mInactiveLoaders.size()) {
                LoaderInfo loaderInfo2 = (LoaderInfo) this.mInactiveLoaders.valueAt(i);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(this.mInactiveLoaders.keyAt(i));
                printWriter.print(": ");
                printWriter.println(loaderInfo2.toString());
                loaderInfo2.dump(stringBuilder, fileDescriptor, printWriter, strArr);
                i++;
            }
        }
    }

    public boolean hasRunningLoaders() {
        int size = this.mLoaders.size();
        int i = 0;
        boolean z = i;
        while (i < size) {
            LoaderInfo loaderInfo = (LoaderInfo) this.mLoaders.valueAt(i);
            int i2 = (!loaderInfo.mStarted || loaderInfo.mDeliveredData) ? 0 : 1;
            z |= i2;
            i++;
        }
        return z;
    }
}
