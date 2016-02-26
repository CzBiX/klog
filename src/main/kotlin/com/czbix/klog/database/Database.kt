package com.czbix.klog.database

import com.czbix.klog.database.dao.ConfigDao
import com.czbix.klog.database.dao.ConfigDao.Config
import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.utils.IoUtils.use
import org.apache.commons.dbutils.QueryRunner
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteDataSource
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import javax.sql.DataSource


object Database {
    private const val DATABASE_VERSION = 1;
    private val DATA_PATH: String by lazy {
        "data/data.db"
    }

    private val dataSource: DataSource by lazy {
        init(DATA_PATH)
    }

    private fun init(path: String): SQLiteDataSource {
        val url = "jdbc:sqlite:$path"
        val config = SQLiteConfig().apply {
            setJournalMode(SQLiteConfig.JournalMode.WAL)
        }

        return SQLiteDataSource(config).apply {
            setUrl(url)
        }
    }

    fun <T> runQuery(operation: (QueryRunner, Connection) -> T, autoCommit: Boolean = true): T {
        return dataSource.connection.use { conn ->
            if (conn.autoCommit != autoCommit) {
                conn.autoCommit = autoCommit
            }

            val result = operation(QueryRunner(), conn)
            if (!autoCommit) {
                conn.commit()
            }

            return@use result
        }
    }

    private fun upgrade() {
        var oldVersion = ConfigDao.getInt(Config.VERSION)

        check(oldVersion == DATABASE_VERSION)
    }

    private fun createDatabase() {
        runQuery({ runner, conn ->
            ConfigDao.createTable(runner, conn)
            PostDao.createTable(runner, conn)

            ConfigDao.innerSet(runner, conn, Config.VERSION, DATABASE_VERSION)
        }, false)
    }


    /**
     * init database connection, check version and so on
     */
    fun initDatabase() {
        if (Files.exists(Paths.get(DATA_PATH))) {
            upgrade()
        } else {
            createDatabase()
        }
    }
}
