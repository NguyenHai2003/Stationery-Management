package com.example.office_management.fragment.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.office_management.R;
import com.example.office_management.fragment.auth.LoginFragment;
import com.example.office_management.fragment.auth.RegisterFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountFragment extends Fragment {

    public AccountFragment() {
        super(R.layout.fragment_account); // Liên kết với file XML chứa BottomNavigationView
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.account_bottom_navigation);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        // Mặc định hiển thị màn hình đăng nhập trước
        if (token == null) {
            // Nếu chưa có token thì mặc định hiển thị LoginFragment
            loadFragment(new LoginFragment());
            bottomNavigationView.setSelectedItemId(R.id.login);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.login) {
                selectedFragment = new LoginFragment();
            } else if (item.getItemId() == R.id.register) {
                selectedFragment = new RegisterFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
    // Trong AccountFragment, sau khi đăng nhập thành công
}
