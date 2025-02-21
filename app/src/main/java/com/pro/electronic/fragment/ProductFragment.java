package com.pro.electronic.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.electronic.MyApplication;
import com.pro.electronic.R;
import com.pro.electronic.activity.ProductDetailActivity;
import com.pro.electronic.adapter.FilterAdapter;
import com.pro.electronic.adapter.ProductAdapter;
import com.pro.electronic.event.SearchKeywordEvent;
import com.pro.electronic.model.Filter;
import com.pro.electronic.model.Product;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.GlobalFunction;
import com.pro.electronic.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ProductFragment extends Fragment {

    private View mView;
    private RecyclerView rcvFilter;
    private RecyclerView rcvProduct;

    private List<Product> listProduct;
    private List<Product> listProductDisplay;
    private List<Product> listProductKeyWord;
    private List<Filter> listFilter;
    private ProductAdapter productAdapter;
    private FilterAdapter filterAdapter;
    private long categoryId;
    private Filter currentFilter;
    private String keyword = "";
    private ValueEventListener mValueEventListener;

    public static ProductFragment newInstance(long categoryId) {
        ProductFragment productFragment = new ProductFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constant.CATEGORY_ID, categoryId);
        productFragment.setArguments(bundle);
        return productFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_product, container, false);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        getDataArguments();
        initUi();
        initListener();

        getListFilter();
        getListProduct();

        return mView;
    }

    private void getDataArguments() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        categoryId = bundle.getLong(Constant.CATEGORY_ID);
    }

    private void initUi() {
        rcvFilter = mView.findViewById(R.id.rcv_filter);
        rcvProduct = mView.findViewById(R.id.rcv_product);
        displayListProduct();
    }

    private void initListener() {
    }

    private void getListFilter() {
        listFilter = new ArrayList<>();
        listFilter.add(new Filter(Filter.TYPE_FILTER_ALL, getString(R.string.filter_all)));
        listFilter.add(new Filter(Filter.TYPE_FILTER_RATE, getString(R.string.filter_rate)));
        listFilter.add(new Filter(Filter.TYPE_FILTER_PRICE, getString(R.string.filter_price)));


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        rcvFilter.setLayoutManager(linearLayoutManager);
        currentFilter = listFilter.get(0);
        currentFilter.setSelected(true);
        filterAdapter = new FilterAdapter(getActivity(), listFilter, this::handleClickFilter);
        rcvFilter.setAdapter(filterAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleClickFilter(Filter filter) {
        for (Filter filterEntity : listFilter) {
            if (filterEntity.getId() == filter.getId()) {
                filterEntity.setSelected(true);
                setListProductDisplay(filterEntity, keyword);
                currentFilter = filterEntity;
            } else {
                filterEntity.setSelected(false);
            }
        }
        if (filterAdapter != null) filterAdapter.notifyDataSetChanged();
    }

    private void getListProduct() {
        if (getActivity() == null) return;
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listProduct != null) {
                    listProduct.clear();
                } else {
                    listProduct = new ArrayList<>();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        listProduct.add(0, product);
                    }
                }
                setListProductDisplay(new Filter(Filter.TYPE_FILTER_ALL, getString(R.string.filter_all)), keyword);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        MyApplication.get(getActivity()).getProductDatabaseReference()
                .orderByChild(Constant.CATEGORY_ID).equalTo(categoryId)
                .addValueEventListener(mValueEventListener);
    }

    private void displayListProduct() {
        if (getActivity() == null) return;
        listProductDisplay = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvProduct.setLayoutManager(linearLayoutManager);
        productAdapter = new ProductAdapter(listProductDisplay, product -> {
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.PRODUCT_ID, product.getId());
            GlobalFunction.startActivity(getActivity(), ProductDetailActivity.class, bundle);
        });
        rcvProduct.setAdapter(productAdapter);
    }

    private void setListProductDisplay(@NonNull Filter filter, @Nullable String keyword) {
        if (listProduct == null || listProduct.isEmpty()) return;

        if (listProductKeyWord != null) {
            listProductKeyWord.clear();
        } else {
            listProductKeyWord = new ArrayList<>();
        }

        if (listProductDisplay != null) {
            listProductDisplay.clear();
        } else {
            listProductDisplay = new ArrayList<>();
        }

        if (!StringUtil.isEmpty(keyword)) {
            for (Product product : listProduct) {
                if (getTextSearch(product.getName()).toLowerCase().trim()
                        .contains(getTextSearch(keyword).toLowerCase().trim())) {
                    listProductKeyWord.add(product);
                }
            }
            switch (filter.getId()) {
                case Filter.TYPE_FILTER_ALL:
                    listProductDisplay.addAll(listProductKeyWord);
                    break;

                case Filter.TYPE_FILTER_RATE:
                    listProductDisplay.addAll(listProductKeyWord);
                    Collections.sort(listProductDisplay,
                            (product1, product2) -> Double.compare(product2.getRate(), product1.getRate()));
                    break;

                case Filter.TYPE_FILTER_PRICE:
                    listProductDisplay.addAll(listProductKeyWord);
                    Collections.sort(listProductDisplay,
                            (product1, product2) -> Integer.compare(product1.getRealPrice(), product2.getRealPrice()));
                    break;


            }
        } else {
            switch (filter.getId()) {
                case Filter.TYPE_FILTER_ALL:
                    listProductDisplay.addAll(listProduct);
                    break;

                case Filter.TYPE_FILTER_RATE:
                    listProductDisplay.addAll(listProduct);
                    Collections.sort(listProductDisplay,
                            (product1, product2) -> Double.compare(product2.getRate(), product1.getRate()));
                    break;

                case Filter.TYPE_FILTER_PRICE:
                    listProductDisplay.addAll(listProduct);
                    Collections.sort(listProductDisplay,
                            (product1, product2) -> Integer.compare(product1.getRealPrice(), product2.getRealPrice()));
                    break;


            }
        }
        reloadListProduct();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void reloadListProduct() {
        if (productAdapter != null) productAdapter.notifyDataSetChanged();
    }

    public String getTextSearch(String input) {
        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchKeywordEvent(SearchKeywordEvent event) {
        keyword = event.getKeyword();
        setListProductDisplay(currentFilter, keyword);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (filterAdapter != null) filterAdapter.release();
        if (getActivity() != null && mValueEventListener != null) {
            MyApplication.get(getActivity()).getProductDatabaseReference()
                    .removeEventListener(mValueEventListener);
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
