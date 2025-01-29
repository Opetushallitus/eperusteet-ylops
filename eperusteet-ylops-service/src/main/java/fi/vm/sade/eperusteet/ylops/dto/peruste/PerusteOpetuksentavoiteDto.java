package fi.vm.sade.eperusteet.ylops.dto.peruste;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerusteOpetuksentavoiteDto implements ReferenceableDto {
    private Long id;
    private UUID tunniste;
    private LokalisoituTekstiDto tavoite;
    private Set<PerusteKeskeinensisaltoalueDto> sisaltoalueet;
    @JsonProperty("laajaalaisetosaamiset")
    private Set<PerusteLaajaalainenosaaminenDto> laajattavoitteet;
    private Set<PerusteOpetuksenkohdealueDto> kohdealueet;
    private Set<PerusteTavoitteenArviointiDto> arvioinninkohteet;
    private LokalisoituTekstiDto arvioinninKuvaus;
    private LokalisoituTekstiDto arvioinninOtsikko;
    private LokalisoituTekstiDto vapaaTeksti;
    private LokalisoituTekstiDto tavoitteistaJohdetutOppimisenTavoitteet;
    private List<PerusteOppiaineenTavoitteenOpetuksenTavoiteDto> oppiaineenTavoitteenOpetuksenTavoitteet;
}
