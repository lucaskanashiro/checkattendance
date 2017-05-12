package br.usp.ime.checkattendance.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by kanashiro on 5/11/17.
 */

public class Student {

    private String nusp;
    private String name;

    public Student(String nusp, String name) {
        this.nusp = nusp;
        this.name = name;
    }

    public String getNusp() {
            return this.nusp;
        }

    public void setNusp(String nusp) {
            this.nusp = nusp;
        }

    public String getName() {
            return name;
        }

    public void setName(String name) {
            this.name = name;
        }

    public String toString() {
        return "Nusp: " + this.nusp + "; Name: " + this.name;
    }

}
