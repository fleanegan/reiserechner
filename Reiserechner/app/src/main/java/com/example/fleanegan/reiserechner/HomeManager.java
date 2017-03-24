package com.example.fleanegan.reiserechner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by fleanegan on 18.02.17.
 */

public class HomeManager extends Fragment {

    TextView total;
    RecyclerView mRecyclerView;
    HomeAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    int storeThemRecyclerHeight;
    int dp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dp = ((int) getResources().getDisplayMetrics().density);

        return inflater.inflate(R.layout.content_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle onSavedInstanceState) {

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.home_recycler_view);
        this.storeThemRecyclerHeight = mRecyclerView.getLayoutParams().height;
        mRecyclerView.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter (see also next example)
        mAdapter = new HomeAdapter(this.getDateSortedUserList());
        mRecyclerView.setAdapter(mAdapter);

        this.initialize();

    }

    public void launchProjectManager() {
        ((MainActivity) getActivity()).pushUserToFragment(-2);
    }

    public void initialize() {
        this.total = (TextView) getView().findViewById(R.id.home_total);
        if (User.totalAmount != null)
            this.total.setText(String.valueOf(User.totalAmount.setScale(2, RoundingMode.HALF_EVEN)));
        final DrawerLayout drawerLayout = (DrawerLayout) this.getActivity().findViewById(R.id.drawer_layout);
        Button manageProjects = (Button) getView().findViewById(R.id.home_manage_projects);
        manageProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchProjectManager();
            }
        });


        this.mAdapter.notifyDataSetChanged();
        if (User.numberOfUsers == 0 && !((MainActivity) getActivity()).intialized) {
            final android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.openDrawer(GravityCompat.START);
                    ((MainActivity) getActivity()).intialized = true;
                }
            }, 1000);
        }
    }


    @Override
    public void onStop() {
        this.mRecyclerView.getLayoutParams().height = this.storeThemRecyclerHeight;
        super.onStop();
    }

    public ArrayList<ArrayList<Item>> getDateSortedUserList() {
        ArrayList<ArrayList<Item>> container = new ArrayList<>();
        ArrayList<Item> sortedByDate = User.itemList;
        if (User.itemList == null) return new ArrayList<>();
        if (User.itemList.size() == 0) return new ArrayList<>();

        Collections.sort(sortedByDate, new Comparator<Item>() {
                    @Override
                    public int compare(Item o1, Item o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                }
        );

        Date day = null;
        ArrayList<Item> holder = new ArrayList<>();

        for (Item i : sortedByDate) {
            Date newDay = i.getDate();
            if (!this.isSameDay(day, newDay)) {
                if (day != null) container.add(holder);
                holder = new ArrayList<>();
                holder.add(i);
                day = newDay;
            } else holder.add(i);
        }
        if (holder.size() > 0) container.add(holder);
        return container;
    }

    public boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

}