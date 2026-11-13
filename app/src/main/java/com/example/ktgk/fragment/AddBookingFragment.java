package com.example.ktgk.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.ktgk.R;
import com.example.ktgk.db.DatabaseHelper;
import com.example.ktgk.model.Booking;
import com.example.ktgk.model.Field;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddBookingFragment extends Fragment {

    public interface OnBookingSavedListener {
        void onBookingSaved();
    }

    private Spinner spinnerFields;
    private EditText etCustomerName;
    private EditText etCustomerPhone;
    private Button btnSelectDate;
    private Button btnSelectTime;
    private EditText etDuration;
    private TextView tvPriceFormula;
    private TextView tvTotalCalculatedPrice;
    private Button btnSubmitBooking;

    private DatabaseHelper dbHelper;
    private List<Field> fieldList = new ArrayList<>();
    private Field selectedField = null;

    private Calendar selectedCalendar = Calendar.getInstance();
    private int selectedStartHour = 17;
    private int selectedStartMinute = 0;

    private final DecimalFormat formatter = new DecimalFormat("#,###");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private OnBookingSavedListener listener;

    public AddBookingFragment() {
        // Hàm khởi tạo rỗng bắt buộc
    }

    public void setOnBookingSavedListener(OnBookingSavedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        spinnerFields = view.findViewById(R.id.spinnerFields);
        etCustomerName = view.findViewById(R.id.etCustomerName);
        etCustomerPhone = view.findViewById(R.id.etCustomerPhone);
        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        btnSelectTime = view.findViewById(R.id.btnSelectTime);
        etDuration = view.findViewById(R.id.etDuration);
        tvPriceFormula = view.findViewById(R.id.tvPriceFormula);
        tvTotalCalculatedPrice = view.findViewById(R.id.tvTotalCalculatedPrice);
        btnSubmitBooking = view.findViewById(R.id.btnSubmitBooking);

        // Cấu hình hiển thị ngày và giờ mặc định
        updateDateButtonText();
        updateTimeButtonText();

        // Lắng nghe sự kiện chọn ngày
        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                    (view1, year, month, dayOfMonth) -> {
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateButtonText();
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        // Lắng nghe sự kiện chọn giờ
        btnSelectTime.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(requireContext(),
                    (view1, hourOfDay, minute) -> {
                        selectedStartHour = hourOfDay;
                        selectedStartMinute = minute;
                        updateTimeButtonText();
                    },
                    selectedStartHour,
                    selectedStartMinute,
                    true
            );
            dialog.show();
        });

        // Lắng nghe sự kiện thay đổi số giờ thuê
        etDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmitBooking.setOnClickListener(v -> submitBooking());

        loadFields();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFields();
    }

    public void loadFields() {
        if (dbHelper == null) return;
        fieldList = dbHelper.getAllFields();

        if (fieldList.isEmpty()) {
            List<String> emptyList = new ArrayList<>();
            emptyList.add("-- Chưa có sân bóng --");
            ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, emptyList);
            emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFields.setAdapter(emptyAdapter);
            selectedField = null;
            calculateTotal();
            return;
        }

        List<String> fieldDisplayList = new ArrayList<>();
        for (Field f : fieldList) {
            fieldDisplayList.add("[" + f.getCode() + "] " + f.getName() + " (" + formatter.format(f.getPricePerHour()) + " VNĐ/h)");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, fieldDisplayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFields.setAdapter(adapter);

        spinnerFields.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < fieldList.size()) {
                    selectedField = fieldList.get(position);
                    calculateTotal();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedField = null;
                calculateTotal();
            }
        });

        if (!fieldList.isEmpty()) {
            selectedField = fieldList.get(0);
            calculateTotal();
        }
    }

    private void updateDateButtonText() {
        btnSelectDate.setText("Ngày: " + displayDateFormat.format(selectedCalendar.getTime()));
    }

    private void updateTimeButtonText() {
        btnSelectTime.setText(String.format(Locale.getDefault(), "Giờ: %02d:%02d", selectedStartHour, selectedStartMinute));
    }

    private int getDurationHours() {
        String durStr = etDuration.getText() != null ? etDuration.getText().toString().trim() : "";
        try {
            int dur = Integer.parseInt(durStr);
            return Math.max(dur, 0);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double calculateTotal() {
        if (selectedField == null) {
            tvPriceFormula.setText("Chưa chọn sân bóng");
            tvTotalCalculatedPrice.setText("Tổng tiền: 0 VNĐ");
            return 0;
        }

        int duration = getDurationHours();
        double price = selectedField.getPricePerHour();
        double total = price * duration;

        tvPriceFormula.setText("Giá thuê: " + formatter.format(price) + " VNĐ x " + duration + " giờ");
        tvTotalCalculatedPrice.setText("Tổng tiền: " + formatter.format(total) + " VNĐ");

        return total;
    }

    private void submitBooking() {
        if (selectedField == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn sân bóng!", Toast.LENGTH_SHORT).show();
            return;
        }

        String customerName = etCustomerName.getText() != null ? etCustomerName.getText().toString().trim() : "";
        String customerPhone = etCustomerPhone.getText() != null ? etCustomerPhone.getText().toString().trim() : "";
        int duration = getDurationHours();

        if (customerName.isEmpty()) {
            etCustomerName.setError("Vui lòng nhập họ tên người đặt!");
            etCustomerName.requestFocus();
            return;
        }

        if (customerPhone.isEmpty()) {
            etCustomerPhone.setError("Vui lòng nhập số điện thoại!");
            etCustomerPhone.requestFocus();
            return;
        }

        if (!customerPhone.matches("\\d{10}")) {
            etCustomerPhone.setError("Số điện thoại phải bao gồm đúng 10 chữ số!");
            etCustomerPhone.requestFocus();
            return;
        }

        if (duration <= 0) {
            etDuration.setError("Số giờ thuê phải lớn hơn 0!");
            etDuration.requestFocus();
            return;
        }

        String bookingDate = dateFormat.format(selectedCalendar.getTime());

        // Kiểm tra xem sân bóng có bị trùng lịch đặt trong cùng khung giờ hay không
        boolean hasOverlap = dbHelper.checkTimeOverlap(
                selectedField.getId(),
                bookingDate,
                selectedStartHour,
                selectedStartMinute,
                duration,
                0
        );

        if (hasOverlap) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Không thể đặt sân")
                    .setMessage("Sân bóng [" + selectedField.getCode() + "] " + selectedField.getName() +
                            " đã có lịch đặt trùng trong khung giờ này vào ngày " + displayDateFormat.format(selectedCalendar.getTime()) +
                            ".\n\nVui lòng chọn giờ hoặc ngày khác!")
                    .setPositiveButton("Đã hiểu", null)
                    .show();
            return;
        }

        double totalPrice = selectedField.getPricePerHour() * duration;

        Booking booking = new Booking(
                0,
                selectedField.getId(),
                customerName,
                customerPhone,
                bookingDate,
                selectedStartHour,
                selectedStartMinute,
                duration,
                totalPrice,
                Booking.STATUS_ACTIVE
        );

        long id = dbHelper.insertBooking(booking);
        if (id != -1) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Đặt sân thành công!")
                    .setMessage("Đơn đặt sân của " + customerName + " tại " + selectedField.getName() +
                            " vào ngày " + displayDateFormat.format(selectedCalendar.getTime()) +
                            " lúc " + String.format(Locale.getDefault(), "%02d:%02d", selectedStartHour, selectedStartMinute) +
                            " (" + duration + " giờ) đã được lưu thành công.\n\nTổng tiền: " + formatter.format(totalPrice) + " VNĐ")
                    .setPositiveButton("OK", (dialog, which) -> {
                        clearForm();
                        if (listener != null) {
                            listener.onBookingSaved();
                        }
                    })
                    .setCancelable(false)
                    .show();
        } else {
            Toast.makeText(requireContext(), "Có lỗi xảy ra khi lưu đơn đặt sân!", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etCustomerName.setText("");
        etCustomerPhone.setText("");
        etDuration.setText("2");
        selectedCalendar = Calendar.getInstance();
        selectedStartHour = 17;
        selectedStartMinute = 0;
        updateDateButtonText();
        updateTimeButtonText();
        calculateTotal();
    }
}
