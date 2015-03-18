package com.eulerian.android.demo.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import com.eulerian.android.sdk.SiteCentricProperty;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private double latitude = 48.871835;
    private double longitude = 2.382430;
    private Product pasta = new Product.Builder("ref-rrr")
            .setParams(new Params.Builder()
                    .addParam("marque", "barilla")
                    .addParam("categorie", "frais")
                    .addParam("stock", 4)
                    .build())
            .setName("Pate fraiche")
            .build();
    private Product lardons = new Product.Builder("ref-lll")
            .setParams(new Params.Builder()
                    .addParam("marque", "herta")
                    .addParam("categorie", "lard")
                    .addParam("stock", 98)
                    .build())
            .setName("Lardons herta")
            .build();
    private EAProperties properties = new EAProperties.Builder("the_path")
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
                    .setReference("test-ref-\"fefds$`^")
                    .setIn("in-test")
                    .addOut(new String[]{"tata", "tutu", "tete"})
                    .build())
            .setProperty(new SiteCentricProperty.Builder()
                    .set("cle1", new String[]{"poisson", "viande"})
                    .set("cle2", "choucroute")
                    .build())
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
                    .addParam("toto", "value")
                    .addParam("titi", 3)
                    .addParam("tata", "value--ffd")
                    .addParam("tata", "value")
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
    }

    public void onClickProperties(View v) {
        EAnalytics.getInstance().track(properties);
    }

    public void onClickProducts(View v) {
        EAProducts products = new EAProducts.Builder("test-path")
                .setEmail("francoisrouault.fr@gmail.com")
                .setLocation(latitude, longitude)
                .addProduct(product1)
                .addProduct(product2)
                .build();

        EAnalytics.getInstance().track(products);
    }

    public void onClickSearch(View v) {
        EASearch search = new EASearch.Builder("search-path", "banana")
                .setParams(new Params.Builder()
                        .addParam("provenance", "martinique")
                        .addParam("color", "yellow")
                        .build())
                .setResults(432)
                .build();

        EAnalytics.getInstance().track(search);
    }

    public void onClickCart(View v) {
        EACart monPanier = new EACart.Builder("path-cart")
                .setCartCumul(true)
                .setRef("test-ref")
                .addProduct(moufle, 2, 42)
                .addProduct(bonnet, 2, -4)
                .build();

        EAnalytics.getInstance().track(monPanier);
    }

    public void onClickEstimate(View v) {
        EAEstimate monDevis = new EAEstimate.Builder("test-path", "test-ref", 43)
                .setAmount(432)
                .setCurrency(CurrencyISO.EUR)
                .setType("test-type")
                .addProduct(pasta, 32, 1)
                .addProduct(lardons, 3, 21)
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
                .addProduct(pasta, 32, 1)
                .addProduct(lardons, 3, 21)
                .build();

        EAnalytics.getInstance().track(maVente);
    }

}
