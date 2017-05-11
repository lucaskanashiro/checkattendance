package br.usp.ime.checkattendance.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.usp.ime.checkattendance.models.Seminar;

/**
 * Created by kanashiro on 5/6/17.
 */

public class Parser {

    public static ArrayList<Seminar> parseSeminars(String response) throws JSONException{
        ArrayList<Seminar> seminars = new ArrayList<Seminar>();

        JSONObject json = new JSONObject(response);
        JSONArray data = json.getJSONArray("data");

        for(int i = 0; i < data.length(); i++) {
            JSONObject seminar = data.getJSONObject(i);
            Seminar s = new Seminar(seminar.getString("id"), seminar.getString("name"));
            seminars.add(s);
        }

        return seminars;
    }

    public static String parseAttendedSeminars(String seminars) throws JSONException {
        String result = "";

        JSONObject json = new JSONObject(seminars);
        JSONArray data = json.getJSONArray("data");

        for(int i = 0; i < data.length(); i++) {
            JSONObject seminar = data.getJSONObject(i);
            result = result + seminar.get("seminar_id") + " ";
        }

        return result;
    }

    public static Seminar parseSingleSeminar(String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        JSONObject data = json.getJSONObject("data");
        return new Seminar(data.getString("id"), data.getString("name"));
    }

    public static String parseData(String response, String field) throws JSONException {
        JSONObject json = new JSONObject(response);
        JSONObject data = json.getJSONObject("data");
        return data.getString(field);
    }

    public static ArrayList<Seminar> parseStringResponse(String seminarsString) {
        ArrayList<Seminar> seminarArrayList = new ArrayList<Seminar>();

        try {
            seminarArrayList = Parser.parseSeminars(seminarsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return seminarArrayList;
    }
}
