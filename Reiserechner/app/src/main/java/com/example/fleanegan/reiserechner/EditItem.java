package com.example.fleanegan.reiserechner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;

/**
 * Created by fleanegan on 18.03.17.
 */

public class EditItem extends Edit {

    int position;
    Item item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.edit);
        getLayoutInflater().inflate(R.layout.content_edit_item, (ScrollView) findViewById(R.id.edit_scroll_view));
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if (intent.hasExtra("item")) this.item = (Item) intent.getExtras().get("item");
        if (intent.hasExtra("position")) this.position = (int) intent.getExtras().get("position");

        this.initialize(savedInstanceState);
    }

    public void initialize(Bundle savedInstanceState) {
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
        if (this.approved) {
            System.out.println("called");
            Intent dataToSendBack = new Intent();
            item.setName(itemName.getText().toString());
            item.setDescription(itemDescription.getText().toString());
            item.setPrice(itemPrice.getText().toString());
            dataToSendBack.putExtra("position", position);
            setResult(1, dataToSendBack.putExtra("item", item));
            getFragmentManager().popBackStackImmediate();
        } else setResult(0);
        super.finish();
    }
}
