package cz.fojtik.radek.countryviewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;

import cz.fojtik.radek.countryviewer.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabL;
    private  ViewPager mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize(){

        mView = (ViewPager)findViewById(R.id.pager);
        mView.setAdapter(new ViewPagerAdapter(this));
        mView.setCurrentItem(0);
        mView.setOffscreenPageLimit(2);
        mTabL = (TabLayout)findViewById(R.id.tabLayout);
        // adds view pager into tab layout
        mTabL.setupWithViewPager(mView);
        new PageHome(this);
    }
}
