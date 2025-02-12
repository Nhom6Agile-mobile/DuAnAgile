package com.pro.electronic.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.electronic.MyApplication;
import com.pro.electronic.R;
import com.pro.electronic.database.ProductDatabase;
import com.pro.electronic.event.DisplayCartEvent;
import com.pro.electronic.model.Product;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.GlideUtils;
import com.pro.electronic.utils.GlobalFunction;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class ProductDetailActivity extends BaseActivity {

    private ImageView imgProduct;
    private TextView tvName;
    private TextView tvPriceSale;
    private TextView tvDescription;
    private TextView tvSub;
    private TextView tvAdd;
    private TextView tvCount;
    private RelativeLayout layoutRatingAndReview;
    private TextView tvRate;
    private TextView tvCountReview;
    private TextView tvInfo;
    private TextView tvTotal;
    private TextView tvAddOrder;

    private long mProductId;
    private Product mProductOld;
    private Product mProduct;

    private ValueEventListener mProductValueEventListener;
    private String formatCurrency(int amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Đặt dấu phân cách là dấu chấm
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        getDataIntent();
        initUi();
        getProductDetailFromFirebase();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        mProductId = bundle.getLong(Constant.PRODUCT_ID);
        if (bundle.get(Constant.PRODUCT_OBJECT) != null) {
            mProductOld = (Product) bundle.get(Constant.PRODUCT_OBJECT);
        }
    }

    private void initUi() {
        imgProduct = findViewById(R.id.img_product);
        tvName = findViewById(R.id.tv_name);
        tvPriceSale = findViewById(R.id.tv_price_sale);
        tvDescription = findViewById(R.id.tv_description);
        tvSub = findViewById(R.id.tv_sub);
        tvAdd = findViewById(R.id.tv_add);
        tvCount = findViewById(R.id.tv_count);
        layoutRatingAndReview = findViewById(R.id.layout_rating_and_review);
        tvCountReview = findViewById(R.id.tv_count_review);
        tvRate = findViewById(R.id.tv_rate);
        tvInfo = findViewById(R.id.tv_info);
        tvTotal = findViewById(R.id.tv_total);
        tvAddOrder = findViewById(R.id.tv_add_order);
    }

    private void getProductDetailFromFirebase() {
        showProgressDialog(true);
        mProductValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                mProduct = snapshot.getValue(Product.class);
                if (mProduct == null) return;

                initToolbar();
                initData();
                initListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
                showToastMessage(getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).getProductDetailDatabaseReference(mProductId)
                .addValueEventListener(mProductValueEventListener);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText(mProduct.getName());
    }

    private void initData() {
        if (mProduct == null) return;
        GlideUtils.loadUrlBanner(mProduct.getBanner(), imgProduct);
        tvName.setText(mProduct.getName());
        String strPrice = formatCurrency(mProduct.getRealPrice()) + Constant.CURRENCY;
        tvPriceSale.setText(strPrice);
        tvDescription.setText(mProduct.getDescription());
        if (mProductOld != null) {
            mProduct.setCount(mProductOld.getCount());
        } else {
            mProduct.setCount(1);
        }
        tvCount.setText(String.valueOf(mProduct.getCount()));
        tvRate.setText(String.valueOf(mProduct.getRate()));
        String strCountReview = "(" + mProduct.getCountReviews() + ")";
        tvCountReview.setText(strCountReview);

        if (mProduct.getInfo() != null) {
            String[] temp = mProduct.getInfo().split(",");
            StringBuilder strInfo = new StringBuilder();
            for (String s : temp) {
                if (strInfo.length() == 0) {
                    strInfo.append(s.trim());
                } else {
                    strInfo.append("\n").append(s.trim());
                }
            }
            tvInfo.setText(strInfo.toString());
        }

        calculatorTotalPrice();
    }

    private void initListener() {
        tvSub.setOnClickListener(v -> {
            int count = Integer.parseInt(tvCount.getText().toString());
            if (count <= 1) {
                return;
            }
            int newCount = Integer.parseInt(tvCount.getText().toString()) - 1;
            tvCount.setText(String.valueOf(newCount));

            calculatorTotalPrice();
        });

        tvAdd.setOnClickListener(v -> {
            int newCount = Integer.parseInt(tvCount.getText().toString()) + 1;
            tvCount.setText(String.valueOf(newCount));

            calculatorTotalPrice();
        });


//        tvAddOrder.setOnClickListener(view -> {
//            if (!isProductInCart()) {
//                ProductDatabase.getInstance(ProductDetailActivity.this).productDAO().insertProduct(mProduct);
//            } else {
//                ProductDatabase.getInstance(ProductDetailActivity.this).productDAO().updateProduct(mProduct);
//            }
//            GlobalFunction.startActivity(ProductDetailActivity.this, CartActivity.class);
//            EventBus.getDefault().post(new DisplayCartEvent());
//            finish();
//        });
    }

    private void calculatorTotalPrice() {
        int count = Integer.parseInt(tvCount.getText().toString().trim());
        int priceOneProduct = mProduct.getRealPrice();
        int totalPrice = priceOneProduct * count;

        // Định dạng số tiền với dấu ngăn cách hàng nghìn
        String strTotalPrice = formatCurrency(totalPrice) + Constant.CURRENCY;
        tvTotal.setText(strTotalPrice);

        mProduct.setCount(count);
        mProduct.setPriceOneProduct(priceOneProduct);
        mProduct.setTotalPrice(totalPrice);
    }


    private boolean isProductInCart() {
        List<Product> list = ProductDatabase.getInstance(this)
                .productDAO().checkProductInCart(mProduct.getId());
        return list != null && !list.isEmpty();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProductValueEventListener != null) {
            MyApplication.get(this).getProductDetailDatabaseReference(mProductId)
                    .removeEventListener(mProductValueEventListener);
        }
    }
}
