package com.victor.loading.book;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import com.victor.loading.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BookLoading extends FrameLayout {
    private static final int DELAYED = 200;
    private static final long DURATION = 1000;
    private static final int PAGE_NUM = 5;
    private BookHandler bookHandler;
    private boolean isStart;
    private ArrayList<PageView> pageViews;

    static class BookHandler extends Handler {
        private WeakReference<BookLoading> weakReference;

        public BookHandler(BookLoading bookLoading) {
            this.weakReference = new WeakReference(bookLoading);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            BookLoading bookLoading = (BookLoading) this.weakReference.get();
            if (bookLoading != null) {
                bookLoading.playAnim();
                sendMessageDelayed(obtainMessage(), 5000);
            }
        }
    }

    public BookLoading(Context context) {
        super(context);
        initView(context);
    }

    public BookLoading(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public BookLoading(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.book_loading, this, true);
        this.pageViews = new ArrayList();
        this.bookHandler = new BookHandler(this);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        addPage();
    }

    private void addPage() {
        LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
        for (int i = 0; i < 5; i++) {
            View pageView = new PageView(getContext());
            addView(pageView, layoutParams);
            pageView.setTag(R.string.app_name, Integer.valueOf(i));
            this.pageViews.add(pageView);
        }
    }

    private void playAnim() {
        setAnim((View) this.pageViews.get(4), 200);
        setAnim((View) this.pageViews.get(4), 1200);
        setAnim((View) this.pageViews.get(3), 1400);
        for (int i = 4; i >= 0; i--) {
            setAnim((View) this.pageViews.get(i), 3000 + ((long) (((4 - i) * DELAYED) / 2)));
        }
    }

    private void setAnim(final View view, long j) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "rotationY", new float[]{0.0f, -180.0f});
        ofFloat.setDuration(DURATION);
        ofFloat.setStartDelay(j);
        ofFloat.setInterpolator(new LinearInterpolator());
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            boolean change = null;

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (valueAnimator.getCurrentPlayTime() > 500 && this.change == null) {
                    this.change = true;
                    view.bringToFront();
                }
            }
        });
        ofFloat.addListener(new AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
                if (((Integer) view.getTag(R.string.app_name)).intValue() == 4) {
                    view.bringToFront();
                }
            }
        });
        ofFloat.start();
    }

    public void start() {
        this.isStart = true;
        this.bookHandler.obtainMessage().sendToTarget();
    }

    public void stop() {
        this.isStart = false;
        this.bookHandler.removeCallbacks(null);
        this.bookHandler.removeCallbacksAndMessages(null);
    }

    public boolean isStart() {
        return this.isStart;
    }
}
