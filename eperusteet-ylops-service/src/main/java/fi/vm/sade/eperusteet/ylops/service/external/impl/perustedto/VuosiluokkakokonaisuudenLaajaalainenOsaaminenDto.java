package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VuosiluokkakokonaisuudenLaajaalainenOsaaminenDto implements ReferenceableDto {
    private Long id;
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("_laajaalainenOsaaminen")
    private LaajaalainenOsaaminenDto laajaalainenOsaaminen;
    private PerusteenLokalisoituTekstiDto kuvaus;
}
