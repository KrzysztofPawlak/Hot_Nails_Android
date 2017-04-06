package com.pawlak.krzysiek.hotnail;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Krzysiek on 2016-01-12.
 */


public class Splash extends Activity {

    boolean flaga;
    private RequestQueue requestQueue;
    private static final String URL = "http://sunpatrol.pe.hu/user_control.php";
    private StringRequest request;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

            SharedPreferences getPrefs = PreferenceManager
            .getDefaultSharedPreferences(this);
    SharedPreferences.Editor checkbox = getPrefs.edit();
//
//        if (checkbox == null) {
//            savePreferences("email", "dziablak@wp.pl");
//            savePreferences("password", "kris");
//        }
//        savePreferences("email", "dziablak@wp.pl");
//        savePreferences("password", "kris");

        requestQueue = Volley.newRequestQueue(this);

        // clear pref
//        SharedPreferences sharedPreferences = PreferenceManager
//                .getDefaultSharedPreferences(this);
//        SharedPreferences.Editor checkbox = sharedPreferences.edit();
//        checkbox.clear();
//        checkbox.commit();

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000);

                    synchronized (this) {
                        while (flaga) {
                            wait();
                        }
                    }
                } catch (InterruptedException e){
                    e.printStackTrace();
                }finally{
                    loginAlready();
//                    cameraRepair();
                    finish();

                }
            }
        };
        timer.start();
    }

    public void cameraRepair() {
        Intent intent = new Intent(Splash.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void loginAlready() { // checking log in/out from pref

        // take email and password from pref
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String email = getPrefs.getString("email", "empty");
        final String password = getPrefs.getString("password", "empty");

        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!response.contains("empty") && jsonObject.names().get(0).equals("success")) {
//                        Toast.makeText(getApplicationContext(), "SUCCESS " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Splash.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
//                        Toast.makeText(getApplicationContext(), "Error " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Splash.this, LoginSignActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("email", email);
                hashMap.put("password", password);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor checkbox = sharedPreferences.edit();
        checkbox.putString(key, value);
        checkbox.commit();
    }

    // back button
    int backButtonCount;
    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1) {
            finish();
        } else {
            Toast.makeText(this, "Przyciśnij przycisk jeszcze raz aby wyjść z aplikacji.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
            flaga = true;
        }
    }
}
