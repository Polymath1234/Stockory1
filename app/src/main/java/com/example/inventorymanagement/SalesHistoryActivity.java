package com.example.inventorymanagement;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SalesHistoryActivity extends AppCompatActivity {
    private ListView lvSalesHistory;
    private TextView tvTotalRevenue;
    private DatabaseHelper dbHelper;
    private String username;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_history);

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "Error: Username not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        lvSalesHistory = findViewById(R.id.lvSalesHistory);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);

        displaySalesHistory();
    }

    private void displaySalesHistory() {
        int vendorId = dbHelper.getVendorId(username);
        if (vendorId == -1) {
            Toast.makeText(this, "Error: Vendor not found", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Get total revenue
            double totalRevenue = dbHelper.getTotalRevenue(vendorId);
            tvTotalRevenue.setText(String.format("Total Revenue: $%.2f", totalRevenue));

            // Get sales history
            Cursor cursor = dbHelper.getSalesHistory(vendorId);
            if (cursor == null || cursor.getCount() == 0) {
                Toast.makeText(this, "No sales history available", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] fromColumns = {
                "item_name",
                "quantity_sold",
                "total_price",
                "sale_date"
            };

            int[] toViews = {
                R.id.tvItemName,
                R.id.tvQuantitySold,
                R.id.tvTotalPrice,
                R.id.tvSaleDate
            };

            if (adapter != null && adapter.getCursor() != null) {
                adapter.getCursor().close();
            }

            adapter = new SimpleCursorAdapter(
                this,
                R.layout.sale_history_row,
                cursor,
                fromColumns,
                toViews,
                0
            ) {
                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    super.bindView(view, context, cursor);
                    
                    // Format price
                    TextView tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
                    double price = cursor.getDouble(2);
                    tvTotalPrice.setText(String.format("$%.2f", price));
                    
                    // Format date
                    TextView tvSaleDate = view.findViewById(R.id.tvSaleDate);
                    String date = cursor.getString(3);
                    tvSaleDate.setText(date);
                }
            };

            lvSalesHistory.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(this, "Error loading sales history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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