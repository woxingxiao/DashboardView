package com.xw.sample.dashboardviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DashboardView1 mDashboardView1;
    private DashboardView2 mDashboardView2;
    private DashboardView3 mDashboardView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDashboardView1 = (DashboardView1) findViewById(R.id.dashboard_view_1);
        mDashboardView2 = (DashboardView2) findViewById(R.id.dashboard_view_2);
        mDashboardView3 = (DashboardView3) findViewById(R.id.dashboard_view_3);
        final DashboardView dashboardView1 = (DashboardView) findViewById(R.id.dashboard_view_2_);
        DashboardView dashboardView3 = (DashboardView) findViewById(R.id.dashboard_view_3_);
        DashboardView dashboardView4 = (DashboardView) findViewById(R.id.dashboard_view_4_);

        mDashboardView1.setOnClickListener(this);
        mDashboardView2.setOnClickListener(this);
        mDashboardView3.setOnClickListener(this);

        dashboardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int ranValue = random.nextInt(dashboardView1.getMaxValue() - dashboardView1.getMinValue())
                        + dashboardView1.getMinValue(); // 在取值范围内随机产生一个数

                dashboardView1.setRealTimeValue(ranValue, true, 500);
            }
        });

        List<HighlightCR> highlight1 = new ArrayList<>();
        highlight1.add(new HighlightCR(210, 60, Color.parseColor("#03A9F4")));
        highlight1.add(new HighlightCR(270, 60, Color.parseColor("#FFA000")));
        dashboardView1.setStripeHighlightColorAndRange(highlight1);

        List<HighlightCR> highlight2 = new ArrayList<>();
        highlight2.add(new HighlightCR(170, 140, Color.parseColor("#607D8B")));
        highlight2.add(new HighlightCR(310, 60, Color.parseColor("#795548")));
        dashboardView3.setStripeHighlightColorAndRange(highlight2);

        dashboardView4.setRadius(110);
        dashboardView4.setArcColor(ContextCompat.getColor(this, android.R.color.black));
        dashboardView4.setTextColor(Color.parseColor("#212121"));
        dashboardView4.setBgColor(ContextCompat.getColor(this, android.R.color.white));
        dashboardView4.setStartAngle(150);
        dashboardView4.setPointerRadius(80);
        dashboardView4.setCircleRadius(8);
        dashboardView4.setSweepAngle(240);
        dashboardView4.setBigSliceCount(12);
        dashboardView4.setMaxValue(240);
        dashboardView4.setRealTimeValue(80);
        dashboardView4.setMeasureTextSize(14);
        dashboardView4.setHeaderRadius(50);
        dashboardView4.setHeaderTitle("km/h");
        dashboardView4.setHeaderTextSize(16);
        dashboardView4.setStripeWidth(20);
        dashboardView4.setStripeMode(DashboardView.StripeMode.OUTER);
        List<HighlightCR> highlight3 = new ArrayList<>();
        highlight3.add(new HighlightCR(150, 100, Color.parseColor("#4CAF50")));
        highlight3.add(new HighlightCR(250, 80, Color.parseColor("#FFEB3B")));
        highlight3.add(new HighlightCR(330, 60, Color.parseColor("#F44336")));
        dashboardView4.setStripeHighlightColorAndRange(highlight3);

        mDashboardView2.setCreditValueWithAnim(new Random().nextInt(600) + 350);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dashboard_view_1:
                mDashboardView1.setRealTimeValue(new Random().nextInt(100));

                break;
            case R.id.dashboard_view_2:
                mDashboardView2.setCreditValueWithAnim(new Random().nextInt(950 - 350) + 350);

                break;
            case R.id.dashboard_view_3:
                mDashboardView3.setCreditValue(new Random().nextInt(950 - 350) + 350);

                break;
        }
    }

}
