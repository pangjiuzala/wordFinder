package cn.zju.edu.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextDatReader {
	// public static String read(String path) throws IOException {
	// File file = new File(path);
	// FileReader reader = new FileReader(file);
	// char buffer[] = new char[(int) file.length()];
	// reader.read(buffer);
	// return new String(buffer);
	// }
	@SuppressWarnings("resource")
	public static String read(String path) throws IOException {
		File file = new File(path);
		FileInputStream s = new FileInputStream(file);
		// ��utf8��ʽ���ļ�
		// FileReader fr = new FileReader(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(s,
				"utf8"));
		char buffer[] = new char[(int) file.length()];
		reader.read(buffer);
		return new String(buffer);
	}

	// �ж��Ƿ����dat�ļ��У�û�еĻ��ʹ���
	public static void createDir() {
		File file = new File("./dat");
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
	}

	public static final String SUFFIX = ".dat"; // �ָ����ļ�����׺

	// ��ָ�����ļ����Ÿ������ļ����ֽ������зָ��ļ�������nameָ������Ҫ���зָ���ļ�����sizeָ����ָ����С�ļ��Ĵ�С
	public static void divide(String name, long size) throws Exception {
		File file = new File(name);
		if (!file.exists() || (!file.isFile())) {
			throw new Exception("ָ���ļ������ڣ�");
		}
		// ȡ���ļ��Ĵ�С
		long fileLength = file.length();
		if (size <= 0) {
			size = fileLength / 2;
		}
		// ȡ�ñ��ָ���С�ļ�����Ŀ
		int num = (fileLength % size != 0) ? (int) (fileLength / size + 1)
				: (int) (fileLength / size);
		// ��ű��ָ���С�ļ���
		String[] fileNames = new String[num];
		// �����ļ����������ָ���ļ�
		FileInputStream in = new FileInputStream(file);
		// �������ļ����Ŀ�ʼ�ͽ����±�
		long end = 0;
		int begin = 0;
		createDir();
		// ����Ҫ�ָ����Ŀ����ļ�
		for (int i = 0; i < num; i++) {
			// ����ǰnum - 1��С�ļ�����С��Ϊָ����size
			File outFile = new File("./dat", "text" + i + SUFFIX);
			// ����С�ļ��������
			FileOutputStream out = new FileOutputStream(outFile);
			// �������±����size
			end += size;
			end = (end > fileLength) ? fileLength : end;
			// ���������ж�ȡ�ֽڴ洢���������
			for (; begin < end; begin++) {
				out.write(in.read());
			}
			out.close();
			fileNames[i] = outFile.getAbsolutePath();
			System.out.println("��" + (i + 1) + "�����ļ����ɡ���");

		}
		in.close();
	}

	// public static void main(final String[] args) throws Exception {
	// String name = "text.dat";
	// long size = 1024 * 1024 * 4;// 1K=1024b(�ֽ�),�и��ÿ���ļ�Ϊ4M
	// TextDatReader.divide(name, size);
	//
	// }

}