package com.example.fleanegan.reiserechner;

import android.content.res.Resources;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
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


    public static void fade(final View v, String inOrOut) throws Resources.NotFoundException {
        if (inOrOut == "out") {
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(1000);
            v.setAnimation(fadeOut);
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                }
            }, 500);
            v.setVisibility(View.INVISIBLE);
        } else {
            v.setVisibility(View.VISIBLE);
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new AccelerateInterpolator());
            fadeIn.setDuration(100);
            v.setAnimation(fadeIn);
        }
    }

}
