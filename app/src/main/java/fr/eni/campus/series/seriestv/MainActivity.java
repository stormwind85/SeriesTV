package fr.eni.campus.series.seriestv;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.support.design.widget.NavigationView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.eni.campus.series.seriestv.model.Episode;
import fr.eni.campus.series.seriestv.model.Saison;
import fr.eni.campus.series.seriestv.model.Serie;
import fr.eni.campus.series.seriestv.model.Status;
import fr.eni.campus.series.seriestv.util.Constantes;
import fr.eni.campus.series.seriestv.util.UtilsGlobal;
import fr.eni.campus.series.seriestv.util.UtilsLogin;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static int TOTAL_LIMIT_SERIES = 100;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private List<Serie> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        enableLeftMenu();
        setNavigationViewListener();
        initRecycler();
        UtilsGlobal.initQueue(getCacheDir());
        startRequestToAPI();
    }

    private void initLayout() {
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.leftHamburgerMenu);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        progressBar = findViewById(R.id.begin_progress_bar);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void enableLeftMenu(){
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    // close drawer when item is tapped
                    drawerLayout.closeDrawers();
                    return true;
                }
            }
        );
        navigationView.setCheckedItem(R.id.home2);
        if(UtilsLogin.isUserLoggedIn(this)){
            MenuItem login = findViewById(R.id.connexion);
            MenuItem logout = findViewById(R.id.logout);
            MenuItem userInfos = findViewById(R.id.groupUserInfos);

            login.setVisible(false);
            logout.setVisible(true);
            userInfos.setVisible(true);
        }
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initRecycler() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()));
        recyclerView.setLayoutManager(layoutManager);
    }

    private void startRequestToAPI() {
        String url = Constantes.ENDPOINT + "/shows/list?order=followers&limit=" + TOTAL_LIMIT_SERIES;
        series = new LinkedList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray shows = response.getJSONArray("shows");
                        for(int i = 0; i < shows.length(); ++i) {
                            if(!shows.getJSONObject(i).getString("title").isEmpty() &&
                                    shows.getJSONObject(i).getInt("seasons") > 0 &&
                                    shows.getJSONObject(i).getInt("episodes") > 0 &&
                                    shows.getJSONObject(i).getJSONObject("images").getString("show") != null) {
                                Serie currentSerie = new Serie(
                                        shows.getJSONObject(i).getLong("id"),
                                        shows.getJSONObject(i).getString("title"),
                                        Status.valueOf(shows.getJSONObject(i).getString("status").toUpperCase()),
                                        shows.getJSONObject(i).getJSONObject("notes").getDouble("mean"),
                                        shows.getJSONObject(i).getJSONObject("images").getString("box"),
                                        shows.getJSONObject(i).getInt("creation"), new LinkedList<Saison>());

                                JSONArray saison_details = shows.getJSONObject(i).getJSONArray("seasons_details");
                                for(int j = 0; j < saison_details.length(); ++j) {
                                    int nbEpisode = saison_details.getJSONObject(j).getInt("episodes");
                                    Saison currentSaison = new Saison(
                                            saison_details.getJSONObject(j).getInt("number"),
                                            new LinkedList<Episode>(), currentSerie);
                                    for(int k = 0; k < nbEpisode; k++) {
                                        currentSaison.addEpisode(new Episode());
                                    }
                                    currentSerie.addSaison(currentSaison);
                                }
                                series.add(currentSerie);
                            } else
                                TOTAL_LIMIT_SERIES--;
                        }
                        recyclerView.setAdapter(new SerieAdapter(series, progressBar));
                    } catch (JSONException e) {
                        Log.e("JSONException", e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley", error.getMessage());
                }
            }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return UtilsGlobal.getHeaders();
            }
        };
        // Access the RequestQueue through your singleton class.
        SingletonRequestAPI.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Intent intent = null;
        Class activityClass = null;
        boolean logOut = false;

        switch (itemId){
            case R.id.home2:
                activityClass = this.getClass();
                break;
            case R.id.connexion:
                activityClass = LoginActivity.class;
                intent = new Intent(this,activityClass);
                break;
            case R.id.logout:
                activityClass = this.getClass();
                UtilsLogin.logout(this);
                Toast.makeText(this, getResources().getString(R.string.userLoggedOut),Toast.LENGTH_LONG);
                logOut = true;
                break;
            case R.id.account:
                activityClass = AccountActivity.class;
                intent = new Intent(this,AccountActivity.class);
                break;
            case R.id.lastUpdates:
                activityClass = LastUpdatesActivity.class;
                intent = new Intent(this,LastUpdatesActivity.class);
                break;
            case R.id.favorites:
                activityClass = FavoritesActivity.class;
                intent = new Intent(this, FavoritesActivity.class);
                break;
            default: return false;
        }

        if(activityClass != null && !this.equals(activityClass))
            if (intent != null)
                startActivity(intent);

        drawerLayout.closeDrawer(GravityCompat.START);

        if(logOut)
            enableLeftMenu();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.searchSerie));
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}
