//package fmgtech.grillprobee.barbecue;
//
//import java.util.Locale;
//
//import fmgtech.grillprobee.barbecue.utils.DataUtils;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.LinearGradient;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.graphics.Shader;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.MeasureSpec;
//
//public class TimePanelView extends View {
//	private int color_start;
//	private int color_end;
//	private int timeType = 1;
//	private float circleDimensionRate = 0.2f;// 鍦嗙幆姣斾緥
//	private float triangleDimensionRate = 0.2f;// 涓夎褰㈡瘮渚�
//	//指针反向超过圆点的长度
//    private static final float DEFAULT_POINT_BACK_LENGTH = 40f;
//
//	private float s;// 涓夎褰㈣竟闀�
//	private float strokeWidth;// 鍦嗙幆瀹藉害
//	private float angle = 360;// 瑙掑害0-360
//	private RectF oval = new RectF();
//	private Paint paint = new Paint();
//	Rect rect = new Rect();// 娴嬮噺鏂囧瓧鎵�鍗犵殑楂樺害瀹藉害
//	private int mTikeCount = 12;
//	private int mPointCount = 60;
//	private Paint paintouter_Num;// 澶栧姬鐨勫埢搴︾殑鐢荤瑪
//	private Context mContext;
//	private Paint paint_text;// 鏂囧瓧鐢荤瑪
//	float time1Position[], time2Position[], time3Position[];
//	Bitmap mBitmap1, mBitmap2, mBitmap3;
//	int mTextSize;
//	String mText;
//	String text = "";
//	private int mMinCircleRadius = 15; // 涓績鍦嗙偣鐨勫崐寰�
//	private Paint paint_centerPoint_Pointer;// 鍐呭渾鐢荤瑪
//
//	public TimePanelView(Context context) {
//		super(context);
//		mContext = context;
//		init(null, 0);
//	}
//
//	public TimePanelView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		mContext = context;
//		init(attrs, 0);
//	}
//
//	public TimePanelView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		mContext = context;
//		init(attrs, defStyle);
//	}
//
//	private void init(AttributeSet attrs, int defStyle) {
//		final TypedArray a = getContext().obtainStyledAttributes(attrs,
//				R.styleable.MyView, defStyle, 0);
//
//		color_start = a.getColor(R.styleable.MyView_color_start, Color.RED);// 榛樿缁胯壊
//		color_end = a.getColor(R.styleable.MyView_color_end, Color.LTGRAY);// 榛樿鐏拌壊
//		circleDimensionRate = a.getFloat(
//				R.styleable.MyView_circleDimensionRate, 0.1f);
//		triangleDimensionRate = a.getFloat(
//				R.styleable.MyView_triangleDimensionRate, 0.1f);
//
//		paintouter_Num = new Paint();
//		paintouter_Num.setAntiAlias(true);
//		paintouter_Num.setColor(Color.RED);
//		paintouter_Num.setStyle(Paint.Style.FILL);// 绌哄績鐢荤瑪
//		paintouter_Num.setStrokeWidth(1);
//
//		paint_text = new Paint();
//		paint_text.setAntiAlias(true);
//		paint_text.setStrokeWidth(1);
//		paint_text.setStyle(Paint.Style.FILL);// 瀹炲績鐢荤瑪
//
//		paint_centerPoint_Pointer = new Paint();
//		paint_centerPoint_Pointer.setAntiAlias(true);
//		paint_centerPoint_Pointer.setStyle(Paint.Style.FILL);// 瀹炲績鐢荤瑪
//		a.recycle();
//
//	}
//
//	private void initDrawVar() {
//		int width = getWidth();
//		s = triangleDimensionRate * width;// 涓夎褰㈣竟闀�
//		strokeWidth = circleDimensionRate * width * 1.5f;// 鍦嗙幆瀹藉害
//		// 缁樺埗鍦嗙幆鐨勭煩褰㈠尯鍩�
//		oval.set(s + strokeWidth / 2f, s + strokeWidth / 2f, width - s
//				- strokeWidth / 2f, width - s - strokeWidth / 2f);
//	}
//
//	public double getTxtHeight(Paint mPaint) {
//		Paint.FontMetrics fm = mPaint.getFontMetrics();
//		return Math.ceil(fm.descent - fm.ascent);
//	}
//
//	private float drawerRecAndText(Canvas canvas) {
//		float length = 0;
//		paint_text.setColor(Color.RED);
//		paint_text.setTextSize(30f);
//		mText = mContext.getResources().getString(R.string.hour);
//		length = paint_text.measureText(mText);
//		float heigh = (float) getTxtHeight(paint_text);
//		/*
//		 * canvas.drawText(mText, getWidth() / 2 - length / 2, (float)
//		 * (getHeight() / 2 * (1 + Math.sqrt(2) / 3)) - 250, paint_text);
//		 */
//		canvas.drawText(mText, getWidth() / 2 - length / 2, (float) getHeight()
//				/ 2 - strokeWidth, paint_text);
//		switch (timeType) {
//		case 1:
//			mBitmap1 = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.zero_choose)).getBitmap();
//			mBitmap2 = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.one)).getBitmap();
//			mBitmap3 = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.two)).getBitmap();
//			break;
//		case 2:
//			mBitmap1 = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.zero)).getBitmap();
//			mBitmap2 = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.one_choose)).getBitmap();
//			mBitmap3 = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.two)).getBitmap();
//			break;
//		case 3:
//			mBitmap1 = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.zero)).getBitmap();
//			mBitmap2 = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.one)).getBitmap();
//			mBitmap3 = ((BitmapDrawable) mContext.getResources().getDrawable(
//					R.drawable.two_choose)).getBitmap();
//			break;
//
//		}
//
//		time1Position = new float[] {
//				getWidth() / 2 - mBitmap1.getWidth() / 2 - mBitmap1.getWidth()
//						- 5, (float) getHeight() / 2 - strokeWidth + heigh,
//				mBitmap1.getWidth(), mBitmap1.getHeight() };
//		canvas.drawBitmap(mBitmap1, time1Position[0], time1Position[1],
//				paint_text);
//		time2Position = new float[] { getWidth() / 2 - mBitmap2.getWidth() / 2,
//				(float) getHeight() / 2 - strokeWidth + heigh,
//				mBitmap2.getWidth(), mBitmap2.getHeight() };
//
//		canvas.drawBitmap(mBitmap2, time2Position[0], time2Position[1],
//				paint_text);
//
//		time3Position = new float[] {
//				getWidth() / 2 + mBitmap3.getWidth() / 2 + 5,
//				(float) getHeight() / 2 - strokeWidth + heigh,
//				mBitmap3.getWidth(), mBitmap3.getHeight() };
//		canvas.drawBitmap(mBitmap3, time3Position[0], time3Position[1],
//				paint_text);
//		canvas.save(); // 璁板綍鐢诲竷鐘舵��
//		heigh = heigh + mBitmap1.getHeight();
//		canvas.rotate(-(180 - 270 + 90), getWidth() / 2, getHeight() / 2);
//		float rAngle = 360 / mPointCount;
//		for (int i = 0; i < mPointCount; i++) {
//			canvas.save(); // 璁板綍鐢诲竷鐘舵��
//			canvas.rotate(rAngle * i, getWidth() / 2, getHeight() / 2);
//			if (i % 5 == 0) {
//				mMinCircleRadius = 10;
//				paint_centerPoint_Pointer.setColor(Color.RED);
//			} else {
//				mMinCircleRadius = 5;
//				paint_centerPoint_Pointer.setColor(Color.GREEN);
//			}
//			canvas.drawCircle(getWidth() / 2, s + strokeWidth / 2,
//					mMinCircleRadius, paint_centerPoint_Pointer);
//			canvas.restore();
//		}
//		return heigh;
//	}
//
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int realWidth = startMeasure(widthMeasureSpec);
//		int realHeight = startMeasure(heightMeasureSpec);
//
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
//	private void test(Canvas canvas){
//		canvas.rotate(0, getWidth() / 2-10, getHeight() / 2-10);
//		float rAngle = 360 / mPointCount;
//		for (int i = 0; i < mPointCount; i++) {
//			canvas.save(); // 璁板綍鐢诲竷鐘舵��
//			canvas.rotate(rAngle * i, getWidth() / 2-10, getHeight() / 2-10);
//			if (i % 5 == 0) {
//				mMinCircleRadius = 10;
//				paint_centerPoint_Pointer.setColor(Color.RED);
//			} else {
//				mMinCircleRadius = 5;
//				paint_centerPoint_Pointer.setColor(Color.GREEN);
//			}
//			canvas.drawCircle(getWidth() / 2, s + strokeWidth / 2,
//					mMinCircleRadius, paint_centerPoint_Pointer);
//			canvas.restore();
//		}
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//		drawerNum(canvas);
//		initDrawVar();
//		//test(canvas);
//		// 鐢荤豢鑹茬幆褰�
//		paint.setAntiAlias(true);
//		paint.setColor(color_end);
//		paint.setStrokeWidth(strokeWidth);
//		paint.setStyle(Paint.Style.STROKE); // 璁剧疆绌哄績
//		// 姝ｅ彸鏂逛负0搴︼紝椤烘椂閽堟棆杞紝姝ｄ笂鏂逛负270搴�
//		canvas.drawArc(oval, 270 - angle, angle, false, paint);
//		// 鐢荤伆鑹茬幆褰�
//		if (angle != 360) {
//			paint.setColor(color_start);
//			// paint.setStrokeCap(Paint.Cap.ROUND);
//			canvas.drawArc(oval, 270, 360 - angle, false, paint);
//		}
//		float heigh = drawerRecAndText(canvas);
//		paint.setStrokeWidth(1);
//		paint.setColor(color_start);
//		paint.setStyle(Paint.Style.FILL);
//		// 鏄剧ず鐧惧垎姣�
//		paint.setTextSize(getWidth() * 0.1f);
//		switch (timeType) {
//		case 1:
//			text = "0:"
//					+ String.format(Locale.CHINA, "%02d",
//							Math.round((360 - angle) / 360 * 100 * 60 / 100));
//
//			break;
//		case 2:
//			text = "1:"
//					+ String.format(Locale.CHINA, "%02d",
//							Math.round((360 - angle) / 360 * 100 * 60 / 100));
//
//			break;
//		case 3:
//			text = "2:"
//					+ String.format(Locale.CHINA, "%02d",
//							Math.round((360 - angle) / 360 * 100 * 60 / 100));
//			break;
//		}
//		paint.getTextBounds(text, 0, text.length(), rect);// 娴嬮噺text鎵�鍗犲搴﹀拰楂樺害
//		canvas.drawText(text, getWidth() / 2f - rect.width() / 2,
//				(float) getHeight() / 2 + heigh, paint);
//	}
//
//	public String getText() {
//		return text;
//	}
//
//	public void setText(String text) {
//		this.text = text;
//	}
//
//	/*
//	 * private void drawerNum(Canvas canvas) { canvas.save(); // 璁板綍鐢诲竷鐘舵��
//	 * paintouter_Num.setTextSize(30); float heigh = (float)
//	 * getTxtHeight(paintouter_Num); float length = 0; canvas.rotate(0,
//	 * getWidth() / 2, getHeight() / 2);
//	 * //杩欎釜鏂规硶鏄皢鏁翠釜鐢诲竷鏃嬭浆锛岃捣鐐逛綅缃繕鏄箣鍓嶇敾甯冮噷闈㈢殑150锛�70 鐐� float rAngle = 360 /
//	 * mTikeCount; for (int i = 0; i < mTikeCount; i++) { canvas.save(); //
//	 * 璁板綍鐢诲竷鐘舵�� canvas.rotate(rAngle * i, getWidth() / 2, getHeight() / 2);
//	 * mText = "" + i * 5; length = paintouter_Num.measureText(mText);
//	 * canvas.drawText(mText, getWidth() / 2-length/2, heigh, paintouter_Num);//
//	 * 鐢诲埢搴� canvas.drawBitmap(bitmap, getWidth() / 2-bitmap.getWidth()/2, 50,
//	 * paintouter_Num); canvas.restore(); }
//	 *
//	 * }
//	 */
//
//	private void drawerNum(Canvas canvas){
//		 //外圆边框宽度
//	      float DEFAULT_BORDER_WIDTH = 6f;
//		 float borderWidth = DEFAULT_BORDER_WIDTH;
//	        float r = Math.min(getHeight() / 2, getWidth() / 2) - borderWidth / 2;
//	        /*Paint paintCircle = new Paint();
//	        paintCircle.setStyle(Paint.Style.STROKE);
//	        paintCircle.setAntiAlias(true);
//	        paintCircle.setStrokeWidth(borderWidth);
//	        canvas.drawCircle(getWidth() / 2, getHeight() / 2, r, paintCircle);*/
//	        //长刻度线
//	        float DEFAULT_LONG_DEGREE_LENGTH = 0f;
//	        int degressNumberSize = 30;
//	        canvas.translate(getWidth() / 2, getHeight() / 2);
//	        Paint paintDegreeNumber = new Paint();
//	        paintDegreeNumber.setTextAlign(Paint.Align.CENTER);
//	        paintDegreeNumber.setTextSize(degressNumberSize);
//	        paintDegreeNumber.setFakeBoldText(true);
//	        for(int i=0;i<12;i++){
//	            float[] temp = calculatePoint((i)*30, r - DEFAULT_LONG_DEGREE_LENGTH - degressNumberSize/2 - 15);
//	            canvas.drawText((i*5)+"", temp[2], temp[3] + degressNumberSize/2-6, paintDegreeNumber);
//	        }
//	        canvas.save();
//	        canvas.restore();
//	        canvas.translate(-getWidth() / 2, -getHeight() / 2);
//	        //canvas.drawLine(0, getHeight()/2, getWidth(),getHeight()/2, paintDegreeNumber);
//	        //canvas.drawLine(getWidth()/2, 0, getWidth()/2,getHeight(), paintDegreeNumber);
//		/*int degressNumberSize = 30;
//       // canvas.translate(getWidth() /2 -20, getHeight() / 2-20);
//        Paint paintDegreeNumber = new Paint();
//        paintouter_Num.setColor(Color.RED);
//        paintouter_Num.setTextAlign(Paint.Align.CENTER);
//        paintouter_Num.setTextSize(degressNumberSize);
//        paintouter_Num.setFakeBoldText(true);
//        r = getWidth()/2-50;
//        for(int i=0;i<12;i++){
//          // float[] temp = calculatePoint((i)*30, r - DEFAULT_LONG_DEGREE_LENGTH - degressNumberSize/2 - 15);
//        	float[] temp = calculatePoint((i)*30, r);
//            canvas.drawText((i*5)+"", temp[2], temp[3] + degressNumberSize/2-6, paintouter_Num);
//        }*/
//	}
//
//	private float[] calculatePoint(float angle, float length){
//        float[] points = new float[4];
//        if(angle <= 90f){
//            points[0] = -(float) Math.sin(angle*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
//            points[1] = (float) Math.cos(angle*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
//            points[2] = (float) Math.sin(angle*Math.PI/180) * length;
//            points[3] = -(float) Math.cos(angle*Math.PI/180) * length;
//        }else if(angle <= 180f){
//            points[0] = -(float) Math.cos((angle-90)*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
//            points[1] = -(float) Math.sin((angle-90)*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
//            points[2] = (float) Math.cos((angle-90)*Math.PI/180) * length;
//            points[3] = (float) Math.sin((angle-90)*Math.PI/180) * length;
//        }else if(angle <= 270f){
//            points[0] = (float) Math.sin((angle-180)*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
//            points[1] = -(float) Math.cos((angle-180)*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
//            points[2] = -(float) Math.sin((angle-180)*Math.PI/180) * length;
//            points[3] = (float) Math.cos((angle-180)*Math.PI/180) * length;
//        }else if(angle <= 360f){
//            points[0] = (float) Math.cos((angle-270)*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
//            points[1] = (float) Math.sin((angle-270)*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
//            points[2] = -(float) Math.cos((angle-270)*Math.PI/180) * length;
//            points[3] = -(float) Math.sin((angle-270)*Math.PI/180) * length;
//        }
//        return points;
//    }
//
//	private void drawerNum1(Canvas canvas) {
//		// canvas.save(); // 璁板綍鐢诲竷鐘舵��
//		paintouter_Num.setTextSize(30);
//		float heigh = (float) getTxtHeight(paintouter_Num);
//		float length = 0;
//		// canvas.rotate(0, getWidth() / 2, getHeight() / 2);
//		// //杩欎釜鏂规硶鏄皢鏁翠釜鐢诲竷鏃嬭浆锛岃捣鐐逛綅缃繕鏄箣鍓嶇敾甯冮噷闈㈢殑150锛�70 鐐�
//		float rAngle = 360 / mTikeCount;
//		int i = 0;
//		length = paintouter_Num.measureText("0");
//		float x = getWidth() / 2-length/2;
//		float y = (float) getTxtHeight(paintouter_Num);
//		canvas.drawText("0", x, y, paintouter_Num);// 鐢诲埢搴�
//		length = paintouter_Num.measureText("5");
//		x = (float) ((float) (getWidth() / 2)
//				* (1 + Math.sin(Math.PI * 30.0 / 180.0)) - length
//				* Math.sin(Math.PI * 30.0 / 180.0));
//		y = (float) ((float) ((float) (getHeight() / 2) * (1 - Math
//				.cos(Math.PI * 30.0 / 180.0))) + getTxtHeight(paintouter_Num)
//				* Math.cos(Math.PI * 30.0 / 180.0));
//		canvas.drawText("5", x, y, paintouter_Num);// 鐢诲埢搴�
//		length = paintouter_Num.measureText("10");
//		x = (float) ((float) ((float) (getWidth() / 2) * (1 + Math
//				.sin(Math.PI * 60.0 / 180.0))) - length
//				* Math.sin(Math.PI * 60.0 / 180.0));
//		y = (float) ((float) (getHeight() / 2)
//				* (1 - Math.cos(Math.PI * 60.0 / 180.0)) + getTxtHeight(paintouter_Num)
//				* Math.cos(Math.PI * 60.0 / 180.0));
//		canvas.drawText("10", x, y, paintouter_Num);// 鐢诲埢搴�
//		length = paintouter_Num.measureText("15");
//		x = getWidth() - length;
//		y = (float) (getHeight() / 2 + getTxtHeight(paintouter_Num) / 2);
//		canvas.drawText("15", x, y, paintouter_Num);// 鐢诲埢搴�
//		length = paintouter_Num.measureText("20");
//		x = (float) ((float) (getWidth() / 2)
//				* (1 + Math.sin(Math.PI * 120.0 / 180.0)) - length
//				* Math.sin(Math.PI * 120.0 / 180.0));
//		y = (float) ((float) ((float) (getHeight() / 2) * (1 - Math
//				.cos(Math.PI * 120.0 / 180.0))) - getTxtHeight(paintouter_Num)
//				* Math.cos(Math.PI * 120.0 / 180.0));
//		canvas.drawText("20", x, y, paintouter_Num);// 鐢诲埢搴�
//		length = paintouter_Num.measureText("25");
//		x = (float) ((float) (getWidth() / 2)
//				* (1 + Math.sin(Math.PI * 150.0 / 180.0)) - length
//				* Math.sin(Math.PI * 150.0 / 180.0)/2);
//		y = (float) ((float) ((float) (getHeight() / 2) * (1 - Math
//				.cos(Math.PI * 150.0 / 180.0))) );
//		// y = 90;
//		canvas.drawText("25", x, y, paintouter_Num);// 鐢诲埢搴�
//		x = getWidth() / 2 - length / 2;
//		y = (float) (getHeight() - getTxtHeight(paintouter_Num) / 2);
//		canvas.drawText("30", x, y, paintouter_Num);// 鐢诲埢搴�
//		length = paintouter_Num.measureText("35");
//		x = (float) ((float) (getWidth() / 2)
//				* (1 + Math.sin(Math.PI * 210 / 180.0)) + length
//				* Math.sin(Math.PI * 210.0 / 180.0));
//		y = (float) ((float) (getHeight() / 2)
//				* (1 - Math.cos(Math.PI * 210 / 180.0)) + (getTxtHeight(paintouter_Num)
//				* Math.cos(Math.PI * 210.0 / 180.0) / 2));
//		canvas.drawText("35", x, y, paintouter_Num);// 鐢诲埢搴�
//		length = paintouter_Num.measureText("40");
//		x = (float) ((float) (getWidth() / 2)
//				* (1 + Math.sin(Math.PI * 240 / 180.0)) + length
//				* Math.sin(Math.PI * 240.0 / 180.0)/2);
//		y = (float) ((float) (getHeight() / 2)
//				* (1 - Math.cos(Math.PI * 240 / 180.0)) + (getTxtHeight(paintouter_Num)
//				* Math.cos(Math.PI * 240.0 / 180.0) / 2));
//		canvas.drawText("40", x, y, paintouter_Num);// 鐢诲埢搴�
//		length = paintouter_Num.measureText("45");
//		x = 0;
//		y = (float) (getHeight() / 2 );
//		canvas.drawText("45", x, y, paintouter_Num);// 鐢诲埢搴�
//		length = paintouter_Num.measureText("50");
//		x = (float) ((float) (getWidth() / 2)
//				* (1 + Math.sin(Math.PI * 300.0 / 180.0)));
//		y = (float) ((float) (getHeight() / 2)
//				* (1 - Math.cos(Math.PI * 300.0 / 180.0)) + (getTxtHeight(paintouter_Num)
//				* Math.cos(Math.PI * 300.0 / 180.0) ));
//		canvas.drawText("50", x, y, paintouter_Num);// 鐢诲埢搴�
//		length = paintouter_Num.measureText("55");
//		x = (float) ((float) (getWidth() / 2)
//				* (1 + Math.sin(Math.PI * 330.0 / 180.0)) + length
//				* Math.sin(Math.PI * 330.0 / 180.0));
//		y = (float) ((float) (getHeight() / 2)
//				* (1 - Math.cos(Math.PI * 330.0 / 180.0)) + (getTxtHeight(paintouter_Num)
//				* Math.cos(Math.PI * 330.0 / 180.0) ));
//		canvas.drawText("55", x, y, paintouter_Num);// 鐢诲埢搴�
//		/*
//		 * for (int i = 0; i < mTikeCount; i++) { //canvas.save(); // 璁板綍鐢诲竷鐘舵��
//		 * //canvas.rotate(rAngle * i, getWidth() / 2, getHeight() / 2); mText =
//		 * "" + i * 5; length = paintouter_Num.measureText(mText); float x =
//		 * (float) ((getWidth() / 2) * (1-Math.sin((rAngle * i)*Math.PI/180)));
//		 * float y = (float) ((getHeight() / 2) * (1-Math.cos((rAngle *
//		 * i)*Math.PI/180))); canvas.drawText(mText, x, y, paintouter_Num);//
//		 * 鐢诲埢搴� canvas.drawBitmap(bitmap, getWidth() / 2-bitmap.getWidth()/2,
//		 * 50, paintouter_Num); //canvas.restore(); }
//		 */
//
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//		case MotionEvent.ACTION_MOVE:
//			float x = event.getX();
//			float y = event.getY();
//			float r = this.getWidth() / 2 - s - strokeWidth;// 鍦嗙幆鍗婂緞
//			// 鍒ゆ柇鍦ㄥ渾鐜 鍒欏鐞嗚Е鎽镐簨浠�
//			if (Math.pow(x - this.getWidth() / 2, 2)
//					+ Math.pow(y - this.getWidth() / 2, 2) > Math.pow(r, 2)) {
//				double angle = Math.atan((this.getWidth() / 2 - x)
//						/ (this.getWidth() / 2 - y));
//				angle = angle / Math.PI * 180;
//				if (x > this.getWidth() / 2 && y <= this.getWidth() / 2) {// 绗竴璞￠檺
//					angle += 360;
//				} else if (y > this.getWidth() / 2) {// 绗笁鍥涜薄闄�
//					angle += 180;
//				}
//				if (Math.abs(this.angle - angle) > 1) {
//					this.angle = (float) angle;
//					this.invalidate();
//				}
//			}
//
//			return true;
//
//		case MotionEvent.ACTION_UP:
//			x = event.getX();
//			y = event.getY();
//			if (x > time1Position[0] && x < time1Position[0] + time1Position[2]
//					&& y > time1Position[1]
//					&& y < time1Position[1] + time1Position[3]) {
//				timeType = 1;
//				this.invalidate();
//			} else if (x > time2Position[0]
//					&& x < time2Position[0] + time2Position[2]
//					&& y > time2Position[1]
//					&& y < time2Position[1] + time2Position[3]) {
//				timeType = 2;
//				this.invalidate();
//			} else if (x > time3Position[0]
//					&& x < time3Position[0] + time3Position[2]
//					&& y > time3Position[1]
//					&& y < time3Position[1] + time3Position[3]) {
//				timeType = 3;
//				this.invalidate();
//			}
//
//		}
//		return super.onTouchEvent(event);
//	}
//
//	// 鑾峰彇褰撳墠閫夋嫨鐨勬瘮渚�
//	public float getRate() {
//		return angle / 360f;
//	}
//
//	public int getColor_start() {
//		return color_start;
//	}
//
//	public void setColor_start(int color_start) {
//		this.color_start = color_start;
//	}
//
//	public int getColor_end() {
//		return color_end;
//	}
//
//	public void setColor_end(int color_end) {
//		this.color_end = color_end;
//	}
//
//	public float getCircleDimensionRate() {
//		return circleDimensionRate;
//	}
//
//	public void setCircleDimensionRate(float circleDimensionRate) {
//		this.circleDimensionRate = circleDimensionRate;
//	}
//
//	public float getTriangleDimensionRate() {
//		return triangleDimensionRate;
//	}
//
//	public void setTriangleDimensionRate(float triangleDimensionRate) {
//		this.triangleDimensionRate = triangleDimensionRate;
//	}
//
//}