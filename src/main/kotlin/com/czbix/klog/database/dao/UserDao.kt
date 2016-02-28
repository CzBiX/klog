package com.czbix.klog.database.dao

import com.czbix.klog.database.Database.runQuery
import com.czbix.klog.utils.or
import org.apache.commons.dbutils.QueryRunner
import org.apache.commons.dbutils.ResultSetHandler
import java.sql.Connection
import java.sql.ResultSet

object UserDao {
    private const val TABLE_NAME = "user"

    private const val COLUMN_ID = "id"
    private const val COLUMN_USERNAME = "username"
    private const val COLUMN_NICKNAME = "nickname"
    private const val COLUMN_PASSWORD = "password"

    private val SCHEMA = arrayOf(
            COLUMN_ID,
            COLUMN_USERNAME,
            COLUMN_NICKNAME,
            COLUMN_PASSWORD).joinToString()

    internal fun createTable(runner: QueryRunner, conn: Connection) {
        @Suppress("ConvertToStringTemplate")
        runner.update(conn, "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY," +
                "$COLUMN_USERNAME TEXT NOT NULL COLLATE NOCASE," +
                "$COLUMN_NICKNAME TEXT," +
                "$COLUMN_PASSWORD TEXT NOT NULL)")
        runner.update(conn, "CREATE UNIQUE INDEX ${TABLE_NAME}_username ON $TABLE_NAME ($COLUMN_USERNAME)")
    }

    private val SQL_GET_BY_ID = "SELECT $SCHEMA FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
    private val SQL_GET_BY_USERNAME = "SELECT $SCHEMA FROM $TABLE_NAME WHERE $COLUMN_USERNAME = ?"

    fun get(id: Int): User? {
        return runQuery({runner, conn ->
            runner.query(conn, SQL_GET_BY_ID, UserResultSetHandler.instance, id)
        })
    }

    fun get(username: String): User? {
        return runQuery({runner, conn ->
            runner.query(conn, SQL_GET_BY_USERNAME, UserResultSetHandler.instance, username)
        })
    }

    data class User(val id: Int? = null, val username: String,
                    val nickName: String? = null, val password: String) {
        val displayName: String
            get() {
                return nickName.or(username)
            }

        fun validatePwd(pwd: String): Boolean {
            return password.equals(pwd)
        }
    }

    private class UserResultSetHandler : ResultSetHandler<User?> {
        companion object {
            val instance by lazy { UserResultSetHandler() }
        }

        override fun handle(rs: ResultSet): User? {
            return if (rs.next()) {
                User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4))
            } else null
        }
    }
}
