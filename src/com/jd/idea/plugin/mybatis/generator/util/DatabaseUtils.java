package com.jd.idea.plugin.mybatis.generator.util;

import com.intellij.openapi.ui.Messages;
import com.jd.idea.plugin.mybatis.generator.model.DbType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils {

	public static void testConnection(String driverClass, String url, String username, String password, boolean isMySQL8) throws ClassNotFoundException, SQLException {
		boolean connected = false;
		Connection conn = null;
		if (driverClass.contains("oracle")) {
			Class.forName(DbType.Oracle.getDriverClass());
		} else if (driverClass.contains("mysql")) {
			if (!isMySQL8) {
				Class.forName(DbType.MySQL.getDriverClass());
			} else {
				Class.forName(DbType.MySQL_8.getDriverClass());
				url += "?serverTimezone=UTC";
			}
		} else if (driverClass.contains("postgresql")) {
			Class.forName(DbType.PostgreSQL.getDriverClass());
		} else if (driverClass.contains("sqlserver")) {
			Class.forName(DbType.SqlServer.getDriverClass());
		} else if (driverClass.contains("sqlite")) {
			Class.forName(DbType.Sqlite.getDriverClass());
		} else if (driverClass.contains("mariadb")) {
			Class.forName(DbType.MariaDB.getDriverClass());
		}

		try {
			conn = DriverManager.getConnection(url, username, password);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
