package be.ugent.groep3.bikebuddy.listeners;

import android.app.ActionBar;
import android.support.v4.view.ViewPager;

/**
 * Created by Jan on 25/04/2015.
 */
public class SimpleOnPageChangeListener implements ViewPager.OnPageChangeListener{

    private ActionBar actionBar;

    public SimpleOnPageChangeListener(ActionBar actionBar) {
        this.actionBar = actionBar;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        actionBar.setSelectedNavigationItem(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
