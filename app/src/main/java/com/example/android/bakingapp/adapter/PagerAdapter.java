package com.example.android.bakingapp.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.bakingapp.StepDetailFragment;
import com.example.android.bakingapp.data.Step;

import java.util.List;

import static com.example.android.bakingapp.StepActivity.CURRENT_STEP;


/**
 * Created by merouane on 28/01/2018.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private List<Step> mSteps;

    public PagerAdapter(FragmentManager fm, List<Step> steps) {

        super(fm);
        this.mSteps = steps;

    }

    @Override
    public Fragment getItem(int position) {

        // pass the step to the fragment
        Bundle bundle = new Bundle();
        Step currentStep = mSteps.get(position);
        bundle.putParcelable(CURRENT_STEP, currentStep);

        // instantiating the fragment and giving it the arguments
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(bundle);

        return stepDetailFragment;
    }

    @Override
    public int getCount() {

        if(mSteps == null){
            return 0;
        }else{
            return mSteps.size();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position + "";
    }
}
