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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktgk.R;
import com.example.ktgk.adapter.FieldAdapter;
import com.example.ktgk.db.DatabaseHelper;
import com.example.ktgk.model.Field;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class FieldListFragment extends Fragment implements FieldAdapter.OnFieldClickListener {

    private RecyclerView rvFields;
    private FieldAdapter fieldAdapter;
    private DatabaseHelper dbHelper;
    private List<Field> allFields = new ArrayList<>();
    private List<Field> filteredFields = new ArrayList<>();
    private LinearLayout layoutEmpty;
    private EditText etSearch;

    public FieldListFragment() {
        // Hàm khởi tạo rỗng bắt buộc
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_field_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        rvFields = view.findViewById(R.id.rvFields);
        layoutEmpty = view.findViewById(R.id.layoutEmptyFields);
        etSearch = view.findViewById(R.id.etSearchField);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddField);

        rvFields.setLayoutManager(new LinearLayoutManager(requireContext()));
        fieldAdapter = new FieldAdapter(requireContext(), filteredFields, this);
        rvFields.setAdapter(fieldAdapter);

        fabAdd.setOnClickListener(v -> showAddFieldDialog());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFields(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadFields();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFields();
    }

    public void loadFields() {
        if (dbHelper == null) return;
        allFields = dbHelper.getAllFields();
        String currentSearch = etSearch != null ? etSearch.getText().toString() : "";
        filterFields(currentSearch);
    }

    private void filterFields(String query) {
        filteredFields.clear();
        String q = query.toLowerCase().trim();

        if (q.isEmpty()) {
            filteredFields.addAll(allFields);
        } else {
            for (Field f : allFields) {
                if (f.getName().toLowerCase().contains(q) ||
                        f.getCode().toLowerCase().contains(q) ||
                        f.getAddress().toLowerCase().contains(q)) {
                    filteredFields.add(f);
                }
            }
        }

        fieldAdapter.setFields(filteredFields);
        if (filteredFields.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvFields.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvFields.setVisibility(View.VISIBLE);
        }
    }

    private void showAddFieldDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_field, null);
        TextInputEditText etCode = dialogView.findViewById(R.id.etFieldCode);
        TextInputEditText etName = dialogView.findViewById(R.id.etFieldName);
        TextInputEditText etAddress = dialogView.findViewById(R.id.etFieldAddress);
        TextInputEditText etPrice = dialogView.findViewById(R.id.etFieldPrice);

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String code = etCode.getText() != null ? etCode.getText().toString().trim() : "";
                    String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                    String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
                    String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";

                    if (code.isEmpty() || name.isEmpty() || address.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (dbHelper.isFieldCodeExists(code, 0)) {
                        Toast.makeText(requireContext(), "Mã sân bóng '" + code + "' đã tồn tại!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price;
                    try {
                        price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Giá thuê phải là số hợp lệ!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Field newField = new Field(code, name, address, price);
                    long result = dbHelper.insertField(newField);
                    if (result != -1) {
                        Toast.makeText(requireContext(), "Thêm sân bóng thành công!", Toast.LENGTH_SHORT).show();
                        loadFields();
                    } else {
                        Toast.makeText(requireContext(), "Có lỗi xảy ra khi thêm sân bóng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .create()
                .show();
    }

    @Override
    public void onItemClick(Field field) {
        Toast.makeText(requireContext(), "Sân bóng: " + field.getName() + " - " + field.getStatusText(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(Field field) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa sân bóng")
                .setMessage("Bạn có chắc chắn muốn xóa sân [" + field.getCode() + "] " + field.getName() + "?\nLưu ý: Tất cả lịch đặt sân của sân này cũng sẽ bị xóa.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int deleted = dbHelper.deleteField(field.getId());
                    if (deleted > 0) {
                        Toast.makeText(requireContext(), "Đã xóa sân bóng thành công!", Toast.LENGTH_SHORT).show();
                        loadFields();
                    } else {
                        Toast.makeText(requireContext(), "Không thể xóa sân bóng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
