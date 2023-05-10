package iaea.nds.nuclides;

//http://stackoverflow.com/questions/10013906/android-zoom-in-out-relativelayout-with-spread-pinch
//http://android-developers.blogspot.co.at/2010/06/making-sense-of-multitouch.html

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

import iaea.nds.nuclides.db.entities.L_decays;
import iaea.nds.nuclides.db.DecayForDetails;
import iaea.nds.nuclides.db.NuclideForChart;
import iaea.nds.nuclides.mvvm.LivechartViewModel;

import static iaea.nds.nuclides.Formatter.COLOR_DECAY_TYPE;
import static iaea.nds.nuclides.Formatter.COLOR_GRAY;
import static iaea.nds.nuclides.Formatter.colors;
import static iaea.nds.nuclides.Formatter.getColorRGBFromDecayCode;

public class LivechartPlot extends LinearLayout  {

    LivechartViewModel livechartVM;
    LivechartActivity myContext;

    NuclideForChart[] NUCS_GRAY = new NuclideForChart[0];
    NuclideForChart[] NUCS_COLORED = new NuclideForChart[0];
    NuclideForChart mNucSelected;

    Decay[] offsprings = new Decay[0];
    Decay[] ancestors = new Decay[0];
    private Decay[] decayChain = new Decay[0];

    boolean bNucsGrayLoaded = false;
    boolean bNucsColorLoaded = false;

    boolean canvas_initialised = false;
    boolean progress_dismiss = false;

    /**/

    private float mx0, my0;
    private float mx, my;
    private float mDeltaX, mDeltaY;
    private float mLastValidDeltaX, mLastValidDeltaY;
    private float mTotDeltaX, mTotDeltaY;

    private double X_SHIFT_TOLERANCE_mm ;
    private double Y_SHIFT_TOLERANCE_mm ;

    boolean fingerdown = false;
    private float fingerdowncircleradius = (float)(8. * getResources().getDisplayMetrics().xdpi / 25.4);
    long timefingerdown = -1;
    long LONG_TAP_THRESH = 400;
    float curX, curY;

    private float mTextScaleFactor = 1;
    private float mTitleTextScaleFactor = 1;

    private static final int INVALID_POINTER_ID = -1;
    private static float SIZE_2 = 200;
    private static float SIZE_1 = 40;

    private int mTextSizeStart = 1;
    private float mRadius = 0;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private float mPrevScaleFactor = 1.f;

    int nucvisible = 0;
    boolean initAfterZoom = false;

    boolean isFilter = false;
    boolean isFirtCallToNucsInit = true;

    boolean plotDecay = false;
    boolean plotAncestors = true;

    float LEFT_OFF = 10;
    float TOP_OFF = 10;
    float NUC_BORDER = 0;

    String mySelectedNucid;
    int mNselected;
    int mZselected;
    float MAIN_WIDTH;
    float MAIN_HEIGHT;
    float mNucWidth;
    float mNucHeight;
    float mSizeforscale;
    float NUC_HEIGHT_START;
    float NUC_WIDTH_START;
    float NUC_WIDTH_DELTA = 1;
    float NUC_HEIGHT_DELTA = 1;

    int PORTRAIT = 1;
    int LANDSCAPE = 0;

    int SIZE_TRESHOLD = 400;
    int TEXT_SIZE = 30;
    int TEXT_SIZE_LEGEND = 30; // set
    int TEXT_SIZE_SMALL = 15;
    int LABEL_GAP = 24;

    int MAX_N = 177;
    int MAX_Z = 118;

    int DECTYPE_STABLE = -1;

    static String[] decay_legend =  new String[0];
    Paint strokePaint = new Paint();
    Paint textPaintLight = new Paint();
    Paint textPaintTitleLight = new Paint();
    Paint textPaintDark = new Paint();
    Paint textPaintTitleDark = new Paint();

    /* assigned when painting on the basis of the nuclide's background*/
    Paint _textPaint = new Paint();
    Paint _textPaintTitle = new Paint();

    Paint decayPaint = new Paint();
    Paint bkgPaint = new Paint();
    Paint bkgPaintStart = new Paint();
    DashPathEffect dashPath = new DashPathEffect(new float[]{ 20, 20, }, 0);

    Rect clipBounds = new Rect();


    public class Decay {
        NuclideForChart parent = null;
        NuclideForChart daughter = null;
        int decType = -1;
        boolean ismain = true;
    }


        public LivechartPlot(Context context) {
        super(context);

    }
    public LivechartPlot(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false) ;
        myContext = (LivechartActivity) context;

        decay_legend = new String[]{myContext.getResources().getString(R.string.chart_decay_lbl_alpha)
                ,myContext.getResources().getString(R.string.chart_decay_lbl_ecbp)
                ,myContext.getResources().getString(R.string.chart_decay_lbl_beta)
                ,myContext.getResources().getString(R.string.chart_decay_lbl_p)
                ,myContext.getResources().getString(R.string.chart_decay_lbl_n)
                ,myContext.getResources().getString(R.string.chart_decay_lbl_ec)
                ,myContext.getResources().getString(R.string.chart_decay_lbl_sf)
                ,myContext.getResources().getString(R.string.stable_prompt)
                ,myContext.getResources().getString(R.string.unknown)};


        if( ( myContext).getIntent().getExtras() != null){
            isFilter = (myContext).getIntent().getExtras().getBoolean("FILTER", false);
        }

        /* see initNuclides() for extracting a nucid from the intent*/

        if( myContext.getShowAncestors().equals( Config.PREFS_VALUE_NO)){
            plotAncestors = false;
        }

        MAIN_WIDTH = 0;
        MAIN_HEIGHT = 0;

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        X_SHIFT_TOLERANCE_mm = 2 * getResources().getDisplayMetrics().xdpi / 25.4;
        Y_SHIFT_TOLERANCE_mm = 2 * getResources().getDisplayMetrics().xdpi / 25.4;

        /* from NuclidesActivity -> decay chain*/
        mySelectedNucid = myContext.getNucidForDecayChainInChart();
    }

    public float[] getQttToSave(){

        if(!canvas_initialised){ return new float[0];}

        return new float[]{mScaleFactor, mDeltaX, mDeltaY, mTotDeltaX, mTotDeltaY,(float)mNselected, (float)mZselected, mTextScaleFactor
                , mRadius,mNucWidth, mNucHeight,mSizeforscale,(MAIN_HEIGHT>MAIN_WIDTH ? PORTRAIT : LANDSCAPE)};
    }

    private void centerNuclide(float n , float z){
        mDeltaX = LEFT_OFF + n * (mNucWidth  + NUC_BORDER) - MAIN_WIDTH/2;
        mTotDeltaX = mDeltaX;
        mDeltaY = MAIN_HEIGHT - TOP_OFF - MAIN_HEIGHT/2 - z * (mNucHeight + NUC_BORDER) ;
        mTotDeltaY =  mDeltaY ;
    }

    private NuclideForChart getNucSelectedFromXandY(float x, float y){

        for (int i = 0; i < NUCS_COLORED.length; i++) {
            if(NUCS_COLORED[i].getRect() != null && NUCS_COLORED[i].getRect().contains(x, y)){
                return NUCS_COLORED[i];
            }
        }
        for (int i = 0; i < NUCS_GRAY.length; i++) {
            if(NUCS_GRAY[i].getRect() != null && NUCS_GRAY[i].getRect().contains(x, y)){
                return NUCS_GRAY[i];
            }
        }

        return null;
    }
    public Decay[] getOffsprings(){
        return offsprings;
    }


    public void setNUCS_GRAY(NuclideForChart[] NUCS_GRAY) {
        this.NUCS_GRAY = NUCS_GRAY;
        bNucsGrayLoaded = true;
        initNuclides();

        // REMOVE
       /* for(int i = 0; i < NUCS_GRAY.length; i++){
          //  System.out.println(NUCS_GRAY[i].getNucid());
            if(NUCS_GRAY[i].getNucid().equals("248CM"))
                fetchAncestors(NUCS_GRAY[i]);
        }
*/
        invalidate();
    }

    public void setNUCS_COLORED(NuclideForChart[] NUCS_COLORED) {
        this.NUCS_COLORED = NUCS_COLORED;
        bNucsColorLoaded = true;
        initNuclides();

       /*for (int i =0; i<NUCS_COLORED.length;i++){
            System.out.println(NUCS_COLORED[i].getNucid());
           //if(NUCS_COLORED[i].getNucid().equals("248CM"))
             fetchOffsprings(NUCS_COLORED[i]);
        }*/

        invalidate();
    }

    private  List<DecayForDetails> nuc_decays(NuclideForChart nuc){
        return livechartVM.nuclideDecays(nuc.getRowid());
    }
    //
    private void initDecayLabels(NuclideForChart nuc){
        List<DecayForDetails> decays = nuc_decays(nuc);

        L_decays dec ;

        if (decays != null){
            String[][] nucDec = new String[decays.size()][2];
            for(int i = 0; i < nucDec.length; i++) {
                dec = decays.get(i);
                nucDec[i][0] = String.valueOf(dec.getDec_type());
                nucDec[i][1] = Formatter.getDecayLabel(dec.getDec_type()+"",dec.getPerc(), dec.getPerc_oper(), dec.getUnc());
            }
            nuc.setDecaysLabel(nucDec);
        }

    }

    private String[][] getNuclideDecaysLabel(NuclideForChart nuc){
        if(nuc.getDecaysLabel() == null){
            initDecayLabels(nuc);
        }
        return nuc.getDecaysLabel();
    }
    private void clearDecay(Decay[] decs){

        for ( int i = 0 ; i < decs.length ; i++){
            if(decs[i].daughter == null || decs[i].parent == null){
                continue;
            }
            decs[i].parent.isindecaychain = false;

            decs[i].daughter.isindecaychain = false;
        }
        decs = new Decay[0];

    }

    private void clearDecayChain(){
        clearDecay(offsprings);
        clearDecay(ancestors);
        clearDecay(decayChain);
        offsprings = new Decay[0];
        ancestors = new Decay[0];
        decayChain = new Decay[0];
        plotDecay = false;
        if(mNucSelected != null) {
            myContext.setNucidForDecayChainInChart(mNucSelected.getNucid());
        }

    }

    private void addDau(Vector<Decay> v, Decay[] dec){

        if(v.size() == 0){
            for (int i = 0; i < dec.length ; i++){
                v.add(dec[i]);
            }
            return;
        }

        for (int i = 0; i < dec.length ; i++){
            int size = v.size();
            boolean bins = true;
            for(int j = 0 ; j < size ; j++){
                Decay dm = (Decay) v.elementAt(j);
                if( dm.parent.getNucid().equals(dec[i].parent.getNucid()) && dm.daughter.getNucid().equals(dec[i].daughter.getNucid())  ){
                    bins = false;
                }
            }
            if(bins){
                v.add(dec[i]);
            }
        }
    }

    private void addAnc(Vector<Decay> v, Decay[] dec){

        if(v.size() == 0){
            for (int i = 0; i < dec.length ; i++){
                v.add(dec[i]);
            }
            return;
        }

        for (int i = 0; i < dec.length ; i++){

            int size = v.size();
            boolean bins = true;
            for(int j = 0 ; j < size ; j++){
                Decay dm = (Decay) v.elementAt(j);
                if( dm.parent == null ||
                        (dm.daughter.getNucid().equals(dec[i].daughter.getNucid()) && dm.parent.getNucid().equals(dec[i].parent.getNucid()))
                ){
                    bins = false;
                }
            }
            if(bins){
                v.add(dec[i]);
            }

        }

    }
    private void fetchOffspringsRecursive(Decay dc, Vector<Decay> v){

        /* check if the nuclide is already there*/

        for(int j = 0 ; j < v.size() ; j++){
            Decay dm = v.elementAt(j);
            if(dc == null || dm == null || dc.daughter == null || dm.parent == null){
                continue;
            }
            if( dc.daughter.getNucid().equals(dm.parent.getNucid())){
                return;
            }
        }
        Decay[] daug = getDecaysToDaughters(dc.daughter);
        addDau(v,daug);
        for (int i = 0; i < daug.length ; i++){
            fetchOffspringsRecursive(daug[i], v);
        }
    }


    private void fetchOffsprings(NuclideForChart nuc){
        clearDecay(offsprings);

        if(nuc == null){
            return;
        }
        Vector<Decay> v = new Vector<Decay>();

        Decay[] dc = getDecaysToDaughters(nuc);//getDaug(getNucFromNucid("238U"));

        if(dc.length == 0) {
            Toast.makeText(myContext, myContext.getResources().getString(R.string.msg_nobranching), Toast.LENGTH_SHORT).show();
            return;
        }

        nuc.isindecaychain = true;
        plotDecay = true;
        addDau(v,dc);
        for (int i = 0; i < dc.length; i++){
            fetchOffspringsRecursive(dc[i],v);
        }
        for (int i = 0; i < v.size(); i++){
            Decay d = (Decay) v.elementAt(i);
        }

        offsprings = new Decay[v.size()];
        v.copyInto(offsprings);
    }

    private NuclideForChart getNucFromNucid(String nucid){
        for (int i = 0; i < NUCS_COLORED.length; i++) {
            if(NUCS_COLORED[i].getNucid().toUpperCase().equals(nucid.toUpperCase())){
                return NUCS_COLORED[i];
            }
        }
        for (int i = 0; i < NUCS_GRAY.length; i++) {
            if(NUCS_GRAY[i].getNucid().toUpperCase().equals(nucid.toUpperCase())){
                return NUCS_GRAY[i];
            }
        }
        return null;
    }

    private Decay[] getDecaysFromParents(NuclideForChart nuc){


        Decay[] anc = new Decay[0];
        if(nuc == null){
            return anc;
        }

        List<DecayForDetails> decays =  livechartVM.decaysFromParents(nuc.getNucid());
        if(decays == null) return anc;

        anc = new Decay[decays.size()];

        for (int i = 0 ; i < decays.size() ; i++){
                anc[i] = new Decay();
                anc[i].daughter = nuc;

                anc[i].decType =  decays.get(i).getDec_type();

                if(i > 0){
                    anc[i].ismain = false;
                }
                anc[i].parent = getNucFromNucid(decays.get(i).getNucid());
                anc[i].daughter.isindecaychain = true;
                if(anc[i].parent != null) {
                    anc[i].parent.isindecaychain = true;
                }

            }

        return anc;

    }

    private Decay[] getDecaysToDaughters(NuclideForChart nuc){

        Decay[] dau = new Decay[0];
        if(nuc == null) return dau;
        List<DecayForDetails> decays =  livechartVM.decaysToDaughter(nuc.getNucid());
        if(decays == null) return dau;

        dau = new Decay[decays.size()];

            for (int i = 0 ; i < decays.size() ; i++){
                dau[i] = new Decay();
                dau[i].parent = nuc;

                dau[i].decType =  decays.get(i).getDec_type();

                if(i > 0){
                    dau[i].ismain = false;
                }
                dau[i].daughter = getNucFromNucid(decays.get(i).getDaughter_nucid());
                if(dau[i].daughter != null)
                    dau[i].daughter.isindecaychain = true;

            }

        return dau;

    }

    private void fetchAncestorsRecursive(Decay dc, Vector<Decay> v){

        /* check if the nuclide is alreay there*/
        for(int j = 0 ; j < v.size() ; j++){
            Decay dm = v.elementAt(j);

            if(dc == null || dm == null || dc.parent == null || dm.daughter == null
                    || dc.parent.getNucid() == null || dm.daughter.getNucid() == null){
                continue;
            }

            if( dc.parent.getNucid().equals(dm.daughter.getNucid())){
                return;
            }
        }


        Decay[] anc = getDecaysFromParents(dc.parent);//getDecaysToDaughters(dc.daughter);

        addAnc(v,anc);//addDau(v,dau);
        for (int i = 0; i < anc.length ; i++){
            fetchAncestorsRecursive(anc[i], v);;//fetchOffspringsRecursive(daug[i], v);
        }

    }

    private void fetchAncestors(NuclideForChart nuc){
        clearDecay(ancestors);

        if(nuc == null){
            return;
        }
        Vector<Decay> v = new Vector<Decay>();

        Decay[] dc = getDecaysFromParents(nuc);

        if(dc.length == 0) {
            Toast.makeText(myContext, myContext.getResources().getString(R.string.msg_nobranching), Toast.LENGTH_SHORT).show();
            return;
        }

        nuc.isindecaychain = true;
        plotDecay = true;
        addAnc(v,dc);
        for (int i = 0; i < dc.length; i++){
            fetchAncestorsRecursive(dc[i],v);
        }

        for (int i = 0; i < v.size(); i++){
            Decay d = (Decay) v.elementAt(i);

        }

        ancestors = new Decay[v.size()];
        v.copyInto(ancestors);

    }


    private void fetchDecayChain(NuclideForChart mNucSelected){

        clearDecayChain();
        myContext.setNucidForDecayChainInChart(mNucSelected.getNucid());
        fetchOffsprings(mNucSelected);
        if(plotAncestors)
            fetchAncestors(mNucSelected);


        Vector<Decay> v = new Vector<Decay>();
        for (int i = 0; i < ancestors.length ;i++){
            v.add(ancestors[i]);
        }
        for (int i = 0; i < offsprings.length;i++){
            v.add(offsprings[i]);
        }

        decayChain = new Decay[v.size()];
        v.copyInto(decayChain);

    }
    private void rescale() {
        float nucWold = mNucWidth;
        float nucHold = mNucHeight;
        float delXold = mDeltaX;
        float delYold = mDeltaY;

        mNucWidth = (mScaleFactor * NUC_WIDTH_START);
        mNucHeight =  (mScaleFactor * NUC_HEIGHT_START);
        mNucWidth = (mNucWidth < NUC_WIDTH_START ? NUC_WIDTH_START : mNucWidth);
        mNucHeight = (mNucHeight < NUC_HEIGHT_START ? NUC_HEIGHT_START: mNucHeight);

        mDeltaX = mNselected * (mNucWidth - nucWold) + delXold;
        mDeltaY = mZselected * (nucHold - mNucHeight) + delYold;

        mRadius = (mNucWidth > 10 ? Math.min(mNucWidth / 8, 5 * mScaleFactor) : 0);

        mSizeforscale =  mNucWidth > mNucHeight ? mNucHeight : mNucWidth ;
        if(mSizeforscale > SIZE_1 && mSizeforscale < SIZE_2){
            mTextScaleFactor = (int)(mSizeforscale / 2.2); //3.5
            mTitleTextScaleFactor = mTextScaleFactor;

        } else {
            mTextScaleFactor = (int)(mSizeforscale / 7.5); //7.5
            mTitleTextScaleFactor = (int)(mSizeforscale / 5);

        }

        textPaintLight.setTextSize(mTextSizeStart * mTextScaleFactor );
        textPaintTitleLight.setTextSize(mTextSizeStart * mTitleTextScaleFactor );

        textPaintDark.setTextSize(mTextSizeStart * mTextScaleFactor );
        textPaintTitleDark.setTextSize(mTextSizeStart * mTitleTextScaleFactor );

    }

    public void setSavedIntialQuantities(float[] savedQuantities){

        if(savedQuantities != null && savedQuantities.length == 13){

            mScaleFactor = savedQuantities[0];
           // mTotDeltaX = (int) savedQuantities[3];
           // mTotDeltaY = (int) savedQuantities[4];
            mNselected = (int) savedQuantities[5];
            mZselected = (int) savedQuantities[6];
            // mTextScaleFactor = savedQuantities[7];
            mRadius = savedQuantities[8];
           // mNucWidth = (int) savedQuantities[9];
          //  mNucHeight = (int) savedQuantities[10];
            rescale();

            _textPaint.setTextSize(mTextSizeStart * mTextScaleFactor );
            _textPaintTitle.setTextSize(mTextSizeStart * mTextScaleFactor );

        }
    }

    private void prepareScale(float scale){

        mScaleFactor *= (scale > 1 ? scale * 1.02 :
                (scale == 1 ? scale : scale * 0.98));

        // Don't let the object get too small or too large.
        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 500.0f));

        rescale();
        if(mNucWidth >= MAIN_WIDTH || mNucHeight >= MAIN_HEIGHT){
            mScaleFactor = mPrevScaleFactor;
            rescale();
        }
        mPrevScaleFactor = mScaleFactor;

    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float scale = detector.getScaleFactor();
            prepareScale(scale);
            invalidate();
            return true;
        }
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            setNandZselected(detector.getFocusX(), detector.getFocusY());

            return true;
        }
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mTotDeltaX = mDeltaX;
            mTotDeltaY = mDeltaY;

        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(event);
        float dx, dy;
        final int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_DOWN:{
                timefingerdown = System.currentTimeMillis();
                mx = event.getX();
                my = event.getY();
                mx0 = mx;
                my0 = my;
                mActivePointerId = event.getPointerId(0);
                fingerdown = true;

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position

                int pointerIndex = event.findPointerIndex(mActivePointerId);
                curX = event.getX(pointerIndex);
                curY = event.getY(pointerIndex);

                if(Math.abs(curX - mx0) > X_SHIFT_TOLERANCE_mm || Math.abs(curY - my0) > Y_SHIFT_TOLERANCE_mm ){
                    timefingerdown = -1;
                }

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    dx = mx - curX;
                    dy = my - curY;
                    mDeltaX = dx + mTotDeltaX;
                    mDeltaY = dy + mTotDeltaY;
                    if(nucvisible > 0){
                        mLastValidDeltaX = mDeltaX;
                        mLastValidDeltaY = mDeltaY;
                    } else {
                        mx = mx - mLastValidDeltaX;
                        my = my - mLastValidDeltaY;
                        dx = mx - curX;
                        dy = my - curY;
                        mDeltaX = dx + mTotDeltaX;
                        mDeltaY = dy + mTotDeltaY;
                    }
                    invalidate();
                }

                break;
            }
            case MotionEvent.ACTION_UP:{
                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                curX = event.getX(pointerIndex);
                curY = event.getY(pointerIndex);
                dx = mx - curX;
                dy = my - curY;
                mTotDeltaX += dx;
                mTotDeltaY += dy;
                mActivePointerId = INVALID_POINTER_ID;

                if(Math.abs(curX - mx0) < X_SHIFT_TOLERANCE_mm && Math.abs(curY - my0) < Y_SHIFT_TOLERANCE_mm ){

                    NuclideForChart selOld = mNucSelected;
                    setNandZselected((int)curX, (int)curY );
                    if(event.getEventTime() - event.getDownTime() > LONG_TAP_THRESH && mNucSelected != null) {
                        if(selOld != mNucSelected || (selOld == mNucSelected && !plotDecay)){
                            fetchDecayChain(mNucSelected);

                        } else {
                            clearDecayChain();
                            myContext.setNucidForDecayChainInChart("");
                        }
                        invalidate();
                    }
                    else {
                        showNuclideDetail();
                        //clearDecayChain();
                        plotDecay = false;
                    }
                    invalidate();

                } else {
                    invalidate();

                }
                /* this is to emulate to zoom*/
                //prepareScale(3);
               // invalidate();

                fingerdown = false;
                timefingerdown = -1;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mx = event.getX(newPointerIndex);
                    my = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    private void showNuclideDetail(){

        if(mNucSelected == null){
            return;
        }
        myContext.showNuclideDetail( String.valueOf(mNucSelected.getRowid()));

    }

    public  NuclideForChart setNandZselected(float x, float y){

        NuclideForChart nuc = getNucSelectedFromXandY(x, y);
        if(nuc != null){
            mNselected = nuc.getN();
            mZselected = nuc.getZ();
            mNucSelected = nuc;

        } else {
            mNselected = (int)Math.ceil((x - LEFT_OFF + mDeltaX )/(mNucWidth + NUC_BORDER));
            mZselected =  - (int)Math.ceil((y - MAIN_HEIGHT + TOP_OFF + mDeltaY)/(mNucHeight + NUC_BORDER));
            mNucSelected = null;

        }
        return mNucSelected;

    }

    private void initNuclides(){

        if(isFirtCallToNucsInit) {
            isFirtCallToNucsInit = false;
            return;
        }

        if(mySelectedNucid.length() > 0){

            mNucSelected = getNucFromNucid(mySelectedNucid);
            if(mNucSelected == null){
                return;
            }
            mNselected = mNucSelected.getN();
            mZselected = mNucSelected.getZ();

            if(myContext.getChartSavedState().length == 0) {
                mScaleFactor = 1;
                float scale = SIZE_1 / NUC_WIDTH_START + 3;
                prepareScale(scale);
            }
            fetchDecayChain(mNucSelected);
            //   main -> selection -> list -> nuclide -> decay chain . The other way is in initCanvas
            if(myContext.getChartNZatCentre().length == 0) {
                centerNuclide((float) mNucSelected.getN(), (float) mNucSelected.getZ());
            }

        } else {
            decayChain = myContext.getChartSavedDecays();
            if (decayChain.length > 0) {
                plotDecay = true;
                for (int i = 0; i < decayChain.length; i++) {
                    decayChain[i].parent = getNucFromNucid(decayChain[i].parent.getNucid());
                    decayChain[i].parent.isindecaychain = true;
                    decayChain[i].daughter = getNucFromNucid(decayChain[i].daughter.getNucid());
                    decayChain[i].daughter.isindecaychain = true;

                }
            }
        }

        // MV TEST


    }


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        if(isFirtCallToNucsInit){
            return;
        }
        if(!canvas_initialised){
            initCanvas(canvas);
        }
        canvas.drawRect(0, 0, MAIN_WIDTH, MAIN_HEIGHT, bkgPaintStart);

        if(!bNucsColorLoaded && !bNucsGrayLoaded){
            return;
        }

        if(mNucWidth > SIZE_2 && !initAfterZoom){
          BaseActivity.progressStart(myContext);
          initAfterZoom = true;
          progress_dismiss = true;

        }
        plotLegend(canvas, strokePaint);

        if(offsprings.length > 0){
            plotDecay = true;
        }
        nucvisible = plotNuclides(canvas, strokePaint);

        if(fingerdown){
            long now = System.currentTimeMillis();
            if( timefingerdown > 0 && now - timefingerdown > LONG_TAP_THRESH) {
                strokePaint.setARGB(100, 255, 180, 180);
            } else {
                strokePaint.setARGB(150, 100, 100, 250);
            }
            canvas.drawCircle(curX, curY, fingerdowncircleradius, strokePaint);
        }

        plotDecays(canvas,strokePaint);

        if(progress_dismiss) {
            myContext.progressDismiss();
            progress_dismiss = false;

        }
    }

    float __xP;
    float __yP;
    float __xD;
    float __yD;
    Decay __dm;
    int __signX ; //__xP < __xD && __yP < __yD
    int __signY ;

    private void plotDecays(Canvas canvas, Paint strokePaint){
        __dm = null;

        for(int i = 0; i < decayChain.length; i++){
            __dm = decayChain[i];
            if(__dm == null || __dm.parent == null || __dm.daughter == null){
                continue;
            }

            __xP = (LEFT_OFF + __dm.parent.getN().intValue() * (mNucWidth  + NUC_BORDER) - mDeltaX) + mNucWidth/2;
            __yP = (MAIN_HEIGHT - TOP_OFF - mDeltaY - __dm.parent.getZ().intValue() * (mNucHeight + NUC_BORDER)) + mNucHeight/2 ;
            __xD = (LEFT_OFF + __dm.daughter.getN().intValue() * (mNucWidth  + NUC_BORDER) - mDeltaX)+ mNucWidth/2;
            __yD = (MAIN_HEIGHT - TOP_OFF - mDeltaY - __dm.daughter.getZ().intValue() * (mNucHeight + NUC_BORDER))+ mNucHeight/2;


            int dlt = (int) (mNucWidth/5);
            if(__xP < __xD){
                dlt = -dlt;
            }

            __xD = __xD +dlt;

            dlt = (int) (mNucHeight/5);;
            if(__yP < __yD){
                dlt = -dlt;
            }

            __yD = __yD +dlt;

            int[] color =  getColorRGBFromDecayCode(__dm.decType);
            strokePaint.setARGB(255, color[0], color[1], color[2]);

            int linewidth = (int) (10 > mNucWidth/8 ? mNucWidth/8 : 10);


            if(__dm.ismain){
                strokePaint.setStrokeWidth(linewidth);
                strokePaint.setPathEffect(null);
            }    else {
                strokePaint.setStrokeWidth( ( linewidth > 2 ? linewidth -2 : linewidth));
                strokePaint.setPathEffect(dashPath);
            }

            canvas.drawLine(__xP, __yP, __xD, __yD,strokePaint);

            __signX = 1; //__xP < __xD && __yP < __yD
            __signY = 1;
            if(__xP > __xD && __yP > __yD){
                __signX = -1;
                __signY = -1;
            }

            if(__xP > __xD && __yP < __yD){
                __signX = -1;
                __signY = +1;
            }

            if(__xP < __xD && __yP > __yD){
                __signX = +1;
                __signY = -1;
            }

            fillArrow(strokePaint,canvas, __xD, __yD, __xD + __signX*5, __yD + __signY*5, (int)(50 > mNucWidth/2 ? mNucWidth/2 : 50));

            if(__dm.parent != null){
                plotNuclideText(__dm.parent,canvas
                        , false);
            }
            if(__dm.daughter != null){
                plotNuclideText(__dm.daughter,canvas, false);
            }

        }
    }

    int arrowHeadAngle = 45;
    float[] linePts;
    float[] linePts2;
    Matrix rotateMat;

    private void fillArrow(Paint paint, Canvas canvas, float x0, float y0, float x1, float y1, int arrowHeadLenght ) {
        //paint.setStyle(Paint.Style.STROKE);
        strokePaint.setPathEffect(null);
        //int arrowHeadLenght = 50;

        linePts = new float[] {x1 - arrowHeadLenght, y1, x1, y1};
        linePts2 = new float[] {x1, y1, x1, y1 + arrowHeadLenght};
        rotateMat = new Matrix();

        //rotate the matrix around the center
        rotateMat.setRotate((float) (Math.atan2(y1 - y0, x1 - x0) * 180 / Math.PI + arrowHeadAngle), x1, y1);
        rotateMat.mapPoints(linePts);
        rotateMat.mapPoints(linePts2);

        canvas.drawLine(linePts [0], linePts [1], linePts [2], linePts [3], paint);
        canvas.drawLine(linePts2 [0], linePts2 [1], linePts2 [2], linePts2 [3], paint);
    }

    private int plotNuclides(Canvas canvas, Paint strokePaint){
        //long start= System.currentTimeMillis();


        int totVis = 0;
        for (int i = 0; i < NUCS_GRAY.length; i++) {
            if(NUCS_GRAY[i].isindecaychain){
            }
            totVis +=  (plotNuclide(NUCS_GRAY[i] , canvas,  strokePaint, false) ? 1 : 0);
        }
        for (int i = 0; i < NUCS_COLORED.length; i++) {
            if(NUCS_COLORED[i].isindecaychain){
            }
            totVis +=  (plotNuclide(NUCS_COLORED[i] , canvas,  strokePaint, true) ? 1 : 0);

        }
        //long end= System.currentTimeMillis();
        //android.util.Log.i("Time Class ", " Time value in millisecinds "+ (end-start));
        plotmagic(canvas,strokePaint);
        return totVis;
    }

    public float[] getCenterNZ(){
        float[] ret = new float[]{( MAIN_WIDTH / 2 + mDeltaX - LEFT_OFF ) / (mNucWidth  + NUC_BORDER),
         -(MAIN_HEIGHT /2 - MAIN_HEIGHT + TOP_OFF + mDeltaY) / (mNucHeight + NUC_BORDER)};
        return ret;
    }

    float __x;
    float __y;

    private boolean plotNuclide(NuclideForChart nuc ,Canvas canvas, Paint strokePaint, boolean isColor){

        __x = LEFT_OFF + nuc.getN() * (mNucWidth  + NUC_BORDER) - mDeltaX;
        __y = MAIN_HEIGHT - TOP_OFF - mDeltaY - nuc.getZ() * (mNucHeight + NUC_BORDER);

        nuc.setRect(new RectF(__x, __y, __x + mNucWidth - NUC_WIDTH_DELTA, __y + mNucHeight - NUC_HEIGHT_DELTA));


        if( __x < -mNucWidth || __x > MAIN_WIDTH + mNucWidth || __y < -mNucHeight || __y > MAIN_HEIGHT + mNucHeight){
            return false;
        }

        if(plotDecay) {
            if (!nuc.isindecaychain) {
                strokePaint.setARGB(255, COLOR_GRAY[0], COLOR_GRAY[1],COLOR_GRAY[2]);
            } else if (nuc.getDec_type() == DECTYPE_STABLE) {
                strokePaint.setARGB(255, 255, 255, 255);
            } else if ( (offsprings.length > 0 && nuc.getNucid().equals(offsprings[0].parent.getNucid()))
                    || (ancestors.length > 0 && nuc.getNucid().equals(ancestors[0].daughter.getNucid()))) {
                strokePaint.setARGB(255, 255, 180, 180);
            } else {
                strokePaint.setARGB(255, 150, 150, 150);
            }
        }
        else if(isColor){
            strokePaint.setARGB(255, colors[nuc.getDecColor()][0], colors[nuc.getDecColor()][1],colors[nuc.getDecColor()][2]);
        }
        else{
            strokePaint.setARGB(255, COLOR_GRAY[0], COLOR_GRAY[1],COLOR_GRAY[2]);
        }

        //nuc.setRect(new RectF(__x, __y, __x + mNucWidth - NUC_WIDTH_DELTA, __y + mNucHeight - NUC_HEIGHT_DELTA));
        canvas.drawRoundRect(nuc.getRect(), mRadius, mRadius, strokePaint);

        if(!nuc.isindecaychain) {
            plotNuclideText(nuc, canvas, plotDecay);


        }
        return true;
    }

    int[] __color ;
    String[] pnt_dm ;
    private Rect __r = new Rect();
    private Rect __r2 = new Rect();
    float _txy ;
    float _xtn;
    float _ytn ;


    private void plotNuclideText(NuclideForChart nuc ,Canvas canvas,  boolean _plot_decay) {

        _xtn = (LEFT_OFF +  nuc.getN().intValue() * (mNucWidth  + NUC_BORDER) - mDeltaX);
        _ytn = (MAIN_HEIGHT - TOP_OFF - mDeltaY - nuc.getZ().intValue() * (mNucHeight + NUC_BORDER));




        if(_plot_decay || (nuc.getDecColor() == COLOR_DECAY_TYPE[0] || nuc.getDecColor() == COLOR_DECAY_TYPE[2])){
            _textPaintTitle = textPaintTitleLight;
            _textPaint = textPaintLight;
        } else {
            _textPaintTitle = textPaintTitleDark;
            _textPaint = textPaintDark;
        }

        if (mSizeforscale > SIZE_1 && mSizeforscale < SIZE_2) {

            _textPaintTitle.getTextBounds(nuc.getMass(), 0 , nuc.getMass().length(), __r);
            _textPaintTitle.getTextBounds(nuc.getElem(), 0 , nuc.getElem().length(), __r2);
            canvas.drawText(nuc.getMass()
                    , _xtn + mRadius / 2
                    , _ytn + mTextScaleFactor
                    , _textPaintTitle);
            canvas.drawText(nuc.getElem()
                    , _xtn + (mNucWidth - __r2.width()) /2
                    , _ytn + mTextScaleFactor +__r.height() + (mNucHeight - (mTextScaleFactor +__r.height() ))/2
                    , _textPaintTitle);

        } else if (mSizeforscale > SIZE_2) {

            _txy = _ytn + mTitleTextScaleFactor;

            _textPaintTitle.getTextBounds(nuc.getMass(), 0 , nuc.getMass().length(), __r);
            _textPaintTitle.getTextBounds(nuc.getElem(), 0 , nuc.getElem().length(), __r2);
            canvas.drawText(nuc.getMass(), _xtn + mRadius / 2
                    , _txy
                    , _textPaintTitle);

            _txy += __r.height();
            canvas.drawText(nuc.getElem(),
                    _xtn + (mNucWidth - __r2.width()) /2
                    ,  _txy
                    , _textPaintTitle);

            canvas.drawText(nuc.getZ().intValue()+"",
                    _xtn + mRadius / 2
                    ,  _txy
                    , _textPaint);



            _txy += 6  + mTextScaleFactor;
            canvas.drawText(nuc.getHalf_life_for_chart()
                    , _xtn + 2
                    , _txy
                    , _textPaint);

            if (nuc.getAbundance().length() > 0) {

                _txy +=  mTextScaleFactor;
                canvas.drawText(nuc.getAbundance()
                        , _xtn + 2
                        , _txy
                        , _textPaint);

            }

            for (int i = 0; i < getNuclideDecaysLabel(nuc).length; i++) {
                pnt_dm = getNuclideDecaysLabel(nuc)[i];
                if (pnt_dm.length == 2) {
                    _txy +=  mTextScaleFactor  + (2 * (2 + i));
                    __color = getColorRGBFromDecayCode(pnt_dm[0]);
                    decayPaint.setARGB(255, __color[0], __color[1], __color[2]);
                    canvas.drawRect(_xtn + 2, _txy - mTextScaleFactor
                            , _xtn + 10
                            , _txy
                            , decayPaint);
                    canvas.drawText(pnt_dm[1], _xtn + 12, _txy, _textPaint);
                }
            }


        }
    }



    int[] __magicnum = new int[]{2, 8, 20, 28, 50, 82, 126};
    int[] __limitsfory = new int[]{2,5,2,14,9,28,12,32,27,50,45,73,76,93};
    int[] __limitsforx = new int[]{1,8,4,18,14,38,20,52, 49,89, 96,138};
    float __y1;
    int __stickout = 1;
    int __off = 1;

    private void plotmagic(Canvas canvas, Paint strokePaint){

        strokePaint.setARGB(255, 255, 255, 255);

        __off = mSizeforscale > SIZE_1 ? 2 : 1;

        for (int i = 0 ;i < __magicnum.length ; i++) {
            __x = (LEFT_OFF + __magicnum[i] * (mNucWidth + NUC_BORDER) - mDeltaX);
            __y = (MAIN_HEIGHT - TOP_OFF - mDeltaY - (__limitsfory[2*i] - __stickout) * (mNucHeight + NUC_BORDER));
            __y1 = (MAIN_HEIGHT - TOP_OFF - mDeltaY - (__limitsfory[2*i + 1] + __stickout)* (mNucHeight + NUC_BORDER));

            canvas.drawRect(__x-__off,__y1,__x+__off,__y, strokePaint);

            __x = (LEFT_OFF + (__magicnum[i] +1)* (mNucWidth + NUC_BORDER) - mDeltaX);
            canvas.drawRect(__x-__off,__y1,__x+__off,__y, strokePaint);
        }
        for (int i = 0 ;i < __magicnum.length -1 ; i++) {
            __x = (LEFT_OFF + (__limitsforx[2*i] - __stickout) * (mNucWidth + NUC_BORDER) - mDeltaX);
            __y = (MAIN_HEIGHT - TOP_OFF - mDeltaY - __magicnum[i] * (mNucHeight + NUC_BORDER));
            __y1 = (LEFT_OFF + (__limitsforx[2*i+1]  + __stickout) * (mNucWidth + NUC_BORDER) - mDeltaX);

            canvas.drawRect(__x, __y-__off, __y1, __y+__off, strokePaint);

            __y = (MAIN_HEIGHT - TOP_OFF - mDeltaY - (__magicnum[i] - 1)* (mNucHeight + NUC_BORDER));
            canvas.drawRect(__x, __y-__off, __y1, __y+__off, strokePaint);

        }

    }

    int pl_width = 5;
    int pl_x = 0;
    int pl_y = 20;
    int pl_height = (int)(TEXT_SIZE_LEGEND/1.8);

    private void plotLegend(Canvas canvas, Paint strokePaint){

        pl_x = 20;
        pl_y = 60;

        strokePaint.setFakeBoldText(true);
        strokePaint.setTextSize(TEXT_SIZE_LEGEND);
        Rect rect = new Rect();
        strokePaint.getTextBounds(decay_legend[0],0,1,rect);

        for (int i = 0; i < decay_legend.length; i++) {
            if(i == 0){
                strokePaint.setTextSize((int)(TEXT_SIZE_LEGEND*1.2));
            } else {
                strokePaint.setTextSize(TEXT_SIZE_LEGEND);
            }
            if(i == 5) continue;
            strokePaint.setARGB(255, colors[COLOR_DECAY_TYPE[i]][0], colors[COLOR_DECAY_TYPE[i]][1],colors[COLOR_DECAY_TYPE[i]][2]);
            canvas.drawText(decay_legend[i]
                    , pl_x + pl_width + 2
                    , pl_y
                    , strokePaint);
            pl_y += pl_height + rect.height()+ 8;
        }

    }

    private void initCanvas(Canvas canvas){

        int light = 250;
        int dark = 50;

        bkgPaintStart.setARGB(255, 0, 0, 0);
        bkgPaint.setARGB(255, 100, 100, 100);

        textPaintLight.setARGB(255, light, light, light);
        textPaintLight.setStyle(Paint.Style.FILL);
        textPaintLight.setAntiAlias(true);

        textPaintTitleLight.setARGB(255, light, light, light);
        textPaintTitleLight.setFakeBoldText(true);
        textPaintTitleLight.setStyle(Paint.Style.FILL);
        textPaintTitleLight.setAntiAlias(true);

        textPaintDark.setARGB(255, dark, dark, dark);
        textPaintDark.setStyle(Paint.Style.FILL);
        textPaintDark.setAntiAlias(true);

        textPaintTitleDark.setARGB(255, dark, dark, dark);
        textPaintTitleDark.setFakeBoldText(true);
        textPaintTitleDark.setStyle(Paint.Style.FILL);
        textPaintTitleDark.setAntiAlias(true);

        strokePaint.setARGB(255, 220, 95, 205);
        strokePaint.setStyle(Paint.Style.FILL);
        strokePaint.setStrokeWidth(1);
        strokePaint.setAntiAlias(true);

        canvas.getClipBounds(clipBounds);
        MAIN_WIDTH = clipBounds.right;
        MAIN_HEIGHT = clipBounds.bottom;

        textPaintLight.setTextSize(mTextSizeStart * mTextScaleFactor );
        textPaintTitleLight.setTextSize(mTextSizeStart * mTextScaleFactor );
        textPaintDark.setTextSize(mTextSizeStart * mTextScaleFactor );
        textPaintTitleDark.setTextSize(mTextSizeStart * mTextScaleFactor );

        mTotDeltaX = 0;//**
        mTotDeltaY = 0;//**

        NUC_WIDTH_START  = MAIN_WIDTH / MAX_N;//**
        NUC_HEIGHT_START = MAIN_HEIGHT / MAX_Z;//**

        if(NUC_HEIGHT_START > 1.3 * NUC_WIDTH_START){
            NUC_HEIGHT_START = (float)1.3 * NUC_WIDTH_START;
        }

        mNucWidth = NUC_WIDTH_START ;
        mNucHeight = NUC_HEIGHT_START ;

        mTotDeltaY = (int)((MAIN_HEIGHT - mNucHeight * MAX_Z)/2.);
        mDeltaY = mTotDeltaY;

        NUC_WIDTH_DELTA = (mNucWidth == 1 ? 0 : NUC_WIDTH_DELTA);
        NUC_HEIGHT_DELTA = (mNucHeight == 1 ? 0 : NUC_HEIGHT_DELTA);

        TEXT_SIZE_LEGEND = (int)(10/Formatter.pxToMm(1)/2.5);

        TEXT_SIZE = (clipBounds.right < SIZE_TRESHOLD
                || clipBounds.bottom < SIZE_TRESHOLD ? TEXT_SIZE_SMALL
                : TEXT_SIZE);
        LABEL_GAP = (clipBounds.right < SIZE_TRESHOLD
                || clipBounds.bottom < SIZE_TRESHOLD ? TEXT_SIZE_SMALL
                : LABEL_GAP);

        setSavedIntialQuantities(myContext.getChartSavedState());

        canvas_initialised = true;

 //   main -> chart -> nuclide -> decay chain . The other way is in initNuclides
        if(myContext.getChartNZatCentre().length == 2){
            centerNuclide(myContext.getChartNZatCentre()[0],myContext.getChartNZatCentre()[1]);
        } else if(mNucSelected != null){
            centerNuclide((float) mNucSelected.getN(), (float) mNucSelected.getZ());
        }
        invalidate();
    }

    private void testFetchOffsprings(){
        for (int i = 0 ; i < NUCS_COLORED.length; i++){
            System.out.println( "**** " + i + " " + NUCS_COLORED[i].getNucid());
            fetchOffsprings(NUCS_COLORED[i]);
        }
    }
}