package com.example.inventorymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnMerchantLogin;
    private Button btnVendorLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMerchantLogin = findViewById(R.id.btnMerchantLogin);
        btnVendorLogin = findViewById(R.id.btnVendorLogin);

        btnMerchantLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MerchantLoginActivity.class);
                startActivity(intent);
            }
        });

        btnVendorLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VendorLoginActivity.class);
                startActivity(intent);
            }
        });
    }
} 