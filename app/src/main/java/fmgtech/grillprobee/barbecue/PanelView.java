//package fmgtech.grillprobee.barbecue;
//
//import fmgtech.grillprobee.barbecue.utils.BleConnectUtils;
//import fmgtech.grillprobee.barbecue.utils.DataUtils;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.graphics.Typeface;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Build;
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.view.View;
//
//public class PanelView extends View {
//	// 目标温度
//	private float targetTemperature = -41;
//	// 当前温度
//	private float currentTemperature = -41;
//	// 炉温
//	private float barbecueTemperature = -41;
//
//	private int workType;
//
//	String leftTime = "";
//
//	private int mScendArcWidth;// 第二个弧的宽度
//
//	private String mText = ""; // 文字内容
//
//	private int mTextSize;// 文字的大小
//
//	// 设置文字颜色
//	private int mTextColor;
//	// 刻度的个数
//	private int mTikeCount;
//
//	// 画笔
//	private Paint paintInerArc;// 内圈白色画笔
//	private Paint paint_text;// 文字画笔
//	// 画笔
//	private Paint paintOuter_Arc;// 外圈弧画笔
//	private RectF rectF2;
//
//	private int OFFSET = 80;
//	private int START_ARC = 120;
//	private int DURING_ARC = 300;
//
//	private Context mContext;
//
//	private PanelViewAttr panelViewattr;
//
//	private Context context;
//
//	private Paint paint = new Paint();
//	private float s;// 三角形边长
//	private Path path = new Path();// 绘制三角形的路径
//	private Path path_matrix = new Path();// 经过matrix变换的三角形路径
//	private Matrix matrix = new Matrix();// 三角形旋转矩阵
//	Rect rect = new Rect();// 测量文字所占的高度宽度
//	private final float sqrt3 = (float) Math.sqrt(3);
//	private boolean batteryLow = false;
//
//	public PanelView(Context context) {
//		this(context, null);
//		init(context);
//	}
//
//	public PanelView(Context context, AttributeSet attrs) {
//		this(context, attrs, 0);
//		init(context);
//	}
//
//	public PanelView(Context context, AttributeSet attrs, int defStyleAttr) {
//		super(context, attrs, defStyleAttr);
//		mContext = context;// 这里必须在构造器里获取
//		panelViewattr = new PanelViewAttr(context, attrs, defStyleAttr);
//		init(context);
//		this.context = context;
//	}
//
//	public double getTxtHeight(Paint mPaint) {
//		Paint.FontMetrics fm = mPaint.getFontMetrics();
//		return Math.ceil(fm.descent - fm.ascent);
//	}
//
//	@SuppressLint("ResourceAsColor")
//	private void init(Context context) {
//		mTikeCount = panelViewattr.getmTikeCount();
//		mTextSize = panelViewattr.getmTextSize();
//		mTextColor = panelViewattr.getTextColor();
//		mText = panelViewattr.getmText();
//		mScendArcWidth = panelViewattr.getmScendArcWidth();
//		// 如果手机版本在4.0以上,则开启硬件加速
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//			setLayerType(View.LAYER_TYPE_HARDWARE, null);
//		}
//		paintInerArc = new Paint();
//		paintInerArc.setAntiAlias(true);
//		paintInerArc.setStrokeWidth(mScendArcWidth);
//		paintInerArc.setStyle(Paint.Style.STROKE);
//		paintInerArc.setColor(Color.WHITE);
//		// paintInerArc.setColor(android.R.color.transparent);
//		paint_text = new Paint();
//		paint_text.setAntiAlias(true);
//		paint_text.setColor(mTextColor);
//		paint_text.setStrokeWidth(1);
//		paint_text.setStyle(Paint.Style.FILL);// 实心画笔
//
//		// 初始化画笔
//		paintOuter_Arc = new Paint();
//		paintOuter_Arc.setAntiAlias(true);
//		paintOuter_Arc.setStyle(Paint.Style.STROKE);// 空心画笔
//		paintOuter_Arc.setStrokeWidth(3);
//
//	}
//
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int realWidth = startMeasure(widthMeasureSpec);
//		int realHeight = startMeasure(heightMeasureSpec);
//		setMeasuredDimension(realWidth, realHeight);
//	}
//
//	private int startMeasure(int msSpec) {
//		int result = 0;
//		int mode = MeasureSpec.getMode(msSpec);
//		int size = MeasureSpec.getSize(msSpec);
//		if (mode == MeasureSpec.EXACTLY) {
//			result = size;
//		} else {
//			if (size < 1080) {
//				result = PxUtils.dpToPx(300, mContext);
//			} else {
//				result = PxUtils.dpToPx(250, mContext);
//			}
//		}
//		return result;
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//
//		// 绘制粗圆弧
//		drawInArc(canvas);
//		// 绘制矩形和文字
//		drawerRecAndText(canvas);
//
//	}
//
//	private Bitmap adjustPhotoRotation(Bitmap bitmap, int orientationDegree) {
//
//		Matrix m = new Matrix();
//		m.setRotate(orientationDegree, (float) bitmap.getWidth() / 2,
//				(float) bitmap.getHeight() / 2);
//
//		try {
//			Bitmap bm1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//					bitmap.getHeight(), m, true);
//			return bm1;
//		} catch (OutOfMemoryError ex) {
//		}
//		return null;
//
//		/*
//		 * Matrix matrix = new Matrix(); matrix.setRotate(orientationDegree,
//		 * (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2); float
//		 * targetX, targetY; if (orientationDegree == 90) { targetX =
//		 * bitmap.getHeight(); targetY = 0; } else { targetX =
//		 * bitmap.getHeight(); targetY = bitmap.getWidth(); }
//		 *
//		 *
//		 * final float[] values = new float[9]; matrix.getValues(values);
//		 *
//		 *
//		 * float x1 = values[Matrix.MTRANS_X]; float y1 =
//		 * values[Matrix.MTRANS_Y];
//		 *
//		 *
//		 * matrix.postTranslate(targetX - x1, targetY - y1); Bitmap canvasBitmap
//		 * = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(),
//		 * Bitmap.Config.ARGB_8888); Paint paint = new Paint(); Canvas canvas =
//		 * new Canvas(canvasBitmap); canvas.drawBitmap(bitmap, matrix, paint);
//		 *
//		 *
//		 * return canvasBitmap;
//		 */
//	}
//
//	private void drawerRecAndText(Canvas canvas) {
//		float length = 0;
//		float position = mScendArcWidth + OFFSET + 10;
//		paint_text.setTextSize(mTextSize);
//		mText = context.getResources().getString(R.string.left_time);
//		length = paint_text.measureText(mText);
//		float heigh = (float) getTxtHeight(paint_text);
//		canvas.drawText(mText, getWidth() / 2 - length / 2, position,
//				paint_text);
//		if (true) {
//			if (batteryLow) {
//				Bitmap mBitmap = ((BitmapDrawable) context.getResources()
//						.getDrawable(R.drawable.battery_low)).getBitmap();
//				canvas.drawBitmap(mBitmap, getWidth() / 2 - mBitmap.getWidth()
//						/ 2, (float) (getHeight() / 2 * (1 + Math.sqrt(2) / 2))
//						- mBitmap.getHeight() / 2, paint_text);
//			}
//			paint_text.setColor(Color.RED);
//			position += heigh;
//			float timeHeighPosition = getHeight() / 2
//					- (mScendArcWidth + OFFSET) / 2;
//			if (TextUtils.isEmpty(leftTime)) {
//				paint_text.setTypeface(Typeface.DEFAULT_BOLD);
//				paint_text.setStrokeWidth(5);
//				canvas.drawLine(getWidth() / 2 - 70, timeHeighPosition,
//						getWidth() / 2 - 10, timeHeighPosition, paint_text);
//				canvas.drawLine(getWidth() / 2 + 10, timeHeighPosition,
//						getWidth() / 2 + 70, timeHeighPosition, paint_text);
//			} else {
//				mText = leftTime;
//				length = paint_text.measureText(mText);
//				canvas.drawText(mText, getWidth() / 2 - length / 2,
//						timeHeighPosition, paint_text);
//			}
//			mText = context.getResources().getString(
//					R.string.current_temperature);
//			length = paint_text.measureText(mText);
//			heigh = (float) getTxtHeight(paint_text);
//			canvas.drawText(mText, getWidth() / 2 - length / 2,
//					getHeight() / 2, paint_text);
//			paint_text.setColor(Color.WHITE);
//
//			paint_text.setColor(Color.RED);
//			if (currentTemperature >= -40) {
//				mText = DataUtils.displayTmeperature(currentTemperature)
//						+ DataUtils.getTemperatureUnit();
//				length = paint_text.measureText(mText);
//				float temperatureHeighPosition = getHeight() / 2
//						+ (mScendArcWidth + OFFSET) / 2;
//				canvas.drawText(mText, getWidth() / 2 - length / 2,
//						temperatureHeighPosition, paint_text);
//			}
//
//			paint_text.setTextSize(40f);
//
//			switch (DataUtils.temperatureUnit) {
//			case 0:
//				mText = context.getResources().getString(
//						R.string.min_centigrade_temperature);
//				length = paint_text.measureText(mText);
//				canvas.drawText(mText, getWidth() / 2 - (OFFSET) * 2 - length,
//						(float) getHeight() - 30, paint_text);
//				mText = context.getResources().getString(
//						R.string.max_centigrade_temperature);
//				length = paint_text.measureText(mText);
//				canvas.drawText(mText, getWidth() / 2 + (OFFSET) * 2,
//						(float) getHeight() - 30, paint_text);
//				break;
//			case 1:
//				mText = context.getResources().getString(
//						R.string.min_fahrenhite_temperature);
//				length = paint_text.measureText(mText);
//				canvas.drawText(mText, getWidth() / 2 - (OFFSET) * 2 - length,
//						(float) getHeight() - 30, paint_text);
//				mText = context.getResources().getString(
//						R.string.max_fahrenhite_temperature);
//				length = paint_text.measureText(mText);
//				canvas.drawText(mText, getWidth() / 2 + (OFFSET) * 2,
//						(float) getHeight() - 30, paint_text);
//				break;
//			}
//
//		} else {
//			position += heigh;
//			paint_text.setTypeface(Typeface.DEFAULT_BOLD);
//			paint_text.setStrokeWidth(5);
//			float timeHeighPosition = getHeight() / 2
//					- (mScendArcWidth + OFFSET) / 2;
//			canvas.drawLine(getWidth() / 2 - 70, timeHeighPosition,
//					getWidth() / 2 - 10, timeHeighPosition, paint_text);
//			canvas.drawLine(getWidth() / 2 + 10, timeHeighPosition,
//					getWidth() / 2 + 70, timeHeighPosition, paint_text);
//			mText = context.getResources().getString(
//					R.string.current_temperature);
//			length = paint_text.measureText(mText);
//			heigh = (float) getTxtHeight(paint_text);
//			canvas.drawText(mText, getWidth() / 2 - length / 2,
//					getHeight() / 2, paint_text);
//			paint_text.setColor(Color.WHITE);
//
//			paint_text.setColor(Color.RED);
//			float temperatureHeighPosition = getHeight() / 2
//					+ (mScendArcWidth + OFFSET) / 2;
//			canvas.drawLine(getWidth() / 2 - 70, temperatureHeighPosition,
//					getWidth() / 2 - 10, temperatureHeighPosition, paint_text);
//			canvas.drawLine(getWidth() / 2 + 10, temperatureHeighPosition,
//					getWidth() / 2 + 70, temperatureHeighPosition, paint_text);
//
//			paint_text.setTextSize(40f);
//			/*
//			switch (DataUtils.temperatureUnit) {
//			case 0:
//				mText = context.getResources().getString(
//						R.string.min_centigrade_temperature);
//				length = paint_text.measureText(mText);
//				canvas.drawText(mText, getWidth() / 2 - (OFFSET) * 2 - length,
//						(float) getHeight() - 30, paint_text);
//				mText = context.getResources().getString(
//						R.string.max_centigrade_temperature);
//				length = paint_text.measureText(mText);
//				canvas.drawText(mText, getWidth() / 2 + (OFFSET) * 2,
//						(float) getHeight() - 30, paint_text);
//				break;
//			case 1:
//				mText = context.getResources().getString(
//						R.string.min_fahrenhite_temperature);
//				length = paint_text.measureText(mText);
//				canvas.drawText(mText, getWidth() / 2 - (OFFSET) * 2 - length,
//						(float) getHeight() - 30, paint_text);
//				mText = context.getResources().getString(
//						R.string.max_fahrenhite_temperature);
//				length = paint_text.measureText(mText);
//				canvas.drawText(mText, getWidth() / 2 + (OFFSET) * 2,
//						(float) getHeight() - 30, paint_text);
//				break;
//			}
//			 */
//		}
//		//畫面刻度點繪製
//		mTikeCount = 100;
//		canvas.save(); // 记录画布状态
//		canvas.rotate(-(180 - START_ARC + 90), getWidth() / 2, getHeight() / 2);
//		float rAngle = DURING_ARC / mTikeCount;
//		for (int i = 1; i < mTikeCount; i++) {
//			canvas.save(); // 记录画布状态
//			canvas.rotate(rAngle * i, getWidth() / 2, getHeight() / 2);
//			if (i > 30 && i < 60) {
//				paintOuter_Arc.setColor(Color.LTGRAY);
//			} else if (i > 60) {
//				paintOuter_Arc.setColor(Color.RED);
//			}
//			canvas.drawLine(getWidth() / 2, OFFSET / 2, getWidth() / 2,
//					OFFSET / 2 + 5, paintOuter_Arc);// 画刻度线
//			canvas.restore();
//		}
//		//
//		canvas.save();
//		Bitmap bitmap = null;
//		/*
//		if (barbecueTemperature >= -40 && currentTemperature >= -40
//				&& targetTemperature >= -40) {
//
//			bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.grill_temperature)).getBitmap();		//環境溫度
//
//			canvas.rotate(barbecueTemperature, getWidth() / 2, getHeight() / 2);
//			canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2,
//				14 + mScendArcWidth - bitmap.getHeight(), paint);
//			canvas.save();
//
//
//			bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.target_temperature)).getBitmap();	//目標溫度
//			// bitmap = adjustPhotoRotation(bitmap,180);
//			float temperature = 0f;
//			if (barbecueTemperature > targetTemperature) {
//				temperature = -(barbecueTemperature - targetTemperature);
//			} else {
//				temperature = (targetTemperature - barbecueTemperature);
//			}
//			canvas.rotate(temperature, getWidth() / 2, getHeight() / 2);
//			canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2,
//					14 + mScendArcWidth - bitmap.getHeight(), paint);
//			canvas.save();
//			bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.current_temperature)).getBitmap();		//探針溫度
//			// bitmap = adjustPhotoRotation(bitmap,180);
//			if (targetTemperature > currentTemperature) {
//				temperature = -(targetTemperature - currentTemperature);
//			} else {
//				temperature = (currentTemperature - targetTemperature);
//			}
//			canvas.rotate(temperature, getWidth() / 2, getHeight() / 2);
//			canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2,
//					14 + mScendArcWidth - bitmap.getHeight(), paint);
//			canvas.save();
//		} else if (barbecueTemperature >= -40 && currentTemperature < -40
//				&& targetTemperature >= -40) {
//			bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.grill_temperature)).getBitmap();
//			canvas.rotate(barbecueTemperature, getWidth() / 2, getHeight() / 2);
//			canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2,
//					14 + mScendArcWidth - bitmap.getHeight(), paint);
//			canvas.save();
//			bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.target_temperature)).getBitmap();
//			// bitmap = adjustPhotoRotation(bitmap,180);
//			float temperature = 0f;
//			if (barbecueTemperature > targetTemperature) {
//				temperature = -(barbecueTemperature - targetTemperature);
//			} else {
//				temperature = (targetTemperature - barbecueTemperature);
//			}
//			canvas.rotate(temperature, getWidth() / 2, getHeight() / 2);
//			canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2,
//					14 + mScendArcWidth - bitmap.getHeight(), paint);
//			canvas.save();
//		} else if (barbecueTemperature >= -40 && currentTemperature >= -40
//				&& targetTemperature < -40) {
//			bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.grill_temperature)).getBitmap();
//			canvas.rotate(barbecueTemperature, getWidth() / 2, getHeight() / 2);
//			canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2,
//					14 + mScendArcWidth - bitmap.getHeight(), paint);
//			canvas.save();
//			bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.current_temperature)).getBitmap();
//			// bitmap = adjustPhotoRotation(bitmap,180);
//			float temperature = 0f;
//			if (barbecueTemperature > currentTemperature) {
//				temperature = -(barbecueTemperature - currentTemperature);
//			} else {
//				temperature = (currentTemperature - barbecueTemperature);
//			}
//			canvas.rotate(temperature, getWidth() / 2, getHeight() / 2);
//			canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2,
//					14 + mScendArcWidth - bitmap.getHeight(), paint);
//			canvas.save();
//		} else if (barbecueTemperature < -40 && currentTemperature < -40
//				&& targetTemperature >= -40) {
//			bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.target_temperature)).getBitmap();
//			canvas.rotate(targetTemperature, getWidth() / 2, getHeight() / 2);
//			canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2,
//					14 + mScendArcWidth - bitmap.getHeight(), paint);
//			canvas.save();
//		}
//		*/
//		/*
//		 * s = OFFSET; path.reset(); path.moveTo(0, -s / sqrt3); path.lineTo(s /
//		 * 4f, sqrt3 / 6 * s); path.lineTo(-s / 4f, sqrt3 / 6 * s);
//		 * path.close(); // 画三角形 paint.setStrokeWidth(1);
//		 * paint.setColor(Color.RED); paint.setStyle(Paint.Style.FILL); int
//		 * width = this.getWidth(); int angle = 270; matrix.reset();
//		 * matrix.setRotate(180 - angle);// 旋转 float angle_rad = (float) (angle
//		 * / 180f * Math.PI);// 化成弧度制 float dx = (float) (width / 2f - (width /
//		 * 2f - s - 22 + s / sqrt3) Math.sin(angle_rad)); float dy = (float)
//		 * (width / 2f - (width / 2f - s - 22 + s / sqrt3) Math.cos(angle_rad));
//		 * matrix.postTranslate(// 平移 dx, dy); path_matrix.set(path);
//		 * path_matrix.transform(matrix);
//		 *
//		 * canvas.drawPath(path_matrix, paint);
//		 *
//		 * paint.setColor(Color.GREEN); matrix.setRotate(-(180 - angle - 90));//
//		 * 旋转 angle_rad = (float) (angle / 180f * Math.PI);// 化成弧度制 dx = (float)
//		 * (width / 2f - (width / 2f - 2f * s + 2 + s / sqrt3)
//		 * Math.cos(angle_rad)); dy = (float) (width / 2f - (width / 2f - 2f * s
//		 * + 2 + s / sqrt3) Math.sin(angle_rad)); matrix.postTranslate(// 平移 dx,
//		 * dy); path_matrix.set(path); path_matrix.transform(matrix);
//		 *
//		 * canvas.drawPath(path_matrix, paint);
//		 */
//	}
//
//	private void drawInArc(Canvas canvas) {
//		rectF2 = new RectF(OFFSET, OFFSET, getWidth() - OFFSET, getHeight()
//				- OFFSET);
//		canvas.drawArc(rectF2, START_ARC, DURING_ARC, false, paintInerArc);
//	}
//
//	/**
//	 * 设置文字大小
//	 *
//	 * @param size
//	 */
//	public void setTextSize(int size) {
//		mTextSize = size;
//		invalidate();
//	}
//
//	/**
//	 * 设置字体颜色
//	 *
//	 * @param mTextColor
//	 */
//	public void setmTextColor(int mTextColor) {
//		this.mTextColor = mTextColor;
//	}
//
//	/**
//	 * 设置字体mText
//	 *
//	 * @param mText
//	 */
//	public void setmText(String mText) {
//		this.mText = mText;
//	}
//
//	public void updateInitStatus(int workType, String leftTime) {
//		this.workType = workType;
//		this.leftTime = leftTime;
//		invalidate();
//	}
//
//	public void updateTargetStatus(int workType, String leftTime,
//			float targetTemperature) {
//		this.workType = workType;
//		this.leftTime = leftTime;
//		// 目标温度
//		this.targetTemperature = targetTemperature;
//		invalidate();
//	}
//
//
//	public void updateTargetStatus(String leftTime,
//			float targetTemperature) {
//		this.leftTime = leftTime;
//		// 目标温度
//		this.targetTemperature = targetTemperature;
//		invalidate();
//	}
//
//	public void updateBleData(int position) {
//		switch (position) {
//		case 1:
//			currentTemperature = BleConnectUtils.current_temperature1;
//			barbecueTemperature = BleConnectUtils.grill_temperature1;
//			break;
//		case 2:
//			currentTemperature = BleConnectUtils.current_temperature2;
//			barbecueTemperature = BleConnectUtils.grill_temperature2;
//			break;
//		case 3:
//			currentTemperature = BleConnectUtils.current_temperature3;
//			barbecueTemperature = BleConnectUtils.grill_temperature3;
//			break;
//		case 4:
//			currentTemperature = BleConnectUtils.current_temperature4;
//			barbecueTemperature = BleConnectUtils.grill_temperature4;
//			break;
//		}
//		invalidate();
//	}
//
//	public void updateChanalData(int position,String leftTime) {
//		switch (position) {
//		case 1:
//			currentTemperature = BleConnectUtils.current_temperature1;
//			barbecueTemperature = BleConnectUtils.grill_temperature1;
//			targetTemperature = DataUtils.targetTemperature1;
//			break;
//		case 2:
//			currentTemperature = BleConnectUtils.current_temperature2;
//			barbecueTemperature = BleConnectUtils.grill_temperature2;
//			targetTemperature = DataUtils.targetTemperature2;
//			break;
//		case 3:
//			currentTemperature = BleConnectUtils.current_temperature3;
//			barbecueTemperature = BleConnectUtils.grill_temperature3;
//			targetTemperature = DataUtils.targetTemperature3;
//			break;
//		case 4:
//			currentTemperature = BleConnectUtils.current_temperature4;
//			barbecueTemperature = BleConnectUtils.grill_temperature4;
//			targetTemperature = DataUtils.targetTemperature4;
//			break;
//		}
//		this.leftTime = leftTime;
//		invalidate();
//	}
//
//
//	public void setBatteryLow(boolean batteryLow) {
//		this.batteryLow = batteryLow;
//		invalidate();
//	}
//
//}
