package com.example.inventorymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MerchantLoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private Button btnLogin;
    private Button btnCreateAccount;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_login);

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                if (username.isEmpty()) {
                    Toast.makeText(MerchantLoginActivity.this, "Please enter username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper.checkMerchantCredentials(username)) {
                    Intent intent = new Intent(MerchantLoginActivity.this, MerchantDashboardActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Toast.makeText(MerchantLoginActivity.this, "Username not found. Please create an account.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MerchantLoginActivity.this, MerchantCreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }
} 