package com.patloew.rxwear.transformers;

import com.google.android.gms.wearable.MessageEvent;
import com.patloew.rxwear.IOUtil;

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import java.io.Serializable;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

/* Copyright 2016 Patrick Löwenstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ---------------------------------
 *
 * Transformer that optionally filters MessageEvents by path and returns an
 * Observable<T> of the Serializable from the MessageEvent.
 *
 * Example: MessageEventGetSerializable.<T>filterByPath("/path")
 */
public class MessageEventGetSerializable<T extends Serializable> implements ObservableTransformer<MessageEvent, T> {

    private final String path;
    private final boolean isPrefix;

    private MessageEventGetSerializable(String path, boolean isPrefix) {
        this.path = path;
        this.isPrefix = isPrefix;
    }

    public static <T extends Serializable> ObservableTransformer<MessageEvent, T> noFilter() {
        return new MessageEventGetSerializable<T>(null, false);
    }

    public static <T extends Serializable> ObservableTransformer<MessageEvent, T> filterByPath(String path) {
        return new MessageEventGetSerializable<T>(path, false);
    }

    public static <T extends Serializable> ObservableTransformer<MessageEvent, T> filterByPathPrefix(String pathPrefix) {
        return new MessageEventGetSerializable<T>(pathPrefix, true);
    }

    @Override
    public Observable<T> apply(Observable<MessageEvent> observable) {
        if(path != null) {
            observable = observable.filter(new Predicate<MessageEvent>() {
                @Override public boolean test(MessageEvent messageEvent) throws Exception {
                    if (isPrefix) {
                        return messageEvent.getPath().startsWith(path);
                    } else {
                        return messageEvent.getPath().equals(path);
                    }
                }
            });
        }

        return observable.map(new Function<MessageEvent, T>() {
            @Override public T apply(MessageEvent messageEvent) throws Exception {
                return IOUtil.<T>readObjectFromByteArray(messageEvent.getData());
            }
        });
    }
}