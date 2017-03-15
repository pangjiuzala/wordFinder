package cn.zju.edu.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fightingliu 对消息处理.txt进一步消除冗余
 */

public class MessageTrimBak {

	// 判读字符串是否起始于prefix
	private static final String baseFile = "消息处理.txt";
	private static final String fullPath2 = "messgae.txt";

	private static final String nickPath = "昵称词.txt";

	private static boolean IsstartWith(String text, String prefix) {

		return text.contains(prefix);

	}

	// 清除重复文件
	private void fileClear(String path) {
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
	private boolean IsstartWithTime(String text) {
		if (text.length() >= 20 && IsTime(text.substring(0, 10)))
			return true;
		return false;
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
		bufferWritter.write(text + "\n");
		bufferWritter.close();
	}

	private void judgeNouse(String line) throws IOException {
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
		String strRegex = "\\[*[\u4e00-\u9fa5]*\\]|\\[*[0-9a-zA-Z]*\\]|\\[*^\\d+$*\\]";
		Pattern pattern = Pattern.compile(strRegex);

		if (!IsstartWith(line, "消息分组:") && !IsstartWith(line, "==")
				&& !IsstartWith(line, "消息对象:") && !IsstartWith(line, "消息记录")
				&& !IsstartWith(line, msg1) && !IsstartWith(line, msg2)
				&& !IsstartWith(line, msg3) && !IsstartWith(line, msg4)
				&& !IsstartWith(line, msg5) && !IsstartWith(line, msg6)
				&& !IsstartWith(line, msg7) && !IsstartWith(line, msg8)
				&& !IsstartWith(line, msg9) && !IsstartWith(line, msg10)
				&& !IsstartWith(line, msg11) && !IsstartWithTime(line)) {
			Matcher m = pattern.matcher(line);
			String result = m.replaceAll("\n").replaceAll("\\[]", "\n");
			// System.out.println(result);
			write(result, fullPath2);

		}
	}

	private void read(String path) throws IOException {
		fileClear(fullPath2);
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

	// 判断是否为邮箱格式
	private boolean isEmail(String line) {
		// String email =
		// "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]([a-z0-9A-Z]+@(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		String email = "^([a-z0-9-]{1,200}.){1,5}[a-z]{1,6}+[\\S\\s]*?$";
		Pattern pattern = Pattern.compile(email);
		Matcher m = pattern.matcher(line);
		return m.find();

	}

	// 判断是否为昵称,昵称后必定为空格，如果昵称中包含空格要设定阈值，暂定[0.25,0.75](不超过3个空格)为阈值
	private String judgeNick(String first) {
		String result = "";
		int blankcount = 0;
		double rate = 0.00;
		int k = 0;
		for (int j = 0; j < first.length(); j++) {
			char item = first.charAt(j);
			if (item == ' ') {
				blankcount++;// 统计空格出现的个数

				rate = blankcount / 4.00;
				if (rate >= 0.25 && rate <= 0.75) {

					k = j;// 记录最后一个空格的位置
					result = first.substring(0, k)

					// + "(可能为昵称)"
							+ "\n";

				}

			}

		}
		// System.out.println(rate);

		// switch (blankcount) {
		// case 1:
		// case 2:
		// result = first + "\n";
		// break;
		// case 3:
		// result = first.substring(0, k) + "(可能为昵称)" + "\n";
		// break;
		// default:// 没有空格或者空格数超过阈值，可以认为不是昵称
		// break;
		// }
		return result;
	}

	// 获取字符串中的昵称词
	private String getNick(String line) {
		String nickResult = "";
		Set<String> set = new TreeSet<String>();// 利用set集合消除每一行中的重复词
		if (IsstartWith(line, "@")) {// 包含@开始处理
			String[] str = line.split("@");
			for (int i = 0; i < str.length; i++) {
				if (!isEmail(str[i])) {
					// nickResult += judgeNick(str[i]);
					if (!IsstartWith(judgeNick(str[i]), "全体成员")) {// 去掉全体成员
						set.add(judgeNick(str[i]));
					}

				}
			}
			for (String word : set) {
				nickResult += word;
			}
		}
		return nickResult;
	}

	// 找出聊天记录中的昵称词
	private void getNickfromFile(String path) throws IOException {
		fileClear(nickPath);
		String fileResult = "";
		System.out.println("开始找出昵称词……");
		File file = new File(path);
		FileInputStream s = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(s,
				"gbk"));
		Set<String> setnick = new TreeSet<String>();// 利用set集合消除重复词
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {
			// fileResult += getNick(line);
			setnick.add(getNick(line));
		}

		for (String word : setnick) {
			fileResult += word;
		}
		write("可能为昵称的词(阈值[0.25,0.75]):" + fileResult, nickPath);
		System.out.println("可能为昵称的词(阈值[0.25,0.75]):" + fileResult);
		reader.close();
	}

	public static void main(String[] args) throws IOException {
		MessageTrimBak mt = new MessageTrimBak();
		mt.read(baseFile);
		// String line =
		// "@心  诚 fightingliu谢谢,那时候被那边的公司录取了，后来觉得太远，没去那边上班，现在在地王这边上班了123@163.com@惠 源 6的飞起@心  诚 fightingliu@在 流年 里忘记 花 开 hahaha@163.com 电话（微信）： 13508962965 谢谢！";
		// System.out.println(mt.getNick(line));
		// // 找出message.txt中的昵称词
		// String line = "163.com 电话（微信）： 13508962965 谢谢！";
		// System.out.println(mt.getNick(line));
		mt.getNickfromFile(fullPath2);

	}

}
