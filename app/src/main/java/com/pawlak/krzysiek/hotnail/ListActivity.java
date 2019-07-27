package com.pawlak.krzysiek.hotnail;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;

import static com.pawlak.krzysiek.hotnail.API_URL.SERVER;

public class ListActivity extends AppCompatActivity implements AsyncResponse, AdapterView.OnItemClickListener {

    private ArrayList<Product> productsList;
    private ListView lvProduct;
    private FunDapter<Product> adapter;
    private static final String URL = SERVER + "/product.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), PhotoActivity.class);
                startActivity(in);
            }
        });

        ImageLoader.getInstance().init(UILConfig.config(ListActivity.this));

        PostResponseAsyncTask taskRead = new PostResponseAsyncTask(ListActivity.this, this);
        taskRead.execute(URL);

        lvProduct = (ListView) findViewById(R.id.lvProduct);

    }
    @Override
    public void processFinish(String s) {
        productsList = new JsonConverter<Product>().toArrayList(s, Product.class);


        BindDictionary<Product> dict = new BindDictionary<Product>();

        dict.addStringField(R.id.tvRate, new StringExtractor<Product>() {
            @Override
            public String getStringValue(Product product, int position) {
                String a = "" + product.avg;
                if (a.contains("null")) {
                    a = "0.000";
                }
                String b = a.substring(0, 3);
                return b + " / 5.0";
            }
        });

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
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });

        adapter = new FunDapter<>(ListActivity.this, productsList, R.layout.layout_list, dict);

        lvProduct = (ListView) findViewById(R.id.lvProduct);
        lvProduct.setAdapter(adapter);
        lvProduct.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Product selectedProduct = productsList.get(position);
        Intent in = new Intent(ListActivity.this, DetailActivity.class);
        in.putExtra("product", (Serializable) selectedProduct);
        startActivity(in);
    }
}
