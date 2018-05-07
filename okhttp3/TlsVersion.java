package okhttp3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum TlsVersion {
    TLS_1_3("TLSv1.3"),
    TLS_1_2("TLSv1.2"),
    TLS_1_1("TLSv1.1"),
    TLS_1_0("TLSv1"),
    SSL_3_0("SSLv3");
    
    final String javaName;

    private TlsVersion(String str) {
        this.javaName = str;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static TlsVersion forJavaName(String str) {
        Object obj;
        StringBuilder stringBuilder;
        int hashCode = str.hashCode();
        if (hashCode != 79201641) {
            if (hashCode != 79923350) {
                switch (hashCode) {
                    case -503070503:
                        if (str.equals("TLSv1.1")) {
                            obj = 2;
                            break;
                        }
                    case -503070502:
                        if (str.equals("TLSv1.2")) {
                            obj = 1;
                            break;
                        }
                    case -503070501:
                        if (str.equals("TLSv1.3")) {
                            obj = null;
                            break;
                        }
                    default:
                }
            } else if (str.equals("TLSv1")) {
                obj = 3;
                switch (obj) {
                    case null:
                        return TLS_1_3;
                    case 1:
                        return TLS_1_2;
                    case 2:
                        return TLS_1_1;
                    case 3:
                        return TLS_1_0;
                    case 4:
                        return SSL_3_0;
                    default:
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Unexpected TLS version: ");
                        stringBuilder.append(str);
                        throw new IllegalArgumentException(stringBuilder.toString());
                }
            }
        } else if (str.equals("SSLv3")) {
            obj = 4;
            switch (obj) {
                case null:
                    return TLS_1_3;
                case 1:
                    return TLS_1_2;
                case 2:
                    return TLS_1_1;
                case 3:
                    return TLS_1_0;
                case 4:
                    return SSL_3_0;
                default:
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Unexpected TLS version: ");
                    stringBuilder.append(str);
                    throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        obj = -1;
        switch (obj) {
            case null:
                return TLS_1_3;
            case 1:
                return TLS_1_2;
            case 2:
                return TLS_1_1;
            case 3:
                return TLS_1_0;
            case 4:
                return SSL_3_0;
            default:
                stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected TLS version: ");
                stringBuilder.append(str);
                throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    static List<TlsVersion> forJavaNames(String... strArr) {
        List arrayList = new ArrayList(strArr.length);
        for (String forJavaName : strArr) {
            arrayList.add(forJavaName(forJavaName));
        }
        return Collections.unmodifiableList(arrayList);
    }

    public String javaName() {
        return this.javaName;
    }
}
