package android.support.v4.text.util;

import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.util.PatternsCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LinkifyCompat {
    private static final Comparator<LinkSpec> COMPARATOR = new Comparator<LinkSpec>() {
        public final int compare(LinkSpec linkSpec, LinkSpec linkSpec2) {
            if (linkSpec.start < linkSpec2.start) {
                return -1;
            }
            if (linkSpec.start > linkSpec2.start || linkSpec.end < linkSpec2.end) {
                return 1;
            }
            if (linkSpec.end > linkSpec2.end) {
                return -1;
            }
            return null;
        }
    };
    private static final String[] EMPTY_STRING = new String[0];

    private static class LinkSpec {
        int end;
        URLSpan frameworkAddedSpan;
        int start;
        String url;

        LinkSpec() {
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LinkifyMask {
    }

    public static final boolean addLinks(@NonNull Spannable spannable, int i) {
        if (VERSION.SDK_INT >= 26) {
            return Linkify.addLinks(spannable, i);
        }
        if (i == 0) {
            return false;
        }
        URLSpan[] uRLSpanArr = (URLSpan[]) spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (int length = uRLSpanArr.length - 1; length >= 0; length--) {
            spannable.removeSpan(uRLSpanArr[length]);
        }
        if ((i & 4) != 0) {
            Linkify.addLinks(spannable, 4);
        }
        ArrayList arrayList = new ArrayList();
        if ((i & 1) != 0) {
            ArrayList arrayList2 = arrayList;
            Spannable spannable2 = spannable;
            gatherLinks(arrayList2, spannable2, PatternsCompat.AUTOLINK_WEB_URL, new String[]{"http://", "https://", "rtsp://"}, Linkify.sUrlMatchFilter, null);
        }
        if ((i & 2) != 0) {
            gatherLinks(arrayList, spannable, PatternsCompat.AUTOLINK_EMAIL_ADDRESS, new String[]{"mailto:"}, null, null);
        }
        if ((i & 8) != 0) {
            gatherMapLinks(arrayList, spannable);
        }
        pruneOverlaps(arrayList, spannable);
        if (arrayList.size() == 0) {
            return false;
        }
        i = arrayList.iterator();
        while (i.hasNext()) {
            LinkSpec linkSpec = (LinkSpec) i.next();
            if (linkSpec.frameworkAddedSpan == null) {
                applyLink(linkSpec.url, linkSpec.start, linkSpec.end, spannable);
            }
        }
        return true;
    }

    public static final boolean addLinks(@NonNull TextView textView, int i) {
        if (VERSION.SDK_INT >= 26) {
            return Linkify.addLinks(textView, i);
        }
        if (i == 0) {
            return false;
        }
        CharSequence text = textView.getText();
        if (!(text instanceof Spannable)) {
            Spannable valueOf = SpannableString.valueOf(text);
            if (addLinks(valueOf, i) == 0) {
                return false;
            }
            addLinkMovementMethod(textView);
            textView.setText(valueOf);
            return true;
        } else if (addLinks((Spannable) text, i) == 0) {
            return false;
        } else {
            addLinkMovementMethod(textView);
            return true;
        }
    }

    public static final void addLinks(@NonNull TextView textView, @NonNull Pattern pattern, @Nullable String str) {
        if (VERSION.SDK_INT >= 26) {
            Linkify.addLinks(textView, pattern, str);
        } else {
            addLinks(textView, pattern, str, null, null, null);
        }
    }

    public static final void addLinks(@NonNull TextView textView, @NonNull Pattern pattern, @Nullable String str, @Nullable MatchFilter matchFilter, @Nullable TransformFilter transformFilter) {
        if (VERSION.SDK_INT >= 26) {
            Linkify.addLinks(textView, pattern, str, matchFilter, transformFilter);
        } else {
            addLinks(textView, pattern, str, null, matchFilter, transformFilter);
        }
    }

    public static final void addLinks(@NonNull TextView textView, @NonNull Pattern pattern, @Nullable String str, @Nullable String[] strArr, @Nullable MatchFilter matchFilter, @Nullable TransformFilter transformFilter) {
        if (VERSION.SDK_INT >= 26) {
            Linkify.addLinks(textView, pattern, str, strArr, matchFilter, transformFilter);
            return;
        }
        CharSequence valueOf = SpannableString.valueOf(textView.getText());
        if (addLinks((Spannable) valueOf, pattern, str, strArr, matchFilter, transformFilter) != null) {
            textView.setText(valueOf);
            addLinkMovementMethod(textView);
        }
    }

    public static final boolean addLinks(@NonNull Spannable spannable, @NonNull Pattern pattern, @Nullable String str) {
        if (VERSION.SDK_INT >= 26) {
            return Linkify.addLinks(spannable, pattern, str);
        }
        return addLinks(spannable, pattern, str, null, null, null);
    }

    public static final boolean addLinks(@NonNull Spannable spannable, @NonNull Pattern pattern, @Nullable String str, @Nullable MatchFilter matchFilter, @Nullable TransformFilter transformFilter) {
        if (VERSION.SDK_INT >= 26) {
            return Linkify.addLinks(spannable, pattern, str, matchFilter, transformFilter);
        }
        return addLinks(spannable, pattern, str, null, matchFilter, transformFilter);
    }

    public static final boolean addLinks(@NonNull Spannable spannable, @NonNull Pattern pattern, @Nullable String str, @Nullable String[] strArr, @Nullable MatchFilter matchFilter, @Nullable TransformFilter transformFilter) {
        if (VERSION.SDK_INT >= 26) {
            return Linkify.addLinks(spannable, pattern, str, strArr, matchFilter, transformFilter);
        }
        if (str == null) {
            str = "";
        }
        if (strArr == null || strArr.length < 1) {
            strArr = EMPTY_STRING;
        }
        String[] strArr2 = new String[(strArr.length + 1)];
        strArr2[0] = str.toLowerCase(Locale.ROOT);
        str = null;
        while (str < strArr.length) {
            String str2 = strArr[str];
            str++;
            if (str2 == null) {
                str2 = "";
            } else {
                str2 = str2.toLowerCase(Locale.ROOT);
            }
            strArr2[str] = str2;
        }
        pattern = pattern.matcher(spannable);
        str = null;
        while (pattern.find() != null) {
            strArr = pattern.start();
            int end = pattern.end();
            if (matchFilter != null ? matchFilter.acceptMatch(spannable, strArr, end) : true) {
                applyLink(makeUrl(pattern.group(0), strArr2, pattern, transformFilter), strArr, end, spannable);
                str = 1;
            }
        }
        return str;
    }

    private static void addLinkMovementMethod(@NonNull TextView textView) {
        MovementMethod movementMethod = textView.getMovementMethod();
        if ((movementMethod == null || !(movementMethod instanceof LinkMovementMethod)) && textView.getLinksClickable()) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static String makeUrl(@NonNull String str, @NonNull String[] strArr, Matcher matcher, @Nullable TransformFilter transformFilter) {
        Matcher matcher2;
        if (transformFilter != null) {
            str = transformFilter.transformUrl(matcher, str);
        }
        transformFilter = null;
        while (true) {
            matcher2 = true;
            if (transformFilter >= strArr.length) {
                break;
            }
            if (str.regionMatches(true, 0, strArr[transformFilter], 0, strArr[transformFilter].length())) {
                break;
            }
            transformFilter++;
            if (matcher2 != null && strArr.length > null) {
                transformFilter = new StringBuilder();
                transformFilter.append(strArr[0]);
                transformFilter.append(str);
                return transformFilter.toString();
            }
        }
        if (!str.regionMatches(false, 0, strArr[transformFilter], 0, strArr[transformFilter].length())) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(strArr[transformFilter]);
            stringBuilder.append(str.substring(strArr[transformFilter].length()));
            str = stringBuilder.toString();
        }
        return matcher2 != null ? str : str;
    }

    private static void gatherLinks(ArrayList<LinkSpec> arrayList, Spannable spannable, Pattern pattern, String[] strArr, MatchFilter matchFilter, TransformFilter transformFilter) {
        pattern = pattern.matcher(spannable);
        while (pattern.find()) {
            int start = pattern.start();
            int end = pattern.end();
            if (matchFilter == null || matchFilter.acceptMatch(spannable, start, end)) {
                LinkSpec linkSpec = new LinkSpec();
                linkSpec.url = makeUrl(pattern.group(0), strArr, pattern, transformFilter);
                linkSpec.start = start;
                linkSpec.end = end;
                arrayList.add(linkSpec);
            }
        }
    }

    private static void applyLink(String str, int i, int i2, Spannable spannable) {
        spannable.setSpan(new URLSpan(str), i, i2, 33);
    }

    private static final void gatherMapLinks(java.util.ArrayList<android.support.v4.text.util.LinkifyCompat.LinkSpec> r5, android.text.Spannable r6) {
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
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r6 = r6.toString();
        r0 = 0;
    L_0x0005:
        r1 = android.webkit.WebView.findAddress(r6);	 Catch:{ UnsupportedOperationException -> 0x0044 }
        if (r1 == 0) goto L_0x0043;	 Catch:{ UnsupportedOperationException -> 0x0044 }
    L_0x000b:
        r2 = r6.indexOf(r1);	 Catch:{ UnsupportedOperationException -> 0x0044 }
        if (r2 >= 0) goto L_0x0012;	 Catch:{ UnsupportedOperationException -> 0x0044 }
    L_0x0011:
        goto L_0x0043;	 Catch:{ UnsupportedOperationException -> 0x0044 }
    L_0x0012:
        r3 = new android.support.v4.text.util.LinkifyCompat$LinkSpec;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r3.<init>();	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r4 = r1.length();	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r4 = r4 + r2;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r2 = r2 + r0;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r3.start = r2;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r0 = r0 + r4;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r3.end = r0;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r6 = r6.substring(r4);	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r2 = "UTF-8";	 Catch:{ UnsupportedEncodingException -> 0x0005 }
        r1 = java.net.URLEncoder.encode(r1, r2);	 Catch:{ UnsupportedEncodingException -> 0x0005 }
        r2 = new java.lang.StringBuilder;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r2.<init>();	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r4 = "geo:0,0?q=";	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r2.append(r4);	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r2.append(r1);	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r1 = r2.toString();	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r3.url = r1;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r5.add(r3);	 Catch:{ UnsupportedOperationException -> 0x0044 }
        goto L_0x0005;
    L_0x0043:
        return;
    L_0x0044:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.text.util.LinkifyCompat.gatherMapLinks(java.util.ArrayList, android.text.Spannable):void");
    }

    private static final void pruneOverlaps(ArrayList<LinkSpec> arrayList, Spannable spannable) {
        int i;
        int i2 = 0;
        URLSpan[] uRLSpanArr = (URLSpan[]) spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (i = 0; i < uRLSpanArr.length; i++) {
            LinkSpec linkSpec = new LinkSpec();
            linkSpec.frameworkAddedSpan = uRLSpanArr[i];
            linkSpec.start = spannable.getSpanStart(uRLSpanArr[i]);
            linkSpec.end = spannable.getSpanEnd(uRLSpanArr[i]);
            arrayList.add(linkSpec);
        }
        Collections.sort(arrayList, COMPARATOR);
        int size = arrayList.size();
        while (i2 < size - 1) {
            LinkSpec linkSpec2 = (LinkSpec) arrayList.get(i2);
            int i3 = i2 + 1;
            LinkSpec linkSpec3 = (LinkSpec) arrayList.get(i3);
            if (linkSpec2.start <= linkSpec3.start && linkSpec2.end > linkSpec3.start) {
                URLSpan uRLSpan;
                if (linkSpec3.end > linkSpec2.end) {
                    if (linkSpec2.end - linkSpec2.start <= linkSpec3.end - linkSpec3.start) {
                        i = linkSpec2.end - linkSpec2.start < linkSpec3.end - linkSpec3.start ? i2 : -1;
                        if (i != -1) {
                            uRLSpan = ((LinkSpec) arrayList.get(i)).frameworkAddedSpan;
                            if (uRLSpan != null) {
                                spannable.removeSpan(uRLSpan);
                            }
                            arrayList.remove(i);
                            size--;
                        }
                    }
                }
                i = i3;
                if (i != -1) {
                    uRLSpan = ((LinkSpec) arrayList.get(i)).frameworkAddedSpan;
                    if (uRLSpan != null) {
                        spannable.removeSpan(uRLSpan);
                    }
                    arrayList.remove(i);
                    size--;
                }
            }
            i2 = i3;
        }
    }

    private LinkifyCompat() {
    }
}
