# Android SDK — Eulerian Analytics

Guía completa de instalación y etiquetado para el SDK Android de Eulerian Analytics.

---

## Índice

- [Instalación](#instalación)
- [Inicialización](#inicialización)
- [Tipos de etiquetas](#tipos-de-etiquetas)
  - [Página genérica](#página-genérica) — `EAProperties`
  - [Página de producto](#página-de-producto) — `EAProducts` (1 producto)
  - [Página de categoría](#página-de-categoría) — `EAProducts` (N productos)
  - [Página de búsqueda interna](#página-de-búsqueda-interna) — `EASearch`
  - [Página de error 404](#página-de-error-404) — `EAProperties`
  - [Página de presupuesto](#página-de-presupuesto) — `EAEstimate`
  - [Página de carrito](#página-de-carrito) — `EACart`
  - [Página de pedido](#página-de-pedido) — `EAOrder`
  - [Tracking de merchandising](#tracking-de-merchandising) — `EATpView` / `EATpClick`
  - [Context Flag (CFLAG)](#context-flag-cflag) — `SiteCentricCFlag`
- [Tracking WebView](#tracking-webview)
- [Métricas de la aplicación](#métricas-de-la-aplicación)
- [GDPR / Consentimiento](#gdpr--consentimiento)
- [Dependencias](#dependencias)

---

## Instalación

Añade el repositorio JitPack en tu `build.gradle` raíz:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Añade la dependencia en el `build.gradle` de tu módulo:

```groovy
dependencies {
    implementation 'com.github.EulerianTechnologies:eanalytics-android:1.9.0'
}
```

Añade los permisos necesarios en tu `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

Para rastrear instalaciones desde Google Play, añade también este receiver:

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

## Inicialización

Inicializa el SDK en tu `Application.onCreate()`. Declara primero tu subclase Application en el manifest.

```xml
<!-- AndroidManifest.xml -->
<application android:name=".DemoApp">
```

```java
public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Reemplaza "example.demo.com" con tu subdominio de tracking
        EAnalytics.init(this, "example.demo.com", false);
    }
}
```

> El tercer parámetro activa (`true`) o desactiva (`false`) los logs de depuración.

> **Atribución de tráfico en Eulerian:** a diferencia del tracking web donde la URL identifica el sitio, una aplicación nativa no tiene URL. El SDK utiliza automáticamente el **package name** de la aplicación (ej. `com.mycompany.myapp`) como URL interna enviada a Eulerian (parámetros `ea-appname` y `url`). Eulerian usa el subdominio de tracking pasado a `init()` para atribuir el tráfico al sitio correcto en su interfaz.

> Si varias aplicaciones comparten el mismo subdominio de tracking, usa el parámetro `from` en las etiquetas para distinguirlas.

---

## Tipos de etiquetas

> `path` (nombre de página) es **obligatorio** en cada etiqueta. A diferencia del tracking web, nunca se infiere automáticamente desde la URL.

---

### Página genérica — `EAProperties`

Úsala en todas las páginas que no sean ficha de producto, carrito, pedido o presupuesto (incluida la homepage y los pasos del embudo entre carrito y confirmación de pedido). Registra páginas vistas, visitas y fuentes de tráfico orgánico.

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

### Página de producto — `EAProducts` (1 producto)

Registra páginas vistas de producto y alimenta el informe "Adquisición & Rendimiento de producto". Una etiqueta se interpreta como página de producto cuando contiene **exactamente una** referencia de producto y ninguno de los parámetros `scart`, `estimate`, `ref`, `amount`.

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

### Página de categoría — `EAProducts` (N productos)

Envía las primeras N referencias de producto mostradas en una página de resultados/categoría a los socios para retargeting. Una etiqueta se interpreta como página de categoría cuando contiene **más de una** referencia de producto y ninguno de los parámetros `scart`, `estimate`, `ref`, `amount`.

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

### Página de búsqueda interna — `EASearch`

Registra las búsquedas internas, el número de resultados y parámetros adicionales. Una etiqueta se interpreta como página de búsqueda cuando contiene el parámetro `isearchengine`. No es exclusivo: puede combinarse con una referencia de producto en la misma etiqueta.

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

### Página de error 404 — `EAProperties`

Marca una página como error 404 añadiendo `set("error", "1")` a cualquier etiqueta. Alimenta el informe "Páginas en error".

```java
EAProperties errorTag = new EAProperties.Builder("erreur|404")
        .set("error", "1")
        .build();
EAnalytics.getInstance().track(errorTag);
```

---

### Página de presupuesto — `EAEstimate`

Registra presupuestos y los productos asociados. Una etiqueta se interpreta como página de presupuesto cuando contiene los parámetros `ref` y `estimate`. Los presupuestos se deduplicen por referencia: una segunda llamada con la misma referencia se ignora.

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

### Página de carrito — `EACart`

Registra carritos iniciados y permite calcular las tasas de conversión y abandono. Una etiqueta se interpreta como página de carrito cuando contiene el parámetro `scart`. La duración de vida de un carrito es de 30 minutos (sesión deslizante).

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
        .setCartCumul(false)               // false = snapshot completo del carrito; true = acumulativo
        .addProduct(product1, 50.30, 2)
        .build();
EAnalytics.getInstance().track(cartPage);
```

> `setCartCumul(false)`: los productos de la etiqueta representan el contenido completo del carrito. `setCartCumul(true)`: los productos se acumulan en cada llamada sucesiva.

---

### Página de pedido — `EAOrder`

Registra conversiones y ROI. Una etiqueta se interpreta como página de pedido cuando contiene `ref` y `amount` pero ni `scart` ni `estimate`. Los pedidos se deduplicen por referencia. Implementa esta etiqueta lo antes posible en el flujo de pago.

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
        .setEstimateRef("asd123qwe")        // opcional
        .set("custom-param-key", "custom-param-value")
        .addProduct(product1, 25.15, 2)
        .build();
EAnalytics.getInstance().track(orderPage);
```

---

### Tracking de merchandising — `EATpView` / `EATpClick`

Registra impresiones y clics en bloques de merchandising (listas de recomendación, banners, etc.). Se envían como peticiones **GET** a `/tpview/` y `/tpclick/` — a diferencia del resto de etiquetas que se envían por POST. Soporta el mismo mecanismo de reintento offline que el resto del SDK.

```java
// Impresión en un bloque de merchandising
EATpView view = new EATpView.Builder("homepage")
        .setSiteName("my-site")
        .setCampaign("summer_sale")
        .setPlacement("banner_top")
        .addProduct("PROD_001", 0)          // referencia de producto, posición
        .addProduct("PROD_002", 1)
        .setUrl("http://eulerian.net")
        .build();
EAnalytics.getInstance().track(view);

// Clic en un producto del bloque
EATpClick click = new EATpClick.Builder("homepage")
        .setSiteName("my-site")
        .setCampaign("summer_sale")
        .setPlacement("banner_top")
        .setProduct("PROD_001", 2)          // referencia de producto, posición en el momento del clic
        .setUrl("http://eulerian.net")
        .build();
EAnalytics.getInstance().track(click);
```

---

### Context Flag (CFLAG) — `SiteCentricCFlag`

Adjunta uno o varios context flags a cualquier etiqueta para enriquecer tus informes con dimensiones contextuales. Cada flag acepta hasta 10 valores.

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

## Tracking WebView

Para aplicaciones híbridas que abren una WebView, pasa el identificador interno del SDK en la URL de apertura para mantener la continuidad del tracking entre la app nativa y la sesión web. Añade también el parámetro `edev` para calificar el tráfico como proveniente de una app nativa (obligatorio para el seguimiento de campañas in-app y notificaciones push).

```java
String euidl = EAnalytics.getEuidl();
// Construye la URL de la WebView:
// http://www.demo.fr/android-landing?ea-euidl-bypass=<euidl>&edev=AppNativeAndroidphone
```

Valores aceptados para `edev`:

| Valor | Dispositivo |
|-------|-------------|
| `AppNativeAndroidphone` | Teléfono Android |
| `AppNativeAndroidtablet` | Tablet Android |
| `AppNativeIOSphone` | iPhone |
| `AppNativeIOStablet` | iPad |

> `ea-euidl-bypass` debe estar presente en **cada cambio de URL** dentro de la WebView, de lo contrario se pierde el vínculo entre la navegación de la app y la web.

---

## Métricas de la aplicación

Registra descargas y actualizaciones incluyendo estos parámetros en la primera etiqueta enviada al lanzar la aplicación.

| Parámetro | Valor | Descripción |
|-----------|-------|-------------|
| `ea-appname` | Nombre de la app | Identificador de la app — **nunca debe cambiar** |
| `ea-appversion` | Cadena de versión | Versión actual |
| `ea-appinstalled` | `1` | Presente si la app existía antes de integrar Eulerian |

**Lógica del sistema:**
- La métrica **Descarga** se incrementa cuando `ea-appname` se ve por primera vez para este usuario (sin `ea-appinstalled`).
- La métrica **Actualización** se incrementa cuando `ea-appname` no cambia pero `ea-appversion` tiene un nuevo valor respecto al último lanzamiento.

---

## GDPR / Consentimiento

> Los dos modos siguientes son **mutuamente excluyentes**. Elige uno y no uses ambos en la misma aplicación.

### Modo 1 — TCF v2 (`gdpr_consent`)

Envía la TCString generada por tu CMP una sola vez por visitante, en el momento en que el usuario expresa su opt-in u opt-out.

```java
EAProperties consentTag = new EAProperties.Builder("|univers|rubrique|page")
        .setUID("5434742")
        .set("abonnement", "mensuel")
        .set("gdpr_consent", "EADURF214345")  // tu TCString
        .build();
EAnalytics.getInstance().track(consentTag);
```

### Modo 2 — Categorías (`pmcat`)

Envía los IDs de Eulerian de las categorías **rechazadas**, separados por `-`. Envía `-` solo si el usuario acepta todas las categorías.

```java
EAProperties consentTag = new EAProperties.Builder("|univers|rubrique|page")
        .setUID("5434742")
        .set("abonnement", "mensuel")
        .set("pmcat", "1-10")  // IDs de categorías rechazadas
        .build();
EAnalytics.getInstance().track(consentTag);
```

Ejemplo de mapeo de categorías:

| Categoría | ID Eulerian |
|-----------|-------------|
| analytics | 1 |
| publicidad | 10 |
| funcional | 19 |

*Ejemplo: el usuario rechaza analytics + publicidad → `pmcat` = `1-10`*

### URL de opt-out general

Incluye esta URL en tu página de privacidad o en tu CMP para un opt-out total incondicional:

```
https://<tu-dominio-de-tracking>/optout.html?url=<tu-dominio>
```

### Resumen

| | Modo TCF v2 | Modo Categorías |
|---|---|---|
| **Parámetro** | `gdpr_consent` | `pmcat` |
| **Cuándo** | En opt-in/out | En opt-in/out |
| **Contenido** | TCString | IDs rechazados o `-` |
| **Compatibilidad CMP** | CMP TCF v2 | CMP propio / no-TCF |

---

## Dependencias

Las siguientes dependencias se incluyen automáticamente con el SDK:

```groovy
com.google.android.gms:play-services-ads-identifier:17.0.0
com.android.installreferrer:installreferrer:2.2
```
