package org.imgracian.masterlistas;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas);

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


}
