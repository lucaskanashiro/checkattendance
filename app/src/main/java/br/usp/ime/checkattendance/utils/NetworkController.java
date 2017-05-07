package br.usp.ime.checkattendance.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import br.usp.ime.checkattendance.RegisterActivity;

/**
 * Created by kanashiro on 5/6/17.
 */

public class NetworkController {

    public void register(String url, final String name, final String nusp, final String passwd,
                          final Context ctx, final ServerCallback callback) {

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("REGISTER", response +  "\n");
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError();
            }
        }
        ) {
            String body = "nusp=" + nusp + "&pass=" + passwd + "&name=" + name;
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.getBytes();
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        RequestQueueSingleton.getInstance(ctx).addToRequestQueue(request);
    }

    public void login(String url, final String nusp, final String passwd, Context ctx,
                      final ServerCallback callback) {

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LOGIN", response +  "\n");
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError();
            }
        }
        ) {
            String body = "nusp=" + nusp + "&pass=" + passwd;
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.getBytes();
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        RequestQueueSingleton.getInstance(ctx).addToRequestQueue(request);
    }

    public void getAllSeminars(final Context context , final ServerCallback callback)
    {
        String url = "http://207.38.82.139:8001/seminar";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError();

            }
        }){
            @Override
            public String getBodyContentType()
            {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getAttendedSeminars(final String nusp, final Context context,
                                    final ServerCallback callback) {

        String url = "http://207.38.82.139:8001/attendence/listSeminars";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError();

            }
        }){
            String body = "nusp=" + nusp;
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.getBytes();
            }
            @Override
            public String getBodyContentType()
            {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getStudentData(final String nusp, final Context ctx, final ServerCallback callback){
        String url = "http://207.38.82.139:8001/student/get/" + nusp;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError();

            }
        }){
            @Override
            public String getBodyContentType()
            {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        RequestQueueSingleton.getInstance(ctx).addToRequestQueue(request);
    }

    public void updateStudentData(final String nusp, final String name, final String passwd,
                                  final Context context, final ServerCallback callback) {

        String url = "http://207.38.82.139:8001/student/edit";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError();

            }
        }){
            String body = "nusp=" + nusp + "&name=" + name + "&pass=" + passwd;
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.getBytes();
            }
            @Override
            public String getBodyContentType()
            {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }
}
