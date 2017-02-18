package com.example.fleanegan.reiserechner;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView putResults;
    Integer userId = 0;
    DrawerLayout drawer;
    NavigationView navigationView;
    LinearLayout addUserLayout;
    LinearLayout addUserInterfaceLayout;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.setDrawerListener(toggle);
        toggle.syncState();

        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);
        this.addButtonsAllOverThePlaace();
    }

    // used to get the navdrawer a nicer height
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    private void modifyNavDrawer(String name) {
        if (!name.equals("")) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            menu.add(R.id.user_list, this.userId, Menu.NONE, name);
            this.userId++;
        }
    }

    public void addUser() {
        Animations animator = new Animations();
        animator.collapse(MainActivity.this.addUserLayout);
        this.addUserInterfaceLayout = (LinearLayout) this.headerView.findViewById(R.id.add_user_interface);
        animator.expand(addUserInterfaceLayout, 125);
        //MainActivity.this.modifyNavDrawer(nameInput.getText().toString());
    }


    private void addButtonsAllOverThePlaace() {
        LinearLayout buttonBox = (LinearLayout) findViewById(R.id.button_container);

        //ScrollViewStuff
        LinearLayout verticalFill1 = new LinearLayout(this);
        final HorizontalScrollView scrollView = new HorizontalScrollView(this);

        //Put the stupid buttons in.
        for (int i = 0; i < 20; i++) {
            final Button tempButton = new Button(this);
            final Integer direction = (int) (Math.random() * 19) + 1;
            tempButton.setText(String.valueOf(direction) + "\n" + (1 + i));
            tempButton.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tempButton.setWidth(64);
            tempButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollView.smoothScrollTo(tempButton.getMeasuredWidth() * (direction - 1), 0);
                }
            });
            verticalFill1.addView(tempButton);
        }

        //locate the header
        this.headerView = this.navigationView.getHeaderView(0);
        this.addUserLayout = (LinearLayout) headerView.findViewById(R.id.add_user);
        Log.d("ADDUSER", "Height = " + this.addUserLayout.getHeight());
        this.addUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.addUser();
            }
        });

        //this resized the dummy TextView in order to push the green header pic to the right pos.
        TextView placeHeader = (TextView) headerView.findViewById(R.id.adapt_to_status_bar);
        placeHeader.getLayoutParams().height = this.getStatusBarHeight();
        Log.d("STATUSBAR", "Height = " + this.getStatusBarHeight());
        placeHeader.setHeight(this.getStatusBarHeight());


        putResults = (TextView) findViewById(R.id.put_results);
        scrollView.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.addView(verticalFill1);
        buttonBox.addView(scrollView);


        //manage the user_add_interface
        Button cancelUserAdd = (Button) this.headerView.findViewById(R.id.add_user_interface_cancel);
        cancelUserAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animations animator = new Animations();
                animator.expand(MainActivity.this.addUserLayout, 80);

                AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(100);
                MainActivity.this.addUserInterfaceLayout.startAnimation(anim);
                MainActivity.this.addUserInterfaceLayout.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        this.putResults.setText(String.valueOf(id) + " height is: " + String.valueOf(this.getStatusBarHeight()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
