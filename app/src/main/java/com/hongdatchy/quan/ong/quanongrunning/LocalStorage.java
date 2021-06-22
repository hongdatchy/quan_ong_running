package com.hongdatchy.quan.ong.quanongrunning;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocalStorage {


    public ArrayList<ArrayList<LatLng>> listOLists = new ArrayList<>();
    public List<Float> listDistance = new ArrayList<>();
    public List<Date> listDate = new ArrayList<>();

    public void update(ArrayList<LatLng> locationList, Context context){
        load(context);
        SharedPreferences mPrefs = context.getSharedPreferences("my_local_storage", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        listOLists.add(locationList);
        String json = new Gson().toJson(listOLists);
        prefsEditor.putString("listOfList", json);
        prefsEditor.apply();

        float s = (float)locationList.size()*10/1000;
        listDistance.add(s);
        json = new Gson().toJson(listDistance);
        prefsEditor.putString("listDistance", json);
        prefsEditor.apply();

        listDate.add(new Date());
        json = new Gson().toJson(listDate);
        prefsEditor.putString("listDate", json);
        prefsEditor.apply();
    }

    public void load(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_local_storage", Context.MODE_PRIVATE);
        String s = sharedPreferences.getString("listOfList", "");
        String s1 = sharedPreferences.getString("listDistance", "");
        String s2 = sharedPreferences.getString("listDate", "");
        if(!s.equals("") && !s1.equals("") && !s2.equals("")){
            listOLists = new Gson().fromJson(s, new TypeToken<ArrayList<ArrayList<LatLng>>>(){}.getType());
            listDistance = new Gson().fromJson(s1, new TypeToken<List<Float>>(){}.getType());
            listDate = new Gson().fromJson(s2, new TypeToken<List<Date>>(){}.getType());
        }

    }

}
