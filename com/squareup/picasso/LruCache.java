package com.squareup.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class LruCache implements Cache {
    private int evictionCount;
    private int hitCount;
    final LinkedHashMap<String, Bitmap> map;
    private final int maxSize;
    private int missCount;
    private int putCount;
    private int size;

    public LruCache(Context context) {
        this(Utils.calculateMemoryCacheSize(context));
    }

    public LruCache(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("Max size must be positive.");
        }
        this.maxSize = i;
        this.map = new LinkedHashMap(0, 0.75f, true);
    }

    public Bitmap get(String str) {
        if (str == null) {
            throw new NullPointerException("key == null");
        }
        synchronized (this) {
            Bitmap bitmap = (Bitmap) this.map.get(str);
            if (bitmap != null) {
                this.hitCount++;
                return bitmap;
            }
            this.missCount++;
            return null;
        }
    }

    public void set(String str, Bitmap bitmap) {
        if (str != null) {
            if (bitmap != null) {
                synchronized (this) {
                    this.putCount++;
                    this.size += Utils.getBitmapBytes(bitmap);
                    Bitmap bitmap2 = (Bitmap) this.map.put(str, bitmap);
                    if (bitmap2 != null) {
                        this.size -= Utils.getBitmapBytes(bitmap2);
                    }
                }
                trimToSize(this.maxSize);
                return;
            }
        }
        throw new NullPointerException("key == null || bitmap == null");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void trimToSize(int i) {
        while (true) {
            synchronized (this) {
                if (this.size >= 0) {
                    if (this.map.isEmpty() && this.size != 0) {
                        break;
                    } else if (this.size <= i) {
                        break;
                    } else if (this.map.isEmpty()) {
                        break;
                    } else {
                        Entry entry = (Entry) this.map.entrySet().iterator().next();
                        String str = (String) entry.getKey();
                        Bitmap bitmap = (Bitmap) entry.getValue();
                        this.map.remove(str);
                        this.size -= Utils.getBitmapBytes(bitmap);
                        this.evictionCount++;
                    }
                } else {
                    break;
                }
            }
        }
    }

    public final void evictAll() {
        trimToSize(-1);
    }

    public final synchronized int size() {
        return this.size;
    }

    public final synchronized int maxSize() {
        return this.maxSize;
    }

    public final synchronized void clear() {
        evictAll();
    }

    public final synchronized void clearKeyUri(String str) {
        int length = str.length();
        Iterator it = this.map.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            String str2 = (String) entry.getKey();
            Bitmap bitmap = (Bitmap) entry.getValue();
            int indexOf = str2.indexOf(10);
            if (indexOf == length && str2.substring(0, indexOf).equals(str)) {
                it.remove();
                this.size -= Utils.getBitmapBytes(bitmap);
                i = 1;
            }
        }
        if (i != 0) {
            trimToSize(this.maxSize);
        }
    }

    public final synchronized int hitCount() {
        return this.hitCount;
    }

    public final synchronized int missCount() {
        return this.missCount;
    }

    public final synchronized int putCount() {
        return this.putCount;
    }

    public final synchronized int evictionCount() {
        return this.evictionCount;
    }
}
