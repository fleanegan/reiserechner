package com.example.fleanegan.reiserechner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by fleanegan on 20.03.17.
 */

public class ProjectManager extends Fragment {

    int dp;
    LinearLayout scrollContainer;
    MainActivity mainActivity;
    IOManager ioManager;
    LinearLayout temp;
    ArrayList<File> fileList;
    FloatingActionButton fab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.dp = ((int) getResources().getDisplayMetrics().density);

        this.mainActivity = (MainActivity) getActivity();
        this.ioManager = (this.mainActivity).ioManager;

        this.mainActivity.remember = -2;

        return inflater.inflate(R.layout.content_project_manager, container, false);

    }



    @Override
    public void onViewCreated(View view, Bundle onSavedInstanceState) {
        this.fillList();
        this.fab = (FloatingActionButton) getActivity().findViewById(R.id.project_manager_fab);
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("fab called");
                Intent newActivity = new Intent(getActivity(), EditProject.class);
                newActivity.putExtra("name", "");
                newActivity.putExtra("isNew", true);
                newActivity.putExtra("ser", new Serializer());
                startActivityForResult(newActivity, 666);
            }
        });
    }

    public void fillList() {
        this.fileList = ioManager.getFiles();
        this.scrollContainer = (LinearLayout) getView().findViewById(R.id.project_manager_scroll_container);
        for (final File f : this.fileList) {
            //get the data
            final Serializer serializer = ioManager.deserialize(f, true);
            final LinearLayout entry = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.home_day, null);
            TextView projectName = (TextView) entry.findViewById(R.id.home_header);
            TextView projectTotal = (TextView) entry.findViewById(R.id.home_header_sub_total);
            //inject the data
            projectName.setText(f.getName().substring(0, f.getName().toCharArray().length - 4));
            projectTotal.setText(String.valueOf(serializer.getSaveTheAmount()));
            this.scrollContainer.addView(entry);
            //expand the button
            entry.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            final int collapsedHeight = entry.getMeasuredHeight();
            entry.getLayoutParams().height = collapsedHeight;
            final LinearLayout lMenu = (LinearLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.project_menu, null);

            final TextView schraegStrich = (TextView) entry.findViewById(R.id.schraegStrich);
            schraegStrich.setText(".");
            schraegStrich.setTextColor(Color.TRANSPARENT);

            this.temp = entry;

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

            entry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animations animations = new Animations();
                    if (!temp.equals(entry)) {
                        animations.collapse(temp, collapsedHeight);
                        schraegStrich.setText(".");
                        temp = entry;
                    }
                    if (schraegStrich.getText().toString().equals(".")) {
                        entry.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        int height = entry.getMeasuredHeight();
                        animations.expand(entry, height, collapsedHeight);
                        schraegStrich.setText("");
                    } else {
                        animations.collapse(entry, collapsedHeight);
                        schraegStrich.setText(".");
                    }
                }
            });
            entry.addView(lMenu);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode >= 0 && resultCode != 0) {
            System.out.println(String.valueOf(resultCode));
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
