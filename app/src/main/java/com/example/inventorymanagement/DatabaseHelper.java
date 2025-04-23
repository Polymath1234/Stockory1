package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "InventoryDB";
    private static final int DATABASE_VERSION = 3;

    // Tables
    public static final String TABLE_MERCHANTS = "merchants";
    public static final String TABLE_VENDORS = "vendors";
    public static final String TABLE_MERCHANT_ITEMS = "merchant_items";
    public static final String TABLE_VENDOR_ITEMS = "vendor_items";
    public static final String TABLE_ITEM_MODIFICATIONS = "item_modifications";
    public static final String TABLE_SALES_HISTORY = "sales_history";

    // Common columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";

    // Item columns
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_TAG = "tag";
    public static final String COLUMN_OWNER_ID = "owner_id";
    public static final String COLUMN_MODIFIED_NAME = "modified_name";
    public static final String COLUMN_MODIFIED_PRICE = "modified_price";
    public static final String COLUMN_ITEM_ID = "item_id";
    
    // Sales history columns
    public static final String COLUMN_SALE_DATE = "sale_date";
    public static final String COLUMN_QUANTITY_SOLD = "quantity_sold";
    public static final String COLUMN_TOTAL_PRICE = "total_price";
    public static final String COLUMN_VENDOR_ID = "vendor_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create merchants table
        String CREATE_MERCHANTS_TABLE = "CREATE TABLE " + TABLE_MERCHANTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PHONE + " TEXT UNIQUE" + ")";
        db.execSQL(CREATE_MERCHANTS_TABLE);

        // Create vendors table
        String CREATE_VENDORS_TABLE = "CREATE TABLE " + TABLE_VENDORS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PHONE + " TEXT UNIQUE" + ")";
        db.execSQL(CREATE_VENDORS_TABLE);

        // Create merchant items table
        String CREATE_MERCHANT_ITEMS_TABLE = "CREATE TABLE " + TABLE_MERCHANT_ITEMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ITEM_NAME + " TEXT,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_QUANTITY + " INTEGER,"
                + COLUMN_TAG + " TEXT,"
                + COLUMN_OWNER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_OWNER_ID + ") REFERENCES " + TABLE_MERCHANTS + "(" + COLUMN_ID + ")" + ")";
        db.execSQL(CREATE_MERCHANT_ITEMS_TABLE);

        // Create vendor items table
        String CREATE_VENDOR_ITEMS_TABLE = "CREATE TABLE " + TABLE_VENDOR_ITEMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ITEM_NAME + " TEXT,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_QUANTITY + " INTEGER,"
                + COLUMN_TAG + " TEXT,"
                + COLUMN_OWNER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_OWNER_ID + ") REFERENCES " + TABLE_VENDORS + "(" + COLUMN_ID + ")" + ")";
        db.execSQL(CREATE_VENDOR_ITEMS_TABLE);

        // Create item modifications table
        String CREATE_ITEM_MODIFICATIONS_TABLE = "CREATE TABLE " + TABLE_ITEM_MODIFICATIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ITEM_ID + " INTEGER,"
                + COLUMN_MODIFIED_NAME + " TEXT,"
                + COLUMN_MODIFIED_PRICE + " REAL,"
                + "FOREIGN KEY(" + COLUMN_ITEM_ID + ") REFERENCES " + TABLE_VENDOR_ITEMS + "(" + COLUMN_ID + ")" + ")";
        db.execSQL(CREATE_ITEM_MODIFICATIONS_TABLE);
        
        // Create sales history table
        String CREATE_SALES_HISTORY_TABLE = "CREATE TABLE " + TABLE_SALES_HISTORY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ITEM_ID + " INTEGER,"
                + COLUMN_QUANTITY_SOLD + " INTEGER,"
                + COLUMN_TOTAL_PRICE + " REAL,"
                + COLUMN_SALE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_VENDOR_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_ITEM_ID + ") REFERENCES " + TABLE_VENDOR_ITEMS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_VENDOR_ID + ") REFERENCES " + TABLE_VENDORS + "(" + COLUMN_ID + ")" + ")";
        db.execSQL(CREATE_SALES_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MERCHANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENDORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MERCHANT_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENDOR_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_MODIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES_HISTORY);
        
        // Create tables again
        onCreate(db);
    }

    // Merchant operations
    public boolean addMerchant(String username, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);

        long result = db.insert(TABLE_MERCHANTS, null, values);
        return result != -1;
    }

    public boolean checkMerchantCredentials(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MERCHANTS, new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Vendor operations
    public boolean addVendor(String username, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);

        long result = db.insert(TABLE_VENDORS, null, values);
        return result != -1;
    }

    public boolean checkVendorCredentials(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VENDORS, new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Item operations
    public boolean addMerchantItem(String itemName, double price, int quantity, String tag, int ownerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_TAG, tag);
        values.put(COLUMN_OWNER_ID, ownerId);

        long result = db.insert(TABLE_MERCHANT_ITEMS, null, values);
        return result != -1;
    }

    public boolean addVendorItem(String itemName, double price, int quantity, String tag, int ownerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_TAG, tag);
        values.put(COLUMN_OWNER_ID, ownerId);

        long result = db.insert(TABLE_VENDOR_ITEMS, null, values);
        return result != -1;
    }

    public Cursor getMerchantItems(int ownerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
            COLUMN_ID + " AS _id",
            COLUMN_ITEM_NAME,
            COLUMN_PRICE,
            COLUMN_QUANTITY,
            COLUMN_TAG
        };
        Cursor cursor = db.query(TABLE_MERCHANT_ITEMS, columns,
                COLUMN_OWNER_ID + "=?", new String[]{String.valueOf(ownerId)},
                null, null, null);
        
        android.util.Log.d("DatabaseHelper", "Found " + cursor.getCount() + " items for merchant ID: " + ownerId);
        return cursor;
    }

    public Cursor getVendorItems(int ownerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
            "v." + COLUMN_ID + " AS _id",
            "v." + COLUMN_ITEM_NAME,
            "v." + COLUMN_PRICE,
            "v." + COLUMN_QUANTITY,
            "m." + COLUMN_USERNAME + " AS merchant_name"
        };
        
        String tables = TABLE_VENDOR_ITEMS + " v LEFT JOIN " + TABLE_MERCHANTS + " m ON v." + COLUMN_OWNER_ID + " = m." + COLUMN_ID;
        String selection = "v." + COLUMN_OWNER_ID + "=?";
        String[] selectionArgs = {String.valueOf(ownerId)};
        
        return db.query(tables, columns, selection, selectionArgs, null, null, null);
    }

    public boolean updateItemQuantity(String tableName, int itemId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, newQuantity);
        int result = db.update(tableName, values, COLUMN_ID + "=?", new String[]{String.valueOf(itemId)});
        return result > 0;
    }

    public boolean updateItemDetails(String tableName, int itemId, String newName, double newPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, newName);
        values.put(COLUMN_PRICE, newPrice);
        int result = db.update(tableName, values, COLUMN_ID + "=?", new String[]{String.valueOf(itemId)});
        return result > 0;
    }

    public int getMerchantId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MERCHANTS, new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
            return id;
        }
        if (cursor != null) {
            cursor.close();
        }
        return -1;
    }

    public Cursor searchMerchants(String searchQuery) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USERNAME + " LIKE ? OR " +
                         COLUMN_EMAIL + " LIKE ? OR " +
                         COLUMN_PHONE + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + searchQuery + "%",
                                            "%" + searchQuery + "%",
                                            "%" + searchQuery + "%"};
        String[] columns = {COLUMN_ID + " AS _id", COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_PHONE};
        return db.query(TABLE_MERCHANTS, columns, selection, selectionArgs, null, null, null);
    }

    public int getVendorId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VENDORS, new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
            return id;
        }
        if (cursor != null) {
            cursor.close();
        }
        return -1;
    }

    public Cursor searchVendorItemsByName(int vendorId, String searchQuery) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "v." + COLUMN_OWNER_ID + "=? AND v." + COLUMN_ITEM_NAME + " LIKE ?";
        String[] selectionArgs = {String.valueOf(vendorId), "%" + searchQuery + "%"};
        
        String[] columns = {
            "v." + COLUMN_ID + " AS _id",
            "v." + COLUMN_ITEM_NAME,
            "v." + COLUMN_PRICE,
            "v." + COLUMN_QUANTITY
        };
        
        return db.query(TABLE_VENDOR_ITEMS + " v", columns, selection, selectionArgs, null, null, null);
    }

    public boolean updateItemModification(int itemId, String newName, double newPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_ID, itemId);
        values.put(COLUMN_MODIFIED_NAME, newName);
        values.put(COLUMN_MODIFIED_PRICE, newPrice);

        // Check if modification already exists
        Cursor cursor = db.query(TABLE_ITEM_MODIFICATIONS, new String[]{COLUMN_ID},
                COLUMN_ITEM_ID + "=?", new String[]{String.valueOf(itemId)}, null, null, null);
        
        boolean exists = cursor.getCount() > 0;
        cursor.close();

        if (exists) {
            // Update existing modification
            return db.update(TABLE_ITEM_MODIFICATIONS, values, COLUMN_ITEM_ID + "=?", 
                    new String[]{String.valueOf(itemId)}) > 0;
        } else {
            // Insert new modification
            return db.insert(TABLE_ITEM_MODIFICATIONS, null, values) != -1;
        }
    }

    public Cursor getModifiedItemsForSales(int vendorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT v." + COLUMN_ID + " AS _id, " +
                      "COALESCE(m." + COLUMN_MODIFIED_NAME + ", v." + COLUMN_ITEM_NAME + ") AS item_name, " +
                      "COALESCE(m." + COLUMN_MODIFIED_PRICE + ", v." + COLUMN_PRICE + ") AS price, " +
                      "v." + COLUMN_QUANTITY + " AS " + COLUMN_QUANTITY + ", " +
                      "mer." + COLUMN_USERNAME + " AS merchant_name " +
                      "FROM " + TABLE_VENDOR_ITEMS + " v " +
                      "LEFT JOIN " + TABLE_ITEM_MODIFICATIONS + " m ON v." + COLUMN_ID + " = m." + COLUMN_ITEM_ID + " " +
                      "LEFT JOIN " + TABLE_MERCHANTS + " mer ON v." + COLUMN_OWNER_ID + " = mer." + COLUMN_ID + " " +
                      "WHERE v." + COLUMN_OWNER_ID + " = ? " +
                      "ORDER BY v." + COLUMN_ID;
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(vendorId)});
        
        // Log the cursor data for debugging
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("_id");
            int nameIndex = cursor.getColumnIndex("item_name");
            int priceIndex = cursor.getColumnIndex("price");
            int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
            
            do {
                if (idIndex >= 0 && nameIndex >= 0 && priceIndex >= 0 && quantityIndex >= 0) {
                    android.util.Log.d("DatabaseHelper", "Item - ID: " + cursor.getInt(idIndex) +
                        ", Name: " + cursor.getString(nameIndex) +
                        ", Price: " + cursor.getDouble(priceIndex) +
                        ", Quantity: " + cursor.getInt(quantityIndex));
                } else {
                    android.util.Log.e("DatabaseHelper", "Invalid column indices - id: " + idIndex + 
                        ", name: " + nameIndex + 
                        ", price: " + priceIndex + 
                        ", quantity: " + quantityIndex);
                }
            } while (cursor.moveToNext());
            cursor.moveToFirst(); // Reset cursor position
        } else {
            android.util.Log.e("DatabaseHelper", "No items found for vendor ID: " + vendorId);
        }
        
        return cursor;
    }

    public boolean recordSale(int itemId, int quantitySold, double totalPrice, int vendorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_ID, itemId);
        values.put(COLUMN_QUANTITY_SOLD, quantitySold);
        values.put(COLUMN_TOTAL_PRICE, totalPrice);
        values.put(COLUMN_VENDOR_ID, vendorId);
        values.put(COLUMN_SALE_DATE, System.currentTimeMillis());
        
        long result = db.insert(TABLE_SALES_HISTORY, null, values);
        return result != -1;
    }
    
    public Cursor getSalesHistory(int vendorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s." + COLUMN_ID + " AS _id, " +
                      "s." + COLUMN_QUANTITY_SOLD + ", " +
                      "s." + COLUMN_TOTAL_PRICE + ", " +
                      "datetime(s." + COLUMN_SALE_DATE + "/1000, 'unixepoch') AS sale_date, " +
                      "COALESCE(m." + COLUMN_MODIFIED_NAME + ", v." + COLUMN_ITEM_NAME + ") AS item_name, " +
                      "mer." + COLUMN_USERNAME + " AS merchant_name " +
                      "FROM " + TABLE_SALES_HISTORY + " s " +
                      "JOIN " + TABLE_VENDOR_ITEMS + " v ON s." + COLUMN_ITEM_ID + " = v." + COLUMN_ID + " " +
                      "LEFT JOIN " + TABLE_ITEM_MODIFICATIONS + " m ON v." + COLUMN_ID + " = m." + COLUMN_ITEM_ID + " " +
                      "LEFT JOIN " + TABLE_MERCHANTS + " mer ON v." + COLUMN_OWNER_ID + " = mer." + COLUMN_ID + " " +
                      "WHERE s." + COLUMN_VENDOR_ID + " = ? " +
                      "ORDER BY s." + COLUMN_SALE_DATE + " DESC";
        
        return db.rawQuery(query, new String[]{String.valueOf(vendorId)});
    }
    
    public double getTotalRevenue(int vendorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_TOTAL_PRICE + ") FROM " + TABLE_SALES_HISTORY + 
                      " WHERE " + COLUMN_VENDOR_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(vendorId)});
        double totalRevenue = 0.0;
        
        if (cursor != null && cursor.moveToFirst()) {
            totalRevenue = cursor.getDouble(0);
            cursor.close();
        }
        
        return totalRevenue;
    }
} 