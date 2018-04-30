/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test.cryptoapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

public class ItemFragment extends Fragment {

    MainActivity act;

    ListItem mainItem;
    NestedScrollView mScroll;
    boolean isLoading;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        act = (MainActivity)getActivity();
        act.mSwipe.setEnabled(true);
        act.mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadList();
            }
        });

        if (mScroll == null) {
            mScroll = (NestedScrollView) inflater.inflate(R.layout.item, container, false);
            loadList();
        }

        return mScroll;
    }

    void loadList() {

        if (isLoading) return;
        isLoading = true;
        act.mSwipe.setRefreshing(true);


        String url = "https://api.hitbtc.com/api/2/public/ticker/" + mainItem.symbol_id;
        Log.i("***LIST", "URL:" + url);

        Ion.with(this).load(url).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {

                act.mSwipe.setRefreshing(false);
                isLoading = false;

                Log.i("***ITEM DATA", "RES:" + result);

                try {

                    JSONObject item = new JSONObject(result);



                    TextView txtName1 = (TextView) mScroll.findViewById(R.id.txtName1);
                    TextView txtName2 = (TextView) mScroll.findViewById(R.id.txtName2);
                    TextView txtDesc = (TextView) mScroll.findViewById(R.id.txtDesc);
                    Button btn = mScroll.findViewById(R.id.btnInfo);


                    txtName1.setText(act.getCurrency(mainItem.baseCurrency));
                    txtName2.setText(act.getCurrency(mainItem.quoteCurrency));

                    txtDesc.setText(result);

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            act.openItemList(mainItem);

                        }
                    });


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

    }

}


