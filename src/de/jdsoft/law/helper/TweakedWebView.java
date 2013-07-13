package de.jdsoft.law.helper;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;

import java.lang.reflect.Method;

/**
 * See: http://stackoverflow.com/a/16377881/395399
 */
public class TweakedWebView extends WebView {

    private ZoomButtonsController zoomButtons;

    public TweakedWebView(Context context) {
        super(context);
        init();
    }

    public TweakedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TweakedWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSettings().setDisplayZoomControls(false);
        } else {
            try {
                Method method = getClass()
                        .getMethod("getZoomButtonsController");
                zoomButtons = (ZoomButtonsController) method.invoke(this);
            } catch (Exception e) {
                // pass
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = super.onTouchEvent(ev);
        if (zoomButtons != null) {
            zoomButtons.setVisible(false);
            zoomButtons.getZoomControls().setVisibility(View.GONE);
        }
        return false;
    }

}
