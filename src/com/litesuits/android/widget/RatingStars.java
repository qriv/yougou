package com.litesuits.android.widget;

import android.view.View;
import android.widget.ImageView;
import com.gsh.app.client.mall.R;

/**
 * @author Tan Chunmao
 */
public class RatingStars {
    public static void rate(View container, float rate) {
        for (int i = 0; i < 5; i++) {
            ImageView imageView = (ImageView) container.findViewById(R.id.star_a + i);
            rate = rate < 0.0f ? 0.0f : rate;
            rate = rate > 5.0f ? 5.0f : rate;
            int doubleRate = Math.round(rate * 2);
            lightStar(imageView, i, doubleRate);
        }
    }

    private static void lightStar(ImageView imageView, int index, float doubleRate) {
        float halfRate = index * 2 + 1;
        int iconRid = R.drawable.ui_star_stroke;
        if (doubleRate == halfRate)
            iconRid = R.drawable.ui_star_half;
        else if (doubleRate > halfRate)
            iconRid = R.drawable.ui_star_solid;
        imageView.setImageResource(iconRid);
    }
}
