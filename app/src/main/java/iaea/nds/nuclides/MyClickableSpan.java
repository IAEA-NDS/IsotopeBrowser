package iaea.nds.nuclides;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * 
 * 
 *
 */
public class MyClickableSpan extends ClickableSpan{
	
	String mytext = "";
	int clickaction = -1;
	public static int actiondetail = 1;
	public static int actionprefs = 2;
	public static int actiondecaychain = 3;
	Context myContext ;
	public void onClick(View textView) {

		if(clickaction == actionprefs){
			Intent nucidIntent = new Intent(myContext, PreferenceActivity.class);
			nucidIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			myContext.startActivity(nucidIntent);
			
		} else if (clickaction == actiondetail){
			Intent nucidIntent = new Intent(myContext, NuclidesActivity.class);
        	nucidIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			BaseActivity.setNucSelectedRowid(mytext);
        	myContext.startActivity(nucidIntent);

		} else if (clickaction == actiondecaychain){

			BaseActivity.progressStart(myContext);
			Intent nucidIntent = new Intent(myContext, LivechartActivity.class);
			nucidIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			BaseActivity.setNucidForDecayChain(mytext);
			BaseActivity.saveChartNZatCentre(new float[0]);

			myContext.startActivity(nucidIntent);

		}
	}
	
    @Override public void updateDrawState(TextPaint ds) {
       super.updateDrawState(ds);
       ds.setUnderlineText(false);
       ds.setFakeBoldText(true);
       ds.setARGB(254, 0, 130, 0);
       //ds.setARGB(254, 150, 150, 250);
    }
}


