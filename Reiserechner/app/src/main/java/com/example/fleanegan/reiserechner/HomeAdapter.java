package com.example.fleanegan.reiserechner;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * Created by fleanegan on 10.03.17.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout homeLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            this.homeLayout = v;
        }
    }

    public HomeAdapter() {

    }

    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout toBePushed = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scrollable_wrapper_vertical, parent, false);


        ViewHolder returnHolder = new ViewHolder(toBePushed);
        return returnHolder;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        System.out.println();
    }


    @Override
    public int getItemCount() {
        return User.numberOfUsers;
    }
}
