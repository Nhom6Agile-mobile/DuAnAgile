package com.pro.electronic.listener;

import com.pro.electronic.model.Category;

public interface IOnAdminManagerCategoryListener {
    void onClickUpdateCategory(Category category);
    void onClickDeleteCategory(Category category);
    void onClickItemCategory(Category category);
}
