package com.mms.interpolator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

    private static final float[] FACTORS = {0.1f, 0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 2.5f, 3.0f};
    private static final int TEXT_SIZE = 12;
    private static final int ANIMATION_DURATION = 5000;
    private static final int START_DELAY = 3000;
    private static final int FINISH_DELAY = 3000;
    private static final int ANIMATION_DELAY = 1000;

    private LinearLayout root;
    private TextView title;
    private List<Integer> colorList;
    private Point displaySize;
    private int maxTextWidth;
    private int margin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displaySize = getDisplaySize(this);
        margin = (int) (displaySize.x * 0.1);
        colorList = getColorList();
        root = (LinearLayout) findViewById(R.id.root);
        title = (TextView) findViewById(R.id.title);
        addViews(getInterpolatorList(), null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(START_DELAY);

                    List<Class> interpolatorClassList = new ArrayList<>();
                    interpolatorClassList.add(null);
                    interpolatorClassList.add(AccelerateInterpolator.class);
                    interpolatorClassList.add(DecelerateInterpolator.class);
                    interpolatorClassList.add(OvershootInterpolator.class);
                    interpolatorClassList.add(AnticipateInterpolator.class);
                    interpolatorClassList.add(AnticipateOvershootInterpolator.class);

                    for (Class aClass : interpolatorClassList) {
                        List<Interpolator> interpolatorList = getInterpolatorList(aClass);
                        addViewsOnUI(interpolatorList, aClass);
                        Thread.sleep(ANIMATION_DELAY);
                        startAnimationOnUI(interpolatorList);
                        Thread.sleep(ANIMATION_DELAY + ANIMATION_DURATION);
                        clearAnimationOnUI();
                        Thread.sleep(ANIMATION_DELAY);
                        startAnimationOnUI(interpolatorList);
                        Thread.sleep(ANIMATION_DELAY + ANIMATION_DURATION);
                    }

                    Thread.sleep(FINISH_DELAY);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullScreenCall();
    }

    private void fullScreenCall() {
        if (Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void clearAnimationOnUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clearAnimation();
            }
        });
    }

    private void startAnimationOnUI(final List<Interpolator> interpolatorList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startAnimation(interpolatorList);
            }
        });
    }

    private void addViewsOnUI(final List<Interpolator> interpolatorList, final Class aClass) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addViews(interpolatorList, aClass);
            }
        });
    }

    private void clearAnimation() {
        for (int i = 0; i < root.getChildCount(); i++) {
            View view = root.getChildAt(i);
            view.clearAnimation();
        }
    }

    private void startAnimation(List<Interpolator> interpolatorList) {
        for (int i = 0; i < interpolatorList.size(); i++) {
            Interpolator interpolator = interpolatorList.get(i);
            View view = root.getChildAt(i);
            TranslateAnimation animation = new TranslateAnimation(0, displaySize.x - maxTextWidth - 2 * margin, 0, 0);
            animation.setFillAfter(true);
            animation.setDuration(ANIMATION_DURATION);
            if (interpolator != null) {
                animation.setInterpolator(interpolator);
            }
            view.startAnimation(animation);
        }
    }

    private void addViews(List<Interpolator> interpolatorList, Class aClass) {
        if (aClass == null) {
            title.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.VISIBLE);
            title.setText(aClass.getSimpleName());
        }
        root.removeAllViews();
        maxTextWidth = 0;
        List<View> views = new ArrayList<>();
        for (int i = 0; i < interpolatorList.size(); i++) {
            TextView view = new TextView(this);
            view.setTextColor(Color.WHITE);
            view.setTextSize(TEXT_SIZE);
            view.setTypeface(Typeface.DEFAULT_BOLD);
            view.setGravity(Gravity.CENTER);
            String interpolatorText;
            if (aClass == null) {
                interpolatorText = interpolatorList.get(i).getClass().getSimpleName().replace("Interpolator", "");
            } else if (aClass.getSimpleName().contains("celerate")) {
                interpolatorText = "factor = " + FACTORS[i];
            } else {
                interpolatorText = "tension = " + FACTORS[i];
            }
            view.setText(interpolatorText);
            view.setBackgroundColor(colorList.get(i));
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f);
            param.setMargins(margin, 0, margin, 0);
            int newTextWidth = getTextWidth(view);
            if (newTextWidth > maxTextWidth) {
                maxTextWidth = newTextWidth;
            }
            root.addView(view, param);
            views.add(view);
        }
        maxTextWidth = (int) (maxTextWidth * 1.1);
        for (View view : views) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = maxTextWidth;
            view.requestLayout();
        }
    }

    private int getTextWidth(TextView textView) {
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(textView.getText().toString(), 0, textView.getText().length(), bounds);
        return bounds.width();
    }

    private static Point getDisplaySize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(displaySize);
        } else {
            display.getSize(displaySize);
        }
        return displaySize;
    }

    private static List<Integer> getColorList() {
        List<Integer> colorList = new ArrayList<>();
        colorList.add(Color.parseColor("#f44336"));
        colorList.add(Color.parseColor("#e91e63"));
        colorList.add(Color.parseColor("#9c27b0"));
        colorList.add(Color.parseColor("#3f51b5"));
        colorList.add(Color.parseColor("#2196f3"));
        colorList.add(Color.parseColor("#009688"));
        colorList.add(Color.parseColor("#689f38"));
        colorList.add(Color.parseColor("#ef6c00"));
        colorList.add(Color.parseColor("#795548"));
        colorList.add(Color.parseColor("#607d8b"));
        colorList.add(Color.parseColor("#827717"));
        Collections.shuffle(colorList);
        return colorList;
    }

    private static List<Interpolator> getInterpolatorList() {
        List<Interpolator> interpolatorList = new ArrayList<>();
        interpolatorList.add(new LinearInterpolator());
        interpolatorList.add(new AccelerateInterpolator());
        interpolatorList.add(new DecelerateInterpolator());
        interpolatorList.add(new AccelerateDecelerateInterpolator());
        interpolatorList.add(new OvershootInterpolator());
        interpolatorList.add(new AnticipateInterpolator());
        interpolatorList.add(new AnticipateOvershootInterpolator());
        interpolatorList.add(new BounceInterpolator());
        interpolatorList.add(new FastOutLinearInInterpolator());
        interpolatorList.add(new FastOutSlowInInterpolator());
        interpolatorList.add(new LinearOutSlowInInterpolator());
        return interpolatorList;
    }

    private static List<Interpolator> getInterpolatorList(Class aClass) {
        if (aClass == null) {
            return getInterpolatorList();
        }
        List<Interpolator> interpolatorList = new ArrayList<>();
        for (float factor : FACTORS) {
            try {
                //noinspection unchecked
                interpolatorList.add((Interpolator) aClass.getConstructor(float.class).newInstance(factor));
            } catch (Exception ignored) {
            }
        }
        return interpolatorList;
    }
}