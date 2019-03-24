package com.example.recyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shoppingcartapp.Product;
import com.example.shoppingcartapp.ProductDetail;
import com.example.shoppingcartapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
//import com.google.gson.Gson;

import java.text.BreakIterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {
    private LayoutInflater mInflater;
    private DatabaseReference mDatabase;
    DatabaseReference myRef;
    DatabaseReference myprodref;
    List<Object> mproductList;
    Context mcontext;
    int mCount;
    AlertDialog productdialog;
    ImageView zoomprodimg;
    TextView proddescview;
    TextView zoomprodName;

    private ProductListAdapterOnClickHandler mClickHandler;

    public interface ProductListAdapterOnClickHandler {
        void onClick(Product productItem);
    }

    public ProductListAdapter(Context context, List<Object> prodList) {
        mcontext = context;
        mInflater = LayoutInflater.from(context);

        this.mproductList = prodList;
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        myRef = mDatabase.child("Cart/");
        System.out.println(mproductList + "product list");
        myprodref = mDatabase.child("ProductCollection/");

    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View

            .OnClickListener {

//        public final TextView productItemView;
        public final ImageView prodimg;
        public final TextView prodNameView;
//        public final TextView prodqnt;
        public final TextView productCat;
        public final TextView productStatus;
//        public final ImageView zoomprodimg;
        public final CardView productcard;
        final ProductListAdapter mAdapter;


        public ProductViewHolder(View itemview, ProductListAdapter adapter) {
            super(itemview);

//            productItemView = (TextView) itemview.findViewById(R.id.product);
//            prodNameView = (TextView) itemview.findViewById(R.id.ProdName);
            prodNameView = (TextView) itemview.findViewById(R.id.ProdrecName);
//            prodimg = (ImageView) itemview.findViewById(R.id.prodimg);
            prodimg = (ImageView) itemview.findViewById(R.id.prodrecimg);
//            prodqnt = (TextView) itemview.findViewById(R.id.showquantity);
            productCat = itemview.findViewById(R.id.prodCat);
            productStatus = itemview.findViewById(R.id.prodstatus);
            productcard = itemview.findViewById(R.id.productcard);
//            zoomprodimg = itemview.findViewById(R.id.zoomprodimg);
            this.mAdapter = adapter;
            itemview.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Toast.makeText(v.getContext(), "clicked!", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        View mItemView = mInflater.inflate(R.layout.product_item, viewGroup, false);
        View mItemView = mInflater.inflate(R.layout.recyclerproduct, viewGroup, false);
        return new ProductViewHolder(mItemView, this);

    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        // Retrieve the data for that position.
        final HashMap<String, String> mCurrent = (HashMap<String, String>) mproductList.get(position);
        // Add the data to the view holder.
        holder.prodNameView.setText(mCurrent.get("Name"));
        holder.productCat.setText(mCurrent.get("Category"));
        Glide.with(mcontext).asBitmap().load("http://msitmp.herokuapp.com" + mCurrent.get("ProductPicUrl")).apply(new RequestOptions().override(600, 200)).into(holder.prodimg);
        holder.productStatus.setText(mCurrent.get("Status"));
//        holder.prodNameView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
        holder.productcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, ProductDetail.class);
                intent.putExtra("firebaseid",  position+"");
                mcontext.startActivity(intent);
            }
        });
        holder.prodimg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                productdialog = new AlertDialog.Builder(mcontext).create();
                LayoutInflater inflater = LayoutInflater.from(mcontext);
                View dialogview = inflater.inflate(R.layout.productzoominfo, null);
                productdialog.setView(dialogview);
                productdialog.setContentView(R.layout.productzoominfo);
                zoomprodimg = dialogview.findViewById(R.id.zoomprodimg);
                proddescview = dialogview.findViewById(R.id.zoomprod);
                proddescview.setText(mCurrent.get("Description"));
                zoomprodName = dialogview.findViewById(R.id.zoomprodName);
                zoomprodName.setText(mCurrent.get("Name"));
                Glide.with(mcontext).asBitmap().load("http://msitmp.herokuapp.com" + mCurrent.get("ProductPicUrl")).apply(new RequestOptions().override(600, 200)).into(zoomprodimg);
                productdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                productdialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mproductList.size();
    }

}
