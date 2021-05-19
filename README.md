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
    implementation 'com.github.EulerianTechnologies:eanalytics-android:1.8.1'
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
