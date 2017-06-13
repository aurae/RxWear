package com.patloew.rxwear.transformers;

import com.google.android.gms.wearable.DataItem;
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
 * Example: DataItemGetSerializable.<T>filterByPath("/path")
 */
public class DataItemGetSerializable<T extends Serializable> implements ObservableTransformer<DataItem, T> {

    private final String path;
    private final boolean isPrefix;

    private DataItemGetSerializable(String path, boolean isPrefix) {
        this.path = path;
        this.isPrefix = isPrefix;
    }

    public static <T extends Serializable> ObservableTransformer<DataItem, T> noFilter() {
        return new DataItemGetSerializable<T>(null, false);
    }

    public static <T extends Serializable> ObservableTransformer<DataItem, T> filterByPath(String path) {
        return new DataItemGetSerializable<T>(path, false);
    }

    public static <T extends Serializable> ObservableTransformer<DataItem, T> filterByPathPrefix(String pathPrefix) {
        return new DataItemGetSerializable<T>(pathPrefix, true);
    }
    @Override
    public Observable<T> apply(Observable<DataItem> observable) {
        if(path != null) {
            observable = observable.filter(new Predicate<DataItem>() {
                @Override public boolean test(DataItem dataItem) throws Exception {
                    if (isPrefix) {
                        return dataItem.getUri().getPath().startsWith(path);
                    } else {
                        return dataItem.getUri().getPath().equals(path);
                    }
                }
            });
        }

        return observable.map(new Function<DataItem, T>() {
            @Override public T apply(DataItem dataItem) throws Exception {
                return IOUtil.<T>readObjectFromByteArray(dataItem.getData());
            }
        });
    }
}
