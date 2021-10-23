package com.velvet.tz_reload;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    //список Названий всех валют
    List<String> loc_cur_codes = new ArrayList<String>();
    //список сегодняшних котировок по отношению к рублю
    List<Double> loc_cur_tod = new ArrayList<Double>();
    //список вчерашних котировок по отношению к рублю
    List<Double> loc_cur_tom = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        swipeDetector = new SwipeDetector(ViewConfiguration.get(this).getScaledTouchSlop()) {
            @Override
            public void onSwipeDetected(Direction direction) {
                switch (direction) {
                    case UP:
                        break;
                    case DOWN:
                        break;
                    case LEFT:
                        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case RIGHT:
                        break;
                    case UN_EXPT:
                        Log.d(TAG,"unexpected error");
                        break;
                    default:
                        Log.d(TAG, "something went wrong");

                }
            }
        };
        loc_cur_codes = Database.cur_codes;
        loc_cur_tod = Database.cur_tod;
        loc_cur_tom = Database.cur_tom;
        try {
            createTable();
        }
        catch (IndexOutOfBoundsException e) {
            Log.e("Error", "Vsyo slomalos'");
        }
    }
    public void createTable() {
        TableLayout tableLayout = findViewById(R.id.tablica);
        TextView zag1 = findViewById(R.id.zag1);
        TextView zag2 = findViewById(R.id.zag2);
        TextView zag3 = findViewById(R.id.zag3);
        zag1.setTextSize(16);
        zag2.setTextSize(16);
        zag3.setTextSize(16);
        zag1.setText("Валюта");
        zag2.setText("Цена в RUB");
        zag3.setText("Колебания за сутки");
        for (int i = 1; i < loc_cur_codes.size(); i++) {
            TableRow row = new TableRow(this);
            TextView codes = new TextView(this);
            TextView nominal = new TextView(this);
            TextView change = new TextView(this);
            codes.setText(loc_cur_codes.get(i));
            nominal.setText(loc_cur_tod.get(i).toString());
            DecimalFormat twoDForm = new DecimalFormat("00.00");
            double changeDif = (loc_cur_tod.get(i) / loc_cur_tom.get(i)) - 1.0;
            double changePercent = changeDif * 100.0;
            if (changePercent>=0.0) {

                row.setBackgroundColor(Color.rgb(30, 200, 30));
            }
            else {
                row.setBackgroundColor(Color.rgb(200, 30, 30));
            }
            change.setText(twoDForm.format(changePercent)+"%");
            row.addView(codes);
            row.addView(nominal);
            row.addView(change);
            tableLayout.addView(row);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeDetector.onTouchEvent(event);
    }
    public static SwipeDetector swipeDetector;
}