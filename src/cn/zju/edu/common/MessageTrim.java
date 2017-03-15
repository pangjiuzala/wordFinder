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
 * @category ��ȡ��Ϣ��¼��text.txt
 */
public class MessageTrim {

	// �ж��ַ����Ƿ���ʼ��prefix
	private static final String fullPath = "message.dat";
	private static final String baseFile = "ȫ����Ϣ��¼1.txt";

	private boolean IsStartWith(String text, String prefix) {

		return text.contains(prefix);

	}

	// ����ظ��ļ�
	public static void fileClear(String path) {
		File files = new File(path);
		// if file doesnt exists, then create it
		if (files.exists()) {
			files.delete();
		}
	}

	// �ж��ַ����Ƿ���ʱ���ʽ
	// \\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2} ���ж�yyyy-MM-dd hh:mm:ss��ʽ
	// \\d{4}-\\d{1,2}-\\d{1,2} ���ж�yyyy-MM-dd��ʽ
	private boolean IsTime(String text) {

		if (text.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}")
				|| text.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
			return true;

		}
		return false;

	}

	// �ж��ַ����Ƿ����ʱ���ʽ
	private boolean IsStartWithTime(String text) {
		if (text.length() >= 20 && IsTime(text.substring(0, 10)))
			return true;
		return false;
	}

	public void GBKtoUtf8(String file) throws Exception {
		BufferedReader bre = null;
		BufferedWriter bw = null;// ����һ����

		bre = new BufferedReader(new FileReader(file));// ��ʱ��ȡ����bre���������ļ��Ļ�����
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				"text.dat"), Charset.forName("UTF-8")));// ȷ����������ļ��ͱ����ʽ���˹��̴����ˡ�test.txt��ʵ��
		String str;
		while ((str = bre.readLine()) != null) // �ж����һ�в����ڣ�Ϊ�ս���ѭ��
		{
			bw.write(str + "\r\n");
		}
		;
		bw.close();// �ر���
		bre.close();// �ر���
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
		String line = lines.replaceAll("��", "[").replaceAll("��", "]")
				.replaceAll(" ", "");
		String msg1 = "�����Ѿ��Ǻ�����";
		String msg2 = "��ȡ����";
		String msg3 = "���ã����������²���";
		String msg4 = "��������";
		String msg5 = "�Է��ѳɹ������������͵������ļ�";
		String msg6 = "�Է�ȡ��";
		String msg7 = "[QQ���]�ҷ���һ������������";
		String msg8 = "QQ��ʹ���°��ֻ�QQ���պ��";
		String msg9 = "����";
		String msg10 = "���뱾Ⱥ";
		String msg11 = "Ⱥ����";
		// ƥ��[�ַ�]���͵��ַ�������"[����] [123] [abc]֮���"
		String strRegex = "\\[*[\u4e00-\u9fa5]*\\]|\\[*[0-9a-zA-Z]*\\]|\\[*^\\d+$*\\]|\\[.*?\\]";
		Pattern pattern = Pattern.compile(strRegex);

		if (!IsStartWith(line, "��Ϣ����:") && !IsStartWith(line, "==")
				&& !IsStartWith(line, "��Ϣ����:") && !IsStartWith(line, "��Ϣ��¼")
				&& !IsStartWith(line, msg1) && !IsStartWith(line, msg2)
				&& !IsStartWith(line, msg3) && !IsStartWith(line, msg4)
				&& !IsStartWith(line, msg5) && !IsStartWith(line, msg6)
				&& !IsStartWith(line, msg7) && !IsStartWith(line, msg8)
				&& !IsStartWith(line, msg9) && !IsStartWith(line, msg10)
				&& !IsStartWith(line, msg11) && !IsStartWithTime(line)) {
			Matcher m = pattern.matcher(line);
			String result = m.replaceAll("").replaceAll("\r\n", "");
			System.out.println(result);
			write(result, fullPath);

		}
	}

	// public static void main(String[] args) throws IOException {
	// new MessageTrim()
	// .judgeNouse("[sh000001 ��ָ֤��]111��][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][sh000001 ��ָ֤��][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][����][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���][���");
	// }

	private void read(String path) throws IOException {
		fileClear(fullPath);
		System.out.println("�����С���");
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
		System.out.println("��ȡ��Ϣ���ϳɹ������������text.dat");

	}
}
