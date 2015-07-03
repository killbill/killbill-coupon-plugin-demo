/*
 * Copyright 2014-2015 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.plugin.entitlement.coupon.demo;

import org.killbill.billing.entitlement.plugin.api.EntitlementContext;
import org.killbill.billing.entitlement.plugin.api.EntitlementPluginApi;
import org.killbill.billing.entitlement.plugin.api.EntitlementPluginApiException;
import org.killbill.billing.entitlement.plugin.api.OnFailureEntitlementResult;
import org.killbill.billing.entitlement.plugin.api.OnSuccessEntitlementResult;
import org.killbill.billing.entitlement.plugin.api.PriorEntitlementResult;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillLogService;

import java.util.Properties;

public class CouponDemoEntitlementPluginApi implements EntitlementPluginApi {

    final OSGIKillbillLogService logService;

    public CouponDemoEntitlementPluginApi(final Properties properties, final OSGIKillbillLogService logService) {
        this.logService = logService;
    }

    @Override
    public PriorEntitlementResult priorCall(EntitlementContext entitlementContext, Iterable<PluginProperty> pluginProperties) throws EntitlementPluginApiException {
        return null;
    }

    @Override
    public OnSuccessEntitlementResult onSuccessCall(EntitlementContext entitlementContext, Iterable<PluginProperty> pluginProperties) throws EntitlementPluginApiException {
        return null;
    }

    @Override
    public OnFailureEntitlementResult onFailureCall(EntitlementContext entitlementContext, Iterable<PluginProperty> pluginProperties) throws EntitlementPluginApiException {
        return null;
    }
}
