package com.example.android.bakingapp;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.example.android.bakingapp.adapter.PagerAdapter;
import com.example.android.bakingapp.data.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepActivity extends AppCompatActivity {

    public static final String STEPS = "steps";

    public static final String CURRENT_STEP = "current-step";


    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        ButterKnife.bind(this);

        List<Step> steps = null;
        int currentStep = 1;

        if(getIntent() != null){
            currentStep = getIntent().getIntExtra(CURRENT_STEP, 1);
            steps = getIntent().getParcelableArrayListExtra(STEPS);
        }


        // Create an adapter that knows which fragment should be shown on each page
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), steps);

        mTabLayout.setupWithViewPager(mViewPager);

        // Set the adapter onto the view pager
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(currentStep);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
