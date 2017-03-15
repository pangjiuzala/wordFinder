package cn.zju.edu.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import cn.zju.edu.util.DBConnection;

public class DBHelper {

	public static void createtable() throws SQLException {
		/* 创建historyresult表，存放预测结果 */
		String sql = "DROP TABLE IF EXISTS `historyresult`;";
		String sqls = "CREATE TABLE `historyresult` (`id` int(100) NOT NULL AUTO_INCREMENT,truevalue varchar(100) DEFAULT NULL,predictvalue int(100) DEFAULT NULL ,PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=gbk";
		Connection conn = (Connection) DBConnection.getConnection();
		Statement st = conn.createStatement();
		st.execute(sql);
		st.execute(sqls);
	}

	public static void createtableresult() throws SQLException {
		/* 创建historyresult表，存放预测结果 */
		String sql = "DROP TABLE IF EXISTS `result`;";
		String sqls = "CREATE TABLE `result` (`id` int(100) NOT NULL AUTO_INCREMENT,truevalue varchar(100) DEFAULT NULL,predictvalue int(100) DEFAULT NULL ,PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=gbk";
		Connection conn = (Connection) DBConnection.getConnection();
		Statement st = conn.createStatement();
		st.execute(sql);
		st.execute(sqls);
	}

	public static void cleartable() throws SQLException {
		Connection conn = (Connection) DBConnection.getConnection();
		Statement st = conn.createStatement();
		st.executeUpdate("DROP TABLE historyresult");// 清空数据库重复内容
		// st.execute("ALTER table historyresult AUTO_INCREMENT = 1");//
		// 设置初始字段为1
	}

}