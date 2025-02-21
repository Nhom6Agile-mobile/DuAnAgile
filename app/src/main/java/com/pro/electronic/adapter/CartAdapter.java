package com.pro.electronic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.electronic.R;
import com.pro.electronic.model.Product;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.GlideUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<Product> listProduct;
    private final IClickCartListener iClickCartListener;

    public interface IClickCartListener {
        void onClickDeleteItem(Product product, int position);
        void onClickUpdateItem(Product product, int position);
        void onClickEditItem(Product product);
    }

    public CartAdapter(List<Product> list, IClickCartListener listener) {
        this.listProduct = list;
        this.iClickCartListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }
    private String formatPrice(int price) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Dấu chấm phân cách hàng nghìn
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols); // Định dạng không có phần thập phân
        return decimalFormat.format(price);
    }



    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = listProduct.get(position);
        if (product == null) return;

        GlideUtils.loadUrl(product.getImage(), holder.imgProduct);
        holder.tvName.setText(product.getName());

        String strPrice = formatPrice(product.getPriceOneProduct()) + Constant.CURRENCY;
        holder.tvPrice.setText(strPrice);
        holder.tvDescription.setText(product.getDescription());

        String strQuantity = "x" + product.getCount();
        holder.tvQuantity.setText(strQuantity);
        holder.tvCount.setText(String.valueOf(product.getCount()));

        holder.tvSub.setOnClickListener(v -> {
            String strCount = holder.tvCount.getText().toString();
            int count = Integer.parseInt(strCount);
            if (count <= 1) {
                return;
            }
            int newCount = count - 1;
            holder.tvCount.setText(String.valueOf(newCount));

            int totalPrice = product.getPriceOneProduct() * newCount;
            product.setCount(newCount);
            product.setTotalPrice(totalPrice);

            iClickCartListener.onClickUpdateItem(product, holder.getAdapterPosition());
        });

        holder.tvAdd.setOnClickListener(v -> {
            int newCount = Integer.parseInt(holder.tvCount.getText().toString()) + 1;
            holder.tvCount.setText(String.valueOf(newCount));

            int totalPrice = product.getPriceOneProduct() * newCount;
            product.setCount(newCount);
            product.setTotalPrice(totalPrice);

            iClickCartListener.onClickUpdateItem(product, holder.getAdapterPosition());
        });

        holder.imgEdit.setOnClickListener(v -> iClickCartListener.onClickEditItem(product));
        holder.imgDelete.setOnClickListener(v
                -> iClickCartListener.onClickDeleteItem(product, holder.getAdapterPosition()));
    }


    @Override
    public int getItemCount() {
        if (listProduct != null) {
            return listProduct.size();
        }
        return 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgProduct;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvDescription;
        private final TextView tvQuantity;
        private final TextView tvSub;
        private final TextView tvCount;
        private final TextView tvAdd;
        private final ImageView imgEdit;
        private final ImageView imgDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSub = itemView.findViewById(R.id.tv_sub);
            tvAdd = itemView.findViewById(R.id.tv_add);
            tvCount = itemView.findViewById(R.id.tv_count);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}
