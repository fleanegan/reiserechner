package com.example.fleanegan.reiserechner;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.ActionMenuView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView putResults;
    Integer userId = 0;
    DrawerLayout drawer;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        this.addButtonsAllOverThePlaace();
    }

    //maybe used to get the navdrawer a nicer height
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
        final AlertDialog.Builder buildAddUser = new AlertDialog.Builder(this);
        final EditText nameInput = new EditText(this);
        MainActivity.this.drawer.closeDrawer(Gravity.LEFT);

        buildAddUser.setTitle("Enter name")
                .setView(nameInput)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.modifyNavDrawer(nameInput.getText().toString());
                    }
                });

        AlertDialog addUser = buildAddUser.create();
        addUser.show();

    }

    private void addButtonsAllOverThePlaace() {
        LinearLayout buttonBox = (LinearLayout) findViewById(R.id.button_container);
        Button testButton = new Button(this);
        testButton.setText("this is a dummy button");
        testButton.setWidth(96);
        testButton.setHeight(96);
        buttonBox.addView(testButton);

        LinearLayout verticalFill1 = new LinearLayout(this);

        final HorizontalScrollView scrollView = new HorizontalScrollView(this);

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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        LinearLayout addUserLayout = (LinearLayout) headerview.findViewById(R.id.add_user);
        Log.d("ADDUSER", "Height = " + addUserLayout.getHeight());
        addUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.addUser();
            }
        });

        TextView placeHeader = (TextView) headerview.findViewById(R.id.adapt_to_status_bar);
        placeHeader.getLayoutParams().height = this.getStatusBarHeight();
        Log.d("STATUSBAR", "Height = " + this.getStatusBarHeight());
        placeHeader.setHeight(this.getStatusBarHeight());


        putResults = (TextView) findViewById(R.id.put_results);
        scrollView.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.addView(verticalFill1);
        buttonBox.addView(scrollView);


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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
