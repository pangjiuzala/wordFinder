package cn.zju.edu.text.selector;

public interface TextSelector {
	public boolean end();

	public void select();

	public String next();

	public int getCurPos();

}
