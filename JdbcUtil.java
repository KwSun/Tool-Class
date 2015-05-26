package cn.itcast.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class JdbcUtil {
	private static String driverClass;
	private static String url;
	private static String user;
	private static String password;
	static{
		ResourceBundle rb = ResourceBundle.getBundle("dbcfg");
		driverClass = rb.getString("driverClass");
		url =  rb.getString("url");
		user =  rb.getString("user");
		password =  rb.getString("password");
		//注册驱动
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError("读取数据库配置文件失败");
		}
	}
	
	public static Connection getConnection() throws SQLException{
		Connection conn = DriverManager.getConnection(url, user, password);
		return conn;
	}
	public static void release(ResultSet rs,Statement stmt,Connection conn){
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rs =null;
		}
		if(stmt!=null){
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt =null;
		}
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			conn =null;
		}
	}
	
}
