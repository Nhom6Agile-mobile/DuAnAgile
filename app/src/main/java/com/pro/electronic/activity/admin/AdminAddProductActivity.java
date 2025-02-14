package com.pro.electronic.activity.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.electronic.MyApplication;
import com.pro.electronic.R;
import com.pro.electronic.activity.BaseActivity;
import com.pro.electronic.adapter.admin.AdminSelectAdapter;
import com.pro.electronic.model.Category;
import com.pro.electronic.model.Product;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.GlobalFunction;
import com.pro.electronic.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAddProductActivity extends BaseActivity {

    private TextView tvToolbarTitle;
    private EditText edtName, edtDescription, edtPrice, edtImage, edtImageBanner, edtInfo;
    private CheckBox chbFeatured;
    private Spinner spnCategory;
    private Button btnAddOrEdit;

    private boolean isUpdate;
    private Product mProduct;
    private Category mCategorySelected;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        loadDataIntent();
        initUi();
        initData();
    }
    private String formatCurrency(int price) {
        return String.format("%,d", price).replace(",", ".");
    }


    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mProduct = (Product) bundleReceived.get(Constant.KEY_INTENT_PRODUCT_OBJECT);
        }
    }

    private void initUi() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        edtName = findViewById(R.id.edt_name);
        edtDescription = findViewById(R.id.edt_description);
        edtInfo = findViewById(R.id.edt_info);
        edtPrice = findViewById(R.id.edt_price);
        edtImage = findViewById(R.id.edt_image);
        edtImageBanner = findViewById(R.id.edt_image_banner);
        chbFeatured = findViewById(R.id.chb_featured);
        btnAddOrEdit = findViewById(R.id.btn_add_or_edit);
        spnCategory = findViewById(R.id.spn_category);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        btnAddOrEdit.setOnClickListener(v -> addOrEditProduct());
    }

    private void initData() {
        if (isUpdate) {
            tvToolbarTitle.setText(getString(R.string.label_update_product));
            btnAddOrEdit.setText(getString(R.string.action_edit));

            edtName.setText(mProduct.getName());
            edtDescription.setText(mProduct.getDescription());
            edtInfo.setText(mProduct.getInfo());
            edtPrice.setText(formatCurrency(mProduct.getPrice()));
            edtImage.setText(mProduct.getImage());
            edtImageBanner.setText(mProduct.getBanner());
            chbFeatured.setChecked(mProduct.isFeatured());
        } else {
            tvToolbarTitle.setText(getString(R.string.label_add_product));
            btnAddOrEdit.setText(getString(R.string.action_add));
        }
        loadListCategory();
    }

    private void loadListCategory() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category == null) return;
                    list.add(0, category);
                }
                AdminSelectAdapter adapter = new AdminSelectAdapter(AdminAddProductActivity.this,
                        R.layout.item_choose_option, list);
                spnCategory.setAdapter(adapter);
                spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mCategorySelected = adapter.getItem(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                if (mProduct != null && mProduct.getCategory_id() > 0) {
                    spnCategory.setSelection(getPositionSelected(list, mProduct.getCategory_id()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        MyApplication.get(this).getCategoryDatabaseReference()
                .addValueEventListener(mValueEventListener);
    }

    private int getPositionSelected(List<Category> list, long id) {
        int position = 0;
        for (int i = 0; i < list.size(); i++) {
            if (id == list.get(i).getId()) {
                position = i;
                break;
            }
        }
        return position;
    }
    private int parseCurrency(String currency) {
        try {
            // Loại bỏ dấu phân cách và đơn vị tiền tệ trước khi chuyển đổi
            return Integer.parseInt(currency.replace(".", "").replace(" VND", ""));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0; // Trả về 0 nếu chuỗi không hợp lệ
        }
    }

    private void addOrEditProduct() {
        String strName = edtName.getText().toString().trim();
        String strDescription = edtDescription.getText().toString().trim();
        String strInfo = edtInfo.getText().toString().trim();
        String strPrice = edtPrice.getText().toString().trim();

        String strImage = edtImage.getText().toString().trim();
        String strImageBanner = edtImageBanner.getText().toString().trim();

        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strDescription)) {
            Toast.makeText(this, getString(R.string.msg_description_require), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Loại bỏ dấu phân cách trước khi chuyển đổi
            int price = parseCurrency(strPrice);
            if (price <= 0) {
                Toast.makeText(this, getString(R.string.msg_price_require), Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.msg_invalid_price), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_image_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strImageBanner)) {
            Toast.makeText(this, getString(R.string.msg_image_banner_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strInfo)) {
            Toast.makeText(this, getString(R.string.msg_product_info), Toast.LENGTH_SHORT).show();
            return;
        }



        // Update product
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("name", strName);
            map.put("description", strDescription);
            map.put("info", strInfo);
            map.put("price", parseCurrency(strPrice));
            map.put("image", strImage);
            map.put("banner", strImageBanner);
            map.put("featured", chbFeatured.isChecked());
            map.put("category_id", mCategorySelected.getId());
            map.put("category_name", mCategorySelected.getName());

            MyApplication.get(this).getProductDatabaseReference()
                    .child(String.valueOf(mProduct.getId())).updateChildren(map, (error, ref) -> {
                        showProgressDialog(false);
                        Toast.makeText(this,
                                getString(R.string.msg_edit_product_success), Toast.LENGTH_SHORT).show();
                        GlobalFunction.hideSoftKeyboard(this);
                    });
            return;
        }

        // Add product
        showProgressDialog(true);
        long productId = System.currentTimeMillis();
        Product product = new Product();
        product.setId(productId);
        product.setName(strName);
        product.setDescription(strDescription);
        product.setInfo(strInfo);
        product.setPrice(parseCurrency(strPrice));

        product.setImage(strImage);
        product.setBanner(strImageBanner);
        product.setFeatured(chbFeatured.isChecked());

        product.setCategory_id(mCategorySelected.getId());
        product.setCategory_name(mCategorySelected.getName());

        MyApplication.get(this).getProductDatabaseReference()
                .child(String.valueOf(productId)).setValue(product, (error, ref) -> {
                    showProgressDialog(false);
                    edtName.setText("");
                    edtDescription.setText("");
                    edtInfo.setText("");
                    edtPrice.setText("");
                    edtImage.setText("");
                    edtImageBanner.setText("");
                    chbFeatured.setChecked(false);
                    spnCategory.setSelection(0);
                    GlobalFunction.hideSoftKeyboard(this);
                    Toast.makeText(this, getString(R.string.msg_add_product_success), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            MyApplication.get(this).getCategoryDatabaseReference()
                    .removeEventListener(mValueEventListener);
        }
    }

}