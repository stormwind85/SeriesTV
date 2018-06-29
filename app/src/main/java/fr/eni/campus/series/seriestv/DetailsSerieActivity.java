package fr.eni.campus.series.seriestv;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
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
import android.view.View;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.eni.campus.series.seriestv.model.Saison;
import fr.eni.campus.series.seriestv.model.Serie;
import fr.eni.campus.series.seriestv.util.Constantes;
import fr.eni.campus.series.seriestv.util.UtilsGlobal;

public class DetailsSerieActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    private TextView textTrailer;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private String imageForDetails;
    private String trailer;

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
        textTrailer = findViewById(R.id.trailer);
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

    public void showTrailer(View view) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.split("\\?v=")[1]));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    private void getTrailer() {
        String url = Constantes.ENDPOINT + "/shows/videos?id=" + serie.getId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray videos = response.getJSONArray("videos");
                            List<String> trailers = new LinkedList<>();
                            for(int i = 0; i < videos.length(); ++i) {
                                if(videos.getJSONObject(i).getString("type").equals("trailer")) {
                                    trailers.add(videos.getJSONObject(i).getString("youtube_url"));
                                }
                            }
                            trailer = trailers.get(new Random().nextInt(trailers.size()));
                            textTrailer.setText("Regarder ici un trailer de la série");
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
        SingletonRequestAPI.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void getImageForDetails() {
        String url = Constantes.ENDPOINT + "/shows/pictures?id=" + serie.getId();
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
                    return UtilsGlobal.getHeaders();
                }
        };
        SingletonRequestAPI.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void startRequestToAPI() {
        String url = Constantes.ENDPOINT + "/shows/display?id=" + serie.getId();
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
                        getTrailer();
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
