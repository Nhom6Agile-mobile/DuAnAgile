package com.pro.electronic.listener;

import com.pro.electronic.model.Product;

public interface IOnAdminManagerProductListener {
    void onClickUpdateProduct(Product product);
    void onClickDeleteProduct(Product product);
}
