package com.example.inventorymanagement;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddItemFromMerchantActivity extends AppCompatActivity {
    private EditText etSearch;
    private ListView lvMerchants;
    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_from_merchant);

        // Get vendor ID from intent
        int vendorId = getIntent().getIntExtra("vendorId", -1);
        if (vendorId == -1) {
            Toast.makeText(this, "Error: Vendor ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        etSearch = findViewById(R.id.etSearch);
        lvMerchants = findViewById(R.id.lvMerchants);

        displayMerchants("");

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                displayMerchants(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lvMerchants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                if (cursor != null) {
                    int merchantId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    String merchantName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME));

                    Intent intent = new Intent(AddItemFromMerchantActivity.this, MerchantItemsActivity.class);
                    intent.putExtra("merchantId", merchantId);
                    intent.putExtra("merchantName", merchantName);
                    intent.putExtra("vendorId", vendorId);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void displayMerchants(String searchQuery) {
        Cursor cursor = dbHelper.searchMerchants(searchQuery);
        String[] fromColumns = {
                "_id",
                DatabaseHelper.COLUMN_USERNAME,
                DatabaseHelper.COLUMN_EMAIL,
                DatabaseHelper.COLUMN_PHONE
        };
        int[] toViews = {
                R.id.tvMerchantId,
                R.id.tvMerchantName,
                R.id.tvEmail,
                R.id.tvPhone
        };

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.merchant_row,
                cursor,
                fromColumns,
                toViews,
                0
        );

        lvMerchants.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
    }
} 