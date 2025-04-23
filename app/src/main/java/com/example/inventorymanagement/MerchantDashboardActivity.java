package com.example.inventorymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MerchantDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnAddItem;
    private Button btnViewInventory;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_dashboard);

        username = getIntent().getStringExtra("username");

        tvWelcome = findViewById(R.id.tvWelcome);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnViewInventory = findViewById(R.id.btnViewInventory);

        tvWelcome.setText("Welcome, " + username + "!");

        btnAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(MerchantDashboardActivity.this, AddItemActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        btnViewInventory.setOnClickListener(v -> {
            Intent intent = new Intent(MerchantDashboardActivity.this, ViewInventoryActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }
} 