package com.czbix.klog.database.dao

import com.czbix.klog.database.Database
import com.google.common.collect.ImmutableList
import org.apache.commons.dbutils.QueryRunner
import org.apache.commons.dbutils.ResultSetHandler
import java.sql.Connection
import java.sql.ResultSet

object PostDao {
    const val TABLE_NAME = "post"

    const val COLUMN_ID = "id"
    const val COLUMN_TITLE = "title"
    const val COLUMN_TEXT = "text"

    const val SCHEMA = "$COLUMN_ID, $COLUMN_TITLE, $COLUMN_TEXT"

    internal fun createTable(runner: QueryRunner, conn: Connection) {
        @Suppress("ConvertToStringTemplate")
        runner.update(conn, "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY," +
                "$COLUMN_TITLE TEXT NOT NULL," +
                "$COLUMN_TEXT TEXT NOT NULL)")
        runner.update(conn, "CREATE INDEX ${TABLE_NAME}_title ON $TABLE_NAME ($COLUMN_TITLE)")
    }

    private val SQL_GET_BY_ID = "SELECT $SCHEMA FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
    private val SQL_GET_ALL = "SELECT $SCHEMA FROM $TABLE_NAME"

    fun get(id: Int): Post? {
        return Database.runQuery({runner, conn ->
            runner.query(conn, SQL_GET_BY_ID, PostResultSetHandler.instance, id)
        })
    }

    fun getAll(): List<Post> {
        return Database.runQuery({runner, conn ->
            runner.query(conn, SQL_GET_ALL, PostListResultSetHandler.instance)
        })
    }

    data class Post(var id: Int, var title: String, var text: String)

    class PostResultSetHandler : ResultSetHandler<Post?> {
        companion object {
            val instance by lazy { PostResultSetHandler() }
        }

        override fun handle(rs: ResultSet): Post? {
            return if (rs.next()) {
                Post(rs.getInt(1), rs.getString(2), rs.getString(3))
            } else null
        }
    }

    class PostListResultSetHandler : ResultSetHandler<List<Post>> {
        override fun handle(rs: ResultSet): List<Post> {
            val builder = ImmutableList.builder<Post>()

            while (rs.next()) {
                builder.add(Post(rs.getInt(1), rs.getString(2), rs.getString(3)))

            }

            return builder.build()
        }

        companion object {
            val instance by lazy { PostListResultSetHandler() }
        }
    }
}

