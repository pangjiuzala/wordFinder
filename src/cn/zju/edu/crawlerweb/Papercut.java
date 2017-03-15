package cn.zju.edu.crawlerweb;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import cn.zju.edu.util.FileUtil;

public class Papercut {
	public static void main(String[] args) throws IOException {
		List<String> list = FileUtil.readfile("毕设论文_21551060_刘兴.txt");
		StringBuffer sb = new StringBuffer();
		for (String result : list) {
			sb.append(URLDecoder.decode(result, "utf-8").replaceAll(" ", ""));
		}
//		String[] m = sb.toString().replaceAll(" ", "").replaceAll("　　　", "")
//				.split("");
//		for (String t : m) {
//			if (t != " ") {
//				// System.out.println(t);
//				count++;
//			}
//		}
		System.out.println(sb.toString());
//		System.out.println("字数:" + "\n" + count + "\n" + "文本:" + "\n"
//				+ sb.toString().replaceAll(" +", "").replaceAll("　　　", ""));
	}

}
