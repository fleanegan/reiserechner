package com.example.fleanegan.reiserechner;

import android.animation.ObjectAnimator;
import android.graphics.drawable.TransitionDrawable;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class UserManagerAdapter extends RecyclerView.Adapter<UserManagerAdapter.ViewHolder> {
    ViewGroup parent;
    UserManager parentFragment;
    User managedUser;
    int colorChangeState;
    int headerSize;
    int dp;


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
    public UserManagerAdapter(User managedUser, UserManager parentFragment) {
        this.parentFragment = parentFragment;
        this.managedUser = managedUser;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UserManagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cute_cardlike_view, parent, false);

        this.parent = parent;
        View d = v.findViewById(R.id.container_delete);
        d.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        this.headerSize = (int) parent.getResources().getDimension(R.dimen.recycler_height);


        this.dp = ((int) parent.getResources().getDisplayMetrics().density);


        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TextView child = (TextView) holder.mViewHolderLayout.findViewById(R.id.item_title);
        child.setText(String.valueOf(position));

        Item y = this.managedUser.getBoughtItems().get(position);

        child.setText(y.getName());

        System.out.println();

        TextView price = (TextView) holder.mViewHolderLayout.findViewById(R.id.item_price);
        price.setText("price: " + y.getPrice() + "€");

        TextView description = (TextView) holder.mViewHolderLayout.findViewById(R.id.item_description);
        description.setText(y.getDescription());

        final TextView deletionWarning = (TextView) holder.mViewHolderLayout.findViewById(R.id.container_delete);
        final TextView editWarning = (TextView) holder.mViewHolderLayout.findViewById(R.id.container_edit);

        final ScrollView scrollView = (ScrollView) holder.mViewHolderLayout.findViewById(R.id.container_special_scroller);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, headerSize);
            }
        });

        new CountDownTimer(1, 1) {
            public void onTick(long milliSeconds) {
            }

            public void onFinish() {
                scrollView.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            int scrollY = scrollView.getScrollY();
                            if (scrollY == 0) {
                                managedUser.removeItem(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
                                parentFragment.manageTheSaldo();
                            } else if (scrollY == 2 * headerSize) {
                                parentFragment.editItem(managedUser.getBoughtItems().get(holder.getAdapterPosition()), holder.getAdapterPosition());
                            } else if (scrollY == headerSize) {
                                scrollView.smoothScrollBy(0, 25);
                                ObjectAnimator.ofInt(scrollView, "scrollY", headerSize).setDuration(1111).start();
                            } else {
                                ObjectAnimator.ofInt(scrollView, "scrollY", headerSize).setDuration(1111).start();
                            }
                        }
                        //important. Otherwise the scrollview wont scroll.
                        return false;
                    }
                });

                scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        final int Yscr = scrollView.getScrollY();
                        if (Yscr == 0) {
                            colorChangeState = 1;
                            deletionWarning.setText("release to delete");
                            animatedBgColorChange(deletionWarning);
                        } else if (Yscr == 2 * headerSize) {
                            colorChangeState = 2;
                            editWarning.setText("release to edit");
                            animatedBgColorChange(editWarning);
                        } else if (Yscr < 150) {
                            if (colorChangeState != 0) {
                                colorChangeState = 0;
                                deletionWarning.setText("swipe further to delete");
                                animatedBgColorChangeRev(deletionWarning);
                            }
                        } else if (Yscr > (2 * headerSize - 50)) {
                            if (colorChangeState != 0) {
                                colorChangeState = 0;
                                editWarning.setText("swipe further to edit");
                                animatedBgColorChangeRev(editWarning);
                            }
                        }
                    }
                });
            }
        }.start();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.managedUser.getBoughtItems().size();
    }


    public void animatedBgColorChange(TextView view) {
        TransitionDrawable trans = (TransitionDrawable) view.getBackground();
        trans.startTransition(250);
    }

    public void animatedBgColorChangeRev(TextView view) {
        TransitionDrawable trans = (TransitionDrawable) view.getBackground();
        trans.reverseTransition(250);
    }
}