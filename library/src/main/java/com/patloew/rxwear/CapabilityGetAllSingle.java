package com.patloew.rxwear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Wearable;

import io.reactivex.functions.Function;
import java.util.Map;
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
class CapabilityGetAllSingle extends BaseSingle<Map<String, CapabilityInfo>> {

    final int nodeFilter;

    CapabilityGetAllSingle(RxWear rxWear, int nodeFilter, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.nodeFilter = nodeFilter;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final SingleEmitter<Map<String, CapabilityInfo>> emitter) {
        setupWearPendingResult(
                Wearable.CapabilityApi.getAllCapabilities(apiClient, nodeFilter),
                SingleResultCallBack.get(emitter,
                    new Function<CapabilityApi.GetAllCapabilitiesResult, Map<String, CapabilityInfo>>() {
                        @Override public Map<String, CapabilityInfo> apply(CapabilityApi.GetAllCapabilitiesResult t)
                            throws Exception {
                          return t.getAllCapabilities();
                        }
                    })
        );
    }
}
