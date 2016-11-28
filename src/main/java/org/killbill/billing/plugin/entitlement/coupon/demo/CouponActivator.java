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


import org.killbill.billing.entitlement.plugin.api.EntitlementPluginApi;
import org.killbill.billing.osgi.api.OSGIPluginProperties;
import org.killbill.billing.osgi.libs.killbill.KillbillActivatorBase;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillEventDispatcher;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import java.util.Hashtable;

public class CouponActivator extends KillbillActivatorBase {

    public static final String PLUGIN_NAME = "killbill-coupon-demo";


    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);

        logService.log(LogService.LOG_INFO, "Starting " + PLUGIN_NAME);

        final EntitlementPluginApi entitlementPluginApi = new CouponDemoEntitlementPluginApi(clock, killbillAPI, logService);
        registerEntitlementPluginApi(context, entitlementPluginApi);

        // Register a servlet (optional)
        final CouponDemoServlet analyticsServlet = new CouponDemoServlet(logService);
        registerServlet(context, analyticsServlet);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        super.stop(context);
        // Do additional work on shutdown (optional)
    }

    private void registerServlet(final BundleContext context, final HttpServlet servlet) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, Servlet.class, servlet, props);
    }

    private void registerEntitlementPluginApi(final BundleContext context, final EntitlementPluginApi api) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, EntitlementPluginApi.class, api, props);
    }
}
