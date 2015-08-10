package com.eulerian.android.demo.activity;

import android.support.v7.app.ActionBarActivity;

import com.eulerian.android.sdk.EACart;
import com.eulerian.android.sdk.EAEstimate;
import com.eulerian.android.sdk.EAOrder;
import com.eulerian.android.sdk.EAProducts;
import com.eulerian.android.sdk.EAProperties;
import com.eulerian.android.sdk.EASearch;
import com.eulerian.android.sdk.EAnalytics;
import com.eulerian.android.sdk.Params;
import com.eulerian.android.sdk.Product;

/**
 * Created by Francois Rouault on 07/08/2015.
 */
public class DocActivity extends ActionBarActivity {

    private void exempleTagGenerique() {
        //Tag générique:

        EAProperties genericTag = new EAProperties.Builder("|univers|rubrique|page")
                .setUID("5434742")
                .set("abonnement", "mensuel")
                .build();
        EAnalytics.getInstance().track(genericTag);
    }

    private void exempleTagProduit() {
        // Tag produit:

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
    }

    private void exempleTagPageDeResultat() {
        //Tag page de résultat:

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
    }

    private void exempleTagDeMoteurInterne() {
        //Tag de moteur interne:

        Params param1 = new Params.Builder()
                .addParam("motcle", "veste")
                .addParam("montant_min", "100.00")
                .addParam("montant_max", "400.00")
                .build();

        EASearch searchPage = new EASearch.Builder("Moteur_interne|veste", "moteur_interne")
                .setUID("34678")
                .setResults(150)
                .setParams(param1)
                .build();

        EAnalytics.getInstance().track(searchPage);
    }

    private void exempleTagPanier() {
        //Tag panier:

        Product produit1 = new Product.Builder("XY65643").build();
        Product produit2 = new Product.Builder("XV12345").build();

        EACart cartPage = new EACart.Builder("Panier")
                .setCartCumul(true)
                .setUID("57483")
                .addProduct(produit1, 65.00, 1)
                .addProduct(produit2, 20.00, 3)
                .build();

        EAnalytics.getInstance().track(cartPage);
    }

    private void exempleTagDevis() {
        //Tag devis:

        Product produit1 = new Product.Builder("505").build();

        EAEstimate estimatePage = new EAEstimate.Builder("Credit|devis", "C4536567")
                .setAmount(5000.00)
                .setType("Credit_48mois")
                .setUID("627253")
                .addProduct(produit1, 5000.00, 1)
                .build();

        EAnalytics.getInstance().track(estimatePage);
    }

    private void exempleTagDeCommande() {
        //Tag de commande:

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
    }

    private void exempleTagErreur() {
        //Tag d 'erreur:

        EAProperties errorTag = new EAProperties.Builder("erreur|404")
                .set("error", "1")
                .build();
        EAnalytics.getInstance().track(errorTag);
    }

}
