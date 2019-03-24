package com.example.shoppingcartapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recyclerView.CartListAdapter;
import com.example.recyclerView.ProductListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CartView extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;
    private RecyclerView mRecyclerView;
    FloatingActionButton placeorder;
    private CartListAdapter mAdapter;
    public TextView TotalPriceView;
    private Toast mFailToast;
    private Toast mPassToast;
    private TreeMap<String,Long> cart;
    private ArrayList<TreeMap<String,Long>> cartlist;
    private Paint p = new Paint();
    long totalprice;
    DatabaseReference myRef;
    Button mplaceOrder;
    public void placeOrder(View view) {
        myRef.removeValue();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(this,"ORDER PLACED",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(CartView.this,OrderPlaced.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);
        cart = new TreeMap<>();
        cartlist = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewCart);
//        mplaceOrder = (Button) findViewById(R.id.placeOrder);
        placeorder = findViewById(R.id.placeorderfab);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myRef =  mDatabase.child("Cart/");
        TotalPriceView = (TextView) findViewById(R.id.totalPriceID);


        enableswipe();
        //to retrive data from firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalprice = 0;
                Map<String,?> keys = sharedPreferences.getAll();
                System.out.println(keys.toString() + "keys here");
                for(Map.Entry<String, ?> entry : keys.entrySet()) {
                    System.out.println(entry.getKey() + "eachkey");
                    totalprice = totalprice +  Long.parseLong(entry.getValue().toString());
                }
                System.out.println(totalprice + "totalstringprice");
                if(totalprice == 0) {
                    TotalPriceView.setText("");
                    placeorder.setEnabled(false);
                } else {
                    TotalPriceView.setText(String.valueOf(totalprice));
                    placeorder.setEnabled(true);
                }
                cartlist = new ArrayList<>();
                Iterable<DataSnapshot> cartSnapshot =  dataSnapshot.getChildren();
                for(DataSnapshot each : cartSnapshot){
                    cart = new TreeMap<>();
                    cart.put(each.getKey(),(long) each.getValue());
                    cartlist.add(cart);
                }
                System.out.println("cart : "+ cart.toString());
                mAdapter = new CartListAdapter(CartView.this,cartlist);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(CartView.this));
                placeorder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        placeOrder(v);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                mFailToast.makeText(CartView.this,"Database Connectivity Failed", Toast.LENGTH_LONG);
                mFailToast.show();
            }
        });
    }

    private void enableswipe() {
        ItemTouchHelper.SimpleCallback simepletouchhelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView,  RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped( RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                if(i == ItemTouchHelper.LEFT) {
                    final TreeMap<String,Long> deletedproduct = cartlist.get(position);
                    final int deletedposition = position;
                    final String key = cartlist.get(position).firstKey();
                    mAdapter.removeItem(position);
                    final String value = sharedPreferences.getString(key, "");
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    System.out.println(sharedPreferences.getAll() + "before the remove in if");
                    editor.remove(key);
                    editor.commit();
                    System.out.println(sharedPreferences.getAll() + "after the remove in if");
                    totalprice = totalprice - Long.parseLong(value);
                    if(totalprice == 0) {
                        TotalPriceView.setText("");
                        placeorder.setEnabled(false);
                        Toast.makeText(CartView.this,"Cart is Empty",Toast.LENGTH_SHORT).show();
                    } else {
                        TotalPriceView.setText(String.valueOf(totalprice));
                        placeorder.setEnabled(true);
                    }
                    //Snackbar for undo option
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Removed From Cart", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAdapter.restoreItem(deletedproduct, deletedposition);
                            System.out.println(sharedPreferences.getAll() + "before the undo in if");
                            editor.putString(key , value );
                            editor.commit();
                            System.out.println(sharedPreferences.getAll() + "after the undo in if");
                            totalprice = totalprice + Long.parseLong(value);
                            if(totalprice == 0) {
                                TotalPriceView.setText("");
                                placeorder.setEnabled(false);
                                Toast.makeText(CartView.this,"Cart is Empty",Toast.LENGTH_SHORT).show();
                            } else {
                                TotalPriceView.setText(String.valueOf(totalprice));
                                placeorder.setEnabled(true);
                            }
                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();

                } else {
                    final TreeMap<String,Long> deletedproduct = cartlist.get(position);;
                    final int deletedposition = position;
                    final String key = cartlist.get(position).firstKey();
                    mAdapter.removeItem(position);
                    final String value = sharedPreferences.getString(key, "");
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(key);
                    editor.commit();
                    totalprice = totalprice - Long.parseLong(value);
                    if(totalprice == 0) {
                        TotalPriceView.setText("");
                        placeorder.setEnabled(false);
                        Toast.makeText(CartView.this,"Cart is Empty",Toast.LENGTH_SHORT).show();
                    } else {
                        TotalPriceView.setText(String.valueOf(totalprice));
                        placeorder.setEnabled(true);
                    }
                    //Snackbar for undo option
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Removed From Cart", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAdapter.restoreItem(deletedproduct, deletedposition);
                            editor.putString(key, value );
                            editor.commit();
                            totalprice = totalprice + Long.parseLong(value);
                            if(totalprice == 0) {
                                TotalPriceView.setText("");
                                placeorder.setEnabled(false);
                                Toast.makeText(CartView.this,"Cart is Empty",Toast.LENGTH_SHORT).show();
                            } else {
                                TotalPriceView.setText(String.valueOf(totalprice));
                                placeorder.setEnabled(true);
                            }
                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height/3;

                    if(dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder , dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simepletouchhelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}
