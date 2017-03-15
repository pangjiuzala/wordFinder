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
		System.out.println("�Ƿ���������?��:1 ��:0\n");
		int flags = scans.nextInt();
		if (flags == 1) {
			WordSpider.crawlerword();

		}
		Scanner scan = new Scanner(System.in);
		System.out.println("�Ƿ���ȡQQ��Ϣ��������?��:1 ��:0\n");
		int flag = scan.nextInt();
		if (flag == 1) {
			System.out.println("��QQ����Ϣ���������Ͻ�-������ȫ����Ϣ��¼����Ϊ ȫ����Ϣ��¼.txt����ǰĿ¼��\n");
			MessageTrim.fileClear("text.dat");
			MessageTrim.MsgExtract();

		}

		System.out.println("��������Ҫ������ļ�����:\n");
		String path = scan.next();
		File file = new File(path);
		if (!file.exists() || (!file.isFile())) {
			throw new Exception("ָ���ļ������ڣ�");
		}

		long maxsize = 1024 * 1024 * 1024;// 1G,�������ֵ��Ҫ���ļ��з�
		long size = 1024 * 1024 * 5; // ���ļ����Ϊ100M
		long fileLength = file.length();
		if (size <= 0) {
			size = fileLength / 2;
		}
		// ȡ�ñ��ָ���С�ļ�����Ŀ
		int num = (fileLength % size != 0) ? (int) (fileLength / size + 1)
				: (int) (fileLength / size);
		if (file.length() >= maxsize) {
			System.out.println("�ļ���С����1G���Ƿ�ʼ�����ļ��и1:�� 0:��\n");

			int t = scan.nextInt();
			if (t == 1) {
				TextDatReader.divide(path, size);
				System.out.println("�и����\n");
				System.out.println("��������ڵ�ǰĿ¼�µ�dat�ļ�����\n");

			}
			// System.out.println("��������Ҫ������ļ���ţ�����1����dat�ļ����µ�text1.dat\n");
			// int m = scans.nextInt();
			for (int m = 0; m < num; m++) {
				String pathdived = "./dat/text" + m + ".dat";
				System.out.println("��ʼ��ȡ��" + (m + 1) + "���ļ�����");
				discovrWord(pathdived);
			}

		} else {
			System.out.println("��ʼ��ȡ�ļ�����");
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
		// System.out.println("��ʱ: " + (double) document.length()
		// / (System.currentTimeMillis() - start) * 1000);
		System.out.println("�´ʸ���: " + words.size());
		System.out.println("���ֵ��´�:" + "\n");
		// for (String newword : words) {
		// System.out.println(newword + "," + ci.count(newword) + "\n");//
		// �����´ʺ�ͳ��ÿ���´ʳ��ֵĴ���
		// writefile(newword + "," + ci.count(newword) + "\n");
		// prepStmt.setString(1, newword);
		// prepStmt.setInt(2, ci.count(newword));
		// prepStmt.executeUpdate();
		// }
		// ������ݵ�map�У����ں����������
		Map<String, Integer> map = new TreeMap<String, Integer>();
		for (String newword : words) {
			map.put(newword, ci.count(newword));
		}
		// ͨ��ArrayList���캯����map.entrySet()ת����list
		List<Map.Entry<String, Integer>> mappingList = new ArrayList<Map.Entry<String, Integer>>(
				map.entrySet());
		// ͨ���Ƚ���ʵ�ֱȽ�����
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
		// ������Ľ�����յ���Ƶ������
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