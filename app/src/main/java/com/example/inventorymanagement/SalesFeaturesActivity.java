package com.example.inventorymanagement;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SalesFeaturesActivity extends AppCompatActivity {
    private ListView lvItems;
    private EditText etItemName;
    private EditText etQuantitySold;
    private Button btnCalculate;
    private Button btnRecordSale;
    private Button btnViewSalesHistory;
    private TextView tvTotalSales;
    private DatabaseHelper dbHelper;
    private String username;
    private SimpleCursorAdapter adapter;
    private int selectedItemId = -1;
    private double selectedItemPrice = 0.0;
    private int selectedItemQuantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_features);

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "Error: Username not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        lvItems = findViewById(R.id.lvItems);
        etItemName = findViewById(R.id.etItemName);
        etQuantitySold = findViewById(R.id.etQuantitySold);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnRecordSale = findViewById(R.id.btnRecordSale);
        btnViewSalesHistory = findViewById(R.id.btnViewSalesHistory);
        tvTotalSales = findViewById(R.id.tvTotalSales);

        displayItems();

        // Set up item name search functionality
        etItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchItemByName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set up calculate button
        btnCalculate.setOnClickListener(v -> calculateSale());

        // Set up record sale button
        btnRecordSale.setOnClickListener(v -> recordSale());
        
        // Set up view sales history button
        btnViewSalesHistory.setOnClickListener(v -> {
            Intent intent = new Intent(SalesFeaturesActivity.this, SalesHistoryActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }

    private void searchItemByName(String searchQuery) {
        int vendorId = dbHelper.getVendorId(username);
        if (vendorId == -1) {
            Toast.makeText(this, "Error: Vendor not found", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Cursor cursor = dbHelper.searchVendorItemsByName(vendorId, searchQuery);
            if (cursor == null || cursor.getCount() == 0) {
                // No items found with this name
                selectedItemId = -1;
                selectedItemPrice = 0.0;
                selectedItemQuantity = 0;
                tvTotalSales.setText("Total Price: ₹0.00");
                return;
            }

            // If we found exactly one item, select it
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                int idIndex = cursor.getColumnIndex("_id");
                int priceIndex = cursor.getColumnIndex("price");
                int quantityIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY);
                int nameIndex = cursor.getColumnIndex("item_name");

                if (idIndex >= 0 && priceIndex >= 0 && quantityIndex >= 0 && nameIndex >= 0) {
                    selectedItemId = cursor.getInt(idIndex);
                    selectedItemPrice = cursor.getDouble(priceIndex);
                    selectedItemQuantity = cursor.getInt(quantityIndex);
                    String itemName = cursor.getString(nameIndex);
                    
                    etItemName.setText(itemName);
                    etQuantitySold.setHint("Available: " + selectedItemQuantity);
                    
                    // Find and select the item in the ListView
                    for (int i = 0; i < adapter.getCount(); i++) {
                        Cursor itemCursor = (Cursor) adapter.getItem(i);
                        if (itemCursor != null) {
                            int itemIdIndex = itemCursor.getColumnIndex("_id");
                            if (itemIdIndex >= 0 && itemCursor.getInt(itemIdIndex) == selectedItemId) {
                                lvItems.setItemChecked(i, true);
                                break;
                            }
                        }
                    }
                } else {
                    android.util.Log.e("SalesFeatures", "Invalid column indices in search results");
                }
            }
        } catch (Exception e) {
            android.util.Log.e("SalesFeatures", "Error searching items: " + e.getMessage());
            Toast.makeText(this, "Error searching items", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayItems() {
        // Get vendor ID from username
        int vendorId = dbHelper.getVendorId(username);
        if (vendorId == -1) {
            Toast.makeText(this, "Vendor not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get items for sales
        Cursor cursor = dbHelper.getModifiedItemsForSales(vendorId);
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No items available for sale", Toast.LENGTH_SHORT).show();
            return;
        }

        // Define columns to display
        String[] fromColumns = {
            "item_name",
            "price",
            DatabaseHelper.COLUMN_QUANTITY
        };

        // Define views to bind to
        int[] toViews = {
            R.id.tvItemName,
            R.id.tvPrice,
            R.id.tvQuantity
        };

        // Create adapter
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
            this,
            R.layout.item_row,
            cursor,
            fromColumns,
            toViews,
            0
        );

        // Set custom binding for price formatting
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.tvPrice) {
                    double price = cursor.getDouble(columnIndex);
                    ((TextView) view).setText(String.format("₹%.2f", price));
                    return true;
                }
                return false;
            }
        });

        // Set adapter to ListView
        lvItems.setAdapter(adapter);

        // Set item click listener
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    int idIndex = cursor.getColumnIndex("_id");
                    int priceIndex = cursor.getColumnIndex("price");
                    int quantityIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY);
                    int nameIndex = cursor.getColumnIndex("item_name");

                    if (idIndex >= 0 && priceIndex >= 0 && quantityIndex >= 0 && nameIndex >= 0) {
                        // Update selected item details
                        selectedItemId = cursor.getInt(idIndex);
                        selectedItemPrice = cursor.getDouble(priceIndex);
                        selectedItemQuantity = cursor.getInt(quantityIndex);
                        
                        // Log selected item details
                        android.util.Log.d("SalesFeatures", "Selected Item - ID: " + selectedItemId +
                            ", Price: " + selectedItemPrice +
                            ", Quantity: " + selectedItemQuantity);
                        
                        // Update UI to show selected item
                        etItemName.setText(cursor.getString(nameIndex));
                        etQuantitySold.setHint("Available: " + selectedItemQuantity);
                        
                        // Highlight the selected item
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            View child = parent.getChildAt(i);
                            if (i == position) {
                                child.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                            } else {
                                child.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                            }
                        }
                        
                        // Enable buttons and clear previous calculations
                        btnCalculate.setEnabled(true);
                        btnRecordSale.setEnabled(true);
                        etQuantitySold.setText("");
                        tvTotalSales.setText("Total Price: ₹0.00");
                    } else {
                        android.util.Log.e("SalesFeatures", "Invalid column indices in item selection");
                        Toast.makeText(SalesFeaturesActivity.this, "Error selecting item", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Set calculate button listener
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItemId == -1) {
                    Toast.makeText(SalesFeaturesActivity.this, "Please select an item", Toast.LENGTH_SHORT).show();
                    return;
                }

                String quantityStr = etQuantitySold.getText().toString();
                if (quantityStr.isEmpty()) {
                    Toast.makeText(SalesFeaturesActivity.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                    return;
                }

                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    Toast.makeText(SalesFeaturesActivity.this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (quantity > selectedItemQuantity) {
                    Toast.makeText(SalesFeaturesActivity.this, "Insufficient stock", Toast.LENGTH_SHORT).show();
                    return;
                }

                double totalPrice = selectedItemPrice * quantity;
                tvTotalSales.setText(String.format("Total Price: ₹%.2f", totalPrice));
            }
        });

        // Set sell button listener
        btnRecordSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItemId == -1) {
                    Toast.makeText(SalesFeaturesActivity.this, "Please select an item", Toast.LENGTH_SHORT).show();
                    return;
                }

                String quantityStr = etQuantitySold.getText().toString();
                if (quantityStr.isEmpty()) {
                    Toast.makeText(SalesFeaturesActivity.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                    return;
                }

                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    Toast.makeText(SalesFeaturesActivity.this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (quantity > selectedItemQuantity) {
                    Toast.makeText(SalesFeaturesActivity.this, "Insufficient stock", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Record sale
                int vendorId = dbHelper.getVendorId(username);
                if (vendorId == -1) {
                    Toast.makeText(SalesFeaturesActivity.this, "Error: Vendor not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean success = dbHelper.recordSale(selectedItemId, quantity, selectedItemPrice * quantity, vendorId);
                if (success) {
                    Toast.makeText(SalesFeaturesActivity.this, "Sale recorded successfully", Toast.LENGTH_SHORT).show();
                    // Reset fields
                    selectedItemId = -1;
                    selectedItemPrice = 0.0;
                    selectedItemQuantity = 0;
                    etQuantitySold.setText("");
                    tvTotalSales.setText("Total Price: ₹0.00");
                    etItemName.setText("");
                    // Refresh item list
                    displayItems();
                } else {
                    Toast.makeText(SalesFeaturesActivity.this, "Failed to record sale", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set view sales button listener
        btnViewSalesHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalesFeaturesActivity.this, SalesHistoryActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    private void calculateSale() {
        if (selectedItemId == -1) {
            Toast.makeText(this, "Please select an item first", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantityStr = etQuantitySold.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int quantitySold = Integer.parseInt(quantityStr);
            if (quantitySold <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (quantitySold > selectedItemQuantity) {
                Toast.makeText(this, "Not enough items available", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalPrice = selectedItemPrice * quantitySold;
            tvTotalSales.setText(String.format("Total Price: ₹%.2f", totalPrice));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
        }
    }

    private void recordSale() {
        if (selectedItemId == -1) {
            Toast.makeText(this, "Please select an item", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantityStr = etQuantitySold.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int quantitySold = Integer.parseInt(quantityStr);
            if (quantitySold <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (quantitySold > selectedItemQuantity) {
                Toast.makeText(this, "Not enough items available", Toast.LENGTH_SHORT).show();
                return;
            }

            int vendorId = dbHelper.getVendorId(username);
            if (vendorId == -1) {
                Toast.makeText(this, "Error: Vendor not found", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalPrice = selectedItemPrice * quantitySold;
            
            // Update quantity
            boolean success = dbHelper.updateItemQuantity(DatabaseHelper.TABLE_VENDOR_ITEMS, selectedItemId, selectedItemQuantity - quantitySold);
            if (success) {
                // Record sale in sales history
                boolean saleRecorded = dbHelper.recordSale(selectedItemId, quantitySold, totalPrice, vendorId);
                if (saleRecorded) {
                    Toast.makeText(this, "Sale recorded successfully", Toast.LENGTH_SHORT).show();
                    // Reset fields
                    etQuantitySold.setText("");
                    etItemName.setText("");
                    tvTotalSales.setText("Total Price: ₹0.00");
                    selectedItemId = -1;
                    selectedItemPrice = 0.0;
                    selectedItemQuantity = 0;
                    lvItems.clearChoices();
                    // Refresh the list
                    displayItems();
                } else {
                    Toast.makeText(this, "Failed to record sale in history", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to update item quantity", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
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