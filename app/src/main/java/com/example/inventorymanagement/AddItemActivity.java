package com.example.inventorymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {
    private EditText etItemName;
    private EditText etPrice;
    private EditText etQuantity;
    private EditText etTag;
    private Button btnAddItem;
    private Button btnBack;
    private DatabaseHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");

        etItemName = findViewById(R.id.etItemName);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etTag = findViewById(R.id.etTag);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddItemActivity.this, MerchantDashboardActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = etItemName.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();
                String quantityStr = etQuantity.getText().toString().trim();
                String tag = etTag.getText().toString().trim();

                if (itemName.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty() || tag.isEmpty()) {
                    Toast.makeText(AddItemActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double price = Double.parseDouble(priceStr);
                    int quantity = Integer.parseInt(quantityStr);

                    // Get merchant ID from username
                    int merchantId = dbHelper.getMerchantId(username);
                    if (merchantId == -1) {
                        Toast.makeText(AddItemActivity.this, "Error: Merchant not found. Please log in again.", Toast.LENGTH_SHORT).show();
                        android.util.Log.e("AddItemActivity", "Merchant ID not found for username: " + username);
                        Intent intent = new Intent(AddItemActivity.this, MerchantLoginActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    boolean success = dbHelper.addMerchantItem(itemName, price, quantity, tag, merchantId);
                    if (success) {
                        Toast.makeText(AddItemActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddItemActivity.this, MerchantDashboardActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(AddItemActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(AddItemActivity.this, "Please enter valid numbers for price and quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
} 