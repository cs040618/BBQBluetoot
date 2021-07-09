package fmgtech.grillprobee.barbecue.utils;

public class BleConnectInfo {
	private int position;
	private boolean connectStatus;
	
	public BleConnectInfo(int position,boolean connectStatus){
		this.position = position;
		this.connectStatus = connectStatus;
	}
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public boolean isConnectStatus() {
		return connectStatus;
	}
	public void setConnectStatus(boolean connectStatus) {
		this.connectStatus = connectStatus;
	}

}
