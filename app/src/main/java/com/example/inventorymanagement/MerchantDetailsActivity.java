package com.example.inventorymanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MerchantDetailsActivity extends AppCompatActivity {
    private TextView tvEmail;
    private TextView tvPhone;
    private Button btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_details);

        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        btnCall = findViewById(R.id.btnCall);

        // Get the merchant details from the intent
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");

        // Display the merchant details
        tvEmail.setText("Email: " + email);
        tvPhone.setText("Phone: " + phone);

        // Set up the call button
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        });
    }
} 