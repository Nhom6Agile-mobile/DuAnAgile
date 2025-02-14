package com.pro.electronic.activity.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.electronic.MyApplication;
import com.pro.electronic.R;
import com.pro.electronic.activity.BaseActivity;
import com.pro.electronic.adapter.admin.AdminProductAdapter;
import com.pro.electronic.listener.IOnAdminManagerProductListener;
import com.pro.electronic.model.Category;
import com.pro.electronic.model.Product;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.GlobalFunction;

import java.util.ArrayList;
import java.util.List;

public class AdminProductByCategoryActivity extends BaseActivity {

    private List<Product> mListProduct;
    private AdminProductAdapter mAdminProductAdapter;
    private Category mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_by_category);

        loadDataIntent();
        initView();
        loadListProduct();
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            mCategory = (Category) bundleReceived.get(Constant.KEY_INTENT_CATEGORY_OBJECT);
        }
    }

    private void initView() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(mCategory.getName());

        RecyclerView rcvData = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);
        mListProduct = new ArrayList<>();
        mAdminProductAdapter = new AdminProductAdapter(mListProduct, new IOnAdminManagerProductListener() {
            @Override
            public void onClickUpdateProduct(Product product) {
                onClickEditProduct(product);
            }

            @Override
            public void onClickDeleteProduct(Product product) {
                deleteProductItem(product);
            }
        });
        rcvData.setAdapter(mAdminProductAdapter);
    }

    private void onClickEditProduct(Product product) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_PRODUCT_OBJECT, product);
        GlobalFunction.startActivity(this, AdminAddProductActivity.class, bundle);
    }

    private void deleteProductItem(Product product) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> MyApplication.get(this).getProductDatabaseReference()
                        .child(String.valueOf(product.getId())).removeValue((error, ref) ->
                                Toast.makeText(this,
                                        getString(R.string.msg_delete_product_successfully),
                                        Toast.LENGTH_SHORT).show()))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void resetListProduct() {
        if (mListProduct != null) {
            mListProduct.clear();
        } else {
            mListProduct = new ArrayList<>();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadListProduct() {
        MyApplication.get(this).getProductDatabaseReference()
                .orderByChild("category_id").equalTo(mCategory.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        resetListProduct();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Product product = dataSnapshot.getValue(Product.class);
                            if (product == null) return;
                            mListProduct.add(0, product);
                        }
                        if (mAdminProductAdapter != null) mAdminProductAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}