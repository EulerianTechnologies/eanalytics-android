package com.eulerian.android.demo.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.eulerian.android.demo.R;
import com.eulerian.android.sdk.Action;
import com.eulerian.android.sdk.EAProducts;
import com.eulerian.android.sdk.EAProperties;
import com.eulerian.android.sdk.EASearch;
import com.eulerian.android.sdk.EAnalytics;
import com.eulerian.android.sdk.Params;
import com.eulerian.android.sdk.Product;
import com.eulerian.android.sdk.SiteCentricProperty;


public class MainActivity extends ActionBarActivity {

    private double latitude = 48.871835;
    private double longitude = 2.382430;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickProperties(View v) {
        EAProperties properties = new EAProperties.Builder("the_path")
                .setPageNewCustomer(true)
                .setPageEmail("test-email")
                .setPageGroup("test-group")
                .setPageLocation(latitude, longitude)
                .setPageProfile("test-profile")
                .setPageUid("test-uid")
                .set("whatever", "...")
                .set("whatever1", "...")
                .set("whatever2", "...")
                .setAction(new Action.Builder()
                        .setReference("test-ref-\"fefds$`^")
                        .addIn(new String[]{"toto", "titi"})
                        .addOut(new String[]{"tata", "tutu", "tete"})
                        .build())
                .setProperty(new SiteCentricProperty.Builder()
                        .set("cle1", new String[]{"poisson", "viande"})
                        .set("cle2", "choucroute")
                        .build())
                .build();

        EAnalytics.getInstance().track(properties);

//        RESULT:
/*
{
   "uid":"test-uid",
   "ea-appname":"com.eulerian.android.demo",
   "ea-android-islat":"false",
   "property":{
      "cle2":[
         "choucroute"
      ],
      "cle1":[
         "poisson",
         "viande"
      ]
   },
   "ea-lat":"48.871835",
   "url":"http:\/\/com.eulerian.android.demo",
   "eos":"Android4.3",
   "ea-android-referrer":"utm_source=test_source&utm_medium=test_medium&utm_term=test_term&utm_content=te​st_content
   &utm_campaign=test_name",
   "email":"test-email",
   "path":"the_path",
   "action":{
      "in":[
         "toto",
         "titi"
      ],
      "ref":"test-ref-\"fefds$`^",
      "out":[
         "tata",
         "pouet"
      ]
   },
   "ea-android-adid":"eae34b48-7308-43e0-92f5-b4923b665855",
   "ea-lon":"2.38243",
   "newcustomer":"2",
   "pagegroup":"test-group",
   "ereplay-time":"1425942046",
   "ehw":"Genymotion Google Nexus 4 - 4.3 - API 18 - 768x1280",
   "euidl":"000000000000000",
   "profile":"test-profile"
}
*/
    }

    public void onClickProducts(View v) {

        Params paramsProduct1 = new Params.Builder()
                .addParam("marque", "mars1")
                .addParam("categorie", "comestique1")
                .addParam("stock", 1)
                .addParam("taille", "43-1")
                .build();

        Product product1 = new Product.Builder("test-reference1")
                .setName("test-name1")
                .setParams(paramsProduct1)
                .build();

        Product product2 = new Product.Builder("test-reference2")
                .setName("test-name2")
                .setParams(new Params.Builder()
                        .addParam("toto", "value")
                        .addParam("titi", 3)
                        .addParam("tata", "value--ffd")
                        .addParam("tata", "value")
                        .build())
                .build();

        EAProducts products = new EAProducts.Builder("test-path")
                .setPageEmail("francoisrouault.fr@gmail.com")
                .setPageLocation(latitude, longitude)
                .addProduct(product1)
                .addProduct(product2)
                .build();

        EAnalytics.getInstance().track(products);

        //RESULT
        /*
{
   "ea-appname":"com.eulerian.android.demo",
   "ea-android-islat":"false",
   "ea-lat":"48.871835",
   "url":"http:\/\/com.eulerian.android.demo",
   "eos":"Android4.3",
   "ea-android-referrer":"utm_source=test_source&utm_medium=test_medium&utm_term=test_term&utm_content=te​st_content
   &utm_campaign=test_name",
   "email":"francoisrouault.fr@gmail.com",
   "path":"test-path",
   "ea-android-adid":"eae34b48-7308-43e0-92f5-b4923b665855",
   "ea-lon":"2.38243",
   "products":[
      {
         "ref":"test-reference1",
         "params":{
            "taille":"43-1",
            "categorie":"comestique1",
            "stock":1,
            "marque":"mars1"
         },
         "name":"test-name1"
      },
      {
         "ref":"test-reference2",
         "params":{
            "tata":"value",
            "toto":"value",
            "titi":3
         },
         "name":"test-name2"
      }
   ],
   "ereplay-time":"1426111116",
   "ehw":"Genymotion Google Nexus 4 - 4.3 - API 18 - 768x1280",
   "euidl":"000000000000000"
}
         */
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

        //RESULT
        /*
{
   "eos":"Android4.3",
   "ea-android-referrer":"utm_source=test_source&utm_medium=test_medium&utm_term=test_term&utm_content=te​st_content
   &utm_campaign=test_name",
   "ea-appname":"com.eulerian.android.demo",
   "ea-android-islat":"false",
   "isearchengine":{
      "results":432,
      "params":{
         "color":"yellow",
         "provenance":"martinique"
      },
      "name":"banana"
   },
   "path":"search-path",
   "ea-android-adid":"eae34b48-7308-43e0-92f5-b4923b665855",
   "ereplay-time":"1426112277",
   "ehw":"Genymotion Google Nexus 4 - 4.3 - API 18 - 768x1280",
   "euidl":"000000000000000",
   "url":"http:\/\/com.eulerian.android.demo"
}
         */
    }

    public void onClickGetProductDetails(View v) {
//        EAProduct monProduit = new EAProduct.Builder()
//                .setPrice(50.34, "€")
//                .setPageLocation(latitude, longitude)
//                .setId("chaussure4314")
//                .build();

//        EAnalytics.getInstance().track(monProduit);

        // RESULT
//        {
//            "eos-type":"Android4.3",
//            "ea-appname":"com.eulerian.android.demo",
//            "epoch":"1425816371519",
//            "ea-currency":"€",
//            "ea-lat":"48.871835",
//            "ea-price":"50.34",
//            "ref-id":"chaussure4314",
//            "ea-lon":"2.38243",
//            "ehw":"Genymotion Google Nexus 4 - 4.3 - API 18 - 768x1280",
//            "euidl":"000000000000000",
//            "url":"http:\/\/com.eulerian.android.demo",
//            "property-type":"product"
//        }
    }

    public void onClickAddToCart(View v) {
//        EAProduct patesFraiches = new EAProduct.Builder()
//                .setId("pates_barilla_extrem_quality-432444")
//                .setPrice(5.23, "euros")
//                .build();

//        EAProduct lardons = new EAProduct.Builder()
//                .setId("lardons_herta-4321")
//                .setPrice(3.10, "euros")
//                .build();

//        EAProduct cremeFraiche = new EAProduct.Builder()
//                .setId("creme_fraiche_de_la_laitière-240003")
//                .setPrice(5.23, "euros")
//                .build();

//        EACart monPanier = new EACart.Builder()
//                .addProductsToCart(patesFraiches, lardons)
//                .addProductToCart(cremeFraiche)
//                .setPageLocation(latitude, longitude)
//                .build();

//        EAnalytics.getInstance().track(monPanier);

        // RESULT:
//        {
//            "eos-type":"Android4.3",
//            "items":[
//                {
//                    "ea-currency":"euros",
//                    "ref-id":"pates_barilla_extrem_quality-432444",
//                    "ea-price":"5.23",
//                    "epoch":"1425816372063",
//                    "property-type":"product"
//                },
//                {
//                    "ea-currency":"euros",
//                    "ref-id":"lardons_herta-4321",
//                    "ea-price":"3.1",
//                    "epoch":"1425816372065",
//                    "property-type":"product"
//                },
//                {
//                    "ea-currency":"euros",
//                    "ref-id":"creme_fraiche_de_la_laitière-240003",
//                    "ea-price":"5.23",
//                    "epoch":"1425816372067",
//                    "property-type":"product"
//                }
//            ],
//            "ea-appname":"com.eulerian.android.demo",
//            "epoch":"1425816372069",
//            "ea-lat":"48.871835",
//            "ea-lon":"2.38243",
//            "ehw":"Genymotion Google Nexus 4 - 4.3 - API 18 - 768x1280",
//            "euidl":"000000000000000",
//            "url":"http:\/\/com.eulerian.android.demo",
//            "property-type":"cart"
//        }
    }

}
