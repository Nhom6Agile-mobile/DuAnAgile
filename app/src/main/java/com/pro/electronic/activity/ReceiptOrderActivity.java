package com.pro.electronic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.electronic.MyApplication;
import com.pro.electronic.R;
import com.pro.electronic.adapter.ProductOrderAdapter;
import com.pro.electronic.model.Order;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.DateTimeUtils;
import com.pro.electronic.utils.GlobalFunction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class ReceiptOrderActivity extends BaseActivity {

    private TextView tvIdTransaction, tvDateTime;
    private RecyclerView rcvProducts;
    private TextView tvPrice, tvTotal, tvPaymentMethod;
    private TextView tvName, tvPhone, tvAddress;
    private TextView tvTrackingOrder;

    private long orderId;
    private Order mOrder;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_order);

        getDataIntent();
        initToolbar();
        initUi();
        initListener();
        getOrderDetailFromFirebase();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        orderId = bundle.getLong(Constant.ORDER_ID);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText(getString(R.string.label_receipt_order));
    }

    private void initUi() {
        tvIdTransaction = findViewById(R.id.tv_id_transaction);
        tvDateTime = findViewById(R.id.tv_date_time);
        rcvProducts = findViewById(R.id.rcv_products);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvProducts.setLayoutManager(linearLayoutManager);
        tvPrice = findViewById(R.id.tv_price);

        tvTotal = findViewById(R.id.tv_total);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvTrackingOrder = findViewById(R.id.tv_tracking_order);
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
    }

    private void initListener() {
        tvTrackingOrder.setOnClickListener(view -> {
            if (mOrder == null) return;
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.ORDER_ID, mOrder.getId());
            GlobalFunction.startActivity(ReceiptOrderActivity.this,
                    TrackingOrderActivity.class, bundle);
            finish();
        });
    }

    private void getOrderDetailFromFirebase() {
        showProgressDialog(true);
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                mOrder = snapshot.getValue(Order.class);
                if (mOrder == null) return;

                initData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
                showToastMessage(getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).getOrderDetailDatabaseReference(orderId)
                .addValueEventListener(mValueEventListener);
    }
    private String formatCurrency(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Đặt dấu phân cách là dấu chấm
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount);
    }

    private void initData() {
        tvIdTransaction.setText(String.valueOf(mOrder.getId()));
        tvDateTime.setText(DateTimeUtils.convertTimeStampToDate(Long.parseLong(mOrder.getDateTime())));

        // Định dạng giá và tổng
        String strPrice = formatCurrency(mOrder.getPrice()) + Constant.CURRENCY;
        tvPrice.setText(strPrice);

        String strTotal = formatCurrency(mOrder.getTotal()) + Constant.CURRENCY;
        tvTotal.setText(strTotal);

        tvPaymentMethod.setText(mOrder.getPaymentMethod());
        tvName.setText(mOrder.getAddress().getName());
        tvPhone.setText(mOrder.getAddress().getPhone());
        tvAddress.setText(mOrder.getAddress().getAddress());

        // Cập nhật danh sách sản phẩm
        ProductOrderAdapter adapter = new ProductOrderAdapter(mOrder.getProducts());
        rcvProducts.setAdapter(adapter);

        // Ẩn/Hiện nút theo trạng thái đơn hàng
        if (Order.STATUS_COMPLETE == mOrder.getStatus()) {
            tvTrackingOrder.setVisibility(View.GONE);
        } else {
            tvTrackingOrder.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            MyApplication.get(this).getOrderDetailDatabaseReference(orderId)
                    .removeEventListener(mValueEventListener);
        }
    }
}
