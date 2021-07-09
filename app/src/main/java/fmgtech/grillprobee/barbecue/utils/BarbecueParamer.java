package fmgtech.grillprobee.barbecue.utils;

public class BarbecueParamer {
	private int status;//1表示常规   2 表示定时    3表示温度
	private int type; //常规下烧烤类别
	private String degree; //常规下烧烤类别的状态
	private float temperature = -1; //存储的是摄氏温度值
	private int hour;
	private int min;
	private int second;
	private int workStatus ; //1表示start   2表示stop
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getTemperature() {
		return (int)temperature;
	}
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getWorkStatus() {
		return workStatus;
	}
	public void setWorkStatus(int workStatus) {
		this.workStatus = workStatus;
	}
	public int getSecond() {
		return second;
	}
	public void setSecond(int second) {
		this.second = second;
	}
}
