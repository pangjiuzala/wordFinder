package cn.zju.edu.main;

import java.util.Vector;

import cn.zju.edu.text.participle.MechanicalParticiple;

public class ParticipleTest {

	private static String document = "我是中国人";

	public static void main(String args[]) {
		MechanicalParticiple participle = new MechanicalParticiple();
		Vector<String> vec = participle.partition(document);
		System.out.println(vec);
	}
}
