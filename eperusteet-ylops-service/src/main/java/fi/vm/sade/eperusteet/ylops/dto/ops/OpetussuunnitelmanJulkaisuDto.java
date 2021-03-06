package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpetussuunnitelmanJulkaisuDto {
    private Long id;
    private OpetussuunnitelmaInfoDto opetussuunnitelma;
    private LokalisoituTekstiDto tiedote;
    private Set<Long> dokumentit = new HashSet<>();
    private int revision;
    private Date luotu;
    private String luoja;
    private KayttajanTietoDto kayttajanTieto;
}
