package iaea.nds.nuclides.mvvm;


import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import iaea.nds.nuclides.Formatter;;
import iaea.nds.nuclides.R;
import iaea.nds.nuclides.db.NuclidesAndRadiation;



public class NuclideListItemAdapterPagedRad extends PagedListAdapter<NuclidesAndRadiation, NuclideListItemAdapterPagedRad.NuclideHolder> {

    private NuclideListViewModel nuclideListViewModel = null;

    public NuclideListItemAdapterPagedRad(){
        super(DIFF_CALLBACK);
    }

    public void setNuclideListViewModel(NuclideListViewModel nuclideListViewModel){
        this.nuclideListViewModel = nuclideListViewModel;
    }

    private static DiffUtil.ItemCallback<NuclidesAndRadiation> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<NuclidesAndRadiation>() {

                @Override
                public boolean areItemsTheSame(NuclidesAndRadiation oldNuclide, NuclidesAndRadiation newNuclide) {
                    return true; //oldConcert.getId() == newConcert.getId();
                }

                @Override
                public boolean areContentsTheSame(NuclidesAndRadiation oldNuclide, NuclidesAndRadiation newNuclide) {
                    return true;// oldConcert.equals(newConcert);
                }
            };

    @NonNull
    @Override
    public NuclideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.nuclide_list_item_rad, parent, false);
        return new NuclideHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull NuclideHolder holder, int position) {

        NuclidesAndRadiation currNuc = getItem(position);

        if (currNuc == null){return;}

        holder.mass.setText( Html.fromHtml(Formatter.nucMassWithMetaHTML(
                (currNuc.nuclide.getZ().intValue() + currNuc.nuclide.getN().intValue())+""
                ,currNuc.nuclide.getNucid())));
        holder.zeta.setText(currNuc.nuclide.getZ().toString());
        holder.sym.setText(Formatter.nucSymbol(currNuc.nuclide.getSymbol(), currNuc.nuclide.getTentative()));

        holder.hl.setText( Formatter.halfLife(
                currNuc.nuclide.getHalf_life(),
                currNuc.nuclide.getHalf_life_unit(),
                currNuc.nuclide.getHalf_life_unc()));

        if(currNuc.decay_radiation == null || currNuc.decay_radiation.getEnergy_num() == null){
            holder.energy.setText(" ");
        } else {
            holder.energy.setText(currNuc.decay_radiation.getEnergy_num().toString() + " keV");
        }

        if(currNuc.decay_radiation == null || currNuc.decay_radiation.getIntensity() == null){
            holder.intensity.setText(" ");
        } else {
            holder.intensity.setText(  Formatter.radintensity(nuclideListViewModel.getLblRadTypeSelected(),currNuc.decay_radiation.getIntensity() ));
        }
        

    }
    @Override
    public int getItemCount() {
        int i = super.getItemCount();
        return i;
    }


    class NuclideHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mass;
        private TextView zeta;
        private TextView sym;
        private TextView hl;
        private TextView intensity;
        private TextView energy;

        public NuclideHolder(@NonNull View view) {
            super(view);

            mass = view.findViewById(R.id.nuclide_mass);
            zeta =  view.findViewById(R.id.nuclide_zeta);
            sym =  view.findViewById(R.id.nuclide_symbol);
            hl =  view.findViewById(R.id.nuclide_half_life);
            intensity = view.findViewById(R.id.rad_intensity);
            energy = view.findViewById(R.id.rad_energy);
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
           nuclideListViewModel.itemClicked(getItem(getAdapterPosition()).nuclide.getPk());

        }
    }

}
