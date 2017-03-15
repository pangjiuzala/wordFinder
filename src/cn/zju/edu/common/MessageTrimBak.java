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
 * @author fightingliu ����Ϣ����.txt��һ����������
 */

public class MessageTrimBak {

	// �ж��ַ����Ƿ���ʼ��prefix
	private static final String baseFile = "��Ϣ����.txt";
	private static final String fullPath2 = "messgae.txt";

	private static final String nickPath = "�ǳƴ�.txt";

	private static boolean IsstartWith(String text, String prefix) {

		return text.contains(prefix);

	}

	// ����ظ��ļ�
	private void fileClear(String path) {
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
		String strRegex = "\\[*[\u4e00-\u9fa5]*\\]|\\[*[0-9a-zA-Z]*\\]|\\[*^\\d+$*\\]";
		Pattern pattern = Pattern.compile(strRegex);

		if (!IsstartWith(line, "��Ϣ����:") && !IsstartWith(line, "==")
				&& !IsstartWith(line, "��Ϣ����:") && !IsstartWith(line, "��Ϣ��¼")
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

	// �ж��Ƿ�Ϊ�����ʽ
	private boolean isEmail(String line) {
		// String email =
		// "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]([a-z0-9A-Z]+@(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		String email = "^([a-z0-9-]{1,200}.){1,5}[a-z]{1,6}+[\\S\\s]*?$";
		Pattern pattern = Pattern.compile(email);
		Matcher m = pattern.matcher(line);
		return m.find();

	}

	// �ж��Ƿ�Ϊ�ǳ�,�ǳƺ�ض�Ϊ�ո�����ǳ��а����ո�Ҫ�趨��ֵ���ݶ�[0.25,0.75](������3���ո�)Ϊ��ֵ
	private String judgeNick(String first) {
		String result = "";
		int blankcount = 0;
		double rate = 0.00;
		int k = 0;
		for (int j = 0; j < first.length(); j++) {
			char item = first.charAt(j);
			if (item == ' ') {
				blankcount++;// ͳ�ƿո���ֵĸ���

				rate = blankcount / 4.00;
				if (rate >= 0.25 && rate <= 0.75) {

					k = j;// ��¼���һ���ո��λ��
					result = first.substring(0, k)

					// + "(����Ϊ�ǳ�)"
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
		// result = first.substring(0, k) + "(����Ϊ�ǳ�)" + "\n";
		// break;
		// default:// û�пո���߿ո���������ֵ��������Ϊ�����ǳ�
		// break;
		// }
		return result;
	}

	// ��ȡ�ַ����е��ǳƴ�
	private String getNick(String line) {
		String nickResult = "";
		Set<String> set = new TreeSet<String>();// ����set��������ÿһ���е��ظ���
		if (IsstartWith(line, "@")) {// ����@��ʼ����
			String[] str = line.split("@");
			for (int i = 0; i < str.length; i++) {
				if (!isEmail(str[i])) {
					// nickResult += judgeNick(str[i]);
					if (!IsstartWith(judgeNick(str[i]), "ȫ���Ա")) {// ȥ��ȫ���Ա
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

	// �ҳ������¼�е��ǳƴ�
	private void getNickfromFile(String path) throws IOException {
		fileClear(nickPath);
		String fileResult = "";
		System.out.println("��ʼ�ҳ��ǳƴʡ���");
		File file = new File(path);
		FileInputStream s = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(s,
				"gbk"));
		Set<String> setnick = new TreeSet<String>();// ����set���������ظ���
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {
			// fileResult += getNick(line);
			setnick.add(getNick(line));
		}

		for (String word : setnick) {
			fileResult += word;
		}
		write("����Ϊ�ǳƵĴ�(��ֵ[0.25,0.75]):" + fileResult, nickPath);
		System.out.println("����Ϊ�ǳƵĴ�(��ֵ[0.25,0.75]):" + fileResult);
		reader.close();
	}

	public static void main(String[] args) throws IOException {
		MessageTrimBak mt = new MessageTrimBak();
		mt.read(baseFile);
		// String line =
		// "@��  �� fightingliuлл,��ʱ���ǱߵĹ�˾¼ȡ�ˣ���������̫Զ��ûȥ�Ǳ��ϰ࣬�����ڵ�������ϰ���123@163.com@�� Դ 6�ķ���@��  �� fightingliu@�� ���� ������ �� �� hahaha@163.com �绰��΢�ţ��� 13508962965 лл��";
		// System.out.println(mt.getNick(line));
		// // �ҳ�message.txt�е��ǳƴ�
		// String line = "163.com �绰��΢�ţ��� 13508962965 лл��";
		// System.out.println(mt.getNick(line));
		mt.getNickfromFile(fullPath2);

	}

}
