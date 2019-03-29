# Android SDK for Eulerian Analytics #

## Instalación

Insertar el SDK de Android en la herramienta [Gradle](http://gradle.org/).

```groovy
dependencies {
   compile 'com.eulerian.android.sdk:eanalytics:1.7.0'
}
```

En el archivo AndroidManifest.xml, añadir los siguientes permisos :

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

Añadir también el siguiente enunciado en el elemento de la aplicación para hacer un seguimiento de la instalación del referrer.

```xml
<receiver
   android:name="com.eulerian.android.sdk.InstallReferrerReceiver"
   android:exported="true">
   <intent-filter>
      <action android:name="com.android.vending.INSTALL_REFERRER" />
   </intent-filter>
</receiver>
```

## Iniciar Eulerian Analytics

Antes de utilizar el SDK, asegúrate de iniciarlo mediante la función onCreate() como sigue.

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
Ahora es posible integrar todo tipo de tags acompañados de sus parámetros. Por ejemplo, el tag genérico se identifica mediante EAProperties. Los demás tipos de tags poseen cada uno una identificación específica (por ejemplo: EACart para la cesta, EAEstimate para el presupuesto, EAProducts para la categoría, etc.).

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

Los pasos de 1 a 3 se han reducido y puedes acceder a Eulerian Analytics sdk a través de Gradle

![Capture d’écran 2015-04-17 à 10.20.12.png](https://bitbucket.org/repo/kA6LdM/images/3850475813-Capture%20d%E2%80%99%C3%A9cran%202015-04-17%20%C3%A0%2010.20.12.png)

![Capture d’écran 2015-04-17 à 10.20.17.png](https://bitbucket.org/repo/kA6LdM/images/807569072-Capture%20d%E2%80%99%C3%A9cran%202015-04-17%20%C3%A0%2010.20.17.png)

![Capture d’écran 2015-04-17 à 10.23.54.png](https://bitbucket.org/repo/kA6LdM/images/275415870-Capture%20d%E2%80%99%C3%A9cran%202015-04-17%20%C3%A0%2010.23.54.png)

# Tagging plan

Link to [Android tagging plan](https://github.com/EulerianTechnologies/eanalytics-android/blob/master/TAGGINGPLAN_ES.md)
