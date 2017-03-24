package com.example.fleanegan.reiserechner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;

/**
 * Created by fleanegan on 18.02.17.
 */

public class UserManager extends Fragment {

    TextView addNewItem;
    TextView nameSpace;
    TextView total;
    TextView saldo;
    boolean leaveOpen;
    RecyclerView mRecyclerView;
    UserManagerAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    User testUser;
    EditText itemName;
    Boolean isCollapsed = true;
    EditText itemPrice;
    int dp;


    public void refresh() {
        Bundle alreadyDone = new Bundle();
        alreadyDone.putSerializable("user", this.testUser);
        alreadyDone.putBoolean("leaveOpen", !this.isCollapsed);

        Fragment fragment = new UserManager();
        fragment.setArguments(alreadyDone);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.button_container, fragment);
        ft.commit();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle tempUser = getArguments();
        this.testUser = (User) tempUser.getSerializable("user");
        this.leaveOpen = tempUser.getBoolean("leaveOpen");


        this.dp = ((int) getResources().getDisplayMetrics().density);

        return inflater.inflate(R.layout.content_user_manager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle onSavedInstanceState) {

        this.itemPrice = (EditText) getView().findViewById(R.id.user_manager_price);
        this.itemPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                } else {
                    //revoke keyboard
                    ((InputMethodManager) getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });


        this.itemName = (EditText) getView().findViewById(R.id.user_manager_item);
        this.itemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                } else {
                    //revoke keyboard
                    ((InputMethodManager) getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    itemName.requestFocus();
                }
            }
        });

        this.mRecyclerView = (RecyclerView) getView().findViewById(R.id.user_manager_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        this.mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        this.mLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        this.mRecyclerView.setLayoutManager(this.mLayoutManager);


        // specify an adapter (see also next example)
        this.mAdapter = new UserManagerAdapter(this.testUser, this);
        this.mRecyclerView.setAdapter(this.mAdapter);


        final LinearLayout collapsibleLayout = (LinearLayout) getView().findViewById(R.id.user_manager_collapsible);
        final Animations animations = new Animations();

        this.addNewItem = (TextView) getView().findViewById(R.id.user_manager_collapse);

        this.addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (UserManager.this.isCollapsed) {
                    UserManager.this.addNewItem.setText("close");
                    collapsibleLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int height = collapsibleLayout.getMeasuredHeight();
                    animations.expand(collapsibleLayout, height, 0);
                    UserManager.this.itemName.requestFocus();
                } else {
                    animations.collapse(collapsibleLayout, 0);
                    UserManager.this.addNewItem.setText("add new item");
                    UserManager.this.nameSpace.requestFocus();
                }
                UserManager.this.isCollapsed = !UserManager.this.isCollapsed;
                UserManager.this.addNewItem.setGravity(Gravity.CENTER);
            }
        });


        Button addItem = (Button) getView().findViewById(R.id.user_manager_add);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surveiller();
            }
        });
        this.initialize();

    }

    @Override
    public void onStart() {
        //this.initialize();
        super.onStart();
    }

    public void initialize() {

        this.nameSpace = (TextView) getView().findViewById(R.id.user_manager_name_space);
        this.saldo = (TextView) getView().findViewById(R.id.user_manager_saldo);
        this.total = (TextView) getView().findViewById(R.id.user_manager_total);

        this.saldo.setText(String.valueOf(0));
        this.nameSpace.setText(this.testUser.getName());
        this.total.setText(String.valueOf(this.testUser.getTotalDispense()));

        if (this.leaveOpen) {
            LinearLayout collapsibleLayout = (LinearLayout) getView().findViewById(R.id.user_manager_collapsible);
            collapsibleLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int height = collapsibleLayout.getMeasuredHeight();
            collapsibleLayout.getLayoutParams().height = height;
            this.isCollapsed = false;
            this.addNewItem.setText("close");
        }
        this.manageTheSaldo();
    }

    public void surveiller() {
        if (!itemPrice.getText().toString().equals("") && !this.itemName.getText().toString().equals("")) {
            this.testUser.addItem(new Item(this.itemName.getText().toString(), this.itemPrice.getText().toString()));
            this.mAdapter.notifyDataSetChanged();
            this.itemPrice.setText("");
            this.itemName.setText("");
            this.manageTheSaldo();
        }
    }

    public void manageTheSaldo() {
        BigDecimal everybodyShouldPay = User.totalAmount.divide(new BigDecimal(User.numberOfUsers), 2, BigDecimal.ROUND_HALF_EVEN);
        everybodyShouldPay = everybodyShouldPay.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal saldo = everybodyShouldPay.subtract(this.testUser.getTotalDispense());
        BigDecimal total = this.testUser.getTotalDispense();
        total.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        if (saldo.toBigInteger().doubleValue() > 0) this.saldo.setTextColor(Color.RED);
        else this.saldo.setTextColor(Color.GREEN);

        this.total.setText((total.setScale(2, BigDecimal.ROUND_HALF_EVEN)).toString());
        this.saldo.setText((saldo.setScale(2, BigDecimal.ROUND_HALF_EVEN)).multiply(new BigDecimal(-1)).toString());
    }

    @Override
    public void onStop() {
        Bundle sendBack = new Bundle();
        sendBack.putSerializable("user", this.testUser);

        super.onStop();
    }

    public void editItem(Item item, int position) {
        Intent secondaryActivity = new Intent(getActivity(), EditItem.class);
        secondaryActivity.putExtra("item", item);
        secondaryActivity.putExtra("position", position);
        startActivityForResult(secondaryActivity, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("banana " + requestCode);
        if (requestCode == 1 && resultCode == 1) {
            Item item = (Item) data.getExtras().get("item");
            int position = (int) data.getExtras().get("position");
            testUser.manageBalance(testUser.getBoughtItems().get(position), false);
            User.itemList.set(User.itemList.indexOf(testUser.getBoughtItems().get(position)), item);
            testUser.getBoughtItems().set(position, item);
            testUser.manageBalance(item, true);
            mAdapter.notifyItemChanged(position);

            refresh();
            System.out.println("reached");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}