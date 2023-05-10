package iaea.nds.nuclides.db;

import androidx.room.Embedded;
import iaea.nds.nuclides.db.entities.Decay_radiations;
import iaea.nds.nuclides.db.entities.Nuclides;


public class NuclidesAndRadiation {
    @Embedded
    public Nuclides nuclide;
    @Embedded
    public Decay_radiations decay_radiation;

    public NuclidesAndRadiation(Nuclides nuclide, Decay_radiations decay_radiation){
        this.nuclide = nuclide;
        this.decay_radiation = decay_radiation;
    }
}
