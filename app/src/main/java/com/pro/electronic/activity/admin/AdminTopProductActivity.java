package com.pro.electronic.activity.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.electronic.MyApplication;
import com.pro.electronic.R;
import com.pro.electronic.activity.BaseActivity;
import com.pro.electronic.adapter.admin.AdminTopProductAdapter;
import com.pro.electronic.listener.IOnSingleClickListener;
import com.pro.electronic.model.Order;
import com.pro.electronic.model.ProductOrder;
import com.pro.electronic.utils.DateTimeUtils;
import com.pro.electronic.utils.GlobalFunction;
import com.pro.electronic.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminTopProductActivity extends BaseActivity {

    private TextView tvDateFrom, tvDateTo;
    private RecyclerView rcvData;
    private List<ProductOrder> mListProductOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_top_product);

        initToolbar();
        initUi();
        initListener();
        getListTopProduct();
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(getString(R.string.label_top_product));
    }

    private void initUi() {
        tvDateFrom = findViewById(R.id.tv_date_from);
        tvDateTo = findViewById(R.id.tv_date_to);
        rcvData = findViewById(R.id.rcv_data);
    }
    private void initListener() {
        tvDateFrom.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(AdminTopProductActivity.this, tvDateFrom.getText().toString(), date -> {
                    tvDateFrom.setText(date);
                    getListTopProduct();
                });
            }
        });

        tvDateTo.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(AdminTopProductActivity.this, tvDateTo.getText().toString(), date -> {
                    tvDateTo.setText(date);
                    getListTopProduct();
                });
            }
        });
    }

    private void getListTopProduct() {
        MyApplication.get(this).getOrderDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (canAddOrder(order)) {
                        list.add(0, order);
                    }
                }
                handleDataTopProduct(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private boolean canAddOrder(@Nullable Order order) {
        if (order == null) return false;
        if (Order.STATUS_COMPLETE != order.getStatus()) return false;
        String strDateFrom = tvDateFrom.getText().toString();
        String strDateTo = tvDateTo.getText().toString();
        if (StringUtil.isEmpty(strDateFrom) && StringUtil.isEmpty(strDateTo)) {
            return true;
        }
        String strDateOrder = DateTimeUtils.convertTimeStampToDate_2(order.getId());
        long longOrder = Long.parseLong(DateTimeUtils.convertDate2ToTimeStamp(strDateOrder));

        if (StringUtil.isEmpty(strDateFrom) && !StringUtil.isEmpty(strDateTo)) {
            long longDateTo = Long.parseLong(DateTimeUtils.convertDate2ToTimeStamp(strDateTo));
            return longOrder <= longDateTo;
        }
        if (!StringUtil.isEmpty(strDateFrom) && StringUtil.isEmpty(strDateTo)) {
            long longDateFrom = Long.parseLong(DateTimeUtils.convertDate2ToTimeStamp(strDateFrom));
            return longOrder >= longDateFrom;
        }
        long longDateTo = Long.parseLong(DateTimeUtils.convertDate2ToTimeStamp(strDateTo));
        long longDateFrom = Long.parseLong(DateTimeUtils.convertDate2ToTimeStamp(strDateFrom));
        return longOrder >= longDateFrom && longOrder <= longDateTo;
    }

    private void handleDataTopProduct(List<Order> list) {
        if (list == null) return;
        if (mListProductOrder != null) {
            mListProductOrder.clear();
        } else {
            mListProductOrder = new ArrayList<>();
        }
        for (Order order : list) {
            for (ProductOrder productOrder : order.getProducts()) {
                long productOrderId = productOrder.getId();
                if (checkProductOrderExist(productOrderId)) {
                    ProductOrder productOrderExist = getProductOrderFromId(productOrderId);
                    productOrderExist.setCount(productOrderExist.getCount() + productOrder.getCount());
                } else {
                    mListProductOrder.add(productOrder);
                }
            }
        }
        List<ProductOrder> listProductOrderDisplay = new ArrayList<>(mListProductOrder);
        Collections.sort(listProductOrderDisplay, (productOrder1, productOrder2)
                -> productOrder2.getCount() - productOrder1.getCount());
        displayDataTopProduct(listProductOrderDisplay);
    }

    private boolean checkProductOrderExist(long productOrderId) {
        if (mListProductOrder == null || mListProductOrder.isEmpty()) return false;
        boolean result = false;
        for (ProductOrder productOrder : mListProductOrder) {
            if (productOrderId == productOrder.getId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private ProductOrder getProductOrderFromId(long productOrderId) {
        ProductOrder result = null;
        for (ProductOrder productOrder : mListProductOrder) {
            if (productOrderId == productOrder.getId()) {
                result = productOrder;
                break;
            }
        }
        return result;
    }

    private void displayDataTopProduct(List<ProductOrder> list) {
        if (list == null) return;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);
        AdminTopProductAdapter adminTopProductAdapter = new AdminTopProductAdapter(list);
        rcvData.setAdapter(adminTopProductAdapter);
    }
}