package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VuosiluokkakokonaisuusDto implements ReferenceableDto {

    private Long id;
    private Reference tunniste;
    private LokalisoituTekstiDto nimi;
    private TekstiosaDto siirtymaEdellisesta;
    private TekstiosaDto tehtava;
    private TekstiosaDto siirtymaSeuraavaan;
    private TekstiosaDto laajaalainenosaaminen;
    private Tila tila;
    private Set<LaajaalainenosaaminenDto> laajaalaisetosaamiset;
    private List<VapaatekstiPaikallinentarkennusDto> vapaatTekstit = new ArrayList<>();

    public VuosiluokkakokonaisuusDto(Reference tunniste) {
        this.tunniste = tunniste;
    }

}
