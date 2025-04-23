package com.example.inventorymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MerchantCreateAccountActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPhone;
    private Button btnCreateAccount;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_create_account);

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (!email.endsWith("@gmail.com")) {
            Toast.makeText(this, "Email must end with @gmail.com", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username already exists
        if (dbHelper.checkMerchantCredentials(username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add merchant to database
        boolean success = dbHelper.addMerchant(username, email, phone);
        if (success) {
            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MerchantCreateAccountActivity.this, MerchantDashboardActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to create account. Email or phone number might be in use.", Toast.LENGTH_SHORT).show();
        }
    }
} 