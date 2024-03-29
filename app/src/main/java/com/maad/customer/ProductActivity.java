package com.maad.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private ArrayList<ProductModel> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
    }

    private void loadProducts(String cat) {
        products.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection("products");
        collection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                for (int i = 0; i < documentSnapshots.size(); ++i) {
                    ProductModel productModel = documentSnapshots.get(i).toObject(ProductModel.class);
                    if (productModel.getCategory().equals(cat))
                        products.add(productModel);
                }
                showProducts();
            }
        });
    }

    private void showProducts() {
        ProductAdapter adapter = new ProductAdapter(this, products);
        RecyclerView recyclerView = findViewById(R.id.rv);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        adapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(int position, ImageView productImage) {
                Intent i = new Intent(ProductActivity.this
                        , ProductDetailsActivity.class);
                i.putExtra("product", products.get(position));
                i.putExtra("imageTransition", ViewCompat.getTransitionName(productImage));
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(ProductActivity.this
                                , productImage, ViewCompat.getTransitionName(productImage));
                startActivity(i, options.toBundle());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;

            case R.id.item_orders:
                startActivity(new Intent(this, ShoppingCartActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String cat = getIntent().getStringExtra("cat");
        setTitle(cat);
        loadProducts(cat);
    }
}