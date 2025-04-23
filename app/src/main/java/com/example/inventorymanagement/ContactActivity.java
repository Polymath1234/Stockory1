package com.example.inventorymanagement;

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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ContactActivity extends AppCompatActivity {
    private EditText etSearch;
    private ListView lvMerchants;
    private Button btnSearch;
    private Button btnBack;
    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");

        etSearch = findViewById(R.id.etSearch);
        lvMerchants = findViewById(R.id.lvMerchants);
        btnSearch = findViewById(R.id.btnSearch);
        
        // Initialize back button
        View backButtonLayout = findViewById(R.id.btnBack);
        btnBack = backButtonLayout.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, VendorDashboardActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = etSearch.getText().toString().trim();
                if (!searchQuery.isEmpty()) {
                    displayMerchants(searchQuery);
                } else {
                    Toast.makeText(ContactActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));
                String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));

                Intent intent = new Intent(ContactActivity.this, MerchantDetailsActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                startActivity(intent);
            }
        });
    }

    private void displayMerchants(String searchQuery) {
        Cursor cursor = dbHelper.searchMerchants(searchQuery);
        String[] fromColumns = {
                DatabaseHelper.COLUMN_USERNAME,
                DatabaseHelper.COLUMN_EMAIL,
                DatabaseHelper.COLUMN_PHONE
        };
        int[] toViews = {
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
} 