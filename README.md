# Android SDK — Eulerian Analytics

Complete installation and tagging guide for the Eulerian Analytics Android SDK.

---

## Table of Contents

- [Installation](#installation)
- [Initialization](#initialization)
- [Tag Types](#tag-types)
  - [Generic Page](#generic-page) — `EAProperties`
  - [Product Page](#product-page) — `EAProducts` (1 product)
  - [Category Page](#category-page) — `EAProducts` (N products)
  - [Search Page](#search-page) — `EASearch`
  - [Error 404](#error-404) — `EAProperties`
  - [Estimate](#estimate) — `EAEstimate`
  - [Cart](#cart) — `EACart`
  - [Order](#order) — `EAOrder`
  - [Merchandise Tracking](#merchandise-tracking) — `EATpView` / `EATpClick`
  - [Context Flag (CFLAG)](#context-flag-cflag) — `SiteCentricCFlag`
- [WebView Tracking](#webview-tracking)
- [App Metrics](#app-metrics)
- [GDPR / Consent](#gdpr--consent)
- [Dependencies](#dependencies)

---

## Installation

Add the JitPack repository to your project-level `build.gradle`:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your module-level `build.gradle`:

```groovy
dependencies {
    implementation 'com.github.EulerianTechnologies:eanalytics-android:1.9.0'
}
```

Add the required permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

To track app installs from Google Play, also add this receiver:

```xml
<receiver
    android:name="com.eulerian.android.sdk.InstallReferrerReceiver"
    android:exported="true">
    <intent-filter>
        <action android:name="com.android.vending.INSTALL_REFERRER" />
    </intent-filter>
</receiver>
```

---

## Initialization

Initialize the SDK in your `Application.onCreate()`. Declare your Application subclass in the manifest first.

```xml
<!-- AndroidManifest.xml -->
<application android:name=".DemoApp">
```

```java
public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Replace "example.demo.com" with your tracking subdomain
        EAnalytics.init(this, "example.demo.com", false);
    }
}
```

> The third parameter enables (`true`) or disables (`false`) debug logging.

> **Traffic attribution in Eulerian:** unlike web tracking where the URL identifies the site, a native app has no URL. The SDK automatically uses the app's **package name** (e.g. `com.mycompany.myapp`) as the internal URL sent to Eulerian (`ea-appname` and `url` parameters). Eulerian uses the tracking subdomain passed to `init()` to attribute traffic to the correct site in its interface.

> If multiple apps share the same tracking subdomain, use the `from` parameter in tags to distinguish them.

---

## Tag Types

> `path` (page name) is **required** on every tag. Unlike web tracking, it is never inferred automatically from the URL.

---

### Generic Page — `EAProperties`

Use on every page that is not a product, cart, order, or estimate page (including the homepage and conversion funnel steps between cart and order confirmation). Tracks pageviews, visits, and organic traffic sources.

```java
EAProperties genericTag = new EAProperties.Builder("NOM_PAGE")
        .setPageGroup("MY-PAGEGROUP")
        .setUID("123asd")
        .setEmail("test@test.fr")
        .setProfile("visitor")
        .set("KEY-CUSTOM-PARAM", "VALUE")
        .build();
EAnalytics.getInstance().track(genericTag);
```

---

### Product Page — `EAProducts` (1 product)

Tracks product page views and feeds the "Acquisition & Product Performance" report. A tag is treated as a product page when it contains **exactly one** product ref and none of `scart`, `estimate`, `ref`, `amount`.

```java
Params productParams = new Params.Builder()
        .addParam("category", "clothes")   // prdparam-category
        .addParam("brand", "nike")          // prdparam-brand
        .build();

Product product1 = new Product.Builder("ref-product")
        .setName("name-product")
        .setGroup("group-product")
        .setParams(productParams)
        .build();

EAProducts productPage = new EAProducts.Builder("NOM_PAGE")
        .setPageGroup("my-pagegroup")
        .setUID("123asd")
        .setEmail("test@test.fr")
        .setProfile("looker")
        .addProduct(product1)
        .build();
EAnalytics.getInstance().track(productPage);
```

---

### Category Page — `EAProducts` (N products)

Sends the first N product refs shown on a results/category page to partners for retargeting. A tag is treated as a category page when it contains **more than one** product ref and none of `scart`, `estimate`, `ref`, `amount`.

```java
Product product1 = new Product.Builder("ref-product1").build();
Product product2 = new Product.Builder("ref-product2").build();
Product product3 = new Product.Builder("ref-product3").build();

EAProducts resultPage = new EAProducts.Builder("nom-de-page")
        .setPageGroup("my-pagegroup")
        .setUID("123asd")
        .setEmail("test@test.fr")
        .addProduct(product1)
        .addProduct(product2)
        .addProduct(product3)
        .build();
EAnalytics.getInstance().track(resultPage);
```

---

### Search Page — `EASearch`

Tracks internal search queries, result counts, and custom search parameters. A tag is treated as a search page when it contains the `isearchengine` parameter. Not exclusive: can be combined with a product ref on the same tag.

```java
Params searchParams = new Params.Builder()
        .addParam("motcle", "veste")
        .addParam("montant_min", "100.00")
        .addParam("montant_max", "400.00")
        .build();

EASearch searchPage = new EASearch.Builder("Moteur_interne|veste", "moteur_interne")
        .setUID("34678")
        .setResults(150)
        .setParams(searchParams)
        .build();
EAnalytics.getInstance().track(searchPage);
```

---

### Error 404 — `EAProperties`

Flag a page as a 404 error by adding `set("error", "1")` to any tag. Feeds the "Pages en erreur" report.

```java
EAProperties errorTag = new EAProperties.Builder("erreur|404")
        .set("error", "1")
        .build();
EAnalytics.getInstance().track(errorTag);
```

---

### Estimate — `EAEstimate`

Tracks quotes and associated products. A tag is treated as an estimate page when it contains both `ref` and `estimate` parameters. Estimates are deduplicated by reference: a second call with the same ref is ignored.

```java
Product product1 = new Product.Builder("505").build();

EAEstimate estimatePage = new EAEstimate.Builder("Credit|devis", "C4536567")
        .setPageGroup("my-pagegroup")
        .setUID("123asd")
        .setEmail("test@test.fr")
        .setAmount(5000.00)
        .setType("Credit_48mois")
        .set("custom-param-KEY", "custom-param-value")
        .addProduct(product1, 5000.00, 1)
        .build();
EAnalytics.getInstance().track(estimatePage);
```

---

### Cart — `EACart`

Tracks started carts and feeds conversion/abandonment rate calculations. A tag is treated as a cart page when it contains the `scart` parameter. Cart lifetime is 30 minutes (rolling session).

```java
Params productParams = new Params.Builder()
        .addParam("category", "T-Shirt")   // prdparam-category
        .addParam("brand", "Nike")          // prdparam-brand
        .build();

Product product1 = new Product.Builder("product-123")
        .setName("product-name")
        .setParams(productParams)
        .build();

EACart cartPage = new EACart.Builder("page-name")
        .setPageGroup("my-page-group")
        .setUID("123asd")
        .setEmail("test@test.fr")
        .setProfile("shopper")
        .setCartCumul(false)               // false = full cart snapshot; true = additive
        .addProduct(product1, 50.30, 2)
        .build();
EAnalytics.getInstance().track(cartPage);
```

> `setCartCumul(false)`: products in the tag represent the entire cart contents. `setCartCumul(true)`: products are accumulated across successive calls.

---

### Order — `EAOrder`

Tracks conversions and ROI. A tag is treated as an order page when it contains `ref` and `amount` but neither `scart` nor `estimate`. Orders are deduplicated by reference. Implement as early as possible in the payment flow.

```java
Params productParams = new Params.Builder()
        .addParam("category", "T-Shirt")   // prdparam-category
        .addParam("brand", "Nike")          // prdparam-brand
        .build();

Product product1 = new Product.Builder("product-123")
        .setName("product-name")
        .setParams(productParams)
        .build();

EAOrder orderPage = new EAOrder.Builder("Tunnel|Confirmation", "F654335671")
        .setPageGroup("my-pagegroup")
        .setUID("123asd")
        .setEmail("test@test.fr")
        .setProfile("buyer")
        .setNewCustomer(true)
        .setAmount(50.30)
        .setType("online")
        .setPayment("credit card")
        .setCurrency("EUR")
        .setEstimateRef("asd123qwe")        // optional
        .set("custom-param-key", "custom-param-value")
        .addProduct(product1, 25.15, 2)
        .build();
EAnalytics.getInstance().track(orderPage);
```

---

### Merchandise Tracking — `EATpView` / `EATpClick`

Tracks impressions and clicks on product showcase blocks (recommendation lists, banners, etc.). Sent as **GET** requests on `/tpview/` and `/tpclick/` — unlike other tags which are POSTed. Supports the same offline retry mechanism as the rest of the SDK.

```java
// Impression on a merchandise block
EATpView view = new EATpView.Builder("homepage")
        .setSiteName("my-site")
        .setCampaign("summer_sale")
        .setPlacement("banner_top")
        .addProduct("PROD_001", 0)          // product ref, position
        .addProduct("PROD_002", 1)
        .setUrl("http://eulerian.net")
        .build();
EAnalytics.getInstance().track(view);

// Click on a product in the block
EATpClick click = new EATpClick.Builder("homepage")
        .setSiteName("my-site")
        .setCampaign("summer_sale")
        .setPlacement("banner_top")
        .setProduct("PROD_001", 2)          // product ref, position at click time
        .setUrl("http://eulerian.net")
        .build();
EAnalytics.getInstance().track(click);
```

---

### Context Flag (CFLAG) — `SiteCentricCFlag`

Attach one or more context flags to any tag to enrich your reports with contextual dimensions. Each flag accepts up to 10 values.

```java
import com.eulerian.android.sdk.SiteCentricCFlag;

EAProperties genericTag = new EAProperties.Builder("NOM_PAGE")
        .setUID("UID")
        .setEmail("email@test.fr")
        .setProfile("profile")
        .set("NOM_PARAM_PERSO", "VALEUR_PARAM_PERSO")
        .setCFlag(new SiteCentricCFlag.Builder()
                .set("categorie_1", "rolandgarros", "wimbledon")
                .set("categorie_2", "tennis")
                .set("categorie_3", "usopen")
                .build())
        .build();
EAnalytics.getInstance().track(genericTag);
```

---

## WebView Tracking

For hybrid apps that open a WebView, pass the SDK's internal identifier in the opening URL to maintain tracking continuity between the native app and the web session. Also add the `edev` parameter to qualify the traffic as coming from a native app (required for in-app campaign tracking and push notification attribution).

```java
String euidl = EAnalytics.getEuidl();
// Build the WebView URL:
// http://www.demo.fr/android-landing?ea-euidl-bypass=<euidl>&edev=AppNativeAndroidphone
```

Accepted `edev` values:

| Value | Device |
|-------|--------|
| `AppNativeAndroidphone` | Android phone |
| `AppNativeAndroidtablet` | Android tablet |
| `AppNativeIOSphone` | iPhone |
| `AppNativeIOStablet` | iPad |

> `ea-euidl-bypass` must be present on **every URL change** in the WebView, otherwise the link between app and web navigation is lost.

---

## App Metrics

Track app downloads and updates by including these parameters in the first tag fired on app launch.

| Parameter | Value | Description |
|-----------|-------|-------------|
| `ea-appname` | App name | App identifier — **must never change** |
| `ea-appversion` | Version string | Current version |
| `ea-appinstalled` | `1` | Present if app existed before Eulerian was integrated |

**System logic:**
- **Download** metric increments when `ea-appname` is seen for the first time for this user (without `ea-appinstalled`).
- **Update** metric increments when `ea-appname` is unchanged but `ea-appversion` has a new value compared to the last launch.

---

## GDPR / Consent

> The two modes below are **mutually exclusive**. Choose one and never use both in the same app.

### Mode 1 — TCF v2 (`gdpr_consent`)

Pass the TCString generated by your CMP once per visitor, at the moment the user expresses opt-in or opt-out.

```java
EAProperties consentTag = new EAProperties.Builder("|univers|rubrique|page")
        .setUID("5434742")
        .set("abonnement", "mensuel")
        .set("gdpr_consent", "EADURF214345")  // your TCString
        .build();
EAnalytics.getInstance().track(consentTag);
```

### Mode 2 — Categories (`pmcat`)

Pass the Eulerian IDs of **refused** categories, separated by `-`. Send `-` alone if the user accepts all categories.

```java
EAProperties consentTag = new EAProperties.Builder("|univers|rubrique|page")
        .setUID("5434742")
        .set("abonnement", "mensuel")
        .set("pmcat", "1-10")  // refused category IDs
        .build();
EAnalytics.getInstance().track(consentTag);
```

Example category mapping:

| Category | Eulerian ID |
|----------|-------------|
| analytics | 1 |
| advertising | 10 |
| functional | 19 |

*Example: user refuses analytics + advertising → `pmcat` = `1-10`*

### Opt-out URL

Provide this URL on your privacy page or inside your CMP for a full unconditional opt-out:

```
https://<your-tracking-domain>/optout.html?url=<your-domain>
```

### Summary

| | Mode TCF v2 | Mode Categories |
|---|---|---|
| **Parameter** | `gdpr_consent` | `pmcat` |
| **When** | At opt-in/out | At opt-in/out |
| **Content** | TCString | Refused IDs or `-` |
| **CMP compatibility** | TCF v2 CMP | Custom / non-TCF CMP |

---

## Dependencies

The following dependencies are bundled automatically with the SDK:

```groovy
com.google.android.gms:play-services-ads-identifier:17.0.0
com.android.installreferrer:installreferrer:2.2
```
