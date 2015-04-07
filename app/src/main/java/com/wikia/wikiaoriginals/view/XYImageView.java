package com.wikia.wikiaoriginals.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * ImageView does not fit the image if it's resolution is smaller than the view's dimensions
 * FIT_XY scale type fits the image but it does not keep the image's aspect ratio.
 * This imageview uses scale type as FIT_XY but also keeps the image's aspect ratio
 */
public class XYImageView extends ImageView{
    public XYImageView(Context context) {
        super(context);
        init();
    }

    public XYImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public XYImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setScaleType(ScaleType.FIT_XY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(getDrawable() != null && (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) && widthMeasureSpec != MeasureSpec.AT_MOST){
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            final int insWidth = getDrawable().getIntrinsicWidth();
            final float ratio = (float)getDrawable().getIntrinsicHeight()/insWidth;

            setMeasuredDimension(width, (int)(ratio * width));
        }else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
