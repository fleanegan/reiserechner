package com.example.fleanegan.testcards;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<User> maDataset;
    Integer i = 0;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mViewHolderLayout;
        public ViewHolder(LinearLayout v) {
            super(v);
            mViewHolderLayout = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(User[] myDataset, ArrayList<User> myaDataset) {
        this.maDataset = myaDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        LinearLayout scrollContainer = (LinearLayout) v.findViewById(R.id.weird_container_scrollable_container);

        for(Item item: this.maDataset.get(i).getBoughtItems()){
            LinearLayout test = new LinearLayout(parent.getContext()){};
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.cute_cardlike_view, test);
            scrollContainer.addView(test);
        }
        i++;

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        User user = maDataset.get(position);
        TextView userName = (TextView) holder.mViewHolderLayout.getChildAt(0);
        userName.setText(user.getName());

        int localCounter = 0;
        for(Item y : this.maDataset.get(position).getBoughtItems()) {

            HorizontalScrollView scrollView = (HorizontalScrollView) holder.mViewHolderLayout.getChildAt(1);
            LinearLayout scrollContainer = (LinearLayout) scrollView.findViewById(R.id.weird_container_scrollable_container);
            LinearLayout cuteCardLayout = (LinearLayout) scrollContainer.getChildAt(localCounter);
            TextView itemName = (TextView) cuteCardLayout.findViewById(R.id.item_title);

            itemName.setText(y.getName());

            TextView price = (TextView) cuteCardLayout.findViewById(R.id.item_price);
            price.setText("price: " + y.getPrice() + "€");
            localCounter ++;

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.maDataset.size();
    }
}