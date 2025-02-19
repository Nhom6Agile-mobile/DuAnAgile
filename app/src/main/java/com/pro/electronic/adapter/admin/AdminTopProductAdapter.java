package com.pro.electronic.adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.electronic.R;
import com.pro.electronic.model.ProductOrder;
import com.pro.electronic.utils.Constant;

import java.util.List;

public class AdminTopProductAdapter extends RecyclerView.Adapter<AdminTopProductAdapter.AdminTopProductViewHolder> {

    private final List<ProductOrder> mListProducts;

    public AdminTopProductAdapter(List<ProductOrder> mListProducts) {
        this.mListProducts = mListProducts;
    }

    @NonNull
    @Override
    public AdminTopProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_top_product,
                parent, false);
        return new AdminTopProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminTopProductViewHolder holder, int position) {
        ProductOrder product = mListProducts.get(position);
        if (product == null) return;
        holder.tvStt.setText(String.valueOf(position + 1));
        holder.tvProductName.setText(product.getName());
        holder.tvQuantity.setText(String.valueOf(product.getCount()));
        String strTotalPrice = product.getPrice() * product.getCount() + Constant.CURRENCY;
        holder.tvTotalPrice.setText(strTotalPrice);
    }

    @Override
    public int getItemCount() {
        if (mListProducts != null) {
            return mListProducts.size();
        }
        return 0;
    }

    public static class AdminTopProductViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvStt;
        private final TextView tvProductName;
        private final TextView tvQuantity;
        private final TextView tvTotalPrice;

        public AdminTopProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tv_stt);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
        }
    }
}
