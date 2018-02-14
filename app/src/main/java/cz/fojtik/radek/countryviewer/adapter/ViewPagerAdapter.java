package cz.fojtik.radek.countryviewer.adapter;

import cz.fojtik.radek.countryviewer.R;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Radek on 21. 1. 2018.
 */

public class ViewPagerAdapter extends PagerAdapter{
    private Context mCon;
    public ViewPagerAdapter(Context context) {this.mCon = context;}

    @Override
    public Object instantiateItem(ViewGroup collection, int position){
        int resId;
        if(position == 0){
            resId = R.id.page_home;
        }
        else if(position == 1)
        {
            resId = R.id.history;
        }
        else
        {
            resId = R.id.settings;
        }

        return collection.findViewById(resId);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {}
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return mCon.getResources().getString(R.string.home);
        }
        else if(position == 1){
            return mCon.getResources().getString(R.string.history);
        }
        else if(position == 2){
            return mCon.getResources().getString(R.string.settings);
        }
        return super.getPageTitle(position);
    }
}
