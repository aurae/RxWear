package com.patloew.rxwear.transformers;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

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
 * Transformer that filters MessageEvents by path and returns an
 * Observable<DataMap> of the dataMap from the MessageEvent.
 */
public class MessageEventGetDataMap implements ObservableTransformer<MessageEvent, DataMap> {

    private final String path;
    private final boolean isPrefix;

    private MessageEventGetDataMap(String path, boolean isPrefix) {
        this.path = path;
        this.isPrefix = isPrefix;
    }

    public static ObservableTransformer<MessageEvent, DataMap> noFilter() {
        return new MessageEventGetDataMap(null, false);
    }

    public static ObservableTransformer<MessageEvent, DataMap> filterByPath(String path) {
        return new MessageEventGetDataMap(path, false);
    }

    public static ObservableTransformer<MessageEvent, DataMap> filterByPathPrefix(String pathPrefix) {
        return new MessageEventGetDataMap(pathPrefix, true);
    }

    @Override
    public Observable<DataMap> apply(Observable<MessageEvent> observable) {
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

        return observable.map(new Function<MessageEvent, DataMap>() {
            @Override public DataMap apply(MessageEvent messageEvent) throws Exception {
                return DataMap.fromByteArray(messageEvent.getData());
            }
        });
    }
}
