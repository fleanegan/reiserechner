package com.example.fleanegan.reiserechner;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import me.anwarshahriar.calligrapher.Calligrapher;

/**
 * Created by fleanegan on 20.03.17.
 */

public class ProjectManager extends Fragment {

    int dp;
    boolean isScrolled = false;
    LinearLayout scrollContainer;
    MainActivity mainActivity;
    IOManager ioManager;
    ArrayList<File> fileList;
    HorizontalScrollView temp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.dp = ((int) getResources().getDisplayMetrics().density);

        this.mainActivity = (MainActivity) getActivity();
        this.ioManager = (this.mainActivity).ioManager;

        this.mainActivity.remember = -2;

        return inflater.inflate(R.layout.content_manager_projects, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle onSavedInstanceState) {
        this.fillList();
        this.mainActivity.fragmentFAB.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.add));
        this.mainActivity.fragmentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newActivity = new Intent(getActivity(), EditProject.class);
                newActivity.putExtra("name", "");
                newActivity.putExtra("isNew", true);
                newActivity.putExtra("ser", new Serializer());
                startActivityForResult(newActivity, 666);
            }
        });
        Calligrapher calligrapher = new Calligrapher(getContext());
        calligrapher.setFont(view, getResources().getString(R.string.font_fu));

        this.mainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        this.mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
        this.mainActivity.weird = true;
        this.mainActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });
    }

    public void fillList() {
        this.fileList = ioManager.getFiles();
        this.scrollContainer = (LinearLayout) getView().findViewById(R.id.project_manager_scroll_container);
        for (final File f : this.fileList) {
            //get the data
            final Serializer serializer = ioManager.deserialize(f, true);
            final HorizontalScrollView entry = (HorizontalScrollView) LayoutInflater.from(this.getContext()).inflate(R.layout.project_menu, null);

            final LinearLayout lMenu = (LinearLayout) entry.findViewById(R.id.project_manager_menu_container);
            lMenu.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            final int lMenuSize = lMenu.getMeasuredWidth();

            TextView projectName = (TextView) entry.findViewById(R.id.project_manager_menu_label);
            //inject the data
            projectName.setText(f.getName().substring(0, f.getName().toCharArray().length - 4));
            projectName.getLayoutParams().width = Math.round(this.mainActivity.screenWidth);
            projectName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (temp != null)
                        ObjectAnimator.ofInt(temp, "scrollX", 0).setDuration(1111).start();
                    temp = entry;
                    if (!isScrolled) {
                        ObjectAnimator.ofInt(entry, "scrollX", lMenuSize).setDuration(1111).start();
                        isScrolled = true;
                    } else {
                        ObjectAnimator.ofInt(entry, "scrollX", 0).setDuration(1111).start();
                        isScrolled = false;
                    }
                }
            });
            this.scrollContainer.addView(entry);
            //expand the button


            entry.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        int scrollX = entry.getScrollX();
                        if (scrollX != 0) {
                            if (!entry.equals(temp)) {
                                ObjectAnimator.ofInt(temp, "scrollX", 0).setDuration(1111).start();
                                isScrolled = true;
                                temp = entry;
                            }
                        }
                    }
                    return false;
                }
            });


            TextView menuDelete = (TextView) lMenu.findViewById(R.id.project_manager_menu_delete);
            menuDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ioManager.deleteProject(f);
                    scrollContainer.removeAllViews();
                    fillList();
                }
            });

            TextView menuRename = (TextView) lMenu.findViewById(R.id.project_manager_menu_rename);
            menuRename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newActivity = new Intent(getActivity(), EditProject.class);
                    newActivity.putExtra("name", f.getName().substring(0, f.getName().toCharArray().length - 4));
                    newActivity.putExtra("ser", serializer);
                    startActivityForResult(newActivity, fileList.indexOf(f));
                }
            });
            TextView menuLoad = (TextView) lMenu.findViewById(R.id.project_manager_menu_load);
            menuLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ioManager.loadProject(f, false);
                }
            });

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode >= 0 && resultCode != 0) {
            boolean isToBeLoaded = (boolean) data.getExtras().get("load");
            String name = data.getExtras().get("name") + ".ser";
            System.out.println("the project " + name + " will be loaded: " + isToBeLoaded);


            if (resultCode == 2) {
                File newFile = new File(this.mainActivity.getApplicationContext().getFileStreamPath(name).getPath());
                this.ioManager.loadProject(newFile, false);
                this.scrollContainer.removeAllViews();
                this.ioManager.serialize(newFile);
                this.fillList();
            } else {
                File file = this.fileList.get(requestCode);
                System.out.println("The file " + file.getName());
                File newFile = file;

                if (!name.equals(file.getName())) {
                    newFile = this.ioManager.rename(name, file);
                    System.out.println("will be renamed to " + name);
                }

                if (isToBeLoaded) {
                    System.out.println(name + "and loaded afterwards.");
                    this.ioManager.loadProject(newFile, false);
                }
            }
        }
        this.mainActivity.pushUserToFragment(-2);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
