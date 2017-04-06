package com.pawlak.krzysiek.hotnail;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        mTabHost = getTabHost();
        TabHost.TabSpec spec, spec2, spec3, spec4;
        Intent intent;

        intent = new Intent(this, ListActivity.class);
        spec2 = mTabHost.newTabSpec("bebe")
                .setIndicator("hot")
                .setContent(intent);
        mTabHost.addTab(spec2);

        intent = new Intent(this, LatestActivity.class);
        spec4 = mTabHost.newTabSpec("Latest")
                .setIndicator("Latest")
                .setContent(intent);
        mTabHost.addTab(spec4);

        intent = new Intent(this, MyStats.class);
        spec3 = mTabHost.newTabSpec("3maj")
                .setIndicator("My stats")
                .setContent(intent);
        mTabHost.addTab(spec3);


    }
}