package iaea.nds.nuclides.mvvm;

import androidx.paging.DataSource;
import iaea.nds.nuclides.db.entities.Nuclides;

public class NuclideDataSourceFactory extends DataSource.Factory<Integer, Nuclides> {
    @Override
    public DataSource<Integer, Nuclides> create() {
        return null;
    }
}
