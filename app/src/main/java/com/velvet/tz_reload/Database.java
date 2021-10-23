package com.velvet.tz_reload;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Database {

    static public List<String> cur_codes = new ArrayList<String>();
    //список сегодняшних котировок по отношению к рублю
    static public List<Double> cur_tod = new ArrayList<Double>();
    //список вчерашних котировок по отношению к рублю
    static public List<Double> cur_tom = new ArrayList<Double>();

}