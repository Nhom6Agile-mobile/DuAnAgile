package com.pro.electronic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.electronic.R;
import com.pro.electronic.model.ProductOrder;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.GlideUtils;

import java.util.List;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.ProductOrderViewHolder> {

    private final List<ProductOrder> listProductOrder;

    public ProductOrderAdapter(List<ProductOrder> list) {
        this.listProductOrder = list;
    }

    @NonNull
    @Override
    public ProductOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_order, parent, false);
        return new ProductOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductOrderViewHolder holder, int position) {
        ProductOrder productOrder = listProductOrder.get(position);
        if (productOrder == null) return;

        GlideUtils.loadUrl(productOrder.getImage(), holder.imgProduct);
        holder.tvName.setText(productOrder.getName());
        String strPrice = productOrder.getPrice() + Constant.CURRENCY;
        holder.tvPrice.setText(strPrice);
        holder.tvDescription.setText(productOrder.getDescription());
        String strCount = "x" + productOrder.getCount();
        holder.tvCount.setText(strCount);
    }

    @Override
    public int getItemCount() {
        if (listProductOrder != null) {
            return listProductOrder.size();
        }
        return 0;
    }

    public static class ProductOrderViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgProduct;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvCount;
        private final TextView tvDescription;

        public ProductOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
}
