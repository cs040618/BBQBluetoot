/**
 * @author anmin
 * 
 */
package fmgtech.grillprobee.barbecue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Environment;

/**
 * log日志统计保存
 * 
 */

public class LogcatHelper {
	private static LogcatHelper INSTANCE = null;
	private static String PATH_LOGCAT;
	private LogDumper mLogDumper = null;
	private int mPId;

	/**
	 * 
	 * 初始化目录
	 * 
	 * */
	public void init(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
			PATH_LOGCAT = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + "GrillLog";
		} else {// 如果SD卡不存在，就保存到本应用的目录下
			PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
					+ File.separator + "GrillLog";
		}
		File file = new File(PATH_LOGCAT);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static LogcatHelper getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new LogcatHelper(context);
		}
		return INSTANCE;
	}

	private LogcatHelper(Context context) {
		init(context);
		mPId = android.os.Process.myPid();
	}

	public void start() {
		if (mLogDumper == null)
			mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
		mLogDumper.start();
	}

	public void stop() {
		if (mLogDumper != null) {
			mLogDumper.stopLogs();
			mLogDumper = null;
		}
	}

	private class LogDumper extends Thread {

		private Process logcatProc;
		private BufferedReader mReader = null;
		private boolean mRunning = true;
		String cmds = null;
		private String mPID;
		private FileOutputStream out = null;

		public LogDumper(String pid, String dir) {
			mPID = pid;
			try {
				clearLogs(dir);
				File file = new File(dir, "GrillLog-" + MyDate.getFileName()
						+ ".log");
				if (!file.exists()) {
					file.createNewFile();
				} 
				// 尾加方式写入
				out = new FileOutputStream(file, true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			/**
			 * 
			 * 日志等级：*:v , *:d ,*:w , *:e , *:f , *:s
			 * 
			 * 显示当前mPID程序的 E和W等级的日志.
			 * 
			 * */

			// cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
			// cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
			// cmds = "logcat -s way";//打印标签过滤信息
			// cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";
			// cmds = "logcat -s System.out";
			cmds = "logcat *:e *:i | grep \"(" + mPID + ")\" -s System.out";
			// cmds = "logcat *:i | grep \"(" + mPID + ")\"";

		}

		// 定时七天后清除掉过期的Log日志
		public void clearLogs(String dir) {
			long endTimer = System.currentTimeMillis();
			long totalDay = 24 * 60 * 60 * 1000 * 7;
			File[] f = new File(dir).listFiles();// 取得文件夹里面的路径
			for (final File nFile : f) {
				if (nFile.isFile()) {
					String time = nFile.getName().substring(
							nFile.getName().length() - 14,
							nFile.getName().length() - 4);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date = null;
					try {
						date = sdf.parse(time);
					} catch (ParseException e) {
						e.printStackTrace();
					}

					long startTimer = date.getTime();
					TimerTask timerTask = new TimerTask() {
						@Override
						public void run() {
							nFile.delete();
							System.out.println("DelLog->" + nFile.getName());
						}
					};
					// 定时器类的对象
					Timer timer = new Timer();
					if ((endTimer - startTimer) >= totalDay) {
						timer.schedule(timerTask, 1); // 7天后执行
					}
				}
			}

		}

		public void stopLogs() {
			mRunning = false;
		}

		@Override
		public void run() {
			try {
				logcatProc = Runtime.getRuntime().exec(cmds);
				mReader = new BufferedReader(new InputStreamReader(
						logcatProc.getInputStream()), 1024);
				String line = null;
				while (mRunning && (line = mReader.readLine()) != null) {
					if (!mRunning) {
						break;
					}
					if (line.length() == 0) {
						continue;
					}
					if (out != null && line.contains(mPID)) {
						out.write((MyDate.getDateEN() + "  " + line + "\n")
								.getBytes());
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (logcatProc != null) {
					try{
						logcatProc.destroy();
						logcatProc = null;
					}catch(Exception e){
						
					}

				}
				if (mReader != null) {
					try {
						mReader.close();
						mReader = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					out = null;
				}

			}

		}

	}

}

class MyDate {
	public static String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;// 2012年10月03日 23:41:31
	}

	public static String getDateEN() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date1 = format1.format(new Date(System.currentTimeMillis()));
		return date1;// 2012-10-03 23:41:31
	}

}