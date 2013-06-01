package com.koushikdutta.ion;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.DataSink;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.TransformFuture;
import com.koushikdutta.async.parser.AsyncParser;
import com.koushikdutta.async.parser.ByteBufferListParser;
import com.koushikdutta.async.stream.ByteBufferListInputStream;

import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Created by koush on 6/1/13.
 */
public class GsonParser<T> implements AsyncParser<T> {
    Gson gson;
    Type type;
    public GsonParser(Gson gson, Class<T> clazz) {
        this.gson = gson;
        type = clazz;
    }
    public GsonParser(Gson gson, TypeToken<T> token) {
        this.gson = gson;
        type = token.getType();
    }
    @Override
    public Future<T> parse(DataEmitter emitter) {
        return new TransformFuture<T, ByteBufferList>() {
            @Override
            protected void transform(ByteBufferList result) throws Exception {
                ByteBufferListInputStream bin = new ByteBufferListInputStream(result);
                T ret = (T)gson.fromJson(new JsonReader(new InputStreamReader(bin)), type);
                setComplete(ret);
            }
        }
        .from(new ByteBufferListParser().parse(emitter));
    }

    @Override
    public void write(DataSink sink, T value, CompletedCallback completed) {

    }
}
