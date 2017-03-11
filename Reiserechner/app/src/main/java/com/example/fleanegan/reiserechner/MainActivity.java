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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Integer userId = 0;
    DrawerLayout drawer;
    NavigationView navigationView;
    LinearLayout addUserLayout;
    LinearLayout addUserInterfaceLayout;
    View headerView;
    Integer dp;
    Button cancelUserAdd;
    ArrayList<User> userList;
    String serFileName = "static.ser";
    File saveTo;
    int saferDeletion = -1;
    TextView saferDeletionTextView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout buttonBox = (LinearLayout) findViewById(R.id.button_container);
        buttonBox.removeAllViews();

        this.dp = ((int) getResources().getDisplayMetrics().density);

        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
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
        this.navigationView.setNavigationItemSelectedListener(this);
        this.addButtonsAllOverThePlaace();

        this.saveTo = new File((this.getApplicationContext().getFileStreamPath(this.serFileName).getPath()));
        this.deserialize(this.saveTo);
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

    private void addButtonsAllOverThePlaace() {

        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        LinearLayout header = (LinearLayout) nav.findViewById(R.id.test_nav_menu_alternative);
        this.headerView = header.getChildAt(0);
        this.addUserInterfaceLayout = (LinearLayout) this.headerView.findViewById(R.id.add_user_interface);

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

        scrollView.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.addView(verticalFill1);
        buttonBox.addView(scrollView);


        //locate the header
        this.addUserLayout = (LinearLayout) headerView.findViewById(R.id.add_user);
        this.addUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.openManageUserAddDialog();
                neutralize();
            }
        });

        TextView placeHeader = (TextView) headerView.findViewById(R.id.adapt_to_status_bar);
        placeHeader.getLayoutParams().height = this.getStatusBarHeight();
        Log.d("STATUSBAR", "Height = " + this.getStatusBarHeight());
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
                MainActivity.this.modifyNavDrawer(userInput.getText().toString(), false);
                userInput.setText("");
                userInput.requestFocus();
            }
        });
    }

    public void pushHome(View view) {

    }

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
                addUserLayout.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
            }
        });
    }

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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public ArrayList<ArrayList<Item>> getDateSortedUserList() {
        ArrayList<ArrayList<Item>> container = new ArrayList<>();
        ArrayList<Item> sortedByDate = new ArrayList<>();
        sortedByDate = User.itemList;
        Collections.sort(sortedByDate, new Comparator<Item>() {
                    @Override
                    public int compare(Item o1, Item o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                }
        );
        int day = -1;
        ArrayList holderList = new ArrayList();
        for (Item i : sortedByDate) {
            if (day == i.getDate().getDay()) {
                //append item to same-day-list
                holderList.add(i);
            } else {
                //open new list

            }
            System.out.println("day: " + day);

            day = i.getDate().getDay();
        }
        return new ArrayList<ArrayList<Item>>();
    }


    private void modifyNavDrawer(String name, Boolean alreadyAdded) {
        if (!name.equals("")) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.menu_item, null);
            final TextView testName = (TextView) myView.findViewById(R.id.test_nav_menu_alternative_name);
            testName.setText(name);
            testName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout test = (LinearLayout) myView.getParent();
                    pushUserToFragment(test.indexOfChild(myView));
                    drawer.closeDrawer(Gravity.LEFT);
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
            LinearLayout testNavMenuAlternative = (LinearLayout) navigationView.findViewById(R.id.test_nav_menu_alternative_scrollable);
            View dummyView = navigationView.findViewById(R.id.dummy);
            dummyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    neutralize();
                }
            });

            testNavMenuAlternative.addView(myView);
            if (!alreadyAdded) this.addUser(name);
        }
    }


    public void neutralize() {
        if (saferDeletionTextView != null)
            saferDeletionTextView.setTextColor(Color.parseColor("#00ff0000"));
        saferDeletion = -1;
    }

    public void addUser(String name) {
        this.userList.add(new User(name, this.userId));
        Log.d("indexMystery", this.userId + name);
        this.userId++;
    }

    public void removeUser(int id, TextView textView) {
        if (id != saferDeletion) {
            if (saferDeletionTextView != null)
                saferDeletionTextView.setTextColor(Color.parseColor("#00ff0000"));
            saferDeletion = id;
            saferDeletionTextView = textView;
            textView.setTextColor(Color.parseColor("#E53935"));
            return;
        } else {
            saferDeletion = -1;
            saferDeletionTextView = null;
        }
        User.totalAmount = User.totalAmount.subtract(this.userList.get(id).getTotalDispense());
        this.userList.remove(id);
        User.numberOfUsers--;
        LinearLayout userOverView = (LinearLayout) navigationView.findViewById(R.id.test_nav_menu_alternative_scrollable);
        userOverView.removeViewAt(id);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        this.cancelUserAdd.performClick();
        this.pushUserToFragment(id);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void pushUserToFragment(Integer id) {
        Bundle alreadyDone = new Bundle();
        neutralize();
        alreadyDone.putSerializable("user", this.userList.get(id));
        alreadyDone.putBoolean("leaveOpen", false);
        Fragment fragment = new UserManager();
        LinearLayout buttonBox = (LinearLayout) findViewById(R.id.button_container);
        buttonBox.removeAllViewsInLayout();
        fragment.setArguments(alreadyDone);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.button_container, fragment);
        transaction.commit();
    }

    @Override
    public void onStop() {
        this.serialize(this.saveTo);
        LinearLayout buttonBox = (LinearLayout) findViewById(R.id.button_container);
        buttonBox.removeAllViewsInLayout();
        super.onStop();
    }

    public void serialize(File serFileName) {
        Log.d("SERIALIZATION", "initialized");

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(serFileName), "utf-8"))) {
            writer.write("");
        } catch (Exception ef) {
            ef.getCause();
        }

        Serializer serializer = new Serializer();
        serializer.addUser(this.userList, User.totalAmount);

        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(serFileName);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(serializer);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("SERIALIZATION", "terminated");
    }

    public void deserialize(File fileName) {
        Log.d("DE-SERIALIZATION", "initialized");
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        Serializer serializer = new Serializer();

        try {
            fileInputStream = new FileInputStream(fileName.getAbsolutePath());
            objectInputStream = new ObjectInputStream(fileInputStream);
            serializer = (Serializer) objectInputStream.readObject();
        } catch (Exception e) {
        }
        this.userList = serializer.getUserArrayList();

        User.totalAmount = serializer.getSaveTheAmount();
        User.numberOfUsers = serializer.getNumberOfUsers();

        for (User u : this.userList) {
            Log.d("DE-SERIALIZATION", "accessed");
            String cc = u.getName();
            this.userId++;
            modifyNavDrawer(cc, true);
        }
    }

}
