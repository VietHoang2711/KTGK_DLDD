package com.example.ktgk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktgk.R;
import com.example.ktgk.model.Booking;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    public interface OnBookingClickListener {
        void onItemClick(Booking booking);
        void onCancelClick(Booking booking);
    }

    private final Context context;
    private List<Booking> bookingList;
    private final OnBookingClickListener listener;
    private final DecimalFormat formatter = new DecimalFormat("#,###");

    public BookingAdapter(Context context, List<Booking> bookingList, OnBookingClickListener listener) {
        this.context = context;
        this.bookingList = bookingList != null ? bookingList : new ArrayList<>();
        this.listener = listener;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookingList = bookings != null ? bookings : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvCustomerName.setText(booking.getCustomerName());
        holder.tvCustomerPhone.setText(booking.getCustomerPhone());

        String fieldDisplay = booking.getFieldCode() != null ?
                "[" + booking.getFieldCode() + "] " + booking.getFieldName() :
                (booking.getFieldName() != null ? booking.getFieldName() : "Sân bóng");
        holder.tvFieldName.setText(fieldDisplay);

        holder.tvBookingDate.setText(booking.getBookingDate());
        holder.tvBookingTime.setText(booking.getTimeRangeText());
        holder.tvTotalPrice.setText("Tổng: " + formatter.format(booking.getTotalPrice()) + " VNĐ");

        if (booking.isActive()) {
            holder.tvStatus.setText("Đã đặt");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_available);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_available));
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setText("Đã hủy");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_cancelled));
            holder.btnCancel.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(booking);
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancelClick(booking);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvCustomerPhone, tvFieldName, tvBookingDate, tvBookingTime, tvTotalPrice, tvStatus;
        Button btnCancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvBookingTime = itemView.findViewById(R.id.tvBookingTime);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}
