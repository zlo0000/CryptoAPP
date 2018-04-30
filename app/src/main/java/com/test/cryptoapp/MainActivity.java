package com.test.cryptoapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    NavigationView navigationView;
    DrawerLayout drawer;

    int fragment_state;

    Handler mHandler;
    SharedPreferences mPreferences;

    Toolbar toolbar;
    SwipeRefreshLayout mSwipe;

    JSONArray currency = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("CRYPTO APP");
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showText("Привет");
            }
        });



        mSwipe = (SwipeRefreshLayout) findViewById(R.id.mSwipe);
        mSwipe.setEnabled(false);
        mSwipe.setDistanceToTriggerSync(300);


        if (currency == null) {
            loadCurrency();
        }

        openMainList(false);

    }

    void loadCurrency() {

        Ion.with(this).load("https://api.hitbtc.com/api/2/public/currency").asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {


                Log.i("***CURRENCY LIST", "RES:" + result);

                try {
                    currency = new JSONArray(result);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
    }




    void openMainList(boolean fg_add_stack) {

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mContent);
        Log.i("***OPEN FRAGMENT", String.valueOf(currentFragment));

        if (currentFragment instanceof ItemMainListFragment) {
            return;
        }




        if (!fg_add_stack) {
            FragmentManager fm = getSupportFragmentManager();
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        mSwipe.setEnabled(false);


        ItemMainListFragment fragment = new ItemMainListFragment();
        fragment.act = this;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (fg_add_stack) {
            ft.addToBackStack("main");
            ft.setCustomAnimations(R.anim.anim_in_right, R.anim.anim_out_left, R.anim.anim_in_left, R.anim.anim_out_right);
        }
        ft.replace(R.id.mContent, fragment);
        ft.commit();
        fragment_state = 0;
    }



    void openItemList(ListItem item) {


        mSwipe.setEnabled(true);

        ItemListFragment fragment = new ItemListFragment();
        fragment.act = this;
        fragment.mainItem = item;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();


        ft.addToBackStack("item_trades");
        ft.setCustomAnimations(R.anim.anim_in_right, R.anim.anim_out_left, R.anim.anim_in_left, R.anim.anim_out_right);
        ft.replace(R.id.mContent, fragment);
        ft.commit();

        fragment_state = 1;
    }


    void openItemFragment(ListItem item) {

        mSwipe.setEnabled(true);

        ItemFragment fragment = new ItemFragment();
        fragment.act = this;
        fragment.mainItem = item;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();


        ft.addToBackStack("item");
        ft.setCustomAnimations(R.anim.anim_in_right, R.anim.anim_out_left, R.anim.anim_in_left, R.anim.anim_out_right);
        ft.replace(R.id.mContent, fragment);
        ft.commit();

        fragment_state = 2;


    }


    void showText(String text) {
        Snackbar.make(mSwipe, text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public String getCurrency(String baseCurrency) {

        if (currency != null) {
            try {
                JSONObject item;
                for (int i = 0; i < currency.length(); i++) {
                    item = currency.getJSONObject(i);
                    if (item.getString("id").equals(baseCurrency)) {
                        return baseCurrency + "\n" + item.getString("fullName");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "ERROR";
            }
        }
        return "NOT FOUND";
    }
}
