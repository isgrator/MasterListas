package org.imgracian.masterlistas;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class ListasActivity extends AppCompatActivity {
    //implements NavigationView.OnNavigationItemSelectedListener {
    private FlowingDrawer mDrawer;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private AdView adView;
    private InterstitialAd interstitialAd;
    private RewardedVideoAd ad;
    private IInAppBillingService serviceBilling;
    private ServiceConnection serviceConnection;
    private final String ID_ARTICULO = "org.imgracian.masterlistas.organizador.listas.producto";
    private final int INAPP_BILLING = 1;
    private final String developerPayLoad = "información adicional";
    private final String ID_SUSCRIPCION =
            "org.imgracian.masterlistas.organizador.listas.suscripcion";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas);

        //Para comprar artículo
        serviceConectInAppBilling();

        //Código para el vídeo bonificado
        ad = MobileAds.getRewardedVideoAdInstance(this);
        ad.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override public void onRewardedVideoAdLoaded() {
                Toast.makeText(ListasActivity.this,"Vídeo Bonificado cargado",
                        Toast.LENGTH_SHORT).show();
            }
            @Override public void onRewardedVideoAdOpened() {}
            @Override public void onRewardedVideoStarted() {}
            @Override public void onRewardedVideoAdClosed() {
                ad.loadAd("ca-app-pub-5594373787368665/7840874966", new AdRequest
                        .Builder().build());
                        //.addTestDevice("ID_DISPOSITIVO_FISICO_TEST").build());
            }
            @Override public void onRewarded(RewardItem rewardItem) {
                Toast.makeText(ListasActivity.this, "onRewarded: moneda virtual: " +
                                rewardItem.getType() + "  aumento: " + rewardItem.getAmount(),
                        Toast.LENGTH_SHORT).show();
            }
            @Override public void onRewardedVideoAdLeftApplication() {}
            @Override public void onRewardedVideoAdFailedToLoad(int i) {}
        });

        //Carga del video bonificado
        ad.loadAd("ca-app-pub-5594373787368665/7840874966", new AdRequest.Builder().build());
                //.addTestDevice("ID_DISPOSITIVO_FISICO_TEST").build());

        showCrossPromoDialog();

        //Anuncio intersticial
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-5594373787368665/5049515509");
        interstitialAd.loadAd(new AdRequest.Builder().build());
                //.addTestDevice("ID_DISPOSITIVO_FISICO_TEST").build());
        //Escuchador que carga el anuncio cuando se solicite
        interstitialAd.setAdListener(new AdListener() {
            @Override public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder()
                        .addTestDevice("ca-app-pub-5594373787368665/5049515509").build());
            }
        });

        //Código que muestra el anuncio
        FloatingActionButton f= findViewById(R.id.fab);
        f.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    Toast.makeText(ListasActivity.this,
                            getString(R.string.anuncio_no_disponible), Toast.LENGTH_LONG).show();
                }
            }
        });

        //Para el banner de adMob
        /*adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);*/
        //en disp. físico
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
                //.addTestDevice("4F3D741664405A22822CC65D95D3DEEE").build();
        adView.loadAd(adRequest);


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Se presionó el FAB", Snackbar.LENGTH_LONG).show();
            }
        });*/

        //Inicializar los elementos
        List items = new ArrayList();

        items.add(new Lista(R.drawable.trabajo, getString(R.string.trabajo), 2));
        items.add(new Lista(R.drawable.casa, getString(R.string.personal), 3));

        //Para puntuar la app
        new RateMyApp(this).app_launched();

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new ListaAdapter(items);
        recycler.setAdapter(adapter);

        //Código para poder excuchar cada elemento del RecyclerView
        /*recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(ListasActivity.this, new
                        RecyclerItemClickListener.OnItemClickListener() {
                            @Override public void onItemClick(View v, int position) {
                                Toast.makeText(ListasActivity.this, "" + position,
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
        );*/
        recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(ListasActivity.this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                Intent intent = new Intent(ListasActivity.this,
                                        DetalleListaActivity.class);
                                intent.putExtra("numeroLista", position);
                                //startActivity(intent);
                                ActivityOptionsCompat options = ActivityOptionsCompat.
                                        makeSceneTransitionAnimation(ListasActivity.this,
                                                new Pair<View, String>(v.findViewById(R.id.imagen),
                                                        getString(R.string.transition_name_img)));
                                ActivityCompat.startActivity(ListasActivity.this, intent, options
                                        .toBundle());
                            }
                        })
        );

        //Código para asociar el Navigation Drawer a la barra de acciones
        // Toolbar
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Navigation Drawer (ver punto 5 de la pág.78)
        NavigationView navigationView =  findViewById(
                R.id.vNavigation);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_compartir:
                                compatirTexto("http://play.google.com/store/apps/details?id="
                                        + getPackageName());
                                break;
                            case  R.id.nav_compartir_lista:
                                compatirTexto("LISTA DE LA COMPRA: patatas, leche, huevos. ---- "+
                                        "Compartido por: http://play.google.com/store/apps/details?id="+
                                        getPackageName());
                                break;
                            case  R.id.nav_compartir_logo:
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                                        R.drawable.logo);
                                compatirBitmap(bitmap, "Compartido por: "+
                                        "http://play.google.com/store/apps/details?id="+getPackageName());
                                break;
                            case  R.id.nav_compartir_desarrollador:
                                compatirTexto(
                                        "https://play.google.com/store/apps/dev?id=7027410910970713274");
                                break;
                            case R.id.nav_1:
                                if (ad.isLoaded()) {
                                    ad.show();
                                }
                                break;
                            case R.id.nav_articulo_no_recurrente:
                                comprarProducto();
                                break;
                            case  R.id.nav_susbripcion:
                                comprarSuscripcion(ListasActivity.this);
                                break;
                            case  R.id.nav_consulta_inapps_disponibles:
                                getInAppInformationOfProducts();
                                break;
                            case  R.id.nav_consulta_subs_disponibles:
                                getSubscriptionInformationOfProducts();
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), menuItem.getTitle(),
                                        Toast.LENGTH_SHORT).show();
                        }
                        return false;

                    }
                });
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.toggleMenu();
            }
        });

        // Navigation Drawer antiguo
        /*DrawerLayout drawer = (DrawerLayout) findViewById(
                R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.drawer_open, R.string. drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(
                R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Transition lista_enter = TransitionInflater.from(this)
                .inflateTransition(R.transition.transition_lista_enter);
        getWindow().setEnterTransition(lista_enter);*/


    }

    @SuppressWarnings("StatementWithEmptyBody")
    /*@Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //No hace falta este método estando setNavigationItemSelectedListener
        int id = item.getItemId();
        if (id == R.id.nav_1) {
            // …
        } else if (id == R.id.nav_2) {
            // …
        } else if (id == R.id.nav_3) {
            // …
        }  // …
        DrawerLayout drawer = (DrawerLayout) findViewById(
                R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/

    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(
                R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/

        //Código para FlowingDrawer (pág.78, punto 7)
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    //Método para compartir URL de MasterListas (unidad 5)
    void compatirTexto(String texto) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(i, getString(R.string.selecciona_app)));
    }

    //método para compartir fichero y añadir URL
    void compatirBitmap(Bitmap bitmap, String texto) {
        // guardamos bitmap en el directorio cache
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream s = new FileOutputStream(cachePath+"/image.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, s);
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Obtenemos la URI usando el FileProvider
        File path = new File(getCacheDir(), "images");
        File file = new File(path, "image.png");
        Uri uri= FileProvider.getUriForFile(this,
                "org.imgracian.masterlistas.organizador.listas.fileprovider", file);
        //Compartimos la URI
        if (uri != null) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // temp permission for receiving app to read this file
            i.setDataAndType(uri,getContentResolver().getType(uri));
            i.putExtra(Intent.EXTRA_STREAM, uri);
            i.putExtra(Intent.EXTRA_TEXT, texto);
            startActivity(Intent.createChooser(i, getString(R.string.selecciona_app)));
        }
    }

    //Método para dialog de cross-promotion
    private void showCrossPromoDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat);
        dialog.setContentView(R.layout.dialog_crosspromotion);
        dialog.setCancelable(true);
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button boton = (Button) dialog.findViewById(R.id.buttonDescargar);
        boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "https://play.google.com/store/apps/details?" +
                                "id=com.mimisoftware.emojicreatoremoticonosemoticones")));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Método para comprar un artículo no recurrente
    public void serviceConectInAppBilling() {
        serviceConnection = new ServiceConnection() {
            @Override public void onServiceDisconnected(ComponentName name) {
                serviceBilling = null;
            }
            @Override public void onServiceConnected(
                    ComponentName name, IBinder service) {
                serviceBilling=IInAppBillingService.Stub.asInterface(service);
                checkPurchasedInAppProducts();
                checkPurchasedSubscriptions();
            }
        };
        Intent serviceIntent = new Intent(
                "com.android.vending.billing.InAppBillingService.BIND");   serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);}


    public void comprarProducto() {
        if (serviceBilling != null) {
            Bundle buyIntentBundle = null;
            try {
                buyIntentBundle= serviceBilling.getBuyIntent(3, getPackageName(),
                        ID_ARTICULO, "inapp", developerPayLoad);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            PendingIntent pendingIntent = buyIntentBundle
                    .getParcelable("BUY_INTENT");
            try {
                if(pendingIntent!=null) {
                    startIntentSenderForResult(pendingIntent.getIntentSender(),
                            INAPP_BILLING,new Intent(), 0, 0, 0);
                }
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "InApp Billing service not available",
                    Toast.LENGTH_LONG).show();
        }
    }

    //Para comprobar que se ha comprado el artículo
    @Override protected void onActivityResult(int requestCode, int resultCode,
                                              Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INAPP_BILLING: {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData =
                        data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra(
                        "INAPP_DATA_SIGNATURE");
                if (resultCode == RESULT_OK) {
                    try {
                        JSONObject jo = new JSONObject(purchaseData);
                        String sku = jo.getString("productId");
                        String developerPayload = jo.getString("developerPayload");
                        String purchaseToken = jo.getString("purchaseToken");
                        if (sku.equals(ID_ARTICULO)) {
                            Toast.makeText(this,"Compra completada",
                                    Toast.LENGTH_LONG).show();
                            backToBuy(purchaseToken);
                        } else if (sku.equals(ID_SUSCRIPCION)) {
                            Toast.makeText(this, "Suscripción correcta", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //Método para comprar un artículo recurrente
    public void backToBuy(String token){
        //IInAppBillingService serviceBilling = application.getServiceBilling();
        if (serviceBilling != null) {
            try {
                int response = serviceBilling.consumePurchase(
                        3, getPackageName(), token);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //Método para comprar una suscripción

    public void comprarSuscripcion(Activity activity) {
        if (serviceBilling != null) {
            Bundle buyIntentBundle = null;
            try {
                buyIntentBundle = serviceBilling.getBuyIntent(3, activity
                        .getPackageName(), ID_SUSCRIPCION, "subs", developerPayLoad);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            assert buyIntentBundle != null;
            PendingIntent pendingIntent =
                    buyIntentBundle.getParcelable("BUY_INTENT");

            try {
                assert pendingIntent != null;
                activity.startIntentSenderForResult(pendingIntent
                        .getIntentSender(), INAPP_BILLING, new Intent(), 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, "Servicio de suscripción no disponible",
                    Toast.LENGTH_LONG).show();
        }
    }

    //Método para consultar los artículos disponibles
    public void getInAppInformationOfProducts(){
        ArrayList<String> skuList = new ArrayList<String> ();
        skuList.add(ID_ARTICULO);
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        Bundle skuDetails;
        ArrayList<String> responseList;
        try {
            skuDetails = serviceBilling.getSkuDetails(3, getPackageName(),
                    "inapp", querySkus);
            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0) {
                responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                assert responseList != null;
                for (String thisResponse : responseList) {
                    JSONObject object = new JSONObject(thisResponse);
                    String ref = object.getString("productId");
                    System.out.println("InApp Reference: " + ref);
                    String price = object.getString("price");
                    System.out.println("InApp Price: " + price);
                }
            }
        } catch (RemoteException | JSONException e) {
            e.printStackTrace();
        }
    }

    //Método para consultar los artículos comprados
    private void checkPurchasedInAppProducts() {
        Bundle ownedItemsInApp = null;
        if (serviceBilling != null) {
            try {
                ownedItemsInApp = serviceBilling.getPurchases(3, getPackageName(),
                        "inapp",
                        null);
            }catch(RemoteException e)
            {
                e.printStackTrace();
            }

            int response = ownedItemsInApp.getInt("RESPONSE_CODE");
            System.out.println(response);
            if(response == 0)
            {
                ArrayList<String> ownedSkus = ownedItemsInApp
                        .getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList = ownedItemsInApp
                        .getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList = ownedItemsInApp
                        .getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken = ownedItemsInApp
                        .getString("INAPP_CONTINUATION_TOKEN");
                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);
                    System.out.println("Inapp Purchase data: " +
                            purchaseData);
                    System.out.println("Inapp Signature: " + signature);
                    System.out.println("Inapp Sku: " + sku);
                    if (sku.equals(ID_ARTICULO)) {
                        Toast.makeText(this,
                                "Inapp comprado: " + sku + "el dia " +
                                        purchaseData, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }


    //Método para conocer las suscripciones disponibles
    private void getSubscriptionInformationOfProducts(){
        ArrayList<String> skuListSubs = new ArrayList<String> ();
        skuListSubs.add(ID_SUSCRIPCION);
        Bundle querySkusSubs = new Bundle();
        querySkusSubs.putStringArrayList("ITEM_ID_LIST", skuListSubs);
        Bundle skuDetailsSubs;
        ArrayList<String> responseListSubs;
        try{
            skuDetailsSubs = serviceBilling.getSkuDetails(
                    3,getPackageName(),"subs",querySkusSubs);
            int responseSubs = skuDetailsSubs.getInt("RESPONSE_CODE");
            System.out.println(responseSubs);
            if (responseSubs == 0) {
                responseListSubs = skuDetailsSubs.getStringArrayList(
                        "DETAILS_LIST");
                assert responseListSubs != null;
                for (String thisResponse : responseListSubs) {
                    JSONObject object = new JSONObject(thisResponse);
                    String ref = object.getString("productId");
                    System.out.println("Subscription Reference: " + ref);
                    String price = object.getString("price");
                    System.out.println("Subscription Price: " + price);
                }
            }
        } catch (RemoteException | JSONException e) {
            e.printStackTrace();
        }
    }

    //Consultar suscripciones compradas
    private void checkPurchasedSubscriptions() {
        Bundle ownedItemsInApp = null;
        if (serviceBilling != null) {
            try {
                ownedItemsInApp = serviceBilling.getPurchases(
                        3, getPackageName(), "subs", null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            int response = ownedItemsInApp.getInt("RESPONSE_CODE");
            System.out.println(response);
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItemsInApp
                        .getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList = ownedItemsInApp
                        .getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList = ownedItemsInApp
                        .getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken = ownedItemsInApp
                        .getString("INAPP_CONTINUATION_TOKEN");
                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);
                    System.out.println("Suscription Purchase data: " +
                            purchaseData);
                    System.out.println("Suscription Signature: " + signature);
                    System.out.println("Suscription Sku: " + sku);
                    if (sku.equals(ID_SUSCRIPCION)) {
                        Toast.makeText(this, "Suscrito correctamente: " + sku +
                                "el dia " + purchaseData, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}

