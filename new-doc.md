# Intégration SDK Android Eulerian Analytics

> Section : **Installation & initialisation**  
> Cette page décrit l’installation du SDK Eulerian Analytics dans une application Android native.

---

## 1. Installation du SDK

### 1.1. Ajouter le dépôt JitPack

Le SDK Android Eulerian est distribué via JitPack.

#### Option A — Projet Android récent avec `settings.gradle` ou `settings.gradle.kts`

Pour les projets Android récents, ajoutez JitPack dans `dependencyResolutionManagement`.

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

#### Option B — Projet Android avec configuration `allprojects`

Pour les projets plus anciens, ajoutez JitPack dans le fichier `build.gradle` racine.

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

> Utiliser l’option adaptée à la structure Gradle du projet.  
> Ne pas déclarer JitPack deux fois si le dépôt est déjà présent.

---

### 1.2. Ajouter la dépendance SDK

Dans le fichier `build.gradle` du module applicatif, généralement `app/build.gradle`, ajouter la dépendance suivante :

```groovy
dependencies {
    implementation 'com.github.EulerianTechnologies:eanalytics-android:1.9.0'
}
```

> **Important** : ne pas utiliser l’ancienne syntaxe `compile`, qui est obsolète.  
> Ne pas utiliser l’ancien artefact `com.eulerian.android.sdk:eanalytics:1.9.0`.

---

## 2. Permissions Android

Dans le fichier `AndroidManifest.xml`, ajouter les permissions suivantes :

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

Ces permissions permettent au SDK :

- d’envoyer les appels de tracking ;
- de connaître l’état de la connexion réseau ;
- de gérer les mécanismes de retry et d’envoi différé lorsque l’application est offline.

---

## 3. Tracking d’installation via Install Referrer

Pour permettre le tracking des installations, ajouter le receiver suivant dans le fichier `AndroidManifest.xml` :

```xml
<receiver
    android:name="com.eulerian.android.sdk.InstallReferrerReceiver"
    android:exported="true">
    <intent-filter>
        <action android:name="com.android.vending.INSTALL_REFERRER" />
    </intent-filter>
</receiver>
```

Ce receiver permet au SDK de recevoir les informations liées à l’installation de l’application.


---

## 4. Initialisation du SDK

### 4.1. Initialiser Eulerian dans la classe `Application`

Avant tout appel de tracking, le SDK doit être initialisé une seule fois au démarrage de l’application.

L’initialisation doit être faite dans la méthode `onCreate()` d’une classe `Application`.

```java
import android.app.Application;
import com.eulerian.android.sdk.EAnalytics;

public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EAnalytics.init(this, "example.demo.com", false);
    }
}
```

Le deuxième argument correspond au domaine de collecte Eulerian.

Exemple générique :

```java
EAnalytics.init(this, "COLLECT_DOMAIN", false);
```

À remplacer par le domaine de collecte réel du client.

> La valeur du troisième paramètre doit rester `false`.

---

### 4.2. Déclarer la classe `Application` dans le manifeste

Dans `AndroidManifest.xml`, déclarer la classe `Application` utilisée pour initialiser Eulerian.

```xml
<application
    android:name=".DemoApp"
    ... >
</application>
```

Si l’application possède déjà une classe `Application`, ne pas en créer une seconde.  
Ajouter simplement l’initialisation Eulerian dans la méthode `onCreate()` existante.

---

## 5. Premier appel de test

Après initialisation du SDK, un premier appel de test peut être envoyé avec `EAProperties`.

```java
import com.eulerian.android.sdk.EAnalytics;
import com.eulerian.android.sdk.EAProperties;

EAProperties properties = new EAProperties.Builder("homepage")
        .build();

EAnalytics.getInstance().track(properties);
```

Le paramètre passé au constructeur de `EAProperties.Builder` correspond au `path`, c’est-à-dire au nom de page utilisé dans Eulerian.

Dans une application native, le `path` doit toujours être renseigné explicitement.  
Contrairement à un site web, il n’est pas déduit automatiquement depuis une URL.

---

## 6. Exemple de tag de recherche interne

Le SDK fournit des classes dédiées aux usages fréquents.  
Par exemple, `EASearch` permet de tracker une recherche interne.

```java
import com.eulerian.android.sdk.EAnalytics;
import com.eulerian.android.sdk.EASearch;
import com.eulerian.android.sdk.Params;

EASearch search = new EASearch.Builder("/search", "internal_search")
        .setParams(new Params.Builder()
                .addParam("query", "banana")
                .addParam("origin", "homepage")
                .build())
        .setResults(42)
        .build();

EAnalytics.getInstance().track(search);
```

---

## 7. Points de contrôle après installation

Après intégration, vérifier les points suivants :

- l’application compile correctement avec la dépendance JitPack ;
- le SDK est initialisé une seule fois au démarrage de l’application ;
- le domaine de collecte Eulerian est correct ;
- les permissions réseau sont présentes dans le manifeste ;
- le receiver Install Referrer est déclaré dans le manifeste ;
- un premier appel de tracking est bien émis ;
- le `path` est toujours renseigné sur les appels natifs ;
- les appels sont rejoués correctement après une période offline, si ce comportement est attendu.

---

## 8. Suite de la documentation

Les classes `EATpView` et `EATpClick` sont disponibles dans le SDK, mais elles seront documentées dans la section dédiée au **tagging plan**, avec les autres types de marqueurs.

Cette section d’installation se limite volontairement à :

- l’ajout du SDK ;
- les permissions Android ;
- le receiver Install Referrer ;
- l’initialisation ;
- un premier appel de test.
