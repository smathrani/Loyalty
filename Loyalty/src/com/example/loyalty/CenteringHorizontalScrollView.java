package com.example.loyalty;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class CenteringHorizontalScrollView extends ScrollView implements View.OnTouchListener {
/*
private Context mContext;

private static final int SWIPE_PAGE_ON_FACTOR = 10;

private int mActiveItem;

private float mPrevScrollY;

private boolean mStart;

private int mItemWidth;

final int MAIN = 300;
final int SECOND = 250;
final int THIRD = 50;

long time;

View targetLeft, targetRight;
ImageView leftImage, rightImage;

public CenteringHorizontalScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);

    mContext=context;
    mItemWidth = 100; // or whatever your item width is.
    setOnTouchListener(this);
}

@Override
public boolean onTouch(View v, MotionEvent event) {
    int y = (int) event.getRawY();

    boolean handled = false;
    switch (event.getAction()) {
        case MotionEvent.ACTION_MOVE:
            if (mStart) {
                mPrevScrollY = y;
                time = System.nanoTime();
                mStart = false;
            }

            break;
        case MotionEvent.ACTION_UP:
        	long time = System.nanoTime()-this.time;
        	double speed = ((y - mPrevScrollY)*1000000000/time);
        	double disp = y-mPrevScrollY;
        	fling((int) speed);
            mStart = true;
            int minFactor = mItemWidth / SWIPE_PAGE_ON_FACTOR;
            if(Math.abs(disp) > minFactor)
            {
            	mActiveItem -= disp/300;
            	mActiveItem = Math.min(Math.max(0, mActiveItem), getMaxItemCount()-1);
            }
/*            if ((mPrevScrollY - (float) y) > minFactor) {
                if (mActiveItem < getMaxItemCount() - 1) {
                    mActiveItem += (mPrevScrollY-y)/100;
                }
            }
            else if (((float) y - mPrevScrollY) > minFactor) {
                if (mActiveItem > 0) {
                    mActiveItem += (mPrevScrollY-y)/100;
                }
            }
*\
            scrollToActiveItem();

            handled = true;
            break;
    }

    return handled;
}

private int getMaxItemCount() {
    return ((LinearLayout) getChildAt(0)).getChildCount();
}

private LinearLayout getLinearLayout() {
    return (LinearLayout) getChildAt(0);
}

/**
 * Centers the current view the best it can.
 *\
public void centerCurrentItem() {
    if (getMaxItemCount() == 0) {
        return;
    }

    int currentY = getScrollY();
    View targetChild;
    int currentChild = -1;

    do {
        currentChild++;
        targetChild = getLinearLayout().getChildAt(currentChild);
    } while (currentChild < getMaxItemCount() && targetChild.getLeft() < currentY);
//    mActiveItem = 8;
    if (mActiveItem != currentChild) {
        mActiveItem = currentChild;
        scrollToActiveItem();
    }
}

/**
 * Scrolls the list view to the currently active child.
 */
private void scrollToActiveItem() {
	
    int maxItemCount = getMaxItemCount();
    if (maxItemCount == 0) {
        return;
    }

    int targetItem = Math.min(maxItemCount - 1, cur);
    targetItem = Math.max(0, targetItem);
    
//    targetItem = 4;

    cur = targetItem;

    // Scroll so that the target child is centered
    View targetView = getLinearLayout().getChildAt(targetItem);
//    super.smoothScrollTo(0, targetView.getBottom());
    ImageView centerImage = (ImageView)targetView;
    int height=300;//set size of centered image
    LinearLayout.LayoutParams flparams = new LinearLayout.LayoutParams(height, height);
    centerImage.setLayoutParams(flparams);

    for(int i = 0; i < maxItemCount; ++i)
    {
    	if(Math.abs(targetItem-i) > 1)
    	{
    		ImageView othr = (ImageView) getLinearLayout().getChildAt(i);
    		int width = THIRD;
    		LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(width,width);
    		par.setMargins(100, 0,0,0);
    		othr.setLayoutParams(par);
    	}
    	else if(i >= 0 && i < maxItemCount && i != targetItem) {
    		ImageView img = (ImageView) getLinearLayout().getChildAt(i);
    		int width = SECOND;
    		LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(width,width);
    		par.setMargins(25, 0,0,0);
    		img.setLayoutParams(par);
    	}
    }
    for(int i = 0; i < maxItemCount; ++i)
    {
    	int x = 200;
    	
    		ImageView img = (ImageView) getLinearLayout().getChildAt(i);
    		float y = img.getY();
    		int width = FIRST;
//    		if(!vis)
    		{
//    			width = 300-Math.abs(p.y);
    		}
    		LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(width, width);
    		img.setLayoutParams(par);
    	
    }

    int targetLeft = targetView.getTop();
    int childWidth = targetView.getBottom() - targetLeft;

    int width = getHeight() - getPaddingTop() - getPaddingBottom();
    int targetScroll = targetLeft - ((width - childWidth) / 2);

}

/**
 * Sets the current item and centers it.
 * @param currentItem The new current item.
 *\
public void setCurrentItemAndCenter(int currentItem) {
    mActiveItem = currentItem;
    scrollToActiveItem();
}
*/
	GestureDetector gd;
	int cur;
	int itemHeight = 100;
	int FIRST = 300;
	int SECOND = 250;
	int THIRD = 100;
	private static final int SWIPE_PAGE_ON_FACTOR = 10;
	int minFactor = itemHeight / SWIPE_PAGE_ON_FACTOR;
	Animate an;
	
	public class Animate implements Runnable {
		Context context;
		OverScroller mScroller;
		int currX;
		int currY;
		
		public Animate(Context c)
		{
			context = c;
			mScroller = new OverScroller(c);
//			mScroller = new OverScroller(c, new LinearInterpolator());
		}
		
		public void StopScroller() {
			mScroller.abortAnimation();
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mScroller.computeScrollOffset()) {
				currX = mScroller.getCurrX();
				currY = mScroller.getCurrY();
				Log.d("****", "moving");
				scrollToActiveItem();
				post(this);
			}
		}
		
		public boolean onContentFling(MotionEvent e1, MotionEvent e2, float velX, float velY)
		{
			mScroller.fling(currX, currY, (int) velX, (int) velY, 0, 0, -3000, 3000, 500, 500);
//			mScroller.fl
//			scrollToActiveItem();
			post(this);
			return true;
		}
		
	}
	
	public CenteringHorizontalScrollView(Context c, AttributeSet attrs)
	{
		super(c, attrs);
		cur = 0;
		an = new Animate(c);
		setClipChildren(false);
		gd = new GestureDetector(c, new GestureDetector.OnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
					float distanceY) {
				an.mScroller.startScroll((int)e1.getX(),(int) e1.getY(),(int) distanceX,(int) distanceY);
				Log.d("****","scroll");
/*				int dif = (int) distanceY/70;
				cur += dif;
				Log.d(cur+"",dif+"");
				scrollToActiveItem();
				CenteringHorizontalScrollView.this.scrollBy((int)distanceX, (int)distanceY);
*/				return true;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				Log.d("****","fling");
				int dif = (int) (e2.getY() - e1.getY())/50;
				cur -= dif;
				cur = Math.min(Math.max(0, cur), getMaxItemCount()-1);
//				CenteringHorizontalScrollView.this.fling((int) -velocityY);
				an.onContentFling(e1, e2, velocityX, velocityY);
//				scrollToActiveItem();
//				CenteringHorizontalScrollView.this.set
				return true;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
	
	private LinearLayout getLinearLayout() {
	    return (LinearLayout) getChildAt(0);
	}
	
	private int getMaxItemCount() {
	    return ((LinearLayout) getChildAt(0)).getChildCount();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		gd.onTouchEvent(event);
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		gd.onTouchEvent(ev);
		return true;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		gd.onTouchEvent(ev);
		return true;
	}
}
