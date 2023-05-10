package iaea.nds.mendel;

import iaea.nds.nuclides.Formatter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;



public class PeriodicTablePlot extends HorizontalScrollView {

    int MAX_N = 177;
    int MAX_Z = 118;

	/**
	 * coordinates of the initial touch
	 */
	private float mx0, my0;

	/**
	 * Minimum Difference in pt (on x or y) between mouse down and mouse up to trigger a click
	 */
	private double SHIFT_TOLERANCE ;
    Context myContext;

	PeriodicTableData mendel;

    int elementSeqHighlighted = -1;
    int[] colorHighlight = new int[]{255,90,0};
    
    Paint mystrokePaint = null;
    
    float strokeTextSizeSymbol = 0;
    float strokeTextSizeName = 0;

    String[] elemGroupLabels;
	String[] elemNames;
	String[] elemSymbols;

    
    public PeriodicTablePlot(Context context) {
        super(context);
 
    }
    public PeriodicTablePlot(Context context, AttributeSet attrs) {
        super(context, attrs);
        bringToFront();

        myContext = context;
        // TODO Auto-generated constructor stub
        setWillNotDraw(false) ;

        mendel = null;
        
        mystrokePaint = new Paint();
        mystrokePaint.setARGB(255, 220,95,205);
        mystrokePaint.setStyle(Paint.Style.FILL);
        mystrokePaint.setStrokeWidth(1);
        mystrokePaint.setAntiAlias(true);

        SHIFT_TOLERANCE = 2 * getResources().getDisplayMetrics().xdpi / 25.4;

        elemGroupLabels = Formatter.getElemGroupLabels();
		elemNames = Formatter.getElemNames();
		elemSymbols = Formatter.getElemSymbols();

		int mwidth = ((PeriodicTableActivity)getContext()).initialSize()[0];
		int mheight = ((PeriodicTableActivity)getContext()).initialSize()[1];


    }
    @Override
    protected void onDraw(Canvas canvas) {

        mendel = getPeriodicTableData();
        plotMendel(canvas, mystrokePaint);
    
    }

	private int[] getWidthAndHeightForDrawable(){

		int mwidth = ((PeriodicTableActivity)getContext()).initialSize()[0];
		int mheight = ((PeriodicTableActivity)getContext()).initialSize()[1];

		int width = mwidth;
		int height = mheight;

		//float btWidth = Formatter.convertPixelsToDp(width/18);
		int bitmapWidth = width;
		int bitmapHeight = height;
		/*int minBtWidth = 200;
		if(btWidth < minBtWidth){
			bitmapWidth =  minBtWidth * 18; //(float) (minBtWidth / btWidth) * width;
			bitmapHeight = minBtWidth * 9;//(float) (minBtWidth / btWidth) * height;

		}*/
		return new int[]{ bitmapWidth, bitmapHeight};
	}


    public PeriodicTableData getPeriodicTableData(){
    	if(mendel == null){
			int[] measure = getWidthAndHeightForDrawable();
			int main_width = measure[0];
			int main_height = measure[1];
			((PeriodicTableActivity)getContext()).postinit(main_width);


    		mendel = new PeriodicTableData(main_width, main_height);
    		strokeTextSizeSymbol = (int)( mendel.el[3].width() / 2.5);
    		strokeTextSizeName = mendel.el[3].width() / 6;

    	}
    	return  mendel;
    }
    
    public int getHilightedElement(int x, int y){
    	try {
			for (int i = 0; i < getPeriodicTableData().el.length; i++) {
				
				if(mendel.el[i].contains(x, y)){
					
					return i;
				}
			}
		} catch (Exception e) {
			// put because of crash report. Mendel or el[i] null I do not know why
		}
    	return -1;
    }
 
    
    public int getSelectedElement(int x, int y){
    	try {
			for (int i = 0; i < getPeriodicTableData().el.length; i++) {

				if(mendel.el[i].contains(x, y)){
					
					return PeriodicTableData.getSeq(i);
				}
			}
		} catch (Exception e) {
			// put because of crash report. Mendel or el[i] null I do not know why
		}
    	return -1;
    }
    
    public void setElementSeqHighlighted(int elementSeq){
    	elementSeqHighlighted = elementSeq;
    }
    
  
    
    private void plotMendel(Canvas canvas, Paint strokePaint){

    	int[] color = new int[0];
    	int legendX0 = mendel.el[0].right * 2 + 5;
    	strokePaint.setTextSize(strokeTextSizeName  * 2);
    	strokePaint.setFakeBoldText(true);
    	for (int i = 0; i < Formatter.getElemGroupLabels().length; i++) {
    		strokePaint.setARGB(255
    				,getPeriodicTableData().colorsForElem[i][0]
    				,getPeriodicTableData().colorsForElem[i][1]
    				,getPeriodicTableData().colorsForElem[i][2]);

    		float mxlbl = legendX0 + (i < 5 ? 0 : (float)(legendX0 + mendel.el[i].width()*3));
    		float mylbl = ( (i < 5 ? (strokeTextSizeName  * 2 + 2) * (i+1) : (strokeTextSizeName  * 2 + 2) *((i+1) -5)));

    		canvas.drawText(elemGroupLabels[i]
    				, mxlbl
    				,  mylbl, strokePaint);
		}

		boolean is_rotated = ((PeriodicTableActivity)getContext()).is_rotated;

    	for (int i = 0; i < getPeriodicTableData().el.length; i++) {

    		if( i == elementSeqHighlighted){
    			color = colorHighlight;
    		} else {
    			color = mendel.getColorForRect(mendel.el[i]);
    		}
    		if(color != null){
    			strokePaint.setARGB(255, color[0], color[1], color[2]);
    		} else {
    			strokePaint.setARGB(255, 255,255,255);
    		}
    		
    		
    		canvas.drawRect(mendel.el[i], strokePaint);
    	
    		strokePaint.setARGB(255, 40,40,40);
    		int seq = PeriodicTableData.getSeq(i);


    		/*name*/
    		strokePaint.setTextSize(strokeTextSizeName);
    		strokePaint.setFakeBoldText(false);

			String name = elemNames.length < seq+1 ? "" : elemNames[seq];
			//canvas.drawText(name, mendel.el[i].left + 3, mendel.el[i].top + strokeTextSizeSymbol*2 + 2 * strokeTextSizeName, strokePaint);

    		/*Symbol*/
    		strokePaint.setFakeBoldText(true);
    		strokePaint.setTextSize(strokeTextSizeSymbol);
    		//int x = (mendel.el[i].left + 3);
    		//int y = (int) (mendel.el[i].top + strokeTextSizeSymbol + 2);

			int x = (mendel.el[i].left );
			int y = (int) (mendel.el[i].top + strokeTextSizeSymbol );


			int angle = is_rotated ? 90 : 0;
			canvas.rotate(-angle);
    		double theta = (angle*1.0) /180 * 3.1415;

			int xr = (int) ((x*Math.cos(theta) - y*Math.sin(theta)) + 8 );
			int yr = (int) ((x*Math.sin(theta) + y*Math.cos(theta))  + 4 );

			if(is_rotated){
				 xr = (int) ((x*Math.cos(theta) - y*Math.sin(theta))  - strokeTextSizeSymbol);
				 yr = (int) ((x*Math.sin(theta) + y*Math.cos(theta)) + strokeTextSizeSymbol + 4 );
			}


    		canvas.drawText( seq +"", xr,  yr, strokePaint);
			canvas.drawText(  elemSymbols[seq], xr + 10,  yr + strokeTextSizeSymbol + 2, strokePaint);
			canvas.rotate(angle);


    		
		}
		//ff =2;
    	 
    }


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float curX, curY;

		switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				mx0 = event.getX();
				my0 = event.getY();

				int seq = getHilightedElement(
						(int) (mx0 +getScrollX()),
						(int)( my0)
				);

				setElementSeqHighlighted(seq);
				invalidate();


				break;

			case MotionEvent.ACTION_UP:

				curX = event.getX();
				curY = event.getY();

                seq = getSelectedElement((int)(curX+getScrollX()),(int)(curY));
				setElementSeqHighlighted(-1);

				if(Math.abs(curX - mx0) < SHIFT_TOLERANCE && Math.abs(curY - my0) < SHIFT_TOLERANCE){

                    ((PeriodicTableActivity)myContext).showElementName(seq);
					((PeriodicTableActivity)myContext).elementChosen(seq);
				}

				break;
		}

		return super.onTouchEvent(event);
	}




}
