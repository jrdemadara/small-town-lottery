package com.slicksoftcoder.smalltownlottery.server

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LocalDatabase (context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

        companion object {
            private const val DATABASE_NAME = "schema_stl"
            private const val DATABASE_VERSION = 1
            /* Tables */
            private const val TABLE_USERS = "users"
            private const val TABLE_BET_HEADERS = "bet_headers"
            private const val TABLE_BET_DETAILS = "bet_details"
            private const val TABLE_DRAWS = "draws"
            private const val TABLE_RESULTS = "result"
            /* Table User */
            private const val USER_SERIAL_COL = "serial"
            private const val USER_AGENT_SERIAL_COL = "agent_serial"
            private const val USER_USERNAME_COL = "username"
            private const val USER_PASSWORD_COL = "password"
            private const val USER_DEVICE_COL = "device_id"
            /* Table Headers */
            private const val HEADERS_SERIAL_COL = "serial"
            private const val HEADERS_AGENT_COL = "agent"
            private const val HEADERS_DRAW_DATE_COL = "draw_date"
            private const val HEADERS_TRANSACTION_CODE_COL = "transaction_code"
            private const val HEADERS_TOTAL_AMOUNT_COL = "total_amount"
            private const val HEADERS_DATE_PRINTED_COL = "date_printed"
            private const val HEADERS_IS_VOID_COL = "is_void"
            private const val HEADERS_DATE_EDITED_COL = "date_edited"
            private const val HEADERS_IS_DELETED_COL = "is_deleted"
            private const val HEADERS_DATE_DELETED_COL = "date_deleted"
            private const val HEADERS_IS_UPLOADED_COL = "is_uploaded"
            private const val HEADERS_DATE_UPLOADED_COL = "date_uploaded"
            /* Table Details */
            private const val DETAILS_SERIAL_COL = "serial"
            private const val DETAILS_HEADER_SERIAL_COL = "header_serial"
            private const val DETAILS_BET_NUMBER_COL = "bet_number"
            private const val DETAILS_AMOUNT_COL = "amount"
            private const val DETAILS_WIN_COL = "win"
            private const val DETAILS_IS_RAMBOLITO_COL = "is_rambolito"
            private const val DETAILS_IS_UPLOADED_COL = "is_uploaded"
            private const val DETAILS_DATE_UPLOADED_COL = "date_uploaded"
            /* Table Draw */
            private const val DRAW_SERIAL_COL = "serial"
            private const val DRAW_DRAW_NAME_COL = "draw_name"
            private const val DRAW_DRAW_TIME_COL = "draw_time"
            private const val DRAW_CUTOFF_COL = "cutoff"
            /* Table Result */
            private const val RESULT_SERIAL_COL = "serial"
            private const val RESULT_DRAW_SERIAL_COL = "draw_serial"
            private const val RESULT_DRAW_DATE_COL = "draw_date"
            private const val RESULT_WINNING_NUMBER_COL = "winning_number"
            private const val RESULT_DATE_CREATED_COL = "date_created"
            private const val RESULT_DATE_DELETED_COL = "date_deleted"
            private const val RESULT_DATE_EDITED_COL = "date_edited"
        }

     override fun onCreate(db: SQLiteDatabase?) {
         val createUserTable = ("CREATE TABLE "
                 + TABLE_USERS + " ("
                 + USER_DEVICE_COL + " TEXT,"
                 + USER_SERIAL_COL + " TEXT,"
                 + USER_AGENT_SERIAL_COL + " TEXT,"
                 + USER_USERNAME_COL + " TEXT,"
                 + USER_PASSWORD_COL + " TEXT)")

         val createHeaderTable = ("CREATE TABLE "
                 + TABLE_BET_HEADERS + " ("
                 + HEADERS_SERIAL_COL + " TEXT,"
                 + HEADERS_AGENT_COL + " TEXT,"
                 + HEADERS_DRAW_DATE_COL + " TEXT,"
                 + HEADERS_TRANSACTION_CODE_COL + " TEXT,"
                 + HEADERS_TOTAL_AMOUNT_COL + " TEXT,"
                 + HEADERS_DATE_PRINTED_COL + " DATETIME,"
                 + HEADERS_IS_VOID_COL + " INTEGER,"
                 + HEADERS_DATE_EDITED_COL + " DATETIME,"
                 + HEADERS_IS_DELETED_COL + " INTEGER,"
                 + HEADERS_DATE_DELETED_COL + " DATETIME,"
                 + HEADERS_IS_UPLOADED_COL + " INTEGER,"
                 + HEADERS_DATE_UPLOADED_COL + " DATETIME)")

         val createDetailsTable = ("CREATE TABLE "
                 + TABLE_BET_DETAILS + " ("
                 + DETAILS_SERIAL_COL + " TEXT,"
                 + DETAILS_HEADER_SERIAL_COL + " TEXT,"
                 + DETAILS_BET_NUMBER_COL + " TEXT,"
                 + DETAILS_AMOUNT_COL + " TEXT,"
                 + DETAILS_WIN_COL + " TEXT,"
                 + DETAILS_IS_RAMBOLITO_COL + " INTEGER,"
                 + DETAILS_IS_UPLOADED_COL + " INTEGER,"
                 + DETAILS_DATE_UPLOADED_COL + " DATETIME)")

         val createDrawsTable = ("CREATE TABLE "
                 + TABLE_DRAWS + " ("
                 + DRAW_SERIAL_COL + " TEXT, "
                 + DRAW_DRAW_NAME_COL + " TEXT, "
                 + DRAW_DRAW_TIME_COL + " TEXT, "
                 + DRAW_CUTOFF_COL + " TEXT)")

         val createResultsTable = ("CREATE TABLE "
                 + TABLE_RESULTS + " ("
                 + RESULT_SERIAL_COL + " TEXT PRIMARY KEY NOT NULL, "
                 + RESULT_DRAW_SERIAL_COL + " TEXT,"
                 + RESULT_DRAW_DATE_COL + " TEXT,"
                 + RESULT_WINNING_NUMBER_COL + " TEXT,"
                 + RESULT_DATE_CREATED_COL + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                 + RESULT_DATE_DELETED_COL + " DATETIME,"
                 + RESULT_DATE_EDITED_COL + " DATETIME)")


         db?.execSQL(createUserTable)
         db?.execSQL(createHeaderTable)
         db?.execSQL(createDetailsTable)
         db?.execSQL(createDrawsTable)
         db?.execSQL(createResultsTable)
     }

     override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
         db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
         db?.execSQL("DROP TABLE IF EXISTS $TABLE_DRAWS")
         onCreate(db)
     }

     /* Cloud Updates */
     fun checkDeviceUser(): Boolean{
         val db = this.readableDatabase
         val selectQuery = "SELECT COUNT(*) FROM $TABLE_USERS"
         val cursor = db.rawQuery(selectQuery, null)
         val cursorCount = cursor.count
         cursor.close()
         db.close()
         return cursorCount > 0
     }

     fun truncateUsers() {
         val db = this.writableDatabase
         db.delete(TABLE_USERS, null, null)
         db.close()
     }

     fun truncateDraws() {
         val db = this.writableDatabase
         db.delete(TABLE_DRAWS, null, null)
         db.close()
     }

     fun updateUsers(serial: String?, agentSerial: String?, username: String?, password: String?) {
         val db = this.writableDatabase
         val values = ContentValues()
         values.put(USER_SERIAL_COL, serial)
         values.put(USER_AGENT_SERIAL_COL, agentSerial)
         values.put(USER_USERNAME_COL, username)
         values.put(USER_PASSWORD_COL, password)
         db.insert(TABLE_USERS, null, values)
         db.close()
     }

    fun updateDraws(serial: String?, drawName: String?, drawTime: String?, cutoff: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DRAW_SERIAL_COL, serial)
        values.put(DRAW_DRAW_NAME_COL, drawName)
        values.put(DRAW_DRAW_TIME_COL, drawTime)
        values.put(DRAW_CUTOFF_COL, cutoff)
        db.insert(TABLE_DRAWS, null, values)
        db.close()
    }
 }