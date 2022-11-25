package com.slicksoftcoder.smalltownlottery.server

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.slicksoftcoder.smalltownlottery.common.model.BetDetailsTransmitModel
import com.slicksoftcoder.smalltownlottery.common.model.BetHeaderTransmitModel
import com.slicksoftcoder.smalltownlottery.features.bet.BetDetailsModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.Draw2pmModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.Draw5pmModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.Draw9pmModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.PnlModel
import com.slicksoftcoder.smalltownlottery.features.history.HistoryBetModel
import com.slicksoftcoder.smalltownlottery.features.history.HistoryModel

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
            private const val TABLE_CONFIG = "config"
            /* Table User */
            private const val USER_SERIAL_COL = "serial"
            private const val USER_AGENT_SERIAL_COL = "agent_serial"
            private const val USER_USERNAME_COL = "username"
            private const val USER_PASSWORD_COL = "password"
            private const val USER_DEVICE_COL = "device_id"
            private const val USER_LOCATION_COL = "location"
            /* Table Config */
            private const val CONFIG_BIOMETRIC_COL = "is_biometric"
            /* Table Headers */
            private const val HEADERS_SERIAL_COL = "serial"
            private const val HEADERS_AGENT_COL = "agent"
            private const val HEADERS_DRAW_DATE_COL = "draw_date"
            private const val HEADERS_DRAW_TIME_COL = "draw_time"
            private const val HEADERS_TRANSACTION_CODE_COL = "transaction_code"
            private const val HEADERS_TOTAL_AMOUNT_COL = "total_amount"
            private const val HEADERS_DATE_PRINTED_COL = "date_printed"
            private const val HEADERS_IS_VOID_COL = "is_void"
            private const val HEADERS_DATE_EDITED_COL = "date_edited"
            private const val HEADERS_IS_DELETED_COL = "is_deleted"
            private const val HEADERS_DATE_DELETED_COL = "date_deleted"
            private const val HEADERS_IS_UPLOADED_COL = "is_uploaded"
            private const val HEADERS_DATE_UPLOADED_COL = "date_uploaded"
            private const val HEADERS_DATE_CREATED_COL = "date_created"
            private const val HEADERS_IS_CLAIMED_COL = "is_claimed"
            /* Table Details */
            private const val DETAILS_SERIAL_COL = "serial"
            private const val DETAILS_HEADER_SERIAL_COL = "header_serial"
            private const val DETAILS_BET_NUMBER_COL = "bet_number"
            private const val DETAILS_AMOUNT_COL = "amount"
            private const val DETAILS_WIN_COL = "win"
            private const val DETAILS_IS_RAMBOLITO_COL = "is_rambolito"
            private const val DETAILS_BET_STATUS_COL = "bet_status"
            private const val DETAILS_IS_UPLOADED_COL = "is_uploaded"
            private const val DETAILS_DATE_UPLOADED_COL = "date_uploaded"
            /* Table Draw */
            private const val DRAW_SERIAL_COL = "serial"
            private const val DRAW_DRAW_NAME_COL = "draw_name"
            private const val DRAW_DRAW_TIME_COL = "draw_time"
            private const val DRAW_CUTOFF_COL = "cutoff"
            private const val DRAW_RESUME_COL = "resume"
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
                 + USER_SERIAL_COL + " TEXT PRIMARY KEY NOT NULL,"
                 + USER_AGENT_SERIAL_COL + " TEXT,"
                 + USER_USERNAME_COL + " TEXT,"
                 + USER_PASSWORD_COL + " TEXT,"
                 + USER_LOCATION_COL + " TEXT)")

         val createConfigTable = ("CREATE TABLE "
                 + TABLE_CONFIG + " ("
                 + CONFIG_BIOMETRIC_COL + " INTEGER)")

         val createHeaderTable = ("CREATE TABLE "
                 + TABLE_BET_HEADERS + " ("
                 + HEADERS_SERIAL_COL + " TEXT PRIMARY KEY NOT NULL,"
                 + HEADERS_AGENT_COL + " TEXT,"
                 + HEADERS_DRAW_DATE_COL + " TEXT,"
                 + HEADERS_DRAW_TIME_COL + " TEXT,"
                 + HEADERS_TRANSACTION_CODE_COL + " TEXT,"
                 + HEADERS_TOTAL_AMOUNT_COL + " TEXT,"
                 + HEADERS_DATE_PRINTED_COL + " DATETIME,"
                 + HEADERS_IS_VOID_COL + " INTEGER,"
                 + HEADERS_DATE_EDITED_COL + " DATETIME,"
                 + HEADERS_IS_DELETED_COL + " INTEGER,"
                 + HEADERS_DATE_DELETED_COL + " DATETIME,"
                 + HEADERS_IS_UPLOADED_COL + " INTEGER,"
                 + HEADERS_DATE_UPLOADED_COL + " DATETIME,"
                 + HEADERS_DATE_CREATED_COL + " DATETIME,"
                 + HEADERS_IS_CLAIMED_COL + " INTEGER)")

         val createDetailsTable = ("CREATE TABLE "
                 + TABLE_BET_DETAILS + " ("
                 + DETAILS_SERIAL_COL + " TEXT PRIMARY KEY NOT NULL,"
                 + DETAILS_HEADER_SERIAL_COL + " TEXT,"
                 + DETAILS_BET_NUMBER_COL + " TEXT,"
                 + DETAILS_AMOUNT_COL + " TEXT,"
                 + DETAILS_WIN_COL + " TEXT,"
                 + DETAILS_IS_RAMBOLITO_COL + " INTEGER,"
                 + DETAILS_BET_STATUS_COL + " TEXT,"
                 + DETAILS_IS_UPLOADED_COL + " INTEGER,"
                 + DETAILS_DATE_UPLOADED_COL + " DATETIME)")

         val createDrawsTable = ("CREATE TABLE "
                 + TABLE_DRAWS + " ("
                 + DRAW_SERIAL_COL + " TEXT PRIMARY KEY NOT NULL, "
                 + DRAW_DRAW_NAME_COL + " TEXT, "
                 + DRAW_DRAW_TIME_COL + " TEXT, "
                 + DRAW_CUTOFF_COL + " TEXT, "
                 + DRAW_RESUME_COL + " TEXT)")

         val createResultsTable = ("CREATE TABLE "
                 + TABLE_RESULTS + " ("
                 + RESULT_SERIAL_COL + " TEXT PRIMARY KEY NOT NULL, "
                 + RESULT_DRAW_SERIAL_COL + " TEXT,"
                 + RESULT_DRAW_DATE_COL + " TEXT,"
                 + RESULT_WINNING_NUMBER_COL + " TEXT,"
                 + RESULT_DATE_CREATED_COL + " DATETIME,"
                 + RESULT_DATE_DELETED_COL + " DATETIME,"
                 + RESULT_DATE_EDITED_COL + " DATETIME)")


         db?.execSQL(createUserTable)
         db?.execSQL(createConfigTable)
         db?.execSQL(createHeaderTable)
         db?.execSQL(createDetailsTable)
         db?.execSQL(createDrawsTable)
         db?.execSQL(createResultsTable)
         db?.execSQL("INSERT INTO $TABLE_CONFIG ($CONFIG_BIOMETRIC_COL) VALUES(0)")
     }

     override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
         db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
         db?.execSQL("DROP TABLE IF EXISTS $TABLE_DRAWS")
         onCreate(db)
     }

     /* Cloud Updates Transaction */
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

    fun truncateResults() {
        val db = this.writableDatabase
        db.delete(TABLE_RESULTS, null, null)
        db.close()
    }

     fun updateUsers(serial: String?, agentSerial: String?, username: String?, password: String?, deviceId: String?, location: String?) {
         val db = this.writableDatabase
         val values = ContentValues()
         values.put(USER_SERIAL_COL, serial)
         values.put(USER_DEVICE_COL, deviceId)
         values.put(USER_AGENT_SERIAL_COL, agentSerial)
         values.put(USER_USERNAME_COL, username)
         values.put(USER_PASSWORD_COL, password)
         values.put(USER_LOCATION_COL, location)
         db.insert(TABLE_USERS, null, values)
         db.close()
     }

    fun updateDraws(serial: String?, drawName: String?, drawTime: String?, cutoff: String?, resume: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DRAW_SERIAL_COL, serial)
        values.put(DRAW_DRAW_NAME_COL, drawName)
        values.put(DRAW_DRAW_TIME_COL, drawTime)
        values.put(DRAW_CUTOFF_COL, cutoff)
        values.put(DRAW_RESUME_COL, resume)
        db.insert(TABLE_DRAWS, null, values)
        db.close()
    }

    fun updateResults(serial: String?, drawSerial: String?, drawDate: String?, winningNumber: String?, dateCreated: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(RESULT_SERIAL_COL, serial)
        values.put(RESULT_DRAW_SERIAL_COL, drawSerial)
        values.put(RESULT_DRAW_DATE_COL, drawDate)
        values.put(RESULT_WINNING_NUMBER_COL, winningNumber)
        values.put(RESULT_DATE_CREATED_COL, dateCreated)
        db.insert(TABLE_RESULTS, null, values)
        db.close()
    }

    /* User Data Transactions */

    fun authenticate(username: String, password: String, deviceId: String?): Boolean {
        val columns = arrayOf(USER_SERIAL_COL)
        val db = this.readableDatabase
        val selection = "$USER_USERNAME_COL = ? AND $USER_PASSWORD_COL = ? AND $USER_DEVICE_COL = ?"
        val selectionArgs = arrayOf(username, password, deviceId)
        val cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)
        val cursorCount = cursor.count
        cursor.close()
        db.close()
        return cursorCount > 0
    }

    fun retrieveAgent(): String {
        var data: String = String()
        val selectQuery = "SELECT agent_serial FROM $TABLE_USERS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    fun retrieveLocation(): String {
        var data: String = String()
        val selectQuery = "SELECT location FROM $TABLE_USERS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    fun retrieveAgentName(): String {
        var data: String = String()
        val selectQuery = "SELECT username FROM $TABLE_USERS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    fun retrievePassword(): String {
        var data: String = String()
        val selectQuery = "SELECT $USER_PASSWORD_COL FROM $TABLE_USERS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    /* Device Config Transactions */
    fun enableBiometric(enable: Int?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(CONFIG_BIOMETRIC_COL, enable)
        db.update(TABLE_CONFIG, values, null, null)
        db.close()
    }

    fun checkBiometric(): Boolean {
        val columns = arrayOf(CONFIG_BIOMETRIC_COL)
        val db = this.readableDatabase
        val selection = "$CONFIG_BIOMETRIC_COL = 1"
        val cursor = db.query(TABLE_CONFIG, columns, selection, null, null, null, null)
        val cursorCount = cursor.count
        cursor.close()
        db.close()
        return cursorCount > 0

    }

    /* Draw Data Transactions */
    fun retrieveDrawSerial(drawName: String?): String {
        var data: String = String()
        val selectQuery = "SELECT serial FROM $TABLE_DRAWS WHERE $DRAW_DRAW_NAME_COL = '$drawName' LIMIT 1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    fun retrieve2pmDrawSerial(): String {
        var data: String = String()
        val selectQuery = "SELECT serial FROM $TABLE_DRAWS WHERE $DRAW_DRAW_NAME_COL = '2 PM' LIMIT 1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    fun retrieve5pmDrawSerial(): String {
        var data: String = String()
        val selectQuery = "SELECT serial FROM $TABLE_DRAWS WHERE $DRAW_DRAW_NAME_COL = '5 PM' LIMIT 1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    fun retrieve9pmDrawSerial(): String {
        var data: String = String()
        val selectQuery = "SELECT serial FROM $TABLE_DRAWS WHERE $DRAW_DRAW_NAME_COL = '9 PM' LIMIT 1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    /* Bet Transactions */
    fun insertBetHeader(serial: String?,agent: String?,drawDate: String?,drawTime: String?, transactionCode: String?, totalAmount: String?, currentDateTime: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(HEADERS_SERIAL_COL, serial)
        values.put(HEADERS_AGENT_COL, agent)
        values.put(HEADERS_DRAW_DATE_COL, drawDate)
        values.put(HEADERS_DRAW_TIME_COL, drawTime)
        values.put(HEADERS_TRANSACTION_CODE_COL, transactionCode)
        values.put(HEADERS_TOTAL_AMOUNT_COL, totalAmount)
        values.put(HEADERS_IS_VOID_COL, 0)
        values.put(HEADERS_IS_DELETED_COL, 0)
        values.put(HEADERS_IS_UPLOADED_COL, 0)
        values.put(HEADERS_DATE_PRINTED_COL, currentDateTime)
        values.put(HEADERS_DATE_EDITED_COL, currentDateTime)
        values.put(HEADERS_DATE_CREATED_COL, currentDateTime)
        values.put(HEADERS_IS_CLAIMED_COL, 0)
        db.insert(TABLE_BET_HEADERS, null, values)
        db.close()
    }

    fun insertBetDetails(serial: String?,header: String?,betNumber: String?, amount: String?, win: String?, isRambolito: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DETAILS_SERIAL_COL, serial)
        values.put(DETAILS_HEADER_SERIAL_COL, header)
        values.put(DETAILS_BET_NUMBER_COL, betNumber)
        values.put(DETAILS_AMOUNT_COL, amount)
        values.put(DETAILS_WIN_COL, win)
        values.put(DETAILS_IS_RAMBOLITO_COL, isRambolito)
        values.put(DETAILS_BET_STATUS_COL, "CANCELLED")
        values.put(DETAILS_IS_UPLOADED_COL, 0)
        db.insert(TABLE_BET_DETAILS, null, values)
        db.close()
    }

    fun confirmBetDetails(headerSerial: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DETAILS_BET_STATUS_COL, "CONFIRMED")
        db.update(TABLE_BET_DETAILS, values, "$DETAILS_HEADER_SERIAL_COL = ?", arrayOf(headerSerial))
        db.close()
    }

    fun deleteCanceledBet() {
        val db = this.writableDatabase
        db.delete(TABLE_BET_DETAILS, "$DETAILS_BET_STATUS_COL = ?", arrayOf("CANCELLED"))
        db.close()
    }

    fun deleteBet(headerSerial: String?) {
        val db = this.writableDatabase
        db.delete(TABLE_BET_HEADERS, "$HEADERS_SERIAL_COL = ?", arrayOf(headerSerial))
        db.delete(TABLE_BET_DETAILS, "$DETAILS_HEADER_SERIAL_COL = ?", arrayOf(headerSerial))
        db.close()
    }

    fun deleteBetDetail(serial: String?) {
        val db = this.writableDatabase
        db.delete(TABLE_BET_DETAILS, "$DETAILS_SERIAL_COL = ?", arrayOf(serial))
        db.close()
    }

    fun voidBet(headerSerial: String?, voidStatus: Int?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(HEADERS_IS_VOID_COL, voidStatus)
        db.update(TABLE_BET_HEADERS, values, "$HEADERS_SERIAL_COL = ?", arrayOf(headerSerial))
        db.close()
    }

    fun claimBet(transactionCode: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(HEADERS_IS_CLAIMED_COL, 1)
        db.update(TABLE_BET_HEADERS, values, "$HEADERS_TRANSACTION_CODE_COL = ?", arrayOf(transactionCode))
        db.close()
    }

    fun retrieveBetDetails(headerSerial: String?): ArrayList<BetDetailsModel> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_BET_DETAILS WHERE $DETAILS_HEADER_SERIAL_COL = '$headerSerial'"
        val data: ArrayList<BetDetailsModel> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query,  null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    BetDetailsModel(
                        serial = cursor.getString(0),
                        betNumber = cursor.getString(2),
                        amount = cursor.getString(3),
                        win = cursor.getString(4),
                        isRambolito = cursor.getString(5),

                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    /* History Transactions */
    fun retrieveHistory(): ArrayList<HistoryModel> {
        val db = this.readableDatabase
        val query = "SELECT t1.serial, t1.draw_date, t2.draw_name, t1.transaction_code, t1.total_amount, t1.is_void, time(t1.date_created) as bet_time FROM $TABLE_BET_HEADERS t1\n" +
                "INNER JOIN draws t2 ON t1.draw_time = t2.serial\n" +
                "WHERE $HEADERS_DATE_CREATED_COL > date('now') AND $HEADERS_IS_DELETED_COL = 0 ORDER BY $HEADERS_DATE_CREATED_COL DESC"
        val data: ArrayList<HistoryModel> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query,  null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    HistoryModel(
                        headerSerial = cursor.getString(0),
                        drawDate = cursor.getString(1),
                        drawTime = cursor.getString(2),
                        transactionCode = cursor.getString(3),
                        totalAmount = cursor.getString(4),
                        isVoid = cursor.getString(5),
                        betTime = cursor.getString(6),

                        )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun retrieveHistoryByTranscode(transactionCode: String?): ArrayList<HistoryModel> {
        val db = this.readableDatabase
        val query = "SELECT t1.serial, t1.draw_date, t2.draw_name, t1.transaction_code, t1.total_amount, t1.is_void, time(t1.date_created) as bet_time FROM $TABLE_BET_HEADERS t1\n" +
                "INNER JOIN draws t2 ON t1.draw_time = t2.serial\n" +
                "WHERE $HEADERS_DATE_CREATED_COL > date('now') AND $HEADERS_TRANSACTION_CODE_COL = '$transactionCode'"
        val data: ArrayList<HistoryModel> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query,  null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    HistoryModel(
                        headerSerial = cursor.getString(0),
                        drawDate = cursor.getString(1),
                        drawTime = cursor.getString(2),
                        transactionCode = cursor.getString(3),
                        totalAmount = cursor.getString(4),
                        isVoid = cursor.getString(5),
                        betTime = cursor.getString(6),

                        )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun retrieveHistoryBetDetails(headerSerial: String?): ArrayList<HistoryBetModel> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_BET_DETAILS WHERE $DETAILS_HEADER_SERIAL_COL = '$headerSerial'"
        val data: ArrayList<HistoryBetModel> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query,  null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    HistoryBetModel(
                        serial = cursor.getString(0),
                        headerSerial = cursor.getString(1),
                        betNumber = cursor.getString(2),
                        amount = cursor.getString(3),
                        win = cursor.getString(4),
                        isRambolito = cursor.getString(5),
                        )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    /* Bet Result */

    fun deleteResult(drawSerial: String?) {
        val db = this.writableDatabase
        db.delete(TABLE_RESULTS, "$RESULT_DRAW_SERIAL_COL = ?", arrayOf(drawSerial))
        db.close()
    }

    fun insertResult(serial: String?,drawSerial: String?,drawDate: String?,winningNumber: String?, currentDateTime: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(RESULT_SERIAL_COL, serial)
        values.put(RESULT_DRAW_SERIAL_COL, drawSerial)
        values.put(RESULT_DRAW_DATE_COL, drawDate)
        values.put(RESULT_WINNING_NUMBER_COL, winningNumber)
        values.put(RESULT_DATE_CREATED_COL, currentDateTime)
        db.insert(TABLE_RESULTS, null, values)
        db.close()
    }

    fun retrieve2pmDrawResult(drawTime: String): String {
        var data: String = String()
        val selectQuery = "SELECT $RESULT_WINNING_NUMBER_COL FROM $TABLE_RESULTS WHERE $RESULT_DATE_CREATED_COL = DATE('now') AND $RESULT_DRAW_SERIAL_COL = '$drawTime' LIMIT 1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    fun retrieve5pmDrawResult(drawTime: String): String {
        var data: String = String()
        val selectQuery = "SELECT $RESULT_WINNING_NUMBER_COL FROM $TABLE_RESULTS WHERE $RESULT_DATE_CREATED_COL = DATE('now') AND $RESULT_DRAW_SERIAL_COL = '$drawTime' LIMIT 1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    fun retrieve9pmDrawResult(drawTime: String): String {
        var data: String = String()
        val selectQuery = "SELECT $RESULT_WINNING_NUMBER_COL FROM $TABLE_RESULTS WHERE $RESULT_DATE_CREATED_COL = DATE('now') AND $RESULT_DRAW_SERIAL_COL = '$drawTime' LIMIT 1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    /* Bet Transmit Transactions */

    fun transmitBetHeaders(): ArrayList<BetHeaderTransmitModel> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_BET_HEADERS WHERE $HEADERS_IS_UPLOADED_COL = '0'"
        val data: ArrayList<BetHeaderTransmitModel> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query,  null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    BetHeaderTransmitModel(
                        serial = cursor.getString(0),
                        agent = cursor.getString(1),
                        drawDate = cursor.getString(2),
                        drawSerial = cursor.getString(3),
                        transactionCode = cursor.getString(4),
                        totalAmount = cursor.getString(5),
                        dateCreated = cursor.getString(13),
                        datePrinted = cursor.getString(6),
                        isVoid = cursor.getString(7),
                        editedBy = cursor.getString(1),
                        dateEdited = cursor.getString(8),
                        )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun transmitBetDetails(): ArrayList<BetDetailsTransmitModel> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_BET_DETAILS WHERE $DETAILS_BET_STATUS_COL = 'CONFIRMED' AND $DETAILS_IS_UPLOADED_COL = '0'"
        val data: ArrayList<BetDetailsTransmitModel> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query,  null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    BetDetailsTransmitModel(
                        serial = cursor.getString(0),
                        headerSerial = cursor.getString(1),
                        betNumber = cursor.getString(2),
                        amount = cursor.getString(3),
                        win = cursor.getString(4),
                        isRambolito = cursor.getString(5),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun transmitBetHeader(headerSerial: String?): ArrayList<BetHeaderTransmitModel> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_BET_HEADERS WHERE $HEADERS_SERIAL_COL = '$headerSerial'"
        val data: ArrayList<BetHeaderTransmitModel> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query,  null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    BetHeaderTransmitModel(
                        serial = cursor.getString(0),
                        agent = cursor.getString(1),
                        drawDate = cursor.getString(2),
                        drawSerial = cursor.getString(3),
                        transactionCode = cursor.getString(4),
                        totalAmount = cursor.getString(5),
                        dateCreated = cursor.getString(13),
                        datePrinted = cursor.getString(6),
                        isVoid = cursor.getString(7),
                        editedBy = cursor.getString(1),
                        dateEdited = cursor.getString(8),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun transmitBetDetail(headerSerial: String?): ArrayList<BetDetailsTransmitModel> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_BET_DETAILS WHERE $DETAILS_BET_STATUS_COL = 'CONFIRMED' AND $DETAILS_HEADER_SERIAL_COL = '$headerSerial'"
        val data: ArrayList<BetDetailsTransmitModel> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query,  null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    BetDetailsTransmitModel(
                        serial = cursor.getString(0),
                        headerSerial = cursor.getString(1),
                        betNumber = cursor.getString(2),
                        amount = cursor.getString(3),
                        win = cursor.getString(4),
                        isRambolito = cursor.getString(5),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun updateBetHeaderTransmitted(headerSerial: String?, date: String, status: Int) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(HEADERS_IS_UPLOADED_COL, status)
        values.put(HEADERS_DATE_UPLOADED_COL, date)
        db.update(TABLE_BET_HEADERS, values, "$HEADERS_SERIAL_COL = ?", arrayOf(headerSerial))
        db.close()
    }

    fun updateBetDetailsTransmitted(headerSerial: String?, date: String, status: Int) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DETAILS_IS_UPLOADED_COL, status)
        values.put(DETAILS_DATE_UPLOADED_COL, date)
        db.update(TABLE_BET_DETAILS, values, "$DETAILS_HEADER_SERIAL_COL = ?", arrayOf(headerSerial))
        db.close()
    }

    fun updateBetDetailTransmitted(detailSerial: String?, date: String, status: Int) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DETAILS_IS_UPLOADED_COL, status)
        values.put(DETAILS_DATE_UPLOADED_COL, date)
        db.update(TABLE_BET_DETAILS, values, "$DETAILS_SERIAL_COL = ?", arrayOf(detailSerial))
        db.close()
    }

    /* Cutoff|Resume Transactions */
    fun retrieveDrawCutOff(drawName: String?): String {
        var data: String = String()
        val selectQuery = "SELECT $DRAW_CUTOFF_COL FROM $TABLE_DRAWS WHERE $DRAW_DRAW_NAME_COL = '$drawName'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    fun retrieveDrawResume(drawName: String?): String {
        var data: String = String()
        val selectQuery = "SELECT $DRAW_RESUME_COL FROM $TABLE_DRAWS WHERE $DRAW_DRAW_NAME_COL = '$drawName'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return data
    }

    /* PNL Transactions */
    fun retrievePNL(drawDate: String?): ArrayList<PnlModel> {
        val db = this.readableDatabase
        val query = "SELECT t1.draw_date 'DRAW DATE', t2.username 'AGENT CODE', t4.draw_name 'DRAW TYPE', \n" +
                "COALESCE((SELECT e1.winning_number FROM result e1 WHERE e1.draw_date = t1.draw_date AND e1.draw_serial = t1.draw_time),'TBA') 'result',\n" +
                "\n" +
                "(SELECT SUM(e1.total_amount) FROM bet_headers e1 WHERE e1.draw_date = t1.draw_date AND e1.agent = t1.agent AND e1.draw_time = t1.draw_time AND e1.is_void = 0) 'totalBet',\n" +
                "\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE f1.is_void = 0 AND e1.bet_number = t5.winning_number AND e1.is_rambolito = 0\n" +
                "AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00) \n" +
                "\n" +
                "+\n" +
                "\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE e1.bet_number IN (\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1)\n" +
                ")\n" +
                "AND f1.is_void = 0 AND e1.is_rambolito = 1 AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00) 'totalHit',\n" +
                "\n" +
                "(SELECT SUM(e1.total_amount) FROM bet_headers e1 WHERE e1.draw_date = t1.draw_date AND e1.agent = t1.agent AND e1.draw_time = t1.draw_time AND e1.is_void = 0)\n" +
                "-\n" +
                "(\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE f1.is_void = 0 AND e1.bet_number = t5.winning_number AND e1.is_rambolito = 0\n" +
                "AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00) \n" +
                "\n" +
                "+\n" +
                "\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE e1.bet_number IN (\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1)\n" +
                ")\n" +
                "AND f1.is_void = 0 AND e1.is_rambolito = 1 AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00)\n" +
                ") 'pnl',\n" +
                "\n" +
                "(\n" +
                "SELECT COUNT(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE f1.is_void = 0 AND e1.bet_number = t5.winning_number AND e1.is_rambolito = 0\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date  \n" +
                ") \n" +
                "+\n" +
                "(\n" +
                "SELECT COUNT(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE e1.bet_number IN (\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1)\n" +
                ")\n" +
                "AND f1.is_void = 0 AND e1.is_rambolito = 1 AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                ") 'win'\n" +
                "\n" +
                "\n" +
                "FROM bet_headers t1\n" +
                "LEFT JOIN users t2 ON t1.agent = t2.agent_serial\n" +
                "LEFT JOIN draws t4 ON t1.draw_time = t4.serial\n" +
                "LEFT JOIN result t5 ON t1.draw_time = t5.draw_serial\n" +
                "WHERE t1.is_void = 0 AND t1.draw_date = '$drawDate'\n" +
                "GROUP BY t1.draw_date, t2.username, t4.draw_name"
        val data: ArrayList<PnlModel> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query,  null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    PnlModel(
                        totalBet = cursor.getString(4),
                        totalHit = cursor.getString(5),
                        pnl = cursor.getString(6),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun retrieve2pmResult(drawDate: String?): ArrayList<Draw2pmModel> {
        val db = this.readableDatabase
        val query = "SELECT t1.draw_date 'DRAW DATE', t2.username 'AGENT CODE', t4.draw_name 'DRAW TYPE', \n" +
                "COALESCE((SELECT e1.winning_number FROM result e1 WHERE e1.draw_date = t1.draw_date AND e1.draw_serial = t1.draw_time),'TBA') 'result',\n" +
                "\n" +
                "(SELECT SUM(e1.total_amount) FROM bet_headers e1 WHERE e1.draw_date = t1.draw_date AND e1.agent = t1.agent AND e1.draw_time = t1.draw_time AND e1.is_void = 0) 'totalBet',\n" +
                "\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE f1.is_void = 0 AND e1.bet_number = t5.winning_number AND e1.is_rambolito = 0\n" +
                "AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00) \n" +
                "\n" +
                "+\n" +
                "\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE e1.bet_number IN (\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1)\n" +
                ")\n" +
                "AND f1.is_void = 0 AND e1.is_rambolito = 1 AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00) 'totalHit',\n" +
                "\n" +
                "(SELECT SUM(e1.total_amount) FROM bet_headers e1 WHERE e1.draw_date = t1.draw_date AND e1.agent = t1.agent AND e1.draw_time = t1.draw_time AND e1.is_void = 0)\n" +
                "-\n" +
                "(\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE f1.is_void = 0 AND e1.bet_number = t5.winning_number AND e1.is_rambolito = 0\n" +
                "AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00) \n" +
                "\n" +
                "+\n" +
                "\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE e1.bet_number IN (\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1)\n" +
                ")\n" +
                "AND f1.is_void = 0 AND e1.is_rambolito = 1 AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00)\n" +
                ") 'pnl',\n" +
                "\n" +
                "(\n" +
                "SELECT COUNT(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE f1.is_void = 0 AND e1.bet_number = t5.winning_number AND e1.is_rambolito = 0\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date  \n" +
                ") \n" +
                "+\n" +
                "(\n" +
                "SELECT COUNT(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE e1.bet_number IN (\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1)\n" +
                ")\n" +
                "AND f1.is_void = 0 AND e1.is_rambolito = 1 AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                ") 'win'\n" +
                "\n" +
                "\n" +
                "FROM bet_headers t1\n" +
                "LEFT JOIN users t2 ON t1.agent = t2.agent_serial\n" +
                "LEFT JOIN draws t4 ON t1.draw_time = t4.serial\n" +
                "LEFT JOIN result t5 ON t1.draw_time = t5.draw_serial\n" +
                "WHERE t1.is_void = 0 AND t1.draw_date = '$drawDate' AND t4.draw_name = '2 PM'\n" +
                "GROUP BY t1.draw_date, t2.username, t4.draw_name"
        val data: ArrayList<Draw2pmModel> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query,  null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Draw2pmModel(
                        result = cursor.getString(3),
                        totalBet = cursor.getString(4),
                        totalHit = cursor.getString(5),
                        pnl = cursor.getString(6),
                        win = cursor.getString(7),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun retrieve5pmResult(drawDate: String?): ArrayList<Draw5pmModel> {
        val db = this.readableDatabase
        val query = "SELECT t1.draw_date 'DRAW DATE', t2.username 'AGENT CODE', t4.draw_name 'DRAW TYPE', \n" +
                "COALESCE((SELECT e1.winning_number FROM result e1 WHERE e1.draw_date = t1.draw_date AND e1.draw_serial = t1.draw_time),'TBA') 'result',\n" +
                "\n" +
                "(SELECT SUM(e1.total_amount) FROM bet_headers e1 WHERE e1.draw_date = t1.draw_date AND e1.agent = t1.agent AND e1.draw_time = t1.draw_time AND e1.is_void = 0) 'totalBet',\n" +
                "\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE f1.is_void = 0 AND e1.bet_number = t5.winning_number AND e1.is_rambolito = 0\n" +
                "AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00) \n" +
                "\n" +
                "+\n" +
                "\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE e1.bet_number IN (\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1)\n" +
                ")\n" +
                "AND f1.is_void = 0 AND e1.is_rambolito = 1 AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00) 'totalHit',\n" +
                "\n" +
                "(SELECT SUM(e1.total_amount) FROM bet_headers e1 WHERE e1.draw_date = t1.draw_date AND e1.agent = t1.agent AND e1.draw_time = t1.draw_time AND e1.is_void = 0)\n" +
                "-\n" +
                "(\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE f1.is_void = 0 AND e1.bet_number = t5.winning_number AND e1.is_rambolito = 0\n" +
                "AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00) \n" +
                "\n" +
                "+\n" +
                "\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE e1.bet_number IN (\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1)\n" +
                ")\n" +
                "AND f1.is_void = 0 AND e1.is_rambolito = 1 AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00)\n" +
                ") 'pnl',\n" +
                "\n" +
                "(\n" +
                "SELECT COUNT(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE f1.is_void = 0 AND e1.bet_number = t5.winning_number AND e1.is_rambolito = 0\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date  \n" +
                ") \n" +
                "+\n" +
                "(\n" +
                "SELECT COUNT(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE e1.bet_number IN (\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1)\n" +
                ")\n" +
                "AND f1.is_void = 0 AND e1.is_rambolito = 1 AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                ") 'win'\n" +
                "\n" +
                "\n" +
                "FROM bet_headers t1\n" +
                "LEFT JOIN users t2 ON t1.agent = t2.agent_serial\n" +
                "LEFT JOIN draws t4 ON t1.draw_time = t4.serial\n" +
                "LEFT JOIN result t5 ON t1.draw_time = t5.draw_serial\n" +
                "WHERE t1.is_void = 0 AND t1.draw_date = '$drawDate' AND t4.draw_name = '5 PM'\n" +
                "GROUP BY t1.draw_date, t2.username, t4.draw_name"
        val data: ArrayList<Draw5pmModel> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query,  null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Draw5pmModel(
                        result = cursor.getString(3),
                        totalBet = cursor.getString(4),
                        totalHit = cursor.getString(5),
                        pnl = cursor.getString(6),
                        win = cursor.getString(7),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun retrieve9pmResult(drawDate: String?): ArrayList<Draw9pmModel> {
        val db = this.readableDatabase
        val query = "SELECT t1.draw_date 'DRAW DATE', t2.username 'AGENT CODE', t4.draw_name 'DRAW TYPE', \n" +
                "COALESCE((SELECT e1.winning_number FROM result e1 WHERE e1.draw_date = t1.draw_date AND e1.draw_serial = t1.draw_time),'TBA') 'result',\n" +
                "\n" +
                "(SELECT SUM(e1.total_amount) FROM bet_headers e1 WHERE e1.draw_date = t1.draw_date AND e1.agent = t1.agent AND e1.draw_time = t1.draw_time AND e1.is_void = 0) 'totalBet',\n" +
                "\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE f1.is_void = 0 AND e1.bet_number = t5.winning_number AND e1.is_rambolito = 0\n" +
                "AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00) \n" +
                "\n" +
                "+\n" +
                "\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE e1.bet_number IN (\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1)\n" +
                ")\n" +
                "AND f1.is_void = 0 AND e1.is_rambolito = 1 AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00) 'totalHit',\n" +
                "\n" +
                "(SELECT SUM(e1.total_amount) FROM bet_headers e1 WHERE e1.draw_date = t1.draw_date AND e1.agent = t1.agent AND e1.draw_time = t1.draw_time AND e1.is_void = 0)\n" +
                "-\n" +
                "(\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE f1.is_void = 0 AND e1.bet_number = t5.winning_number AND e1.is_rambolito = 0\n" +
                "AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00) \n" +
                "\n" +
                "+\n" +
                "\n" +
                "COALESCE((\n" +
                "SELECT SUM(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE e1.bet_number IN (\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1)\n" +
                ")\n" +
                "AND f1.is_void = 0 AND e1.is_rambolito = 1 AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                "),0.00)\n" +
                ") 'pnl',\n" +
                "\n" +
                "(\n" +
                "SELECT COUNT(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE f1.is_void = 0 AND e1.bet_number = t5.winning_number AND e1.is_rambolito = 0\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date  \n" +
                ") \n" +
                "+\n" +
                "(\n" +
                "SELECT COUNT(e1.win) FROM bet_details e1 \n" +
                "INNER JOIN bet_headers f1 ON e1.header_serial = f1.serial\n" +
                "WHERE e1.bet_number IN (\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,3,1),\n" +
                "substr(t5.winning_number,2,1) || substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,1,1) || substr(t5.winning_number,2,1),\n" +
                "substr(t5.winning_number,3,1) || substr(t5.winning_number,2,1) || substr(t5.winning_number,1,1)\n" +
                ")\n" +
                "AND f1.is_void = 0 AND e1.is_rambolito = 1 AND t5.winning_number IS NOT NULL\n" +
                "AND f1.agent = t2.agent_serial AND f1.draw_time = t5.draw_serial AND f1.draw_date = t5.draw_date\n" +
                ") 'win'\n" +
                "\n" +
                "\n" +
                "FROM bet_headers t1\n" +
                "LEFT JOIN users t2 ON t1.agent = t2.agent_serial\n" +
                "LEFT JOIN draws t4 ON t1.draw_time = t4.serial\n" +
                "LEFT JOIN result t5 ON t1.draw_time = t5.draw_serial\n" +
                "WHERE t1.is_void = 0 AND t1.draw_date = '$drawDate' AND t4.draw_name = '9 PM'\n" +
                "GROUP BY t1.draw_date, t2.username, t4.draw_name"
        val data: ArrayList<Draw9pmModel> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query,  null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Draw9pmModel(
                        result = cursor.getString(3),
                        totalBet = cursor.getString(4),
                        totalHit = cursor.getString(5),
                        pnl = cursor.getString(6),
                        win = cursor.getString(7),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

 }