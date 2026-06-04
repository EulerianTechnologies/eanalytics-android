# Android SDK — Eulerian Analytics

Guide complet d'installation et de taggage pour le SDK Android Eulerian Analytics.

---

## Sommaire

- [Installation](#installation)
- [Initialisation](#initialisation)
- [Types de tags](#types-de-tags)
  - [Page générique](#page-générique) — `EAProperties`
  - [Page produit](#page-produit) — `EAProducts` (1 produit)
  - [Page catégorie](#page-catégorie) — `EAProducts` (N produits)
  - [Page moteur de recherche](#page-moteur-de-recherche) — `EASearch`
  - [Page d'erreur 404](#page-derreur-404) — `EAProperties`
  - [Page devis](#page-devis) — `EAEstimate`
  - [Page panier](#page-panier) — `EACart`
  - [Page commande](#page-commande) — `EAOrder`
  - [Tracking marchandisage](#tracking-marchandisage) — `EATpView` / `EATpClick`
  - [Context Flag (CFLAG)](#context-flag-cflag) — `SiteCentricCFlag`
- [Tracking WebView](#tracking-webview)
- [Métriques application](#métriques-application)
- [Consentement (GDPR / RGPD)](#consentement-gdpr--rgpd)
- [Dépendances](#dépendances)

---

## Installation

Ajoutez le dépôt JitPack dans votre fichier `build.gradle` racine :

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Ajoutez la dépendance dans le `build.gradle` de votre module :

```groovy
dependencies {
    implementation 'com.github.EulerianTechnologies:eanalytics-android:1.9.0'
}
```

Ajoutez les permissions requises dans votre `AndroidManifest.xml` :

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

Pour suivre les installations depuis Google Play, ajoutez également ce receiver :

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

## Initialisation

Initialisez le SDK dans votre `Application.onCreate()`. Déclarez d'abord votre sous-classe Application dans le manifest.

```xml
<!-- AndroidManifest.xml -->
<application android:name=".DemoApp">
```

```java
public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Remplacez "example.demo.com" par votre sous-domaine de tracking
        EAnalytics.init(this, "example.demo.com", false);
    }
}
```

> Le troisième paramètre active (`true`) ou désactive (`false`) les logs de debug.

> **Attribution du trafic dans Eulerian :** contrairement au tracking web où l'URL identifie le site, une application native n'a pas d'URL. Le SDK utilise automatiquement le **package name** de l'application (ex. `com.mycompany.myapp`) comme URL interne envoyée à Eulerian (paramètres `ea-appname` et `url`). Eulerian se base sur le sous-domaine de tracking passé à `init()` pour attribuer le trafic au bon site dans son interface.

> Si plusieurs applications partagent le même sous-domaine de tracking, utilisez le paramètre `from` dans les marqueurs pour les distinguer.

---

## Types de tags

> `path` (nom de page) est **obligatoire** sur chaque tag. Contrairement au web, il n'est jamais récupéré automatiquement depuis l'URL.

> **Méthode générique `set(String key, String value)` :** disponible sur tous les tags, elle permet d'ajouter n'importe quel paramètre personnalisé non couvert par les méthodes dédiées. Exemple : `.set("abonnement", "mensuel")`.

---

### Page générique — `EAProperties`

À utiliser sur toutes les pages qui ne sont pas des fiches produit, panier, commande ou devis (y compris la homepage et les étapes du tunnel entre panier et confirmation de commande). Remonte le trafic site-centric, les pages vues, les visites et les sources naturelles.

**Méthodes :**

- `Builder(String path)` — nom de la page **(obligatoire)**
- `setUID(String uid)` — ID interne de l'utilisateur connecté ; consolide l'historique multi-device et permet de réconcilier un clic avec un téléchargement
- `setEmail(String email)` — email de l'utilisateur
- `setProfile(String profile)` — profil de l'utilisateur (ex. `visitor`, `buyer`, `looker`)
- `setPageGroup(String group)` — groupe de pages pour le reporting
- `set(String key, String value)` — paramètre personnalisé libre
- `setCFlag(SiteCentricCFlag flag)` — attache un ou plusieurs context flags au tag (voir [Context Flag](#context-flag-cflag))

**Exemple :**

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

### Page produit — `EAProducts` (1 produit)

Suit les pages vues produit et alimente le rapport « Acquisition & Performance produit ». Un marqueur est considéré comme page produit quand il contient **exactement une** référence produit et aucun des paramètres `scart`, `estimate`, `ref`, `amount`.

**Méthodes :**

- `Builder(String path)` — nom de la page **(obligatoire)**
- `setUID(String uid)` — ID interne de l'utilisateur connecté
- `setEmail(String email)` — email de l'utilisateur
- `setProfile(String profile)` — profil de l'utilisateur
- `setPageGroup(String group)` — groupe de pages pour le reporting
- `addProduct(Product product)` — ajoute la référence produit **(obligatoire)**
- `set(String key, String value)` — paramètre personnalisé libre

**Construction d'un `Product` :**

- `Product.Builder(String ref)` — référence produit **(obligatoire)**
- `setName(String name)` — nom lisible du produit pour le reporting
- `setGroup(String group)` — groupe de marge (`A` ou `B`)
- `setParams(Params params)` — catégories produit (ex. `prdparam-category`, `prdparam-brand`)

**Construction d'un `Params` :**

- `addParam(String key, String value)` — ajoute une catégorie produit ; la clé correspond au suffixe après `prdparam-`

**Exemple :**

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

### Page catégorie — `EAProducts` (N produits)

Envoie les N premières références produit affichées sur une page de résultats/catégorie aux partenaires pour le retargeting. Un marqueur est considéré comme page catégorie quand il contient **plus d'une** référence produit et aucun des paramètres `scart`, `estimate`, `ref`, `amount`.

**Méthodes :**

- `Builder(String path)` — nom de la page **(obligatoire)**
- `setUID(String uid)` — ID interne de l'utilisateur connecté
- `setEmail(String email)` — email de l'utilisateur
- `setPageGroup(String group)` — groupe de pages pour le reporting
- `addProduct(Product product)` — ajoute une référence produit ; répétez pour chaque produit affiché
- `set(String key, String value)` — paramètre personnalisé libre

**Exemple :**

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

### Page moteur de recherche — `EASearch`

Suit les requêtes du moteur de recherche interne, le nombre de résultats et les paramètres de recherche personnalisés. Un marqueur est considéré comme page moteur quand il contient `isearchengine`. Non exclusif : peut être combiné avec une référence produit sur le même tag.

**Méthodes :**

- `Builder(String path, String engineName)` — nom de page + nom du moteur de recherche **(obligatoire)**
- `setResults(int results)` — nombre de résultats retournés par la recherche
- `setParams(Params params)` — paramètres de recherche supplémentaires (couples clé/valeur correspondant à `isearchkey`/`isearchdata`)
- `setUID(String uid)` — ID interne de l'utilisateur connecté
- `set(String key, String value)` — paramètre personnalisé libre

**Exemple :**

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

### Page d'erreur 404 — `EAProperties`

Signalez une page d'erreur 404 en ajoutant `set("error", "1")` à n'importe quel tag. Alimente le rapport « Pages en erreur ».

**Méthodes :**

- `Builder(String path)` — nom de la page **(obligatoire)**
- `set("error", "1")` — flag la page comme erreur 404 **(obligatoire)**

**Exemple :**

```java
EAProperties errorTag = new EAProperties.Builder("erreur|404")
        .set("error", "1")
        .build();
EAnalytics.getInstance().track(errorTag);
```

---

### Page devis — `EAEstimate`

Suit les devis et les produits associés. Un marqueur est considéré comme page de devis quand il contient les paramètres `ref` et `estimate` — tous deux définis automatiquement par le constructeur. Les devis sont dédoublonnés par référence : un second appel avec la même référence est ignoré.

**Méthodes :**

- `Builder(String path, String ref)` — nom de page + référence unique du devis **(obligatoire)**
- `setAmount(double amount)` — montant total TTC du devis (décimales séparées par un point)
- `setType(String type)` — type de devis selon votre propre référentiel
- `setCurrency(String currency)` — devise si différente de celle configurée dans l'interface
- `addProduct(Product product, double amount, int quantity)` — ajoute un produit avec son montant unitaire et sa quantité
- `setUID(String uid)` — ID interne de l'utilisateur connecté
- `setEmail(String email)` — email de l'utilisateur
- `set(String key, String value)` — paramètre personnalisé libre

**Exemple :**

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

### Page panier — `EACart`

Suit les paniers commencés et permet le calcul des taux de conversion et d'abandon. Un marqueur est considéré comme page panier quand il contient le paramètre `scart` — défini automatiquement par le constructeur. Durée de vie d'un panier : 30 minutes glissantes.

**Méthodes :**

- `Builder(String path)` — nom de la page **(obligatoire)**
- `setCartCumul(boolean cumul)` — `false` : les produits du tag représentent l'intégralité du panier (snapshot) ; `true` : les produits s'accumulent à chaque appel successif
- `addProduct(Product product, double amount, int quantity)` — ajoute un produit avec son montant unitaire et sa quantité
- `setUID(String uid)` — ID interne de l'utilisateur connecté
- `setEmail(String email)` — email de l'utilisateur
- `setProfile(String profile)` — profil de l'utilisateur
- `setPageGroup(String group)` — groupe de pages pour le reporting
- `set(String key, String value)` — paramètre personnalisé libre

**Exemple :**

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

### Page commande — `EAOrder`

Suit les conversions et le ROI. Un marqueur est considéré comme commande quand il contient `ref` et `amount` sans `scart` ni `estimate`. Les commandes sont dédoublonnées par référence. Implémentez ce tag le plus tôt possible dans le tunnel de paiement pour ne pas le manquer si l'utilisateur ne revient pas sur le site après la plateforme de paiement.

**Méthodes :**

- `Builder(String path, String ref)` — nom de page + référence unique de la commande **(obligatoire)**
- `setAmount(double amount)` — montant total TTC hors frais de port **(obligatoire)**
- `setPayment(String payment)` — moyen de paiement utilisé (ex. `credit card`, `paypal`)
- `setType(String type)` — type de vente selon votre propre référentiel
- `setCurrency(String currency)` — devise si différente de celle configurée dans l'interface
- `setNewCustomer(boolean newCustomer)` — `true` : nouvel acheteur ; `false` : client fidèle
- `setEstimateRef(String ref)` — référence du devis associé à cette commande (optionnel)
- `addProduct(Product product, double amount, int quantity)` — ajoute un produit avec son montant unitaire et sa quantité
- `setUID(String uid)` — ID interne de l'utilisateur connecté
- `setEmail(String email)` — email de l'utilisateur
- `setProfile(String profile)` — profil de l'utilisateur
- `set(String key, String value)` — paramètre personnalisé libre

**Exemple :**

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

### Tracking marchandisage — `EATpView` / `EATpClick`

Suit les impressions et clics sur les blocs de marchandisage (listes de recommandation, bannières, etc.). Envoyés en **GET** sur `/tpview/` et `/tpclick/` — contrairement aux autres tags envoyés en POST. Supporte le même mécanisme de retry offline que le reste du SDK.

**Méthodes `EATpView` (impression) :**

- `Builder(String path)` — page de référence **(obligatoire)**
- `setSiteName(String name)` — nom du site marchand
- `setCampaign(String campaign)` — nom de la campagne
- `setPlacement(String placement)` — emplacement du bloc dans la page
- `addProduct(String ref, Integer position)` — référence produit et sa position dans le bloc ; répétez pour chaque produit affiché
- `setUrl(String url)` — URL de contexte
- `setPublisher(String publisher)` — éditeur (optionnel)
- `setMedia(String media)` — type de média (optionnel)
- `setCategory(String category)` — catégorie du bloc (optionnel)

**Méthodes `EATpClick` (clic) :**

- `Builder(String path)` — page de référence **(obligatoire)**
- `setSiteName(String name)` — nom du site marchand
- `setCampaign(String campaign)` — nom de la campagne
- `setPlacement(String placement)` — emplacement du bloc dans la page
- `setProduct(String ref, int position)` — référence et position du produit cliqué **(obligatoire)**
- `setProduct(String ref, int position, Integer totalProducts)` — variante avec le nombre total de produits dans le bloc
- `setUrl(String url)` — URL de contexte
- `setPublisher(String publisher)` — éditeur (optionnel)
- `setMedia(String media)` — type de média (optionnel)
- `setCategory(String category)` — catégorie du bloc (optionnel)

**Exemple :**

```java
// Impression sur un bloc marchandisage
EATpView view = new EATpView.Builder("homepage")
        .setSiteName("my-site")
        .setCampaign("summer_sale")
        .setPlacement("banner_top")
        .addProduct("PROD_001", 0)
        .addProduct("PROD_002", 1)
        .setUrl("http://eulerian.net")
        .build();
EAnalytics.getInstance().track(view);

// Clic sur un produit du bloc
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

Associez un ou plusieurs context flags à n'importe quel tag pour enrichir vos rapports de dimensions contextuelles. Chaque flag accepte jusqu'à 10 valeurs.

**Méthodes `SiteCentricCFlag` :**

- `Builder()` — constructeur
- `set(String key, String... values)` — définit une dimension contextuelle avec une ou plusieurs valeurs (maximum 10)

**Ajout sur un tag via :**

- `setCFlag(SiteCentricCFlag flag)` — disponible sur tous les types de tags

**Exemple :**

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

Pour les applications hybrides ouvrant une WebView, passez l'identifiant interne du SDK dans l'URL d'ouverture pour assurer la continuité du tracking entre l'app native et la session web. Ajoutez aussi le paramètre `edev` pour qualifier le trafic comme natif (obligatoire pour le suivi des campagnes in-app et des notifications push).

```java
String euidl = EAnalytics.getEuidl();
// Construisez l'URL de la WebView :
// http://www.demo.fr/android-landing?ea-euidl-bypass=<euidl>&edev=AppNativeAndroidphone
```

Valeurs acceptées pour `edev` :

| Valeur | Appareil |
|--------|----------|
| `AppNativeAndroidphone` | Téléphone Android |
| `AppNativeAndroidtablet` | Tablette Android |
| `AppNativeIOSphone` | iPhone |
| `AppNativeIOStablet` | iPad |

> `ea-euidl-bypass` doit être présent à **chaque changement d'URL** dans la WebView, sinon le lien entre navigation app et web est perdu.

---

## Métriques application

Suivez les téléchargements et mises à jour en incluant ces paramètres dans le premier tag envoyé au lancement de l'application.

| Paramètre | Valeur | Description |
|-----------|--------|-------------|
| `ea-appname` | Nom de l'app | Identifiant app — **ne doit jamais changer** |
| `ea-appversion` | Chaîne de version | Version courante de l'application |
| `ea-appinstalled` | `1` | Présent si l'app existait avant l'intégration Eulerian |

**Logique système :**
- La métrique **Téléchargement** est incrémentée quand `ea-appname` est vu pour la première fois pour cet utilisateur (sans `ea-appinstalled`).
- La métrique **Mise à jour** est incrémentée quand `ea-appname` est inchangé mais `ea-appversion` a une nouvelle valeur par rapport au dernier lancement.

---

## Consentement (GDPR / RGPD)

> Les deux modes ci-dessous sont **mutuellement exclusifs**. Choisissez-en un et ne les utilisez jamais ensemble dans la même application.

### Mode 1 — TCF v2 (`gdpr_consent`)

Transmettez la TCString générée par votre CMP une seule fois par visiteur, au moment où l'utilisateur exprime son opt-in ou opt-out.

```java
EAProperties consentTag = new EAProperties.Builder("|univers|rubrique|page")
        .setUID("5434742")
        .set("abonnement", "mensuel")
        .set("gdpr_consent", "EADURF214345")  // votre TCString
        .build();
EAnalytics.getInstance().track(consentTag);
```

### Mode 2 — Catégories (`pmcat`)

Passez les IDs Eulerian des catégories **refusées**, séparés par `-`. Envoyez `-` seul si l'utilisateur accepte toutes les catégories.

```java
EAProperties consentTag = new EAProperties.Builder("|univers|rubrique|page")
        .setUID("5434742")
        .set("abonnement", "mensuel")
        .set("pmcat", "1-10")  // IDs des catégories refusées
        .build();
EAnalytics.getInstance().track(consentTag);
```

Exemple de mappage des catégories :

| Catégorie | ID Eulerian |
|-----------|-------------|
| analytics | 1 |
| publicité | 10 |
| fonctionnel | 19 |

*Exemple : l'utilisateur refuse analytics + publicité → `pmcat` = `1-10`*

### URL d'opt-out général

Fournissez cette URL sur votre page « Vie privée / RGPD » ou dans votre CMP pour un retrait total inconditionnel :

```
https://<votre-domaine-de-collecte>/optout.html?url=<votre-domaine>
```

### Récapitulatif

| | Mode TCF v2 | Mode Catégories |
|---|---|---|
| **Paramètre** | `gdpr_consent` | `pmcat` |
| **Quand** | Lors de l'opt-in/out | Lors de l'opt-in/out |
| **Contenu** | TCString | IDs refusés ou `-` |
| **Compatibilité CMP** | CMP TCF v2 | CMP maison / non-TCF |

---

## Dépendances

Les dépendances suivantes sont incluses automatiquement avec le SDK :

```groovy
com.google.android.gms:play-services-ads-identifier:17.0.0
com.android.installreferrer:installreferrer:2.2
```
