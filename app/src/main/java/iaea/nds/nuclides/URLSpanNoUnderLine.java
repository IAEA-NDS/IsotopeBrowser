package iaea.nds.nuclides;

import android.text.TextPaint;
import android.text.style.URLSpan;

public class URLSpanNoUnderLine extends URLSpan {


    public URLSpanNoUnderLine(String url) {
        super(url);
    }

    @Override public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        ds.setFakeBoldText(true);
        ds.setARGB(254, 0, 130, 0);
        //ds.setARGB(254, 220, 150, 0);
    }
}
