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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemListFragment extends Fragment {

    MainActivity act;
    ArrayList<ListItem> items = new ArrayList<>();

    ListItem mainItem;


    RecyclerView rv;
    SimpleStringRecyclerViewAdapter adapter;

    boolean isLoading = false;



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



        rv = (RecyclerView) inflater.inflate(R.layout.item_list, container, false);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        adapter = new SimpleStringRecyclerViewAdapter();
        rv.setAdapter(adapter);

        if (items.size() == 0) loadList();

        return rv;
    }



    void loadList() {

        if (isLoading) return;
        isLoading = true;
        act.mSwipe.setRefreshing(true);


        String url = "https://api.hitbtc.com/api/2/public/candles/" + mainItem.symbol_id + "?period=D1";
        Log.i("***ITEM LIST", "URL:" + url);

        Ion.with(this).load(url).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {

                act.mSwipe.setRefreshing(false);
                isLoading = false;

                Log.i("***ITEM LIST", "RES:" + result);

                try {

                    items.clear();

                    ListItem item;
                    JSONArray catalog = new JSONArray(result);
                    for(int i = 0; i < catalog.length(); i++) {

                        JSONObject el = catalog.getJSONObject(i);

                        item = new ListItem();
                        item.symbol_id = mainItem.symbol_id;

                        item.timestamp = el.getString("timestamp");

                        item.p_open = el.getString("open");
                        item.p_close = el.getString("close");

                        item.p_max = el.getString("max");
                        item.p_min = el.getString("min");


                        items.add(0, item);
                    }

                    adapter.notifyDataSetChanged();


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

    }

    public class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View mView;
            public int type;

            TextView txtData;
            TextView txtTime;


            public ViewHolder(View view, int new_type) {
                super(view);
                mView = view;
                type = new_type;

                txtData = (TextView) view.findViewById(R.id.txtData);
                txtTime = (TextView) view.findViewById(R.id.txtTime);

            }
        }

        public SimpleStringRecyclerViewAdapter() {

        }

        @Override
        public int getItemViewType(int position) {

            return 0;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_item, parent, false);
            return new ViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            View rootView = holder.mView;
            final ListItem item = items.get(position);


            holder.txtData.setText("Open:" + item.p_open + "  Close:" + item.p_close + "  Max:" + item.p_max + "  Min:" + item.p_min);



            holder.txtTime.setText(item.timestamp);


        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}

