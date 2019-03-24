package com.example.shoppingcartapp;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class OrderPlaced extends AppCompatActivity {
    TextView orderid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_placed);
        orderid = (TextView) findViewById(R.id.orderid);
        orderid.setText("Order ID: "+ generateid());
    }

    String generateid() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder((100000 + rnd.nextInt(900000)) + "-");
        for (int i = 0; i < 5; i++)
            sb.append(chars[rnd.nextInt(chars.length)]);
        return sb.toString();
    }

    public void gotomain(View view) {
        Intent intent = new Intent(OrderPlaced.this, MainActivity.class);
        startActivity(intent);
    }

    public void exit(View view ) {
//        Intent intent = new Intent(getApplicationContext(), HomePage.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("EXIT", true);
//        startActivity(intent);
        ActivityCompat.finishAffinity(OrderPlaced.this);
//        finishAffinity();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
