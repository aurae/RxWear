package com.patloew.rxwear.transformers;

import com.google.android.gms.wearable.DataEvent;
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
 * Transformer that optionally filters DataEvents by path and/or type and
 * returns an Observable<T> of the Serializable from the DataItem.
 *
 * Example: DataEventGetSerializable.<T>filterByPathAndType("/path", DataEvent.TYPE_CHANGED)
 */
public class DataEventGetSerializable<T extends Serializable> implements ObservableTransformer<DataEvent, T> {

    private final String path;
    private final boolean isPrefix;
    private final Integer type;

    private DataEventGetSerializable(String path, boolean isPrefix, Integer type) {
        this.path = path;
        this.isPrefix = isPrefix;
        this.type = type;

    }

    public static <T extends Serializable> ObservableTransformer<DataEvent, T> noFilter() {
        return new DataEventGetSerializable<T>(null, false, null);
    }

    public static <T extends Serializable> ObservableTransformer<DataEvent, T> filterByPath(String path) {
        return new DataEventGetSerializable<T>(path, false, null);
    }

    public static <T extends Serializable> ObservableTransformer<DataEvent, T> filterByPathAndType(String path, int type) {
        return new DataEventGetSerializable<T>(path, false, type);
    }

    public static <T extends Serializable> ObservableTransformer<DataEvent, T> filterByPathPrefix(String pathPrefix) {
        return new DataEventGetSerializable<T>(pathPrefix, true, null);
    }

    public static <T extends Serializable> ObservableTransformer<DataEvent, T> filterByPathPrefixAndType(String pathPrefix, int type) {
        return new DataEventGetSerializable<T>(pathPrefix, true, type);
    }

    public static <T extends Serializable> ObservableTransformer<DataEvent, T> filterByType(int type) {
        return new DataEventGetSerializable<T>(null, false, type);
    }

    @Override
    public Observable<T> apply(Observable<DataEvent> observable) {
        if(type != null) {
            observable = observable.filter(new Predicate<DataEvent>() {
                @Override public boolean test(DataEvent dataEvent) throws Exception {
                    return dataEvent.getType() == type;
                }
            });
        }

        if(path != null) {
            observable = observable.filter(new Predicate<DataEvent>() {
                @Override public boolean test(DataEvent dataEvent) throws Exception {
                    if (isPrefix) {
                        return dataEvent.getDataItem().getUri().getPath().startsWith(path);
                    } else {
                        return dataEvent.getDataItem().getUri().getPath().equals(path);
                    }
                }
            });
        }

        return observable.map(new Function<DataEvent, T>() {
            @Override public T apply(DataEvent dataEvent) throws Exception {
                return IOUtil.<T>readObjectFromByteArray(dataEvent.getDataItem().getData());
            }
        });
    }
}
