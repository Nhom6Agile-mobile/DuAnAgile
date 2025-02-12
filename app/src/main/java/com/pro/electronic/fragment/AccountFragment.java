package com.pro.electronic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.pro.electronic.R;
import com.pro.electronic.activity.ChangePasswordActivity;
import com.pro.electronic.activity.LoginActivity;
import com.pro.electronic.activity.MainActivity;
import com.pro.electronic.prefs.DataStoreManager;
import com.pro.electronic.utils.GlobalFunction;

public class AccountFragment extends Fragment {

    private View mView;
    private LinearLayout layoutChangePassword;
    private LinearLayout layoutSignOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_account, container, false);

        initToolbar();
        initUi();
        initListener();

        return mView;
    }

    private void initToolbar() {
        ImageView imgToolbarBack = mView.findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = mView.findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> backToHomeScreen());
        tvToolbarTitle.setText(getString(R.string.nav_account));
    }

    private void backToHomeScreen() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) return;
        mainActivity.getViewPager2().setCurrentItem(0);
    }

    private void initUi() {
        TextView tvUsername = mView.findViewById(R.id.tv_username);
        tvUsername.setText(DataStoreManager.getUser().getEmail());

        layoutChangePassword = mView.findViewById(R.id.layout_change_password);
        layoutSignOut = mView.findViewById(R.id.layout_sign_out);
    }

    private void initListener() {
        layoutChangePassword.setOnClickListener(view ->
                GlobalFunction.startActivity(getActivity(), ChangePasswordActivity.class));
        layoutSignOut.setOnClickListener(view -> onClickSignOut());
    }

    private void onClickSignOut() {
        if (getActivity() == null) return;

        FirebaseAuth.getInstance().signOut();
        DataStoreManager.setUser(null);
        GlobalFunction.startActivity(getActivity(), LoginActivity.class);
        getActivity().finishAffinity();
    }
}
