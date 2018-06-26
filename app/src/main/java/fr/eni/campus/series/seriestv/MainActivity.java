package fr.eni.campus.series.seriestv;

import android.app.SearchManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.view.View;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.support.design.widget.NavigationView;

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
import com.github.pwittchen.infinitescroll.library.InfiniteScrollListener;

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

public class MainActivity extends AppCompatActivity {
    private static final String ENDPOINT = "https://api.betaseries.com";
    private static final String API_KEY = "54ec90b87704";
    private static final String API_TOKEN = "Bearer f17e68d82c20";
    private static final int MAX_ITEMS_PER_REQUEST = 3;
    private static final int SIMULATED_LOADING_TIME_IN_MS = 1500;
    private static int TOTAL_LIMIT_SERIES = 10;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ProgressBar beginProgressBar;
    private RecyclerView recyclerView;
    private static ProgressBar progressBar;
    private LinearLayoutManager layoutManager;

    private List<Serie> series;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        enableLeftMenu();
        initRecycler();
        initQueue();
        startRequestToAPI();
    }

    private void initLayout() {
        setContentView(R.layout.activity_main);
        beginProgressBar = findViewById(R.id.begin_progress_bar);
        drawerLayout = findViewById(R.id.leftHamburgerMenu);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R .id.progress_bar);
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

    private void initRecycler() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()));
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initQueue() {
        // Instantiate the RequestQueue.
        RequestQueue mRequestQueue;
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);
        // Start the queue
        mRequestQueue.start();
    }

    private void startRequestToAPI() {
        String url = ENDPOINT + "/shows/list?limit=" + TOTAL_LIMIT_SERIES;
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
                                        shows.getJSONObject(i).getString("status"),
                                        shows.getJSONObject(i).getJSONObject("images").getString("show"),
                                        shows.getJSONObject(i).getInt("creation"), new LinkedList<Saison>());

                                JSONArray saison_details = shows.getJSONObject(i).getJSONArray("seasons_details");
                                for(int j = 0; j < saison_details.length(); ++j) {
                                    Saison currentSaison = new Saison(
                                            saison_details.getJSONObject(j).getInt("number"),
                                            new LinkedList<Episode>(), currentSerie);
                                    currentSerie.addSaison(currentSaison);
                                }
                                series.add(currentSerie);
                            } else
                                TOTAL_LIMIT_SERIES--;
                        }
                        recyclerView.setAdapter(new SerieAdapter(series.subList(page, MAX_ITEMS_PER_REQUEST)));
                        recyclerView.addOnScrollListener(createInfiniteScrollListener());
                        beginProgressBar.setVisibility(View.GONE);
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
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("X-BetaSeries-Key", API_KEY);
                headers.put("Authorization", API_TOKEN);
                headers.put("X-BetaSeries-Version", "3.0");
                return headers;
            }
        };
        // Access the RequestQueue through your singleton class.
        SingletonRequestAPI.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private InfiniteScrollListener createInfiniteScrollListener() {
        return new InfiniteScrollListener(MAX_ITEMS_PER_REQUEST, layoutManager) {
            @Override public void onScrolledToEnd(final int firstVisibleItemPosition) {
                simulateLoading();
                int start = ++page * MAX_ITEMS_PER_REQUEST;
                final boolean allItemsLoaded = start >= series.size();
                if (allItemsLoaded) {
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    int end = TOTAL_LIMIT_SERIES;
                    if(start + MAX_ITEMS_PER_REQUEST <= TOTAL_LIMIT_SERIES)
                        end = start + MAX_ITEMS_PER_REQUEST;
                    final List<Serie> itemsLocal = getItemsToBeLoaded(start, end);
                    refreshView(recyclerView, new SerieAdapter(itemsLocal), firstVisibleItemPosition);
                }
            }
        };
    }

    private List<Serie> getItemsToBeLoaded(int start, int end) {
        List<Serie> newItems = series.subList(start, end);
        final List<Serie> oldItems = ((SerieAdapter) recyclerView.getAdapter()).getItems();
        final List<Serie> itemsLocal = new LinkedList<>();
        Log.i("getItemsToBeLoaded", "oldItems : " + oldItems.size());
        Log.i("getItemsToBeLoaded", "newItems : " + newItems.size());
        itemsLocal.addAll(oldItems);
        itemsLocal.addAll(newItems);
        return itemsLocal;
    }

    private static void simulateLoading() {
         new AsyncTask<Void, Void, Void>() {
            @Override protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(SIMULATED_LOADING_TIME_IN_MS);
                } catch (InterruptedException e) {
                    Log.e("simulateLoading", e.getMessage());
                }
                return null;
            }
            @Override protected void onPostExecute(Void param) {
                progressBar.setVisibility(View.GONE);
            }
        }.execute();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.searchSerie));
        return true;
    }
}
