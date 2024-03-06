package fi.vm.sade.eperusteet.ylops.dto.peruste;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerusteTekstiKappaleViiteMatalaDto {
    private Long id;
    private PerusteTekstiKappaleDto perusteenOsa;
    private Long perusteTekstikappaleId;
    private Omistussuhde omistussuhde;
}
