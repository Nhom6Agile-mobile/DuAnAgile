package com.pro.electronic.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pro.electronic.R;
import com.pro.electronic.adapter.MyViewPagerAdapter;
import com.pro.electronic.database.ProductDatabase;
import com.pro.electronic.event.DisplayCartEvent;
import com.pro.electronic.model.Product;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.GlobalFunction;
import com.pro.electronic.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MainActivity extends BaseActivity {

    private BottomNavigationView mBottomNavigationView;
    private ViewPager2 mViewPager2;
    private RelativeLayout layoutCartBottom;
    private TextView tvCountItem, tvProductsName, tvAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        initUi();

        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mViewPager2 = findViewById(R.id.viewpager_2);
        mViewPager2.setUserInputEnabled(false);
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(this);
        mViewPager2.setAdapter(myViewPagerAdapter);

        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mBottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
                        break;

                    case 1:
                        mBottomNavigationView.getMenu().findItem(R.id.nav_history).setChecked(true);
                        break;

                    case 2:
                        mBottomNavigationView.getMenu().findItem(R.id.nav_account).setChecked(true);
                        break;
                }
            }
        });

        mBottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                mViewPager2.setCurrentItem(0);
            } else if (id == R.id.nav_history) {
                mViewPager2.setCurrentItem(1);
            } else if (id == R.id.nav_account) {
                mViewPager2.setCurrentItem(2);
            }
            return true;
        });

        displayLayoutCartBottom();
    }

    private void initUi() {
        layoutCartBottom = findViewById(R.id.layout_cart_bottom);
        tvCountItem = findViewById(R.id.tv_count_item);
        tvProductsName = findViewById(R.id.tv_products_name);
        tvAmount = findViewById(R.id.tv_amount);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showConfirmExitApp();
    }

    private void showConfirmExitApp() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finish())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisplayCartEvent(DisplayCartEvent event) {
        displayLayoutCartBottom();
    }

    private void displayLayoutCartBottom() {
        List<Product> listProduct = ProductDatabase.getInstance(this).productDAO().getListProductCart();
        if (listProduct == null || listProduct.isEmpty()) {
            layoutCartBottom.setVisibility(View.GONE);
        } else {
            layoutCartBottom.setVisibility(View.VISIBLE);
            String strCountItem = listProduct.size() + " " + getString(R.string.label_item);
            tvCountItem.setText(strCountItem);

            String strProductsName = "";
            for (Product product : listProduct) {
                if (StringUtil.isEmpty(strProductsName)) {
                    strProductsName += product.getName();
                } else {
                    strProductsName += ", " + product.getName();
                }
            }
            if (StringUtil.isEmpty(strProductsName)) {
                tvProductsName.setVisibility(View.GONE);
            } else {
                tvProductsName.setVisibility(View.VISIBLE);
                tvProductsName.setText(strProductsName);
            }

            int amount = 0;
            for (Product product : listProduct) {
                amount = amount + product.getTotalPrice();
            }
            String strAmount = amount + Constant.CURRENCY;
            tvAmount.setText(strAmount);
        }
//        layoutCartBottom.setOnClickListener(v ->
//                GlobalFunction.startActivity(this, CartActivity.class));
    }

    public ViewPager2 getViewPager2() {
        return mViewPager2;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
