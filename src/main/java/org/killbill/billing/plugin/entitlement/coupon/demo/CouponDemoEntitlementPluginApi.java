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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.killbill.billing.account.api.Account;
import org.killbill.billing.account.api.AccountApiException;
import org.killbill.billing.catalog.api.CatalogApiException;
import org.killbill.billing.catalog.api.Plan;
import org.killbill.billing.catalog.api.PlanPhase;
import org.killbill.billing.catalog.api.PlanPhasePriceOverride;
import org.killbill.billing.catalog.api.PlanPhaseSpecifier;
import org.killbill.billing.entitlement.api.BaseEntitlementWithAddOnsSpecifier;
import org.killbill.billing.entitlement.api.EntitlementSpecifier;
import org.killbill.billing.entitlement.plugin.api.EntitlementContext;
import org.killbill.billing.entitlement.plugin.api.EntitlementPluginApi;
import org.killbill.billing.entitlement.plugin.api.EntitlementPluginApiException;
import org.killbill.billing.entitlement.plugin.api.OnFailureEntitlementResult;
import org.killbill.billing.entitlement.plugin.api.OnSuccessEntitlementResult;
import org.killbill.billing.entitlement.plugin.api.OperationType;
import org.killbill.billing.entitlement.plugin.api.PriorEntitlementResult;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillClock;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.entitlement.coupon.demo.catalog.DefaultBaseEntitlementWithAddOnsSpecifier;
import org.killbill.billing.plugin.entitlement.coupon.demo.catalog.DefaultEntitlementSpecifier;
import org.killbill.billing.plugin.entitlement.coupon.demo.catalog.DefaultPlanPhasePriceOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

public class CouponDemoEntitlementPluginApi implements EntitlementPluginApi {

    private static final Logger logger = LoggerFactory.getLogger(CouponDemoEntitlementPluginApi.class);

    private static final String COUPON_PROPERTY = CouponActivator.PLUGIN_NAME + ":coupon";

    final OSGIKillbillClock clock;
    final OSGIKillbillAPI   killbillAPI;

    public CouponDemoEntitlementPluginApi(final OSGIKillbillClock clock,
                                          final OSGIKillbillAPI killbillAPI) {
        this.clock = clock;
        this.killbillAPI = killbillAPI;
    }

    @Override
    public PriorEntitlementResult priorCall(final EntitlementContext entitlementContext,
                                            final Iterable<PluginProperty> pluginProperties) throws EntitlementPluginApiException {


        if (entitlementContext.getOperationType() != OperationType.CREATE_SUBSCRIPTION &&
                entitlementContext.getOperationType() != OperationType.CREATE_SUBSCRIPTIONS_WITH_AO &&
                entitlementContext.getOperationType() != OperationType.CREATE_SHOPPING_CART_SUBSCRIPTIONS) {
            return null;
        }


        final PluginProperty couponProperty = findCouponProperty(pluginProperties);
        if (couponProperty == null) {
            return null;
        }

        try {
            final Account account = killbillAPI.getAccountUserApi()
                                               .getAccountById(entitlementContext.getAccountId(), entitlementContext);

            final List<BaseEntitlementWithAddOnsSpecifier> baseEntitlementWithAddOnsSpecifiers = new LinkedList<BaseEntitlementWithAddOnsSpecifier>();
            for (final BaseEntitlementWithAddOnsSpecifier curBaseSpecifier : entitlementContext.getBaseEntitlementWithAddOnsSpecifiers()) {
                if (curBaseSpecifier.getEntitlementSpecifier() == null) {
                    continue;
                }

                final List<EntitlementSpecifier> entitlementSpecifiersWithOverrides = new LinkedList<EntitlementSpecifier>();
                for (final EntitlementSpecifier entitlementSpecifier : curBaseSpecifier.getEntitlementSpecifier()) {
                    final PlanPhaseSpecifier spec = entitlementSpecifier.getPlanPhaseSpecifier();
                    final Plan plan = killbillAPI.getCatalogUserApi()
                                                 .getCatalog("whatever", entitlementContext)
                                                 .getCurrentVersion()
                                                 .createOrFindPlan(spec, null);
                    final PlanPhase lastPhase = plan.getFinalPhase();
                    if (lastPhase.getRecurring() != null) {
                        logger.info(String.format("%s overriding price for phase %s ",
                                                  CouponActivator.PLUGIN_NAME,
                                                  lastPhase.getName()));

                        final BigDecimal overridePrice = new BigDecimal((String) couponProperty.getValue());
                        final List<PlanPhasePriceOverride> overrides = new ArrayList<PlanPhasePriceOverride>();
                        overrides.add(new DefaultPlanPhasePriceOverride(lastPhase.getName(),
                                                                        account.getCurrency(),
                                                                        null,
                                                                        overridePrice));

                        entitlementSpecifiersWithOverrides.add(new DefaultEntitlementSpecifier(entitlementSpecifier.getPlanPhaseSpecifier(),
                                                                                               overrides));
                    }
                }

                baseEntitlementWithAddOnsSpecifiers.add(new DefaultBaseEntitlementWithAddOnsSpecifier(curBaseSpecifier.getBundleId(),
                                                                                                      curBaseSpecifier.getBundleExternalKey(),
                                                                                                      entitlementSpecifiersWithOverrides,
                                                                                                      curBaseSpecifier.getEntitlementEffectiveDate(),
                                                                                                      curBaseSpecifier.getBillingEffectiveDate(),
                                                                                                      curBaseSpecifier.isMigrated()));
            }
            return new DefaultPriorEntitlementResult(baseEntitlementWithAddOnsSpecifiers);
        }
        catch (CatalogApiException e) {
            throw new EntitlementPluginApiException(e);
        }
        catch (AccountApiException e) {
            throw new EntitlementPluginApiException(e);
        }
    }

    @Override
    public OnSuccessEntitlementResult onSuccessCall(final EntitlementContext entitlementContext,
                                                    final Iterable<PluginProperty> pluginProperties) throws EntitlementPluginApiException {
        return null;
    }

    @Override
    public OnFailureEntitlementResult onFailureCall(final EntitlementContext entitlementContext,
                                                    final Iterable<PluginProperty> pluginProperties) throws EntitlementPluginApiException {
        return null;
    }

    private PluginProperty findCouponProperty(@Nullable final Iterable<PluginProperty> pluginProperties) {
        return Iterables.tryFind(pluginProperties, new Predicate<PluginProperty>() {
            @Override
            public boolean apply(@Nullable PluginProperty input) {
                return input.getKey().equals(COUPON_PROPERTY);
            }
        }).orNull();
    }
}
