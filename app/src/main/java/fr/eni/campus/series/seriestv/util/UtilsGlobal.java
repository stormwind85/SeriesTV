package fr.eni.campus.series.seriestv.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UtilsGlobal {
    public static final Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("X-BetaSeries-Key", Constantes.API_KEY);
        headers.put("Authorization", Constantes.API_TOKEN);
        headers.put("X-BetaSeries-Version", "3.0");

        return headers;
    }

    public static void initQueue(File cacheDirectory) {
        // Instantiate the RequestQueue.
        RequestQueue mRequestQueue;
        // Instantiate the cache
        Cache cache = new DiskBasedCache(cacheDirectory, 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);
        // Start the queue
        mRequestQueue.start();
    }
}
