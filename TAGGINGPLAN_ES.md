# Plan de tagging SDK Android #

## Tracking de una aplicación webview

En lo que concierne a la navegación en una aplicación de tipo *webview* en Android (sitio web html/js standard), la URL de apertura debe proporcionar nuestro ID interno para asegurar la continuidad del seguimiento. 
Para recuperar este valor, el soporte técnico del cliente debe usar la función siguiente definida en nuestro SDK:  * Android :
```xml
String euidl = EAnalytics.getEUIDL();
```

Una vez se añade este valor al parámetro **ea-euidl-bypass**, bloquearemos el sistema de cookies normal para usar el euidl proveniente de la aplicación.

**Ejemplo:**
```xml
http://www.vpg.fr/ios-landing-webview?ea-euidl-bypass=$euidl_de_l_app
```

Si el ea.js detecta este parámetro, lo utilizará y lo guardará durante toda la sesión, para evitar que el sitio lo reenvíe con cada página visitada.
Una vez se pasa este parámetro, el tracking de una aplicación iOS en contexto webview no difiere de los formatos JavaScript que usamos para una web clásica.

La documentación disponible en [esta dirección](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:general) contiene toda la información necesaria para la implementación de nuestros Tags JavaScript.



## Tracking de una aplicación nativa

En el caso de una aplicación nativa, nuestra SDK debe incorporarse al código de la aplicación para poder integrar el tipo de marcador siguiente: 

```xml
EAProperties properties = new EAProperties.Builder("NOM_PAGE”).build();
Eanalytics.getInstance().track(properties);
```

Nuestra **SDK** se ha concebido para facilitar al máximo la integración ofreciendo una estructura simple y una documentación detallada. 
Los parámetros disponibles y las posibilidades son exactamente las mismas. Podrás hacer un seguimiento del rendimiento de tu aplicación como si fuese una web clásica integrando las mismas variables.

Las llamadas generadas por estos marcadores se acercan también a las del colector clásico, y han sido concebidas para ser lo más ligeras posible para no perturbar la aplicación.

Algunos parámetros de nuestro colector son específicos al tracking de una aplicación móvil. Para saber más acerca de esto puedes consultar  [esta documentación] (https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).

Nuestra SDK también puede recoger un número ilimitado de interacciones offline y enviarlas una vez el usuario se conecta. Este procedimiento nos permite hacer un seguimiento del usuario incluso si éste usa la aplicación sin conexión. La navegación queda registrada y se reatribuye posteriormente a través de nuestro parámetro ereplay-time.

## Règle d'affectation de trafic

### Regla de afectación de tráfico

En un sitio web clásico, nuestro sistema identifica y afecta el tráfico en función de la **URL** sobre la que se llama al marcador. Todavía no tenemos acceso a esta información en el contexto de una aplicación

Para definir la regla de atribución del tráfico, nos basamos en el subdominio de tracking utilizado y pasado en parámetro con el método **initialize** de nuestra SDK:

```xml
public class YourApp extends Application {
     @Override
    public void onCreate() {
        super.onCreate();
        EAnalytics.init(this, "DOMAINE_DE_TRACKING", true);
    }
 }
```

<note important>
Esto significa también que si usas el mismo subdominio de tracking para varias de tus aplicaciones, tendremos que distinguirlos añadiendo el parámetro [from] (https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list) en nuestros marcadores. Así será automáticamente para una aplicación hibrida que comparte páginas en común con tu sitio web.</note>

Igualmente cabe señalar que la ausencia de URL significa también la ausencia de un nombre de página por defecto. El parámetro [path](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list) no es opcional sino __obligatorio__ en el seguimiento de una aplicación.

El tráfico generado por tu aplicación se puede rastrear en un sitio de Eulerian dedicado o fusionarse con el de otro sitio web existente. Como se ha indicado anteriormente, si tu aplicación pasa a modo webview para algunas páginas es obligatorio añadir el parámetro [from](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list) para que podamos redirigir el tráfico correctamente a nuestros sistemas. 

## Configuración de descargas y actualizaciones

Descripción de parámetros asociados: 
  * **ea-appname**: NOM_APPLICATION, corresponde al nombre de la aplicación. Este último no debe cambiar 
  * **ea-appversion**: VERSION_APPLICATION, corresponde a la versión de la aplicación 
  * **ea-appinstalled**: 1, debe añadirse a todos los usuarios que hayan descargado la aplicación antes de la actualización de la URL de llamada que contiene los dos parámetros más arriba

La presencia del parámetro **ea-appname** inicia un tratamiento a nivel de sistema.

El sistema abastecerá el valor **descarga** si:
  * El usuario no ha estado jamás expuesto al valor del parámetro **ea-appname** al momento de apertura de la aplicación y el parámetro **ea-appinstalled** no está presente en la llamada

El sistema abastecerá el valor **Actualización** si:
  * El usuario ha estado expuesto al parámetro **ea-appname** y su valor se indica tras la última apertura de la aplicación. En cambio, el valor del parámetro **ea-appversion** es distinto. 

![download_upgrade.png](https://bitbucket.org/repo/kA6LdM/images/3930826066-download_upgrade.png)

## Integración a través de jCenter

Dentro del tracking de aplicaciones móviles, **jCenter** te permite simplificar el proceso de integración de la.

### Instalación

Inserta la SDK Android en la herramienta [Gradle](http://gradle.org/|Gradle).
```xml
dependencies {
   compile 'com.eulerian.android.sdk:eanalytics:1.6.0'
}
```

En el archivo **AndroidManifest.xml**, añade los permisos siguientes:

```javascript
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

## Iniciar Eulerian Analytics

Antes de usar SDK, asegúrate que lo has iniciado con la función **onCreate()** más abajo.

```javascript
<!-- in your AndroidManifest.xml-->
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

A partir de ahora es posible integrar todo tipo de tags acompañados de sus parámetros. Por ejemplo, el tag genérico se identifica gracias a EAProperties. Los otros tipos de tags tienen una identificación específica(ex : EACart para la cesta, EAEstimate para el presupuesto, EAProducts para la categoría, etc.).

```javascript
EASearch search = new EASearch.Builder("/path-example", "banana")
                .setParams(new Params.Builder()
                        .addParam("provenance", "martinique")
                        .addParam("couleur", "jaune")
                        .build())
                .setResults(42)
                .build();

EAnalytics.getInstance().track(search)
```

# Lista de páginas #

  * Página genérica
  * Página de producto
  * Página de categoría 
  * Página de motor de búsqueda 
  * Página de error 
  * Página de presupuesto  
  * Página de cesta
  * Página de pedido
  
# Página genérica

El marcador a continuación es genérico y debe implementarse en todas las páginas del sitio web excepto en la de la ficha de producto, pedido, presupuesto y cesta iniciada. Esto incluye principalmente la página de inicio y las páginas del túnel de conversión entre la cesta y la confirmación del pedido.

Te permite subir todo el tráfico site-centric, las páginas vistas y las visitas, así como su origen a través de los canales naturales como el acceso directo, la indexación y los referentes.

Nosotros controlamos de forma descentralizada todas las acciones que se implementarán más tarde. De este modo, no necesitamos cambiar el código de la página de tu sitio web para disfrutar de funcionalidades adicionales.

Para tener informes más completos puedes añadir los parámetros a continuación al formato genérico o al conjunto de marcadores colectores descritos en esta documentación.

### Liste des paramètres

  * __**path :**__ (NOM_PAGE) Este parámetro permite nombrar la página para identificarla más tarde en los informes. Para saber más puedes consultar el [artículo siguiente] (https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**uid :**__ (UID) Este parámetro debe completarse con tu ID interna cuando el internauta está conectado para consolidar los datos del historial provenientes de diferentes dispositivos. Puedes reconciliar un clic con la descarga de la aplicación. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).

## Implementación

El objeto **EAProperties** es la categoría que contiene todos los parámetros internos o modificables de nuestro colector de tags. 

Para añadir un nuevo parámetro, usa el método nativo **setCLE(VALEUR)** o el método genérico **set("CLE","VALEUR")**.

__**Ejemplo:**__


```xml
EAProperties genericTag = new EAProperties.Builder("NOM_PAGE")
.setUID("UID")
.set("NOM_PARAM_PERSO","VALEUR_PARAM_PERSO")
.build();
Eanalytics.getInstance().track(genericTag);
```

__**Con valores:**__

```xml
EAProperties genericTag = new EAProperties.Builder("|univers|rubrique|page")
.setUID("5434742")
.set("abonnement","mensuel")
.build();
Eanalytics.getInstance().track(genericTag);
```

# Página de producto

Este marcador permite recuperar las páginas vistas y las visitas a productos de tu catálogo. Sirve principalmente para abastecer los informes disponibles en la siguiente dirección:

  * Site-centric > Productos > **Adquisición & rendimiento de producto **

También puedes pasar una o varias categorías de producto.

**Nota: Se considera una página de producto todo marcador que contenga solamente una referencia de producto sin los parámetros __scart__ __estimate__ __ref__ y __amount__.**

En general, nuestro sistema no reconocerá como marcador de página de producto un marcador colector que contenga uno de los parámetros citados más arriba o varias referencias de producto.

## Lista de parámetros

  * __**path:**__ (NOM_PAGE) Este parámetro permite nombrar la página para identificarla más tarde en los informes. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**uid:**__ (UID) Este parámetro debe completarse con tu ID interna cuando el internauta está conectado para consolidar los datos del historial provenientes de diferentes dispositivos. Puedes reconciliar un clic con la descarga de la aplicación. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdref:**__ (ID_PRODUIT) Ce paramètre doit être valorisé avec la référence du produit consulté par l'internaute. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdname :**__ (NOM_PRODUIT) Este parámetro debe valorarse con la referencia de producto consultada por el internauta. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdgroup :**__ (GROUPE) Puedes asociar un grupo a una referencia de producto. Este parámetro permite asocia un margen a un producto a través de una asociación a un grupo “A” o “B”. Por tanto, cada una de las etiquetas designa el grupo de margen sin divulgar el total exacto en nuestro colector de tags. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdparam-xxxxx:**__ (VALEUR_PARAM, NOM_PARAM) Este parámetro permite asociar una categoría a la referencia del producto. Debes precisar el nombre de la categoría tras el prefijo **prdparam-**. Por ejemplo, prdparam-universo, prdparam-talla, etc. También puedes añadir tantas categorías como quieras. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).

## Implementación

El objeto **EAProducts** es la categoría dedicada al tracking de páginas de producto.

El producto debe ser creado a través del objeto **EAOProduct** que se convierte en parámetro obligatorio.
Cada objeto **Product** puedo completarse con un objeto **Params** para asociarle uno o varios parámetros de producto.
Una vez se ha iniciado y completado el producto con los parámetros deseados, intégralo al objeto **EAProducts** a través del método **addProduct**.

__**Ejemplo:**__

```xml
Params param1 = new Params.Builder()
.addParam("NOM_PARAM","VALEUR_PARAM")
.addParam("marque", "MARQUE")
.addParam("categorie", "CATEGORIE")
.build();

Product produit1 = new Product.Builder("ID_PRODUIT")
.setName("NOM_PRODUIT")
.setGroup("GROUPE")
.setParams(param1)
.build();

EAProducts productPage = new EAProducts.Builder("NOM_PAGE")
.setUID("UID")                
.addProduct(produit1)
.build();

EAnalytics.getInstance().track(productPage);
```

__**Con valores:**__

```xml
Params param1 = new Params.Builder()
.addParam("univers", "Vetement")
.addParam("categorie", "Tshirt")
.build();

Product produit1 = new Product.Builder("G73624623")
.setName("Tshirt_bleu")
.setGroup("A")
.setParams(param1)
.build();

EAProducts productPage = new EAProducts.Builder("Vetements|Tshirts|Tshirt_bleu_marqueA")
.setUID("63726")
.set("newsletter", "oui")
.addProduct(produit1)
.build();

EAnalytics.getInstance().track(productPage);
```

# Page de resultado

Este marcador permite enviar a tus partners las 3 primeras referencias de producto que se muestran en la página de resultados para optimizar el retargeting de internautas en tu página.
Su uso se limita principalmente a nuestro producto Eulerian Tag Master y no produce ningún efecto en los informes de adquisición&rendimiento en la DDP de Eulerian.

**Nota: Se considera una página de resultado todo marcador que contenga varias referencias de producto sin los parámetros __scart__ __estimate__ __ref__ y __amount__.**

En caso de que tu página de resultado solo tenga una referencia de producto, y por tanto un solo parámetro **prdref**, nuestro sistema reconocerá el marcador como página de producto y no como página de resultado.

## Lista de parámetros

  * __**path :**__ (NOM_PAGE) Este parámetro permite nombrar la página para identificarla más tarde en los informes. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**uid :**__ (UID) Este parámetro debe completarse con tu ID interna cuando el internauta está conectado para consolidar los datos del historial provenientes de diferentes dispositivos. Puedes reconciliar un clic con la descarga de la aplicación. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdref :**__ (ID_PRODUIT) Este parámetro debe valorarse con la referencia de producto consultada por el internauta. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).

## Implementación

Usa el objeto **EAProducts** para este tipo de Tag y pasa los primero X productos mostrados.

El producto debe ser creado a través del objeto **Product** que se convierte en parámetro obligatorio.
Una vez que éste se inicia y se completa con los parámetros deseados, intégralo al objeto **EAProducts** a través del método addProduct. 

__**Ejemplo:**__

```xml
Product produit1 = new Product.Builder("ID_PRODUIT_1").build();
Product produit2 = new Product.Builder("ID_PRODUIT_2").build();
Product produit3 = new Product.Builder("ID_PRODUIT_3").build();
 
EAProducts resultPage = new EAProducts.Builder("NOM_PAGE")
.setUID("UID")                
.addProduct(produit1)
.addProduct(produit2)
.addProduct(produit3)
.build();
 
EAnalytics.getInstance().track(resultPage);
```

__**Con valores:**__

```xml
Product produit1 = new Product.Builder("CH32452").build();
Product produit2 = new Product.Builder("C654322").build();
Product produit3 = new Product.Builder("V643536").build();
 
EAProducts resultPage = new EAProducts.Builder("Categorie|Vestes")
.setUID("73646")                
.addProduct(produit1)
.addProduct(produit2)
.addProduct(produit3)
.build();
 
EAnalytics.getInstance().track(resultPage);
```

# Página de motor de búsqueda

El marcador de motor de búsqueda interna permite abastecer la interfaz con información sobre consultas de los internautas y un número ilimitado de parámetros adicionales. Toda la información se compara con las ventas generadas y los canales de adquisición activados durante la sesión.

**Nota: Se considera una página motor de búsqueda interna todo marcador que contenga el parámetro  __isearchengine__.**

Debe tenerse en cuenta que el marcador de motor de búsqueda no es exclusivo. Si añades una referencia de producto con el parámetro **prdref** por ejemplo, nuestro sistema considerará el marcador como página de producto.

## Lista de parámetros

  * __**path:**__ (NOM_PAGE) Este parámetro permite nombrar la página para identificarla más tarde en los informes. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**uid:**__ (UID) Este parámetro debe completarse con tu ID interna cuando el internauta está conectado para consolidar los datos del historial provenientes de diferentes dispositivos. Puedes reconciliar un clic con la descarga de la aplicación. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**isearchengine:**__ (NOM_MOTEUR_INTERNE) Este parámetro permite nombre el motor de búsqueda interna y distinguirlo de los otros campos de búsqueda si hay varios en tu página. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**isearchresults:**__ (NOMBRE_DE_RESULTATS) Este parámetro debe contener el número de resultados de búsqueda generados por el internauta. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**isearchkey :**__ (CLE_DU_PARAMETRE_RECHERCHE) Este parámetro debe completarse con la llave del campo adicional que deseas integrar en tu marcador de búsqueda interna. Para saber más puedes consultar [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**isearchdata :**__ (VALEUR_DU_PARAMETRE_RECHERCHE) Este parámetro debe contener el valor del campo isearchkey citado más arriba. A cada campo de búsqueda suplementaria deben añadirse los parámetros  isearchkey y isearchdata. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).

## Implementación

El objeto **EASearch** es la categoría dedicada al tracking del motor de búsqueda interna

Crea un objeto **Params** y usa el método **addParam** para los parámetros **isearchkey**, **isearchdata**.

__**Ejemplos:**__

```xml
Params param1 = new Params()
.addParam("CLE_DU_PARAMETRE_DE_RECHERCHE_1","VALEUR_DU_PARAMETRE_RECHERCHE_1")
.addParam("CLE_DU_PARAMETRE_DE_RECHERCHE_2","VALEUR_DU_PARAMETRE_RECHERCHE_2")
.addParam("CLE_DU_PARAMETRE_DE_RECHERCHE_3","VALEUR_DU_PARAMETRE_RECHERCHE_3")
.build();

EASearch searchPage = new EASearch.Builder("NOM_PAGE", "NOM_MOTEUR")
.setUID("UID")
.setResults(NOMBRE_DE_RESULTATS)
.setParams(param1)
.build();

EAnalytics.getInstance().track(searchPage);
```

__**Con valores:**__

```xml
Params param1 = new Params()
.addParam("motcle","veste")
.addParam("montant_min","100.00")
.addParam("montant_max","400.00")
.build();

EASearch searchPage = new EASearch.Builder("Moteur_interne|veste", "moteur_interne")
.setUID("34678")
.setResults(150)
.setParams(Params)
.build();

EAnalytics.getInstance().track(searchPage);
```

# Página de error 404

El marcador **error** indica que la aplicación de la página ha generado un error 404. Esto sirve para abastecer los informes disponibles en la página siguiente:
  * Site-centric > Análisis de audiencia > Por página > **Página de error**

Este tag permite recuperar todas las URLs que han generado una página 404 y te da la posibilidad de hacer las correcciones necesarias para redirigir tu tráfico.

Puedes usarlo para estar al tanto de problemas recurrentes en una etapa concreta del túnel de conversión.

## Lista de parámetros

  * __**error:**__ (Obligatorio) Este parámetro es siempre **1** y puede añadirse a todos los tipos de marcador. Su presencia contabiliza una página vista en el informe más arriba. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**path:**__ (Obligatorio) Este parámetro permite nombrar la página para identificarla más tarde en los informes. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).

## Implementación

Utiliza el método **set** con la clave de **__error__** para indicar la página de error. 

__**Ejemplo:**__

```xml
EAProperties errorTag = new EAProperties.Builder("erreur|404")
.set("error","1")
.build();
Eanalytics.getInstance().track(errorTag);
```

# Página de presupuesto

Este marcador contabiliza los presupuestos y el conjunto de informaciones asociadas a este tipo de conversión: 
  * Los productos que lo componen 
  * El tipo de presupuesto
  * El total global
  * Informaciones contextuales 

Cada nuevo presupuesto se detalla con todos los canales del historial de navegación que han conducido a su generación. 

**Nota: Se eliminarán los dobles de todos los presupuestos en función de la referencia proporcionada con el marcador.**


Si usas 2 veces la misma referencia para 2 presupuestos diferentes, se ignorará la segunda llamada en virtud del principio de unicidad de conversión.
Eulerian Analytics te permite anular o validar presupuestos para medir el rendimiento real de tus campañas en el tiempo. 

**Nota: Se considera una página de presupuestos todo marcador que contenga los parámetros __ref__ y __estimate__.**

## Lista de parámetros

  * __**path :**__ (NOM_PAGE) Este parámetro permite nombrar la página para identificarla más tarde en los informes. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**uid :**__ (UID) Este parámetro debe completarse con tu ID interna cuando el internauta está conectado para consolidar los datos del historial provenientes de diferentes dispositivos. Puedes reconciliar un clic con la descarga de la aplicación. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**ref :**__ (REFERENCE_DEVIS) Se trata de una referencia única del presupuesto que permite identificarlo y encontrarlo el nuestro sistema. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**amount :**__ (MONTANT_DEVIS) Este parámetro debe contener el total del presupuesto, IVA incluido. Los decimales se separán por un punto. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**type :**__ (TYPE_DE_DEVIS) Este parámetro permite categorizar el presupuesto según tu propio sistema de referencia. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdref :**__ (ID_PRODUIT) Este parámetro debe completarse con la referencia de producto asociada al presupuesto y repetida en cada valor diferente. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdamount :**__ (MONTANT_PRODUIT) Este parámetro permite especificar el total unitario de cada producto asociado al presupuesto. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdquantity :**__ (QUANTITE_PRODUIT) Este parámetro permite especificar las cantidades de cada producto asociado al presupuesto. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).

## Implementación

El objeto **EAEstimate** es la categoría dedicada al tracking de páginas de presupuesto.

Un presupuesto puede contener uno o varios productos. Cada producto que constituye el presupuesto debe crearse a través del objeto **Product** que se convierte en parámetro obligatorio. Cada objeto **Product** debe iniciarse y completarse con los parámetros elegidos antes de ser integrado al objeto **EAEstimate** a través del método addProduct. 

__**Ejemplo:**__

```xml
Product produit1 = new Product.Builder("ID_PRODUIT_1").build();
Product produit2 = new Product.Builder("ID_PRODUIT_2").build();

EAEstimate estimatePage = new EAEstimate.Builder("NOM_PAGE", "REFERENCE_DU_DEVIS")
.setAmount(MONTANT_DU_DEVIS)
.setType("TYPE_DE_DEVIS")
.setUID("UID")          
.addProduct(produit1, MONTANT_PRODUIT_1, QUANTITE_PRODUIT_1)
.addProduct(produit2, MONTANT_PRODUIT_2, QUANTITE_PRODUIT_2)
.build();
 
EAnalytics.getInstance().track(estimatePage);
```

__**Con valores:**__

```xml
Product produit1 = new Product.Builder("505").build();

EAEstimate estimatePage = new EAEstimate.Builder("Credit|devis", "C4536567")
.setAmount(5000.00)
.setType("Credit_48mois")
.setUID("627253")          
.addProduct(produit1, 5000.00, 1)
.build();
 
EAnalytics.getInstance().track(estimatePage);
```

# Página de cesta

Este marcador contabiliza las cestas iniciadas y permita calcular la tasa de conversión y de abandono con respecto al marcador de venta.

También puedes pasar los productos y categorías de productos asociados a la cesta del visitante especificando los totales y las cantidades respectivas. Estos parámetros permiten abastecer los informes de productos disponibles en la dirección siguiente:

  * Site-centric > Productos > **Adquisición & rendimiento de producto**

La duración de vida de una cesta es de 30 minutos durante la sesión del internauta, según [[fr:glossary|nuestra definición]].

**Nota: Se considera una página de cesta todo marcador que contenga el parámetro __scart__.**

## Lista de parámetros

  * __**path:**__ (Obligatorio) Este parámetro permite nombrar la página para identificarla más tarde en los informes. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**uid :**__ (Opcional) Este parámetro debe completarse con tu ID interna cuando el internauta está conectado para consolidar los datos del historial provenientes de diferentes dispositivos Puedes reconciliar un clic con la descarga de la aplicación. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**scartcumul :**__ (Obligatorio) El valor de este parámetro indica el modo de contabilización de los productos en la cesta. Cuando es igual a 0, nuestro sistema interpreta los productos pasados por el marcador como constituyentes de la integridad de la cesta. Cuando es igual a 1, los productos pasados por el marcador se añaden a cada llamada sucesiva. Para saber más puedes consultar [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdref :**__ (Obligatoire) Este parámetro debe completarse con la referencia del producto incluido por el internauta en la cesta y repetido para cada producto diferente. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdamount :**__ (Obligatorio) Este parámetro permite especificar el total unitario de cada producto asociado al presupuesto. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdquantity :**__ (Obligatorio) Este parámetro permite especificar las cantidades de cada producto asociado al presupuesto. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).

## Implementación

El objeto **EACart** es la categoría dedicada al tracking de la página de cesta iniciada.

Una cesta puede contener uno o varios productos. Cada producto que constituye la cesta debe crease a través del objeto **Product** que se convierte en parámetro obligatorio. 
Cada objeto **Product** debe iniciarse y completarse con los parámetros deseados antes de ser integrado en el objeto **EACart** a través del método addProduct. 

__**Ejemplo:**__

```xml
Product produit1 = new Product.Builder("ID_PRODUIT_1").build();
Product produit2 = new Product.Builder("ID_PRODUIT_2").build();
Product produit3 = new Product.Builder("ID_PRODUIT_3").build();
 
EACart cartPage = new EACart.Builder("NOM_PAGE")
.setCartCumul(true_OU_false)
.setUID("UID")
.addProduct(produit1, MONTANT_PRODUIT_1, QUANTITE_PRODUIT_1)
.addProduct(produit2, MONTANT_PRODUIT_2, QUANTITE_PRODUIT_2)
.addProduct(produit3, MONTANT_PRODUIT_3, QUANTITE_PRODUIT_3)
.build();
 
EAnalytics.getInstance().track(cartPage);
```

__**Con valores:**__

```xml
Product produit1 = new Product.Builder("XY65643").build();
Product produit2 = new Product.Builder("XV12345").build();
 
EACart cartPage = new EACart.Builder("Panier")
.setCartCumul(false)
.setUID("57483")
.addProduct(produit1, 65.00, 1)
.addProduct(produit2, 20.00, 3)
.build();
 
EAnalytics.getInstance().track(cartPage);
```

# Página de pedido

Este marcador contabiliza las conversiones y permite el cálculo del ROI para el conjunto de canales marketing. Además del total global del pedido, puedes pasar productos asociados, el modo de pago, la divisa o el tipo de venta. Estas prioridades proporcionarán más detalles a tus análisis y podrán explotarse en nuestra suite. Cada nueva conversión aparece detallada con los canales y el historial de navegación. 

**Nota: Se eliminarán los dobles de todos los pedidos en función de la referencia proporcionada en el marcador de venta.**

Si usas 2 veces la misma referencia para 2 pedidos diferentes, se ignorará la segunda llamada en virtud del principio de unicidad de conversión.

Eulerian Analytics te permite anular los pedidos para medir el rendimiento real de tus campañas. Por tanto, se aconseja implementar el marcador de pedido lo antes posible en el proceso de compra si las informaciones para configurar su llamada están disponibles (referencia, total, tipo de pago, etc.).

De hecho, si la plataforma de pago no obliga al internauta a pasar de nuevo por la página de compra para validar su pedido, no siempre se llamará al marcador tras la plataforma de pago.

**Nota: Fuera del status del pedido (pendiente, válido o inválido), se puede definir el contenido de un pedido tras haber sido registrado. No podemos añadir o quitar un producto pero sí modificar el total global del pedido.**

**Nota: Se considera una página de confirmación todo marcador que contenga los parámetros __ref__ y __amount__ y que no contenga __scart__ ni __estimate__.**

## Lista de parámetros

  * __**path:**__ (NOM_PAGE) Este parámetro permite nombrar la página para identificarla más tarde en los informes. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**uid :**__ (UID) Este parámetro debe completarse con tu ID interna cuando el internauta está conectado para consolidar los datos del historial provenientes de diferentes dispositivos. Puedes reconciliar un clic con la descarga de la aplicación. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**ref :**__ (REFERENCE_VENTE) Se trata de una referencia única del presupuesto que permite identificarlo y encontrarlo el nuestro sistema. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**amount :**__ (MONTANT_VENTE) Este parámetro debe contener el total del presupuesto, IVA incluido. Los decimales se separan por un punto. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**payment :**__ (MOYEN_DE_PAIEMENT) Este parámetro permite identificar el tipo de pago usado por el internauta. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**type :**__ (TYPE_DE_VENTE) Este parámetro permite categorizar el presupuesto según tu propio sistema de referencia. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**currency :**__ (DEVISE_DU_MONTANT) Este parámetro permite convertir el total indicado en el parámetro amount si este tiene una divisa diferente a la configurada en la interfaz. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**newcustomer :**__ (0_OU_1) Este parámetro se suele usar en el marcador de pedido para diferenciar los nuevos compradores de los clientes asiduos. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdref :**__ (ID_PRODUIT) Este parámetro debe completarse con la referencia de producto asociada al presupuesto y repetida en cada valor diferente. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdamount :**__ (MONTANT_PRODUIT) Este parámetro permite especificar el total unitario de cada producto asociado al presupuesto. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).
  * __**prdquantity :**__ (QUANTITE_PRODUIT) Este parámetro permite especificar las cantidades de cada producto asociado al presupuesto. Para saber más puedes consultar el [artículo siguiente](https://eulerian.wiki/doku.php?id=es:collect:technical_implementation:parameters_list).

## Implementación

El objeto **EAOrder** es la categoría dedicada al tracking de la página de confirmación de pedido.

Una venta puede contener uno o varios productos. Cada producto que constituye el presupuesto debe crearse a través del objeto **Product** que se convierte en parámetro obligatorio. 
Cada objeto **Product** debe iniciarse y completarse con los parámetros elegidos antes de ser integrado al objeto **EAOrder** ** a través del método **addProduct**.

__**Ejemplo:**__

```xml
Product produit1 = new Product.Builder("ID_PRODUIT_1").build();
Product produit2 = new Product.Builder("ID_PRODUIT_2").build();
Product produit3 = new Product.Builder("ID_PRODUIT_3").build();
 
EAOrder orderPage = new EAOrder.Builder("NOM_PAGE", "REFERENCE_DE_LA_VENTE")
.setAmount(MONTANT_DE_LA_VENTE)
.setType("TYPE_DE_VENTE")
.setPayment("MOYEN_DE_PAIEMENT")
.setCurrency("DEVISE_DU_MONTANT")
.setEstimateRef("REFERENCE_DU_DEVIS_ASSOCIE")
.setUID("UID")
.set("profile", "NOUVEAU_OU_ANCIEN_CLIENT")
.addProduct(produit1, MONTANT_PRODUIT_1, QUANTITE_PRODUIT_1)
.addProduct(produit2, MONTANT_PRODUIT_2, QUANTITE_PRODUIT_2)
.addProduct(produit3, MONTANT_PRODUIT_3, QUANTITE_PRODUIT_3)
.build();
 
EAnalytics.getInstance().track(orderPage);
```

__**Con valores:**__

```xml
Product produit1 = new Product.Builder("HA1432245").build();
Product produit2 = new Product.Builder("VE98373626").build();
 
EAOrder orderPage = new EAOrder.Builder("Tunnel|Confirmation", "F654335671")
.setAmount(460.00)
.setType("Vol+Hotel")
.setPayment("CB")
.setCurrency("USD")
.setEstimateRef("D872635671")
.setUID("62524")
.set("profile", "nouveau_client")
.addProduct(produit1, 60.00, 1)
.addProduct(produit2, 400.00, 1)
.build();
 
EAnalytics.getInstance().track(orderPage);
```
