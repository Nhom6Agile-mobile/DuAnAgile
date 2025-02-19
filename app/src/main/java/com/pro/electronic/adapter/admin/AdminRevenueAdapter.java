package com.pro.electronic.adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.electronic.R;
import com.pro.electronic.model.Order;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.DateTimeUtils;

import java.util.List;

public class AdminRevenueAdapter extends RecyclerView.Adapter<AdminRevenueAdapter.RevenueViewHolder> {

    private final List<Order> mListOrder;

    public AdminRevenueAdapter(List<Order> mListOrder) {
        this.mListOrder = mListOrder;
    }

    @NonNull
    @Override
    public RevenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_revenue, parent, false);
        return new RevenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RevenueViewHolder holder, int position) {
        Order order = mListOrder.get(position);
        if (order == null) {
            return;
        }
        holder.tvId.setText(String.valueOf(order.getId()));
        holder.tvDate.setText(DateTimeUtils.convertTimeStampToDate_2(order.getId()));

        String strAmount = order.getTotal() + Constant.CURRENCY;
        holder.tvTotalAmount.setText(strAmount);
    }

    @Override
    public int getItemCount() {
        if (mListOrder != null) {
            return mListOrder.size();
        }
        return 0;
    }

    public static class RevenueViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvId;
        private final TextView tvDate;
        private final TextView tvTotalAmount;

        public RevenueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_id);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
        }
    }
}
