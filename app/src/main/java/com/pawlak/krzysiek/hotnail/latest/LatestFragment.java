package com.pawlak.krzysiek.hotnail.latest;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pawlak.krzysiek.hotnail.Product;
import com.pawlak.krzysiek.hotnail.R;
import com.pawlak.krzysiek.hotnail.UILConfig;
import com.pawlak.krzysiek.hotnail.detail.DetailActivity;
import com.pawlak.krzysiek.hotnail.image.PhotoActivity;

import java.util.ArrayList;

import static com.pawlak.krzysiek.hotnail.API_URL.SERVER;

public class LatestFragment extends Fragment implements AsyncResponse, AdapterView.OnItemClickListener {

    private static final String URL = SERVER + "latest.php";
    private ListView lvProduct;
    private ArrayList<Product> productsList = new ArrayList<>();
    private BindDictionary<Product> dict;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_latest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            Intent in = new Intent(getContext(), PhotoActivity.class);
            startActivity(in);
        });

        FunDapter<Product> adapter = new FunDapter<>(getContext(), productsList, R.layout.layout_latest, dict);

        lvProduct = view.findViewById(R.id.lvProduct);
        lvProduct.setAdapter(adapter);
        lvProduct.setOnItemClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageLoader.getInstance().init(UILConfig.config(getContext()));

        PostResponseAsyncTask taskRead = new PostResponseAsyncTask(getContext(), this);
        taskRead.execute(URL);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Product selectedProduct = productsList.get(position);
        Intent in = new Intent(getContext(), DetailActivity.class);
        in.putExtra("product", selectedProduct);
        startActivity(in);
    }

    @Override
    public void processFinish(String s) {
        productsList = new JsonConverter<Product>().toArrayList(s, Product.class);

        dict = new BindDictionary<>();

        dict.addStringField(R.id.tvRate, (product, position) -> "" + product.data_add);

        dict.addDynamicImageField(R.id.ivImage, (product, position) -> product.url, (url, imageView) -> {
            ImageLoader.getInstance().displayImage(url, imageView);
            imageView.setPadding(0, 0, 0, 0);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        });

        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof LatestFragment) {
            FragmentTransaction fragTransaction =   (getActivity()).getSupportFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commit();
        }
    }
}
