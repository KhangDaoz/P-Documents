package com.ptit.p.documents.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DAO {
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/p_documents?useSSL=false&serverTimezone=UTC";
	private static final String JDBC_USER = "root";
	private static final String JDBC_PASSWORD = "1812";

	protected Connection getConnection() throws SQLException {
		return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
	}
}
