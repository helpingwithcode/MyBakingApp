package com.helpingiwthcode.mybakingapp.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.adapters.ViewPagerAdapter;
import com.helpingiwthcode.mybakingapp.fragments.IngredientsFragment;
import com.helpingiwthcode.mybakingapp.fragments.InstructionsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    private int recipeId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);
        setTabsLayout();
    }

    private void setTabsLayout() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupTabViews(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.setColo
    }

    private void setupTabViews(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new IngredientsFragment(), "Ingredients");
        viewPagerAdapter.addFragment(new InstructionsFragment(), "Instructions");
        viewPager.setAdapter(viewPagerAdapter);
    }

//    private void getRecipeIntent() {
//        Bundle extra = getIntent().getExtras();
//        if(extra != null){
//            recipeId = extra.getInt("recipeId",0);
//            Timber.e("RecipeId from intent: "+recipeId);
//            setupTabViews(viewPager);
//        }
//    }
}
