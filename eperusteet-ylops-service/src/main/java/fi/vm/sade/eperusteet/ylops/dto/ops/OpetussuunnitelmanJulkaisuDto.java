package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class OpetussuunnitelmanJulkaisuDto {
    private Long id;
    private OpetussuunnitelmaInfoDto opetussuunnitelma;
    private LokalisoituTekstiDto tiedote;
    private Set<Reference> dokumentit = new HashSet<>();
    private int revision;
    private Date luotu;
    private String luoja;
}