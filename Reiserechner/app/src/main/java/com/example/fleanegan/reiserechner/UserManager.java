package com.example.fleanegan.reiserechner;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
        alreadyDone.putSerializable("user", testUser);
        alreadyDone.putBoolean("leaveOpen", !isCollapsed);

        Fragment fragment = new UserManager();

        fragment.setArguments(alreadyDone);

        System.out.println("calling refresh");

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

        itemPrice = (EditText) getView().findViewById(R.id.user_manager_price);
        itemPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    System.out.println("iprice lost focus");
                    InputMethodManager imm = (InputMethodManager) getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                } else {
                    System.out.println("iprice has focus");
                    //revoke keyboard
                    ((InputMethodManager) getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });


        itemName = (EditText) getView().findViewById(R.id.user_manager_item);
        itemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    System.out.println("iname lost focus");
                    InputMethodManager imm = (InputMethodManager) getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                } else {
                    System.out.println("iname has focus");
                    //revoke keyboard
                    ((InputMethodManager) getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    itemName.requestFocus();
                }
            }
        });

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.user_manager_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter (see also next example)
        mAdapter = new UserManagerAdapter(this.testUser, this);
        mRecyclerView.setAdapter(mAdapter);


        final LinearLayout collapsibleLayout = (LinearLayout) getView().findViewById(R.id.user_manager_collapsible);
        final Animations animations = new Animations();

        this.addNewItem = (TextView) getView().findViewById(R.id.user_manager_collapse);

        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCollapsed) {
                    addNewItem.setText("close");
                    collapsibleLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int height = collapsibleLayout.getMeasuredHeight();
                    Log.d("HEIGHT", "" + height);
                    animations.expand(collapsibleLayout, height, 0);
                    itemName.requestFocus();
                } else {
                    animations.collapse(collapsibleLayout, 0);
                    addNewItem.setText("add new item");
                    nameSpace.requestFocus();
                }
                isCollapsed = !isCollapsed;
                addNewItem.setGravity(Gravity.CENTER);
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

        System.out.println("initializing" + testUser.getName());
        this.nameSpace = (TextView) getView().findViewById(R.id.user_manager_name_space);
        this.saldo = (TextView) getView().findViewById(R.id.user_manager_saldo);
        this.total = (TextView) getView().findViewById(R.id.user_manager_total);

        this.saldo.setText(String.valueOf(0));
        this.nameSpace.setText(testUser.getName());
        this.total.setText(String.valueOf(testUser.getTotalDispense()));

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
        if (!itemPrice.getText().toString().equals("") && !itemName.getText().toString().equals("")) {
            testUser.addItem(new Item(itemName.getText().toString(), itemPrice.getText().toString()));
            mAdapter.notifyDataSetChanged();
            itemPrice.setText("");
            itemName.setText("");
        }
    }

    public void manageTheSaldo() {
        System.out.println("total sum: " + User.totalAmount + " rdr: " + User.totalAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
        BigDecimal everybodyShouldPay = User.totalAmount.divide(new BigDecimal(User.numberOfUsers), 2, BigDecimal.ROUND_HALF_EVEN);
        everybodyShouldPay = everybodyShouldPay.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal saldo = everybodyShouldPay.subtract(this.testUser.getTotalDispense());
        System.out.println(testUser.getName() + " payed " + testUser.getTotalDispense());
        System.out.println("we've got " + User.numberOfUsers + " users.");
        System.out.println("total dispenses: " + User.totalAmount);
        System.out.println("everybodyShouldPay: " + everybodyShouldPay);
        System.out.println(this.testUser.getName() + " should pay: " + saldo);
        if (saldo.toBigInteger().doubleValue() > 0) this.saldo.setTextColor(Color.RED);
        else this.saldo.setTextColor(Color.GREEN);

        this.saldo.setText((saldo.setScale(2, BigDecimal.ROUND_HALF_EVEN)).multiply(new BigDecimal(-1)).toString());
    }

    @Override
    public void onStop() {
        Bundle sendBack = new Bundle();
        sendBack.putSerializable("user", testUser);

        super.onStop();
    }
}