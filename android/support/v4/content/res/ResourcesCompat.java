package android.support.v4.content.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.FontRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.content.res.FontResourcesParserCompat.FamilyResourceEntry;
import android.support.v4.graphics.TypefaceCompat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

public final class ResourcesCompat {
    private static final String TAG = "ResourcesCompat";

    @Nullable
    public static Drawable getDrawable(@NonNull Resources resources, @DrawableRes int i, @Nullable Theme theme) throws NotFoundException {
        if (VERSION.SDK_INT >= 21) {
            return resources.getDrawable(i, theme);
        }
        return resources.getDrawable(i);
    }

    @Nullable
    public static Drawable getDrawableForDensity(@NonNull Resources resources, @DrawableRes int i, int i2, @Nullable Theme theme) throws NotFoundException {
        if (VERSION.SDK_INT >= 21) {
            return resources.getDrawableForDensity(i, i2, theme);
        }
        if (VERSION.SDK_INT >= 15) {
            return resources.getDrawableForDensity(i, i2);
        }
        return resources.getDrawable(i);
    }

    @ColorInt
    public static int getColor(@NonNull Resources resources, @ColorRes int i, @Nullable Theme theme) throws NotFoundException {
        if (VERSION.SDK_INT >= 23) {
            return resources.getColor(i, theme);
        }
        return resources.getColor(i);
    }

    @Nullable
    public static ColorStateList getColorStateList(@NonNull Resources resources, @ColorRes int i, @Nullable Theme theme) throws NotFoundException {
        if (VERSION.SDK_INT >= 23) {
            return resources.getColorStateList(i, theme);
        }
        return resources.getColorStateList(i);
    }

    @Nullable
    public static Typeface getFont(@NonNull Context context, @FontRes int i) throws NotFoundException {
        if (context.isRestricted()) {
            return null;
        }
        return loadFont(context, i, new TypedValue(), 0, null);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static Typeface getFont(@NonNull Context context, @FontRes int i, TypedValue typedValue, int i2, @Nullable TextView textView) throws NotFoundException {
        if (context.isRestricted()) {
            return null;
        }
        return loadFont(context, i, typedValue, i2, textView);
    }

    private static Typeface loadFont(@NonNull Context context, int i, TypedValue typedValue, int i2, @Nullable TextView textView) {
        Resources resources = context.getResources();
        resources.getValue(i, typedValue, true);
        context = loadFont(context, resources, typedValue, i, i2, textView);
        if (context != null) {
            return context;
        }
        typedValue = new StringBuilder();
        typedValue.append("Font resource ID #0x");
        typedValue.append(Integer.toHexString(i));
        throw new NotFoundException(typedValue.toString());
    }

    private static Typeface loadFont(@NonNull Context context, Resources resources, TypedValue typedValue, int i, int i2, @Nullable TextView textView) {
        if (typedValue.string == null) {
            i2 = new StringBuilder();
            i2.append("Resource \"");
            i2.append(resources.getResourceName(i));
            i2.append("\" (");
            i2.append(Integer.toHexString(i));
            i2.append(") is not a Font: ");
            i2.append(typedValue);
            throw new NotFoundException(i2.toString());
        }
        typedValue = typedValue.string.toString();
        if (!typedValue.startsWith("res/")) {
            return null;
        }
        Typeface findFromCache = TypefaceCompat.findFromCache(resources, i, i2);
        if (findFromCache != null) {
            return findFromCache;
        }
        try {
            if (!typedValue.toLowerCase().endsWith(".xml")) {
                return TypefaceCompat.createFromResourcesFontFile(context, resources, i, typedValue, i2);
            }
            FamilyResourceEntry parse = FontResourcesParserCompat.parse(resources.getXml(i), resources);
            if (parse != null) {
                return TypefaceCompat.createFromResourcesFamilyXml(context, parse, resources, i, i2, textView);
            }
            Log.e(TAG, "Failed to find font-family tag");
            return null;
        } catch (Context context2) {
            resources = TAG;
            i = new StringBuilder();
            i.append("Failed to parse xml resource ");
            i.append(typedValue);
            Log.e(resources, i.toString(), context2);
            return null;
        } catch (Context context22) {
            resources = TAG;
            i = new StringBuilder();
            i.append("Failed to read xml resource ");
            i.append(typedValue);
            Log.e(resources, i.toString(), context22);
            return null;
        }
    }

    private ResourcesCompat() {
    }
}
