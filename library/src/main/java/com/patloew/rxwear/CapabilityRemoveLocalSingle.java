package com.patloew.rxwear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi;
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
class CapabilityRemoveLocalSingle extends BaseSingle<Status> {

    final String capability;

    CapabilityRemoveLocalSingle(RxWear rxWear, String capability, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.capability = capability;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final SingleEmitter<Status> emitter) {
        setupWearPendingResult(
                Wearable.CapabilityApi.removeLocalCapability(apiClient, capability),
                SingleResultCallBack.get(emitter, new Function<CapabilityApi.RemoveLocalCapabilityResult, Status>() {
                    @Override public Status apply(CapabilityApi.RemoveLocalCapabilityResult t) throws Exception {
                        return t.getStatus();
                    }
                })
        );
    }
}
