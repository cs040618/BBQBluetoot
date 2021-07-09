//
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
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.view.View;
//
//public class AblePanelView extends View {
//	int chanalDrawableResourceID;
//	int position;
//	// 目标温度
//	private float targetTemperature = -41;
//	// 当前温度
//	private float currentTemperature = -41;
//	// 炉温
//	private float barbecueTemperature = -41;
//
//	private boolean connectStatus;
//
//	private int workType;
//
//	String time = "";
//
//	private int mScendArcWidth;// 第二个弧的宽度
//
//	private String mText = ""; // 文字内容
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
//	public AblePanelView(Context context) {
//		this(context, null);
//		init(context);
//	}
//
//	public AblePanelView(Context context, AttributeSet attrs) {
//		this(context, attrs, 0);
//		init(context);
//	}
//
//	public AblePanelView(Context context, AttributeSet attrs, int defStyleAttr) {
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
//		mTextColor = panelViewattr.getTextColor();
//		mText = panelViewattr.getmText();
//		mScendArcWidth = panelViewattr.getmScendArcWidth();
//		position = panelViewattr.getPosition();
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
//			result = PxUtils.dpToPx(180, mContext);
//		}
//		return result;
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//		// 绘制粗圆弧
//		drawInArc(canvas);
//		// 绘制矩形和文字
//		drawerRecAndText(canvas);
//
//	}
//
//	private Bitmap adjustPhotoRotation(Bitmap bitmap, int orientationDegree) {
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
//	}
//
//	private void drawerRecAndText(Canvas canvas) {
//		float length = 0;
//		Bitmap mBitmap = null;
//		if (connectStatus) {
//			if (batteryLow) {
//				mBitmap = ((BitmapDrawable) context.getResources().getDrawable(
//						R.drawable.battery_low)).getBitmap();
//				canvas.drawBitmap(mBitmap, getWidth() / 2 - mBitmap.getWidth()
//								/ 2, (float) getHeight() / 2 - mBitmap.getHeight() / 2,
//						paint_text);
//			}
//			paint_text.setTextSize(50f);
//			float timeHeighPosition = getHeight() / 2 - ((mBitmap!=null)?mBitmap.getHeight():0)
//					- 20;
//			mText = time;
//			length = paint_text.measureText(mText);
//			canvas.drawText(mText, getWidth() / 2 - length / 2,
//					timeHeighPosition, paint_text);
//
//			if (currentTemperature >= -40) {
//				mText = DataUtils.displayTmeperature(currentTemperature) + DataUtils.getTemperatureUnit();
//				paint_text.setColor(Color.RED);
//				length = paint_text.measureText(mText);
//				float temperatureHeighPosition = getHeight() / 2
//						+ mBitmap.getHeight() + 20
//						+ (float) getTxtHeight(paint_text) / 2;
//				canvas.drawText(mText, getWidth() / 2 - length / 2,
//						temperatureHeighPosition, paint_text);
//			}
//		} else {
//			paint_text.setTypeface(Typeface.DEFAULT_BOLD);
//			paint_text.setStrokeWidth(5);
//			canvas.drawLine(getWidth() / 2 - 70, getHeight() / 2 - 50,
//					getWidth() / 2 - 10, getHeight() / 2 - 50, paint_text);
//			canvas.drawLine(getWidth() / 2 + 10, getHeight() / 2 - 50,
//					getWidth() / 2 + 70, getHeight() / 2 - 50, paint_text);
//
//			paint_text.setColor(Color.RED);
//			canvas.drawLine(getWidth() / 2 - 70, getHeight() / 2 + 50,
//					getWidth() / 2 - 10, getHeight() / 2 + 50, paint_text);
//			canvas.drawLine(getWidth() / 2 + 10, getHeight() / 2 + 50,
//					getWidth() / 2 + 70, getHeight() / 2 + 50, paint_text);
//		}
//
//		if(chanalDrawableResourceID==0){
//			switch (position) {
//				case 1:
//					mBitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.one_small_disconnect)).getBitmap();
//					break;
//				case 2:
//					mBitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.two_small_disconnect)).getBitmap();
//					break;
//				case 3:
//					mBitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.three_small_disconnect)).getBitmap();
//					break;
//				case 4:
//					mBitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.four_small_disconnect)).getBitmap();
//					break;
//			}
//		}else{
//			mBitmap = ((BitmapDrawable) context.getResources().getDrawable(chanalDrawableResourceID)).getBitmap();
//		}
//		canvas.drawBitmap(mBitmap, getWidth() / 2 - mBitmap.getWidth() / 2, (float) (getHeight() / 2 * (1 + Math.sqrt(2) / 2))-mBitmap.getHeight()/2, paint_text);
//		/*paint_text.setTextSize(80f);
//		length = paint_text.measureText(position + "");
//		canvas.drawText(position + "", getWidth() / 2 - length / 2,
//				(float) (getHeight() / 2 * (1 + Math.sqrt(2) / 2)), paint_text);*/
//
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
//
//		/*if (connectStatus) { //連接狀態
//			canvas.save();
//			Bitmap bitmap = null;
//
//			if (barbecueTemperature >= -40 && currentTemperature >= -40
//					&& targetTemperature >= -40) {
//				bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//						R.drawable.grill_temperature)).getBitmap();
//				canvas.rotate(barbecueTemperature, getWidth() / 2,
//						getHeight() / 2);
//				canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth()
//						/ 2, 14 + mScendArcWidth - bitmap.getHeight(), paint);
//				canvas.save();
//				bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//						R.drawable.target_temperature)).getBitmap();
//				// bitmap = adjustPhotoRotation(bitmap,180);
//				float temperature = 0f;
//				if (barbecueTemperature > targetTemperature) {
//					temperature = -(barbecueTemperature - targetTemperature);
//				} else {
//					temperature = (targetTemperature - barbecueTemperature);
//				}
//				canvas.rotate(temperature, getWidth() / 2, getHeight() / 2);
//				canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth()
//						/ 2, 14 + mScendArcWidth - bitmap.getHeight(), paint);
//				canvas.save();
//				bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//						R.drawable.current_temperature)).getBitmap();
//				// bitmap = adjustPhotoRotation(bitmap,180);
//				if (targetTemperature > currentTemperature) {
//					temperature = -(targetTemperature - currentTemperature);
//				} else {
//					temperature = (currentTemperature - targetTemperature);
//				}
//				canvas.rotate(temperature, getWidth() / 2, getHeight() / 2);
//				canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth()
//						/ 2, 14 + mScendArcWidth - bitmap.getHeight(), paint);
//				canvas.save();
//			} else if (barbecueTemperature >= -40 && currentTemperature < -40
//					&& targetTemperature >= -40) {
//				bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//						R.drawable.grill_temperature)).getBitmap();
//				canvas.rotate(barbecueTemperature, getWidth() / 2,
//						getHeight() / 2);
//				canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth()
//						/ 2, 14 + mScendArcWidth - bitmap.getHeight(), paint);
//				canvas.save();
//				bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//						R.drawable.target_temperature)).getBitmap();
//				// bitmap = adjustPhotoRotation(bitmap,180);
//				float temperature = 0f;
//				if (barbecueTemperature > targetTemperature) {
//					temperature = -(barbecueTemperature - targetTemperature);
//				} else {
//					temperature = (targetTemperature - barbecueTemperature);
//				}
//				canvas.rotate(temperature, getWidth() / 2, getHeight() / 2);
//				canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth()
//						/ 2, 14 + mScendArcWidth - bitmap.getHeight(), paint);
//				canvas.save();
//			} else if (barbecueTemperature >= -40 && currentTemperature >= -40
//					&& targetTemperature < -40) {
//				bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//						R.drawable.grill_temperature)).getBitmap();
//				canvas.rotate(barbecueTemperature, getWidth() / 2,
//						getHeight() / 2);
//				canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth()
//						/ 2, 14 + mScendArcWidth - bitmap.getHeight(), paint);
//				canvas.save();
//				bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//						R.drawable.current_temperature)).getBitmap();
//				// bitmap = adjustPhotoRotation(bitmap,180);
//				float temperature = 0f;
//				if (barbecueTemperature > currentTemperature) {
//					temperature = -(barbecueTemperature - currentTemperature);
//				} else {
//					temperature = (currentTemperature - barbecueTemperature);
//				}
//				canvas.rotate(temperature, getWidth() / 2, getHeight() / 2);
//				canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth()
//						/ 2, 14 + mScendArcWidth - bitmap.getHeight(), paint);
//				canvas.save();
//			}else if (barbecueTemperature < -40 && currentTemperature < -40
//					&& targetTemperature >= -40) {
//				bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
//						R.drawable.target_temperature)).getBitmap();
//				canvas.rotate(targetTemperature, getWidth() / 2,
//						getHeight() / 2);
//				canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth()
//						/ 2, 14 + mScendArcWidth - bitmap.getHeight(), paint);
//				canvas.save();
//			}
//		}*/
//	}
//
//	private void drawInArc(Canvas canvas) {
//		rectF2 = new RectF(OFFSET, OFFSET, getWidth() - OFFSET, getHeight()
//				- OFFSET);
//		canvas.drawArc(rectF2, START_ARC, DURING_ARC, false, paintInerArc);
//	}
//
//	public void updateInitStatus(boolean connectStatus,
//								 String leftTime,float targetTemperature,int chanalDrawableResourceID) {
//		this.connectStatus = connectStatus;
//		this.time = leftTime;
//		this.targetTemperature = targetTemperature;
//		this.chanalDrawableResourceID = chanalDrawableResourceID;
//		invalidate();
//	}
//
//	public void updateCurrentStatus(float currentTemperature,float barbecueTemperature) {
//		// 当前温度
//		this.currentTemperature = currentTemperature;
//		// 炉温
//		this.barbecueTemperature = barbecueTemperature;
//
//		invalidate();
//	}
//
//
//	public void updateBleData(int position) {
//		switch (position) {
//			case 1:
//				currentTemperature = BleConnectUtils.current_temperature1;
//				barbecueTemperature = BleConnectUtils.grill_temperature1;
//				break;
//			case 2:
//				currentTemperature = BleConnectUtils.current_temperature2;
//				barbecueTemperature = BleConnectUtils.grill_temperature2;
//				break;
//			case 3:
//				currentTemperature = BleConnectUtils.current_temperature3;
//				barbecueTemperature = BleConnectUtils.grill_temperature3;
//				break;
//			case 4:
//				currentTemperature = BleConnectUtils.current_temperature4;
//				barbecueTemperature = BleConnectUtils.grill_temperature4;
//				break;
//		}
//		invalidate();
//	}
//
//
//
//	public void setConnectStatus(boolean connectStatus) {
//		this.connectStatus = connectStatus;
//		invalidate();
//	}
//
//	public void setTime(String time) {
//		this.time = time;
//		invalidate();
//	}
//
//	public void setBatteryLow(boolean batteryLow) {
//		this.batteryLow = batteryLow;
//		invalidate();
//	}
//
//	public void setChanalStatus(int chanalDrawableResourceID){
//		this.chanalDrawableResourceID = chanalDrawableResourceID;
//		invalidate();
//	}
//
//}
