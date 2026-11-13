package com.example.ktgk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ktgk.model.Booking;
import com.example.ktgk.model.Field;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sanbong_manager.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng sân bóng
    public static final String TABLE_FIELDS = "fields";
    public static final String COLUMN_FIELD_ID = "id";
    public static final String COLUMN_FIELD_CODE = "code";
    public static final String COLUMN_FIELD_NAME = "name";
    public static final String COLUMN_FIELD_ADDRESS = "address";
    public static final String COLUMN_FIELD_PRICE = "price_per_hour";

    // Bảng đơn đặt sân
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COLUMN_BOOKING_ID = "id";
    public static final String COLUMN_BOOKING_FIELD_ID = "field_id";
    public static final String COLUMN_BOOKING_CUSTOMER_NAME = "customer_name";
    public static final String COLUMN_BOOKING_CUSTOMER_PHONE = "customer_phone";
    public static final String COLUMN_BOOKING_DATE = "booking_date";
    public static final String COLUMN_BOOKING_START_HOUR = "start_hour";
    public static final String COLUMN_BOOKING_START_MINUTE = "start_minute";
    public static final String COLUMN_BOOKING_DURATION = "duration_hours";
    public static final String COLUMN_BOOKING_TOTAL_PRICE = "total_price";
    public static final String COLUMN_BOOKING_STATUS = "status";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createFieldsTable = "CREATE TABLE " + TABLE_FIELDS + " (" +
                COLUMN_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FIELD_CODE + " TEXT UNIQUE NOT NULL, " +
                COLUMN_FIELD_NAME + " TEXT NOT NULL, " +
                COLUMN_FIELD_ADDRESS + " TEXT NOT NULL, " +
                COLUMN_FIELD_PRICE + " REAL NOT NULL" +
                ");";

        String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS + " (" +
                COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BOOKING_FIELD_ID + " INTEGER NOT NULL, " +
                COLUMN_BOOKING_CUSTOMER_NAME + " TEXT NOT NULL, " +
                COLUMN_BOOKING_CUSTOMER_PHONE + " TEXT NOT NULL, " +
                COLUMN_BOOKING_DATE + " TEXT NOT NULL, " +
                COLUMN_BOOKING_START_HOUR + " INTEGER NOT NULL, " +
                COLUMN_BOOKING_START_MINUTE + " INTEGER NOT NULL, " +
                COLUMN_BOOKING_DURATION + " INTEGER NOT NULL, " +
                COLUMN_BOOKING_TOTAL_PRICE + " REAL NOT NULL, " +
                COLUMN_BOOKING_STATUS + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_BOOKING_FIELD_ID + ") REFERENCES " + TABLE_FIELDS + "(" + COLUMN_FIELD_ID + ") ON DELETE CASCADE" +
                ");";

        db.execSQL(createFieldsTable);
        db.execSQL(createBookingsTable);

        // Khởi tạo dữ liệu mẫu ban đầu
        seedInitialData(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Dữ liệu mẫu cho sân bóng
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FIELD_CODE, "SB01");
        cv.put(COLUMN_FIELD_NAME, "Sân bóng Chuyên Việt 1");
        cv.put(COLUMN_FIELD_ADDRESS, "98 Hoàng Ngân, Cầu Giấy, Hà Nội");
        cv.put(COLUMN_FIELD_PRICE, 200000);
        long id1 = db.insert(TABLE_FIELDS, null, cv);

        cv.clear();
        cv.put(COLUMN_FIELD_CODE, "SB02");
        cv.put(COLUMN_FIELD_NAME, "Sân bóng Chuyên Việt 2");
        cv.put(COLUMN_FIELD_ADDRESS, "98 Hoàng Ngân, Cầu Giấy, Hà Nội");
        cv.put(COLUMN_FIELD_PRICE, 250000);
        long id2 = db.insert(TABLE_FIELDS, null, cv);

        cv.clear();
        cv.put(COLUMN_FIELD_CODE, "SB03");
        cv.put(COLUMN_FIELD_NAME, "Sân bóng Mỹ Đình 7 người");
        cv.put(COLUMN_FIELD_ADDRESS, "15 Lê Đức Thọ, Nam Từ Liêm, Hà Nội");
        cv.put(COLUMN_FIELD_PRICE, 300000);
        long id3 = db.insert(TABLE_FIELDS, null, cv);

        cv.clear();
        cv.put(COLUMN_FIELD_CODE, "SB04");
        cv.put(COLUMN_FIELD_NAME, "Sân bóng Thăng Long");
        cv.put(COLUMN_FIELD_ADDRESS, "234 Phạm Văn Đồng, Bắc Từ Liêm, Hà Nội");
        cv.put(COLUMN_FIELD_PRICE, 180000);
        db.insert(TABLE_FIELDS, null, cv);

        // Ngày hôm nay (định dạng YYYY-MM-DD)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        // Dữ liệu mẫu cho đơn đặt sân
        if (id1 != -1) {
            cv.clear();
            cv.put(COLUMN_BOOKING_FIELD_ID, id1);
            cv.put(COLUMN_BOOKING_CUSTOMER_NAME, "Nguyễn Văn An");
            cv.put(COLUMN_BOOKING_CUSTOMER_PHONE, "0912345678");
            cv.put(COLUMN_BOOKING_DATE, today);
            cv.put(COLUMN_BOOKING_START_HOUR, 17);
            cv.put(COLUMN_BOOKING_START_MINUTE, 30);
            cv.put(COLUMN_BOOKING_DURATION, 2);
            cv.put(COLUMN_BOOKING_TOTAL_PRICE, 400000);
            cv.put(COLUMN_BOOKING_STATUS, Booking.STATUS_ACTIVE);
            db.insert(TABLE_BOOKINGS, null, cv);
        }

        if (id2 != -1) {
            cv.clear();
            cv.put(COLUMN_BOOKING_FIELD_ID, id2);
            cv.put(COLUMN_BOOKING_CUSTOMER_NAME, "Trần Thị Bình");
            cv.put(COLUMN_BOOKING_CUSTOMER_PHONE, "0987654321");
            cv.put(COLUMN_BOOKING_DATE, today);
            cv.put(COLUMN_BOOKING_START_HOUR, 19);
            cv.put(COLUMN_BOOKING_START_MINUTE, 0);
            cv.put(COLUMN_BOOKING_DURATION, 1);
            cv.put(COLUMN_BOOKING_TOTAL_PRICE, 250000);
            cv.put(COLUMN_BOOKING_STATUS, Booking.STATUS_ACTIVE);
            db.insert(TABLE_BOOKINGS, null, cv);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIELDS);
        onCreate(db);
    }

    // ================= THAO TÁC SÂN BÓNG =================

    public long insertField(Field field) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FIELD_CODE, field.getCode().trim().toUpperCase());
        cv.put(COLUMN_FIELD_NAME, field.getName().trim());
        cv.put(COLUMN_FIELD_ADDRESS, field.getAddress().trim());
        cv.put(COLUMN_FIELD_PRICE, field.getPricePerHour());
        return db.insert(TABLE_FIELDS, null, cv);
    }

    public int updateField(Field field) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FIELD_CODE, field.getCode().trim().toUpperCase());
        cv.put(COLUMN_FIELD_NAME, field.getName().trim());
        cv.put(COLUMN_FIELD_ADDRESS, field.getAddress().trim());
        cv.put(COLUMN_FIELD_PRICE, field.getPricePerHour());
        return db.update(TABLE_FIELDS, cv, COLUMN_FIELD_ID + " = ?", new String[]{String.valueOf(field.getId())});
    }

    public int deleteField(int fieldId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_FIELDS, COLUMN_FIELD_ID + " = ?", new String[]{String.valueOf(fieldId)});
    }

    public boolean isFieldCodeExists(String code, int excludeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FIELDS, new String[]{COLUMN_FIELD_ID},
                COLUMN_FIELD_CODE + " = ? AND " + COLUMN_FIELD_ID + " != ?",
                new String[]{code.trim().toUpperCase(), String.valueOf(excludeId)},
                null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public List<Field> getAllFields() {
        List<Field> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT f.*, " +
                "(SELECT COUNT(*) FROM " + TABLE_BOOKINGS + " b WHERE b." + COLUMN_BOOKING_FIELD_ID + " = f." + COLUMN_FIELD_ID +
                " AND b." + COLUMN_BOOKING_STATUS + " = '" + Booking.STATUS_ACTIVE + "') AS active_count " +
                "FROM " + TABLE_FIELDS + " f ORDER BY f." + COLUMN_FIELD_CODE + " ASC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            int idIdx = cursor.getColumnIndexOrThrow(COLUMN_FIELD_ID);
            int codeIdx = cursor.getColumnIndexOrThrow(COLUMN_FIELD_CODE);
            int nameIdx = cursor.getColumnIndexOrThrow(COLUMN_FIELD_NAME);
            int addrIdx = cursor.getColumnIndexOrThrow(COLUMN_FIELD_ADDRESS);
            int priceIdx = cursor.getColumnIndexOrThrow(COLUMN_FIELD_PRICE);
            int activeCountIdx = cursor.getColumnIndexOrThrow("active_count");

            do {
                Field f = new Field(
                        cursor.getInt(idIdx),
                        cursor.getString(codeIdx),
                        cursor.getString(nameIdx),
                        cursor.getString(addrIdx),
                        cursor.getDouble(priceIdx)
                );
                int activeCount = cursor.getInt(activeCountIdx);
                f.setBooked(activeCount > 0);
                list.add(f);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public Field getFieldById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FIELDS, null, COLUMN_FIELD_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Field f = null;
        if (cursor != null && cursor.moveToFirst()) {
            f = new Field(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FIELD_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIELD_CODE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIELD_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIELD_ADDRESS)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FIELD_PRICE))
            );
            cursor.close();
        }
        return f;
    }

    // ================= THAO TÁC ĐƠN ĐẶT SÂN =================

    public long insertBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BOOKING_FIELD_ID, booking.getFieldId());
        cv.put(COLUMN_BOOKING_CUSTOMER_NAME, booking.getCustomerName().trim());
        cv.put(COLUMN_BOOKING_CUSTOMER_PHONE, booking.getCustomerPhone().trim());
        cv.put(COLUMN_BOOKING_DATE, booking.getBookingDate().trim());
        cv.put(COLUMN_BOOKING_START_HOUR, booking.getStartHour());
        cv.put(COLUMN_BOOKING_START_MINUTE, booking.getStartMinute());
        cv.put(COLUMN_BOOKING_DURATION, booking.getDurationHours());
        cv.put(COLUMN_BOOKING_TOTAL_PRICE, booking.getTotalPrice());
        cv.put(COLUMN_BOOKING_STATUS, booking.getStatus() != null ? booking.getStatus() : Booking.STATUS_ACTIVE);
        return db.insert(TABLE_BOOKINGS, null, cv);
    }

    public int updateBookingStatus(int bookingId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BOOKING_STATUS, status);
        return db.update(TABLE_BOOKINGS, cv, COLUMN_BOOKING_ID + " = ?", new String[]{String.valueOf(bookingId)});
    }

    public int deleteBooking(int bookingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BOOKINGS, COLUMN_BOOKING_ID + " = ?", new String[]{String.valueOf(bookingId)});
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT b.*, f." + COLUMN_FIELD_NAME + " AS field_name, f." + COLUMN_FIELD_CODE + " AS field_code " +
                "FROM " + TABLE_BOOKINGS + " b " +
                "LEFT JOIN " + TABLE_FIELDS + " f ON b." + COLUMN_BOOKING_FIELD_ID + " = f." + COLUMN_FIELD_ID + " " +
                "ORDER BY b." + COLUMN_BOOKING_DATE + " DESC, b." + COLUMN_BOOKING_START_HOUR + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            int idIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID);
            int fieldIdIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_FIELD_ID);
            int nameIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_CUSTOMER_NAME);
            int phoneIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_CUSTOMER_PHONE);
            int dateIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_DATE);
            int hourIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_START_HOUR);
            int minIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_START_MINUTE);
            int durIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_DURATION);
            int totalIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_TOTAL_PRICE);
            int statusIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_STATUS);
            int fNameIdx = cursor.getColumnIndexOrThrow("field_name");
            int fCodeIdx = cursor.getColumnIndexOrThrow("field_code");

            do {
                Booking b = new Booking(
                        cursor.getInt(idIdx),
                        cursor.getInt(fieldIdIdx),
                        cursor.getString(nameIdx),
                        cursor.getString(phoneIdx),
                        cursor.getString(dateIdx),
                        cursor.getInt(hourIdx),
                        cursor.getInt(minIdx),
                        cursor.getInt(durIdx),
                        cursor.getDouble(totalIdx),
                        cursor.getString(statusIdx)
                );
                b.setFieldName(cursor.getString(fNameIdx));
                b.setFieldCode(cursor.getString(fCodeIdx));
                list.add(b);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    /**
     * Kiểm tra xem sân bóng có bị trùng lịch đặt đã kích hoạt trong khoảng thời gian quy định hay không.
     * @return true nếu bị trùng lịch, false nếu khung giờ còn trống.
     */
    public boolean checkTimeOverlap(int fieldId, String bookingDate, int startHour, int startMinute, int durationHours, int excludeBookingId) {
        SQLiteDatabase db = this.getReadableDatabase();

        int newStart = startHour * 60 + startMinute;
        int newEnd = newStart + durationHours * 60;

        String query = "SELECT " + COLUMN_BOOKING_START_HOUR + ", " + COLUMN_BOOKING_START_MINUTE + ", " + COLUMN_BOOKING_DURATION +
                " FROM " + TABLE_BOOKINGS +
                " WHERE " + COLUMN_BOOKING_FIELD_ID + " = ? AND " +
                COLUMN_BOOKING_DATE + " = ? AND " +
                COLUMN_BOOKING_STATUS + " = ? AND " +
                COLUMN_BOOKING_ID + " != ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(fieldId),
                bookingDate,
                Booking.STATUS_ACTIVE,
                String.valueOf(excludeBookingId)
        });

        boolean hasOverlap = false;

        if (cursor != null && cursor.moveToFirst()) {
            int hourIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_START_HOUR);
            int minIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_START_MINUTE);
            int durIdx = cursor.getColumnIndexOrThrow(COLUMN_BOOKING_DURATION);

            do {
                int existHour = cursor.getInt(hourIdx);
                int existMin = cursor.getInt(minIdx);
                int existDur = cursor.getInt(durIdx);

                int existStart = existHour * 60 + existMin;
                int existEnd = existStart + existDur * 60;

                // Điều kiện trùng lịch: newStart < existEnd VÀ newEnd > existStart
                if (newStart < existEnd && newEnd > existStart) {
                    hasOverlap = true;
                    break;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        return hasOverlap;
    }
}
