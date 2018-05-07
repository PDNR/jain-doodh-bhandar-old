package android.support.v4.graphics;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.fonts.FontVariationAxis;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.content.res.FontResourcesParserCompat.FontFamilyFilesResourceEntry;
import android.support.v4.content.res.FontResourcesParserCompat.FontFileResourceEntry;
import android.util.Log;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

@RequiresApi(26)
@RestrictTo({Scope.LIBRARY_GROUP})
public class TypefaceCompatApi26Impl extends TypefaceCompatApi21Impl {
    private static final String ABORT_CREATION_METHOD = "abortCreation";
    private static final String ADD_FONT_FROM_ASSET_MANAGER_METHOD = "addFontFromAssetManager";
    private static final String ADD_FONT_FROM_BUFFER_METHOD = "addFontFromBuffer";
    private static final String CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD = "createFromFamiliesWithDefault";
    private static final String FONT_FAMILY_CLASS = "android.graphics.FontFamily";
    private static final String FREEZE_METHOD = "freeze";
    private static final int RESOLVE_BY_FONT_TABLE = -1;
    private static final String TAG = "TypefaceCompatApi26Impl";
    private static final Method sAbortCreation;
    private static final Method sAddFontFromAssetManager;
    private static final Method sAddFontFromBuffer;
    private static final Method sCreateFromFamiliesWithDefault;
    private static final Class sFontFamily;
    private static final Constructor sFontFamilyCtor;
    private static final Method sFreeze;

    static {
        Class cls;
        Method method;
        Method method2;
        Method method3;
        Method method4;
        Method declaredMethod;
        Constructor constructor = null;
        try {
            cls = Class.forName(FONT_FAMILY_CLASS);
            Constructor constructor2 = cls.getConstructor(new Class[0]);
            method = cls.getMethod(ADD_FONT_FROM_ASSET_MANAGER_METHOD, new Class[]{AssetManager.class, String.class, Integer.TYPE, Boolean.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, FontVariationAxis[].class});
            method2 = cls.getMethod(ADD_FONT_FROM_BUFFER_METHOD, new Class[]{ByteBuffer.class, Integer.TYPE, FontVariationAxis[].class, Integer.TYPE, Integer.TYPE});
            method3 = cls.getMethod(FREEZE_METHOD, new Class[0]);
            method4 = cls.getMethod(ABORT_CREATION_METHOD, new Class[0]);
            Object newInstance = Array.newInstance(cls, 1);
            declaredMethod = Typeface.class.getDeclaredMethod(CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD, new Class[]{newInstance.getClass(), Integer.TYPE, Integer.TYPE});
            declaredMethod.setAccessible(true);
            constructor = constructor2;
        } catch (Throwable e) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to collect necessary methods for class ");
            stringBuilder.append(e.getClass().getName());
            Log.e(str, stringBuilder.toString(), e);
            cls = null;
            declaredMethod = cls;
            method = declaredMethod;
            method2 = method;
            method3 = method2;
            method4 = method3;
        }
        sFontFamilyCtor = constructor;
        sFontFamily = cls;
        sAddFontFromAssetManager = method;
        sAddFontFromBuffer = method2;
        sFreeze = method3;
        sAbortCreation = method4;
        sCreateFromFamiliesWithDefault = declaredMethod;
    }

    private static boolean isFontFamilyPrivateAPIAvailable() {
        if (sAddFontFromAssetManager == null) {
            Log.w(TAG, "Unable to collect necessary private methods.Fallback to legacy implementation.");
        }
        return sAddFontFromAssetManager != null;
    }

    private static Object newFamily() {
        try {
            return sFontFamilyCtor.newInstance(new Object[0]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean addFontFromAssetManager(Context context, Object obj, String str, int i, int i2, int i3) {
        try {
            return ((Boolean) sAddFontFromAssetManager.invoke(obj, new Object[]{context.getAssets(), str, Integer.valueOf(0), Boolean.valueOf(false), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), null})).booleanValue();
        } catch (Context context2) {
            throw new RuntimeException(context2);
        }
    }

    private static boolean addFontFromBuffer(Object obj, ByteBuffer byteBuffer, int i, int i2, int i3) {
        try {
            return ((Boolean) sAddFontFromBuffer.invoke(obj, new Object[]{byteBuffer, Integer.valueOf(i), 0, Integer.valueOf(i2), Integer.valueOf(i3)})).booleanValue();
        } catch (Object obj2) {
            throw new RuntimeException(obj2);
        }
    }

    private static Typeface createFromFamiliesWithDefault(Object obj) {
        try {
            Array.set(Array.newInstance(sFontFamily, 1), 0, obj);
            return (Typeface) sCreateFromFamiliesWithDefault.invoke(null, new Object[]{r0, Integer.valueOf(-1), Integer.valueOf(-1)});
        } catch (Object obj2) {
            throw new RuntimeException(obj2);
        }
    }

    private static boolean freeze(Object obj) {
        try {
            return ((Boolean) sFreeze.invoke(obj, new Object[0])).booleanValue();
        } catch (Object obj2) {
            throw new RuntimeException(obj2);
        }
    }

    private static boolean abortCreation(Object obj) {
        try {
            return ((Boolean) sAbortCreation.invoke(obj, new Object[0])).booleanValue();
        } catch (Object obj2) {
            throw new RuntimeException(obj2);
        }
    }

    public Typeface createFromFontFamilyFilesResourceEntry(Context context, FontFamilyFilesResourceEntry fontFamilyFilesResourceEntry, Resources resources, int i) {
        if (!isFontFamilyPrivateAPIAvailable()) {
            return super.createFromFontFamilyFilesResourceEntry(context, fontFamilyFilesResourceEntry, resources, i);
        }
        resources = newFamily();
        fontFamilyFilesResourceEntry = fontFamilyFilesResourceEntry.getEntries();
        i = fontFamilyFilesResourceEntry.length;
        int i2 = 0;
        while (i2 < i) {
            FontFileResourceEntry fontFileResourceEntry = fontFamilyFilesResourceEntry[i2];
            if (addFontFromAssetManager(context, resources, fontFileResourceEntry.getFileName(), 0, fontFileResourceEntry.getWeight(), fontFileResourceEntry.isItalic())) {
                i2++;
            } else {
                abortCreation(resources);
                return null;
            }
        }
        if (freeze(resources) == null) {
            return null;
        }
        return createFromFamiliesWithDefault(resources);
    }

    public android.graphics.Typeface createFromFontInfo(android.content.Context r9, @android.support.annotation.Nullable android.os.CancellationSignal r10, @android.support.annotation.NonNull android.support.v4.provider.FontsContractCompat.FontInfo[] r11, int r12) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r8 = this;
        r0 = 1;
        r1 = 0;
        r2 = r11.length;
        if (r2 >= r0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        r2 = isFontFamilyPrivateAPIAvailable();
        if (r2 != 0) goto L_0x005c;
    L_0x000c:
        r11 = r8.findBestInfo(r11, r12);
        r9 = r9.getContentResolver();
        r12 = r11.getUri();	 Catch:{ IOException -> 0x005b }
        r0 = "r";	 Catch:{ IOException -> 0x005b }
        r9 = r9.openFileDescriptor(r12, r0, r10);	 Catch:{ IOException -> 0x005b }
        r10 = new android.graphics.Typeface$Builder;	 Catch:{ Throwable -> 0x0044, all -> 0x0041 }
        r12 = r9.getFileDescriptor();	 Catch:{ Throwable -> 0x0044, all -> 0x0041 }
        r10.<init>(r12);	 Catch:{ Throwable -> 0x0044, all -> 0x0041 }
        r12 = r11.getWeight();	 Catch:{ Throwable -> 0x0044, all -> 0x0041 }
        r10 = r10.setWeight(r12);	 Catch:{ Throwable -> 0x0044, all -> 0x0041 }
        r11 = r11.isItalic();	 Catch:{ Throwable -> 0x0044, all -> 0x0041 }
        r10 = r10.setItalic(r11);	 Catch:{ Throwable -> 0x0044, all -> 0x0041 }
        r10 = r10.build();	 Catch:{ Throwable -> 0x0044, all -> 0x0041 }
        if (r9 == 0) goto L_0x0040;
    L_0x003d:
        r9.close();	 Catch:{ IOException -> 0x005b }
    L_0x0040:
        return r10;
    L_0x0041:
        r10 = move-exception;
        r11 = r1;
        goto L_0x004a;
    L_0x0044:
        r10 = move-exception;
        throw r10;	 Catch:{ all -> 0x0046 }
    L_0x0046:
        r11 = move-exception;
        r7 = r11;
        r11 = r10;
        r10 = r7;
    L_0x004a:
        if (r9 == 0) goto L_0x005a;
    L_0x004c:
        if (r11 == 0) goto L_0x0057;
    L_0x004e:
        r9.close();	 Catch:{ Throwable -> 0x0052 }
        goto L_0x005a;
    L_0x0052:
        r9 = move-exception;
        r11.addSuppressed(r9);	 Catch:{ IOException -> 0x005b }
        goto L_0x005a;	 Catch:{ IOException -> 0x005b }
    L_0x0057:
        r9.close();	 Catch:{ IOException -> 0x005b }
    L_0x005a:
        throw r10;	 Catch:{ IOException -> 0x005b }
    L_0x005b:
        return r1;
    L_0x005c:
        r9 = android.support.v4.provider.FontsContractCompat.prepareFontData(r9, r11, r10);
        r10 = newFamily();
        r12 = 0;
        r2 = r11.length;
        r3 = r12;
    L_0x0067:
        if (r12 >= r2) goto L_0x0092;
    L_0x0069:
        r4 = r11[r12];
        r5 = r4.getUri();
        r5 = r9.get(r5);
        r5 = (java.nio.ByteBuffer) r5;
        if (r5 != 0) goto L_0x0078;
    L_0x0077:
        goto L_0x008f;
    L_0x0078:
        r3 = r4.getTtcIndex();
        r6 = r4.getWeight();
        r4 = r4.isItalic();
        r3 = addFontFromBuffer(r10, r5, r3, r6, r4);
        if (r3 != 0) goto L_0x008e;
    L_0x008a:
        abortCreation(r10);
        return r1;
    L_0x008e:
        r3 = r0;
    L_0x008f:
        r12 = r12 + 1;
        goto L_0x0067;
    L_0x0092:
        if (r3 != 0) goto L_0x0098;
    L_0x0094:
        abortCreation(r10);
        return r1;
    L_0x0098:
        r9 = freeze(r10);
        if (r9 != 0) goto L_0x009f;
    L_0x009e:
        return r1;
    L_0x009f:
        r9 = createFromFamiliesWithDefault(r10);
        return r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatApi26Impl.createFromFontInfo(android.content.Context, android.os.CancellationSignal, android.support.v4.provider.FontsContractCompat$FontInfo[], int):android.graphics.Typeface");
    }

    @Nullable
    public Typeface createFromResourcesFontFile(Context context, Resources resources, int i, String str, int i2) {
        if (!isFontFamilyPrivateAPIAvailable()) {
            return super.createFromResourcesFontFile(context, resources, i, str, i2);
        }
        resources = newFamily();
        if (addFontFromAssetManager(context, resources, str, 0, -1, -1) == null) {
            abortCreation(resources);
            return null;
        } else if (freeze(resources) == null) {
            return null;
        } else {
            return createFromFamiliesWithDefault(resources);
        }
    }
}
