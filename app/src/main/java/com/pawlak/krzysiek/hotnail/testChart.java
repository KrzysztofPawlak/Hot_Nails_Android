package com.pawlak.krzysiek.hotnail;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kosalgeek.android.json.JsonConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class testChart extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static final String URL = "http://sunpatrol.pe.hu/rchart.php";
    private StringRequest request;
    public float rate1, rate2, rate3, rate4, rate5;
    private ArrayList<Points> pointsImage; // Points your images

    private FrameLayout mainLayout;
    private PieChart mChart;
    private String[] xData = {"rate 1", "rate 2", "rate 3", "rate 4", "rate 5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // check vote stats from db
        final Product product = (Product) getIntent().getSerializableExtra("product");
        requestQueue = Volley.newRequestQueue(this);

        // check your image vote if you done it before
        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

//                pointsImage = new JsonConverter<Points>().toArrayList(response, Points.class);
                pointsImage = new JsonConverter<Points>().toArrayList(response, Points.class);

                if(!response.contains("null")) {

                    // set 1-5 vote count
                    for(int i = 0; i < pointsImage.size(); i++) {
                        if(pointsImage.get(i).rate_id == 1) {
                            rate1 = pointsImage.get(i).count;
                        } else if(pointsImage.get(i).rate_id == 2) {
                            rate2 = pointsImage.get(i).count;
                        } else if(pointsImage.get(i).rate_id == 3) {
                            rate3 = pointsImage.get(i).count;
                        } else if(pointsImage.get(i).rate_id == 4) {
                            rate4 = pointsImage.get(i).count;
                        } else if(pointsImage.get(i).rate_id == 5) {
                            rate5 = pointsImage.get(i).count;
                        }

                    }
                } else {
                    Toast.makeText(testChart.this, "nobody vote on it", Toast.LENGTH_SHORT).show();
                }

                addData(rate1, rate2, rate3, rate4, rate5); // added value to chart
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
                return hashMap;
            }
        };
        requestQueue.add(request);

    // chart //
        mainLayout = (FrameLayout) findViewById(R.id.chartContainer);
        mChart = new PieChart(this);
        mainLayout.addView(mChart);
        mainLayout.setBackgroundColor(Color.rgb(250, 232, 232));
        mChart.setUsePercentValues(true);
        mChart.setDescription("My images vote");
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleRadius(7);
        mChart.setTransparentCircleRadius(0);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                if (entry == null)
                    return;
                Toast.makeText(testChart.this, xData[entry.getXIndex()] + " = " + ((int) entry.getVal())
                        + " votes", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

//        addData();

        Legend l  = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

    } // end onCreate

    private void addData(float rate1, float rate2, float rate3, float rate4, float rate5) { // to chart
        float[] yData = {rate1, rate2, rate3, rate4, rate5};
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        for(int i = 0; i < yData.length; i++)
            yVals1.add(new Entry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();
        for(int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        PieDataSet dataSet = new PieDataSet(yVals1, "My images vote");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for(int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);
        mChart.highlightValues(null);
        mChart.invalidate();
    } // end addData
}
