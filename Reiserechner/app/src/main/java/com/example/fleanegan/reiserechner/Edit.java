package com.example.fleanegan.reiserechner;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

/**
 * Created by fleanegan on 22.03.17.
 */

public class Edit extends AppCompatActivity {

    FloatingActionButton fabAccept;
    boolean approved;
    EditText itemPrice;
    EditText itemName;
    MultiAutoCompleteTextView itemDescription;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.itemPrice = (EditText) findViewById(R.id.edit_item_price);
        this.itemName = (EditText) findViewById(R.id.edit_item_name);
        this.itemDescription = (MultiAutoCompleteTextView) findViewById(R.id.edit_item_description);

        this.fabAccept = (FloatingActionButton) findViewById(R.id.edit_item_fab);

        this.fabAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                v.requestFocus();
                approved = true;
                finish();
            }
        });
        this.fabAccept.requestFocus();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}
