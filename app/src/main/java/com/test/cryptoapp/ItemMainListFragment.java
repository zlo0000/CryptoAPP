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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemMainListFragment extends Fragment {

    MainActivity act;
    ArrayList<ListItem> items = new ArrayList<>();
    SimpleStringRecyclerViewAdapter adapter;
    boolean isLoading = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        act = (MainActivity)getActivity();


        act.mSwipe.setEnabled(true);
        act.mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMainList();
            }
        });


        RecyclerView rv = (RecyclerView) inflater.inflate(R.layout.item_list, container, false);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));

        adapter = new SimpleStringRecyclerViewAdapter();
        rv.setAdapter(adapter);

        if (items.size() == 0) loadMainList();

        return rv;
    }


    void loadMainList() {

        if (isLoading) return;
        isLoading = true;

        act.mSwipe.setRefreshing(true);
        Ion.with(this).load("https://api.hitbtc.com/api/2/public/symbol").asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {

                act.mSwipe.setRefreshing(false);
                isLoading = false;

                Log.i("***MAIN LIST", "RES:" + result);

                try {

                    items.clear();

                    ListItem item;
                    JSONArray catalog = new JSONArray(result);
                    for(int i = 0; i < catalog.length(); i++) {

                        JSONObject el = catalog.getJSONObject(i);

                        item = new ListItem();
                        item.symbol_id = el.getString("id");
                        item.baseCurrency = el.getString("baseCurrency");
                        item.quoteCurrency = el.getString("quoteCurrency");
                        item.data = el.toString();

                        items.add(item);
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
            public TextView mTextView;
            public ImageView mImageView;
            public TextView mData;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTextView = (TextView) view.findViewById(R.id.txtName);
                mImageView = (ImageView) view.findViewById(R.id.imgState);
            }
        }

        public SimpleStringRecyclerViewAdapter() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_list_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            final ListItem item = items.get(position);

            holder.mImageView.setImageResource(R.drawable.mn_home);
            holder.mTextView.setText(item.baseCurrency + " → " + item.quoteCurrency);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    act.openItemFragment(item);
               }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
