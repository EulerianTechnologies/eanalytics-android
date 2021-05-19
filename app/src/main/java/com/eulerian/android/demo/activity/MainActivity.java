package com.eulerian.android.demo.activity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.eulerian.android.demo.R;
import com.eulerian.android.sdk.Action;
import com.eulerian.android.sdk.CurrencyISO;
import com.eulerian.android.sdk.EACart;
import com.eulerian.android.sdk.EAEstimate;
import com.eulerian.android.sdk.EAOrder;
import com.eulerian.android.sdk.EAProducts;
import com.eulerian.android.sdk.EAProperties;
import com.eulerian.android.sdk.EASearch;
import com.eulerian.android.sdk.EAnalytics;
import com.eulerian.android.sdk.Params;
import com.eulerian.android.sdk.Product;
import com.eulerian.android.sdk.SiteCentricCFlag;
import com.eulerian.android.sdk.SiteCentricProperty;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private double latitude = 48.871835;
    private double longitude = 2.382430;
    private Product pates = new Product.Builder("ref-rrr")
            .setParams(new Params.Builder()
                    .addParam("marque", "pasta4ever")
                    .addParam("categorie", "frais")
                    .addParam("stock", 4)
                    .build())
            .setName("Pates fraiches")
            .build();
    private Product lardons = new Product.Builder("ref-lll")
            .setParams(new Params.Builder()
                    .addParam("marque", "Miam Lardons")
                    .addParam("categorie", "viande")
                    .addParam("stock", 98)
                    .build())
            .setName("Lardons Premium")
            .build();
    private Product bonnet = new Product.Builder("ref-bonnet-rouge")
            .setName("spiderbonnet")
            .setParams(new Params.Builder()
                    .addParam("origin", "Belgique")
                    .addParam("tissu", "rouge")
                    .addParam("fait", "machine")
                    .build())
            .build();
    private Product moufle = new Product.Builder("ref-moufle-noire")
            .setName("aeromoufle")
            .setParams(new Params.Builder()
                    .addParam("origin", "France")
                    .addParam("tissu", "noir")
                    .addParam("fait", "main")
                    .build())
            .build();
    private Product product2 = new Product.Builder("test-reference2")
            .setName("test-name2")
            .setParams(new Params.Builder()
                    .addParam("param1", "value")
                    .addParam("param2", 3)
                    .addParam("param3", "value-3")
                    .addParam("param4", "value-4")
                    .build())
            .build();
    ;
    private Product product1 = new Product.Builder("test-reference1")
            .setName("test-name1")
            .setParams(new Params.Builder()
                    .addParam("marque", "mars1")
                    .addParam("categorie", "comestique1")
                    .addParam("stock", 1)
                    .addParam("taille", "43-1")
                    .build())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "EUIDL : " + EAnalytics.getEuidl());
    }

    public void onClickProperties(View v) {
        EAProperties properties = new EAProperties.Builder("the_path")
                .setNewCustomer(true)
                .setEmail("test-email")
                .setPageGroup("test-group")
                .setLocation(latitude, longitude)
                .setProfile("test-profile")
                .setUID("test-uid")
                .set("whatever", "...")
                .set("whatever1", "...")
                .set("whatever2", "...")
                .setAction(new Action.Builder()
                        .setReference("test-ref-\"fefds$432`^")
                        .setIn("in-test")
                        .addOut(new String[]{"tata", "tutu", "tete"})
                        .build())
                .setProperty(new SiteCentricProperty.Builder()
                        .set("cle1", new String[]{"poisson", "viande"})
                        .set("cle2", "choucroute")
                        .build())
                .setCFlag(new SiteCentricCFlag.Builder()
                        .set("categorie_1", "rolandgarros", "wimbledon")
                        .set("categorie_2", "tennis")
                        .set("categorie_3", "usopen")
                        .build())
                .build();
        EAnalytics.getInstance().track(properties);
    }

    public void onClickProducts(View v) {
        EAProducts products = new EAProducts.Builder("test-path")
                .setEmail("prenom.nom@mail.com")
                .setLocation(latitude, longitude)
                .addProduct(product1)
                .addProduct(product2)
                .build();

        EAnalytics.getInstance().track(products);
    }

    public void onClickSearch(View v) {
        EASearch search = new EASearch.Builder("search-path", "banane")
                .setParams(new Params.Builder()
                        .addParam("provenance", "martinique")
                        .addParam("couleur", "jaune")
                        .build())
                .setResults(432)
                .build();

        EAnalytics.getInstance().track(search);
    }

    public void onClickCart(View v) {
        EACart monPanier = new EACart.Builder("path-cart")
                .setCartCumul(true)
                .addProduct(moufle, 2.52, 42)
                .addProduct(bonnet, 2.123, -4)
                .build();

        EAnalytics.getInstance().track(monPanier);
    }

    public void onClickEstimate(View v) {
        EAEstimate monDevis = new EAEstimate.Builder("test-path", "test-ref")
                .setAmount(432)
                .setCurrency(CurrencyISO.EUR)
                .setType("test-type")
                .addProduct(pates, 32.111, 1)
                .addProduct(lardons, 3.99, 21)
                .build();

        EAnalytics.getInstance().track(monDevis);
    }

    public void onClickSale(View v) {
        EAOrder maVente = new EAOrder.Builder("test-path", "test-ref")
                .setAmount(432)
                .setCurrency(CurrencyISO.EUR)
                .setType("test-type")
                .setPayment("test-payment")
                .setEstimateRef("test-estimate-ref")
                .addProduct(pates, 32.32, 1)
                .addProduct(lardons, 3.01, 21)
                .build();

        EAnalytics.getInstance().track(maVente);
    }

}