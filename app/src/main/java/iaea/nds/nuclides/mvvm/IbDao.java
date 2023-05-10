package iaea.nds.nuclides.mvvm;



import java.util.List;



import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteStatement;
import iaea.nds.nuclides.db.entities.Nuclides;
import iaea.nds.nuclides.db.NuclidesAndRadiation;
import iaea.nds.nuclides.db.CumFYForDetail;
import iaea.nds.nuclides.db.DecayForDetails;
import iaea.nds.nuclides.db.NuclideForChart;
import iaea.nds.nuclides.db.RadiationForDetail;
import iaea.nds.nuclides.db.entities.Thermal_cross_sect;


@Dao
public interface IbDao {


    @Insert
    void insert(Nuclides n );

    @Query("UPDATE nuclides set pk = rowid")
    int updatepk();

    //@Query("UPDATE l_decays set daughter_nucid = '116INm1' where nucid = '116INm2'")
    //int update_in116();

    @Query("SELECT *, pk FROM nuclides where ROWID = :rowidPar")
    LiveData<Nuclides> getNuclide(String rowidPar);

    @Query("SELECT * FROM nuclides where nucid = :nucidPar")
    Nuclides[] getNuclideFromNucid(String nucidPar);

    @Query("SELECT ROWID from nuclides where pk = :pkPar")
    int getRowidFromPk(String pkPar);

    @RawQuery
    List<NuclideForChart> getNuclidesForChart(SupportSQLiteQuery query);

    @RawQuery(observedEntities = Nuclides.class)
    DataSource.Factory<Integer, Nuclides> getNuclidesPagedStd(SupportSQLiteQuery query);

    @RawQuery(observedEntities = NuclidesAndRadiation.class)
    DataSource.Factory<Integer, NuclidesAndRadiation> getNuclidesAndRadiationPaged(SupportSQLiteQuery query);

    @RawQuery
    List<DecayForDetails> getDecays(SupportSQLiteQuery query);

    @RawQuery
    List<RadiationForDetail> getRadiationForDetail(SupportSQLiteQuery query);

    @RawQuery
    List<CumFYForDetail> getCumFYForDetail(SupportSQLiteQuery query);

    @RawQuery
    List<Thermal_cross_sect> getTcs(SupportSQLiteQuery query);

}
