# Plan de taggage SDK Android #


## Tracking d'une application webview

En ce qui concerne la navigation dans le contexte d'une application de type *webview* sur Android (site web html/js standard), notre identifiant interne doit être fourni dans l'url d'ouverture afin d'assurer la continuité du tracking.
Pour récupérer cette valeur, la technique du client doit utiliser la fonction suivante définie dans notre SDK:

  * Android :
```xml
String euidl = EAnalytics.getEUIDL();
```

Une fois cette valeur fournie dans le paramètre **ea-euidl-bypass**, on débraye le système de cookie usuel pour utiliser l'euidl en provenance de l'application.

**Exemple:**

```xml
http://www.vpg.fr/ios-landing-webview?ea-euidl-bypass=$euidl_de_l_app
```

Si le ea.js détecte ce paramètre alors il l'utilise et le stocke en interne tout le long de la session, ceci afin d'éviter que le site ne le repasse a chaque page visitée.
Une fois ce passage de paramètre effectué, le tracking d'une application IOS en contexte webview ne diffère pas des formats Javascript que nous utilisons pour un site web classique.

La documentation disponible à [cette adresse](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:general) contient toutes les informations nécessaires à l'implémentation de nos Tags Javascript.



## Tracking d'une application native

Dans le cas d'une application native, notre SDK doit être incorporé au code source de l'application pour pouvoir intégrer le type de marqueur suivant:

```xml
EAProperties properties = new EAProperties.Builder("NOM_PAGE”).build();
Eanalytics.getInstance().track(properties);
```

Notre **SDK** a été conçu pour faciliter au maximum l'intégration en offrant une structure objet simple à utiliser et une documentation détaillée.

Les paramètres disponibles et les possibilités sont exactement les mêmes que pour notre Tag collector. Vous pourrez donc suivre les chemins de navigation sur votre application et les comparer avec votre site web classique en intégrant les mêmes variables.

Les appels générés par ces marqueurs sont également très proches de l'appel collector classique et ont été conçus pour être le plus léger possible afin de ne pas perturber l'application.\\
Certains paramètres de notre collector sont spécifiques au tracking d'une application mobile. Pour plus d'informations à ce sujet consultez [[fr:collect:technical_implementation:eacollectortag:general:applicationparameters#parametres_specifiques_aux_applications_mobiles|cette documentation]].

<note>Notre SDK est également capable de collecter un nombre illimité d'interactions offline et de les envoyer une fois l'utilisateur connecté. Ce procédé nous permet de continuer à tracker l'internaute même si ce dernier interagit avec votre application hors connection. La navigation est enregistrée et réattribuée à posteriori via notre paramètre ereplay-time.</note>

## Règle d'affectation de trafic

Sur un site web classique, notre système identifie et affecte le trafic en fonction de l'**URL** sur lequel le marqueur est appelé. Nous n'avons cependant pas accès à cette information dans le contexte d'une application.\\
Pour définir la règle d'attribution de traffic, nous nous basons donc sur le sous domaine de tracking utilisé et passé en paramètre de la méthode **init** de l'objet EAnalytics défini dans notre SDK:

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
Ça signifie aussi que si vous utilisez le même sous domaine de tracking pour plusieurs de vos applications, nous devrons les distinguer en ajoutant le paramètre [from](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list) dans les marqueurs. Ce sera automatiquement le cas pour une application hybride qui partage certaines pages en commun avec votre site web.</note>

A noter également que l'absence d'URL signifie aussi l'absence d'un nom de page par défaut. Le paramètre [path](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list) n'est pas optionnel mais __obligatoire__ dans le tracking d'une application.

Le trafic généré par votre application peut remonter sur un site Eulerian dédié ou être fusionné avec celui d'un autre site web existant. Comme indiqué plus haut, si votre application passe en mode webview pour certaines pages l'ajout du paramètre [from](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list) est obligatoire pour nous permettre de rediriger le trafic correctement dans nos systèmes. 



## Intégration via jCenter

Dans le cadre du tracking des applications mobiles, **jCenter** vous permet de simplifier le processus d'intégration du SDK.

### Installation

Insérer le SDK Android dans l'outil [Gradle](http://gradle.org/|Gradle).
```xml
dependencies {
   compile 'com.eulerian.android.sdk:eanalytics:1.6.0'
}
```

Dans le fichier **AndroidManifest.xml**, ajouter les permissions suivantes :

```javascript
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

Ajouter également la déclaration suivante au sein de l'élément de l'application pour tracker l'installation du refereur

```javascript
<receiver
   android:name="com.eulerian.android.sdk.InstallReferrerReceiver"
   android:exported="true">
   <intent-filter>
      <action android:name="com.android.vending.INSTALL_REFERRER" />
   </intent-filter>
</receiver>
```

## Démarrer Eulerian Analytics

Avant d'utiliser le SDK, assurez vous de l'initialiser, grâce à la fonction **onCreate()** ci-dessous.

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

Il est désormais possible d'intégrer tout type de tags accompagnés de leurs paramètres. Par exemple, le tag générique est identifié par EAProperties. Les autres types de tags possèdent chacun une identification spécifique (ex : EACart pour le panier, EAEstimate pour le devis, EAProducts pour la catégorie, etc.).

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

# Liste des pages #

  * Page générique
  * Page produit
  * Page catégorie
  * Page moteur de recherche
  * Page erreur
  * Page devis
  * Page panier
  * Page commande

# Page générique

Le marqueur ci-dessous est générique et doit être implémenté sur toutes les pages du site hors fiche produit, commande, devis et panier commencé. Ça inclut notamment la homepage et les pages du tunnel de conversion entre le panier et la confirmation de commande.

Il vous permet de remonter tout le traffic site-centric, les pages vues et visites ainsi que la provenance via les leviers naturels comme l'accès direct, le référencement naturel ou les référents.

Nous gérons ensuite l’intégralité des actions à mettre en place en délocalisé. Ainsi, vous n'avez pas besoin de changer le code de la page sur votre site pour bénéficier de fonctionnalités supplémentaires.

Vous pouvez ajouter les paramètres ci-après au format générique ou à l'ensemble des marqueurs collector décrits dans cette documentation afin d'enrichir vos rapports.

### Liste des paramètres

  * __**path :**__ (NOM_PAGE) Ce paramètre permet de donner un nom à la page afin de l'identifier dans les rapports. Pour en savoir plus veuillez vous reporter à [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**uid :**__ (UID) Ce paramètre doit être valorisé avec votre ID interne quand l'internaute est loggé afin de consolider l'historique accumulée sur les différents devices utilisés. Vous pouvez ainsi réconciler un clic avec le téléchargement de l'application. Pour en savoir plus, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).

### Implémentation

L'objet **EAProperties** est la classe mère qui contient tous les paramètres internes ou modifiables de notre Tag collector. 

Pour ajouter un nouveau paramètre, utilisez une des méthodes **setCLE(VALEUR)** natives ou la méthode générique **set("CLE","VALEUR")**.

__**Exemple:**__


```xml
EAProperties genericTag = new EAProperties.Builder("NOM_PAGE")
.setUID("UID")
.set("NOM_PARAM_PERSO","VALEUR_PARAM_PERSO")
.build();
Eanalytics.getInstance().track(genericTag);
```

__**Avec valeurs:**__

```xml
EAProperties genericTag = new EAProperties.Builder("|univers|rubrique|page")
.setUID("5434742")
.set("abonnement","mensuel")
.build();
Eanalytics.getInstance().track(genericTag);
```

# Page produit

Ce marqueur permet de récupérer les pages vue et visites des produits de votre catalogue. Il sert notamment à alimenter les rapports disponibles à l'adresse suivante :

  * Site-centric > Produits > **Acquisitions & Performance produit**

Vous pouvez également passer une ou plusieurs catégories de produit.

**Note : Est considéré comme une page produit tout marqueur contenant une et une seule référence produit sans les paramètres __scart__ __estimate__ __ref__ et __amount__.**
En clair, un marqueur collector qui contient un des paramètres cités ci-dessus ou plusieurs références produit ne sera pas interprété par notre système comme un marqueur de page produit.



## Liste des paramètres

  * __**path :**__ (NOM_PAGE) Ce paramètre permet de donner un nom à la page afin de l'identifier dans les rapports. Pour en savoir plus veuillez vous reporter à [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**uid :**__ (UID) Ce paramètre doit être valorisé avec votre ID interne quand l'internaute est loggé afin de consolider l'historique accumulée sur les différents devices utilisés. Vous pouvez ainsi réconciler un clic avec le téléchargement de l'application. Pour en savoir plus, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdref :**__ (ID_PRODUIT) Ce paramètre doit être valorisé avec la référence du produit consulté par l'internaute. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdname :**__ (NOM_PRODUIT) Vous pouvez associer à une référence produit un nom plus lisible pour faciliter votre reporting. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdgroup :**__ (GROUPE) Vous pouvez associer un groupe à une référence produit. Ce paramètre permet d'associer une marge à un produit via une association à un groupe “A” ou “B”. Chacun de ces labels désignant un groupe de marge sans pour autant divulguer le montant exact en clair dans nos Tags collector.Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdparam-xxxxx :**__ (NOM_PARAM, VALEUR_PARAM) Ce paramètre permet d'associer une catégorie à la référence du produit. Vous devez préciser le nom de la catégorisation après le préfixe **prdparam-**. Exemples : prdparam-univers, prdparam-taille etc. Vous pouvez ajouter autant de catégorisations que souhaité. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).

## Implémentation

L'objet **EAProducts** est la classe dédiée au tracking des pages produit.

Le produit en lui même doit être créé via l'objet **Product** qui prend la référence en paramètre obligatoire. 
Chaque objet **Product** peut être complété avec un objet **Params** pour lui associer un ou plusieurs paramètres produit.
Une fois le produit initialisé et complété avec les paramètres souhaités, intégrez le à l'objet **EAProducts** via la méthode **addProduct**.

__**Exemple:**__

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

__**Avec valeurs:**__

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

# Page de résultat

Ce marqueur permet d'envoyer à vos partenaires les 3 premières références produit listées dans la page de résultat pour optimiser le retargeting des internautes sur votre site. 
Son utilisation se limite principalement à notre produit Eulerian TMS et n'impacte pas le rapport Acquisition & performance produit dans Eulerian DDP.

<WRAP center round info 80%>
**Est considéré comme une page de résultat tout marqueur contenant plusieurs références produit sans les paramètres __scart__ __estimate__ __ref__ et __amount__.** 
Dans le cas ou votre page de résultat ne contiendrait qu'une seule référence produit et donc un seul paramètre **prdref**, le marqueur sera interprété comme une page produit et non une page de résultat.
</WRAP>



## Liste des paramètres

  * __**path :**__ (NOM_PAGE) Ce paramètre permet de donner un nom à la page afin de l'identifier dans les rapports. Pour en savoir plus veuillez vous reporter à [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**uid :**__ (UID) Ce paramètre doit être valorisé avec votre ID interne quand l'internaute est loggé afin de consolider l'historique accumulée sur les différents devices utilisés. Vous pouvez ainsi réconciler un clic avec le téléchargement de l'application. Pour en savoir plus, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdref :**__ (ID_PRODUIT) Ce paramètre doit être valorisé avec la référence du produit consulté par l'internaute. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).

## Implémentation

Utilisez l'objet **EAProducts** pour ce type de Tag et passez les X premiers produits affichés.

Le produit en lui même doit être créé via l'objet **Product** qui prend la référence en paramètre obligatoire. 
Une fois ce dernier initialisé et complété avec les paramètres souhaités, intégrez le à l'objet **EAProducts** via la méthode addProduct. 

__**Exemple:**__

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

__**Avec valeurs:**__

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

# Page de recherche interne

Le marqueur de moteur de recherche interne permet de remonter dans l'interface les requêtes tapées par les internautes ainsi qu'un nombre illimité de paramètres additionnels. Toutes ces informations sont croisées avec les ventes générées et les leviers d'acquisition activés pendant la session.

<WRAP center round info 60%>
**Est considéré comme une page de moteur de recherche interne tout marqueur contenant le paramètre __isearchengine__.**
</WRAP>

A noter que le marqueur de moteur interne n'est pas exclusif. Si vous ajoutez une référence produit avec le paramètre **prdref** par exemple, le marqueur sera également considéré comme une page produit.

## Liste des paramètres

  * __**path :**__ (NOM_PAGE) Ce paramètre permet de donner un nom à la page afin de l'identifier dans les rapports. Pour en savoir plus veuillez vous reporter à [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**uid :**__ (UID) Ce paramètre doit être valorisé avec votre ID interne quand l'internaute est loggé afin de consolider l'historique accumulée sur les différents devices utilisés. Vous pouvez ainsi réconciler un clic avec le téléchargement de l'application. Pour en savoir plus, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**isearchengine :**__ (NOM_MOTEUR) Ce paramètre désigne le nom du moteur interne et permet de le distinguer des autres champs de recherche si vous en avez plusieurs sur votre site. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**isearchresults :**__ (NOMBRE_DE_RESULTATS) Ce paramètre doit contenir le nombre de résultats de recherche générés par l'internaute. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**isearchkey :**__ (CLE_DU_PARAMETRE_DE_RECHERCHE) Ce paramètre doit être valorisé avec la clé du champ additionnel que vous souhaitez intégrer à votre marqueur de recherche interne. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**isearchdata :**__ (VALEUR_DU_PARAMETRE_RECHERCHE) Ce paramètre doit contenir la valeur du champ isearchkey cité plus haut. Chaque champ de recherche supplémentaire nécessite l'ajout d'un nouveau couple de paramètres isearchkey et isearchdata. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).

## Implémentation

L'objet **EASearch** est la classe dédiée au tracking du moteur de recherche interne.

Créez un objet **Params** et utilisez la méthode **addParam** pour chaque couple de paramètres **isearchkey**, **isearchdata**.

__**Exemples:**__

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

__**Avec valeurs:**__

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

# Page d'erreur 404

Le marqueur **error** indique que l’applicatif du site pour cette page a généré une erreur 404. Il sert notamment à alimenter les rapports disponibles à l'adresse suivante :

  * Site-centric > Analyse d'audience > Par page > **Page en erreur**

Ce tag permet de récupérer toutes les URLs qui ont généré une page 404 et vous donne la possibilité de faire les corrections nécessaires pour rediriger votre trafic en conséquence.

Vous pouvez notamment l'utiliser pour être alerté en cas de problème récurrent sur une étape particulière de votre tunnel de conversion.


## Liste des paramètres

  * __**error :**__ Ce paramètre vaut toujours **1** et peut être ajouté sur tous les types de marqueur. Sa présence dans le marqueur comptabilise une page vue dans le rapport cité ci-dessus. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**path :**__ (NOM_DE_LA_PAGE) Ce paramètre permet de donner un nom à la page afin de l'identifier dans les rapports.Pour en savoir plus veuillez vous reporter à [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).

## Implémentation

Utilisez la méthode **set** avec la clé **__error__** pour flagger la page en erreur.

__**Exemple:**__

```xml
EAProperties errorTag = new EAProperties.Builder("erreur|404")
.set("error","1")
.build();
Eanalytics.getInstance().track(errorTag);
```

# Page de devis

Ce marqueur comptabilise les devis et l'ensemble des informations associées à ce type de conversion : 
  * Les produits qui le composent. 
  * Le type de devis.
  * Le montant global
  * Des informations contextuelles 

Chaque nouveau devis est ainsi détaillé avec tous les leviers et l'historique de navigation qui a conduit à sa génération. 

<WRAP center round important 60%>
**Les devis sont dédoublonnés en fonction de la référence fournie dans le marqueur.**
</WRAP>


Si vous utilisez 2 fois la même référence pour 2 devis différents, le second appel est ignoré en vertu du principe d'unicité de la conversion. 
Eulerian Analytics vous permet d'annuler ou de valider des devis pour mesurer la performance réelle de vos campagnes dans le temps. 

<WRAP center round info 60%>
**Est considéré comme une page de devis tout marqueur contenant les paramètres __ref__ et __estimate__.**
</WRAP>

## Liste des paramètres

  * __**path :**__ (NOM_PAGE) Ce paramètre permet de donner un nom à la page afin de l'identifier dans les rapports. Pour en savoir plus veuillez vous reporter à [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**uid :**__ (UID) Ce paramètre doit être valorisé avec votre ID interne quand l'internaute est loggé afin de consolider l'historique accumulée sur les différents devices utilisés. Vous pouvez ainsi réconciler un clic avec le téléchargement de l'application. Pour en savoir plus, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**ref :**__ (REFERENCE_DU_DEVIS) Il s'agit de la référence unique du devis permettant d'identifier et de retrouver ce dernier dans notre système. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**amount :**__ (MONTANT_DU_DEVIS) Ce paramètre doit contenir le montant total TTC du devis. Les décimales doivent être séparées par un point. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**type :**__ (TYPE_DE_DEVIS) Ce paramètre permet de catégoriser le devis selon votre propre référentiel. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdref :**__ (ID_PRODUIT) Ce paramètre doit être valorisé avec la référence du produit associé au devis et répété pour chaque valeur différente. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdamount :**__ (MONTANT_PRODUIT) Ce paramètre permet de spécifier le montant unitaire de chaque produit associé au devis. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdquantity :**__ (QUANTITE_PRODUIT) Ce paramètre permet de spécifier les quantités de chaque produit associé au devis. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).

## Implémentation

L'objet **EAEstimate** est la classe dédiée au tracking des pages de devis.

Un devis peut contenir un ou plusieurs produits. Chaque produit constituant le devis doit être créé via l'objet **Product** qui prend sa référence en paramètre obligatoire. 
Chaque objet **Product** doit être initialisé et complété avec les paramètres souhaités avant d'être intégré à l'objet **EAEstimate** via la méthode addProduct. 

__**Exemple:**__

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

__**Avec valeurs:**__

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

# Page panier

Ce marqueur comptabilise les paniers commencés et permet le calcul du taux de conversion et d'abandon par rapport au marqueur de vente.

Vous pouvez également passer les produits et catégories de produits associées au panier du visiteur en spécifiant les montants et quantités respectives. Ces paramètres permettent d'alimenter les rapports produits disponibles à l'adresse suivante :

  * Site-centric > Produits > **Acquisitions & Performance produit**

La durée de vie d'un panier est de 30 minutes glissantes soit la session de l'internaute selon [[fr:glossary|notre définition]]. 

<WRAP center round info 60%>
**Est considéré comme une page panier tout marqueur contenant le paramètre __scart__.**
</WRAP>


## Liste des paramètres

  * __**path :**__ (NOM_PAGE) Ce paramètre permet de donner un nom à la page afin de l'identifier dans les rapports. Pour en savoir plus veuillez vous reporter à [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**uid :**__ (UID) Ce paramètre doit être valorisé avec votre ID interne quand l'internaute est loggé afin de consolider l'historique accumulée sur les différents devices utilisés. Vous pouvez ainsi réconciler un clic avec le téléchargement de l'application. Pour en savoir plus, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**scartcumul :**__ (0_OU_1) La valeur de ce paramètre indique le mode de comptabilisation des produits dans le panier. Lorsqu'il est égal à 0 notre système interprète les produits passés dans le marqueur comme constituant l'intégralité du panier. Lorsqu'il est égal à 1 les produits passés dans le marqueur s'additionneront à chaque appel successif. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdref :**__ (ID_PRODUIT) Ce paramètre doit être valorisé avec la référence du produit mis en panier par l'internaute et répété pour chaque produit différent. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdamount :**__ (MONTANT_PRODUIT) Ce paramètre permet de spécifier le montant unitaire de chaque produit différent dans le panier. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdquantity :**__ (QUANTITE_PRODUIT) Ce paramètre permet de spécifier les quantités de chacun des produits listés dans le panier. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).

## Implémentation

L'objet **EACart** est la classe dédiée au tracking de la page panier commencé.

Un panier peut contenir un ou plusieurs produits. Chaque produit constituant le panier doit être créé via l'objet **Product** qui prend sa référence en paramètre obligatoire. 
Chaque objet **Product** doit être initialisé et complété avec les paramètres souhaités avant d'être intégré à l'objet **EACart** via la méthode **addProduct**.

__**Exemple:**__

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

__**Avec valeurs:**__

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

# Page de commande

Ce marqueur comptabilise les conversions et permet le calcul du ROI pour l'ensemble de vos leviers marketing. En plus du montant global de la commande, vous pouvez passer les produits associés, le mode de paiement, la devise ou le type de vente. Ces propriétés enrichissent le niveau de détail disponible dans vos reportings et peuvent être exploités dans toute notre suite produit. Chaque nouvelle conversion est ainsi détaillée avec tous les leviers et l'historique de navigation. 

<WRAP center round important 60%>
**Les commande sont dédoublonnées en fonction de la référence fournie dans le marqueur de vente.**
</WRAP>


Si vous utilisez 2 fois la même référence pour 2 commandes différentes, le second appel est ignoré en vertu du principe d'unicité de la commande.

Eulerian Analytics vous permet d'annuler des commandes pour mesurer la performance réelle de vos campagnes. Il est par conséquent conseillé d'implémenter le marqueur de commande le plus tôt possible dans le processus d'achat si les informations nécessaires pour l'appeler sont disponibles (référence, montant, type de paiement, etc).

En effet, si la plateforme de paiement ne force pas l'internaute à repasser par le site marchand pour valider sa commande, un marqueur positionné après la plateforme de paiement ne sera pas toujours appelé.

<WRAP center round important 60%>
**En dehors du statut de la commande (pending, valid ou invalid), il est impossible de redéfinir le contenu d'une commande à posteriori de son enregistrement. On ne peut pas ajouter ou retirer un produit mais il est possible par contre de modifier le montant global de la commande.**
</WRAP>

<WRAP center round info 60%>
**Est considéré comme une page de confirmation de commande tout marqueur contenant les paramètres __ref__ et __amount__ et ne contenant ni __scart__ ni __estimate__.**
</WRAP>


## Liste des paramètres

  * __**path :**__ (NOM_PAGE) Ce paramètre permet de donner un nom à la page afin de l'identifier dans les rapports. Pour en savoir plus veuillez vous reporter à [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**uid :**__ (UID) Ce paramètre doit être valorisé avec votre ID interne quand l'internaute est loggé afin de consolider l'historique accumulée sur les différents devices utilisés. Vous pouvez ainsi réconciler un clic avec le téléchargement de l'application. Pour en savoir plus, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**ref :**__ (REFERENCE_DE_LA_VENTE) Il s'agit de la référence unique de la commande permettant d'identifier et de retrouver cette dernière dans notre système. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**amount :**__ (MONTANT_DE_LA_VENTE) Ce paramètre doit contenir le montant total de la commande TTC hors frais de ports. Les décimales doivent être séparées par un point. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**payment :**__ (MOYEN_DE_PAIEMENT) Ce paramètre permet de remonter le type de paiement utilisé par l'internaute. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**type :**__ (TYPE_DE_VENTE) Ce paramètre permet de catégoriser la vente sur votre propre référentiel. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**currency :**__ (DEVISE_DU_MONTANT) Ce paramètre permet de convertir le montant indiqué dans le paramètre amount si celui-ci est dans une devise différente de celle qui est configurée dans votre interface. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**profile :**__ (profile) En principe, vous pouvez forcer le profil de l'internaute sur n'importe quelle type de page. Ce paramètre est souvent utilisé sur le marqueur de commande pour différencier les nouveaux acheteurs des clients fidèles. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdref :**__ (ID_PRODUIT) Ce paramètre doit être valorisé avec la référence du produit commandé par l'internaute et répété pour chaque produit différent acheté. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdamount :**__ (MONTANT_PRODUIT) Ce paramètre permet de spécifier le montant unitaire de chaque produit différent dans la commande. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).
  * __**prdquantity :**__ (QUANTITE_PRODUIT) Ce paramètre permet de spécifier les quantités de chacun des produits achetés. Pour plus d'informations, veuillez consulter [l'article suivant](https://eulerian.wiki/doku.php?id=fr:collect:technical_implementation:parameters_list).

## Implémentation

L'objet **EAOrder** est la classe dédiée au tracking des commandes.

Un tag de commande peut contenir un ou plusieurs produits. Chaque produit constituant la commande doit être créé via l'objet **Product** qui prend sa référence en paramètre obligatoire. 
Chaque objet **Product** doit être initialisé et complété avec les paramètres souhaités avant d'être intégré à l'objet **EAOrder** via la méthode **addProduct**.

__**Exemple:**__

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

__**Avec valeurs:**__

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
