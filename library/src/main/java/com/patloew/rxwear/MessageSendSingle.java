package com.patloew.rxwear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;

import io.reactivex.functions.Function;
import java.util.concurrent.TimeUnit;

import io.reactivex.SingleEmitter;

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
class MessageSendSingle extends BaseSingle<Integer> {

    final String nodeId;
    final String path;
    final byte[] data;

    MessageSendSingle(RxWear rxWear, String nodeId, String path, byte[] data, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.nodeId = nodeId;
        this.path = path;
        this.data = data;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final SingleEmitter<Integer> emitter) {
        setupWearPendingResult(
                Wearable.MessageApi.sendMessage(apiClient, nodeId, path, data),
                SingleResultCallBack.get(emitter, new Function<MessageApi.SendMessageResult, Integer>() {
                    @Override public Integer apply(MessageApi.SendMessageResult t) throws Exception {
                        return t.getRequestId();
                    }
                })
        );
    }
}
