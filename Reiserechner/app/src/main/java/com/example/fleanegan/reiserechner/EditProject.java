package com.example.fleanegan.reiserechner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

/**
 * Created by fleanegan on 21.03.17.
 */

public class EditProject extends Edit {

    Serializer serializer;
    boolean isToBeLoaded = false;
    boolean isNew = false;
    String name;
    EditText projectName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.edit);
        getLayoutInflater().inflate(R.layout.content_edit_project, (ScrollView) findViewById(R.id.edit_scroll_view));
        super.onCreate(savedInstanceState);

        this.serializer = (Serializer) getIntent().getExtras().get("ser");
        this.name = getIntent().getExtras().getString("name");


        this.projectName = (EditText) findViewById(R.id.edit_project_name);
        this.projectName.setText(name);

        if (getIntent().getExtras().getBoolean("isNew")) this.isNew = true;

        final String blockCharacterSet = "~#^|$%&*!/.:\\;"; //Special characters to block
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source != null && blockCharacterSet.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };
        this.projectName.setFilters(new InputFilter[]{filter});

        this.initialize(savedInstanceState);
    }

    public void initialize(Bundle savedInstanceState) {
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.finish();
    }

    @Override
    public void finish() {
        if (this.approved) {
            name = projectName.getText().toString();
            Intent dataToSendBack = new Intent();
            dataToSendBack.putExtra("load", isToBeLoaded);
            dataToSendBack.putExtra("name", name);
            if (isNew) setResult(2, dataToSendBack);
            else setResult(1, dataToSendBack);
        } else setResult(0);
        if (this.projectName.getText().toString().equals("")) {
            this.projectName.setBackgroundColor(Color.CYAN);
            this.projectName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        projectName.setBackgroundColor(Color.parseColor("#E0E0E0"));
                        projectName.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                    } else if (!hasFocus && projectName.getText().toString().equals("")) {
                        projectName.setTextColor(Color.CYAN);
                    }
                }
            });
            return;
        }
        super.finish();
    }
}
