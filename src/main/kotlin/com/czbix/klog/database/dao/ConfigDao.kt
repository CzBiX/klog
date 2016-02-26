package com.czbix.klog.database.dao

import com.czbix.klog.database.Database
import org.apache.commons.dbutils.QueryRunner
import org.apache.commons.dbutils.ResultSetHandler
import java.sql.Connection

object ConfigDao {
    const val TABLE_NAME = "config"

    const val COLUMN_KEY = "key"
    const val COLUMN_VALUE = "value"

    const val SCHEMA = "$COLUMN_KEY, $COLUMN_VALUE"

    internal fun createTable(runner: QueryRunner, conn: Connection) {
        @Suppress("ConvertToStringTemplate")
        runner.update(conn, "CREATE TABLE $TABLE_NAME (" +
                "id INTEGER PRIMARY KEY," +
                "$COLUMN_KEY TEXT NOT NULL UNIQUE," +
                "$COLUMN_VALUE TEXT)")
    }

    private val SQL_SET = "REPLACE INTO $TABLE_NAME($SCHEMA) VALUES (?, ?)"
    private val SQL_GET = "SELECT $COLUMN_VALUE FROM $TABLE_NAME WHERE $COLUMN_KEY = ?"

    fun getString(key: Config, defVal: String? = null): String? {
        return Database.runQuery({runner, conn ->
            runner.query(conn, SQL_GET, ResultSetHandler {
                if (it.next()) it.getString(1) else defVal
            }, key.key)
        })
    }

    fun getInt(key: Config, defVal: Int = 0): Int {
        val str = getString(key)
        return if (str == null) defVal else str.toInt()
    }

    fun <T> set(key: Config, value: T) {
        Database.runQuery({ runner, conn ->
            innerSet(runner, conn, key, value)
        })
    }

    internal fun <T> innerSet(runner: QueryRunner, conn: Connection, key: Config, value: T) {
        runner.update(conn, SQL_SET, key.key, value)
    }

    enum class Config(val key: String) {
        VERSION("version"),
    }
}
