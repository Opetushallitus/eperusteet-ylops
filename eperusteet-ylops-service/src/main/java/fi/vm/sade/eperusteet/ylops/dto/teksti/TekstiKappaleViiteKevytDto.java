package fi.vm.sade.eperusteet.ylops.dto.teksti;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.dto.Reference;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TekstiKappaleViiteKevytDto {
    private Long id;
    @JsonProperty("_tekstiKappale")
    private Reference tekstiKappaleRef;
    private TekstiKappaleKevytDto tekstiKappale;
    private Omistussuhde omistussuhde;
    private boolean pakollinen;
    private boolean valmis;
    private List<TekstiKappaleViiteKevytDto> lapset;
    private boolean liite;
}
