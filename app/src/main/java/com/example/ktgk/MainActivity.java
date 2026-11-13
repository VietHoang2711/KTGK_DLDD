package com.example.ktgk;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ktgk.fragment.AddBookingFragment;
import com.example.ktgk.fragment.BookingListFragment;
import com.example.ktgk.fragment.FieldListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;

    private FieldListFragment fieldListFragment;
    private AddBookingFragment addBookingFragment;
    private BookingListFragment bookingListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        fieldListFragment = new FieldListFragment();
        addBookingFragment = new AddBookingFragment();
        bookingListFragment = new BookingListFragment();

        addBookingFragment.setOnBookingSavedListener(() -> {
            // Chuyển sang Tab Danh sách đơn đặt và tải lại dữ liệu của cả 2 danh sách
            if (viewPager != null) {
                viewPager.setCurrentItem(2, true);
            }
            if (bookingListFragment != null) {
                bookingListFragment.loadBookings();
            }
            if (fieldListFragment != null) {
                fieldListFragment.loadFields();
            }
        });

        viewPager.setAdapter(new MainPagerAdapter(this));
        viewPager.setOffscreenPageLimit(2);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigation.setSelectedItemId(R.id.nav_fields);
                        if (fieldListFragment != null) fieldListFragment.loadFields();
                        break;
                    case 1:
                        bottomNavigation.setSelectedItemId(R.id.nav_add_booking);
                        if (addBookingFragment != null) addBookingFragment.loadFields();
                        break;
                    case 2:
                        bottomNavigation.setSelectedItemId(R.id.nav_bookings);
                        if (bookingListFragment != null) bookingListFragment.loadBookings();
                        break;
                }
            }
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_fields) {
                viewPager.setCurrentItem(0, true);
                return true;
            } else if (itemId == R.id.nav_add_booking) {
                viewPager.setCurrentItem(1, true);
                return true;
            } else if (itemId == R.id.nav_bookings) {
                viewPager.setCurrentItem(2, true);
                return true;
            }
            return false;
        });
    }

    private class MainPagerAdapter extends FragmentStateAdapter {

        public MainPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1:
                    return addBookingFragment;
                case 2:
                    return bookingListFragment;
                case 0:
                default:
                    return fieldListFragment;
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
