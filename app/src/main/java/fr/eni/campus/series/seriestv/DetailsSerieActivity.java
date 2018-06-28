package fr.eni.campus.series.seriestv;

import android.content.Intent;
import android.content.res.Configuration;
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
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import fr.eni.campus.series.seriestv.model.Saison;
import fr.eni.campus.series.seriestv.model.Serie;

public class DetailsSerieActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String ENDPOINT = "https://api.betaseries.com";
    private static final String API_KEY = "54ec90b87704";
    private static final String API_TOKEN = "Bearer f17e68d82c20";

    private Serie serie;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ImageView imageView;
    private TextView textTitle;
    private TextView textDate;
    private RatingBar ratingBar;
    private TextView textNote;
    private TextView textDescription;
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
        setContentView(R.layout.activity_details_serie);
        serie = (Serie)getIntent().getSerializableExtra("serie");
        drawerLayout = findViewById(R.id.leftHamburgerMenu);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        imageView = findViewById(R.id.banner);
        textTitle = findViewById(R.id.title);
        textDate = findViewById(R.id.date);
        ratingBar = findViewById(R.id.rating);
        textNote = findViewById(R.id.note);
        textDescription = findViewById(R.id.description);
        recyclerView = findViewById(R.id.listSaisons);
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

    private void getImageForDetails() {
        String url = ENDPOINT + "/shows/pictures?id=" + serie.getId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray pictures = response.getJSONArray("pictures");
                        for(int i = 0; i < pictures.length() && imageForDetails == null; ++i) {
                            if(pictures.getJSONObject(i).getInt("width") > pictures.getJSONObject(i).getInt("height") &&
                                    (pictures.getJSONObject(i).getInt("width") == 768)) {
                                imageForDetails = pictures.getJSONObject(i).getString("url");
                            }
                        }
                        recyclerView.setAdapter(new SaisonAdapter(serie.getSaisons(), imageForDetails, new SaisonAdapter.OnClick() {
                            @Override
                            public void onItemClickListener(Saison saison) {
                                Intent intent = new Intent(DetailsSerieActivity.this, DetailsSaisonActivity.class);
                                intent.putExtra("saison", saison);
                                intent.putExtra("imageDetail", imageForDetails);
                                startActivity(intent);
                            }
                        }));
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
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("X-BetaSeries-Key", API_KEY);
                    headers.put("Authorization", API_TOKEN);
                    headers.put("X-BetaSeries-Version", "3.0");
                    return headers;
                }
        };
        SingletonRequestAPI.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void startRequestToAPI() {
        String url = ENDPOINT + "/shows/display?id=" + serie.getId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject show = response.getJSONObject("show");
                        float density = getResources().getDisplayMetrics().density;

                        textTitle.setText(serie.getTitle());
                        int heightBase = 100;
                        int widthBase = 50;
                        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            heightBase = 140;
                            widthBase = 200;
                        }
                        imageView.getLayoutParams().width = getWindow().getDecorView().getWidth() - widthBase;
                        imageView.getLayoutParams().height = (int)(heightBase * density + 0.5f);
                        imageView.requestLayout();
                        Picasso.get().load(show.getJSONObject("images").getString("banner")).fit().centerInside().into(imageView);
                        textDate.setText("Créée en " + show.getInt("creation"));
                        ratingBar.setRating(serie.getNote().floatValue());
                        textNote.setText(new DecimalFormat("#.#").format(serie.getNote()));
                        textDescription.setText(show.getString("description"));

                        initRecycler();
                        getImageForDetails();
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
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("X-BetaSeries-Key", API_KEY);
                headers.put("Authorization", API_TOKEN);
                headers.put("X-BetaSeries-Version", "3.0");
                return headers;
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
