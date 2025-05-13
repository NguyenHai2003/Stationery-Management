package com.example.office_management.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.office_management.R;
import com.example.office_management.activity.search.SearchActivity;
import com.example.office_management.adapter.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private ViewPagerAdapter viewPagerAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Lấy token từ SharedPreferences
        String token = sharedPreferences.getString("token", null);
        Log.d("AuthInterceptor", "Token: " + token);

        // Gán trạng thái đăng nhập dựa vào token
        boolean isLoggedIn = (token != null);

        // Khởi tạo adapter với trạng thái đăng nhập
        viewPagerAdapter = new ViewPagerAdapter(this, isLoggedIn);
        viewPager.setAdapter(viewPagerAdapter);

        // Nếu chưa đăng nhập, chuyển đến tab profile (AccountFragment hiển thị LoginFragment)
        if (token == null) {
            viewPager.setCurrentItem(4, false);
            bottomNavigationView.setSelectedItemId(R.id.profile);
        } else {
            viewPager.setCurrentItem(0, false);  // Hiển thị HomeFragment
        }

        // Xử lý chọn item trên BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.categories:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.chatbot:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.shopping:
                    viewPager.setCurrentItem(3);
                    return true;
                case R.id.profile:
                    if (isLoggedIn) {
                        viewPager.setCurrentItem(4); // Hiển thị UserInfoFragment
                    } else {
                        viewPager.setCurrentItem(4); // Hiển thị AccountFragment (login)
                    }
                    return true;
            }
            return false;
        });


        // Đồng bộ hóa khi vuốt ngang
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.categories);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.chatbot);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.shopping);
                        break;
                    case 4:
                        bottomNavigationView.setSelectedItemId(R.id.profile);
                        break;
                }
            }
        });
    }
    // Phương thức gọi khi đăng nhập thành công
    public void onLoginSuccess() {
        // Lưu trạng thái đăng nhập
        sharedPreferences.edit().putString("token", "your_token_here").apply(); // Lưu token (giả định bạn có token khi đăng nhập thành công)

        // Cập nhật trạng thái đăng nhập
        viewPagerAdapter.setLoggedIn(true);

        // Nếu đang ở trang profile, làm mới nó ngay lập tức
        if (viewPager.getCurrentItem() == 4) {
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(4, false);  // giữ ở Profile tab
        }
    }

    // Phương thức gọi khi đăng xuất
    public void onLogout() {
        // Xóa token và trạng thái đăng nhập
        sharedPreferences.edit().remove("token").apply();

        // Cập nhật trạng thái đăng xuất
        viewPagerAdapter.setLoggedIn(false);

        // Nếu đang ở trang profile, làm mới nó ngay lập tức
        if (viewPager.getCurrentItem() == 4) {
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(4, false);  // quay lại trang login (AccountFragment)
        }
    }

    // Phương thức này có thể được gọi từ các Fragment khác để kiểm tra trạng thái đăng nhập
    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    // Phương thức để hiển thị SearchFragment
    public void showSearchFragment() {
        // Sử dụng Intent để mở SearchActivity
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }


    // Phương thức để ẩn SearchFragment
    public void hideSearchFragment() {
        View overlayContainer = findViewById(R.id.overlay_container);
        if (overlayContainer != null) {
            overlayContainer.setVisibility(View.GONE);
            getSupportFragmentManager().popBackStack();
        }
    }
}