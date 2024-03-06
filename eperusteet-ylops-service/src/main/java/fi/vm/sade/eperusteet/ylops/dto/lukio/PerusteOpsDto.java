package fi.vm.sade.eperusteet.ylops.dto.lukio;

import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.PerusteenOsa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class PerusteOpsDto<T extends PerusteenOsa, OpsT extends PerusteeseenViittaava<T>>
        implements PerusteeseenViittaava<T>, PerusteenOsa {
    private T perusteen;
    private OpsT paikallinen;
    @Setter
    private String kommentti; // tallennusta varten

    public PerusteOpsDto(T perusteen, OpsT paikallinen) {
        this.perusteen = perusteen;
        this.paikallinen = paikallinen;
        map();
    }

    private void map() {
        PerusteenOsa.map(this.perusteen, this.paikallinen);
    }

    public PerusteOpsDto(OpsT paikallinen) {
        this.paikallinen = paikallinen;
    }

    @Override
    public void setPerusteen(T vastaava) {
        this.perusteen = vastaava;
        map();
    }

    public void setPaikallinen(OpsT paikallinen) {
        this.paikallinen = paikallinen;
        map();
    }

    @Override
    public UUID getTunniste() {
        if (perusteen != null) {
            return perusteen.getTunniste();
        }
        if (paikallinen != null) {
            return paikallinen.getTunniste();
        }
        return null;
    }
}
