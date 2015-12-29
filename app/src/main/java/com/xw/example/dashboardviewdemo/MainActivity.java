package com.xw.example.dashboardviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DashboardView dashboardView1 = (DashboardView) findViewById(R.id.dashboardView1);
        DashboardView dashboardView2 = (DashboardView) findViewById(R.id.dashboardView2);
        DashboardView dashboardView3 = (DashboardView) findViewById(R.id.dashboardView3);
        dashboardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboardView1.setRealTimeValueWithAnim(150.f);
            }
        });

        List<HighlightCR> highlight1 = new ArrayList<>();
        highlight1.add(new HighlightCR(210, 60, Color.parseColor("#03A9F4")));
        highlight1.add(new HighlightCR(270, 60, Color.parseColor("#FFA000")));
        dashboardView1.setStripeHighlightColorAndRange(highlight1);

        List<HighlightCR> highlight2 = new ArrayList<>();
        highlight2.add(new HighlightCR(170, 140, Color.parseColor("#607D8B")));
        highlight2.add(new HighlightCR(310, 60, Color.parseColor("#795548")));
        dashboardView2.setStripeHighlightColorAndRange(highlight2);

        dashboardView3.setRadius(110);
        dashboardView3.setArcColor(getResources().getColor(android.R.color.black));
        dashboardView3.setTextColor(Color.parseColor("#212121"));
        dashboardView3.setBgColor(getResources().getColor(android.R.color.white));
        dashboardView3.setStartAngle(150);
        dashboardView3.setPointerRadius(80);
        dashboardView3.setCircleRadius(8);
        dashboardView3.setSweepAngle(240);
        dashboardView3.setBigSliceCount(12);
        dashboardView3.setMaxValue(240);
        dashboardView3.setRealTimeValue(80);
        dashboardView3.setMeasureTextSize(14);
        dashboardView3.setHeaderRadius(50);
        dashboardView3.setHeaderTitle("km/h");
        dashboardView3.setHeaderTextSize(16);
        dashboardView3.setStripeWidth(20);
        dashboardView3.setStripeMode(DashboardView.StripeMode.OUTER);
        List<HighlightCR> highlight3 = new ArrayList<>();
        highlight3.add(new HighlightCR(150, 100, Color.parseColor("#4CAF50")));
        highlight3.add(new HighlightCR(250, 80, Color.parseColor("#FFEB3B")));
        highlight3.add(new HighlightCR(330, 60, Color.parseColor("#F44336")));
        dashboardView3.setStripeHighlightColorAndRange(highlight3);

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
