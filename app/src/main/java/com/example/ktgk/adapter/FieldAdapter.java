package com.example.ktgk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktgk.R;
import com.example.ktgk.model.Field;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.FieldViewHolder> {

    public interface OnFieldClickListener {
        void onItemClick(Field field);
        void onDeleteClick(Field field);
    }

    private final Context context;
    private List<Field> fieldList;
    private final OnFieldClickListener listener;
    private final DecimalFormat formatter = new DecimalFormat("#,###");

    public FieldAdapter(Context context, List<Field> fieldList, OnFieldClickListener listener) {
        this.context = context;
        this.fieldList = fieldList != null ? fieldList : new ArrayList<>();
        this.listener = listener;
    }

    public void setFields(List<Field> fields) {
        this.fieldList = fields != null ? fields : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_field, parent, false);
        return new FieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        Field field = fieldList.get(position);

        holder.tvCode.setText(field.getCode());
        holder.tvName.setText(field.getName());
        holder.tvAddress.setText(field.getAddress());
        holder.tvPrice.setText(formatter.format(field.getPricePerHour()) + " VNĐ / giờ");

        if (field.isBooked()) {
            holder.tvStatus.setText("Đã được đặt");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_booked);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_booked));
        } else {
            holder.tvStatus.setText("Còn trống");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_available);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_available));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(field);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(field);
        });
    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    static class FieldViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvName, tvAddress, tvPrice, tvStatus;
        ImageButton btnDelete;

        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvFieldCode);
            tvName = itemView.findViewById(R.id.tvFieldName);
            tvAddress = itemView.findViewById(R.id.tvFieldAddress);
            tvPrice = itemView.findViewById(R.id.tvFieldPrice);
            tvStatus = itemView.findViewById(R.id.tvFieldStatus);
            btnDelete = itemView.findViewById(R.id.btnDeleteField);
        }
    }
}
