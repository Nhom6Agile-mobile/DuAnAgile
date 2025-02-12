package com.pro.electronic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.electronic.R;
import com.pro.electronic.listener.IClickProductListener;
import com.pro.electronic.model.Product;
import com.pro.electronic.utils.GlideUtils;

import java.util.List;

public class BannerViewPagerAdapter extends RecyclerView.Adapter<BannerViewPagerAdapter.BannerViewHolder> {

    private final List<Product> mListProduct;
    private final IClickProductListener iClickProductListener;

    public BannerViewPagerAdapter(List<Product> list, IClickProductListener listener) {
        this.mListProduct = list;
        this.iClickProductListener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Product product = mListProduct.get(position);
        if (product == null) return;
        GlideUtils.loadUrlBanner(product.getBanner(), holder.imgBanner);
        holder.imgBanner.setOnClickListener(view
                -> iClickProductListener.onClickProductItem(product));
    }

    @Override
    public int getItemCount() {
        if (mListProduct != null) {
            return mListProduct.size();
        }
        return 0;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgBanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.img_banner);
        }
    }
}
