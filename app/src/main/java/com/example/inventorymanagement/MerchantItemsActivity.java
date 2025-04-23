package com.example.inventorymanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MerchantItemsActivity extends AppCompatActivity {
    private static final String TAG = "MerchantItemsActivity";
    private TextView tvMerchantName;
    private ListView lvItems;
    private Button btnAddItem;
    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;
    private int merchantId;
    private String merchantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_items);

        // Get merchant details from intent
        merchantId = getIntent().getIntExtra("merchantId", -1);
        merchantName = getIntent().getStringExtra("merchantName");

        Log.d(TAG, "Received merchantId: " + merchantId + ", merchantName: " + merchantName);

        if (merchantId == -1 || merchantName == null) {
            Toast.makeText(this, "Error: Merchant details not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        tvMerchantName = findViewById(R.id.tvMerchantName);
        lvItems = findViewById(R.id.lvItems);
        btnAddItem = findViewById(R.id.btnAddItem);

        // Set merchant name
        tvMerchantName.setText("Items from " + merchantName);

        // Display items
        displayItems();

        // Set item click listener
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                if (cursor != null) {
                    int itemId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    String itemName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_NAME));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE));
                    int availableQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY));

                    showAddItemDialog(itemId, itemName, price, availableQuantity);
                }
            }
        });

        // Set add item button click listener
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewItemDialog();
            }
        });
    }

    private void showAddItemDialog(final int itemId, final String itemName, final double price, final int availableQuantity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Item to Inventory");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        TextView tvItemName = dialogView.findViewById(R.id.tvItemName);
        TextView tvPrice = dialogView.findViewById(R.id.tvPrice);
        TextView tvAvailableQuantity = dialogView.findViewById(R.id.tvAvailableQuantity);
        final EditText etQuantity = dialogView.findViewById(R.id.etQuantity);

        tvItemName.setText("Item: " + itemName);
        tvPrice.setText("Price: $" + String.format("%.2f", price));
        tvAvailableQuantity.setText("Available: " + availableQuantity);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quantityStr = etQuantity.getText().toString();
                if (quantityStr.isEmpty()) {
                    Toast.makeText(MerchantItemsActivity.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                    return;
                }

                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    Toast.makeText(MerchantItemsActivity.this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (quantity > availableQuantity) {
                    Toast.makeText(MerchantItemsActivity.this, "Not enough items available", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get vendor ID from intent
                int vendorId = getIntent().getIntExtra("vendorId", -1);
                if (vendorId == -1) {
                    Toast.makeText(MerchantItemsActivity.this, "Error: Vendor ID not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add item to vendor's inventory
                boolean success = dbHelper.addVendorItem(itemName, price, quantity, "Added from merchant", vendorId);
                if (success) {
                    // Update merchant's inventory
                    dbHelper.updateItemQuantity(DatabaseHelper.TABLE_MERCHANT_ITEMS, itemId, availableQuantity - quantity);
                    Toast.makeText(MerchantItemsActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                    displayItems(); // Refresh the list
                } else {
                    Toast.makeText(MerchantItemsActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showAddNewItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Item");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_new_item, null);
        builder.setView(dialogView);

        final EditText etItemName = dialogView.findViewById(R.id.etItemName);
        final EditText etPrice = dialogView.findViewById(R.id.etPrice);
        final EditText etQuantity = dialogView.findViewById(R.id.etQuantity);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = etItemName.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();
                String quantityStr = etQuantity.getText().toString().trim();

                if (itemName.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
                    Toast.makeText(MerchantItemsActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double price = Double.parseDouble(priceStr);
                    int quantity = Integer.parseInt(quantityStr);

                    if (price <= 0 || quantity <= 0) {
                        Toast.makeText(MerchantItemsActivity.this, "Price and quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean success = dbHelper.addMerchantItem(itemName, price, quantity, "New item", merchantId);
                    if (success) {
                        Toast.makeText(MerchantItemsActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                        displayItems(); // Refresh the list
                    } else {
                        Toast.makeText(MerchantItemsActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MerchantItemsActivity.this, "Invalid price or quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void displayItems() {
        Log.d(TAG, "Displaying items for merchantId: " + merchantId);
        Cursor cursor = dbHelper.getMerchantItems(merchantId);
        
        if (cursor == null) {
            Log.e(TAG, "Cursor is null");
            Toast.makeText(this, "Error: Could not load items", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Found " + cursor.getCount() + " items");

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
                double price = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE));
                tvPrice.setText(String.format("$%.2f", price));
            }
        };

        lvItems.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
    }
} 