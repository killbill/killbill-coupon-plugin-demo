# Overview

Kill Bill coupon plugin (demo).

Release builds are available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.kill-bill.billing.plugin.java%22%20AND%20a%3A%22coupon-demo-plugin%22) with coordinates `org.kill-bill.billing.plugin.java:coupon-demo-plugin`.

## Kill Bill compatibility

| Plugin version | Kill Bill version |
| -------------: | ----------------: |
| 0.0.y          | 0.18.z            |
| 0.0.y          | 0.20.z            |
| 0.1.y          | 0.22.z            |

## Description

This plugin highlights the use the of the EntitlementPluginApi as a means to create a coupon plugin. The current functionality is very rudimentary: The plugin will register itself and therefore be called for each subscription operation (create new subscription, change plan, cancellation, pause, resume, ..). In order to keep it very simple, the plugin will:
* Only look for subscription creation
* Look for a property `killbill-coupon-demo:coupon` and interpret the value as a BigDecimal that will be used to override the price of the last phase where a recurring price already exists.
* Make use of the catalog price override functionality to override the price on the fly (discounted coupon price)


Assuming an `Account` with id `1f979085-1765-471b-878a-5f640db4d831` was created and the `bob/lazar` tenant was configured with a catalog with a `planName=sports-monthly`:

```
curl -v \
     -u admin:password \
     -H "X-Killbill-ApiKey: bob" \
     -H "X-Killbill-ApiSecret: lazar" \
     -H "Content-Type: application/json" \
     -H "X-Killbill-CreatedBy: demo" \
     -X POST \
     --data-binary '{"accountId":"1f979085-1765-471b-878a-5f640db4d831", "externalKey":"foo", "bundleExternalKey":"bar", "planName":"sports-monthly"}' \
     "http://127.0.0.1:8080/1.0/kb/subscriptions?pluginProperty=killbill-coupon-demo:coupon%3D108.7"
```

And then we will see the following:

The entry in the `catalog_override_plan_definition`: 
```
mysql> select * from catalog_override_plan_definition  where tenant_record_id = 1\G
*************************** 1. row ***************************
       record_id: 29
parent_plan_name: sports-monthly
  effective_date: 2013-02-08 00:00:00
       is_active: 1
    created_date: 2015-07-04 00:49:29
      created_by: demo
tenant_record_id: 1
```

And then the events for our newly created subscription:

```
mysql> select * from subscription_events where tenant_record_id = 1\G
*************************** 1. row ***************************
        record_id: 1793
               id: 38d88dd7-2b57-4ace-a0fa-0d0c93d1babd
       event_type: API_USER
        user_type: CREATE
   requested_date: 2015-07-04 00:49:29
   effective_date: 2015-07-04 00:49:29
  subscription_id: 2e2ca911-0ce6-4ca8-a5fd-289c6ec27e91
        plan_name: sports-monthly-29
       phase_name: sports-monthly-29-trial
  price_list_name: DEFAULT
  current_version: 1
        is_active: 1
       created_by: demo
     created_date: 2015-07-04 00:49:29
       updated_by: demo
     updated_date: 2015-07-04 00:49:29
account_record_id: 854
 tenant_record_id: 1
*************************** 2. row ***************************
        record_id: 1794
               id: a0ff861a-1dfe-4433-83ad-c54ee2779286
       event_type: PHASE
        user_type: NULL
   requested_date: 2015-07-04 00:49:29
   effective_date: 2015-08-03 00:49:29
  subscription_id: 2e2ca911-0ce6-4ca8-a5fd-289c6ec27e91
        plan_name: NULL
       phase_name: sports-monthly-29-evergreen
  price_list_name: NULL
  current_version: 1
        is_active: 1
       created_by: demo
     created_date: 2015-07-04 00:49:29
       updated_by: demo
     updated_date: 2015-07-04 00:49:29
account_record_id: 854
 tenant_record_id: 1
2 rows in set (0.00 sec)
```


# How That *Could* Work In Real Life

## Creation of the coupons

The plugin could export some endpoints to register new coupons. Those coupons could be as sophisticated as desired and include (max number of utilisation, redemption date, percentage or raw value discount, ...). The plugin would maintain all that state (probably though the use of a few tables (plugin specific schema).

## Use of Coupons

The mechanism would be very similar to what was implemented, but instead of passing the property `killbill-coupon-demo:coupon` with a price, one would now reference some of registered coupons, and the plugin would implement all the logic to understand whether the coupon is still valid, what is the discounted value, ... and in fine use the catalog override api to override the various prices (fixed price and recurring price) across the various phases.





