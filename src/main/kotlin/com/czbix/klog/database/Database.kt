package com.czbix.klog.database

import com.czbix.klog.common.Config
import com.czbix.klog.database.dao.ConfigDao
import com.czbix.klog.database.dao.ConfigDao.ConfigKey
import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.utils.IoUtils.use
import org.apache.commons.dbutils.QueryRunner
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteDataSource
import java.io.File
import java.nio.file.Files
import java.sql.Connection
import javax.sql.DataSource


object Database {
    private const val DATABASE_VERSION = 1;
    private fun getDbPath() = Config.DATABASE_PATH

    private val dataSource: DataSource by lazy {
        init(getDbPath().toString())
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
        var oldVersion = ConfigDao.getInt(ConfigKey.VERSION)

        check(oldVersion == DATABASE_VERSION)
    }

    private fun createDatabase() {
        runQuery({ runner, conn ->
            ConfigDao.createTable(runner, conn)
            PostDao.createTable(runner, conn)

            ConfigDao.innerSet(runner, conn, ConfigKey.VERSION, DATABASE_VERSION)
        }, false)
    }


    /**
     * init database connection, check version and so on
     */
    fun initDatabase() {
        if (Files.exists(getDbPath())) {
            upgrade()
        } else {
            createDatabase()
        }
    }
}
