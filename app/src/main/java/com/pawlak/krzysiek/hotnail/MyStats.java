package com.pawlak.krzysiek.hotnail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.android.json.JsonConverter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyStats extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ArrayList<Product> productsList;
    private ListView lvProduct;
    private FunDapter<Product> adapter;

    private RequestQueue requestQueue;
    private static final String URL = "http://sunpatrol.pe.hu/stat.php";
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_panel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), PhotoActivity.class);
                startActivity(in);
            }
        });

    // get email to look how vote user
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String email = getPrefs.getString("email", "empty");

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

//        onCreateOptionsMenu()

//        getSupportActionBar().setIcon(R.drawable.logout);

        ImageLoader.getInstance().init(UILConfig.config(MyStats.this));

//        PostResponseAsyncTask taskRead = new PostResponseAsyncTask(MyStats.this, this);
//        taskRead.execute("http://votenails.pe.hu/product.php");

        requestQueue = Volley.newRequestQueue(this);
        final Product product = (Product) getIntent().getSerializableExtra("product");

        lvProduct = (ListView) findViewById(R.id.lvProduct);
        lvProduct.setOnItemClickListener(this);

    // check your image vote if you done it before
        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//                Toast.makeText(MyStats.this, response, Toast.LENGTH_LONG).show();
                productsList = new JsonConverter<Product>().toArrayList(response, Product.class);
                BindDictionary<Product> dict = new BindDictionary<Product>();

//                dict.addStringField(R.id.tvRate, new StringExtractor<Product>() {
//                    @Override
//                    public String getStringValue(Product product, int position) {
//                        String a = "" + product.avg;
//                        if (a.contains("null")) {
//                            a = "0.000";
//                        }
//                        String b = a.substring(0, 3);
//                        return b + " / 5.0";
//                    }
//                });

                dict.addDynamicImageField(R.id.ivImage, new StringExtractor<Product>() {
                    @Override
                    public String getStringValue(Product product, int position) {
                        return product.url;
                    }
                }, new DynamicImageLoader() {
                    @Override
                    public void loadImage(String url, ImageView imageView) {
                        ImageLoader.getInstance().displayImage(url, imageView);
                        imageView.setPadding(0, 0, 0, 0);
                        imageView.setAdjustViewBounds(true);
                    }
                });

                dict.addStringField(R.id.tvRate, new StringExtractor<Product>() {
                    @Override
                    public String getStringValue(Product product, int position) {
                        return "" + product.image_name;
                    }
                });

                adapter = new FunDapter<>(MyStats.this, productsList, R.layout.layout_panel, dict);
//
                lvProduct = (ListView) findViewById(R.id.lvProduct);
                lvProduct.setAdapter(adapter);
//                lvProduct.setOnItemClickListener(this);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap();
                hashMap.put("user_email", email); // present user
                return hashMap;
            }
        };
        requestQueue.add(request);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent in = new Intent(getApplicationContext(), LoginSignActivity.class);
            savePreferences("email", "empty");
            savePreferences("password", "empty");
            startActivity(in);
            finish();
//            Toast.makeText(this, "Try Again", Toast.LENGTH_LONG).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor checkbox = sharedPreferences.edit();
        checkbox.putString(key, value);
        checkbox.commit();
    }

//    @Override
//    public void processFinish(String s) {
//        productsList = new JsonConverter<Product>().toArrayList(s, Product.class);
//
//        BindDictionary<Product> dict = new BindDictionary<Product>();
////        dict.addStringField(R.id.tvName, new StringExtractor<Product>() {
////            @Override
////            public String getStringValue(Product product, int position) {
////                return product.image_name;
////            }
////        });
//
////        dict.addStringField(R.id.tvPrice, new StringExtractor<Product>() {
////            @Override
////            public String getStringValue(Product product, int position) {
////                return "" + product.data_add;
////            }
////        });
//
//        dict.addStringField(R.id.tvRate, new StringExtractor<Product>() {
//            @Override
//            public String getStringValue(Product product, int position) {
//                String a = "" + product.avg;
//                if (a.contains("null")) {
//                    a = "0.000";
//                }
//                String b = a.substring(0, 3);
//                return b + " / 5.0";
//            }
//        });
//
//        dict.addDynamicImageField(R.id.ivImage, new StringExtractor<Product>() {
//            @Override
//            public String getStringValue(Product product, int position) {
//                return product.url;
//            }
//        }, new DynamicImageLoader() {
//            @Override
//            public void loadImage(String url, ImageView imageView) {
//                ImageLoader.getInstance().displayImage(url, imageView);
//                imageView.setPadding(0, 0, 0, 0);
//                imageView.setAdjustViewBounds(true);
//            }
//        });
//
//        adapter = new FunDapter<>(MyStats.this, productsList, R.layout.layout_list, dict);
//
//        lvProduct = (ListView) findViewById(R.id.lvProduct);
//        lvProduct.setAdapter(adapter);
//        lvProduct.setOnItemClickListener(this);
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Product selectedProduct = productsList.get(position);
        Intent in = new Intent(MyStats.this, testChart.class);
        in.putExtra("product", (Serializable) selectedProduct);
        startActivity(in);
    }


}
