package fmgtech.grillprobee.barbecue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by luoanmin on 2020/6/6.
 */

public class CircleView extends View {

    //    定义画笔
    Paint paint;
    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //    重写draw方法
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

//        实例化画笔对象
        paint = new Paint();
//        给画笔设置颜色
        paint.setColor(getContext().getColor(R.color.circle_bg));
//        设置画笔属性
        paint.setStyle(Paint.Style.FILL);//画笔属性是实心圆
        paint.setAntiAlias(true);
        paint.setStrokeWidth(8);//设置画笔粗细

        /*四个参数：
                参数一：圆心的x坐标
                参数二：圆心的y坐标
                参数三：圆的半径
                参数四：定义好的画笔
                */
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, paint);

    }


}