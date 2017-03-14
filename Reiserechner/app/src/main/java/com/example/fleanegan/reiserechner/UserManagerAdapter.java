package com.example.fleanegan.reiserechner;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserManagerAdapter extends RecyclerView.Adapter<UserManagerAdapter.ViewHolder> {
    private User tempUser;
    LayoutInflater inflater;
    Context parentContext;
    TextView saferDeletionView;
    LinearLayout scrollContainer;
    TextView total;
    int syncTheIds = 0;
    UserManager parentFragment;
    TextView neutralizerView;
    Integer soManyPlatesDoWeHave = 0;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mViewHolderLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            mViewHolderLayout = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public UserManagerAdapter(User myaDataset, UserManager parentFragment) {
        this.tempUser = myaDataset;
        this.parentFragment = parentFragment;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UserManagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scrollable_wrapper_horizontal, parent, false);

        v.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        this.parentContext = parent.getContext();
        this.inflater = (LayoutInflater) this.parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.total = (TextView) ((LinearLayout) parent.getParent().getParent()).findViewById(R.id.user_manager_total);

        this.neutralizerView = (TextView) ((LinearLayout) parent.getParent().getParent()).findViewById(R.id.user_manager_neutralizer);
        this.neutralizerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neutralize();
            }
        });

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        int localCounter = 0;
        for (Item y : this.tempUser.getBoughtItems()) {

            //gets the scrollview -> not touched, only to retrieve children
            HorizontalScrollView scrollView = (HorizontalScrollView) holder.mViewHolderLayout.getChildAt(0);
            //where to put the stuff
            this.scrollContainer = (LinearLayout) scrollView.findViewById(R.id.weird_container_scrollable_container);
            //add another card and inflate the layout right into it
            LinearLayout test = new LinearLayout(this.parentContext) {
            };
            inflater.inflate(R.layout.cute_cardlike_view, test);
            //add test to view
            if (soManyPlatesDoWeHave < tempUser.getBoughtItems().size()) {
                scrollContainer.addView(test);
                this.syncTheIds--;
                soManyPlatesDoWeHave++;
            }
            //modify card content
            LinearLayout cuteCardLayout = (LinearLayout) this.scrollContainer.getChildAt(localCounter);

            TextView itemName = (TextView) cuteCardLayout.findViewById(R.id.item_title);
            itemName.setText(y.getName());
            TextView price = (TextView) cuteCardLayout.findViewById(R.id.item_price);
            price.setText("price: " + y.getPrice() + "€");
            this.neutralizerView.setText("last added: " + y.getTimeStamp());
            this.total.setText(String.valueOf(tempUser.getTotalDispense()));
            TextView removeItem = (TextView) test.findViewById(R.id.item_remove_button);
            final LinearLayout statTest = test;
            removeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageItemDeletion((TextView) v, statTest);
                }
            });
            removeItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        saferDeletionView = null;
                        ((TextView) v).setTextColor(Color.parseColor("#00ff0000"));
                    } else {
                        manageItemDeletion((TextView) v, statTest);
                    }
                }
            });
            localCounter++;
            this.parentFragment.manageTheSaldo();
        }
        holder.mViewHolderLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }


    public void manageItemDeletion(TextView view, LinearLayout test) {
        if (!view.equals(this.saferDeletionView)) {
            if (saferDeletionView != null) {
                this.saferDeletionView.setTextColor(Color.parseColor("#00ff0000"));
            }
            this.saferDeletionView = view;
            view.setTextColor(Color.parseColor("#E53935"));
            return;
        } else this.saferDeletionView = null;

        int measuredId = this.scrollContainer.indexOfChild(test);

        this.scrollContainer.removeViewAt(measuredId);
        tempUser.removeItem(measuredId);

        this.parentFragment.manageTheSaldo();
        this.total.setText(String.valueOf(tempUser.getTotalDispense()));

        //fix weird bug that forcecloses the app when trying to
        //add a user after clearing the whole list.
        if (this.scrollContainer.getChildCount() == 0) this.parentFragment.refresh();
    }

    public void neutralize() {
        if (this.saferDeletionView != null) {
            this.saferDeletionView.setTextColor(Color.parseColor("#00ff0000"));
            this.saferDeletionView = null;
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 1;
    }
}