package iaea.nds.nuclides.mvvm;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

public class ShowHideSpan extends ReplacementSpan {



    @Override
    public void draw(Canvas arg0, CharSequence arg1, int arg2, int arg3,
                     float arg4, int arg5, int arg6, int arg7, Paint arg8) {}

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        return 0;
    }
}
