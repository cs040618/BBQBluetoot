package fmgtech.grillprobee.barbecue;

import fmgtech.grillprobee.barbecue.utils.DataUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TemperaturePanelView extends View {

	private int color_start;
	private int color_end;
	private float circleDimensionRate = 0.1f;// 鍦嗙幆姣斾緥
	private float triangleDimensionRate = 0.1f;// 涓夎褰㈡瘮渚�
	//指针反向超过圆点的长度
	private static final float DEFAULT_POINT_BACK_LENGTH = 40f;
	private float s;// 涓夎褰㈣竟闀�
	private float strokeWidth;// 鍦嗙幆瀹藉害
	private float angle = 0;// 瑙掑害0-360
	private RectF oval = new RectF();
	private Paint paint = new Paint();
	Rect rect = new Rect();// 娴嬮噺鏂囧瓧鎵�鍗犵殑楂樺害瀹藉害
	private int mTikeCount = 17; //時鐘計數
	private int mPointCount = 86;
	private Paint paintouter_Num;// 澶栧姬鐨勫埢搴︾殑鐢荤瑪
	private Context mContext;
	private Paint paint_text;// 鏂囧瓧鐢荤瑪
	int mTextSize;
	String mText;
	String text ;
	private int mMinCircleRadius = 15; // 最小圓半徑
	private Paint paint_centerPoint_Pointer;// 鍐呭渾鐢荤瑪

	public TemperaturePanelView(Context context) {
		super(context);
		mContext = context;
		init(null, 0);
	}

	public TemperaturePanelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(attrs, 0);
	}

	public TemperaturePanelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init(attrs, defStyle);
	}


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	private void init(AttributeSet attrs, int defStyle) {
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.MyView, defStyle, 0);

		color_start = a.getColor(R.styleable.MyView_color_start, Color.YELLOW);// 榛樿缁胯壊
		color_end = a.getColor(R.styleable.MyView_color_end, Color.WHITE);// 榛樿鐏拌壊
		circleDimensionRate = a.getFloat(
				R.styleable.MyView_circleDimensionRate, 0.1f);
		triangleDimensionRate = a.getFloat(
				R.styleable.MyView_triangleDimensionRate, 0.1f);

		paintouter_Num = new Paint();
		paintouter_Num.setAntiAlias(true);
		paintouter_Num.setColor(Color.WHITE);
		paintouter_Num.setStyle(Paint.Style.FILL);// 绌哄績鐢荤瑪
		paintouter_Num.setStrokeWidth(1);

		paint_text = new Paint();
		paint_text.setAntiAlias(true);
		paint_text.setStrokeWidth(1);
		paint_text.setStyle(Paint.Style.FILL);// 瀹炲績鐢荤瑪

		paint_centerPoint_Pointer = new Paint();
		paint_centerPoint_Pointer.setAntiAlias(true);
		paint_centerPoint_Pointer.setStyle(Paint.Style.FILL);// 瀹炲績鐢荤瑪
		a.recycle();

	}

	private void initDrawVar() {
		int width = getWidth();
		s = triangleDimensionRate * width;// 涓夎褰㈣竟闀�
		strokeWidth = circleDimensionRate * width * 1.5f;// 鍦嗙幆瀹藉害
		// 缁樺埗鍦嗙幆鐨勭煩褰㈠尯鍩�
		oval.set(s + strokeWidth / 2f, s + strokeWidth / 2f, width - s
				- strokeWidth / 2f, width - s - strokeWidth / 2f);
	}

	private void drawerRecAndText(Canvas canvas) {
		canvas.save(); //璁板綍鐢诲竷鐘舵��
		canvas.rotate( 2, getWidth() / 2, getHeight() / 2);
		float rAngle = (float)300 / mPointCount;
		for (int i = 0; i < mPointCount; i++) {
			canvas.save(); //璁板綍鐢诲竷鐘舵��
			canvas.rotate(rAngle * i, getWidth() / 2, getHeight() / 2);
			if (i % 5 == 0) {
				mMinCircleRadius = 3;
				paint_centerPoint_Pointer.setColor(Color.GREEN);
			} else {
				mMinCircleRadius = 2;
				paint_centerPoint_Pointer.setColor(Color.LTGRAY);
			}
			canvas.drawCircle(getWidth() / 2, s + strokeWidth / 2,
					mMinCircleRadius, paint_centerPoint_Pointer);
			canvas.restore();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int realWidth = startMeasure(widthMeasureSpec);
		int realHeight = startMeasure(heightMeasureSpec);

		setMeasuredDimension(realWidth, realHeight);
	}

	private int startMeasure(int msSpec) {
		int result = 0;
		int mode = MeasureSpec.getMode(msSpec);
		int size = MeasureSpec.getSize(msSpec);
		if (mode == MeasureSpec.EXACTLY) {
			result = size;
		} else {
			if (size < 1080) {
				result = PxUtils.dpToPx(300, mContext);
			} else {
				result = PxUtils.dpToPx(250, mContext);
			}
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawerNum(canvas);
		initDrawVar();

		// 鐢荤豢鑹茬幆褰�
		paint.setAntiAlias(true);
		paint.setColor(color_end);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Paint.Style.STROKE); // 璁剧疆绌哄績
		// 姝ｅ彸鏂逛负0搴︼紝椤烘椂閽堟棆杞紝姝ｄ笂鏂逛负270搴�
		canvas.drawArc(oval, 270, 300, false, paint);
		// 鐢荤伆鑹茬幆褰�
		if (angle != 0) {
			paint.setColor(color_start);
			canvas.drawArc(oval, 270, angle, false, paint);
		}

		drawerRecAndText(canvas);
		paint.setStrokeWidth(1);
		paint.setColor(color_start);
		paint.setStyle(Paint.Style.FILL);
		// 鏄剧ず鐧惧垎姣�
		paint.setTextSize(getWidth() * 0.1f);
		int temperature = Math.round(-40+(angle / 300 * 100 * 240
				/ 100));
		if(DataUtils.temperatureUnit==0){
			text = temperature+DataUtils.centigrade;
		}else{
			text = DataUtils.centigrade2Fahrenhite(temperature)+DataUtils.fahrenhite;
		}
		canvas.rotate(150, getMeasuredWidth()/2, getMeasuredHeight()/2);
		// paint.getFontMetrics()鏂规硶娴嬮噺涓嶅噯纭紝鏃犳硶淇濊瘉灞呬腑鏄剧ず锛沺aint.getTextBounds鏂规硶娴嬮噺杈冨噯纭�
		paint.getTextBounds(text, 0, text.length(), rect);// 娴嬮噺text鎵�鍗犲搴﹀拰楂樺害

		canvas.drawText(text, getWidth() / 2f - rect.width() / 2, getWidth()
				/ 2f + rect.height() / 2 , paint);
	}

	public double getTxtHeight(Paint mPaint) {
		Paint.FontMetrics fm = mPaint.getFontMetrics();
		return Math.ceil(fm.descent - fm.ascent);
	}

	private void drawerNum1(Canvas canvas) {
		canvas.save(); // 璁板綍鐢诲竷鐘舵��
		paintouter_Num.setTextSize(30);
		float heigh = (float) getTxtHeight(paintouter_Num);
		float length = 0;
		canvas.rotate(210, getWidth() / 2, getHeight() / 2); //杩欎釜鏂规硶鏄皢鏁翠釜鐢诲竷鏃嬭浆锛岃捣鐐逛綅缃繕鏄箣鍓嶇敾甯冮噷闈㈢殑150锛�70 鐐�
		float rAngle = 300 / mTikeCount;
		switch (DataUtils.temperatureUnit) {
			case 0:
				for (int i = 0; i < mTikeCount; i++) {
					canvas.save(); // 璁板綍鐢诲竷鐘舵��
					canvas.rotate(300* i / mTikeCount + (i+2.5f)*0.7f +2 , getWidth() / 2, getHeight() / 2);
					mText = "" + i * 5;
					length = paintouter_Num.measureText(mText);
					canvas.drawText(mText, getWidth() / 2-length/2, heigh, paintouter_Num);// 鐢诲埢搴�
					canvas.restore();
				}
				break;
			case 1:
				for (int i = 0; i < mTikeCount; i++) {
					canvas.save(); // 璁板綍鐢诲竷鐘舵��
					canvas.rotate(300* i / mTikeCount + (i+2.5f)*0.7f +2 , getWidth() / 2, getHeight() / 2);
					mText = "" + (int)DataUtils.centigrade2Fahrenhite(i * 5);
					length = paintouter_Num.measureText(mText);
					canvas.drawText(mText, getWidth() / 2-length/2, heigh, paintouter_Num);// 鐢诲埢搴�
					canvas.restore();
				}
				break;
		}
	}



	private void drawerNum(Canvas canvas){
		//外圆边框宽度
		float DEFAULT_BORDER_WIDTH = 6f;
		float borderWidth = DEFAULT_BORDER_WIDTH;
		float r = Math.min(getHeight() / 2, getWidth() / 2) - borderWidth / 2;
	       /* Paint paintCircle = new Paint();
	        paintCircle.setStyle(Paint.Style.STROKE);
	        paintCircle.setAntiAlias(true);
	        paintCircle.setStrokeWidth(borderWidth);
	        canvas.drawCircle(getWidth() / 2, getHeight() / 2, r, paintCircle);*/
		mTikeCount = 17;
		float rAngle = 300 / mTikeCount;
		//长刻度线
		float DEFAULT_LONG_DEGREE_LENGTH = 0f;
		int degressNumberSize = 21;
		canvas.translate(getWidth() / 2, getHeight() / 2);
		Paint paintDegreeNumber = new Paint();
		paintDegreeNumber.setTextAlign(Paint.Align.CENTER);
		paintDegreeNumber.setTextSize(degressNumberSize);
		paintDegreeNumber.setFakeBoldText(true);
		switch (DataUtils.temperatureUnit) {
			case 0:
				for(int i=0;i<=16;i++){
					rAngle = 210+(float)((i)*18.75);//17.4
					if(rAngle>360){
						rAngle = (210+(float)((i)*18.75))-360;
					}
					float[] temp = calculatePoint(rAngle, r - DEFAULT_LONG_DEGREE_LENGTH - degressNumberSize/2 - 15);
					canvas.drawText(-40+(i*15)+"", temp[2], temp[3] + degressNumberSize/2-6, paintDegreeNumber);
				}
				break;
			case 1:
				for(int i=0;i<=16;i++){
					rAngle = 210+(float)((i)*18.75);//17.4
					if(rAngle>360){
						rAngle = (210+(float)((i)*18.75))-360;
					}
					float[] temp = calculatePoint(rAngle, r - DEFAULT_LONG_DEGREE_LENGTH - degressNumberSize/2 - 15);
					canvas.drawText(-40+(int)DataUtils.centigrade2Fahrenhite(i * 15)+"", temp[2], temp[3] + degressNumberSize/2-6, paintDegreeNumber);
				}
				break;
		}
		canvas.save();
		canvas.restore();
		canvas.translate(-getWidth() / 2, -getHeight() / 2);
		canvas.rotate(210, getWidth() / 2, getHeight() / 2);
		//canvas.rotate(210);
		//canvas.drawLine(0, getHeight()/2, getWidth(),getHeight()/2, paintDegreeNumber);
		//canvas.drawLine(getWidth()/2, 0, getWidth()/2,getHeight(), paintDegreeNumber);
		/*int degressNumberSize = 30;
      // canvas.translate(getWidth() /2 -20, getHeight() / 2-20);
       Paint paintDegreeNumber = new Paint();
       paintouter_Num.setColor(Color.RED);
       paintouter_Num.setTextAlign(Paint.Align.CENTER);
       paintouter_Num.setTextSize(degressNumberSize);
       paintouter_Num.setFakeBoldText(true);
       r = getWidth()/2-50;
       for(int i=0;i<12;i++){
         // float[] temp = calculatePoint((i)*30, r - DEFAULT_LONG_DEGREE_LENGTH - degressNumberSize/2 - 15);
       	float[] temp = calculatePoint((i)*30, r);
           canvas.drawText((i*5)+"", temp[2], temp[3] + degressNumberSize/2-6, paintouter_Num);
       }*/
	}

	private float[] calculatePoint(float angle, float length){
		float[] points = new float[4];
		if(angle <= 90f){
			points[0] = -(float) Math.sin(angle*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
			points[1] = (float) Math.cos(angle*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
			points[2] = (float) Math.sin(angle*Math.PI/180) * length;
			points[3] = -(float) Math.cos(angle*Math.PI/180) * length;
		}else if(angle <= 180f){
			points[0] = -(float) Math.cos((angle-90)*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
			points[1] = -(float) Math.sin((angle-90)*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
			points[2] = (float) Math.cos((angle-90)*Math.PI/180) * length;
			points[3] = (float) Math.sin((angle-90)*Math.PI/180) * length;
		}else if(angle <= 270f){
			points[0] = (float) Math.sin((angle-180)*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
			points[1] = -(float) Math.cos((angle-180)*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
			points[2] = -(float) Math.sin((angle-180)*Math.PI/180) * length;
			points[3] = (float) Math.cos((angle-180)*Math.PI/180) * length;
		}else if(angle <= 360f){
			points[0] = (float) Math.cos((angle-270)*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
			points[1] = (float) Math.sin((angle-270)*Math.PI/180) * DEFAULT_POINT_BACK_LENGTH;
			points[2] = -(float) Math.cos((angle-270)*Math.PI/180) * length;
			points[3] = -(float) Math.sin((angle-270)*Math.PI/180) * length;
		}
		return points;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				float x = event.getX();
				float y = event.getY();
				float r = this.getWidth() / 2 - s - strokeWidth;// 鍦嗙幆鍗婂緞
				// 鍒ゆ柇鍦ㄥ渾鐜 鍒欏鐞嗚Е鎽镐簨浠�
				if (Math.pow(x - this.getWidth() / 2, 2)
						+ Math.pow(y - this.getWidth() / 2, 2) > Math.pow(r, 2)) {
					double angle = Math.atan((this.getWidth() / 2 - x)
							/ (this.getWidth() / 2 - y));
					angle = angle / Math.PI * 180;
					if (x > this.getWidth() / 2 && y <= this.getWidth() / 2) {// 绗竴璞￠檺
						angle = (-angle) +150;
						this.angle = (float) (angle);
						this.invalidate();
					} else if (y > this.getWidth() / 2) {// 绗笁鍥涜薄闄�
						//angle += 180;
						if((angle>0 && angle<30)||(angle<0 && angle>-30)){

						}else{
							if(angle<-30){
								if(angle<-30f && angle>-30.5f){
									angle = 0;
								}else{
									angle=-(angle+30);
								}
								this.angle = (float) (angle);
								this.invalidate();
							}else if(angle>=30){
								if(angle<31f){
									angle = 300;
								}else{
									angle = (330-angle);
								}
								this.angle = (float) (angle);
								this.invalidate();
							}
						}
					}else{
						angle = 150 - angle;
						this.angle = (float) (angle);
						this.invalidate();
					}
				}
				return true;
		}
		return super.onTouchEvent(event);
	}


}