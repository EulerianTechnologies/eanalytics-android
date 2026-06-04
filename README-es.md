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

> **Método genérico `set(String key, String value)`:** disponible en todas las etiquetas, permite añadir cualquier parámetro personalizado no cubierto por los métodos dedicados. Ejemplo: `.set("suscripcion", "mensual")`.

---

### Página genérica — `EAProperties`

Úsala en todas las páginas que no sean ficha de producto, carrito, pedido o presupuesto (incluida la homepage y los pasos del embudo entre carrito y confirmación de pedido). Registra páginas vistas, visitas y fuentes de tráfico orgánico.

**Métodos:**

- `Builder(String path)` — nombre de la página **(obligatorio)**
- `setUID(String uid)` — ID interno del usuario conectado; consolida el historial multi-device y permite reconciliar un clic con una descarga
- `setEmail(String email)` — email del usuario
- `setProfile(String profile)` — perfil del usuario (ej. `visitor`, `buyer`, `looker`)
- `setPageGroup(String group)` — grupo de páginas para el reporting
- `set(String key, String value)` — parámetro personalizado libre
- `setCFlag(SiteCentricCFlag flag)` — adjunta uno o varios context flags a la etiqueta (ver [Context Flag](#context-flag-cflag))

**Ejemplo:**

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

**Métodos:**

- `Builder(String path)` — nombre de la página **(obligatorio)**
- `setUID(String uid)` — ID interno del usuario conectado
- `setEmail(String email)` — email del usuario
- `setProfile(String profile)` — perfil del usuario
- `setPageGroup(String group)` — grupo de páginas para el reporting
- `addProduct(Product product)` — añade la referencia de producto **(obligatorio)**
- `set(String key, String value)` — parámetro personalizado libre

**Construcción de un `Product`:**

- `Product.Builder(String ref)` — referencia del producto **(obligatorio)**
- `setName(String name)` — nombre legible del producto para el reporting
- `setGroup(String group)` — grupo de margen (`A` o `B`)
- `setParams(Params params)` — categorías del producto (ej. `prdparam-category`, `prdparam-brand`)

**Construcción de `Params`:**

- `addParam(String key, String value)` — añade una categoría de producto; la clave es el sufijo tras `prdparam-`

**Ejemplo:**

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

**Métodos:**

- `Builder(String path)` — nombre de la página **(obligatorio)**
- `setUID(String uid)` — ID interno del usuario conectado
- `setEmail(String email)` — email del usuario
- `setPageGroup(String group)` — grupo de páginas para el reporting
- `addProduct(Product product)` — añade una referencia de producto; repite para cada producto mostrado
- `set(String key, String value)` — parámetro personalizado libre

**Ejemplo:**

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

**Métodos:**

- `Builder(String path, String engineName)` — nombre de página + nombre del motor de búsqueda **(obligatorio)**
- `setResults(int results)` — número de resultados devueltos por la búsqueda
- `setParams(Params params)` — parámetros de búsqueda adicionales (pares clave/valor que corresponden a `isearchkey`/`isearchdata`)
- `setUID(String uid)` — ID interno del usuario conectado
- `set(String key, String value)` — parámetro personalizado libre

**Ejemplo:**

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

**Métodos:**

- `Builder(String path)` — nombre de la página **(obligatorio)**
- `set("error", "1")` — marca la página como error 404 **(obligatorio)**

**Ejemplo:**

```java
EAProperties errorTag = new EAProperties.Builder("erreur|404")
        .set("error", "1")
        .build();
EAnalytics.getInstance().track(errorTag);
```

---

### Página de presupuesto — `EAEstimate`

Registra presupuestos y los productos asociados. Una etiqueta se interpreta como página de presupuesto cuando contiene `ref` y `estimate` — ambos definidos automáticamente por el constructor. Los presupuestos se deduplicen por referencia: una segunda llamada con la misma referencia se ignora.

**Métodos:**

- `Builder(String path, String ref)` — nombre de página + referencia única del presupuesto **(obligatorio)**
- `setAmount(double amount)` — importe total IVA incluido (separador decimal: punto)
- `setType(String type)` — tipo de presupuesto según tu propio referencial
- `setCurrency(String currency)` — divisa si es diferente a la configurada en la interfaz
- `addProduct(Product product, double amount, int quantity)` — añade un producto con su precio unitario y cantidad
- `setUID(String uid)` — ID interno del usuario conectado
- `setEmail(String email)` — email del usuario
- `set(String key, String value)` — parámetro personalizado libre

**Ejemplo:**

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

Registra carritos iniciados y permite calcular las tasas de conversión y abandono. Una etiqueta se interpreta como página de carrito cuando contiene `scart` — definido automáticamente por el constructor. La duración de vida de un carrito es de 30 minutos (sesión deslizante).

**Métodos:**

- `Builder(String path)` — nombre de la página **(obligatorio)**
- `setCartCumul(boolean cumul)` — `false`: los productos del tag representan el contenido completo del carrito (snapshot); `true`: los productos se acumulan en cada llamada sucesiva
- `addProduct(Product product, double amount, int quantity)` — añade un producto con su precio unitario y cantidad
- `setUID(String uid)` — ID interno del usuario conectado
- `setEmail(String email)` — email del usuario
- `setProfile(String profile)` — perfil del usuario
- `setPageGroup(String group)` — grupo de páginas para el reporting
- `set(String key, String value)` — parámetro personalizado libre

**Ejemplo:**

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
        .setCartCumul(false)
        .addProduct(product1, 50.30, 2)
        .build();
EAnalytics.getInstance().track(cartPage);
```

---

### Página de pedido — `EAOrder`

Registra conversiones y ROI. Una etiqueta se interpreta como página de pedido cuando contiene `ref` y `amount` pero ni `scart` ni `estimate`. Los pedidos se deduplicen por referencia. Implementa esta etiqueta lo antes posible en el flujo de pago para no perder la llamada si el usuario no regresa al sitio tras la plataforma de pago.

**Métodos:**

- `Builder(String path, String ref)` — nombre de página + referencia única del pedido **(obligatorio)**
- `setAmount(double amount)` — importe total IVA incluido sin gastos de envío **(obligatorio)**
- `setPayment(String payment)` — método de pago utilizado (ej. `credit card`, `paypal`)
- `setType(String type)` — tipo de venta según tu propio referencial
- `setCurrency(String currency)` — divisa si es diferente a la configurada en la interfaz
- `setNewCustomer(boolean newCustomer)` — `true`: nuevo cliente; `false`: cliente recurrente
- `setEstimateRef(String ref)` — referencia del presupuesto vinculado a este pedido (opcional)
- `addProduct(Product product, double amount, int quantity)` — añade un producto con su precio unitario y cantidad
- `setUID(String uid)` — ID interno del usuario conectado
- `setEmail(String email)` — email del usuario
- `setProfile(String profile)` — perfil del usuario
- `set(String key, String value)` — parámetro personalizado libre

**Ejemplo:**

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
        .setEstimateRef("asd123qwe")
        .set("custom-param-key", "custom-param-value")
        .addProduct(product1, 25.15, 2)
        .build();
EAnalytics.getInstance().track(orderPage);
```

---

### Tracking de merchandising — `EATpView` / `EATpClick`

Registra impresiones y clics en bloques de merchandising (listas de recomendación, banners, etc.). Se envían como peticiones **GET** a `/tpview/` y `/tpclick/` — a diferencia del resto de etiquetas que se envían por POST. Soporta el mismo mecanismo de reintento offline que el resto del SDK.

**Métodos `EATpView` (impresión):**

- `Builder(String path)` — página de referencia **(obligatorio)**
- `setSiteName(String name)` — nombre del sitio de comercio
- `setCampaign(String campaign)` — nombre de la campaña
- `setPlacement(String placement)` — ubicación del bloque en la página
- `addProduct(String ref, Integer position)` — referencia de producto y su posición en el bloque; repite para cada producto mostrado
- `setUrl(String url)` — URL de contexto
- `setPublisher(String publisher)` — editor (opcional)
- `setMedia(String media)` — tipo de medio (opcional)
- `setCategory(String category)` — categoría del bloque (opcional)

**Métodos `EATpClick` (clic):**

- `Builder(String path)` — página de referencia **(obligatorio)**
- `setSiteName(String name)` — nombre del sitio de comercio
- `setCampaign(String campaign)` — nombre de la campaña
- `setPlacement(String placement)` — ubicación del bloque en la página
- `setProduct(String ref, int position)` — referencia y posición del producto clicado **(obligatorio)**
- `setProduct(String ref, int position, Integer totalProducts)` — variante con el número total de productos en el bloque
- `setUrl(String url)` — URL de contexto
- `setPublisher(String publisher)` — editor (opcional)
- `setMedia(String media)` — tipo de medio (opcional)
- `setCategory(String category)` — categoría del bloque (opcional)

**Ejemplo:**

```java
// Impresión en un bloque de merchandising
EATpView view = new EATpView.Builder("homepage")
        .setSiteName("my-site")
        .setCampaign("summer_sale")
        .setPlacement("banner_top")
        .addProduct("PROD_001", 0)
        .addProduct("PROD_002", 1)
        .setUrl("http://eulerian.net")
        .build();
EAnalytics.getInstance().track(view);

// Clic en un producto del bloque
EATpClick click = new EATpClick.Builder("homepage")
        .setSiteName("my-site")
        .setCampaign("summer_sale")
        .setPlacement("banner_top")
        .setProduct("PROD_001", 2)
        .setUrl("http://eulerian.net")
        .build();
EAnalytics.getInstance().track(click);
```

---

### Context Flag (CFLAG) — `SiteCentricCFlag`

Adjunta uno o varios context flags a cualquier etiqueta para enriquecer tus informes con dimensiones contextuales. Cada flag acepta hasta 10 valores.

**Métodos `SiteCentricCFlag`:**

- `Builder()` — constructor
- `set(String key, String... values)` — define una dimensión contextual con uno o varios valores (máximo 10)

**Se adjunta a una etiqueta mediante:**

- `setCFlag(SiteCentricCFlag flag)` — disponible en todos los tipos de etiqueta

**Ejemplo:**

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
