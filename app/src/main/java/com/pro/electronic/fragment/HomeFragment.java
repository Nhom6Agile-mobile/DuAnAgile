package com.pro.electronic.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.electronic.MyApplication;
import com.pro.electronic.R;
import com.pro.electronic.activity.ProductDetailActivity;
import com.pro.electronic.adapter.BannerViewPagerAdapter;
import com.pro.electronic.adapter.CategoryPagerAdapter;
import com.pro.electronic.event.SearchKeywordEvent;
import com.pro.electronic.model.Category;
import com.pro.electronic.model.Product;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.GlobalFunction;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment {

    private View mView;
    private ViewPager2 viewPagerProductFeatured;
    private CircleIndicator3 indicatorProductFeatured;
    private ViewPager2 viewPagerCategory;
    private TabLayout tabCategory;
    private EditText edtSearchName;
    private ImageView imgSearch;

    private List<Product> listProductFeatured;
    private List<Category> listCategory;
    private ValueEventListener mCategoryValueEventListener;
    private ValueEventListener mProductValueEventListener;

    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (viewPagerProductFeatured == null || listProductFeatured == null || listProductFeatured.isEmpty()) {
                return;
            }
            if (viewPagerProductFeatured.getCurrentItem() == listProductFeatured.size() - 1) {
                viewPagerProductFeatured.setCurrentItem(0);
                return;
            }
            viewPagerProductFeatured.setCurrentItem(viewPagerProductFeatured.getCurrentItem() + 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        initUi();
        initListener();

        getListProductBanner();
        getListCategory();

        return mView;
    }

    private void initUi() {
        viewPagerProductFeatured = mView.findViewById(R.id.view_pager_product_featured);
        indicatorProductFeatured = mView.findViewById(R.id.indicator_product_featured);
        viewPagerCategory = mView.findViewById(R.id.view_pager_category);
        viewPagerCategory.setUserInputEnabled(false);
        tabCategory = mView.findViewById(R.id.tab_category);
        edtSearchName = mView.findViewById(R.id.edt_search_name);
        imgSearch = mView.findViewById(R.id.img_search);
    }

    private void initListener() {
        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.isEmpty()) {
                    searchProduct();
                }
            }
        });

        imgSearch.setOnClickListener(view -> searchProduct());

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchProduct();
                return true;
            }
            return false;
        });
    }

    private void getListProductBanner() {
        if (getActivity() == null) return;
        mProductValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listProductFeatured != null) {
                    listProductFeatured.clear();
                } else {
                    listProductFeatured = new ArrayList<>();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null && product.isFeatured()) {
                        listProductFeatured.add(product);
                    }
                }
                displayListBanner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        MyApplication.get(getActivity()).getProductDatabaseReference()
                .addValueEventListener(mProductValueEventListener);
    }

    private void displayListBanner() {
        BannerViewPagerAdapter adapter = new BannerViewPagerAdapter(listProductFeatured, product -> {
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.PRODUCT_ID, product.getId());
            GlobalFunction.startActivity(getActivity(), ProductDetailActivity.class, bundle);
        });
        viewPagerProductFeatured.setAdapter(adapter);
        indicatorProductFeatured.setViewPager(viewPagerProductFeatured);

        viewPagerProductFeatured.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private void getListCategory() {
        if (getActivity() == null) return;
        mCategoryValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listCategory != null) {
                    listCategory.clear();
                } else {
                    listCategory = new ArrayList<>();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        listCategory.add(category);
                    }
                }
                displayTabsCategory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        MyApplication.get(getActivity()).getCategoryDatabaseReference()
                .addValueEventListener(mCategoryValueEventListener);
    }

    private void displayTabsCategory() {
        if (getActivity() == null || listCategory == null || listCategory.isEmpty()) return;
        viewPagerCategory.setOffscreenPageLimit(listCategory.size());
        CategoryPagerAdapter adapter = new CategoryPagerAdapter(getActivity(), listCategory);
        viewPagerCategory.setAdapter(adapter);
        new TabLayoutMediator(tabCategory, viewPagerCategory,
                (tab, position) -> tab.setText(listCategory.get(position).getName().toLowerCase()))
                .attach();
    }

    private void searchProduct() {
        String strKey = edtSearchName.getText().toString().trim();
        EventBus.getDefault().post(new SearchKeywordEvent(strKey));
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null && mCategoryValueEventListener != null) {
            MyApplication.get(getActivity()).getCategoryDatabaseReference()
                    .removeEventListener(mCategoryValueEventListener);
        }
        if (getActivity() != null && mProductValueEventListener != null) {
            MyApplication.get(getActivity()).getProductDatabaseReference()
                    .removeEventListener(mProductValueEventListener);
        }
    }
}
