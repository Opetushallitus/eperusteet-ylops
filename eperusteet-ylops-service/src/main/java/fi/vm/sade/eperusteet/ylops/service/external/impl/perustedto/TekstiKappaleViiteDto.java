package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TekstiKappaleViiteDto {
    private Long id;
    @JsonProperty(value = "perusteenOsa")
    private TekstiKappaleDto tekstiKappale;
    private List<TekstiKappaleViiteDto> lapset = new ArrayList<>();
}
