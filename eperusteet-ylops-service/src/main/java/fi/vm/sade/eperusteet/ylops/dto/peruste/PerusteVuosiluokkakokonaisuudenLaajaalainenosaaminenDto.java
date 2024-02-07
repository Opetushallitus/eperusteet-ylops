package fi.vm.sade.eperusteet.ylops.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerusteVuosiluokkakokonaisuudenLaajaalainenosaaminenDto implements ReferenceableDto {
    private Long id;
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("_laajaalainenosaaminen")
    private PerusteLaajaalainenosaaminenDto laajaalainenOsaaminen;
    private LokalisoituTekstiDto kuvaus;
}
