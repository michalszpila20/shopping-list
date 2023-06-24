package com.android.sharedshoppinglist.database
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(tableQueryList)
        db?.execSQL(tableQueryProduct)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME1")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME2")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun deleteProduct(productID: String){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME2 WHERE $COLUMN_ID_PRODUCT= '$productID'")
        db.close()
    }

    fun deleteList(listID: String){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME1 WHERE $COLUMN_ID_LIST= '$listID'")
        db.close()
    }

    private val tableQueryList =
        "CREATE TABLE $TABLE_NAME1 (" +
                "$COLUMN_ID_LIST INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_LIST TEXT," +
                "$COLUMN_DATE TEXT," +
                "$COLUMN_TIME TEXT," +
                "$COLUMN_REMINDER TEXT," +
                "$COLUMN_CODE INTEGER)"

    private val tableQueryProduct =
        "CREATE TABLE $TABLE_NAME2 (" +
                "$COLUMN_ID_PRODUCT INTEGER PRIMARY KEY," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_STORE TEXT," +
                "$COLUMN_QUANTITY TEXT," +
                "$COLUMN_CATEGORY TEXT," +
                "$COLUMN_ID_LIST_REF INTEGER," +
                "FOREIGN KEY ($COLUMN_ID_LIST_REF) REFERENCES $TABLE_NAME1 ($COLUMN_ID_LIST))"


    fun insertList(listName: String, date: String, time: String, reminder: String, code : Int)
    {
        val valuesList = ContentValues()
        valuesList.put(COLUMN_NAME_LIST, listName)
        valuesList.put(COLUMN_DATE, date)
        valuesList.put(COLUMN_TIME, time)
        valuesList.put(COLUMN_REMINDER, reminder)
        valuesList.put(COLUMN_CODE, code)

        val db = this.writableDatabase
        db.insert(TABLE_NAME1, null, valuesList)
        db.close()
    }

    fun insertProduct(productName: String, store: String, quantity: String, category: String, idList: String)
    {
        val db = this.writableDatabase

        val valuesProduct = ContentValues()
        valuesProduct.put(COLUMN_NAME, productName)
        valuesProduct.put(COLUMN_STORE, store)
        valuesProduct.put(COLUMN_QUANTITY, quantity)
        valuesProduct.put(COLUMN_CATEGORY, category)
        valuesProduct.put(COLUMN_ID_LIST_REF, idList)

        db.insert(TABLE_NAME2, null, valuesProduct)

        db.close()
    }

    fun getList(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME1", null)
    }

    fun getProduct(productId: String): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME2 WHERE $COLUMN_ID_PRODUCT = '$productId'", null)
    }

    fun getProductsFromList(listID : String): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME2 WHERE $COLUMN_ID_LIST_REF = '$listID'", null)
    }

    fun getListRow(listID: String): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME1 WHERE $COLUMN_ID_LIST= '$listID'", null)
    }


    fun deleteAllProductsFromList(listID: String){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME2 WHERE $COLUMN_ID_LIST_REF= '$listID'")
        db.close()
    }

    companion object{
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "ShoppingList.db"

        const val TABLE_NAME1 = "lists"
        const val COLUMN_ID_LIST = "list_id"
        const val COLUMN_NAME_LIST = "list_name"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_REMINDER = "reminder"
        const val COLUMN_CODE = "code"

        const val TABLE_NAME2 = "products"
        const val COLUMN_ID_PRODUCT = "product_id"
        const val COLUMN_NAME = "product_name"
        const val COLUMN_STORE = "store"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_ID_LIST_REF = "list_id_ref"

    }
}