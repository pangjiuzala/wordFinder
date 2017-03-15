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
		// 以utf8格式打开文件
		// FileReader fr = new FileReader(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(s,
				"utf8"));
		char buffer[] = new char[(int) file.length()];
		reader.read(buffer);
		return new String(buffer);
	}

	// 判断是否存在dat文件夹，没有的话就创建
	public static void createDir() {
		File file = new File("./dat");
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
	}

	public static final String SUFFIX = ".dat"; // 分割后的文件名后缀

	// 将指定的文件按着给定的文件的字节数进行分割文件，其中name指的是需要进行分割的文件名，size指的是指定的小文件的大小
	public static void divide(String name, long size) throws Exception {
		File file = new File(name);
		if (!file.exists() || (!file.isFile())) {
			throw new Exception("指定文件不存在！");
		}
		// 取得文件的大小
		long fileLength = file.length();
		if (size <= 0) {
			size = fileLength / 2;
		}
		// 取得被分割后的小文件的数目
		int num = (fileLength % size != 0) ? (int) (fileLength / size + 1)
				: (int) (fileLength / size);
		// 存放被分割后的小文件名
		String[] fileNames = new String[num];
		// 输入文件流，即被分割的文件
		FileInputStream in = new FileInputStream(file);
		// 读输入文件流的开始和结束下标
		long end = 0;
		int begin = 0;
		createDir();
		// 根据要分割的数目输出文件
		for (int i = 0; i < num; i++) {
			// 对于前num - 1个小文件，大小都为指定的size
			File outFile = new File("./dat", "text" + i + SUFFIX);
			// 构建小文件的输出流
			FileOutputStream out = new FileOutputStream(outFile);
			// 将结束下标后移size
			end += size;
			end = (end > fileLength) ? fileLength : end;
			// 从输入流中读取字节存储到输出流中
			for (; begin < end; begin++) {
				out.write(in.read());
			}
			out.close();
			fileNames[i] = outFile.getAbsolutePath();
			System.out.println("第" + (i + 1) + "个子文件生成……");

		}
		in.close();
	}

	// public static void main(final String[] args) throws Exception {
	// String name = "text.dat";
	// long size = 1024 * 1024 * 4;// 1K=1024b(字节),切割后每个文件为4M
	// TextDatReader.divide(name, size);
	//
	// }

}