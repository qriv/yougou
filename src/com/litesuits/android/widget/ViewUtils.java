package com.litesuits.android.widget;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.litesuits.android.log.Log;

import java.util.List;

/**
 * 界面工具
 *
 * @author Tan Chunmao
 */
public class ViewUtils {
    public static final int UNKNOWN_WIDTH = -1;

    public static void gridFour(Context context, LinearLayout parentLayout, List<ImageView> imageViews, int gapWidth) {
        ViewGroup.LayoutParams horizontalGapLayoutParams = new ViewGroup.LayoutParams(gapWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        ViewGroup.LayoutParams verticalGapLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, gapWidth);
        ViewGroup.LayoutParams layoutLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parentLayout.removeAllViews();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(layoutLayoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        int count = 0;
        for (ImageView view : imageViews) {

            if (count == 2) {
                parentLayout.addView(linearLayout);
                //add a vertical gap
                View verticalGap = new View(context);
                verticalGap.setLayoutParams(verticalGapLayoutParams);
                parentLayout.addView(verticalGap);

                linearLayout = new LinearLayout(context);
                linearLayout.setLayoutParams(layoutLayoutParams);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            }
            linearLayout.addView(view);
            count++;

            if (imageViews.indexOf(view) < imageViews.size() - 1) {
                //add a horizontal gap
                View horizontalGap = new View(context);
                horizontalGap.setLayoutParams(horizontalGapLayoutParams);
                linearLayout.addView(horizontalGap);
            }
        }
        parentLayout.addView(linearLayout);
    }

    public static void gridOne(Context context, LinearLayout parentLayout, ImageView imageView) {
        parentLayout.addView(imageView);
    }

    public static void girdTwo(Context context, LinearLayout parentLayout, List<ImageView> imageViews, int gapWidth) {
        ViewGroup.LayoutParams horizontalGapLayoutParams = new ViewGroup.LayoutParams(gapWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        parentLayout.removeAllViews();
        parentLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (ImageView view : imageViews) {
            parentLayout.addView(view);
            if (imageViews.indexOf(view) < imageViews.size() - 1) {
                //add a horizontal gap
                View horizontalGap = new View(context);
                horizontalGap.setLayoutParams(horizontalGapLayoutParams);
                parentLayout.addView(horizontalGap);
            }
        }
    }

    public static void gridThree(Context context, LinearLayout parentLayout, List<ImageView> imageViews, int gapWidth) {
        ViewGroup.LayoutParams verticalGapLayoutParams = new ViewGroup.LayoutParams(gapWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        ViewGroup.LayoutParams horizontalGapLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, gapWidth);
        ViewGroup.LayoutParams verticalLayoutLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parentLayout.removeAllViews();
        parentLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(verticalLayoutLayoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for (ImageView view : imageViews) {
            if (imageViews.indexOf(view) == 0) {
                parentLayout.addView(view);
                View verticalGap = new View(context);
                verticalGap.setLayoutParams(verticalGapLayoutParams);
                parentLayout.addView(verticalGap);
            } else if (imageViews.indexOf(view) == 1) {
                linearLayout.addView(view);
                View horizontalGap = new View(context);
                horizontalGap.setLayoutParams(horizontalGapLayoutParams);
                linearLayout.addView(horizontalGap);
            } else {
                linearLayout.addView(view);
            }
        }
        parentLayout.addView(linearLayout);
    }

    public static void addViews(Context context, LinearLayout parentLayout, List<? extends View> views, int viewWidth, int gravity, int gapWidth, int leftEdgeWidth) {
        addViews(context, parentLayout, views, viewWidth, gravity, gapWidth, leftEdgeWidth, -1, false);
    }

    public static void addViews(Context context, LinearLayout parentLayout, List<? extends View> views, int viewWidth, int gravity, int gapWidth, int leftEdgeWidth, int gapColor, boolean addBorder) {
        ViewGroup.LayoutParams horizontalGapLayoutParams = new ViewGroup.LayoutParams(gapWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        ViewGroup.LayoutParams verticalGapLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, gapWidth);
        ViewGroup.LayoutParams layoutLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int maxWidth = metrics.widthPixels;
        parentLayout.removeAllViews();
        if (addBorder) {
            addVerticalGap(context, parentLayout, gapColor, verticalGapLayoutParams);
        }
        if (views.size() > 0) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(layoutLayoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(gravity);
            int widthSoFar = leftEdgeWidth;


            for (View view : views) {
                if (viewWidth == UNKNOWN_WIDTH) {
                    view.measure(0, 0);
                    widthSoFar += view.getMeasuredWidth();
                } else {
                    widthSoFar += viewWidth;
                }
                //if a line if filled up, start a new line
                if (widthSoFar > maxWidth) {
                    parentLayout.addView(linearLayout);
                    //add a vertical gap
                    addVerticalGap(context, parentLayout, gapColor, verticalGapLayoutParams);

                    linearLayout = new LinearLayout(context);
                    linearLayout.setLayoutParams(layoutLayoutParams);
                    linearLayout.setGravity(gravity);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                    linearLayout.addView(view);
                    if (viewWidth == UNKNOWN_WIDTH) {
                        view.measure(0, 0);
                        widthSoFar = leftEdgeWidth + view.getMeasuredWidth();
                    } else {
                        widthSoFar = leftEdgeWidth + viewWidth;
                    }
                } else {
                    linearLayout.addView(view);
                }

                if (views.indexOf(view) < views.size() - 1) {
                    //add a horizontal gap
                    View horizontalGap = new View(context);
                    horizontalGap.setLayoutParams(horizontalGapLayoutParams);
                    if (gapColor != -1) {
                        horizontalGap.setBackgroundColor(context.getResources().getColor(gapColor));
                    }
                    linearLayout.addView(horizontalGap);
                    widthSoFar += gapWidth;
                }
            }

            parentLayout.addView(linearLayout);
        }
        if (addBorder) {
            addVerticalGap(context, parentLayout, gapColor, verticalGapLayoutParams);
        }
    }

    private static void addVerticalGap(Context context, LinearLayout parentLayout, int gapColor, ViewGroup.LayoutParams verticalGapLayoutParams) {
        View verticalGap = new View(context);
        if (gapColor != -1) {
            verticalGap.setBackgroundColor(context.getResources().getColor(gapColor));
        }
        verticalGap.setLayoutParams(verticalGapLayoutParams);
        parentLayout.addView(verticalGap);
    }

    public static void addViews(Context context, LinearLayout parentLayout, List<? extends View> views, int viewWidth, int gravity, int gapWidth) {
        final int edgeWidth = context.getResources().getDimensionPixelSize(R.dimen.ui_margin_e) * 2;
        addViews(context, parentLayout, views, viewWidth, gravity, gapWidth, edgeWidth);
    }

    /*动态添加控件
    * */
    public static void addViews(Context context, LinearLayout parentLayout, List<? extends View> views, int viewWidth, int gravity) {
        int gapWidth = context.getResources().getDimensionPixelSize(R.dimen.ui_size_one_dp);
        addViews(context, parentLayout, views, viewWidth, gravity, gapWidth);

    }


}
