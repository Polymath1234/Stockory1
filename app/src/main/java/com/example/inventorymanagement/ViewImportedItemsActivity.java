package com.example.inventorymanagement;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ViewImportedItemsActivity extends AppCompatActivity {
    private ListView lvImportedItems;
    private DatabaseHelper dbHelper;
    private String username;
    private SimpleCursorAdapter adapter;
    private int itemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");

        lvImportedItems = findViewById(R.id.lvInventory);
        displayImportedItems();
    }

    private void displayImportedItems() {
        // Get vendor ID
        int vendorId = dbHelper.getVendorId(username);
        if (vendorId == -1) {
            Toast.makeText(this, "Error: Vendor not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get items
        Cursor cursor = dbHelper.getVendorItems(vendorId);
        if (cursor == null) {
            Toast.makeText(this, "Error: Could not load items", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set up adapter
        String[] fromColumns = {
            "_id",
            DatabaseHelper.COLUMN_ITEM_NAME,
            DatabaseHelper.COLUMN_PRICE,
            DatabaseHelper.COLUMN_QUANTITY,
            "merchant_name"
        };

        int[] toViews = {
            R.id.tvSerialNo,
            R.id.tvItemName,
            R.id.tvPrice,
            R.id.tvQuantity,
            R.id.tvMerchantName
        };

        adapter = new SimpleCursorAdapter(
            this,
            R.layout.item_row,
            cursor,
            fromColumns,
            toViews,
            0
        ) {
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);
                
                // Set serial number
                TextView tvSerialNo = view.findViewById(R.id.tvSerialNo);
                tvSerialNo.setText(String.valueOf(++itemCount));
                
                // Format price
                TextView tvPrice = view.findViewById(R.id.tvPrice);
                double price = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE));
                tvPrice.setText(String.format("$%.2f", price));
            }
        };

        lvImportedItems.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
    }
} 