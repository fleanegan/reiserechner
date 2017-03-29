package com.example.fleanegan.reiserechner;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by fleanegan on 18.02.17.
 */

public class Animations {
    static Integer factor = 3;

    public static void collapse(final View v, final Integer f) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) ((initialHeight - f) * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight * Animations.factor / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    public static void expand(final View v, int dest, final int beg) {
        final Integer targetHeight = dest;

        v.setVisibility(View.INVISIBLE);
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) ((targetHeight - beg) * interpolatedTime) + beg;
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight * Animations.factor / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static ValueAnimator slideAnimator(int start, int end, final View view, int duration) {
        view.setVisibility(View.VISIBLE);
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = val;
                view.setLayoutParams(layoutParams);
            }
        });
        animator.setDuration(duration);
        return animator;
    }

    public static ValueAnimator alphaAnimator(boolean isFadingIn, final View view, int duration) {
        view.setVisibility(View.VISIBLE);
        ValueAnimator animator;
        if (isFadingIn) animator = ValueAnimator.ofInt(0, 1);
        else animator = ValueAnimator.ofInt(1, 0);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                view.setAlpha(val);
            }
        });
        animator.setDuration(duration);
        return animator;
    }
}
