package cn.zju.edu.model;

public class Predict {
	private int id;
	private String truevalue;
	private int predictvalue;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTruevalue() {
		return truevalue;
	}

	public void setTruevalue(String truevalue) {
		this.truevalue = truevalue;
	}

	public int getPredictvalue() {
		return predictvalue;
	}

	public void setPredictvalue(int predictvalue) {
		this.predictvalue = predictvalue;
	}

}
