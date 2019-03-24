package com.example.recyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shoppingcartapp.CartView;
import com.example.shoppingcartapp.Product;
import com.example.shoppingcartapp.ProductDetail;
import com.example.shoppingcartapp.R;
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

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartViewHolder> {
    Context mcontext;
    private long TotalSum = 0;
    TextView totalsumview;
    ArrayList<TreeMap<String,Long>> mcartList;
    Map<Object,Object> product;

    private LayoutInflater mInflater;
    private DatabaseReference mDatabase;
    private DatabaseReference mProdbase;
    private String temp;
    public CartListAdapter(Context context,  ArrayList<TreeMap<String,Long>> cartList){
        mcontext = context;
        mInflater = LayoutInflater.from(context);
        this.mcartList = cartList;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cart/");
        mProdbase = FirebaseDatabase.getInstance().getReference().child("ProductCollection");
    }

    public void restoreItem(TreeMap<String,Long> deletedproduct , int deletedposition) {
        String key = deletedproduct.firstKey();
        mcartList.add(deletedposition, deletedproduct);
        mDatabase.child(key).setValue(deletedproduct.get(key));
        notifyItemInserted(deletedposition);
    }
    public long getTotalSum() {
        return TotalSum;
    }


    public class CartViewHolder extends RecyclerView.ViewHolder implements View

            .OnClickListener {

        public final ImageView cartimg;
        public final TextView cartName;
        public final TextView cartPrice;
        public final TextView indiprice;
        final CartListAdapter mAdapter;
//        TextView totalsumview;
        long eachSum = 0;

        public CartViewHolder(View itemview, CartListAdapter adapter) {
            super(itemview);
//            cartItemView = (TextView) itemview.findViewById(R.id.item);
            this.mAdapter = adapter;
            cartimg = (ImageView) itemview.findViewById(R.id.prodImg);
            cartName = (TextView) itemview.findViewById(R.id.prodName);
            cartPrice = (TextView) itemview.findViewById(R.id.priceID);
            indiprice = itemview.findViewById(R.id.indiprices);
            itemview.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String firebaseid = mcartList.get(position).firstKey();
            long count = mcartList.get(position).get(firebaseid);
            Intent intent = new Intent(mcontext , ProductDetail.class);
            intent.putExtra("firebaseid", firebaseid);
            intent.putExtra("cartquant", count);
            mcontext.startActivity(intent);
        }
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.cart_item, viewGroup, false);
        totalsumview = (TextView) mItemView.findViewById(R.id.totalPriceID);
        System.out.println(totalsumview + "totalsumview");
        return new CartListAdapter.CartViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(final CartViewHolder holder, final int i) {
        TreeMap<String, Long> mCurrent = mcartList.get(i);
        String key = mCurrent.firstKey();
        final long value = mCurrent.get(key);
        mProdbase = FirebaseDatabase.getInstance().getReference();
        mProdbase = mProdbase.child("ProductCollection/"+key);
        mProdbase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                product = (Map<Object,Object>) dataSnapshot.getValue();
                Glide.with(mcontext).asBitmap().load("http://msitmp.herokuapp.com"+product.get("ProductPicUrl")).into(holder.cartimg);
                holder.cartName.setText(String.valueOf(product.get("Name")));
                holder.eachSum = value * (long) product.get("Price");
                TotalSum = TotalSum + holder.eachSum;
                holder.cartPrice.setText(product.get("Price").toString() + " x " + value);
                holder.indiprice.setText(String.valueOf(holder.eachSum));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mcartList.size();
    }

    public void removeItem(int position) {
        String key = mcartList.get(position).firstKey();
        mcartList.remove(position);
        mDatabase.child(key).removeValue();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mcartList.size());
    }
}
