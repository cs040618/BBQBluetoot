package fmgtech.grillprobee.barbecue.utils;

public class Hex {
	/**
	 * 用于建立十六进制字符的输出的小写字符数组
	 */
	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	/**
	 * 用于建立十六进制字符的输出的大写字符数组
	 */
	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * 将字节数组转换为十六进制字符数组
	 * 
	 * @param data
	 *            byte[]
	 * @return 十六进制char[]
	 */
	public static char[] encodeHex(byte[] data) {
		return encodeHex(data, true);
	}

	/**
	 * 将字节数组转换为十六进制字符数组
	 * 
	 * @param data
	 *            byte[]
	 * @param toLowerCase
	 *            <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
	 * @return 十六进制char[]
	 */
	public static char[] encodeHex(byte[] data, boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * 将字节数组转换为十六进制字符数组
	 * 
	 * @param data
	 *            byte[]
	 * @param toDigits
	 *            用于控制输出的char[]
	 * @return 十六进制char[]
	 */
	protected static char[] encodeHex(byte[] data, char[] toDigits) {
		int l = data.length;
		char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return out;
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 * 
	 * @param data
	 *            byte[]
	 * @return 十六进制String
	 */
	public static String encodeHexStr(byte[] data) {
		return encodeHexStr(data, true);
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 * 
	 * @param data
	 *            byte[]
	 * @param toLowerCase
	 *            <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
	 * @return 十六进制String
	 */
	public static String encodeHexStr(byte[] data, boolean toLowerCase) {
		return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 * 
	 * @param data
	 *            byte[]
	 * @param toDigits
	 *            用于控制输出的char[]
	 * @return 十六进制String
	 */
	protected static String encodeHexStr(byte[] data, char[] toDigits) {
		return new String(encodeHex(data, toDigits));
	}

	/**
	 * 将十六进制字符数组转换为字节数组
	 * 
	 * @param data
	 *            十六进制char[]
	 * @return byte[]
	 * @throws RuntimeException
	 *             如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
	 */
	public static byte[] decodeHex(char[] data) {
		int len = data.length;
		if ((len & 0x01) != 0) {
			throw new RuntimeException("Odd number of characters.");
		}
		byte[] out = new byte[len >> 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; j < len; i++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f = f | toDigit(data[j], j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}
		return out;
	}

	/**
	 * 将十六进制字符转换成一个整数
	 * 
	 * @param ch
	 *            十六进制char
	 * @param index
	 *            十六进制字符在字符数组中的位置
	 * @return 一个整数
	 * @throws RuntimeException
	 *             当ch不是一个合法的十六进制字符时，抛出运行时异常
	 */
	protected static int toDigit(char ch, int index) {
		int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new RuntimeException("Illegal hexadecimal character " + ch
					+ " at index " + index);
		}
		return digit;
	}

	/**
	 * byte数组转16进制字符串
	 * 
	 * @param src
	 * @return
	 */

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 16进制字符串转byte数组
	 * 
	 * @param src
	 * @return
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 二进制转换为ASCII
	 * 
	 * @param hex
	 * @return
	 */
	public static String convertHexToString(byte[] data) {
		String hex = bytesToHexString(data);
		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < hex.length() - 1; i += 2) {
			String output = hex.substring(i, (i + 2));
			int decimal = Integer.parseInt(output, 16);
			sb.append((char) decimal);
			temp.append(decimal);
		}
		return sb.toString();
	}

	// 将二进制字符串转换回字节
	public static byte bit2byte(String bString) {
		byte result = 0;
		for (int i = bString.length() - 1, j = 0; i >= 0; i--, j++) {
			result += (Byte.parseByte(bString.charAt(i) + "") * Math.pow(2, j));
		}
		return result;
	}

	public static byte[] intToByteArray(final int integer) {
		int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer
				: integer)) / 8;
		byte[] byteArray = new byte[4];
		for (int n = 0; n < byteNum; n++)
			byteArray[3 - n] = (byte) (integer >>> (n * 8));
		return (byteArray);
	}
	
	/**
	 * 倒序
	 * @return
	 */
	public static byte[] changeByteArray(byte[] value){
		if(value.length==4){
			return new byte[]{value[3],value[2],value[1],value[0]};
		}else if(value.length==2){
			return new byte[]{value[1],value[0]};
		}else{
			return value;
		}
	}
	
	
	public static byte[] shortToByteArray(short s) {
		  byte[] shortBuf = new byte[2];
		  for(int i=0;i<2;i++) {
		     int offset = (shortBuf.length - 1 -i)*8;
		     shortBuf[i] = (byte)((s>>>offset)&0xff);
		  }
		  return shortBuf;
		 }

	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}
	
	
	 public static short byteArrayToShort(byte[] b) {  
	        short s = 0;  
	        short s0 = (short) (b[0] & 0xff);// 最低位  
	        short s1 = (short) (b[1] & 0xff);  
	        s1 <<= 8;  
	        s = (short) (s0 | s1);  
	        return s;  
	    }  
	
	
	 public static String byteToBit(byte b){
		 return ""+(byte)((b>>7)&01)+
				 (byte)((b>>6)&01)+
				 (byte)((b>>5)&01)+
				 (byte)((b>>4)&01)+
				 (byte)((b>>3)&01)+
				 (byte)((b>>2)&01)+
				 (byte)((b>>1)&01)+
				 (byte)((b>>0)&01);
	 }
	 
	 public static String byteHighBit(byte b){
		 return ""+(byte)((b>>7)&01);
	 }
	 
	 //
	 public static byte bitToByte(String bitStr){
		 int re,len;
		 if(null==bitStr){
			 return 0;
		 }
		 len = bitStr.length();
		 if(len!=8){
			return 0; 
		 }
		 if(bitStr.charAt(0)=='0'){
			 re = Integer.parseInt(bitStr,2);
		 }else{
			 re = Integer.parseInt(bitStr,2)-256;
		 }
		 return (byte)re;
	 }
	 
	 
	 public static int zhenIndex(byte b){
		 String bitStr = byteToBit(b);
		 int re;
		 if(bitStr.charAt(0)=='0'){
			 re = Integer.parseInt(bitStr,2);
		 }else{
			 bitStr = bitStr.replaceFirst("1", "0");
			 re = Integer.parseInt(bitStr,2);
		 }
		 return re;
	 }
	 
	 
	 public static int bytesToInt(byte[] src) {  
		    int value;    
		    value = (int) ( ((src[0] & 0xFF)<<24)  
		            |((src[1] & 0xFF)<<16)  
		            |((src[2] & 0xFF)<<8)  
		            |(src[3] & 0xFF));    
		    return value;  
		}  

}
