package br.usp.ime.checkattendance.utils;

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

}
