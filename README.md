# Eulerian Analytics #

## Get started
(Tested with Android Studio 1.1.0)

Drag and drop Eulerian Analytics JAR library to you app/libs folder. Then, add the following dependencies to your app build.gradle.

```
#!groovy
dependencies {
   compile fileTree(dir: 'libs', include: ['*.jar'])
   compile 'com.google.android.gms:play-services:6.5.+'
   ...
}
```

In your AndroidManifest.xml, add the following permissions :

```
#!xml
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

Note that READ_PHONE_STATE has been considered as a 'Dangerous Permission' since Android 6, so it is no more mandatory for the SDK to work.

Also add the following declaration within the <application> element. This tracks the install referrer.

```
#!xml
<receiver
   android:name="com.eulerian.android.sdk.InstallReferrerReceiver"
   android:exported="true">
   <intent-filter>
      <action android:name="com.android.vending.INSTALL_REFERRER" />
   </intent-filter>
</receiver>
```

## Run Eulerian Analytics

Before using the SDK make sure to initialize it, in your Application onCreate() for instance.

```
#!xml
<!-- in your AndroidManifest.xml-->
<application
   android:name=".YourApp">
```

```
#!java
public class YourApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EAnalytics.init(this, "example.test.com", false);
    }

}
```
Now you can track any properties you want. The generic properties is EAProperties, and the SDK provides convenience classes for the most common usage (ie. EACart, EAEstimate, EAProducts, etc...)


```
#!java
EASearch search = new EASearch.Builder("/path-example", "banana")
                .setParams(new Params.Builder()
                        .addParam("provenance", "martinique")
                        .addParam("couleur", "jaune")
                        .build())
                .setResults(42)
                .build();

EAnalytics.getInstance().track(search)
```

## Tutorial screenshots

![Capture d’écran 2015-04-17 à 10.15.42.png](https://bitbucket.org/repo/kA6LdM/images/1900818051-Capture%20d%E2%80%99%C3%A9cran%202015-04-17%20%C3%A0%2010.15.42.png)

![Capture d’écran 2015-04-17 à 10.20.12.png](https://bitbucket.org/repo/kA6LdM/images/3850475813-Capture%20d%E2%80%99%C3%A9cran%202015-04-17%20%C3%A0%2010.20.12.png)

![Capture d’écran 2015-04-17 à 10.20.17.png](https://bitbucket.org/repo/kA6LdM/images/807569072-Capture%20d%E2%80%99%C3%A9cran%202015-04-17%20%C3%A0%2010.20.17.png)

![Capture d’écran 2015-04-17 à 10.23.54.png](https://bitbucket.org/repo/kA6LdM/images/275415870-Capture%20d%E2%80%99%C3%A9cran%202015-04-17%20%C3%A0%2010.23.54.png)