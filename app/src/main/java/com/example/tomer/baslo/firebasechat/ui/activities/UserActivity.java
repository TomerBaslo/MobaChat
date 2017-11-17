package com.example.tomer.baslo.firebasechat.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tomer.baslo.firebasechat.Game;
import com.example.tomer.baslo.firebasechat.R;
import com.example.tomer.baslo.firebasechat.ui.adapters.UserPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TabLayout mTabLayoutUserListing;
    private ViewPager mViewPagerUserListing;
    private Game mGame;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int flags) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.setFlags(flags);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_listing);
        extractDataFromBundle();
        bindViews();
        init();
    }

    private void extractDataFromBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(MainActivity.LOBBY_BUNDLE_KEY);
        if (bundle != null) {
            mGame = (Game) bundle.getSerializable(MainActivity.GAME_KEY);
        }
    }

    private void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTabLayoutUserListing = (TabLayout) findViewById(R.id.tab_layout_user_listing);
        mViewPagerUserListing = (ViewPager) findViewById(R.id.view_pager_user_listing);
    }

    private void init() {
        // set the toolbar
        setSupportActionBar(mToolbar);

        // set the view pager adapter
        UserPagerAdapter userListingPagerAdapter = new UserPagerAdapter(getSupportFragmentManager(), mGame);
        mViewPagerUserListing.setAdapter(userListingPagerAdapter);

        // attach tab layout with view pager
        mTabLayoutUserListing.setupWithViewPager(mViewPagerUserListing);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_listing, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logoutDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void performFirebaseLogout() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            onSuccess("Successfully logged out!");
        } else {
            onFailure("No user logged in yet!");
        }
    }

    public void logout() {
        performFirebaseLogout();
    }

    public void onSuccess(String message) {
        onLogoutSuccess(message);
    }

    public void onFailure(String message) {
        onLogoutFailure(message);
    }

    private void logoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logout();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void onLogoutSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        LoginActivity.startIntent(this,
                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public void onLogoutFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
