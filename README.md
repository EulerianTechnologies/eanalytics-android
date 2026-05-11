# Android SDK for Eulerian Analytics #

## Installation

*Step 1.* Add the JitPack repository to your build file

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

*Step 2.* Add the dependency

```groovy
dependencies {
    implementation 'com.github.EulerianTechnologies:eanalytics-android:1.9.0'
}
```

*Step 3.* In your AndroidManifest.xml, add the following permissions :

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

## Run Eulerian Analytics

Before using the SDK make sure to initialize it, in your Application onCreate() for instance.

```xml
<!-- in your AndroidManifest.xml-->
<application
   android:name=".DemoApp">
```

```java
public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EAnalytics.init(this, "example.demo.com", false);
    }

}
```
Now you can track any properties you want. The generic properties is EAProperties, and the SDK provides convenience classes for the most common usage (ie. EACart, EAEstimate, EAProducts, etc...)


```java
EASearch search = new EASearch.Builder("/path-example", "banana")
                .setParams(new Params.Builder()
                        .addParam("provenance", "martinique")
                        .addParam("couleur", "jaune")
                        .build())
                .setResults(42)
                .build();

EAnalytics.getInstance().track(search)
```

## Dependencies

Please note that Eulerian Analytics SDK includes the following dependencies :

* com.google.android.gms:play-services-base:9.2.0

## Tutorial screenshots

Step 1 to 3 has been reduced in simply grabbing Eulerian Analytics sdk via Gradle.

![Capture d’écran 2015-04-17 à 10.20.12.png](https://bitbucket.org/repo/kA6LdM/images/3850475813-Capture%20d%E2%80%99%C3%A9cran%202015-04-17%20%C3%A0%2010.20.12.png)

![Capture d’écran 2015-04-17 à 10.20.17.png](https://bitbucket.org/repo/kA6LdM/images/807569072-Capture%20d%E2%80%99%C3%A9cran%202015-04-17%20%C3%A0%2010.20.17.png)

![Capture d’écran 2015-04-17 à 10.23.54.png](https://bitbucket.org/repo/kA6LdM/images/275415870-Capture%20d%E2%80%99%C3%A9cran%202015-04-17%20%C3%A0%2010.23.54.png)

# Tagging plan

Link to [Android tagging plan](https://github.com/EulerianTechnologies/eanalytics-android/blob/master/TAGGINGPLAN_FR.md)

## Merchandise tracking (EATpView / EATpClick)

A dedicated **merchandise** tracking flow has been added to send impressions and clicks on product showcases, recommendation lists and similar surfaces.

Two new trackable properties, modeled as subclasses of `EAProperties`:

- **EATpView** — impression events, sent on `GET /tpview/`
- **EATpClick** — click events, sent on `GET /tpclick/`

Unlike the existing events (`EACart`, `EAOrder`, `EAEstimate`, `EASearch`, `EAProducts`), which are POSTed to the standard tracking endpoint, merchandise events are sent as **GET** requests on the two new dedicated paths. They share the same offline retry mechanism as the rest of the SDK: if the request fails, the payload is stored locally and replayed on the next tracking call or on the next app launch.

### What's new

- New `EATpClick` and `EATpView` classes (`eanalytics/src/main/java/com/eulerian/android/sdk/`) sending merchandise events as GET requests on `/tpview/` and `/tpclick/`.
- `Action`: new `name` / `mode` / `label` / `params` API; the legacy `in` / `out` fields have been **removed**.
- `HttpHelper`: GET request support added.
- `PropertiesTracker` and `EAnalytics`: events are routed to the correct endpoint based on their type.
- `StoredPropertiesTracker`: typed replay of offline payloads (the event type is stored next to the payload and used on restart).
- Demo `MainActivity` + layout: dedicated buttons for the merchandise flow.

### Example

```java
// Impression on a merchandise block — sent on GET /tpview/
EATpView view = new EATpView.Builder("homepage")
        .setSiteName("my-site")
        .setCampaign("summer_sale")
        .setPlacement("banner_top")
        .addProduct("PROD_001", 0)
        .addProduct("PROD_002", 1)
        .setUrl("http://eulerian.net")
        .build();
EAnalytics.getInstance().track(view);

// Click on a product inside that block — sent on GET /tpclick/
EATpClick click = new EATpClick.Builder("homepage")
        .setSiteName("my-site")
        .setCampaign("summer_sale")
        .setPlacement("banner_top")
        .setProduct("PROD_001", 2)
        .setUrl("http://eulerian.net")
        .build();
EAnalytics.getInstance().track(click);
```
