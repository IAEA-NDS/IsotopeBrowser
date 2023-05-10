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
import iaea.nds.nuclides.Nuclide;
import iaea.nds.nuclides.R;
import iaea.nds.nuclides.db.entities.Nuclides;
import iaea.nds.nuclides.db.NuclidesAndRadiation;

public class NuclideListItemAdapterPagedStd extends PagedListAdapter<NuclidesAndRadiation, NuclideListItemAdapterPagedStd.NuclideHolder> {

    private NuclideListViewModel nuclideListViewModel = null;

    public NuclideListItemAdapterPagedStd(){
        super(DIFF_CALLBACK);
    }

    public void setNuclideListViewModel(NuclideListViewModel nuclideListViewModel){
        this.nuclideListViewModel = nuclideListViewModel;
    }

    private static DiffUtil.ItemCallback<NuclidesAndRadiation> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<NuclidesAndRadiation>() {

                @Override
                public boolean areItemsTheSame(NuclidesAndRadiation oldNuclide, NuclidesAndRadiation newNuclide) {
                    return true;
                }
                @Override
                public boolean areContentsTheSame(NuclidesAndRadiation NuclidesAndRadiation, NuclidesAndRadiation newNuclide) {
                    return true;
                }
            };

    @NonNull
    @Override
    public NuclideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.nuclide_list_item_std, parent, false);
        return new NuclideHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull NuclideHolder holder, int position) {
        if(getItem(position) == null){
            return;
        }
        Nuclides currNuc = getItem(position).nuclide;

        if (currNuc == null){return;}


        holder.mass.setText( Html.fromHtml(Formatter.nucMassWithMetaHTML(
                (currNuc.getMass())+""
                ,currNuc.getNucid())));
        holder.zeta.setText(currNuc.getZ().toString());
        //System.out.println(currNuc.getNucid() + " **********");
        holder.sym.setText(Formatter.nucSymbol(currNuc.getSymbol(), currNuc.getTentative()));

        holder.hl.setText( Formatter.halfLife(
                currNuc.getHalf_life(),
                currNuc.getHalf_life_unit(),
                currNuc.getHalf_life_unc()));

        nuclideListViewModel.setNuclideDecayAbundance(currNuc.getPk(),currNuc.getAbundance(), currNuc.getAbundance_unc(), holder.decay);
        holder.jp.setText(currNuc.getJp());


    }
    @Override
    public int getItemCount() {

        return super.getItemCount();
    }


    class NuclideHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mass;
        private TextView zeta;
        private TextView sym;
        private TextView hl;
        private TextView jp;
        private TextView decay;

        public NuclideHolder(@NonNull View view) {
            super(view);

            mass = view.findViewById(R.id.nuclide_mass);
            zeta =  view.findViewById(R.id.nuclide_zeta);
            sym =  view.findViewById(R.id.nuclide_symbol);
            hl =  view.findViewById(R.id.nuclide_half_life);
            jp = view.findViewById(R.id.nuclide_jp);
            decay = view.findViewById(R.id.nuclide_decay);
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            nuclideListViewModel.itemClicked(getItem(getAbsoluteAdapterPosition()).nuclide.getPk());

            /* TO TEST the nuclide detail page on all nuclides
            To use this : search with * , scroll down to the last nuclide, then click on a nuclide
            int i = 1;
            try{

            Nuclide n = null;
            for( i = 1; i < 4336; i++){
                if(i==4300)
                    System.out.println(" **** "  + i);
                nuclideListViewModel.itemClicked(getItem(i).nuclide.getPk());
                n =  new Nuclide(getItem(i).nuclide);
                n.getDetailDisplay(false);
            }

            } catch (Exception e){
                i = i+0;
                e.printStackTrace();
            }

*/

        }
    }


}

