package com.example.fleanegan.reiserechner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

/**
 * Created by fleanegan on 18.03.17.
 */

public class EditItem extends AppCompatActivity {

    int position;
    Item item;
    Button apply;
    EditText itemPrice;
    EditText itemName;
    MultiAutoCompleteTextView itemDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_item);

        Intent intent = getIntent();
        if (intent.hasExtra("item")) this.item = (Item) intent.getExtras().get("item");
        if (intent.hasExtra("position")) this.position = (int) intent.getExtras().get("position");

        this.itemPrice = (EditText) findViewById(R.id.edit_item_price);
        this.itemName = (EditText) findViewById(R.id.edit_item_name);
        this.itemDescription = (MultiAutoCompleteTextView) findViewById(R.id.edit_item_description);
        this.apply = (Button) findViewById(R.id.edit_item_apply);
        this.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditItem.this.finish();
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.getString("itemPrice").equals(""))
                this.itemPrice.setText(String.valueOf(item.getPrice()));
            else this.itemPrice.setText(savedInstanceState.getString("itemPrice"));

            if (savedInstanceState.getString("itemName").equals(""))
                this.itemName.setText(item.getName());
            else this.itemPrice.setText(savedInstanceState.getString("itemName"));

            if (savedInstanceState.getString("itemDescription").equals(""))
                this.itemDescription.setText(item.getDescription());
            else this.itemPrice.setText(savedInstanceState.getString("itemDescription"));
        } else {
            this.itemPrice.setText(String.valueOf(item.getPrice()));
            this.itemName.setText(item.getName());
            this.itemDescription.setText(item.getDescription());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("itemPrice", this.itemPrice.getText().toString());
        savedInstanceState.putString("itemName", this.itemName.getText().toString());
        savedInstanceState.putString("itemDescription", this.itemDescription.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void finish() {
        Intent dataToSendBack = new Intent();
        item.setName(itemName.getText().toString());
        item.setDescription(itemDescription.getText().toString());
        item.setPrice(itemPrice.getText().toString());
        dataToSendBack.putExtra("position", position);
        setResult(1, dataToSendBack.putExtra("item", item));
        getFragmentManager().popBackStackImmediate();
        super.finish();
    }
}
