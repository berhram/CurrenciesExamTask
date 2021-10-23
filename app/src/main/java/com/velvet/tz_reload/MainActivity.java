package com.velvet.tz_reload;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView outputText;
    EditText inputText;
    Button convert;
    TextView updatedTime;
    Calendar calendar = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        swipeDetector = new SwipeDetector(ViewConfiguration.get(this).getScaledTouchSlop()) {
            @Override
            public void onSwipeDetected(Direction direction) {
                switch (direction) {
                    case DOWN:
                        break;
                    case UP:
                        refreshData();
                        Log.e(TAG,"Data refreshed");
                        break;
                    case LEFT:
                        break;
                    case RIGHT:
                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        startActivity(intent);
                        break;
                    case UN_EXPT:
                        Log.d(TAG,"unexpected error");
                        break;
                    default:
                        Log.d(TAG, "something went wrong");

                }
            }
        };
        updatedTime = findViewById(R.id.timer);
        refreshData();
        updatedTime.setTextSize(16);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.curnames, R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner1 =  findViewById(R.id.spinner1);
        Spinner spinner2 =  findViewById(R.id.spinner2);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        // выделяем элемент
        spinner1.setSelection(11);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                input =  parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                output= parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // устанавливаем обработчик нажатия
        convert = findViewById(R.id.button);
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculus();
            }
        });

        inputText = findViewById(R.id.vvod);
        outputText = findViewById(R.id.vivod);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeDetector.onTouchEvent(event);
    }

    public static SwipeDetector swipeDetector;

    //коллекция кодов всех валют
    List<String> cur_codes = new ArrayList<>();
    //список сегодняшних котировок по отношению к рублю
    List<Double> cur_tod = new ArrayList<>();
    //
    List<Double> cur_tom = new ArrayList<>();

    public static String URL = "https://www.cbr-xml-daily.ru/daily_json.js";

    public void refreshData() {
        calendar.setTime(new Date());
        updatedTime.setText("Данные были обновлены "+ calendar.get(Calendar.HOUR_OF_DAY) + " : " +calendar.get(Calendar.MINUTE)+ " : " + calendar.get(Calendar.SECOND));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //получаем массив Valute
                    cur_codes.clear();
                    cur_tod.clear();
                    cur_tom.clear();
                    cur_codes.add("RUB");
                    cur_tod.add(1.0);
                    cur_tom.add(1.0);
                    JSONObject valute = response.getJSONObject("Valute");
                    Iterator<String> iterator = valute.keys();
                    while (iterator.hasNext()) {
                        JSONObject temp = valute.getJSONObject(iterator.next());
                        cur_codes.add(temp.getString("CharCode"));
                        cur_tod.add(temp.getDouble("Value"));
                        cur_tom.add(temp.getDouble("Previous"));
                    }
                    Log.d("Rest Response", response.toString());
                    Database.cur_codes = cur_codes;
                    Database.cur_tod = cur_tod;
                    Database.cur_tom = cur_tom;
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error Rest Response", error.toString());
            }
        });
        requestQueue.add(objectRequest);
    }
    public void calculus() {
        try {
            double nominal = Double.parseDouble(inputText.getText().toString());
            double kurs_vvod = cur_tod.get(cur_codes.indexOf(input));
            double rub_eq_input = nominal * kurs_vvod;
            double kurs_vivod = cur_tod.get(cur_codes.indexOf(output));
            double itog = rub_eq_input / kurs_vivod;
            outputText.setText(Math.round(itog*100.0)/100.0+"");
        }
        catch (Exception e) {
            outputText.setText("Error");
            inputText.setText("");
        }
    }
    public static String input = "";
    public static String output = "";
}