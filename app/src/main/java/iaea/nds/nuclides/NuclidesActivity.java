/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package iaea.nds.nuclides;

import android.os.Bundle;
import android.text.Html;

import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;
import iaea.nds.nuclides.db.CumFYForDetail;
import iaea.nds.nuclides.db.DecayForDetails;
import iaea.nds.nuclides.db.RadiationForDetail;

import iaea.nds.nuclides.db.entities.Thermal_cross_sect;
import iaea.nds.nuclides.mvvm.NuclidesViewModel;

import java.util.List;

/**
 * Displays a Nuclide with details.
 *
 * Google search: android textview click on word
 */
public class NuclidesActivity extends BaseActivity  {


    Nuclide mNuc = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail);
        initButtonDrawer();

        NuclidesViewModel nuclidesVM = ViewModelProviders.of(this).get(NuclidesViewModel.class);

        String rowid = getNucSelectedRowid();

        nuclidesVM.getNuclide(rowid).observe(this, nuclide -> {
            if(nuclide != null){
                mNuc = new Nuclide(nuclide);
                mNuc.isFrench = getLanguage() == Config.PREFS_VALUE_FRENCH;
                mNuc.isbqg = getSpecificActivityUnits() != Config.PREFS_VALUE_CIG;

                List<DecayForDetails> decs = nuclidesVM.nuclideDecays(rowid);
                mNuc.nucdecs = decs.toArray(new DecayForDetails[decs.size()]);

                decs = nuclidesVM.nuclideParents(nuclide.getNucid());
                mNuc.nucparents = decs.toArray(new DecayForDetails[decs.size()]);

                List<RadiationForDetail> rads = nuclidesVM.radiationForDetail(rowid, getRadiationOrderBy());
                mNuc.nucrad = rads.toArray(new RadiationForDetail[rads.size()]);

                List<CumFYForDetail> cfy = nuclidesVM.cumFYForForDetail(rowid);
                mNuc.nuccfy = cfy.toArray(new CumFYForDetail[cfy.size()]);

                List<Thermal_cross_sect> tcs = nuclidesVM.thermal_cs(nuclide.getNucid(), nuclide.getL_seqno()+"");
                mNuc.nuctcs = tcs.toArray(new Thermal_cross_sect[tcs.size()]);


                init();


            }
        });

    }



    private void init(){


        TextView header = findViewById(R.id.nuclide_header);
        TextView definition =  findViewById(R.id.nuclide_detail);

        TextView nuclideJsp =  findViewById(R.id.nuclide_jsp);

        mNuc.setLeftOffset((getResources().getString(R.string.left_pad)));
        mNuc.setNuclideLabelColor(getResources().getColor(R.color.text_foreground_label_detail));
        mNuc.setDecayradtbltitleColor(getResources().getColor(R.color.button_disabled));
        mNuc.setDecayradselecColor(getResources().getColor(R.color.rad_selected));
        mNuc.setNdsTag(getResources().getString(R.string.nds_tag));
        mNuc.setTentative(getResources().getString(R.string.tentative));
        header.setText(mNuc.getHeaderDisplay());


        definition.setText(mNuc.getDetailDisplay(isFilterByRad()));

        definition.setMovementMethod(LinkMovementMethod.getInstance());

        Formatter.stripUnderlines(definition);


        String jsp = getResources().getString(R.string.nuclide_jsp);
        String str = getResources().getString(R.string.more_about);
        str = str.replaceAll("NUCID",mNuc.getMynuc().getNucid());
        str = str.replaceAll("href", "href=\""+jsp + mNuc.getMynuc().getNucid()+"\"");
        nuclideJsp.setText(
                Formatter.createIndentedText(
                        Html.fromHtml(str
                                //"&#x25cf; More about <b> "+nucid +"</b> on <a href=\"" + jsp + nucid + "\"> on NDS web</a> <br>"
                                //+"&#x25cf; <b>Uncertainty</b> applies to the\nleast significant digit(s)"
                                //  + "<br>&#x25cf; Refer to the <b>Guide</b> for the meaning of the data"
                        ),
                        0,50));

        // nuclideJsp.setText(Util.createIndentedText(nuclideJsp.get., 0, 50));
        nuclideJsp. setMovementMethod(LinkMovementMethod.getInstance());
        Formatter.stripUnderlines(nuclideJsp);

        TextView nds = (TextView)findViewById(R.id.nds_tag);

        nds.setMovementMethod(LinkMovementMethod.getInstance());
        Formatter.stripUnderlines(nds);

        /* parent-daughter clickable nucid */
        for (int i = 0; i < mNuc.getClickSpans().length; i++) {
            mNuc.getClickSpans()[i].myContext = this;
        }


        if(isRightAligned()) {

            final HorizontalScrollView mScrollView = findViewById(R.id.nuclide_scroll);
            definition.setGravity(Gravity.RIGHT);

            mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> mScrollView.post(() -> mScrollView.fullScroll(View.FOCUS_RIGHT)));
        }

    }


}
