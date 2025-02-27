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
import com.pro.electronic.adapter.admin.AdminRevenueAdapter;
import com.pro.electronic.listener.IOnSingleClickListener;
import com.pro.electronic.model.Order;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.DateTimeUtils;
import com.pro.electronic.utils.GlobalFunction;
import com.pro.electronic.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminRevenueActivity extends BaseActivity {

    private TextView tvDateFrom, tvDateTo, tvTotalValue;
    private RecyclerView rcvOrderHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_revenue);

        initToolbar();
        initUi();
        initListener();
        getListRevenue();
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(getString(R.string.revenue));
    }

    private void initUi() {
        tvDateFrom = findViewById(R.id.tv_date_from);
        tvDateTo = findViewById(R.id.tv_date_to);
        tvTotalValue = findViewById(R.id.tv_total_value);
        rcvOrderHistory = findViewById(R.id.rcv_order_history);
    }
    private void initListener() {
        tvDateFrom.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(AdminRevenueActivity.this, tvDateFrom.getText().toString(), date -> {
                    tvDateFrom.setText(date);
                    getListRevenue();
                });
            }
        });

        tvDateTo.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(AdminRevenueActivity.this, tvDateTo.getText().toString(), date -> {
                    tvDateTo.setText(date);
                    getListRevenue();
                });
            }
        });
    }

    private void getListRevenue() {
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
                handleDataHistories(list);
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

    private void handleDataHistories(List<Order> list) {
        if (list == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvOrderHistory.setLayoutManager(linearLayoutManager);
        AdminRevenueAdapter adminRevenueAdapter = new AdminRevenueAdapter(list);
        rcvOrderHistory.setAdapter(adminRevenueAdapter);

        // Calculate total
        String strTotalValue = getTotalValues(list) + Constant.CURRENCY;
        tvTotalValue.setText(strTotalValue);
    }

    private int getTotalValues(List<Order> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }

        int total = 0;
        for (Order order : list) {
            total += order.getTotal();
        }
        return total;
    }
}