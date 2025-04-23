package com.example.inventorymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class VendorDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnContact;
    private Button btnAddItem;
    private Button btnViewItems;
    private Button btnUpdate;
    private Button btnSales;
    private Button btnBack;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_dashboard);

        username = getIntent().getStringExtra("username");
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int vendorId = dbHelper.getVendorId(username);

        if (vendorId == -1) {
            Toast.makeText(this, "Error: Vendor ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvWelcome = findViewById(R.id.tvWelcome);
        btnContact = findViewById(R.id.btnContact);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnViewItems = findViewById(R.id.btnViewItems);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnSales = findViewById(R.id.btnSales);
        
        // Find the included layout first, then find the button inside it
        View backButtonLayout = findViewById(R.id.btnBack);
        btnBack = backButtonLayout.findViewById(R.id.btnBack);

        tvWelcome.setText("Welcome, " + username + "!");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorDashboardActivity.this, VendorLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorDashboardActivity.this, ContactActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorDashboardActivity.this, AddItemFromMerchantActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("vendorId", vendorId);
                startActivity(intent);
            }
        });

        btnViewItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorDashboardActivity.this, ViewImportedItemsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorDashboardActivity.this, UpdateItemActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        btnSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorDashboardActivity.this, SalesFeaturesActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
} 