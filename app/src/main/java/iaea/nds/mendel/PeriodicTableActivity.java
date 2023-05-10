package iaea.nds.mendel;

import iaea.nds.nuclides.BaseActivity;
import iaea.nds.nuclides.Formatter;

import iaea.nds.nuclides.R;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class PeriodicTableActivity extends BaseActivity {
	
	public static String ELEMENT_CHOSEN;

    /**
     * Where the Table is plotted  see Mendel.xml
     */
    private PeriodicTablePlot periodicPlot;
    
    /**
     * List of elements  see Mendel.xml
     */
    private Spinner spinElements;
    
    private PeriodicTableActivity mThis;

    /* for use in the plotting*/
    public boolean is_rotated = false;

    
    /**
     * List of elements alphabetically ordered to populate spinElements
     */
    private String[] elementsSorted = new String[0];

	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	
		   super.onCreate(savedInstanceState);

		   if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
			   /* remenber this even when the instance is destroyed */
			   BaseActivity.mendel_forced_rotation = true;

		   }

		   this.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE  );

		   this.is_rotated = BaseActivity.mendel_forced_rotation;
		   init();

	   }
	   
	   /**
	    * Initialises the visual elements before every start
	    */
	   private void init() {

		   mThis = this;
		   setContentView(R.layout.mendel);
	   }

	   public int[] initialSize(){
		   Activity pp = this;
		   android.view.Display display = pp.getWindowManager().getDefaultDisplay();

		   int width = display.getWidth();
		   int height = display.getHeight();
		   return new int[]{width,height};
	   }


	   public void postinit(int width){

		   Button btn = findViewById(R.id.fakebtn);
		   btn.setWidth(width);
	        periodicPlot = findViewById(R.id.periodicTable);

	        periodicPlot.bringToFront();

	        periodicPlot.requestLayout();
	        	        
	        elementsSorted = Formatter.elementsSorted();
	        
	        spinElements = findViewById(R.id.spinElements);
	        fillSpin(R.string.elements_alpha_prompt, spinElements, elementsSorted, true, false);
	        spinElements.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					int i = spinElements.getSelectedItemPosition();
					if(i > 0){
						try {
							String elemName = elementsSorted[i-1];
							String zz =elemName.split("=")[1].trim();
							int zeta = Integer.parseInt(zz);
							mThis.sendElementToSearch(zeta);

						} catch (Exception e){
						}
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
									
				}
	        	
			});

	   }
	   public void showElementName(int seq ){
		   
		   String msg = "";
		   if(seq > -1){
			   msg = Formatter.getElemNames()[seq];
		   }
	        if(msg.length() > 0){
	        	final Toast toast = Toast.makeText(this, msg,Toast.LENGTH_SHORT);
	        	toast.show();
	        	Handler handler = new Handler();
	            handler.postDelayed(new Runnable() {
	                  @Override
	                  public void run() {
	                      toast.cancel(); 
	                  }
	           }, 500);
	        }

	   }

	    /**
	     * Check the validity of the z selected
	     * @param z
	     */
	    public void elementChosen(int z){
	    	if( z < 1) {
	    		return;
	    	}
	    	sendElementToSearch(z);
	    }
	    /**
	     * Returns the control to the calling activity and sets the choosen element
	     * @param z
	     */
	    private void sendElementToSearch(int z){
	    	if(getCallingActivity() != null) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra(ELEMENT_CHOSEN, Formatter.getElemSymbols()[z]);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			} else {
	    		resetQuery();
				setQueryPartsFromElement(Formatter.getElemSymbols()[z], z, getResources().getString(R.string.elements_prompt));
				Intent nucListIntent = getIntentForResultListActivity();

				if(nucListIntent != null) {
					startActivity(nucListIntent);
				}
				return;
			}
	    }

	    /**
	     * fills the spin the the list of elements
	     * @param promptId
	     * @param spin
	     * @param values
	     * @param allowNoChoice
	     * @param addCounter
	     */
	    private void fillSpin(int promptId, Spinner spin, String[] values, boolean allowNoChoice, boolean addCounter){
	    	   	
	    	List<String> list = new ArrayList<String>();
	    	
	    	if(allowNoChoice ){	
	    		list.add(getString(promptId)); 		
	    	}
	    	
	    	for (int i = 0; i < values.length; i++) {
				list.add( (addCounter ? i + " - " : " " ) + values[i] + "         ");
			}	    	

	    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
	   			  R.layout.custom_spinner_face_item, list);
	    	dataAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
	    	spin.setAdapter(dataAdapter);
	    	
	    }
	    /**
	     * Return z for a given element name
	     * @param name String the element name
	     * @return int the z
	     */
	    private int getElementZetaFromName(String name){
	    	name = name.split(" ")[0].trim();
	    	String[] names = Formatter.getElemNames();
	    	for (int i = 0; i < names.length; i++) {
	    		if(name.equals(names[i])){
	    			return i;
	    		}
			}
	    	
	    	return -1;
	    }

	    
	    @Override
	    public void onStop(){
	    	// user reported bug : null pointer. put checks
	    	super.onStop();
	    	if(periodicPlot == null) return;
	    	 periodicPlot.setBackgroundDrawable(null);
	    		    	
	    	ViewGroup vg = (ViewGroup)(periodicPlot.getParent());
	    	if(vg == null) return;
	    	vg.removeAllViews();
	    	

	    	if(vg == null) return;
	    	vg.removeAllViews();
	    	

	    	if(vg == null) return;
	    	vg.removeAllViews();

	    	
	    }
	    
		@Override
		protected void onStart() {
			
			super.onStart();
			if(periodicPlot != null){
				init();
			}
			
		}





	        

}
