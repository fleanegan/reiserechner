package com.example.fleanegan.reiserechner;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import me.anwarshahriar.calligrapher.Calligrapher;

import static com.example.fleanegan.reiserechner.R.string.navigation_drawer_open;

public class MainActivity extends AppCompatActivity {

    int remember = -1;
    boolean weird = false;
    boolean intialized = false;
    float screenWidth;
    float pushHomeInitPos;
    float rightSide;
    float screenHeight;
    float drawerSlideOffset;
    Integer userId = 0;
    Integer dp;
    String serFileName = "new project.ser";
    ArrayList<User> userList;
    File saveTo;
    View headerView;
    View dummyView;
    DrawerLayout drawer;
    NavigationView navigationView;
    FloatingActionButton pushHome;
    FloatingActionButton fragmentFAB;
    TextView saferDeletionTextView = null;
    LinearLayout testNavMenuAlternative;
    IOManager ioManager = new IOManager(this);
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;

    @Override
    public void onSaveInstanceState(Bundle out) {
        //bugfix -> illegalStateException
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(this.serFileName.substring(0, this.serFileName.toCharArray().length - 4));

        LinearLayout buttonBox = (LinearLayout) findViewById(R.id.button_container);
        buttonBox.removeAllViews();

        this.dp = ((int) getResources().getDisplayMetrics().density);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        this.screenWidth = displaymetrics.widthPixels;
        this.screenHeight = displaymetrics.heightPixels;

        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.pushHomeInitPos = 200 * dp;
        if (fragmentFAB != null)
            fragmentFAB.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        this.toggle = new ActionBarDrawerToggle(
                this, this.drawer, toolbar, navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View view) {
                neutralize();
            }

            @Override
            public void onDrawerClosed(View view) {
                //pushHome.setAlpha(1f);
                //pushHome.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (DrawerLayout.STATE_IDLE == newState && drawerSlideOffset == 1)
                    if (fragmentFAB.getTranslationX() >= -65 * dp && fragmentFAB.getTranslationX() < 30 * dp) {
                    }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                pushHome.setVisibility(View.VISIBLE);
                if (slideOffset > 0.9f) {
                    pushHome.setAlpha(1f - new AccelerateInterpolator(0.3f).getInterpolation(Math.abs((slideOffset - 1) * 10)));
                    drawerSlideOffset = 1;
                } else if (slideOffset < 0.8f) {
                    pushHome.setAlpha(0f);
                    if (fragmentFAB != null)
                        fragmentFAB.setX((rightSide - (rightSide - pushHomeInitPos - pushHome.getWidth() / 2 - (int) getResources().getDimension(R.dimen.fab_margin)) * (slideOffset) * 1f - (int) getResources().getDimension(R.dimen.fab_margin) - fragmentFAB.getWidth()));
                }
            }
        };
        this.drawer.setDrawerListener(toggle);
        toggle.syncState();

        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.initialize();

        ArrayList<File> fileList = this.ioManager.getFiles();
        if (fileList.size() == 0)
            this.saveTo = new File((this.getApplicationContext().getFileStreamPath(this.serFileName).getPath()));
        else this.saveTo = fileList.get(0);

        this.ioManager.deserialize(this.saveTo, false);

        this.ioManager.getFiles();

        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, getResources().getString(R.string.font_fu), true);
    }


    /**
     * makes the home screen appear on every launch
     */
    @Override
    public void onResumeFragments() {
        this.pushUserToFragment(this.remember);
    }


    /**
     * prepares globally used views and other stuff.
     * Mainly occupied in order to get the navbar working.
     */
    protected void initialize() {
        this.pushHome = (FloatingActionButton) findViewById(R.id.nav_push_home);
        this.rightSide = this.screenWidth;
        this.pushHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushUserToFragment(-1);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        LinearLayout header = (LinearLayout) nav.findViewById(R.id.test_nav_menu_alternative);
        this.headerView = header.getChildAt(0);

        Button confirmUserAdd = (Button) this.headerView.findViewById(R.id.add_user_interface_confirm);
        confirmUserAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userInput = (EditText) MainActivity.this.headerView.findViewById(R.id.add_user_interface_input);
                MainActivity.this.addUser(userInput.getText().toString(), false);
                userInput.setText("");
                userInput.requestFocus();
            }
        });
    }


    /**
     * declares the behavior of the app when the back button is tapped-
     * default is toggle nav drawer.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (this.remember == -2) {
            this.remember = -1;
            this.pushUserToFragment(-1);
            return;
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.pushUserToFragment(-1);
        return true;
    }


    /**
     * Add the user to the user-list and creates a view for it in the navdrawer
     *
     * @param alreadyAdded: only true when called by the serializer in order to prevent
     *                      the program to multiply users.
     * @param name:         name of the user. in this very function it is going to be written to the navdrawer.
     */
    protected void addUser(String name, Boolean alreadyAdded) {
        //gets all the necessary stuff -> condition attempts to reduce calculation
        if (!name.equals("")) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.menu_item, null);
            final TextView testName = (TextView) myView.findViewById(R.id.test_nav_menu_alternative_name);
            if (this.testNavMenuAlternative == null) {
                this.testNavMenuAlternative = (LinearLayout) this.navigationView.findViewById(R.id.test_nav_menu_alternative_scrollable);
            }
            //setting values to the item-specific view
            testName.setText(name);
            testName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout test = (LinearLayout) myView.getParent();
                    MainActivity.this.drawer.closeDrawer(Gravity.LEFT);
                    pushUserToFragment(test.indexOfChild(myView));
                }
            });

            final Button testRemove = (Button) myView.findViewById(R.id.test_nav_menu_alternative_remove);
            testRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout test = (LinearLayout) myView.getParent();
                    Integer id = test.indexOfChild(myView);
                    removeUser(id, testRemove);
                }
            });

            myView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) System.out.println(" 01remove has focus");
                    else System.out.println(" 02remove has lost focus");
                }
            });

            this.testNavMenuAlternative.addView(myView);
            if (!alreadyAdded) {
                this.userList.add(new User(name, this.userId));
                this.userId++;
            }
        }
    }

    /**
     * enables the deselection of the zh -> deletion
     */
    public void neutralize() {
        if (this.saferDeletionTextView != null) {
            this.saferDeletionTextView.setTextColor(Color.parseColor("#00ff0000"));
            this.saferDeletionTextView = null;
        }
    }

    /**
     * uses a double-tap-mechanism in order to prevent the customer
     * to accidentally delete a user.
     *
     * @param id:       position of user-view in @see this.testNavMenuAlternative
     * @param textView: zh, needed to enable double-tap-security
     */
    public void removeUser(int id, TextView textView) {
        if (!textView.equals(this.saferDeletionTextView)) {
            if (this.saferDeletionTextView != null)
                this.saferDeletionTextView.setTextColor(Color.parseColor("#00ff0000"));
            this.saferDeletionTextView = textView;
            textView.setTextColor(Color.parseColor("#E53935"));
            return;
        } else {
            this.saferDeletionTextView = null;
        }
        for (Item i : this.userList.get(id).getBoughtItems()) {
            User.itemList.remove(i);
        }
        User.totalAmount = User.totalAmount.subtract(this.userList.get(id).getTotalDispense());
        this.userList.remove(id);
        User.numberOfUsers--;
        this.testNavMenuAlternative.removeViewAt(id);
        this.pushUserToFragment(-1);
    }

    /**
     * Fills and calls the fragment with home or user respectively.
     *
     * @param id: position in @see this.userList. if id < 0 -> home or descendant
     */
    public void pushUserToFragment(Integer id) {
        Bundle alreadyDone = new Bundle();
        neutralize();
        Fragment fragment;
        if (id >= 0) {
            alreadyDone.putSerializable("user", this.userList.get(id));
            alreadyDone.putBoolean("leaveOpen", false);
            fragment = new UserManager();
        } else if (id == -1) {
            fragment = new HomeManager();
            this.ioManager.serialize(this.saveTo);
        } else {
            fragment = new ProjectManager();
        }

        this.remember = id;

        if (this.weird) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            this.toggle.syncState();
            //this.toggle.syncState();
            this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
            this.weird = false;
        }

        if (this.fragmentFAB != null) {
            this.fragmentFAB.setVisibility(View.VISIBLE);
        }
        this.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);



        LinearLayout buttonBox = (LinearLayout) findViewById(R.id.button_container);
        buttonBox.removeAllViewsInLayout();
        fragment.setArguments(alreadyDone);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.button_container, fragment);
        transaction.commit();
    }


    @Override
    public void onStop() {
        this.ioManager.serialize(this.saveTo);
        LinearLayout buttonBox = (LinearLayout) findViewById(R.id.button_container);
        buttonBox.removeAllViewsInLayout();

        super.onStop();
    }
}
