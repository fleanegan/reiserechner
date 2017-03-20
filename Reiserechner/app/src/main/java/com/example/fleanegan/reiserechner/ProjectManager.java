package com.example.fleanegan.reiserechner;

import android.graphics.Color;
import android.os.Bundle;
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
        ArrayList<File> fileList = ioManager.getFiles();
        this.scrollContainer = (LinearLayout) getView().findViewById(R.id.project_manager_scroll_container);
        for (final File f : fileList) {
            //get the data
            Serializer serializer = ioManager.deserialize(f, true);
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
            TextView menuRename = (TextView) lMenu.findViewById(R.id.project_manager_menu_rename);
            TextView menuLoad = (TextView) lMenu.findViewById(R.id.project_manager_menu_load);
            menuLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ioManager.switchProjects(f);
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
    public void onStop() {
        super.onStop();
    }
}
