package cn.zju.edu.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
	/* 文件操作类 */
	public static final String SUFFIX = ".dat"; // 分割后的文件名后缀

	public static final String allpath = "resultAll.txt";
	public static final String countpath = "resultcount.txt";
	public static final String crawlerpath = "resultcrawler.txt";

	@SuppressWarnings({ "unchecked" })
	// 统计每种词性的词出现次数和每个词分别是什么词性的出现次数
	public static void extractedOther(String sourcePath, String resultPath)
			throws FileNotFoundException, IOException {
		StringBuilder builder = readSource(sourcePath);
		String pattenAttr = "\\/+[a-zA-Z]+";
		String pattenall = "([\u4e00-\u9fa5]+)\\/+[a-zA-Z]+";
		Map<String, Integer> mapattr = countWord(builder, pattenAttr);
		Map<String, Integer> mapall = countWord(builder, pattenall);
		// FileUtil.writefile("\n" + "词性--次数" + "\n", resultPath);
		FileUtil.writefile("=========分割线===========" + "\n", resultPath);
		Iterator<?> iterattr = mapattr.entrySet().iterator();
		while (iterattr.hasNext()) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iterattr
					.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (Integer.parseInt(val.toString()) >= 5) {
				FileUtil.writefile(key.toString().replace("/", "") + " " + val
						+ "\n", resultPath);
			}
		}
		// FileUtil.writefile("\n" + "词--词性--次数" + "\n", resultPath);
		FileUtil.writefile("=========分割线===========" + "\n", resultPath);
		Iterator<?> iterall = mapall.entrySet().iterator();
		while (iterall.hasNext()) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iterall
					.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (Integer.parseInt(val.toString()) >= 5) {
				FileUtil.writefile(key.toString().replaceAll("/", " ") + " "
						+ val + "\n", resultPath);
			}
		}
	}

	// 合并多个文件
	public static final int BUFSIZE = 1024 * 8;

	public static void mergeFiles(String outFile, String[] files) {

		FileChannel outChannel = null;
		System.out.println("Merge " + Arrays.toString(files) + " into "
				+ outFile);
		try {
			outChannel = new FileOutputStream(outFile).getChannel();
			for (String f : files) {
				@SuppressWarnings("resource")
				FileChannel fc = new FileInputStream(f).getChannel();
				ByteBuffer bb = ByteBuffer.allocate(BUFSIZE);
				while (fc.read(bb) != -1) {
					bb.flip();
					outChannel.write(bb);
					bb.clear();
				}
				fc.close();
			}
			System.out.println("合并成功 ");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (outChannel != null) {
					outChannel.close();
				}
			} catch (IOException ignore) {
			}
		}
	}

	public static void resultCut() throws Exception {
		// FileUtil. mergeFiles(path, new
		// String[] {
		// "resultcrawler1.html",
		// "resultcrawler2.html", });
		// resultcrawler.txt过大，需要切分大文件

		String path = allpath;
		File file = new File(path);
		// 清空上一次resultAll.txt结果，避免重复写

		if (file.exists() && file.isFile()) {
			file.delete();
		}
		bigFileCut(crawlerpath);
		System.out.println("去重结果保存在" + allpath + "中" + "\n");
		System.out.println("词数统计成功，结果保存在" + countpath + "中");
		FileUtil.deleteDirectory("htmlfind");
		FileUtil.deleteDirectory("htmlnext");
		// 删除当前目录下后缀为.html的所有文件
		FileUtil.deleteHtml("./");
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.println("是否统计词性出现次数?是:1 否:0\n");
		int flag = scan.nextInt();
		if (flag == 1) {
			FileUtil.extractedOther(allpath, countpath);

			System.out.println("词数统计成功，结果保存在" + countpath + "中");
		}
	}

	// 切分大文件
	private static void bigFileCut(String path) throws Exception, IOException,
			UnsupportedEncodingException {
		Set<String> set = new HashSet<String>();
		long maxsize = 1024 * 1024 * 50;// 1G,超过这个值需要做文件切分
		long size = 1024 * 1024 * 10; // 子文件最大为100M
		File file = new File(path);
		long fileLength = file.length();
		if (size <= 0) {
			size = fileLength / 2;
		}
		// 取得被分割后的小文件的数目
		int num = (fileLength % size != 0) ? (int) (fileLength / size + 1)
				: (int) (fileLength / size);
		if (file.length() >= maxsize) {
			FileUtil.divide(path, size);
			for (int m = 0; m < num; m++) {
				String pathdived = "./htmlfind/text" + m + ".dat";
				System.out.println("开始提取第" + (m + 1) + "个文件……");
				set.addAll(FileUtil.RemoveDuplicate(pathdived));
			}
		} else {
			set.addAll(FileUtil.RemoveDuplicate(path));
		}
		for (String i : set) {
			System.out.println("正在写入" + URLDecoder.decode(i, "utf-8") + "\n");
			FileUtil.writefile(URLDecoder.decode(i, "utf-8") + "\n", allpath);
		}
	}

	public static void deleteHtml(String path) {
		File file = new File(path);// 里面输入特定目录
		File temp = null;
		File[] filelist = file.listFiles();
		for (int i = 0; i < filelist.length; i++) {
			temp = filelist[i];
			if (temp.getName().endsWith("html")) {
				temp.delete();// 删除文件}
			}
		}
	}

	// 统计每个词后面是另一个词时出现次数如我们@去：3
	@SuppressWarnings({ "unchecked" })
	public static void extractedWord(String first, String sourcePath,
			String resultPath) throws IOException {
		StringBuilder builder = readSource(sourcePath);
		String pattenWord = "([\u4e00-\u9fa5]+)";
		Map<String, Integer> mapword = countWord(builder, pattenWord);
		// FileUtil.writefile("\n" + "词--其他词--次数" + "\n", resultPath);
		Iterator<?> iterword = mapword.entrySet().iterator();
		while (iterword.hasNext()) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iterword
					.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (Integer.parseInt(val.toString()) >= 5) {
				if (isKey(first, pattenWord, key) == false) {
					FileUtil.writefile(first + "@" + key + ": " + val + "\n",
							resultPath);
				}
			}

		}
	}

	// 读取要统计词数的文件
	@SuppressWarnings("resource")
	public static StringBuilder readSource(String sourcePath)
			throws FileNotFoundException, IOException {
		File file = new File(sourcePath);
		FileReader fileReader = new FileReader(file);
		BufferedReader reader = new BufferedReader(fileReader);
		StringBuilder builder = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		return builder;
	}

	// 找出key和first的中文，去掉我@我这种格式
	public static boolean isKey(String first, String pattenWord, Object key) {
		Pattern pattern = Pattern.compile(pattenWord);
		Matcher matcher = pattern.matcher(key.toString());
		Matcher matchers = pattern.matcher(first.toString());
		while (matcher.find() && matchers.find()) {
			String keymatch = matcher.group();
			String firstmathc = matchers.group();
			if (keymatch.equals(firstmathc)) {
				return true;
			}
		}
		return false;
	}

	public static Map<String, Integer> countWord(StringBuilder builder,
			String patten) {
		Pattern pattern = Pattern.compile(patten);
		String content = builder.toString();
		Matcher matcher = pattern.matcher(content);
		Map<String, Integer> map = new HashMap<String, Integer>();
		String word = "";
		Integer times = 0;
		while (matcher.find()) {
			word = matcher.group();
			if (map.containsKey(word)) {
				times = map.get(word);
				map.put(word, times + 1);
			} else {
				map.put(word, 1);
			}
		}
		return map;

	}

	// 文件去重
	public static Set<String> RemoveDuplicate(String path) throws IOException,
			UnsupportedEncodingException {
		Set<String> set = new HashSet<String>();// 定义一个set结合，用于去掉文件切分时候的重复词
		List<String> resultall = FileUtil.readfile(path);
		List<String> listTemp = new ArrayList<String>();
		Iterator<String> it = resultall.iterator();
		while (it.hasNext()) {
			String i = it.next();
			if (listTemp.contains(i)) {
				it.remove();
			} else {
				listTemp.add(i);
			}
		}

		for (String i : listTemp) {
			set.add(i);

		}
		return set;

	}

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
		// 根据要分割的数目输出文件
		for (int i = 0; i < num; i++) {
			// 对于前num - 1个小文件，大小都为指定的size
			File outFile = new File("./htmlfind", "text" + i + SUFFIX);
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

	// 读文件
	public static List<String> readfile(String path) throws IOException {
		List<String> list = new ArrayList<String>();
		File file = new File(path);
		FileInputStream s = new FileInputStream(file);

		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new InputStreamReader(s,
				"utf-8"), 5 * 1024 * 1024);
		String tempString = null;

		// 一次读入一行，直到读入null为文件结束
		while ((tempString = reader.readLine()) != null) {

			// 显示行号

			String word = java.net.URLEncoder.encode(tempString, "utf-8");
			list.add(word);
			// System.out.println("正在读取" + tempString + "\n");
		}
		return list;
	}

	// 保存结果

	public static void writefile(String m, String path) {

		try {
			File file = new File(path);
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

	// 创建html目录
	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {// 判断目录是否存在
			// System.out.println("创建目录失败，目标目录已存在！");

			return false;
		}
		if (!destDirName.endsWith(File.separator)) {// 结尾是否以"/"结束
			destDirName = destDirName + File.separator;
		}
		if (dir.mkdirs()) {// 创建目标目录
			// System.out.println("创建目录成功！" + destDirName);
			return true;
		} else {
			// System.out.println("创建目录失败！");
			return false;
		}
	}

	// 清空目录
	public static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	public static void clearFile() {
		deleteDirectory("htmlfind");
		deleteDirectory("htmlnext");
		createDir("htmlfind");
		createDir("htmlnext");
	}
}