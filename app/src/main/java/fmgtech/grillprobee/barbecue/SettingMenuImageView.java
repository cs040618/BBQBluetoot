package fmgtech.grillprobee.barbecue;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import fmgtech.grillprobee.barbecue.R;


public class SettingMenuImageView extends View { //設置菜單圖像視圖
	private Paint paint = new Paint();
	private Context mContext;
	Bitmap mBitmap1, mBitmap2, mBitmap3;
	private int type = 0;

	public SettingMenuImageView(Context context) {
		super(context);
		mContext = context;
		init(null, 0);
	}

	public SettingMenuImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(attrs, 0);
	}

	public SettingMenuImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init(attrs, defStyle);
	}
	
	
	public int getType() {
		return type;
	}


	public Bitmap getmBitmap1() {
		return mBitmap1;
	}

	public void setmBitmap1(Bitmap mBitmap1) {
		this.mBitmap1 = mBitmap1;
	}

	private void init(AttributeSet attrs, int defStyle) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.MyView, defStyle, 0);	
		mBitmap1 = ((BitmapDrawable) mContext.getResources().getDrawable(
				R.drawable.menu)).getBitmap();
		mBitmap2 = ((BitmapDrawable) mContext.getResources().getDrawable(
				R.drawable.setting_connect)).getBitmap();
		mBitmap3 = ((BitmapDrawable) mContext.getResources().getDrawable(
				R.drawable.setting_disconnect)).getBitmap();
		a.recycle();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int realWidth = mBitmap1.getWidth();
		int realHeight = mBitmap1.getHeight();

		setMeasuredDimension(realWidth, realHeight);
	}

	private int startMeasure(int msSpec) {
		int result = 0;
		int mode = MeasureSpec.getMode(msSpec);
		int size = MeasureSpec.getSize(msSpec);
		if (mode == MeasureSpec.EXACTLY) {
			result = size;
		} else {
			result = PxUtils.dpToPx(200, mContext);
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mBitmap1, 0, 0,paint);
		canvas.save(); // 记录画布状态
		if(type == 0){
			canvas.drawBitmap(mBitmap3, (mBitmap1.getWidth()-mBitmap3.getWidth())/2, (mBitmap1.getHeight()-mBitmap3.getHeight())/2,
					paint);
		}else if(type == 1){
			canvas.drawBitmap(mBitmap2, (mBitmap1.getWidth()-mBitmap2.getWidth())/2, (mBitmap1.getHeight()-mBitmap2.getHeight())/2,
					paint);
		}
		canvas.save(); // 记录画布状态

	}
	
	public void update(int type){
		this.type = type;
		invalidate();
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			return true;
		case MotionEvent.ACTION_UP:
			float x = event.getX();
			float y = event.getY();
			if (x < mBitmap1.getWidth() && y < mBitmap1.getHeight()){
				System.out.println("onTouchEvent=======");
			} 
			return true;
		}
		return super.onTouchEvent(event);
	}

	
	
	  
	
}