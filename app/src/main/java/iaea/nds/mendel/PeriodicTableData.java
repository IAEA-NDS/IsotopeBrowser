package iaea.nds.mendel;

import java.util.HashMap;
import java.util.Vector;

/*
* rotations :
* (x,y) --> (y, H-x)
*
* */

import android.graphics.Rect;;

public class PeriodicTableData {
	int numCols = 18;
	int numRows = 11; // add 2 rows to make space for the list of elements names
	int[] alkalineMetal = new int[]{3,11,19,37,55,87};
	int[] alkalineEarthMetal = new int[]{4,12,20,38,56,88};
	int[] transitionMetal = new int[]{21,22,23,24,25,26,27,28,29,30, //ok
			 39,40,41,42,43,44,45,46,47,48,
			 72,73,74,75,76,77,78,79,80,
			 104,105,106,107,108,109,110,111,112};
	
	 int[] postTransitionMetal = new int[]{13,31,49,50,81,82,83,113,114,115,116}; //ok
	 int[] poorMetals = new int[]{5, 14, 32, 33,51,52,84}; //ok metalloids
	 int[] otherNonMetal = new int[]{1,6,7,8,15,16,34}; //ok
	 int[] halogens = new int[]{9,17, 35, 53, 85, 117}; //ok
	 int[] noble=  new int[]{2,10,18,36,54,86,118}; //ok
	 int[] lantha =  new int[]{57,58,59,60,61,62,63,64,65,66,67,68,69,70,71}; //ok
	 int[] actin=  new int[]{89,90,91,92,93,94,95,96,97,98,99,100,101,102,103};//ok

	Vector<int[]> elementTypes = new Vector<int[]>();

	
	public Rect[] el = new Rect[0];
	
	int numEl = 118;

	HashMap<Rect, int[]> colorForRect= new HashMap<Rect, int[]>();
	HashMap<int[], int[]> colorForElementType= new HashMap<int[], int[]>();
	
	 int[] a1 = new int[]{220,95,205};
	 int[] a2 = new int[]{220,109,191};
	 int[] a3 = new int[]{220,123,177};
	 int[] a4= new int[]{220,138,162};
	 int[] a5 = new int[]{220,152,148};
	 int[] a6 = new int[]{220,166,134};
	 int[] a7 = new int[]{220,180,120};
	 int[] a8 = new int[]{220,135,86};
	 int[] a9 = new int[]{220,149,72};

	 int[] a10 = new int[]{220,163,58};
	 int[] a11 = new int[]{220,178,43};
	 int[] a12 = new int[]{220,192,29};
	 int[] a13 = new int[]{220,206,15};
	 int[] a14 = new int[]{220,220,1};
	 int[] a15 = new int[]{118,200,72};
	 int[] a16 = new int[]{107,200,93};
	 int[] a17 = new int[]{95,200,115};
	 int[] a18 = new int[]{84,200,136};
	 int[] a19 = new int[]{72,200,158};
	 int[] a20 = new int[]{61,200,179};
	 int[] a21 = new int[]{50,200,200};
	 int[] a22 = new int[]{50,208,200};
	 int[] a23 = new int[]{50,187,200};
	 int[] a24 = new int[]{50,165,200};
	 int[] a25 = new int[]{50,144,200};
	 int[] a26 = new int[]{50,122,200};
	 int[] a27 = new int[]{50,101,200};
	 int[] a28 = new int[]{0,0,0};
	 int[] a29 = new int[]{255, 255, 255};
	 int[]a30 =  new int[]{180, 180, 180};
	 int[]a31 =  new int[]{255,120,120};
	 
	 int[][] colorsForElem = new int[][] {a1,a8,a13,a30,a27,a4,a21,a24,a19,a16};


	public PeriodicTableData(int width, int height) {
		fillElementType();
		fillColorForElementType();

		init(width/numCols, height/numRows);
	}
	

	
	public static int getSeq(int i){
		if(i<56){
			return i+1;
		}else if(i<73){
			return i+16;
		}else if(i<88){
			return i+31;
		}else if(i<103){
			return i-31;
		}else{
			return i-14;
		}
		
	}
	
	private void fillElementType(){
		
	
		elementTypes.add(alkalineMetal);
		elementTypes.add(alkalineEarthMetal);
		elementTypes.add(transitionMetal);
		elementTypes.add(postTransitionMetal);		
		elementTypes.add(poorMetals);
		elementTypes.add(otherNonMetal);
		elementTypes.add(halogens);
		elementTypes.add(noble);
		elementTypes.add(lantha);
		elementTypes.add(actin);

		
	}
	
	private void fillColorForRect(Rect rect, int z){
		colorForRect.put(rect, getColorForElement(z));
	}
	
	private int[] getColorForElement(int z){
		return colorForElementType.get(getElementType(z));
		
	}
	
	public int[] getColorForRect(Rect rect){
		return colorForRect.get(rect);
	}

	

	private void fillColorForElementType(){
		
		colorForElementType.put(alkalineMetal, colorsForElem[0]);
		colorForElementType.put(alkalineEarthMetal, colorsForElem[1]);
		colorForElementType.put(transitionMetal, colorsForElem[2]);
		colorForElementType.put(postTransitionMetal, colorsForElem[3]);
		colorForElementType.put(poorMetals, colorsForElem[4]);
		colorForElementType.put(otherNonMetal, colorsForElem[5]);
		colorForElementType.put(halogens, colorsForElem[6]);
		colorForElementType.put(noble, colorsForElem[7]);
		colorForElementType.put(lantha, colorsForElem[8]);
		colorForElementType.put(actin, colorsForElem[9]);
		
	}

	
	private int[] getElementType(int z){
		for (int i = 0; i < elementTypes.size(); i++) {
			if (isInArray(elementTypes.elementAt(i), z)){
				return elementTypes.elementAt(i);
			}
		}
		return null;
	}
	
	private boolean isInArray(int[] array , int z){
		for (int i = 0; i < array.length; i++) {
			if(z == array[i]){
				return true;
			}
		}
		return false;
		
	}
	
	public void init(int width, int height){
		
		int tot = numEl;
		int space = 4;
		int xOffset = 0;
		int yOffset = 0;

		int w = width - 2;
		int h = height;


		el=new Rect[tot];
		for (int i = 0; i < el.length; i++) {
			el[i]=new Rect();
		}

		int z=0;
		int k=0;

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				if(!( k>117 || (z>0 && z<17 )|| (z>19 && z<30) ||(z>37 &&z<48) ||
					  z==92 || z==110 ||    z==126 || z==142 || z==143 ||  z==144 ) 
					  ){
				     
					int left =xOffset+j*(w+space); //x1
					int top = yOffset+i*(h+space); //y1
					int right = left + w;  //x2
					int bottom = top + h; // y2

					// left top right bottom
					el[k].set(left,top,right,bottom);
					k++;
				}				
				z++;			
			}
		}

		for (int i = 0; i < el.length; i++) {
			if(i==(el.length-1)){
				i = i*1;
			}
			fillColorForRect(el[i], getSeq(i));
		}
	}


}
