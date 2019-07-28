package com.pawlak.krzysiek.hotnail.stats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.android.json.JsonConverter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pawlak.krzysiek.hotnail.Product;
import com.pawlak.krzysiek.hotnail.R;
import com.pawlak.krzysiek.hotnail.UILConfig;
import com.pawlak.krzysiek.hotnail.testChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.pawlak.krzysiek.hotnail.API_URL.SERVER;

public class StatsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<Product> productsList = new ArrayList<>();
    private ListView lvProduct;
    private BindDictionary<Product> dict;
    private StringRequest request;
    private RequestQueue requestQueue;
    private SharedPreferences getPrefs;
    private static final String URL = SERVER + "stat.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String email = getPrefs.getString("email", "empty");

        ImageLoader.getInstance().init(UILConfig.config(getContext()));

        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(createRequest(email));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvProduct = view.findViewById(R.id.lvProduct);
        lvProduct.setOnItemClickListener(this);

        FunDapter<Product> adapter = new FunDapter<>(getContext(), productsList, R.layout.layout_panel, dict);
        lvProduct = view.findViewById(R.id.lvProduct);
        lvProduct.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Product selectedProduct = productsList.get(position);
        Intent in = new Intent(getContext(), testChart.class);
        in.putExtra("product", selectedProduct);
        startActivity(in);
    }

    private Request createRequest(final String email) {
        request = new StringRequest(Request.Method.POST, URL, response -> {
            productsList = new JsonConverter<Product>().toArrayList(response, Product.class);

            dict = new BindDictionary<>();
            dict.addDynamicImageField(R.id.ivImage, (product, position) -> product.url, (url, imageView) -> {
                ImageLoader.getInstance().displayImage(url, imageView);
                imageView.setPadding(0, 0, 0, 0);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            });
            dict.addStringField(R.id.tvRate, (product, position) -> "" + product.image_name);

            Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof StatsFragment) {
                FragmentTransaction fragTransaction = (getActivity()).getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
        }, error -> {
            // TODO:
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap();
                hashMap.put("user_email", email);
                return hashMap;
            }
        };
        return request;
    }
}
