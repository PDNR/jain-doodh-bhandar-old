package retrofit2.converter.scalars;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;

public final class ScalarsConverterFactory extends Factory {
    public static ScalarsConverterFactory create() {
        return new ScalarsConverterFactory();
    }

    private ScalarsConverterFactory() {
    }

    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] annotationArr, Annotation[] annotationArr2, Retrofit retrofit) {
        if (!(type == String.class || type == Boolean.TYPE || type == Boolean.class || type == Byte.TYPE || type == Byte.class || type == Character.TYPE || type == Character.class || type == Double.TYPE || type == Double.class || type == Float.TYPE || type == Float.class || type == Integer.TYPE || type == Integer.class || type == Long.TYPE || type == Long.class || type == Short.TYPE)) {
            if (type != Short.class) {
                return null;
            }
        }
        return ScalarRequestBodyConverter.INSTANCE;
    }

    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotationArr, Retrofit retrofit) {
        if (type == String.class) {
            return StringResponseBodyConverter.INSTANCE;
        }
        if (type != Boolean.class) {
            if (type != Boolean.TYPE) {
                if (type != Byte.class) {
                    if (type != Byte.TYPE) {
                        if (type != Character.class) {
                            if (type != Character.TYPE) {
                                if (type != Double.class) {
                                    if (type != Double.TYPE) {
                                        if (type != Float.class) {
                                            if (type != Float.TYPE) {
                                                if (type != Integer.class) {
                                                    if (type != Integer.TYPE) {
                                                        if (type != Long.class) {
                                                            if (type != Long.TYPE) {
                                                                if (type != Short.class) {
                                                                    if (type != Short.TYPE) {
                                                                        return null;
                                                                    }
                                                                }
                                                                return ShortResponseBodyConverter.INSTANCE;
                                                            }
                                                        }
                                                        return LongResponseBodyConverter.INSTANCE;
                                                    }
                                                }
                                                return IntegerResponseBodyConverter.INSTANCE;
                                            }
                                        }
                                        return FloatResponseBodyConverter.INSTANCE;
                                    }
                                }
                                return DoubleResponseBodyConverter.INSTANCE;
                            }
                        }
                        return CharacterResponseBodyConverter.INSTANCE;
                    }
                }
                return ByteResponseBodyConverter.INSTANCE;
            }
        }
        return BooleanResponseBodyConverter.INSTANCE;
    }
}
