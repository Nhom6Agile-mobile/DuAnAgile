package com.pro.electronic.adapter;

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
import com.pro.electronic.listener.IClickProductListener;
import com.pro.electronic.model.Product;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.GlideUtils;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> listProduct;
    private final IClickProductListener iClickProductListener;

    public ProductAdapter(List<Product> list, IClickProductListener listener) {
        this.listProduct = list;
        this.iClickProductListener = listener;
    }
    public static final String CURRENCY = "vnd";
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = listProduct.get(position);
        if (product == null) return;

        GlideUtils.loadUrl(product.getImage(), holder.imgProduct);
        holder.tvName.setText(product.getName());
        holder.tvDescription.setText(product.getDescription());
        holder.tvRate.setText(String.valueOf(product.getRate()));

        String formattedPrice = String.format("%,d", product.getPrice()).replace(",",".");
        String formattedRealPrice = String.format("%,d", product.getRealPrice()).replace(",",".");
        if (product.getSale() <= 0) {
            holder.tvPrice.setVisibility(View.GONE);
            String strPrice = formattedPrice + "" + Constant.CURRENCY;
            holder.tvPriceSale.setText(strPrice);
        } else {
            holder.tvPrice.setVisibility(View.VISIBLE);

            String strOldPrice = formattedPrice + "" + Constant.CURRENCY;
            holder.tvPrice.setText(strOldPrice);
            holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String strRealPrice = formattedRealPrice + "" + Constant.CURRENCY;
            holder.tvPriceSale.setText(strRealPrice);
        }

        holder.layoutItem.setOnClickListener(view
                -> iClickProductListener.onClickProductItem(product));
    }

    @Override
    public int getItemCount() {
        if (listProduct != null) {
            return listProduct.size();
        }
        return 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgProduct;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvPriceSale;
        private final TextView tvDescription;
        private final TextView tvRate;
        private final LinearLayout layoutItem;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPriceSale = itemView.findViewById(R.id.tv_price_sale);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvRate = itemView.findViewById(R.id.tv_rate);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
