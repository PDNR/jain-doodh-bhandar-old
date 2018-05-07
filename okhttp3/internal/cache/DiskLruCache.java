package okhttp3.internal.cache;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.platform.Platform;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public final class DiskLruCache implements Closeable, Flushable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    static final long ANY_SEQUENCE_NUMBER = -1;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final Pattern LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,120}");
    static final String MAGIC = "libcore.io.DiskLruCache";
    private static final String READ = "READ";
    private static final String REMOVE = "REMOVE";
    static final String VERSION_1 = "1";
    private final int appVersion;
    private final Runnable cleanupRunnable = new Runnable() {
        public void run() {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r4 = this;
            r0 = okhttp3.internal.cache.DiskLruCache.this;
            monitor-enter(r0);
            r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ all -> 0x0041 }
            r1 = r1.initialized;	 Catch:{ all -> 0x0041 }
            r2 = 1;	 Catch:{ all -> 0x0041 }
            r1 = r1 ^ r2;	 Catch:{ all -> 0x0041 }
            r3 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ all -> 0x0041 }
            r3 = r3.closed;	 Catch:{ all -> 0x0041 }
            r1 = r1 | r3;	 Catch:{ all -> 0x0041 }
            if (r1 == 0) goto L_0x0012;	 Catch:{ all -> 0x0041 }
        L_0x0010:
            monitor-exit(r0);	 Catch:{ all -> 0x0041 }
            return;
        L_0x0012:
            r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ IOException -> 0x0018 }
            r1.trimToSize();	 Catch:{ IOException -> 0x0018 }
            goto L_0x001c;
        L_0x0018:
            r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ all -> 0x0041 }
            r1.mostRecentTrimFailed = r2;	 Catch:{ all -> 0x0041 }
        L_0x001c:
            r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ IOException -> 0x002f }
            r1 = r1.journalRebuildRequired();	 Catch:{ IOException -> 0x002f }
            if (r1 == 0) goto L_0x003f;	 Catch:{ IOException -> 0x002f }
        L_0x0024:
            r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ IOException -> 0x002f }
            r1.rebuildJournal();	 Catch:{ IOException -> 0x002f }
            r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ IOException -> 0x002f }
            r3 = 0;	 Catch:{ IOException -> 0x002f }
            r1.redundantOpCount = r3;	 Catch:{ IOException -> 0x002f }
            goto L_0x003f;
        L_0x002f:
            r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ all -> 0x0041 }
            r1.mostRecentRebuildFailed = r2;	 Catch:{ all -> 0x0041 }
            r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ all -> 0x0041 }
            r2 = okio.Okio.blackhole();	 Catch:{ all -> 0x0041 }
            r2 = okio.Okio.buffer(r2);	 Catch:{ all -> 0x0041 }
            r1.journalWriter = r2;	 Catch:{ all -> 0x0041 }
        L_0x003f:
            monitor-exit(r0);	 Catch:{ all -> 0x0041 }
            return;	 Catch:{ all -> 0x0041 }
        L_0x0041:
            r1 = move-exception;	 Catch:{ all -> 0x0041 }
            monitor-exit(r0);	 Catch:{ all -> 0x0041 }
            throw r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.1.run():void");
        }
    };
    boolean closed;
    final File directory;
    private final Executor executor;
    final FileSystem fileSystem;
    boolean hasJournalErrors;
    boolean initialized;
    private final File journalFile;
    private final File journalFileBackup;
    private final File journalFileTmp;
    BufferedSink journalWriter;
    final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap(0, 0.75f, true);
    private long maxSize;
    boolean mostRecentRebuildFailed;
    boolean mostRecentTrimFailed;
    private long nextSequenceNumber = 0;
    int redundantOpCount;
    private long size = 0;
    final int valueCount;

    public final class Editor {
        private boolean done;
        final Entry entry;
        final boolean[] written;

        Editor(Entry entry) {
            this.entry = entry;
            this.written = entry.readable != null ? null : new boolean[DiskLruCache.this.valueCount];
        }

        void detach() {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r3 = this;
            r0 = r3.entry;
            r0 = r0.currentEditor;
            if (r0 != r3) goto L_0x0022;
        L_0x0006:
            r0 = 0;
        L_0x0007:
            r1 = okhttp3.internal.cache.DiskLruCache.this;
            r1 = r1.valueCount;
            if (r0 >= r1) goto L_0x001d;
        L_0x000d:
            r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ IOException -> 0x001a }
            r1 = r1.fileSystem;	 Catch:{ IOException -> 0x001a }
            r2 = r3.entry;	 Catch:{ IOException -> 0x001a }
            r2 = r2.dirtyFiles;	 Catch:{ IOException -> 0x001a }
            r2 = r2[r0];	 Catch:{ IOException -> 0x001a }
            r1.delete(r2);	 Catch:{ IOException -> 0x001a }
        L_0x001a:
            r0 = r0 + 1;
            goto L_0x0007;
        L_0x001d:
            r0 = r3.entry;
            r1 = 0;
            r0.currentEditor = r1;
        L_0x0022:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.Editor.detach():void");
        }

        public okio.Source newSource(int r5) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r4 = this;
            r0 = okhttp3.internal.cache.DiskLruCache.this;
            monitor-enter(r0);
            r1 = r4.done;	 Catch:{ all -> 0x002f }
            if (r1 == 0) goto L_0x000d;	 Catch:{ all -> 0x002f }
        L_0x0007:
            r5 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x002f }
            r5.<init>();	 Catch:{ all -> 0x002f }
            throw r5;	 Catch:{ all -> 0x002f }
        L_0x000d:
            r1 = r4.entry;	 Catch:{ all -> 0x002f }
            r1 = r1.readable;	 Catch:{ all -> 0x002f }
            r2 = 0;	 Catch:{ all -> 0x002f }
            if (r1 == 0) goto L_0x002d;	 Catch:{ all -> 0x002f }
        L_0x0014:
            r1 = r4.entry;	 Catch:{ all -> 0x002f }
            r1 = r1.currentEditor;	 Catch:{ all -> 0x002f }
            if (r1 == r4) goto L_0x001b;
        L_0x001a:
            goto L_0x002d;
        L_0x001b:
            r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ FileNotFoundException -> 0x002b }
            r1 = r1.fileSystem;	 Catch:{ FileNotFoundException -> 0x002b }
            r3 = r4.entry;	 Catch:{ FileNotFoundException -> 0x002b }
            r3 = r3.cleanFiles;	 Catch:{ FileNotFoundException -> 0x002b }
            r5 = r3[r5];	 Catch:{ FileNotFoundException -> 0x002b }
            r5 = r1.source(r5);	 Catch:{ FileNotFoundException -> 0x002b }
            monitor-exit(r0);	 Catch:{ all -> 0x002f }
            return r5;	 Catch:{ all -> 0x002f }
        L_0x002b:
            monitor-exit(r0);	 Catch:{ all -> 0x002f }
            return r2;	 Catch:{ all -> 0x002f }
        L_0x002d:
            monitor-exit(r0);	 Catch:{ all -> 0x002f }
            return r2;	 Catch:{ all -> 0x002f }
        L_0x002f:
            r5 = move-exception;	 Catch:{ all -> 0x002f }
            monitor-exit(r0);	 Catch:{ all -> 0x002f }
            throw r5;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.Editor.newSource(int):okio.Source");
        }

        public okio.Sink newSink(int r4) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r3 = this;
            r0 = okhttp3.internal.cache.DiskLruCache.this;
            monitor-enter(r0);
            r1 = r3.done;	 Catch:{ all -> 0x003f }
            if (r1 == 0) goto L_0x000d;	 Catch:{ all -> 0x003f }
        L_0x0007:
            r4 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x003f }
            r4.<init>();	 Catch:{ all -> 0x003f }
            throw r4;	 Catch:{ all -> 0x003f }
        L_0x000d:
            r1 = r3.entry;	 Catch:{ all -> 0x003f }
            r1 = r1.currentEditor;	 Catch:{ all -> 0x003f }
            if (r1 == r3) goto L_0x0019;	 Catch:{ all -> 0x003f }
        L_0x0013:
            r4 = okio.Okio.blackhole();	 Catch:{ all -> 0x003f }
            monitor-exit(r0);	 Catch:{ all -> 0x003f }
            return r4;	 Catch:{ all -> 0x003f }
        L_0x0019:
            r1 = r3.entry;	 Catch:{ all -> 0x003f }
            r1 = r1.readable;	 Catch:{ all -> 0x003f }
            if (r1 != 0) goto L_0x0024;	 Catch:{ all -> 0x003f }
        L_0x001f:
            r1 = r3.written;	 Catch:{ all -> 0x003f }
            r2 = 1;	 Catch:{ all -> 0x003f }
            r1[r4] = r2;	 Catch:{ all -> 0x003f }
        L_0x0024:
            r1 = r3.entry;	 Catch:{ all -> 0x003f }
            r1 = r1.dirtyFiles;	 Catch:{ all -> 0x003f }
            r4 = r1[r4];	 Catch:{ all -> 0x003f }
            r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ FileNotFoundException -> 0x0039 }
            r1 = r1.fileSystem;	 Catch:{ FileNotFoundException -> 0x0039 }
            r4 = r1.sink(r4);	 Catch:{ FileNotFoundException -> 0x0039 }
            r1 = new okhttp3.internal.cache.DiskLruCache$Editor$1;	 Catch:{ all -> 0x003f }
            r1.<init>(r4);	 Catch:{ all -> 0x003f }
            monitor-exit(r0);	 Catch:{ all -> 0x003f }
            return r1;	 Catch:{ all -> 0x003f }
        L_0x0039:
            r4 = okio.Okio.blackhole();	 Catch:{ all -> 0x003f }
            monitor-exit(r0);	 Catch:{ all -> 0x003f }
            return r4;	 Catch:{ all -> 0x003f }
        L_0x003f:
            r4 = move-exception;	 Catch:{ all -> 0x003f }
            monitor-exit(r0);	 Catch:{ all -> 0x003f }
            throw r4;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.Editor.newSink(int):okio.Sink");
        }

        public void commit() throws IOException {
            synchronized (DiskLruCache.this) {
                if (this.done) {
                    throw new IllegalStateException();
                }
                if (this.entry.currentEditor == this) {
                    DiskLruCache.this.completeEdit(this, true);
                }
                this.done = true;
            }
        }

        public void abort() throws IOException {
            synchronized (DiskLruCache.this) {
                if (this.done) {
                    throw new IllegalStateException();
                }
                if (this.entry.currentEditor == this) {
                    DiskLruCache.this.completeEdit(this, false);
                }
                this.done = true;
            }
        }

        public void abortUnlessCommitted() {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r3 = this;
            r0 = okhttp3.internal.cache.DiskLruCache.this;
            monitor-enter(r0);
            r1 = r3.done;	 Catch:{ all -> 0x0015 }
            if (r1 != 0) goto L_0x0013;	 Catch:{ all -> 0x0015 }
        L_0x0007:
            r1 = r3.entry;	 Catch:{ all -> 0x0015 }
            r1 = r1.currentEditor;	 Catch:{ all -> 0x0015 }
            if (r1 != r3) goto L_0x0013;
        L_0x000d:
            r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ IOException -> 0x0013 }
            r2 = 0;	 Catch:{ IOException -> 0x0013 }
            r1.completeEdit(r3, r2);	 Catch:{ IOException -> 0x0013 }
        L_0x0013:
            monitor-exit(r0);	 Catch:{ all -> 0x0015 }
            return;	 Catch:{ all -> 0x0015 }
        L_0x0015:
            r1 = move-exception;	 Catch:{ all -> 0x0015 }
            monitor-exit(r0);	 Catch:{ all -> 0x0015 }
            throw r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.Editor.abortUnlessCommitted():void");
        }
    }

    private final class Entry {
        final File[] cleanFiles;
        Editor currentEditor;
        final File[] dirtyFiles;
        final String key;
        final long[] lengths;
        boolean readable;
        long sequenceNumber;

        Entry(String str) {
            this.key = str;
            this.lengths = new long[DiskLruCache.this.valueCount];
            this.cleanFiles = new File[DiskLruCache.this.valueCount];
            this.dirtyFiles = new File[DiskLruCache.this.valueCount];
            StringBuilder stringBuilder = new StringBuilder(str);
            stringBuilder.append('.');
            str = stringBuilder.length();
            for (int i = 0; i < DiskLruCache.this.valueCount; i++) {
                stringBuilder.append(i);
                this.cleanFiles[i] = new File(DiskLruCache.this.directory, stringBuilder.toString());
                stringBuilder.append(".tmp");
                this.dirtyFiles[i] = new File(DiskLruCache.this.directory, stringBuilder.toString());
                stringBuilder.setLength(str);
            }
        }

        void setLengths(java.lang.String[] r5) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r4 = this;
            r0 = r5.length;
            r1 = okhttp3.internal.cache.DiskLruCache.this;
            r1 = r1.valueCount;
            if (r0 == r1) goto L_0x000c;
        L_0x0007:
            r5 = r4.invalidLengths(r5);
            throw r5;
        L_0x000c:
            r0 = 0;
        L_0x000d:
            r1 = r5.length;	 Catch:{ NumberFormatException -> 0x001e }
            if (r0 >= r1) goto L_0x001d;	 Catch:{ NumberFormatException -> 0x001e }
        L_0x0010:
            r1 = r4.lengths;	 Catch:{ NumberFormatException -> 0x001e }
            r2 = r5[r0];	 Catch:{ NumberFormatException -> 0x001e }
            r2 = java.lang.Long.parseLong(r2);	 Catch:{ NumberFormatException -> 0x001e }
            r1[r0] = r2;	 Catch:{ NumberFormatException -> 0x001e }
            r0 = r0 + 1;
            goto L_0x000d;
        L_0x001d:
            return;
        L_0x001e:
            r5 = r4.invalidLengths(r5);
            throw r5;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.Entry.setLengths(java.lang.String[]):void");
        }

        void writeLengths(BufferedSink bufferedSink) throws IOException {
            for (long writeDecimalLong : this.lengths) {
                bufferedSink.writeByte(32).writeDecimalLong(writeDecimalLong);
            }
        }

        private IOException invalidLengths(String[] strArr) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("unexpected journal line: ");
            stringBuilder.append(Arrays.toString(strArr));
            throw new IOException(stringBuilder.toString());
        }

        okhttp3.internal.cache.DiskLruCache.Snapshot snapshot() {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r10 = this;
            r0 = okhttp3.internal.cache.DiskLruCache.this;
            r0 = java.lang.Thread.holdsLock(r0);
            if (r0 != 0) goto L_0x000e;
        L_0x0008:
            r0 = new java.lang.AssertionError;
            r0.<init>();
            throw r0;
        L_0x000e:
            r0 = okhttp3.internal.cache.DiskLruCache.this;
            r0 = r0.valueCount;
            r0 = new okio.Source[r0];
            r1 = r10.lengths;
            r1 = r1.clone();
            r7 = r1;
            r7 = (long[]) r7;
            r8 = 0;
            r1 = r8;
        L_0x001f:
            r2 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ FileNotFoundException -> 0x0044 }
            r2 = r2.valueCount;	 Catch:{ FileNotFoundException -> 0x0044 }
            if (r1 >= r2) goto L_0x0036;	 Catch:{ FileNotFoundException -> 0x0044 }
        L_0x0025:
            r2 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ FileNotFoundException -> 0x0044 }
            r2 = r2.fileSystem;	 Catch:{ FileNotFoundException -> 0x0044 }
            r3 = r10.cleanFiles;	 Catch:{ FileNotFoundException -> 0x0044 }
            r3 = r3[r1];	 Catch:{ FileNotFoundException -> 0x0044 }
            r2 = r2.source(r3);	 Catch:{ FileNotFoundException -> 0x0044 }
            r0[r1] = r2;	 Catch:{ FileNotFoundException -> 0x0044 }
            r1 = r1 + 1;	 Catch:{ FileNotFoundException -> 0x0044 }
            goto L_0x001f;	 Catch:{ FileNotFoundException -> 0x0044 }
        L_0x0036:
            r9 = new okhttp3.internal.cache.DiskLruCache$Snapshot;	 Catch:{ FileNotFoundException -> 0x0044 }
            r2 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ FileNotFoundException -> 0x0044 }
            r3 = r10.key;	 Catch:{ FileNotFoundException -> 0x0044 }
            r4 = r10.sequenceNumber;	 Catch:{ FileNotFoundException -> 0x0044 }
            r1 = r9;	 Catch:{ FileNotFoundException -> 0x0044 }
            r6 = r0;	 Catch:{ FileNotFoundException -> 0x0044 }
            r1.<init>(r3, r4, r6, r7);	 Catch:{ FileNotFoundException -> 0x0044 }
            return r9;
        L_0x0044:
            r1 = okhttp3.internal.cache.DiskLruCache.this;
            r1 = r1.valueCount;
            if (r8 >= r1) goto L_0x0056;
        L_0x004a:
            r1 = r0[r8];
            if (r1 == 0) goto L_0x0056;
        L_0x004e:
            r1 = r0[r8];
            okhttp3.internal.Util.closeQuietly(r1);
            r8 = r8 + 1;
            goto L_0x0044;
        L_0x0056:
            r0 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ IOException -> 0x005b }
            r0.removeEntry(r10);	 Catch:{ IOException -> 0x005b }
        L_0x005b:
            r0 = 0;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.Entry.snapshot():okhttp3.internal.cache.DiskLruCache$Snapshot");
        }
    }

    public final class Snapshot implements Closeable {
        private final String key;
        private final long[] lengths;
        private final long sequenceNumber;
        private final Source[] sources;

        Snapshot(String str, long j, Source[] sourceArr, long[] jArr) {
            this.key = str;
            this.sequenceNumber = j;
            this.sources = sourceArr;
            this.lengths = jArr;
        }

        public String key() {
            return this.key;
        }

        @Nullable
        public Editor edit() throws IOException {
            return DiskLruCache.this.edit(this.key, this.sequenceNumber);
        }

        public Source getSource(int i) {
            return this.sources[i];
        }

        public long getLength(int i) {
            return this.lengths[i];
        }

        public void close() {
            for (Closeable closeQuietly : this.sources) {
                Util.closeQuietly(closeQuietly);
            }
        }
    }

    DiskLruCache(FileSystem fileSystem, File file, int i, int i2, long j, Executor executor) {
        this.fileSystem = fileSystem;
        this.directory = file;
        this.appVersion = i;
        this.journalFile = new File(file, JOURNAL_FILE);
        this.journalFileTmp = new File(file, JOURNAL_FILE_TEMP);
        this.journalFileBackup = new File(file, JOURNAL_FILE_BACKUP);
        this.valueCount = i2;
        this.maxSize = j;
        this.executor = executor;
    }

    public synchronized void initialize() throws IOException {
        if (!this.initialized) {
            if (this.fileSystem.exists(this.journalFileBackup)) {
                if (this.fileSystem.exists(this.journalFile)) {
                    this.fileSystem.delete(this.journalFileBackup);
                } else {
                    this.fileSystem.rename(this.journalFileBackup, this.journalFile);
                }
            }
            if (this.fileSystem.exists(this.journalFile)) {
                try {
                    readJournal();
                    processJournal();
                    this.initialized = true;
                    return;
                } catch (Throwable e) {
                    Platform platform = Platform.get();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DiskLruCache ");
                    stringBuilder.append(this.directory);
                    stringBuilder.append(" is corrupt: ");
                    stringBuilder.append(e.getMessage());
                    stringBuilder.append(", removing");
                    platform.log(5, stringBuilder.toString(), e);
                    delete();
                } finally {
                    this.closed = false;
                }
            }
            rebuildJournal();
            this.initialized = true;
        }
    }

    public static DiskLruCache create(FileSystem fileSystem, File file, int i, int i2, long j) {
        if (j <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        } else if (i2 <= 0) {
            throw new IllegalArgumentException("valueCount <= 0");
        } else {
            return new DiskLruCache(fileSystem, file, i, i2, j, new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory("OkHttp DiskLruCache", true)));
        }
    }

    private void readJournal() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r8 = this;
        r0 = r8.fileSystem;
        r1 = r8.journalFile;
        r0 = r0.source(r1);
        r0 = okio.Okio.buffer(r0);
        r1 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x00ad }
        r2 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x00ad }
        r3 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x00ad }
        r4 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x00ad }
        r5 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x00ad }
        r6 = "libcore.io.DiskLruCache";	 Catch:{ all -> 0x00ad }
        r6 = r6.equals(r1);	 Catch:{ all -> 0x00ad }
        if (r6 == 0) goto L_0x0079;	 Catch:{ all -> 0x00ad }
    L_0x0028:
        r6 = "1";	 Catch:{ all -> 0x00ad }
        r6 = r6.equals(r2);	 Catch:{ all -> 0x00ad }
        if (r6 == 0) goto L_0x0079;	 Catch:{ all -> 0x00ad }
    L_0x0030:
        r6 = r8.appVersion;	 Catch:{ all -> 0x00ad }
        r6 = java.lang.Integer.toString(r6);	 Catch:{ all -> 0x00ad }
        r3 = r6.equals(r3);	 Catch:{ all -> 0x00ad }
        if (r3 == 0) goto L_0x0079;	 Catch:{ all -> 0x00ad }
    L_0x003c:
        r3 = r8.valueCount;	 Catch:{ all -> 0x00ad }
        r3 = java.lang.Integer.toString(r3);	 Catch:{ all -> 0x00ad }
        r3 = r3.equals(r4);	 Catch:{ all -> 0x00ad }
        if (r3 == 0) goto L_0x0079;	 Catch:{ all -> 0x00ad }
    L_0x0048:
        r3 = "";	 Catch:{ all -> 0x00ad }
        r3 = r3.equals(r5);	 Catch:{ all -> 0x00ad }
        if (r3 != 0) goto L_0x0051;
    L_0x0050:
        goto L_0x0079;
    L_0x0051:
        r1 = 0;
    L_0x0052:
        r2 = r0.readUtf8LineStrict();	 Catch:{ EOFException -> 0x005c }
        r8.readJournalLine(r2);	 Catch:{ EOFException -> 0x005c }
        r1 = r1 + 1;
        goto L_0x0052;
    L_0x005c:
        r2 = r8.lruEntries;	 Catch:{ all -> 0x00ad }
        r2 = r2.size();	 Catch:{ all -> 0x00ad }
        r1 = r1 - r2;	 Catch:{ all -> 0x00ad }
        r8.redundantOpCount = r1;	 Catch:{ all -> 0x00ad }
        r1 = r0.exhausted();	 Catch:{ all -> 0x00ad }
        if (r1 != 0) goto L_0x006f;	 Catch:{ all -> 0x00ad }
    L_0x006b:
        r8.rebuildJournal();	 Catch:{ all -> 0x00ad }
        goto L_0x0075;	 Catch:{ all -> 0x00ad }
    L_0x006f:
        r1 = r8.newJournalWriter();	 Catch:{ all -> 0x00ad }
        r8.journalWriter = r1;	 Catch:{ all -> 0x00ad }
    L_0x0075:
        okhttp3.internal.Util.closeQuietly(r0);
        return;
    L_0x0079:
        r3 = new java.io.IOException;	 Catch:{ all -> 0x00ad }
        r6 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00ad }
        r6.<init>();	 Catch:{ all -> 0x00ad }
        r7 = "unexpected journal header: [";	 Catch:{ all -> 0x00ad }
        r6.append(r7);	 Catch:{ all -> 0x00ad }
        r6.append(r1);	 Catch:{ all -> 0x00ad }
        r1 = ", ";	 Catch:{ all -> 0x00ad }
        r6.append(r1);	 Catch:{ all -> 0x00ad }
        r6.append(r2);	 Catch:{ all -> 0x00ad }
        r1 = ", ";	 Catch:{ all -> 0x00ad }
        r6.append(r1);	 Catch:{ all -> 0x00ad }
        r6.append(r4);	 Catch:{ all -> 0x00ad }
        r1 = ", ";	 Catch:{ all -> 0x00ad }
        r6.append(r1);	 Catch:{ all -> 0x00ad }
        r6.append(r5);	 Catch:{ all -> 0x00ad }
        r1 = "]";	 Catch:{ all -> 0x00ad }
        r6.append(r1);	 Catch:{ all -> 0x00ad }
        r1 = r6.toString();	 Catch:{ all -> 0x00ad }
        r3.<init>(r1);	 Catch:{ all -> 0x00ad }
        throw r3;	 Catch:{ all -> 0x00ad }
    L_0x00ad:
        r1 = move-exception;
        okhttp3.internal.Util.closeQuietly(r0);
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.readJournal():void");
    }

    private BufferedSink newJournalWriter() throws FileNotFoundException {
        return Okio.buffer(new FaultHidingSink(this.fileSystem.appendingSink(this.journalFile)) {
            static final /* synthetic */ boolean $assertionsDisabled = false;

            static {
                Class cls = DiskLruCache.class;
            }

            protected void onException(IOException iOException) {
                DiskLruCache.this.hasJournalErrors = true;
            }
        });
    }

    private void readJournalLine(String str) throws IOException {
        int indexOf = str.indexOf(32);
        if (indexOf == -1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("unexpected journal line: ");
            stringBuilder.append(str);
            throw new IOException(stringBuilder.toString());
        }
        String substring;
        int i = indexOf + 1;
        int indexOf2 = str.indexOf(32, i);
        if (indexOf2 == -1) {
            substring = str.substring(i);
            if (indexOf == REMOVE.length() && str.startsWith(REMOVE)) {
                this.lruEntries.remove(substring);
                return;
            }
        }
        substring = str.substring(i, indexOf2);
        Entry entry = (Entry) this.lruEntries.get(substring);
        if (entry == null) {
            entry = new Entry(substring);
            this.lruEntries.put(substring, entry);
        }
        if (indexOf2 != -1 && indexOf == CLEAN.length() && str.startsWith(CLEAN)) {
            str = str.substring(indexOf2 + 1).split(" ");
            entry.readable = true;
            entry.currentEditor = null;
            entry.setLengths(str);
        } else if (indexOf2 == -1 && indexOf == DIRTY.length() && str.startsWith(DIRTY)) {
            entry.currentEditor = new Editor(entry);
        } else if (!(indexOf2 == -1 && indexOf == READ.length() && str.startsWith(READ))) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("unexpected journal line: ");
            stringBuilder.append(str);
            throw new IOException(stringBuilder.toString());
        }
    }

    private void processJournal() throws IOException {
        this.fileSystem.delete(this.journalFileTmp);
        Iterator it = this.lruEntries.values().iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            int i = 0;
            if (entry.currentEditor == null) {
                while (i < this.valueCount) {
                    this.size += entry.lengths[i];
                    i++;
                }
            } else {
                entry.currentEditor = null;
                while (i < this.valueCount) {
                    this.fileSystem.delete(entry.cleanFiles[i]);
                    this.fileSystem.delete(entry.dirtyFiles[i]);
                    i++;
                }
                it.remove();
            }
        }
    }

    synchronized void rebuildJournal() throws IOException {
        if (this.journalWriter != null) {
            this.journalWriter.close();
        }
        BufferedSink buffer = Okio.buffer(this.fileSystem.sink(this.journalFileTmp));
        try {
            buffer.writeUtf8(MAGIC).writeByte(10);
            buffer.writeUtf8(VERSION_1).writeByte(10);
            buffer.writeDecimalLong((long) this.appVersion).writeByte(10);
            buffer.writeDecimalLong((long) this.valueCount).writeByte(10);
            buffer.writeByte(10);
            for (Entry entry : this.lruEntries.values()) {
                if (entry.currentEditor != null) {
                    buffer.writeUtf8(DIRTY).writeByte(32);
                    buffer.writeUtf8(entry.key);
                    buffer.writeByte(10);
                } else {
                    buffer.writeUtf8(CLEAN).writeByte(32);
                    buffer.writeUtf8(entry.key);
                    entry.writeLengths(buffer);
                    buffer.writeByte(10);
                }
            }
            if (this.fileSystem.exists(this.journalFile)) {
                this.fileSystem.rename(this.journalFile, this.journalFileBackup);
            }
            this.fileSystem.rename(this.journalFileTmp, this.journalFile);
            this.fileSystem.delete(this.journalFileBackup);
            this.journalWriter = newJournalWriter();
            this.hasJournalErrors = false;
            this.mostRecentRebuildFailed = false;
        } finally {
            buffer.close();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized Snapshot get(String str) throws IOException {
        initialize();
        checkNotClosed();
        validateKey(str);
        Entry entry = (Entry) this.lruEntries.get(str);
        if (entry != null) {
            if (entry.readable) {
                Snapshot snapshot = entry.snapshot();
                if (snapshot == null) {
                    return null;
                }
                this.redundantOpCount++;
                this.journalWriter.writeUtf8(READ).writeByte(32).writeUtf8(str).writeByte(10);
                if (journalRebuildRequired() != null) {
                    this.executor.execute(this.cleanupRunnable);
                }
            }
        }
    }

    @Nullable
    public Editor edit(String str) throws IOException {
        return edit(str, -1);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    synchronized Editor edit(String str, long j) throws IOException {
        initialize();
        checkNotClosed();
        validateKey(str);
        Entry entry = (Entry) this.lruEntries.get(str);
        if (j == -1 || (entry != null && entry.sequenceNumber == j)) {
            if (entry != null) {
                if (entry.currentEditor != null) {
                    return null;
                }
            }
            if (this.mostRecentTrimFailed == null) {
                if (this.mostRecentRebuildFailed == null) {
                    this.journalWriter.writeUtf8(DIRTY).writeByte(32).writeUtf8(str).writeByte(10);
                    this.journalWriter.flush();
                    if (this.hasJournalErrors != null) {
                        return null;
                    }
                    if (entry == null) {
                        entry = new Entry(str);
                        this.lruEntries.put(str, entry);
                    }
                    str = new Editor(entry);
                    entry.currentEditor = str;
                    return str;
                }
            }
            this.executor.execute(this.cleanupRunnable);
            return null;
        }
    }

    public File getDirectory() {
        return this.directory;
    }

    public synchronized long getMaxSize() {
        return this.maxSize;
    }

    public synchronized void setMaxSize(long j) {
        this.maxSize = j;
        if (this.initialized != null) {
            this.executor.execute(this.cleanupRunnable);
        }
    }

    public synchronized long size() throws IOException {
        initialize();
        return this.size;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    synchronized void completeEdit(Editor editor, boolean z) throws IOException {
        Entry entry = editor.entry;
        if (entry.currentEditor != editor) {
            throw new IllegalStateException();
        }
        int i = 0;
        if (z && !entry.readable) {
            int i2 = 0;
            while (i2 < this.valueCount) {
                if (!editor.written[i2]) {
                    editor.abort();
                    z = new StringBuilder();
                    z.append("Newly created entry didn't create value for index ");
                    z.append(i2);
                    throw new IllegalStateException(z.toString());
                } else if (this.fileSystem.exists(entry.dirtyFiles[i2])) {
                    i2++;
                } else {
                    editor.abort();
                    return;
                }
            }
        }
        while (i < this.valueCount) {
            editor = entry.dirtyFiles[i];
            if (!z) {
                this.fileSystem.delete(editor);
            } else if (this.fileSystem.exists(editor)) {
                File file = entry.cleanFiles[i];
                this.fileSystem.rename(editor, file);
                long j = entry.lengths[i];
                long size = this.fileSystem.size(file);
                entry.lengths[i] = size;
                this.size = (this.size - j) + size;
            }
            i++;
        }
        this.redundantOpCount += 1;
        entry.currentEditor = null;
        if ((entry.readable | z) != null) {
            entry.readable = true;
            this.journalWriter.writeUtf8(CLEAN).writeByte(32);
            this.journalWriter.writeUtf8(entry.key);
            entry.writeLengths(this.journalWriter);
            this.journalWriter.writeByte(10);
            if (z) {
                editor = this.nextSequenceNumber;
                this.nextSequenceNumber = editor + 1;
                entry.sequenceNumber = editor;
            }
        } else {
            this.lruEntries.remove(entry.key);
            this.journalWriter.writeUtf8(REMOVE).writeByte(32);
            this.journalWriter.writeUtf8(entry.key);
            this.journalWriter.writeByte(10);
        }
        this.journalWriter.flush();
        if (this.size > this.maxSize || journalRebuildRequired() != null) {
            this.executor.execute(this.cleanupRunnable);
        }
    }

    boolean journalRebuildRequired() {
        return this.redundantOpCount >= 2000 && this.redundantOpCount >= this.lruEntries.size();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean remove(String str) throws IOException {
        initialize();
        checkNotClosed();
        validateKey(str);
        Entry entry = (Entry) this.lruEntries.get(str);
        if (entry == null) {
            return false;
        }
        str = removeEntry(entry);
        if (str != null && this.size <= this.maxSize) {
            this.mostRecentTrimFailed = false;
        }
    }

    boolean removeEntry(Entry entry) throws IOException {
        if (entry.currentEditor != null) {
            entry.currentEditor.detach();
        }
        for (int i = 0; i < this.valueCount; i++) {
            this.fileSystem.delete(entry.cleanFiles[i]);
            this.size -= entry.lengths[i];
            entry.lengths[i] = 0;
        }
        this.redundantOpCount++;
        this.journalWriter.writeUtf8(REMOVE).writeByte(32).writeUtf8(entry.key).writeByte(10);
        this.lruEntries.remove(entry.key);
        if (journalRebuildRequired() != null) {
            this.executor.execute(this.cleanupRunnable);
        }
        return true;
    }

    public synchronized boolean isClosed() {
        return this.closed;
    }

    private synchronized void checkNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("cache is closed");
        }
    }

    public synchronized void flush() throws IOException {
        if (this.initialized) {
            checkNotClosed();
            trimToSize();
            this.journalWriter.flush();
        }
    }

    public synchronized void close() throws IOException {
        if (this.initialized) {
            if (!this.closed) {
                for (Entry entry : (Entry[]) this.lruEntries.values().toArray(new Entry[this.lruEntries.size()])) {
                    if (entry.currentEditor != null) {
                        entry.currentEditor.abort();
                    }
                }
                trimToSize();
                this.journalWriter.close();
                this.journalWriter = null;
                this.closed = true;
                return;
            }
        }
        this.closed = true;
    }

    void trimToSize() throws IOException {
        while (this.size > this.maxSize) {
            removeEntry((Entry) this.lruEntries.values().iterator().next());
        }
        this.mostRecentTrimFailed = false;
    }

    public void delete() throws IOException {
        close();
        this.fileSystem.deleteContents(this.directory);
    }

    public synchronized void evictAll() throws IOException {
        initialize();
        for (Entry removeEntry : (Entry[]) this.lruEntries.values().toArray(new Entry[this.lruEntries.size()])) {
            removeEntry(removeEntry);
        }
        this.mostRecentTrimFailed = false;
    }

    private void validateKey(String str) {
        if (!LEGAL_KEY_PATTERN.matcher(str).matches()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("keys must match regex [a-z0-9_-]{1,120}: \"");
            stringBuilder.append(str);
            stringBuilder.append("\"");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public synchronized Iterator<Snapshot> snapshots() throws IOException {
        initialize();
        return new Iterator<Snapshot>() {
            final Iterator<Entry> delegate = new ArrayList(DiskLruCache.this.lruEntries.values()).iterator();
            Snapshot nextSnapshot;
            Snapshot removeSnapshot;

            public boolean hasNext() {
                if (this.nextSnapshot != null) {
                    return true;
                }
                synchronized (DiskLruCache.this) {
                    if (DiskLruCache.this.closed) {
                        return false;
                    }
                    while (this.delegate.hasNext()) {
                        Snapshot snapshot = ((Entry) this.delegate.next()).snapshot();
                        if (snapshot != null) {
                            this.nextSnapshot = snapshot;
                            return true;
                        }
                    }
                    return false;
                }
            }

            public Snapshot next() {
                if (hasNext()) {
                    this.removeSnapshot = this.nextSnapshot;
                    this.nextSnapshot = null;
                    return this.removeSnapshot;
                }
                throw new NoSuchElementException();
            }

            public void remove() {
                /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
                /*
                r3 = this;
                r0 = r3.removeSnapshot;
                if (r0 != 0) goto L_0x000c;
            L_0x0004:
                r0 = new java.lang.IllegalStateException;
                r1 = "remove() before next()";
                r0.<init>(r1);
                throw r0;
            L_0x000c:
                r0 = 0;
                r1 = okhttp3.internal.cache.DiskLruCache.this;	 Catch:{ IOException -> 0x001d, all -> 0x0019 }
                r2 = r3.removeSnapshot;	 Catch:{ IOException -> 0x001d, all -> 0x0019 }
                r2 = r2.key;	 Catch:{ IOException -> 0x001d, all -> 0x0019 }
                r1.remove(r2);	 Catch:{ IOException -> 0x001d, all -> 0x0019 }
                goto L_0x001d;
            L_0x0019:
                r1 = move-exception;
                r3.removeSnapshot = r0;
                throw r1;
            L_0x001d:
                r3.removeSnapshot = r0;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.3.remove():void");
            }
        };
    }
}
