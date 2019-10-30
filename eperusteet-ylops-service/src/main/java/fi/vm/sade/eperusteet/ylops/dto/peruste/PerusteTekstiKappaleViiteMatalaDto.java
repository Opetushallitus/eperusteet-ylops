package fi.vm.sade.eperusteet.ylops.dto.peruste;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerusteTekstiKappaleViiteMatalaDto {
    private Long id;
    private PerusteTekstiKappaleDto perusteenOsa;
    private Long perusteTekstikappaleId;
    private Omistussuhde omistussuhde;
}
