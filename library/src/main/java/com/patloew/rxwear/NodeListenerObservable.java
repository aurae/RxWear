package com.patloew.rxwear;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.patloew.rxwear.events.NodeEvent;

import java.util.concurrent.TimeUnit;

import rx.Observer;

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
 * limitations under the License. */
public class NodeListenerObservable extends BaseObservable<NodeEvent> {


    private NodeApi.NodeListener listener;

    NodeListenerObservable(RxWear rxWear, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final Observer<? super NodeEvent> observer) {
        listener = new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(Node node) {
                observer.onNext(new NodeEvent(node, true));
            }

            @Override
            public void onPeerDisconnected(Node node) {
                observer.onNext(new NodeEvent(node, false));
            }
        };

        ResultCallback<Status> resultCallback = new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (!status.isSuccess()) {
                    observer.onError(new StatusException(status));
                }
            }
        };

        setupWearPendingResult(Wearable.NodeApi.addListener(apiClient, listener), resultCallback);
    }


    @Override
    protected void onUnsubscribed(GoogleApiClient apiClient) {
        Wearable.NodeApi.removeListener(apiClient, listener);
    }
}
