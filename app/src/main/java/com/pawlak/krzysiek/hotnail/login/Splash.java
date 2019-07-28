package com.pawlak.krzysiek.hotnail.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.pawlak.krzysiek.hotnail.data.HotNailService;
import com.pawlak.krzysiek.hotnail.MainActivity;
import com.pawlak.krzysiek.hotnail.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.pawlak.krzysiek.hotnail.API_URL.SERVER;

public class Splash extends Activity {

    boolean flaga;
    private HotNailService hotNailService;
    private SharedPreferences getPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String email = getPrefs.getString("email", "empty");
        final String password = getPrefs.getString("password", "empty");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        hotNailService = retrofit.create(HotNailService.class);

        // TODO: split login and registration
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                    synchronized (this) {
                        while (flaga) {
                            wait();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (checkIsLogOut(email, password)) {
                        Intent intent = new Intent(Splash.this, LoginSignActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        loginAlready(email, password);
                        finish();
                    }
                }
            }
        };
        timer.start();
    }

    private boolean checkIsLogOut(final String email, final String password) {
        return email.contains("empty") && password.contains("empty");
    }

    public void loginAlready(final String email, final String password) {
        Map<String, String> fields = new HashMap<>();
        fields.put("email", email);
        fields.put("password", password);

        Call<ResponseBody> call = hotNailService.logIn(email, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.names().get(0).equals("success")) {
                            Intent intent = new Intent(Splash.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(Splash.this, LoginSignActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // TODO: do something better
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // back button
    int backButtonCount;

    @Override
    public void onBackPressed() {
        if (backButtonCount >= 1) {
            finish();
        } else {
            Toast.makeText(this, "Przyciśnij przycisk jeszcze raz aby wyjść z aplikacji.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
            flaga = true;
        }
    }
}
