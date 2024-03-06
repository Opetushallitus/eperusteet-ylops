package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineValinnainenTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class OppiaineBaseDto {
    private Long id;
    private UUID tunniste;
    private Tila tila;
    private OppiaineTyyppi tyyppi;
    private OppiaineValinnainenTyyppi valinnainenTyyppi = OppiaineValinnainenTyyppi.EI_MAARITETTY;
    @JsonProperty("_liittyvaOppiaine")
    private Reference liittyvaOppiaine;
    private String laajuus;
    private boolean koosteinen;
    private LokalisoituTekstiDto nimi;
    private Boolean abstrakti;

    public OppiaineValinnainenTyyppi getValinnainenTyyppi() {
        return valinnainenTyyppi != null ? valinnainenTyyppi : OppiaineValinnainenTyyppi.EI_MAARITETTY;
    }
}
