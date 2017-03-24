package com.example.fleanegan.reiserechner;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static com.example.fleanegan.reiserechner.R.string.navigation_drawer_open;

public class MainActivity extends AppCompatActivity {

    int remember = -1;
    boolean intialized = false;
    Integer userId = 0;
    Integer dp;
    String serFileName = "new project.ser";
    ArrayList<User> userList;
    File saveTo;
    View headerView;
    View dummyView;
    DrawerLayout drawer;
    NavigationView navigationView;
    Button cancelUserAdd;
    TextView saferDeletionTextView = null;
    LinearLayout addUserLayout;
    LinearLayout addUserInterfaceLayout;
    LinearLayout testNavMenuAlternative;
    IOManager ioManager = new IOManager(this);

    ActionBarDrawerToggle mDrawerToggle;
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
        getSupportActionBar().setTitle(this.serFileName.substring(0, this.serFileName.toCharArray().length - 4));

        LinearLayout buttonBox = (LinearLayout) findViewById(R.id.button_container);
        buttonBox.removeAllViews();

        this.dp = ((int) getResources().getDisplayMetrics().density);

        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.drawer, toolbar, navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                MainActivity.this.addUserLayout.getLayoutParams().height = 80 * dp;
                MainActivity.this.addUserInterfaceLayout.getLayoutParams().height = 1;
                //weirdest bug ever. If bgcolor not set -> bar disapears
                MainActivity.this.addUserInterfaceLayout.setBackgroundColor(Color.TRANSPARENT);
                neutralize();
            }

            @Override
            public void onDrawerOpened(View view) {
                addUserInterfaceLayout.requestFocus();
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

        this.mDrawerToggle = new ActionBarDrawerToggle(this, drawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        this.drawer.setDrawerListener(this.mDrawerToggle);
    }


    /**
     * makes the home screen appear on every launch
     */
    @Override
    public void onResumeFragments() {
        this.pushUserToFragment(this.remember);
    }


    /**
     * layout reasons
     *
     * @return status bar height
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * prepares globally used views and other stuff.
     * Mainly occupied in order to get the navbar working.
     */
    protected void initialize() {
        final TextView pushHome = (TextView) findViewById(R.id.nav_bar_push_home);
        pushHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushUserToFragment(-1);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        LinearLayout header = (LinearLayout) nav.findViewById(R.id.test_nav_menu_alternative);
        this.headerView = header.getChildAt(0);
        this.addUserInterfaceLayout = (LinearLayout) this.headerView.findViewById(R.id.add_user_interface);
        //locate the header
        this.addUserLayout = (LinearLayout) headerView.findViewById(R.id.add_user);
        this.addUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.openManageUserAddDialog();
                neutralize();
            }
        });

        TextView placeHeader = (TextView) this.headerView.findViewById(R.id.adapt_to_status_bar);
        placeHeader.getLayoutParams().height = this.getStatusBarHeight();
        placeHeader.setHeight(this.getStatusBarHeight());

        //manage the user_add_interface
        this.cancelUserAdd = (Button) this.headerView.findViewById(R.id.add_user_interface_cancel);
        this.cancelUserAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.closeManageUserAddDialog();
            }
        });

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
     * wrapper for managing a clean user-adding-experience - closer
     */
    private void closeManageUserAddDialog() {
        //collapses keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(MainActivity.this.addUserInterfaceLayout.getWindowToken(), 0);
        //switches back
        Animations animator = new Animations();
        animator.fade(MainActivity.this.addUserInterfaceLayout, "out");
        animator.expand(MainActivity.this.addUserLayout, 80, 0);
        MainActivity.this.addUserInterfaceLayout.getLayoutParams().height = 1;
        MainActivity.this.addUserLayout.post(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.addUserLayout.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
            }
        });
    }

    /**
     * wrapper for managing a clean user-adding-experience - opener
     */
    public void openManageUserAddDialog() {
        Animations animator = new Animations();
        animator.collapse(MainActivity.this.addUserLayout, 0);
        this.addUserInterfaceLayout = (LinearLayout) this.headerView.findViewById(R.id.add_user_interface);
        this.addUserInterfaceLayout.getLayoutParams().height = findViewById(R.id.nav_boss).getMeasuredHeight();
        animator.fade(this.addUserInterfaceLayout, "in");
        EditText editText = (EditText) this.headerView.findViewById(R.id.add_user_interface_input);


        //revoke keyboard
        editText.requestFocus();
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                editText.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }


    /**
     * declares the behavior of the app when the back button is tapped-
     * default is toggle nav drawer. TODO: Figure out if customers appreciate this behavior
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        System.out.println("drawertest " + this.remember);
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
                this.dummyView = navigationView.findViewById(R.id.dummy);
                this.dummyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        neutralize();
                    }
                });
            }

            //setting values to the item-specific view
            testName.setText(name);
            testName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout test = (LinearLayout) myView.getParent();
                    pushUserToFragment(test.indexOfChild(myView));
                    Log.d("indexer", String.valueOf(test.indexOfChild(myView)));
                    MainActivity.this.drawer.closeDrawer(Gravity.LEFT);
                }
            });

            final TextView testRemove = (TextView) myView.findViewById(R.id.test_nav_menu_alternative_remove);
            testRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout test = (LinearLayout) myView.getParent();
                    Integer id = test.indexOfChild(myView);
                    removeUser(id, testRemove);
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
     * @param id: position of user-view in @see this.testNavMenuAlternative
     * @param textView: zh, needed to enable double-tap-security
     *
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
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            alreadyDone.putSerializable("user", this.userList.get(id));
            alreadyDone.putBoolean("leaveOpen", false);
            fragment = new UserManager();
        } else if(id == -1) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragment = new HomeManager();
            this.ioManager.serialize(this.saveTo);
        } else {
            fragment = new ProjectManager();
        }

        this.remember = id;

        LinearLayout buttonBox = (LinearLayout) findViewById(R.id.button_container);
        buttonBox.removeAllViewsInLayout();
        fragment.setArguments(alreadyDone);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.button_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    //show hamburger
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    mDrawerToggle.syncState();
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onStop() {
        this.ioManager.serialize(this.saveTo);
        LinearLayout buttonBox = (LinearLayout) findViewById(R.id.button_container);
        buttonBox.removeAllViewsInLayout();

        super.onStop();
    }
}
