package be.ugent.groep3.bikebuddy.listeners;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

/**
 * Created by Jan on 25/04/2015.
 */
public class TabListener implements ActionBar.TabListener {

    private ViewPager viewPager;

    public TabListener(ViewPager viewPager){
        this.viewPager = viewPager;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}
