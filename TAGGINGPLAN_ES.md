# Plan de Etiquetado del SDK de Android

## Seguimiento de una aplicación WebView

Para la navegación dentro de una aplicación *WebView* de Android (sitio web HTML/JS estándar), se debe proporcionar nuestro identificador interno en la URL de inicio para asegurar la continuidad del seguimiento.
Para recuperar este valor, la técnica del lado del cliente debe llamar a la siguiente función definida en nuestro SDK:

*Android* :

```xml
String euidl = EAnalytics.getEUIDL();
```

Una vez que este valor se suministra en el parámetro **ea-euidl-bypass**, el sistema habitual de cookies se desactiva y se utiliza el EUIDL proveniente de la aplicación.

**Ejemplo:**

```xml
http://www.demo.fr/android-landing-webview?ea-euidl-bypass=$euidl_de_l_app
```

Si **ea.js** detecta este parámetro, lo utiliza y lo almacena internamente para toda la sesión, por lo que el sitio no tiene que reenviarlo en cada página visitada.
Después de pasar este parámetro, el seguimiento de una aplicación Android en un contexto WebView no difiere del formato JavaScript utilizado para un sitio web estándar.

**IMPORTANTE**: El parámetro `"ea-euidl-bypass"` debe proporcionarse **siempre** (cada vez que cambie la URL); de lo contrario, se perderá el vínculo entre la navegación de la app y del sitio web.

**IMPORTANTE**: En una aplicación **WebView**, se debe agregar el parámetro **`edev`** al abrir el WebView. Este parámetro **califica el tráfico como proveniente de una aplicación nativa**.

Es esencial para garantizar un seguimiento adecuado de **campañas in-app**, **notificaciones push** y una correcta **atribución de llamadas WebView**.

Valores aceptados para el parámetro `edev`:

* `AppNativeIOSphone`
* `AppNativeIOStablet`
* `AppNativeAndroidphone`
* `AppNativeAndroidtablet`

## Seguimiento de una aplicación nativa

En una aplicación nativa, nuestro SDK debe integrarse en el código fuente de la app para incluir el siguiente tipo de marcador:

```xml
EAProperties properties = new EAProperties.Builder("PAGE_NAME").build();
Eanalytics.getInstance().track(properties);
```

Nuestro **SDK** está diseñado para simplificar al máximo la integración, ofreciendo una estructura de objetos fácil de usar y una documentación detallada.

Los parámetros disponibles y funcionalidades son exactamente los mismos que para nuestro Tag Collector. Por lo tanto, puedes seguir los caminos de navegación en tu aplicación y compararlos con los de tu sitio web clásico integrando las mismas variables.

Las llamadas generadas por estos marcadores son también muy similares a las de Collector clásico y están diseñadas para ser lo más ligeras posible, sin interferir con la aplicación.

Algunos parámetros del Collector son específicos para el seguimiento de apps móviles. Para más información, consulta [esta documentación](https://eulerian.wiki/doku.php?id=fr:modules:collect:onsite_collection:taggingplan_web&s[]=webview#tab__mobiles).

Nuestro SDK también puede recolectar un número ilimitado de interacciones offline y enviarlas una vez que el usuario se reconecte. Esto permite continuar el seguimiento incluso cuando interactúa sin conexión. La navegación se registra y posteriormente se reasigna usando nuestro parámetro `ereplay-time`.

## Regla de atribución del tráfico

En un sitio web estándar, nuestro sistema identifica y atribuye el tráfico en función de la **URL** en la que se llama el marcador. Sin embargo, no tenemos acceso a esta información en un contexto de aplicación.

Para definir la regla de atribución, nos basamos en el subdominio de seguimiento usado y pasado como parámetro al método **init** del objeto `EAnalytics` definido en nuestro SDK:

```xml
public class YourApp extends Application {
     @Override
    public void onCreate() {
        super.onCreate();
        EAnalytics.init(this, "TRACKING_DOMAIN", true);
    }
 }
```

<note important>
Esto también significa que si usas el mismo subdominio de seguimiento para varias de tus aplicaciones, necesitaremos distinguirlas agregando el parámetro [from](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list) en los marcadores. Esto es automático en una app híbrida que comparte páginas con tu sitio web.
</note>

También ten en cuenta que la ausencia de una URL implica la ausencia de nombre de página por defecto. El parámetro [path](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list) no es opcional, sino **obligatorio** en el seguimiento de aplicaciones.

El tráfico generado por tu app puede ser reportado en un sitio dedicado Eulerian o fusionado con el de un sitio web existente. Como se mencionó, si tu app cambia a modo WebView para ciertas páginas, agregar el parámetro [from](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list) es obligatorio para redirigir correctamente el tráfico en nuestros sistemas.

## Métricas de descarga y actualización

Descripción de los parámetros:

* **ea-appname** : `APPLICATION_NAME` – el nombre de la aplicación (no debe cambiar)
* **ea-appversion** : `APPLICATION_VERSION` – la versión de la aplicación
* **ea-appinstalled** : `1` – indica que **no** se debe contar como una nueva instalación para esta llamada.
  Se usa para manejar la inicialización de usuarios que ya tenían la aplicación antes de la integración con Eulerian.
  Este parámetro puede suministrarse permanentemente junto con `appversion` y `appname`, siempre que la aplicación considere que ya se ha iniciado previamente en este dispositivo/usuario.

La presencia del parámetro **ea-appname** activa el procesamiento en el sistema.

El sistema alimenta la métrica **Download** si:

* El usuario nunca ha estado expuesto al valor de **ea-appname** cuando se abre la aplicación **y** el parámetro **ea-appinstalled** no está presente en la llamada.

El sistema alimenta la métrica **Update** si:

* El usuario ya ha estado expuesto al parámetro **ea-appname** y su valor es idéntico al visto en el último inicio de la aplicación, **pero** el valor de **ea-appversion** es diferente.

## Integración vía JitPack

Para el seguimiento de aplicaciones móviles, **JitPack** simplifica el proceso de integración del SDK.

### Instalación

Agrega JitPack en el archivo de construcción:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Inserta el SDK de Android en [Gradle](http://gradle.org/|Gradle):

```xml
dependencies {
   compile 'com.eulerian.android.sdk:eanalytics:1.9.0'
}
```

En el archivo **AndroidManifest.xml**, agrega los siguientes permisos:

```javascript
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

En el mismo archivo, añade el siguiente código para el seguimiento de descargas:

```javascript
<receiver
   android:name="com.eulerian.android.sdk.InstallReferrerReceiver"
   android:exported="true">
   <intent-filter>
      <action android:name="com.android.vending.INSTALL_REFERRER" />
   </intent-filter>
</receiver>
```

## Iniciando Eulerian Analytics

Antes de utilizar el SDK, asegúrate de inicializarlo con la función **onCreate()** que se muestra a continuación.

```javascript
<!-- en tu AndroidManifest.xml -->
<application
   android:name=".DemoApp">
```

```javascript
public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EAnalytics.init(this, "example.demo.com", false);
    }

}
```

Ahora puedes integrar cualquier tipo de etiquetas con sus parámetros. Por ejemplo, la etiqueta genérica se identifica mediante **EAProperties**. Otros tipos de etiquetas tienen un identificador específico (por ejemplo, **EACart** para el carrito, **EAEstimate** para la cotización, **EAProducts** para la categoría, etc.).

```javascript
EASearch search = new EASearch.Builder("/path-example", "banana")
                .setParams(new Params.Builder()
                        .addParam("provenance", "martinique")
                        .addParam("couleur", "jaune")
                        .build())
                .setResults(42)
                .build();

EAnalytics.getInstance().track(search);
```

### Lista de Páginas

* Página Genérica
* Página de Producto
* Página de Categoría
* Página de Motor de Búsqueda
* Página de Error
* Página de Cotización
* Página de Carrito
* Página de Pedido

## Página Genérica

El marcador siguiente es genérico y debe implementarse en todas las páginas del sitio excepto en las páginas de producto, pedido, cotización y carrito iniciado. Esto incluye especialmente la página de inicio y las páginas del embudo de conversión entre el carrito y la confirmación del pedido.

Permite rastrear todo el tráfico centrado en el sitio, vistas de página y visitas, así como fuentes de origen mediante palancas naturales como el acceso directo, búsqueda orgánica o referidos.

Luego gestionamos todas las acciones necesarias de forma remota. Por lo tanto, no necesitas cambiar el código de tu página para beneficiarte de funciones adicionales.

Puedes agregar los siguientes parámetros en el formato genérico o a cualquiera de las etiquetas de recopilación descritas en esta documentación para enriquecer tus informes.

### Lista de Parámetros

* **path:** (NOMBRE\_PÁGINA) Este parámetro permite nombrar la página para su identificación en los informes. Para más detalles, consulta [este artículo](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
* **uid:** (UID) Este parámetro debe completarse con tu ID interno cuando el usuario haya iniciado sesión, para consolidar el historial entre diferentes dispositivos. Esto permite reconciliar un clic con una descarga de app. Más información en [este artículo](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).

### Implementación

El objeto **EAProperties** es la clase padre que contiene todos los parámetros internos o editables de nuestro colector de etiquetas.

Para agregar un nuevo parámetro, usa uno de los métodos nativos **setKEY(VALOR)** o el método genérico **set("KEY", "VALOR")**.

**Ejemplo:**

```xml
EAProperties genericTag = new EAProperties.Builder(path: String)
                        .setPageGroup(String)
                        .setUID(String)
                        .setEmail(String)
                        .setProfile(String)
                        .set(String - KEY, String - VALUE)
                        .build();
EAnalytics.getInstance().track(genericTag);
```

**Con valores:**

```xml
EAProperties genericTag = new EAProperties.Builder("PAGE_NAME")
                        .setPageGroup("MY-PAGEGROUP")
                        .setUID("123asd")
                        .setEmail("test@test.fr")
                        .setProfile("visitor")
                        .set("KEY-CUSTOM-PARAMETER","VALUE")
                        .build();
EAnalytics.getInstance().track(genericTag);
```

## Página de Producto

Este marcador permite el seguimiento de vistas y visitas de páginas de producto de tu catálogo. Alimenta los informes disponibles en:

* Site-centric > Productos > **Adquisición y Rendimiento del Producto**

También puedes pasar una o más categorías de producto.

**Nota:** Se considera una página de producto cualquier marcador que incluya una única referencia de producto, sin los parámetros **scart**, **estimate**, **ref** y **amount**. Si una etiqueta de recopilación incluye alguno de estos parámetros o múltiples referencias de producto, no se interpretará como una página de producto.

### Lista de Parámetros

* **path:** (NOMBRE\_PÁGINA) Requerido para la identificación en informes, debe configurarse manualmente. Más info [aquí](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
* **uid:** (UID) ID interno para usuarios logueados, para consolidar historial entre dispositivos. Más info [aquí](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
* **prdref:** (ID\_PRODUCTO) Debe configurarse con la referencia del producto visualizado por el usuario. Más info [aquí](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
* **prdname:** (NOMBRE\_PRODUCTO) Nombre legible del producto para facilitar informes. Más info [aquí](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
* **prdgroup:** (GRUPO) Asocia un grupo al producto (por ejemplo, grupo de margen "A" o "B"). No expone valores exactos de margen. Más info [aquí](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
* **prdparam-xxxxx:** (NOMBRE\_PARÁMETRO, VALOR\_PARÁMETRO) Asigna categorías a referencias de producto. Especifica la categoría tras el prefijo **prdparam-**. Ejemplos: prdparam-universo, prdparam-talla. Puedes agregar cuantos necesites. Más info [aquí](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).

### Implementación

El objeto **EAProducts** se utiliza para el seguimiento de páginas de producto.

Un producto debe crearse con un objeto **Product** utilizando la referencia del producto. Puede complementarse con un objeto **Params** para asignar atributos adicionales.

**Ejemplo:**

```xml
Params paramProduct = new Params.Builder()
.addParam(String - key, String - Value)
.build();

Product product1 = new Product.Builder(String - prdref)
.setName(String - prdname)
.setGroup(String - prdgroup)
.setParams(paramProduct)
.build();

EAProducts productPage = new EAProducts.Builder("PAGE_NAME")
.setPageGroup(String)
.setUID(String)
.setEmail(String)
.setProfile(String)             
.addProduct(product1)
.build();

EAnalytics.getInstance().track(productPage);
```

**Con valores:**

```xml
Params paramProduct = new Params.Builder()
.addParam("category", "clothes")
.addParam("brand", "nike")
.build();

Product product1 = new Product.Builder("ref-product")
.setName("name-product")
.setGroup("group-product")
.setParams(paramProduct)
.build();

EAProducts productPage = new EAProducts.Builder("PAGE_NAME")
.setPageGroup("my-pagegroup")
.setUID("123asd")
.setEmail("test@test.fr")
.setProfile("looker")             
.addProduct(product1)
.build();

EAnalytics.getInstance().track(productPage);
```

## Página de Categoría

Este marcador envía las 3 principales referencias de producto listadas en la página de resultados a tus socios para optimizar el retargeting. Su uso está mayormente restringido a Eulerian TMS y no impacta el informe de Adquisición y Rendimiento del Producto en Eulerian DDP.

**Nota:** Una página de resultados es cualquier marcador que contenga múltiples referencias de producto sin los parámetros **scart**, **estimate**, **ref** y **amount**. Si solo hay una referencia de producto (**prdref**), el marcador se tratará como una página de producto.

### Lista de Parámetros

* **path:** (NOMBRE\_PÁGINA) Requerido para la identificación en informes, debe configurarse manualmente. Más info [aquí](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
* **uid:** (UID) ID interno para usuarios logueados, para consolidar historial entre dispositivos. Más info [aquí](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
* **prdref:** (ID\_PRODUCTO) Debe configurarse con la referencia del producto visualizado por el usuario. Más info [aquí](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).

### Implementación

Utiliza el objeto **EAProducts** y pasa los primeros X productos mostrados en la página.

Cada producto se crea mediante un objeto **Product**. Una vez inicializado y opcionalmente completado con atributos, añádelo a **EAProducts** mediante el método **addProduct**.

**Ejemplo:**

```xml
Product product1 = new Product.Builder(String - product ref).build();
Product product2 = new Product.Builder(String - product ref).build();
Product product3 = new Product.Builder(String - product ref).build();

EAProducts resultPage = new EAProducts.Builder(path: String)
.setPageGroup(String!)
.setUID(String!)
.setEmail(String!)             
.addProduct(Product product1)
.addProduct(Product product2)
.addProduct(Product product3)
.build();

EAnalytics.getInstance().track(resultPage);
```

**Con valores:**

```xml
Product product1 = new Product.Builder("ref-product1").build();
Product product2 = new Product.Builder("ref-product2").build();
Product product3 = new Product.Builder("ref-product3").build();

EAProducts resultPage = new EAProducts.Builder("page-name")
.setPageGroup("my-pagegroup")
.setUID("123asd")
.setEmail("test@test.fr")             
.addProduct(product1)
.addProduct(product2)
.addProduct(product3)
.build();

EAnalytics.getInstance().track(resultPage);
```
## Página del Motor de Búsqueda

El marcador del motor de búsqueda interno permite capturar las consultas introducidas por los usuarios junto con un número ilimitado de parámetros adicionales. Toda esta información se cruza con las ventas generadas y las palancas de adquisición activadas durante la sesión.

**Nota:** Una página se considera de motor de búsqueda si contiene el parámetro **isearchengine**.

Ten en cuenta que este marcador no es exclusivo. Si incluyes una referencia de producto con el parámetro **prdref**, también se tratará como una página de producto.

### Lista de Parámetros

* **path:** (NOMBRE\_PÁGINA) Permite identificar la página en los informes. Debe establecerse manualmente. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **uid:** (UID) ID interno cuando el usuario ha iniciado sesión para el seguimiento entre dispositivos. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **isearchengine:** (NOMBRE\_MOTOR) Identifica el nombre del motor interno para distinguir entre múltiples campos de búsqueda. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **isearchresults:** (CANTIDAD\_RESULTADOS) Número de resultados devueltos por la búsqueda. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **isearchkey:** (CLAVE\_PARÁMETRO\_BÚSQUEDA) Clave para un campo de búsqueda adicional. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **isearchdata:** (VALOR\_PARÁMETRO\_BÚSQUEDA) Valor correspondiente a isearchkey. Para cada campo adicional, añade un nuevo par isearchkey/isearchdata. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)

### Implementación

Usa la clase **EASearch** para el seguimiento del motor de búsqueda interno. Crea un objeto **Params** y utiliza **addParam** para cada par clave-valor.

**Ejemplo:**

```xml
Params param1 = new Params()
.addParam("SEARCH_PARAM_KEY_1","SEARCH_PARAM_VALUE_1")
.addParam("SEARCH_PARAM_KEY_2","SEARCH_PARAM_VALUE_2")
.addParam("SEARCH_PARAM_KEY_3","SEARCH_PARAM_VALUE_3")
.build();

EASearch searchPage = new EASearch.Builder("PAGE_NAME", "ENGINE_NAME")
.setUID("UID")
.setResults(RESULT_COUNT)
.setParams(param1)
.build();

EAnalytics.getInstance().track(searchPage);
```

**Con valores:**

```xml
Params param1 = new Params()
.addParam("keyword","jacket")
.addParam("min_price","100.00")
.addParam("max_price","400.00")
.build();

EASearch searchPage = new EASearch.Builder("InternalSearch|jacket", "internal_engine")
.setUID("34678")
.setResults(150)
.setParams(Params)
.build();

EAnalytics.getInstance().track(searchPage);
```

---

## Página de Error 404

El marcador **error** indica que la aplicación del sitio ha generado una página de error 404. Alimenta informes como:

* Site-centric > Análisis de Audiencia > Por Página > **Página de Error**

Esta etiqueta captura todas las URLs que provocaron un 404 y ayuda a redirigir el tráfico adecuadamente.

Úsala para obtener alertas sobre problemas recurrentes en tu embudo de conversión.

### Lista de Parámetros

* **error:** Siempre configurado en **1** y puede añadirse a cualquier tipo de marcador. Registra una vista de página en el informe correspondiente. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **path:** (NOMBRE\_PÁGINA) Requerido para la identificación en los informes. Debe establecerse manualmente. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)

### Implementación

Usa el método **set** con la clave **error** para marcar la página como de error.

**Ejemplo:**

```xml
EAProperties errorTag = new EAProperties.Builder("error|404")
.set("error","1")
.build();
Eanalytics.getInstance().track(errorTag);
```

---

## Página de Cotización

Este marcador realiza el seguimiento de cotizaciones y toda la información relacionada:

* Productos involucrados
* Tipo de cotización
* Monto total
* Datos contextuales

Cada nueva cotización incluye palancas de adquisición e historial de navegación.

**Nota:** Las cotizaciones se desduplican usando la referencia proporcionada. Usar la misma referencia para distintas cotizaciones hará que se ignore la segunda instancia.

**Nota:** Una página se considera de cotización si contiene ambos parámetros **ref** y **estimate**.

### Lista de Parámetros

* **path:** (NOMBRE\_PÁGINA) Requerido para la identificación en informes. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **uid:** (UID) ID interno para usuarios registrados. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **ref:** (REFERENCIA\_COTIZACIÓN) Referencia única de la cotización. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **amount:** (MONTO\_COTIZACIÓN) Monto total de la cotización en decimal. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **type:** (TIPO\_COTIZACIÓN) Permite categorizar según tu taxonomía. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **prdref:** (ID\_PRODUCTO) Referencia del producto en la cotización. Repetir por cada producto. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **prdamount:** (MONTO\_PRODUCTO) Precio unitario por producto en la cotización. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **prdquantity:** (CANTIDAD\_PRODUCTO) Cantidad por producto en la cotización. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)

### Implementación

Utiliza el objeto **EAEstimate** para el seguimiento de la página de cotización. Añade cada producto mediante **Product** e inclúyelo con **addProduct**.

**Ejemplo:**

```xml
Product product1 = new Product.Builder(String - id-product).build();
Product product2 = new Product.Builder(String - id-product).build();

EAEstimate estimatePage = new EAEstimate.Builder(String - page-name, String - id-estimate)
.setPageGroup(String!)
.setUID(String!)
.setEmail(String!) 
.setType(String!)
.setAmount(Double!)
.set(String key, String value)
.addProduct(Product product, Double amount, Int quantity)
.addProduct(Product product, Double amount, Int quantity)
.build();

EAnalytics.getInstance().track(estimatePage);
```

**Con valores:**

```xml
Product product1 = new Product.Builder("505").build();

EAEstimate estimatePage = new EAEstimate.Builder("Credit|quote", "C4536567")
.setPageGroup("my-pagegroup")
.setUID("123asd")
.setEmail("test@test.fr") 
.setAmount(5000.00)
.setType("Credit_48months")
.set("custom-param-KEY", "custom-param-value")
.addProduct(product1, 5000.00, 1)
.build();

EAnalytics.getInstance().track(estimatePage);
```

## Página de Carrito

Este marcador rastrea carritos iniciados y ayuda a calcular las tasas de conversión y abandono.

También puedes incluir productos y sus categorías en el carrito, junto con montos y cantidades. Estos parámetros alimentan los informes de producto:

* Site-centric > Productos > **Adquisición y Rendimiento del Producto**

La duración del carrito es de 30 minutos continuos, es decir, la sesión del usuario.

**Nota:** Una página se considera de carrito si incluye el parámetro **scart**.

### Lista de Parámetros

* **path:** (NOMBRE\_PÁGINA) Para la identificación de página en informes. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **uid:** (UID) ID interno para usuarios registrados. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **scartcumul:** (0\_O\_1) Si es 0, los productos pasados representan el carrito completo. Si es 1, se agregan de forma acumulativa. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **prdref:** (ID\_PRODUCTO) Referencia del producto, repetir por cada producto. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **prdamount:** (MONTO\_PRODUCTO) Precio unitario por producto en el carrito. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **prdquantity:** (CANTIDAD\_PRODUCTO) Cantidad por producto en el carrito. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)

### Implementación

Usa **EACart** para rastrear la página de carrito. Cada producto se crea mediante **Product** y se añade usando **addProduct**.

**Ejemplo:**

```xml
Params paramProduct = new Params.Builder()
.addParam(String Key, String value)
.addParam(StringKey, String value)
.build();

Product product1 = new Product.Builder(String ref-product)
.setName(String!)
.setParams(paramProduct)
.build();

EACart cartPage = new EACart.Builder(String path)
.setPageGroup(String!)
.setUID(String!)
.setEmail(String!) 
.setProfile(String!)
.setCartCumul(Bool!) 
.addProduct(product1, Double amount, Int quantity)
.build();

EAnalytics.getInstance().track(cartPage);
```

**Con valores:**

```xml
Params paramProduct = new Params.Builder()
.addParam("category", "T-Shirt")
.addParam("brand", "Nike")
.build();

Product product1 = new Product.Builder("product-123")
.setName("product-name")
.setParams(paramProduct)
.build();

EACart cartPage = new EACart.Builder("page-name")
.setPageGroup("my-page-group")
.setUID("123asd")
.setEmail("test@test.fr") 
.setProfile("shopper")
.setCartCumul(true) 
.addProduct(product1, 50.30, 2)
.build();

EAnalytics.getInstance().track(cartPage);
```

## Página de Pedido

Este marcador registra conversiones y permite el cálculo del ROI para todos tus canales de marketing. Además del monto total del pedido, puedes incluir productos asociados, método de pago, moneda o tipo de venta. Estos detalles enriquecen las capacidades de informes en toda la suite de Eulerian. Cada conversión se detalla con sus palancas de adquisición e historial de navegación.

**Nota:** Los pedidos se desduplican según la referencia proporcionada en el marcador de venta.

Si se usa la misma referencia para dos pedidos distintos, el segundo se ignora por el principio de unicidad.

Eulerian Analytics permite la cancelación de pedidos para medir con mayor precisión el rendimiento de las campañas a lo largo del tiempo. Se recomienda activar el marcador de pedido lo antes posible en el proceso de compra, siempre que se disponga de los detalles requeridos (referencia, monto, tipo de pago, etc.).

Si la plataforma de pago no redirige al usuario de nuevo a tu sitio, es posible que una etiqueta colocada después no se active.

**Nota:** Aparte del estado del pedido (pendiente, válido o inválido), no es posible redefinir el contenido del pedido una vez registrado. No puedes añadir ni eliminar productos, pero sí cambiar el monto total.

**Nota:** Una página se considera de confirmación de pedido si contiene **ref** y **amount**, pero excluye **scart** y **estimate**.

### Lista de Parámetros

* **path:** (NOMBRE\_PÁGINA) Para identificación en informes. Debe establecerse manualmente. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **uid:** (UID) ID interno cuando el usuario ha iniciado sesión. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **ref:** (REFERENCIA\_PEDIDO) Referencia única del pedido. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **amount:** (MONTO\_PEDIDO) Monto total del pedido (incluye impuestos, excluye envío). Usar punto para decimales. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **payment:** (MÉTODO\_PAGO) Indica el tipo de pago usado. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **type:** (TIPO\_PEDIDO) Permite categorizar la venta usando tu taxonomía. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **currency:** (MONEDA) Convierte el monto si difiere del valor predeterminado configurado. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **profile:** (PERFIL) Puede usarse para diferenciar entre clientes nuevos y recurrentes. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **prdref:** (ID\_PRODUCTO) Referencias de productos pedidos. Repetir por cada uno. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **prdamount:** (MONTO\_PRODUCTO) Precio unitario de cada producto pedido. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)
* **prdquantity:** (CANTIDAD\_PRODUCTO) Cantidad por producto pedido. [Más info](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list)

### Implementación

Usa la clase **EAOrder** para el seguimiento de pedidos. Cada producto debe crearse mediante un objeto **Product** y añadirse usando **addProduct**.

**Ejemplo:**

```xml
Params paramProduct = new Params.Builder()
.addParam(String Key, String value)
.addParam(StringKey, String value)
.build();

Product product1 = new Product.Builder(String ref-product)
.setName(String!)
.setParams(paramProduct)
.build();

EAOrder orderPage = new EAOrder.Builder(String path, String id-sale)
.setPageGroup(String!)
.setUID(String!)
.setEmail(String!) 
.setProfile(String!)
.setNewCustomer(Bool!)
.setAmount(Double!)
.setType(String!)
.setPayment(String!)
.setCurrency(String!)
.setEstimateRef(String!)
.set("custom-param-key", "custom-param-value")
.addProduct(product1, Double amount, Int quantity)
.build();

EAnalytics.getInstance().track(orderPage);
```

**Con valores:**

```xml
Params paramProduct = new Params.Builder()
.addParam("category", "T-Shirt")
.addParam("brand", "Nike")
.build();

Product product1 = new Product.Builder("product-123")
.setName("product-name")
.setParams(paramProduct)
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


## Indicador de Contexto (CFLAG)

El objeto **SiteCentricCFlag** está dedicado a la señalización de contexto.

Puedes crear uno o más indicadores de contexto usando el objeto **SiteCentricCFlag** y añadirlos con la función `.setCFlag()` dentro de cualquier etiqueta de Eulerian. La función `.set()` toma como argumentos `"clave string", "uno o más valores string"`, con un máximo de 10 valores.

**Ejemplo:**

```xml
import com.eulerian.android.sdk.SiteCentricCFlag;

// Ejemplo para una etiqueta genérica
EAProperties genericTag = new EAProperties.Builder(path) //path
                        .setUID(UID) // UID
                        .setEmail("email")
                        .setProfile("profile")
                        .set("CUSTOM_PARAM_NAME","CUSTOM_PARAM_VALUE")
                        .set("CUSTOM_PARAM_NAME","CUSTOM_PARAM_VALUE")
                        .setCFlag(new SiteCentricCFlag.Builder()
                                .set("category_1", "rolandgarros", "wimbledon")
                                .set("category_2", "tennis")
                                .set("category_3", "usopen")
                                .build())
                        .build();

EAnalytics.getInstance().track(genericTag);
```

---

## Consentimiento en una Aplicación Android (GDPR / RGPD)

> **Importante: Existen dos enfoques EXCLUYENTES.**
> Elige **uno u otro**, nunca ambos:
>
> 1. **Modo TCF v2 – TCString**: transmitir la cadena de consentimiento vía `gdpr_consent`.
> 2. **Modo por Categorías – `pmcat`**: transmitir los IDs de las categorías rechazadas.

---

### 1. **Modo TCF v2** — parámetro `gdpr_consent`

Envía la **TCString** generada por tu CMP exactamente cuando el usuario acepta o rechaza. Envíala **una vez por visitante**.

**Ejemplo en Android:**

```xml
// Android – sin valores
EAProperties genericTag = new EAProperties.Builder("PAGE_NAME")
.setUID("UID")
.set("CUSTOM_PARAM_NAME","CUSTOM_PARAM_VALUE")
.set("gdpr_consent","TCSTRING")
.build();
EAnalytics.getInstance().track(genericTag);
```

```xml
// Android – con valores
EAProperties genericTag = new EAProperties.Builder("|universo|sección|página")
.setUID("5434742")
.set("subscription","monthly")
.set("gdpr_consent","EADURF214345")
.build();
EAnalytics.getInstance().track(genericTag);
```

---

### 2. **Modo por Categorías** — parámetro `pmcat`

Si no deseas manejar la TCString, utiliza el parámetro `pmcat` para listar los **IDs de Eulerian** de las categorías **rechazadas** por el usuario, separadas por guiones (`-`).

*Envía `-` si el usuario acepta todas las categorías.*

**Ejemplo de correspondencia:**

| Categoría   | ID Eulerian |
| ----------- | ----------- |
| analytics   | 1           |
| advertising | 10          |
| functional  | 19          |

Si el usuario rechaza "analytics" **y** "advertising", el valor será: `1-10`.

**Ejemplos en Android:**

```xml
// Android – sin valores
EAProperties genericTag = new EAProperties.Builder("PAGE_NAME")
.setUID("UID")
.set("CUSTOM_PARAM_NAME","CUSTOM_PARAM_VALUE")
.set("pmcat","CATEGORÍAS CMP RECHAZADAS")
.build();
EAnalytics.getInstance().track(genericTag);
```

```xml
// Android – con valores
EAProperties genericTag = new EAProperties.Builder("|universo|sección|página")
.setUID("5434742")
.set("subscription","monthly")
.set("pmcat","1-2")
.build();
EAnalytics.getInstance().track(genericTag);
```

---

## URL de Exclusión General (Opt-out)

Para ofrecer una opción de exclusión total, proporciona la siguiente URL:

```
<dominio_colección>/optout.html?url=tu_dominio
```

**Ejemplo:**

```
https://mj23.eulerian.com/optout.html?url=www.eulerian.com
```

Inclúyelo en tu página de "Privacidad / RGPD" o dentro de tu CMP.

---

## Resumen

| Paso              | Modo TCF v2         | Modo por Categorías        |
| ----------------- | ------------------- | -------------------------- |
| Parámetro clave   | `gdpr_consent`      | `pmcat`                    |
| ¿Cuándo enviarlo? | Al aceptar/rechazar | Al aceptar/rechazar        |
| Contenido         | TCString            | IDs rechazados (o `-`)     |
| Compatibilidad    | CMP TCF v2          | CMP personalizada / no TCF |

**Nunca uses ambos modos simultáneamente en la misma aplicación.**
