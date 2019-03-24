package com.example.shoppingcartapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recyclerView.ProductListAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private ProgressBar loading;
    private ValueEventListener mPostListener;
    private String mPostKey;
    TextView mlist;
    private Toast mFailToast;
    private Toast mPassToast;
    FloatingActionButton viewcart;

    private RecyclerView mRecyclerView;
    private ProductListAdapter mAdapter;
    List<Object> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //for data base connection
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        DatabaseReference myRef = mDatabase.child("ProductCollection/");
        loading = findViewById(R.id.dataloading);
        viewcart = findViewById(R.id.viewcarticon);
        loading.setVisibility(View.VISIBLE);
        //to retrive data from firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Object> product = (List<Object>) dataSnapshot.getValue();
                loading.setVisibility(View.INVISIBLE);
                viewcart.show();
                productList = product;
                mAdapter = new ProductListAdapter(MainActivity.this, productList);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                viewcart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       Intent intent = new Intent(MainActivity.this, CartView.class);
                       startActivity(intent);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                mFailToast.makeText(MainActivity.this,"Database Connectivity Failed",Toast.LENGTH_LONG);
                mFailToast.show();
            }
        });
    }



}
