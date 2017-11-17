package com.example.tomer.baslo.firebasechat.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.tomer.baslo.firebasechat.Game;
import com.example.tomer.baslo.firebasechat.ui.fragments.UsersFragment;

/**
 * Created by Tomer on 8/11/2017.
 */

public class UserPagerAdapter extends FragmentPagerAdapter {
    private static Fragment[] sFragments;
    private static final String[] sTitles = new String[]{
            "All Users"};
    private Game mGame;

    public UserPagerAdapter(FragmentManager fm, Game game) {
        super(fm);
        mGame = game;
        sFragments = new Fragment[]{ UsersFragment.newInstance(UsersFragment.TYPE_ALL, mGame) };
    }

    @Override
    public Fragment getItem(int position) {
        return sFragments[position];
    }

    @Override
    public int getCount() {
        return sFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return sTitles[position];
    }
}
