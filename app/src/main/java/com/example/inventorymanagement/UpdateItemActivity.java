package com.example.inventorymanagement;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateItemActivity extends AppCompatActivity {
    private EditText etSearch;
    private EditText etNewName;
    private EditText etNewPrice;
    private Button btnUpdate;
    private ListView lvItems;
    private DatabaseHelper dbHelper;
    private String username;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");

        etSearch = findViewById(R.id.etSearch);
        etNewName = findViewById(R.id.etNewName);
        etNewPrice = findViewById(R.id.etNewPrice);
        btnUpdate = findViewById(R.id.btnUpdate);
        lvItems = findViewById(R.id.lvItems);

        // Set up search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set up item click listener
        lvItems.setOnItemClickListener((parent, view, position, id) -> {
            lvItems.setItemChecked(position, true);
            Cursor cursor = (Cursor) adapter.getItem(position);
            if (cursor != null) {
                String currentName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ITEM_NAME));
                double currentPrice = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE));
                
                etNewName.setText(currentName);
                etNewPrice.setText(String.valueOf(currentPrice));
            }
        });

        btnUpdate.setOnClickListener(v -> updateItem());
    }

    private void searchItems(String searchQuery) {
        int vendorId = dbHelper.getVendorId(username);
        if (vendorId == -1) {
            Toast.makeText(this, "Error: Vendor not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.searchVendorItemsByName(vendorId, searchQuery);
        if (cursor == null) {
            Toast.makeText(this, "Error: Could not search items", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] fromColumns = {
            "_id",
            DatabaseHelper.COLUMN_ITEM_NAME,
            DatabaseHelper.COLUMN_PRICE,
            DatabaseHelper.COLUMN_QUANTITY
        };

        int[] toViews = {
            R.id.tvSerialNo,
            R.id.tvItemName,
            R.id.tvPrice,
            R.id.tvQuantity
        };

        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }

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
                tvSerialNo.setText(String.valueOf(cursor.getPosition() + 1));
                
                // Format price
                TextView tvPrice = view.findViewById(R.id.tvPrice);
                int priceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE);
                if (priceIndex >= 0) {
                    double price = cursor.getDouble(priceIndex);
                    tvPrice.setText(String.format("$%.2f", price));
                }
            }
        };

        lvItems.setAdapter(adapter);
    }

    private void updateItem() {
        String newName = etNewName.getText().toString().trim();
        String newPriceStr = etNewPrice.getText().toString().trim();

        if (newName.isEmpty() || newPriceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double newPrice = Double.parseDouble(newPriceStr);
            if (newPrice <= 0) {
                Toast.makeText(this, "Price must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get selected item
            int position = lvItems.getCheckedItemPosition();
            if (position == ListView.INVALID_POSITION) {
                Toast.makeText(this, "Please select an item to update", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = (Cursor) adapter.getItem(position);
            int idIndex = cursor.getColumnIndex("_id");
            if (idIndex >= 0) {
                int itemId = cursor.getInt(idIndex);

                // Update item modification
                boolean success = dbHelper.updateItemModification(itemId, newName, newPrice);
                if (success) {
                    Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                    etNewName.setText("");
                    etNewPrice.setText("");
                    lvItems.clearChoices();
                    
                    // Refresh the list with the current search query
                    String currentSearch = etSearch.getText().toString();
                    searchItems(currentSearch);
                } else {
                    Toast.makeText(this, "Failed to update item", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
    }
} 