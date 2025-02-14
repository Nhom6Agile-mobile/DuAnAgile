package com.pro.electronic.fragment.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.pro.electronic.MyApplication;
import com.pro.electronic.R;
import com.pro.electronic.activity.admin.AdminAddProductActivity;
import com.pro.electronic.adapter.admin.AdminProductAdapter;
import com.pro.electronic.listener.IOnAdminManagerProductListener;
import com.pro.electronic.model.Product;
import com.pro.electronic.utils.Constant;
import com.pro.electronic.utils.GlobalFunction;
import com.pro.electronic.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminProductFragment extends Fragment {

    private View mView;
    private List<Product> mListProduct;
    private AdminProductAdapter mAdminProductAdapter;
    private ChildEventListener mChildEventListener;
    private EditText edtSearchName;
    private ImageView imgSearch;
    private FloatingActionButton btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_admin_product, container, false);

        initUi();
        initView();
        initListener();
        loadListProduct("");

        return mView;
    }

    private void initUi() {
        edtSearchName = mView.findViewById(R.id.edt_search_name);
        imgSearch = mView.findViewById(R.id.img_search);
        btnAdd = mView.findViewById(R.id.btn_add);
    }

    private void initView() {
        RecyclerView rcvData = mView.findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvData.setLayoutManager(linearLayoutManager);
        mListProduct = new ArrayList<>();
        mAdminProductAdapter = new AdminProductAdapter(mListProduct, new IOnAdminManagerProductListener() {
            @Override
            public void onClickUpdateProduct(Product product) {
                onClickEditProduct(product);
            }

            @Override
            public void onClickDeleteProduct(Product product) {
                deleteProductItem(product);
            }
        });
        rcvData.setAdapter(mAdminProductAdapter);
        rcvData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    btnAdd.hide();
                } else {
                    btnAdd.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initListener() {
        btnAdd.setOnClickListener(v -> onClickAddProduct());

        imgSearch.setOnClickListener(v -> searchProduct());

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchProduct();
                return true;
            }
            return false;
        });

        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.isEmpty()) {
                    searchProduct();
                }
            }
        });
    }

    private void onClickAddProduct() {
        GlobalFunction.startActivity(getActivity(), AdminAddProductActivity.class);
    }

    private void onClickEditProduct(Product product) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_PRODUCT_OBJECT, product);
        GlobalFunction.startActivity(getActivity(), AdminAddProductActivity.class, bundle);
    }

    private void deleteProductItem(Product product) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    MyApplication.get(getActivity()).getProductDatabaseReference()
                            .child(String.valueOf(product.getId())).removeValue((error, ref) ->
                                    Toast.makeText(getActivity(),
                                            getString(R.string.msg_delete_product_successfully),
                                            Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchProduct() {
        String strKey = edtSearchName.getText().toString().trim();
        resetListProduct();
        if (getActivity() != null && mChildEventListener != null) {
            MyApplication.get(getActivity()).getProductDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
        loadListProduct(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    private void resetListProduct() {
        if (mListProduct != null) {
            mListProduct.clear();
        } else {
            mListProduct = new ArrayList<>();
        }
    }

    public void loadListProduct(String keyword) {
        if (getActivity() == null) return;
        mChildEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product == null || mListProduct == null) return;
                if (StringUtil.isEmpty(keyword)) {
                    mListProduct.add(0, product);
                } else {
                    if (GlobalFunction.getTextSearch(product.getName()).toLowerCase().trim()
                            .contains(GlobalFunction.getTextSearch(keyword).toLowerCase().trim())) {
                        mListProduct.add(0, product);
                    }
                }
                if (mAdminProductAdapter != null) mAdminProductAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product == null || mListProduct == null || mListProduct.isEmpty()) return;
                for (int i = 0; i < mListProduct.size(); i++) {
                    if (product.getId() == mListProduct.get(i).getId()) {
                        mListProduct.set(i, product);
                        break;
                    }
                }
                if (mAdminProductAdapter != null) mAdminProductAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product == null || mListProduct == null || mListProduct.isEmpty()) return;
                for (Product productObject : mListProduct) {
                    if (product.getId() == productObject.getId()) {
                        mListProduct.remove(productObject);
                        break;
                    }
                }
                if (mAdminProductAdapter != null) mAdminProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        MyApplication.get(getActivity()).getProductDatabaseReference().addChildEventListener(mChildEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null && mChildEventListener != null) {
            MyApplication.get(getActivity()).getProductDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
    }
}
