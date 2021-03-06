package com.google.gson.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;

public final class Streams {

    private static final class AppendableWriter extends Writer {
        private final Appendable appendable;
        private final CurrentWrite currentWrite = new CurrentWrite();

        static class CurrentWrite implements CharSequence {
            char[] chars;

            CurrentWrite() {
            }

            public int length() {
                return this.chars.length;
            }

            public char charAt(int i) {
                return this.chars[i];
            }

            public CharSequence subSequence(int i, int i2) {
                return new String(this.chars, i, i2 - i);
            }
        }

        public void close() {
        }

        public void flush() {
        }

        AppendableWriter(Appendable appendable) {
            this.appendable = appendable;
        }

        public void write(char[] cArr, int i, int i2) throws IOException {
            this.currentWrite.chars = cArr;
            this.appendable.append(this.currentWrite, i, i2 + i);
        }

        public void write(int i) throws IOException {
            this.appendable.append((char) i);
        }
    }

    private Streams() {
        throw new UnsupportedOperationException();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static JsonElement parse(JsonReader jsonReader) throws JsonParseException {
        Object obj;
        Throwable e;
        try {
            jsonReader.peek();
            obj = null;
            return (JsonElement) TypeAdapters.JSON_ELEMENT.read(jsonReader);
        } catch (EOFException e2) {
            e = e2;
            obj = 1;
            if (obj != null) {
                return JsonNull.INSTANCE;
            }
            throw new JsonSyntaxException(e);
        } catch (Throwable e3) {
            throw new JsonSyntaxException(e3);
        } catch (Throwable e32) {
            throw new JsonIOException(e32);
        } catch (Throwable e322) {
            throw new JsonSyntaxException(e322);
        }
    }

    public static void write(JsonElement jsonElement, JsonWriter jsonWriter) throws IOException {
        TypeAdapters.JSON_ELEMENT.write(jsonWriter, jsonElement);
    }

    public static Writer writerForAppendable(Appendable appendable) {
        return appendable instanceof Writer ? (Writer) appendable : new AppendableWriter(appendable);
    }
}
