package fmgtech.grillprobee.barbecue.utils;

public class DeviceRecord {
	public String name;
	public String mac;
    public int position;

    public DeviceRecord(String name,String mac,int position) {
          this.name = name;
          this.mac = mac;
          this.position = position;
      }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
      

}
