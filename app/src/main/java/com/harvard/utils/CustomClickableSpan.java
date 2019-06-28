package com.harvard.utils;

import android.text.TextPaint;
import android.text.style.ClickableSpan;

/**
 * Created by Rohit on 12/13/2016.
 */

public abstract class CustomClickableSpan extends ClickableSpan {
    public CustomClickableSpan() {
        super();
    }

    public void updateDrawState(TextPaint p_DrawState) {
        super.updateDrawState(p_DrawState);
        p_DrawState.setUnderlineText(false);
    }
}
