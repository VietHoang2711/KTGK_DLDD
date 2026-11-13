package com.example.ktgk.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktgk.R;
import com.example.ktgk.adapter.BookingAdapter;
import com.example.ktgk.db.DatabaseHelper;
import com.example.ktgk.model.Booking;
import com.example.ktgk.model.Field;
import com.google.android.material.chip.ChipGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingListFragment extends Fragment implements BookingAdapter.OnBookingClickListener {

    private RecyclerView rvBookings;
    private BookingAdapter bookingAdapter;
    private DatabaseHelper dbHelper;
    private List<Booking> allBookings = new ArrayList<>();
    private List<Booking> filteredBookings = new ArrayList<>();
    private LinearLayout layoutEmpty;
    private EditText etSearch;
    private ChipGroup chipGroupStatus;

    private static final int FILTER_ALL = 0;
    private static final int FILTER_ACTIVE = 1;
    private static final int FILTER_CANCELLED = 2;
    private int currentStatusFilter = FILTER_ALL;

    private final DecimalFormat formatter = new DecimalFormat("#,###");

    public BookingListFragment() {
        // Hàm khởi tạo rỗng bắt buộc
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        rvBookings = view.findViewById(R.id.rvBookings);
        layoutEmpty = view.findViewById(R.id.layoutEmptyBookings);
        etSearch = view.findViewById(R.id.etSearchBooking);
        chipGroupStatus = view.findViewById(R.id.chipGroupStatus);

        rvBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookingAdapter = new BookingAdapter(requireContext(), filteredBookings, this);
        rvBookings.setAdapter(bookingAdapter);

        chipGroupStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipActive) {
                currentStatusFilter = FILTER_ACTIVE;
            } else if (checkedId == R.id.chipCancelled) {
                currentStatusFilter = FILTER_CANCELLED;
            } else {
                currentStatusFilter = FILTER_ALL;
            }
            filterBookings();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBookings();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadBookings();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookings();
    }

    public void loadBookings() {
        if (dbHelper == null) return;
        allBookings = dbHelper.getAllBookings();
        filterBookings();
    }

    private void filterBookings() {
        filteredBookings.clear();
        String query = etSearch != null ? etSearch.getText().toString().toLowerCase().trim() : "";

        for (Booking b : allBookings) {
            // Lọc theo trạng thái
            if (currentStatusFilter == FILTER_ACTIVE && !b.isActive()) continue;
            if (currentStatusFilter == FILTER_CANCELLED && b.isActive()) continue;

            // Lọc theo từ khóa tìm kiếm
            if (!query.isEmpty()) {
                boolean matchName = b.getCustomerName() != null && b.getCustomerName().toLowerCase().contains(query);
                boolean matchPhone = b.getCustomerPhone() != null && b.getCustomerPhone().toLowerCase().contains(query);
                boolean matchField = b.getFieldName() != null && b.getFieldName().toLowerCase().contains(query);
                boolean matchCode = b.getFieldCode() != null && b.getFieldCode().toLowerCase().contains(query);

                if (!matchName && !matchPhone && !matchField && !matchCode) {
                    continue;
                }
            }

            filteredBookings.add(b);
        }

        bookingAdapter.setBookings(filteredBookings);

        if (filteredBookings.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvBookings.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvBookings.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(Booking booking) {
        showBookingDetailDialog(booking);
    }

    @Override
    public void onCancelClick(Booking booking) {
        confirmCancelBooking(booking);
    }

    private void showBookingDetailDialog(Booking booking) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_booking_detail, null);

        TextView tvStatus = dialogView.findViewById(R.id.tvDetailStatus);
        TextView tvCustomerName = dialogView.findViewById(R.id.tvDetailCustomerName);
        TextView tvCustomerPhone = dialogView.findViewById(R.id.tvDetailCustomerPhone);
        TextView tvFieldName = dialogView.findViewById(R.id.tvDetailFieldName);
        TextView tvFieldAddress = dialogView.findViewById(R.id.tvDetailFieldAddress);
        TextView tvDate = dialogView.findViewById(R.id.tvDetailDate);
        TextView tvTime = dialogView.findViewById(R.id.tvDetailTime);
        TextView tvTotalPrice = dialogView.findViewById(R.id.tvDetailTotalPrice);

        tvCustomerName.setText(booking.getCustomerName());
        tvCustomerPhone.setText("SĐT: " + booking.getCustomerPhone());

        String fDisplay = booking.getFieldCode() != null ? "[" + booking.getFieldCode() + "] " + booking.getFieldName() : booking.getFieldName();
        tvFieldName.setText(fDisplay);

        Field field = dbHelper.getFieldById(booking.getFieldId());
        if (field != null) {
            tvFieldAddress.setText("Địa chỉ: " + field.getAddress());
        } else {
            tvFieldAddress.setText("Địa chỉ: N/A");
        }

        tvDate.setText("Ngày thuê: " + booking.getBookingDate());
        tvTime.setText("Khung giờ: " + booking.getTimeRangeText());
        tvTotalPrice.setText("Tổng thanh toán: " + formatter.format(booking.getTotalPrice()) + " VNĐ");

        if (booking.isActive()) {
            tvStatus.setText("Đã đặt");
            tvStatus.setBackgroundResource(R.drawable.bg_status_available);
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_available));
        } else {
            tvStatus.setText("Đã hủy");
            tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_cancelled));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Đóng", null);

        if (booking.isActive()) {
            builder.setNegativeButton("Hủy đơn đặt sân", (dialog, which) -> confirmCancelBooking(booking));
        }

        builder.show();
    }

    private void confirmCancelBooking(Booking booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hủy đơn đặt sân")
                .setMessage("Bạn có chắc chắn muốn hủy đơn đặt sân này của " + booking.getCustomerName() + "?")
                .setPositiveButton("Xác nhận hủy", (dialog, which) -> {
                    int updated = dbHelper.updateBookingStatus(booking.getId(), Booking.STATUS_CANCELLED);
                    if (updated > 0) {
                        Toast.makeText(requireContext(), "Đã hủy đơn đặt sân thành công!", Toast.LENGTH_SHORT).show();
                        loadBookings();
                    } else {
                        Toast.makeText(requireContext(), "Không thể hủy đơn đặt sân!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Quay lại", null)
                .show();
    }
}
