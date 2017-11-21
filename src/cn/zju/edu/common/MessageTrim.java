package cn.zju.edu.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Xingliu
 * @category 提取消息记录到text.txt
 */
public class MessageTrim {

	// 判读字符串是否起始于prefix
	private static final String fullPath = "message.dat";
	private static final String baseFile = "全部消息记录.txt";

	private boolean IsStartWith(String text, String prefix) {

		return text.contains(prefix);

	}

	// 清除重复文件
	public static void fileClear(String path) {
		File files = new File(path);
		// if file doesnt exists, then create it
		if (files.exists()) {
			files.delete();
		}
	}

	// 判断字符串是否是时间格式
	// \\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2} 是判断yyyy-MM-dd hh:mm:ss格式
	// \\d{4}-\\d{1,2}-\\d{1,2} 是判断yyyy-MM-dd格式
	private boolean IsTime(String text) {

		if (text.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}")
				|| text.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
			return true;

		}
		return false;

	}

	// 判读字符串是否包含时间格式
	private boolean IsStartWithTime(String text) {
		if (text.length() >= 20 && IsTime(text.substring(0, 10)))
			return true;
		return false;
	}

	public void GBKtoUtf8(String file) throws Exception {
		BufferedReader bre = null;
		BufferedWriter bw = null;// 定义一个流

		bre = new BufferedReader(new FileReader(file));// 此时获取到的bre就是整个文件的缓存流
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				"text.dat"), Charset.forName("UTF-8")));// 确认流的输出文件和编码格式，此过程创建了“test.txt”实例
		String str;
		while ((str = bre.readLine()) != null) // 判断最后一行不存在，为空结束循环
		{
			bw.write(str + "\r\n");
		}
		;
		bw.close();// 关闭流
		bre.close();// 关闭流
	}

	private void write(String text, String path) throws IOException {
		File file = new File(path);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		// true = append file
		FileWriter fileWritter = new FileWriter(file.getName(), true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(text + "\r\n");
		bufferWritter.close();
	}

	public void judgeNouse(String lines) throws IOException {
		String line = lines.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll(" ", "");
		String msg1 = "我们已经是好友了";
		String msg2 = "您取消了";
		String msg3 = "您好，我现在有事不在";
		String msg4 = "您发送了";
		String msg5 = "对方已成功接收了您发送的离线文件";
		String msg6 = "对方取消";
		String msg7 = "[QQ红包]我发了一个“口令红包”";
		String msg8 = "QQ请使用新版手机QQ查收红包";
		String msg9 = "分享";
		String msg10 = "加入本群";
		String msg11 = "群主已";
		// 匹配[字符]类型的字符串，如"[表情] [123] [abc]之类的"
		String strRegex = "\\[*[\u4e00-\u9fa5]*\\]|\\[*[0-9a-zA-Z]*\\]|\\[*^\\d+$*\\]|\\[.*?\\]";
		Pattern pattern = Pattern.compile(strRegex);

		if (!IsStartWith(line, "消息分组:") && !IsStartWith(line, "==")
				&& !IsStartWith(line, "消息对象:") && !IsStartWith(line, "消息记录")
				&& !IsStartWith(line, msg1) && !IsStartWith(line, msg2)
				&& !IsStartWith(line, msg3) && !IsStartWith(line, msg4)
				&& !IsStartWith(line, msg5) && !IsStartWith(line, msg6)
				&& !IsStartWith(line, msg7) && !IsStartWith(line, msg8)
				&& !IsStartWith(line, msg9) && !IsStartWith(line, msg10)
				&& !IsStartWith(line, msg11) && !IsStartWithTime(line)) {
			Matcher m = pattern.matcher(line);
			String result = m.replaceAll("").replaceAll("\r\n", "");
			//去除空行
			if (!result.isEmpty()) {
				System.out.println(result);
				write(result, fullPath);
			}
		}
	}

	// public static void main(String[] args) throws IOException {
	// new MessageTrim()
	// .judgeNouse("[sh000001 上证指数]111便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][sh000001 上证指数][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][篮球][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便][便便");
	// }

	private void read(String path) throws IOException {
		fileClear(fullPath);
		System.out.println("处理中……");
		File file = new File(path);
		FileInputStream s = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(s,
				"utf8"));

		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {
			judgeNouse(line);

		}
		reader.close();
	}

	public static void MsgExtract() throws Exception {
		MessageTrim mt = new MessageTrim();
		mt.read(baseFile);
		mt.GBKtoUtf8(fullPath);
		fileClear(fullPath);
		System.out.println("提取消息语料成功，结果保存在text.dat");

	}
}
