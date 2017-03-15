package com.chuck.contactsdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;

/**
 * 通讯录右边导航条
 */
public class SideBar extends View {
	/**
	 * 需要展示的导航内容
	 */
	public static String[] contentArray = { "↑", "☆", "A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z", "#" };

	private OnTouchTextChangeListener onTouchTextChangeListener;// 监听器
	
	private Paint mPaint = new Paint();//画笔对象
	
	private int choosePosition = -1;//选中位置

	private Context context;
	
	private TextView mToastTextView;//选择某一项时弹出的TextView

	public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
	}

	public SideBar(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public SideBar(Context context) {
		this(context,null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 获取焦点改变背景颜色.  
        int height = getHeight();// 获取视图高度  
        int width = getWidth(); // 获取视图宽度  
        int singleHeight = height / contentArray.length;// 获取每一个字母的高度  
  
        for (int i = 0; i < contentArray.length; i++) {  
        	mPaint.setColor(Color.rgb(86, 86, 86));  
            // paint.setColor(Color.WHITE);  
        	mPaint.setTypeface(Typeface.DEFAULT_BOLD);  
        	mPaint.setAntiAlias(true);  
        	mPaint.setTextSize(CommonUtil.sp2px(context, 15));  
            // x坐标等于中间-字符串宽度的一半.  
            float xPos = width / 2 - mPaint.measureText(contentArray[i]) / 2;  
            float yPos = singleHeight * i + singleHeight;  
            canvas.drawText(contentArray[i], xPos, yPos, mPaint);  
            mPaint.reset();// 重置画笔  
        }  
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();  
        final float y = event.getY();// 点击y坐标  
        final int oldChoose = choosePosition;  
        final OnTouchTextChangeListener listener = onTouchTextChangeListener;  
        final int c = (int) (y / getHeight() * contentArray.length);// 点击y坐标所占总高度的比例*contentArray数组的长度就等于点击b中的个数.  
  
        switch (action) {  
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
        	setBackgroundDrawable(new ColorDrawable(0xE6D6DAE1));
            if (oldChoose != c) {  
                if (c >= 0 && c < contentArray.length) {  
                    if (listener != null) {  
                        listener.onTouchTextChanged(contentArray[c]);  
                    }  
                    if (mToastTextView != null) {  
                    	mToastTextView.setText(contentArray[c]);  
                    	mToastTextView.setVisibility(View.VISIBLE);
						ObjectAnimator.ofFloat(mToastTextView, "translationY", y).start();
                    }  
                    choosePosition = c;
                    invalidate();
                }  
            }
        	break;
        case MotionEvent.ACTION_UP:  
            setBackgroundDrawable(new ColorDrawable(0x00000000));  
            choosePosition = -1;//  
            invalidate();  
            if (mToastTextView != null) {  
            	mToastTextView.setVisibility(View.GONE);  
            }  
            break;  
  
        default:  
            break;  
        }  
        return true; 
	}
	
	public void setToastTextView(TextView tv){
		mToastTextView = tv;
	}
	
	/**
	 * 外部绑定触摸位置变化监听器方法
	 * 
	 * @param onTouchTextChangeListener
	 */
	public void setOnTouchTextChangeListener(
			OnTouchTextChangeListener onTouchTextChangeListener) {
		this.onTouchTextChangeListener = onTouchTextChangeListener;
	}

	/**
	 * 触摸位置改变监听器
	 */
	public interface OnTouchTextChangeListener {
		/**
		 * 触摸位置发生改变后的回调方法
		 * 
		 * @param s
		 *            当前触摸的内容
		 */
		void onTouchTextChanged(String s);
	}

}
