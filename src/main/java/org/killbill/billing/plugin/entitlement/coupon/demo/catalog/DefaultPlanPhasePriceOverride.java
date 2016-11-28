/*
 * Copyright 2014-2016 The Billing Project, LLC
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

package org.killbill.billing.plugin.entitlement.coupon.demo.catalog;

import java.math.BigDecimal;

import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.catalog.api.PlanPhasePriceOverride;
import org.killbill.billing.catalog.api.PlanPhaseSpecifier;

public class DefaultPlanPhasePriceOverride implements PlanPhasePriceOverride {

    private final String phaseName;
    private final PlanPhaseSpecifier planPhaseSpecifier;
    private final Currency currency;
    private final BigDecimal fixedPrice;
    private final BigDecimal recurringPrice;

    public DefaultPlanPhasePriceOverride(final PlanPhaseSpecifier planPhaseSpecifier, final Currency currency, final BigDecimal fixedPrice, final BigDecimal recurringPrice) {
        this.phaseName = null;
        this.planPhaseSpecifier = planPhaseSpecifier;
        this.currency = currency;
        this.fixedPrice = fixedPrice;
        this.recurringPrice = recurringPrice;
    }

    public DefaultPlanPhasePriceOverride(final String phaseName, final Currency currency, final BigDecimal fixedPrice, final BigDecimal recurringPrice) {
        this.phaseName = phaseName;
        this.planPhaseSpecifier = null;
        this.currency = currency;
        this.fixedPrice = fixedPrice;
        this.recurringPrice = recurringPrice;
    }

    @Override
    public String getPhaseName() {
        return phaseName;
    }

    @Override
    public PlanPhaseSpecifier getPlanPhaseSpecifier() {
        return planPhaseSpecifier;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public BigDecimal getFixedPrice() {
        return fixedPrice;
    }

    @Override
    public BigDecimal getRecurringPrice() {
        return recurringPrice;
    }
}
