package com.example.fleanegan.reiserechner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.math.BigDecimal;

import me.anwarshahriar.calligrapher.Calligrapher;

/**
 * Created by fleanegan on 18.02.17.
 */

public class UserManager extends Fragment {

    boolean leaveOpen;
    int dp;
    Boolean mainIsCollapsed = true;
    Boolean scopeIsCollapsed = true;

    User testUser;
    TextView addNewItem;
    TextView nameSpace;
    TextView total;
    TextView saldo;
    TextView scope;
    EditText itemName;
    EditText itemPrice;
    LinearLayout snack;
    LinearLayout scopeCollapsible;
    RecyclerView mRecyclerView;
    UserManagerAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    MainActivity mainActivity;
    View.OnClickListener defaultAction;

    public void refresh() {
        Bundle alreadyDone = new Bundle();

        alreadyDone.putSerializable("user", this.testUser);
        alreadyDone.putBoolean("leaveOpen", !this.mainIsCollapsed);

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

        this.mainActivity = (MainActivity) getActivity();

        this.dp = ((int) getResources().getDisplayMetrics().density);

        return inflater.inflate(R.layout.content_manage_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle onSavedInstanceState) {

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

        this.addNewItem = (TextView) getView().findViewById(R.id.user_manager_collapse);

        this.addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (UserManager.this.mainIsCollapsed) {
                    UserManager.this.addNewItem.setText("close");
                    Animations.alphaAnimator(true, getView().findViewById(R.id.user_manager_add), 555).start();
                    mainActivity.fragmentFAB.setVisibility(View.INVISIBLE);
                    collapsibleLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int height = collapsibleLayout.getMeasuredHeight();
                    Animations.slideAnimator(0, height, collapsibleLayout, 1111).start();

                    getView().findViewById(R.id.user_manager_add).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            surveiller();
                        }
                    });
                    UserManager.this.itemName.requestFocus();
                } else {
                    Animations.slideAnimator(collapsibleLayout.getMeasuredHeight(), 0, collapsibleLayout, 1111).start();
                    mainActivity.fragmentFAB.setOnClickListener(defaultAction);
                    UserManager.this.addNewItem.setText("add new item");
                    UserManager.this.nameSpace.requestFocus();
                    Animations.alphaAnimator(true, mainActivity.fragmentFAB, 555).start();
                    Animations.alphaAnimator(false, getView().findViewById(R.id.user_manager_add), 555).start();
                }
                UserManager.this.mainIsCollapsed = !UserManager.this.mainIsCollapsed;
                UserManager.this.addNewItem.setGravity(Gravity.CENTER);
            }
        });

        this.snack = (LinearLayout) getActivity().findViewById(R.id.user_manager_snack);

        DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (snack != null) {
                    mainActivity.fragmentFAB.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.calc));
                    if (snack.getMeasuredHeight() > 1) Animations.collapse(snack, 0);
                }
            }
        };

        mainActivity.drawer.addDrawerListener(drawerListener);

        this.mainActivity.fragmentFAB.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.calc));
        this.defaultAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200 * dp);
                if (snack.getLayoutParams().height < 5) {
                    mainActivity.fragmentFAB.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.down));
                    Animations.slideAnimator(0, 200 * dp, snack, 666).start();
                    params.gravity = Gravity.BOTTOM;
                    snack.setLayoutParams(params);
                } else {
                    mainActivity.fragmentFAB.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.calc));
                    Animations.collapse(snack, 0);
                    params.gravity = Gravity.BOTTOM;
                    snack.setLayoutParams(params);
                }
            }
        };
        this.mainActivity.fragmentFAB.setOnClickListener(this.defaultAction);


        this.itemPrice = (EditText) getView().findViewById(R.id.user_manager_price);
        this.itemPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    System.out.println("why price");
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
                    mainActivity.fragmentFAB.setOnClickListener(defaultAction);
                    System.out.println("why price");
                } else {
                    //revoke keyboard
                    ((InputMethodManager) getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    itemName.requestFocus();
                }
            }
        });
        this.initialize();

        Calligrapher calligrapher = new Calligrapher(getContext());
        calligrapher.setFont(view, getResources().getString(R.string.font_fu));
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
        this.scope = (TextView) getView().findViewById(R.id.user_manager_scope);
        this.scopeCollapsible = (LinearLayout) getView().findViewById(R.id.user_manager_scope_collapse_container);

        for (User u : ((MainActivity) getActivity()).userList) {
            LinearLayout holder = new LinearLayout(getContext());
            TextView name = new TextView(getContext());
            name.setText(u.getName());
            CheckBox checker = new CheckBox(getContext());
            holder.addView(checker);
            holder.addView(name);
            this.scopeCollapsible.addView(holder);
            name.getLayoutParams().height = 45 * dp;
        }


        this.saldo.setText(String.valueOf(0));
        this.nameSpace.setText(this.testUser.getName());
        this.total.setText(String.valueOf(this.testUser.getTotalDispense()));

        this.scope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollView collapser = (ScrollView) getView().findViewById(R.id.user_manager_scope_collapse);
                scopeCollapsible.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                InputMethodManager imm = (InputMethodManager) getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (scopeIsCollapsed) {
                    Animations.slideAnimator(0, scopeCollapsible.getMeasuredHeight(), collapser, 555).start();
                    System.out.println("called");
                    scopeIsCollapsed = false;
                } else {
                    Animations.slideAnimator(scopeCollapsible.getMeasuredHeight(), 0, collapser, 555).start();
                    scopeIsCollapsed = true;
                }
            }
        });

        if (this.leaveOpen) {
            LinearLayout collapsibleLayout = (LinearLayout) getView().findViewById(R.id.user_manager_collapsible);
            collapsibleLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int height = collapsibleLayout.getMeasuredHeight();
            collapsibleLayout.getLayoutParams().height = height;
            this.mainIsCollapsed = false;
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

        if (saldo.toBigInteger().doubleValue() > 0) {
            this.saldo.setTextColor(Color.parseColor("#ff5252"));
        } else {
            this.saldo.setTextColor(Color.parseColor("#76ff03"));
        }

        this.total.setText((total.setScale(2, BigDecimal.ROUND_HALF_EVEN)).toString());
        this.saldo.setText((saldo.setScale(2, BigDecimal.ROUND_HALF_EVEN)).multiply(new BigDecimal(-1)).toString());
    }

    @Override
    public void onStop() {
        Bundle sendBack = new Bundle();
        sendBack.putSerializable("user", this.testUser);
        this.snack = null;
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