package br.usp.ime.checkattendance.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import br.usp.ime.checkattendance.RegisterActivity;

/**
 * Created by kanashiro on 5/6/17.
 */

public class NetworkController {

    private final String domain = "http://207.38.82.139:8001/";

    public void register(String type, String name, String nusp, String passwd, Context context,
                         ServerCallback callback) {
        String url = this.domain + type + "/add";
        String params = "nusp=" + nusp + "&pass=" + passwd + "&name=" + name;
        this.post(url, params, context, callback);
    }

    public void login(String type, String nusp, String passwd, Context context,
                      ServerCallback callback) {
        String url = this.domain + "login/" + type;
        String params = "nusp=" + nusp + "&pass=" + passwd;
        this.post(url, params, context, callback);
    }

    public void getAllSeminars(final Context context , final ServerCallback callback) {
        String url = this.domain + "seminar";
        this.get(url, context, callback);
    }

    public void getStudentData(String nusp, Context context, ServerCallback callback){
        String url = this.domain + "student/get/" + nusp;
        this.get(url, context, callback);
    }

    public void getTeacherData(String nusp, Context context, ServerCallback callback){
        String url = this.domain + "teacher/get/" + nusp;
        this.get(url, context, callback);
    }

    public void updateStudentData(String nusp, String name, String passwd, Context context,
                                  ServerCallback callback) {
        String url = this.domain + "student/edit";
        String params = "nusp=" + nusp + "&name=" + name + "&pass=" + passwd;
        this.post(url, params, context, callback);
    }

    public void updateTeacherData(String nusp, String name, String passwd, Context context,
                                  ServerCallback callback) {
        String url = this.domain + "teacher/edit";
        String params = "nusp=" + nusp + "&name=" + name + "&pass=" + passwd;
        this.post(url, params, context, callback);
    }

    public void registerSeminar(String name, Context context, ServerCallback callback) {
        String url = this.domain + "seminar/add";
        String params = "name=" + name;
        this.post(url, params, context, callback);
    }

    public void getSeminar(String id, Context context, ServerCallback callback) {
        String url = this.domain + "seminar/get/" + id;
        this.get(url, context, callback);
    }

    public void updateSeminar(String id, String name, Context context, ServerCallback callback) {
        String url = this.domain + "seminar/edit";
        String params = "id=" + id + "&name=" + name;
        this.post(url, params, context, callback);
    }

    public void confirmAttendance(String nusp, String seminarId, Context context,
                                         ServerCallback callback) {
        String url = this.domain + "attendence/submit";
        String params = "nusp=" + nusp + "&seminar_id=" + seminarId;
        this.post(url, params, context, callback);
    }

    public void getAttendedSeminars(String nusp, Context context, ServerCallback callback) {
        String url = this.domain + "attendence/listSeminars";
        String params = "nusp=" + nusp;
        this.post(url, params, context, callback);
    }

    public void getAttendees(String seminarId, Context context, ServerCallback callback) {
        String url = this.domain + "attendence/listStudents";
        String params = "seminar_id=" + seminarId;
        this.post(url, params, context, callback);
    }

    public void getAllStudents(Context context, ServerCallback callback) {
        String url = this.domain + "student";
        this.get(url, context, callback);
    }

    private void get(String url, Context context, final ServerCallback callback) {

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
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }

    private void post(String url, final String params, Context context,
                      final ServerCallback callback) {

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
            String body = params;
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.getBytes();
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }
}
