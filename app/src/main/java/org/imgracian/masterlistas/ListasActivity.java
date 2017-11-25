package org.imgracian.masterlistas;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.util.ArrayList;
import java.util.List;


public class ListasActivity extends AppCompatActivity {
    //implements NavigationView.OnNavigationItemSelectedListener {
    private FlowingDrawer mDrawer;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Se presionó el FAB", Snackbar.LENGTH_LONG).show();
            }
        });

        //Inicializar los elementos
        List items = new ArrayList();

        items.add(new Lista(R.drawable.trabajo, getString(R.string.trabajo), 2));
        items.add(new Lista(R.drawable.casa, getString(R.string.personal), 3));

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
                        Toast.makeText(getApplicationContext(), menuItem.getTitle(),
                                Toast.LENGTH_SHORT).show();
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

}
