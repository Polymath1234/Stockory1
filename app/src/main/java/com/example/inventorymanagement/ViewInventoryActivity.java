package com.example.inventorymanagement;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ViewInventoryActivity extends AppCompatActivity {
    private ListView lvInventory;
    private DatabaseHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Error: Username not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        lvInventory = findViewById(R.id.lvInventory);
        displayInventory();
    }

    private void displayInventory() {
        int merchantId = dbHelper.getMerchantId(username);
        if (merchantId == -1) {
            Toast.makeText(this, "Error: Merchant not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Cursor cursor = dbHelper.getMerchantItems(merchantId);
        if (cursor == null) {
            Toast.makeText(this, "Error: Could not load inventory", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] fromColumns = {
                DatabaseHelper.COLUMN_ITEM_NAME,
                DatabaseHelper.COLUMN_PRICE,
                DatabaseHelper.COLUMN_QUANTITY
        };
        int[] toViews = {
                R.id.tvItemName,
                R.id.tvPrice,
                R.id.tvQuantity
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.item_row,
                cursor,
                fromColumns,
                toViews,
                0
        );

        lvInventory.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
} 