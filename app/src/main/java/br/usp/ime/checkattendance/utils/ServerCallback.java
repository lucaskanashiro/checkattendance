package br.usp.ime.checkattendance.utils;

/**
 * Created by kanashiro on 5/6/17.
 */

public interface ServerCallback {
    void onSuccess(String response);
    void onError();
}
