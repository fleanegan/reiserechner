package com.example.fleanegan.reiserechner;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import me.anwarshahriar.calligrapher.Calligrapher;


/**
 * Created by fleanegan on 10.03.17.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    ArrayList<ArrayList<Item>> sortedArrayList;
    ViewGroup parent;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout homeLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            this.homeLayout = v;
        }
    }

    public HomeAdapter(ArrayList<ArrayList<Item>> sortedArrayList) {
        this.sortedArrayList = sortedArrayList;
    }

    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout toBePushed = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_day, parent, false);

        Calligrapher calligrapher = new Calligrapher(parent.getContext());
        calligrapher.setFont(toBePushed, parent.getResources().getString(R.string.font_fu));

        toBePushed.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

        this.parent = parent;



        ViewHolder returnHolder = new ViewHolder(toBePushed);
        return returnHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        TextView header = (TextView) holder.homeLayout.findViewById(R.id.home_header);
        header.setText(new SimpleDateFormat("dd.MM.yyyy").format(this.sortedArrayList.get(position).get(0).getDate()));

        TextView subTotal = (TextView) holder.homeLayout.findViewById(R.id.home_header_sub_total);
        BigDecimal subTotalValue = new BigDecimal(0);

        final LinearLayout container = (LinearLayout) holder.homeLayout.findViewById(R.id.home_day_container);
        final LinearLayout collapser = (LinearLayout) holder.homeLayout.findViewById(R.id.home_collapser);
        final TextView schraegStrich = (TextView) collapser.findViewById(R.id.schraegStrich);
        schraegStrich.setText("\\");
        collapser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int height = container.getMeasuredHeight();
                if (schraegStrich.getText().toString().equals("\\")) {
                    container.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    Animations.slideAnimator(0, height, container, 555).start();
                    schraegStrich.setText("/");
                } else {
                    Animations.slideAnimator(height, 0, container, 555).start();
                    schraegStrich.setText("\\");
                }
            }
        });

        for (Item i : this.sortedArrayList.get(position)) {
            LinearLayout boughtItem = (LinearLayout) LayoutInflater.from(this.parent.getContext())
                    .inflate(R.layout.home_day_sub, this.parent, false);
            TextView itemName = (TextView) boughtItem.findViewById(R.id.home_day_sub_text_view);
            itemName.setText(i.getName());
            TextView itemPrice = (TextView) boughtItem.findViewById(R.id.home_day_sub_price);
            itemPrice.setText(String.valueOf(i.getPrice()));
            container.addView(boughtItem);
            subTotalValue = subTotalValue.add(i.getPrice());
        }
        subTotal.setText(String.valueOf(subTotalValue.setScale(2, RoundingMode.HALF_EVEN)));
    }


    @Override
    public int getItemCount() {
        return sortedArrayList.size();
    }
}
