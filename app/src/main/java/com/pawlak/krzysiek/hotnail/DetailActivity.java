package com.pawlak.krzysiek.hotnail;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    TextView tvName, tvAvg, tvDate, tvVote;
    ImageView ivImage;
    RatingBar ratingBar;
    Button btnVote;

    private RequestQueue requestQueue;
    private static final String URL = "http://sunpatrol.pe.hu/vreq.php";
    private static final String URL2 = "http://sunpatrol.pe.hu/insert.php";
    private StringRequest request;
    private int rat;
    private String yet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // get email to look how vote user
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String email = getPrefs.getString("email", "empty");

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageLoader.getInstance().init(UILConfig.config(DetailActivity.this));

        final Product product = (Product) getIntent().getSerializableExtra("product");

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        tvName = (TextView) findViewById(R.id.tvName);
        tvAvg = (TextView) findViewById(R.id.tvAvg);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvVote = (TextView) findViewById(R.id.tvVote);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        btnVote = (Button) findViewById(R.id.btnVote);

        requestQueue = Volley.newRequestQueue(this);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                Toast.makeText(getApplicationContext(), String.valueOf(rating), Toast.LENGTH_SHORT).show();
                rat = (int) rating;
            }
        });

        // check your image vote if you done it before
        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                yet = response; // tmp for checking you already vote on it
                if (response.contains("null")) {
//                    Snackbar snackbar = Snackbar
//                            .make(coordinatorLayout, "you voted on it", Snackbar.LENGTH_LONG);
//                    snackbar.show();
                    ratingBar.setRating(0);
                } else if (response.contains("1")) {
                    ratingBar.setRating(1);
                    ratingBar.setEnabled(false);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "you voted on it", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (response.contains("2")) {
                    ratingBar.setRating(2);
                    ratingBar.setEnabled(false);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "you voted on it", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (response.contains("3")) {
                    ratingBar.setRating(3);
                    ratingBar.setEnabled(false);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "you voted on it", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (response.contains("4")) {
                    ratingBar.setRating(4);
                    ratingBar.setEnabled(false);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "you voted on it", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (response.contains("5")) {
                    ratingBar.setRating(5);
                    ratingBar.setEnabled(false);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "you voted on it", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    ratingBar.setRating(0);
                    ratingBar.setEnabled(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap();
                hashMap.put("image_id", product.image_id); // select picture
                hashMap.put("user_email", email); // present user
                return hashMap;
            }
        };
        requestQueue.add(request);

    // vote send/update button
        btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!yet.contains("null")) { // check you vote before
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Sorry! You have already voted", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                request = new StringRequest(Request.Method.POST, URL2, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "done", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "error", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap();
                        hashMap.put("image_id", product.image_id); // select picture
                        hashMap.put("user_email", email); // present user
                        hashMap.put("vote_rate", String.valueOf(rat));
                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        }); // end vote request

        if (product != null) {
            tvName.setText("name: " + product.image_name);
//            tvAvg.setText("rating: " + String.valueOf(product.avg).substring(0, 3));
            String a = "" + product.avg;
            if (a.contains("null")) {
                a = "0.000";
            }
            String b = a.substring(0, 3);
            tvAvg.setText("rating: " + b + " / 5.0");
            tvDate.setText("date add: " + product.data_add);
            tvVote.setText("votes: " + ((int) product.vote));

            ImageLoader.getInstance().displayImage(product.url, ivImage);
            ivImage.setPadding(0, 0, 0, 0);
            ivImage.setAdjustViewBounds(true);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        yet = null; //reset tmp
    }
}
