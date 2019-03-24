package com.example.shoppingcartapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.recyclerView.ProductListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetail extends AppCompatActivity {
    TextView mDetail;
    TextView mQuantcount;
    Button mAddCart;
    private DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    long mCount;
    String theData;
    String dataposition;
    Map<Object,Object> product;
    TextView mprodName;
    ImageView mprodImg;
    TextView mprodPrice;
    TextView mprodDes;
    ImageView incrementview;
    ImageView decementview;
    FloatingActionButton viewcart;
    FloatingActionButton addtocart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        mDetail =  findViewById(R.id.productDetail);
        mQuantcount = findViewById(R.id.quantityCount);
        mprodName = findViewById(R.id.prodName);
        mprodImg = findViewById(R.id.prodImg);
        mprodPrice = findViewById(R.id.prodPrice);
        mprodDes = findViewById(R.id.prodDes);
        incrementview = findViewById(R.id.increment);
        decementview = findViewById(R.id.decrement);
        viewcart = findViewById(R.id.viewcartbutton);
        addtocart = findViewById(R.id.addtocart);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //receive data from adapter
        Intent intent = getIntent();
        String data = intent.getExtras().getString("firebaseid");
        if(intent.getExtras().containsKey("cartquant")) {
            mCount = intent.getExtras().getLong("cartquant");
        } else {
            mCount = 1;
        }

//        mCount = Long.parseLong(intent.getExtras().getString("quantcount"));
        dataposition = data;
        //for data base connection
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDatabase.child("ProductCollection/"+data);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                product = (Map<Object,Object>) dataSnapshot.getValue();
                theData = product.toString();
                //mDetail.setText(theData);
                mQuantcount.setText(mCount+"");
                mprodName.setText(product.get("Name").toString());
                Glide.with(ProductDetail.this).asBitmap().load("http://msitmp.herokuapp.com"+product.get("ProductPicUrl")).into(mprodImg);
                mprodPrice.setText("Rs."+ product.get("Price").toString() +"/-");
                mprodDes.setText(product.get("Description").toString());
                incrementview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        increment();
                    }
                });
                decementview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        decrement();
                    }
                });
                viewcart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewCart();
                    }
                });
                addtocart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addCart();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
    public void increment() {
        if(Long.parseLong(product.get("Quantity").toString()) > mCount) {
            mCount++;
        }

        mQuantcount.setText(mCount+"");
    }
    public void decrement() {
        if(mCount > 1) {
            --mCount;
        }
        mQuantcount.setText(mCount+"");
    }

    public void addCart() {
        Toast.makeText(ProductDetail.this,"Added to Cart Successfully",Toast.LENGTH_LONG).show();
        mDatabase.child("Cart").child(dataposition).setValue(mCount);
        long priceval = mCount * (long) product.get("Price");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(dataposition , String.valueOf(priceval));
        editor.commit();
    }


    public void viewCart() {
        Intent intent = new Intent(ProductDetail.this, CartView.class);
        startActivity(intent);
    }
}
