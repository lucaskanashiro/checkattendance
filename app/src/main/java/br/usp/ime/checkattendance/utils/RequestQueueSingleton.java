package br.usp.ime.checkattendance.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by kanashiro on 5/6/17.
 */

public class RequestQueueSingleton {

    private static RequestQueueSingleton singletonInstance;
    private static Context context;
    private RequestQueue requestQueue;

    private RequestQueueSingleton(Context ctx) {
        context = ctx;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return requestQueue;
    }

    public static synchronized RequestQueueSingleton getInstance(Context ctx) {
        if (singletonInstance == null) {
            singletonInstance = new RequestQueueSingleton(ctx.getApplicationContext());
        }

        return singletonInstance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
