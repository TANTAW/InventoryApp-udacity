package com.apps.viscar.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddQuantityActivity extends AppCompatActivity {


    private EditText quantityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quantity);
        quantityEditText = (EditText) findViewById(R.id.edit_quantity);
        FloatingActionButton fabEditQuantity = (FloatingActionButton) findViewById(R.id.fab_edit_quantity);
        fabEditQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantity = quantityEditText.getText().toString().trim();
                if (quantity == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_quantity), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(AddQuantityActivity.this, DetailsActivity.class);
                    intent.putExtra("quantity", quantity);
                    startActivity(intent);
                }
            }
        });
    }
}
