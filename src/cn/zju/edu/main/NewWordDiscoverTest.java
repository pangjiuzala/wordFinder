package cn.zju.edu.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import cn.zju.edu.common.MessageTrim;
import cn.zju.edu.common.TextDatReader;
import cn.zju.edu.crawlerweb.WordSpider;
import cn.zju.edu.dao.DBHelper;
import cn.zju.edu.index.CnPreviewTextIndexer;
import cn.zju.edu.text.evolution.NewWordDiscover;
import cn.zju.edu.util.DBConnection;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class NewWordDiscoverTest {
	public Connection conn;

	public NewWordDiscoverTest() throws Exception {
		conn = (Connection) DBConnection.getConnection();
		/* this.findPredict(id); */

	}

	public void writefile(String m) {

		try {
			File file = new File("result.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(m);
			bufferWritter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public void FileDivide() throws Exception {
		File filere = new File("result.txt");
		filere.delete();
		Scanner scans = new Scanner(System.in);
		System.out.println("是否启动爬虫?是:1 否:0\n");
		int flags = scans.nextInt();
		if (flags == 1) {
			WordSpider.crawlerword();

		}
		Scanner scan = new Scanner(System.in);
		System.out.println("是否提取QQ信息语料数据?是:1 否:0\n");
		int flag = scan.nextInt();
		if (flag == 1) {
			System.out.println("在QQ中消息管理器右上角-》导出全部消息记录导出为 全部消息记录.txt到当前目录下\n");
			MessageTrim.fileClear("text.dat");
			MessageTrim.MsgExtract();

		}

		System.out.println("请输入您要处理的文件名称:\n");
		String path = scan.next();
		File file = new File(path);
		if (!file.exists() || (!file.isFile())) {
			throw new Exception("指定文件不存在！");
		}

		long maxsize = 1024 * 1024 * 1024;// 1G,超过这个值需要做文件切分
		long size = 1024 * 1024 * 5; // 子文件最大为100M
		long fileLength = file.length();
		if (size <= 0) {
			size = fileLength / 2;
		}
		// 取得被分割后的小文件的数目
		int num = (fileLength % size != 0) ? (int) (fileLength / size + 1)
				: (int) (fileLength / size);
		if (file.length() >= maxsize) {
			System.out.println("文件大小超出1G，是否开始进行文件切割？1:是 0:否\n");

			int t = scan.nextInt();
			if (t == 1) {
				TextDatReader.divide(path, size);
				System.out.println("切割完成\n");
				System.out.println("结果保存在当前目录下的dat文件夹中\n");

			}
			// System.out.println("请输入您要处理的文件序号，例如1代表dat文件架下的text1.dat\n");
			// int m = scans.nextInt();
			for (int m = 0; m < num; m++) {
				String pathdived = "./dat/text" + m + ".dat";
				System.out.println("开始提取第" + (m + 1) + "个文件……");
				discovrWord(pathdived);
			}

		} else {
			System.out.println("开始提取文件……");
			discovrWord(path);
		}

	}

	public void discovrWord(String path) throws Exception {
		String document = TextDatReader.read(path);
		NewWordDiscover discover = new NewWordDiscover();
		Set<String> words = discover.discover(document);
		CnPreviewTextIndexer ci = new CnPreviewTextIndexer(document);
		DBHelper.createtable();
		String s1 = "insert into historyresult (truevalue,predictvalue) values(?,?);";
		PreparedStatement prepStmt = (PreparedStatement) conn
				.prepareStatement(s1);

		// long start = System.currentTimeMillis();
		// System.out.println("耗时: " + (double) document.length()
		// / (System.currentTimeMillis() - start) * 1000);
		System.out.println("新词个数: " + words.size());
		System.out.println("发现的新词:" + "\n");
		// for (String newword : words) {
		// System.out.println(newword + "," + ci.count(newword) + "\n");//
		// 发现新词后，统计每个新词出现的次数
		// writefile(newword + "," + ci.count(newword) + "\n");
		// prepStmt.setString(1, newword);
		// prepStmt.setInt(2, ci.count(newword));
		// prepStmt.executeUpdate();
		// }
		// 填充数据到map中，便于后续排序操作
		Map<String, Integer> map = new TreeMap<String, Integer>();
		for (String newword : words) {
			map.put(newword, ci.count(newword));
		}
		// 通过ArrayList构造函数把map.entrySet()转换成list
		List<Map.Entry<String, Integer>> mappingList = new ArrayList<Map.Entry<String, Integer>>(
				map.entrySet());
		// 通过比较器实现比较排序
		Collections.sort(mappingList,
				new Comparator<Map.Entry<String, Integer>>() {
					@Override
					public int compare(Map.Entry<String, Integer> mapping1,
							Map.Entry<String, Integer> mapping2) {
						// TODO Auto-generated method stub
						return mapping2.getValue().compareTo(
								mapping1.getValue());
					}
				});
		// 将生存的结果按照单词频数排序
		for (Entry<String, Integer> mapping : mappingList) {
			System.out.println(mapping.getKey() + "," + mapping.getValue()
					+ "\n");

			writefile(mapping.getKey() + "," + mapping.getValue() + "\n");
			prepStmt.setString(1, mapping.getKey());
			prepStmt.setInt(2, mapping.getValue());
			prepStmt.executeUpdate();
		}
	}

	public static void main(String[] args) throws Exception {
		NewWordDiscoverTest nd = new NewWordDiscoverTest();
		nd.FileDivide();
	}

}