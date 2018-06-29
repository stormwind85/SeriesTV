package fr.eni.campus.series.seriestv;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import fr.eni.campus.series.seriestv.model.Episode;
import fr.eni.campus.series.seriestv.model.Saison;
import fr.eni.campus.series.seriestv.util.Constantes;
import fr.eni.campus.series.seriestv.util.UtilsGlobal;


public class DetailsSaisonActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Saison saison;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView textView;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private String imageForDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        enableLeftMenu();
        setNavigationViewListener();
        startRequestToAPI();
    }

    private void initLayout() {
        setContentView(R.layout.activity_details_saison);
        saison = (Saison)getIntent().getSerializableExtra("saison");
        imageForDetails = getIntent().getStringExtra("imageDetail");
        drawerLayout = findViewById(R.id.leftHamburgerMenu);
        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.saison);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.listEpisodes);
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
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void startRequestToAPI() {
        String url = Constantes.ENDPOINT + "/shows/episodes?id=" + saison.getSerie().getId() + "&season=" + saison.getNumber();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    textView.setText("Saison " + saison.getNumber());
                    try {
                        JSONArray episodes = response.getJSONArray("episodes");
                        for(int i = 0; i < episodes.length(); ++i) {
                            saison.getEpisodes().get(i).setNumber(episodes.getJSONObject(i).getInt("episode"));
                            saison.getEpisodes().get(i).setTitle(episodes.getJSONObject(i).getString("title"));
                            saison.getEpisodes().get(i).setSortieDate(Constantes.format.parse(episodes.getJSONObject(i).getString("date")));
                            saison.getEpisodes().get(i).setSaison(saison);
                        }
                        Collections.sort(saison.getEpisodes(), new Comparator<Episode>() {
                            @Override
                            public int compare(Episode episode, Episode t1) {
                                return episode.getNumber().compareTo(t1.getNumber());
                            }
                        });
                        initRecycler();
                        recyclerView.setAdapter(new EpisodeAdapter(saison.getEpisodes(), imageForDetails, new EpisodeAdapter.OnClick() {
                            @Override
                            public void onItemClickListener(Episode episode) {
                                Intent intent = new Intent(DetailsSaisonActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }));
                    } catch (JSONException e) {
                        Log.e("JSONException", e.getMessage());
                    } catch (ParseException e) {
                        e.printStackTrace();
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

        switch (itemId){
            case R.id.home2:
                activityClass = MainActivity.class;
                intent = new Intent(this, activityClass);
                break;
            case R.id.connexion:
                break;
            case R.id.account:
                activityClass = AccountActivity.class;
                intent = new Intent(this, activityClass);
                break;
            case R.id.lastUpdates:
                activityClass = LastUpdatesActivity.class;
                intent = new Intent(this, activityClass);
                break;
            case R.id.favorites:
                activityClass = FavoritesActivity.class;
                intent = new Intent(this, activityClass);
                break;
            default: return false;
        }

        if(activityClass != null && !this.equals(activityClass))
            if (intent != null)
                startActivity(intent);

        drawerLayout.closeDrawer(GravityCompat.START);
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
