package android.support.v4.text;

import android.support.annotation.RequiresApi;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.Locale;

@RequiresApi(14)
class ICUCompatIcs {
    private static final String TAG = "ICUCompatIcs";
    private static Method sAddLikelySubtagsMethod;
    private static Method sGetScriptMethod;

    ICUCompatIcs() {
    }

    static {
        try {
            Class cls = Class.forName("libcore.icu.ICU");
            if (cls != null) {
                sGetScriptMethod = cls.getMethod("getScript", new Class[]{String.class});
                sAddLikelySubtagsMethod = cls.getMethod("addLikelySubtags", new Class[]{String.class});
            }
        } catch (Throwable e) {
            sGetScriptMethod = null;
            sAddLikelySubtagsMethod = null;
            Log.w(TAG, e);
        }
    }

    public static String maximizeAndGetScript(Locale locale) {
        locale = addLikelySubtags(locale);
        return locale != null ? getScript(locale) : null;
    }

    private static String getScript(String str) {
        try {
            if (sGetScriptMethod != null) {
                return (String) sGetScriptMethod.invoke(null, new Object[]{str});
            }
        } catch (String str2) {
            Log.w(TAG, str2);
        } catch (String str22) {
            Log.w(TAG, str22);
        }
        return null;
    }

    private static String addLikelySubtags(Locale locale) {
        locale = locale.toString();
        try {
            if (sAddLikelySubtagsMethod != null) {
                return (String) sAddLikelySubtagsMethod.invoke(null, new Object[]{locale});
            }
        } catch (Throwable e) {
            Log.w(TAG, e);
        } catch (Throwable e2) {
            Log.w(TAG, e2);
        }
        return locale;
    }
}
