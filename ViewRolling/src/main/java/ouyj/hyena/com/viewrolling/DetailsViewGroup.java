package ouyj.hyena.com.viewrolling;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import ouyj.hyena.com.viewrolling.utils.ScreenUtils;

/**
 * 派生自ViewGroup
 * 存放IBaseArticleLayout和IBaseCommentLayout接口的容器
 * 实现IBaseArticleLayout和IBaseCommentLayout接口的上下切换
 */
public class DetailsViewGroup extends ViewGroup {

	private VelocityTracker mVelocityTracker; // 用于判断甩动手势
	private static final int SNAP_VELOCITY = 600; // X轴速度基值，大于该值时进行切换
	private Scroller mScroller; // 滑动控制
	private int mCurScreen; // 当前页面为第几屏
	private int mDefaultScreen = 0;
	private float mLastMotionY;
	private float mLastMotionDispatchY = 0;
	private int touchSlop = 0;
	private int mWebViewHeight = -1;
	private boolean isScrollBy = false;
	private Context mContext;
	private ViewChangeListener mViewChangeListener;

	//详情和评论布局（视图）
	private IArticleLayout articleLayout;
	private ICommentLayout commentLayout;

	/**
	 * 构造方法
	 * @param context
	 */
	public DetailsViewGroup(Context context) {
		super(context);
		init(context);
	}
	public DetailsViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public DetailsViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	private void init(Context context) {
		mContext = context;
		mCurScreen = mDefaultScreen;
		mWebViewHeight = -1;
		mScroller = new Scroller(context);

		//触发移动事件的最小距离
		touchSlop = ScreenUtils.getTouchSlop(mContext);
		touchSlop = 1;
	}


	/**
	 * 由父视图调用，测量当前视图的尺寸
	 * MeasureSpec类：封装父布局传递过来的布局参数
	 * @param widthMeasureSpec
	 * @param heightMeasureSpec
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		//当前视图设置的模式
		//MeasureSpec.AT_MOST（wrap_content）
		//MeasureSpec.EXACTLY（match_parent）
		//MeasureSpec.UNSPECIFIED（可将视图设置成任意大小，没有限制）
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		//由父视图经过计算，传递给子视图的默认宽高
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		//当前视图的子视图数
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			if (0 == i && -1 != mWebViewHeight) {
				getChildAt(i).measure(
						widthMeasureSpec,
						MeasureSpec.EXACTLY + mWebViewHeight
				);
			}
			else {
				getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
			}
		}


		if(-1 != mWebViewHeight)
			scrollTo(0, mCurScreen * mWebViewHeight);
		else
			scrollTo(0, mCurScreen * getHeight());

		//告知父视图，当前视图的尺寸大小
		setMeasuredDimension(widthSize, heightSize);
	}




	public void setWebViewHeight(int webViewHeight) {
		this.mWebViewHeight = webViewHeight;
	}



	/**
	 * 加入详情布局（未实现接口会抛出异常）
	 * @param view
	 */
	public void addArticleView(View view) {
		if (view instanceof IArticleLayout) {
			articleLayout = (IArticleLayout) view;
			addView(view, 0);
		}
		else
			throw new RuntimeException("View No interface IArticleLayout");
	}
	/**
	 * 加入评论布局（未实现接口抛出异常）
	 * @param view
	 */
	public void addCommentView(View view) {
		if (view instanceof ICommentLayout) {
			commentLayout = (ICommentLayout) view;
			addView(view, 1);
		}
		else
			throw new RuntimeException("View No interface ICommentLayout");
	}



	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// if (changed) {
		int childTop = 0;
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {
				int childHeight = 0;
				if (0 == i && -1 != mWebViewHeight) {
					childHeight = mWebViewHeight;
				} else {
					childHeight = childView.getMeasuredHeight();
				}
				childView.layout(0, childTop, childView.getMeasuredWidth(),
						childTop + childHeight);
				childTop += childHeight;
			}
		}
		// }
	}






	public void snapToDestination() {

		int screenHeight = 0;
		if (-1 == mWebViewHeight) {
			screenHeight = getHeight();
		} else {
			screenHeight = mWebViewHeight;
		}

		final int destScreen = (getScrollY() + screenHeight / 2) / screenHeight;
		snapToScreen(destScreen);
	}

	// 使屏幕移动到第whichScreen+1屏
	public void snapToScreen(int whichScreen) {

		int webViewHeight = 0;

		if (-1 == mWebViewHeight) {
			webViewHeight = getHeight();
		} else {
			webViewHeight = mWebViewHeight;
		}

		// if (getScrollY() != (whichScreen * webViewHeight)) {
		final int delta = whichScreen * webViewHeight - getScrollY();
		mScroller.startScroll(0, getScrollY(), 0, delta,
				(int) (Math.abs(delta) * 0.5));
		mCurScreen = whichScreen;
		invalidate();

		if (mViewChangeListener != null) {
			mViewChangeListener.OnViewChange(mCurScreen);
		}
		// }
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
		super.computeScroll();

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
			}
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionY = y;

			break;

		case MotionEvent.ACTION_MOVE:

			int tempDeltaY = (int) (mLastMotionY - y);
			if (tempDeltaY < 0 && Math.abs(tempDeltaY) >= touchSlop) {
				// 向下
				// System.out.println("向下");
				if (isScrollBy || (commentLayout.isFirstViewTop())) {
					if (isScrollBy || 1 == mCurScreen) {
						if (0 == mLastMotionDispatchY) {
							mLastMotionDispatchY = y;
						}
						int deltaY = (int) (mLastMotionDispatchY - y);
						if (IsCanMove(deltaY)) {
							if (mVelocityTracker != null) {
								mVelocityTracker.addMovement(event);
							}
							mLastMotionDispatchY = y;
							articleLayout.setIsScroll(false);
							commentLayout.setIsScroll(false);
							// 正向或者负向移动，屏幕跟随手指移动
							if (deltaY + getScrollY() < 0) {
								scrollTo(0, 0);
							} else {
								scrollBy(0, deltaY);
							}
							// }
						} else {
							return true;
						}
					}
				}
			} else if (tempDeltaY > 0 && Math.abs(tempDeltaY) >= touchSlop) {
				// 向上
				if (isScrollBy || articleLayout.isScrollBottom()) {
					if (isScrollBy || 0 == mCurScreen) {
						if (0 == mLastMotionDispatchY) {
							mLastMotionDispatchY = y;
						}
						int deltaY = (int) (mLastMotionDispatchY - y);
						if (IsCanMove(deltaY)) {
							if (mVelocityTracker != null) {
								mVelocityTracker.addMovement(event);
							}
							mLastMotionY = y;
							mLastMotionDispatchY = y;
							articleLayout.setIsScroll(false);
							commentLayout.setIsScroll(false);
							// 正向或者负向移动，屏幕跟随手指移动
							if (deltaY + getScrollY() > getHeight()) {
								scrollTo(0, getHeight());
							} else {
								scrollBy(0, deltaY);
							}
						} else {
							return true;
						}
					}
				}
			} else {
				return true;
			}

			mLastMotionY = y;

			break;

		case MotionEvent.ACTION_UP:

			isScrollBy = false;
			mLastMotionDispatchY = 0;
			articleLayout.setIsScroll(true);
			commentLayout.setIsScroll(true);

			int velocityY = 0;
			if (articleLayout.isScrollBottom()
					&& commentLayout.isFirstViewTop()
					&& mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				// 得到Y轴方向手指移动速度
				velocityY = (int) mVelocityTracker.getYVelocity();
			}
			if (velocityY > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move top
				snapToScreen(mCurScreen - 1);
			} else if (velocityY < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// Fling enough to move bottom
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			break;
		case MotionEvent.ACTION_CANCEL: {
			break;
		}
		}
		return super.dispatchTouchEvent(event);

	}

	@Override
	public void scrollBy(int x, int y) {
		isScrollBy = true;
		super.scrollBy(x, y);
	}

	private boolean IsCanMove(int deltaY) {

		int webViewHeight = 0;
		if (-1 == mWebViewHeight) {
			webViewHeight = getHeight();
		} else {
			webViewHeight = mWebViewHeight;
		}

		// deltaY<0说明手指向下划
		if (getScrollY() <= 0 && deltaY < 0) {
			return false;
		}
		// deltaX>0说明手指向上划
		if (getScrollY() >= (getChildCount() - 1) * webViewHeight && deltaY > 0) {
			return false;
		}
		return true;
	}

	public void SetOnViewChangeListener(ViewChangeListener listener) {
		mViewChangeListener = listener;
	}

	public int getCurrentItem() {
		return mCurScreen;
	}

	public void setCurrentItem(int item) {
		snapToScreen(item);
	}

	/**
	 * 判断Scroller是否完成
	 * 
	 * @return
	 */
	public boolean isScrollerFinished() {
		return mScroller.isFinished();
	}

}
