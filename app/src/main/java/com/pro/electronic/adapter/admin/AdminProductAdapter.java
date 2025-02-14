package com.pro.electronic.adapter.admin;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.electronic.R;
import com.pro.electronic.listener.IOnAdminManagerProductListener;
import com.pro.electronic.model.Product;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.GlideUtils;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder> {

    private final List<Product> listProduct;
    private final IOnAdminManagerProductListener mListener;

    public AdminProductAdapter(List<Product> list, IOnAdminManagerProductListener listener) {
        this.listProduct = list;
        this.mListener = listener;
    }
    public static final String CURRENCY = "vnd";
    @NonNull
    @Override
    public AdminProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new AdminProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductViewHolder holder, int position) {
        Product product = listProduct.get(position);
        if (product == null) return;

        GlideUtils.loadUrl(product.getImage(), holder.imgProduct);
        holder.tvName.setText(product.getName());
        String formattedPrice = String.format("%,d", product.getPrice()).replace(",", ".");
        formattedPrice = formattedPrice + Constant.CURRENCY;
        if (product.getSale() <= 0) {
            holder.tvPrice.setVisibility(View.GONE); // Ẩn giá gốc
            holder.tvPriceSale.setVisibility(View.VISIBLE);
            holder.tvPriceSale.setText(formattedPrice);
        } else {
            holder.tvPrice.setVisibility(View.VISIBLE);

            String strOldPrice = product.getPrice() + Constant.CURRENCY;
            holder.tvPrice.setText(strOldPrice);
            holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String strRealPrice = product.getRealPrice() + Constant.CURRENCY;
            holder.tvPriceSale.setText(strRealPrice);
        }
        if (product.getCategory_id() > 0) {
            holder.layoutCategory.setVisibility(View.VISIBLE);
            holder.tvCategory.setText(product.getCategory_name());
        } else {
            holder.layoutCategory.setVisibility(View.GONE);
        }
        if (product.isFeatured()) {
            holder.tvFeatured.setText("Có");
        } else {
            holder.tvFeatured.setText("Không");
        }

        holder.imgEdit.setOnClickListener(view -> mListener.onClickUpdateProduct(product));
        holder.imgDelete.setOnClickListener(view -> mListener.onClickDeleteProduct(product));
    }

    @Override
    public int getItemCount() {
        if (listProduct != null) {
            return listProduct.size();
        }
        return 0;
    }

    public static class AdminProductViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgProduct;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvPriceSale;
        private final LinearLayout layoutCategory;
        private final TextView tvCategory;
        private final TextView tvFeatured;
        private final ImageView imgEdit;
        private final ImageView imgDelete;

        public AdminProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPriceSale = itemView.findViewById(R.id.tv_price_sale);
            layoutCategory = itemView.findViewById(R.id.layout_category);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvFeatured = itemView.findViewById(R.id.tv_featured);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}
