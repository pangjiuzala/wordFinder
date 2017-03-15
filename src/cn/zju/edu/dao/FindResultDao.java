package cn.zju.edu.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import cn.zju.edu.model.Predict;
import cn.zju.edu.util.DBConnection;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

/**
 * @author liuxing
 * @category query history data of stock,and transfer data to history.jsp
 */
public class FindResultDao {
	private Connection conn;
	private static Predict result = null;

	public FindResultDao() throws Exception {
		conn = (Connection) DBConnection.getConnection();
		// saveResult();
		/* this.findPredict(id); */

	}

	public void initpredict(int ididinit) throws SQLException {
		Iterator<Predict> iterators = this.findPredict(ididinit).iterator();
		if (iterators.hasNext()) { // 如果有下一个值，进入循环
			result = iterators.next(); // 得到迭代器中下一个值 返回String类型
			// 输出结果
		}
	}

	public String gettrue(int id) throws SQLException {

		initpredict(id);
		return result.getTruevalue();
	}

	public int getpredict(int id) throws SQLException {
		initpredict(id);
		return result.getPredictvalue();
	}

	// public void saveResult() throws SQLException {
	// DataHelper.createtableresult();
	// PreparedStatement pstmt = null;
	// String sql =
	// "insert into result(truevalue,predictvalue) SELECT truevalue,predictvalue from historyresult order by predictvalue desc";
	// pstmt = (PreparedStatement) conn.prepareStatement(sql);
	// pstmt.executeUpdate();
	//
	// }

	public ArrayList<Predict> findPredict(int idf) {
		ArrayList<Predict> predictlist = new ArrayList<Predict>();
		PreparedStatement pstmt = null;
		// String sql =
		// "insert into result(SELECT *  FROM historyresult order by predictvalue desc)";
		// String sqls = "SELECT *  FROM result where id=" + idf;
		String sqls = "SELECT *  FROM historyresult where id=" + idf;
		try {
			// pstmts = (PreparedStatement) conn.prepareStatement(sql);
			pstmt = (PreparedStatement) conn.prepareStatement(sqls);
			// pstmts.executeUpdate();
			ResultSet rs = (ResultSet) pstmt.executeQuery();
			while (rs.next()) {
				System.out.println();
				Predict pd = new Predict();
				// pd.setId(rs.getInt("id"));
				pd.setTruevalue(rs.getString("truevalue"));
				pd.setPredictvalue(rs.getInt("predictvalue"));

				/*
				 * System.out.println(pd.getId() + "," + pd.getTruevalue() + ","
				 * + pd.getPredictvalue());
				 */
				predictlist.add(pd);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return predictlist;
	}

	// public static void main(String[] args) throws Exception {
	// FindResultDao pds = new FindResultDao();
	// // System.out.println(pds.gettrue(1));
	// System.out.println(pds.getpredict(1));
	// // System.out.println(pds.gettrue(2));
	// System.out.println(pds.getpredict(2));
	//
	// /*
	// * pds.findPredict(1); pds.findPredict(2);
	// */
	// }

}
