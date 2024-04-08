package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpetuksenTavoiteDto implements ReferenceableDto {
    private Long id;
    private UUID tunniste;
    private PerusteenLokalisoituTekstiDto tavoite;
    @JsonIdentityReference(alwaysAsId = true)
    private Set<KeskeinenSisaltoalueDto> sisaltoalueet;
    @JsonIdentityReference(alwaysAsId = true)
    private Set<LaajaalainenOsaaminenDto> laajattavoitteet;
    @JsonIdentityReference(alwaysAsId = true)
    private Set<OpetuksenKohdealueDto> kohdealueet;
    private Set<TavoitteenArviointiDto> arvioinninkohteet;
    private PerusteenLokalisoituTekstiDto arvioinninKuvaus;
    private PerusteenLokalisoituTekstiDto arvioinninOtsikko;
    private PerusteenLokalisoituTekstiDto vapaaTeksti;
    private PerusteenLokalisoituTekstiDto tavoitteistaJohdetutOppimisenTavoitteet;
    private List<OppiaineenTavoitteenOpetuksenTavoiteDto> oppiaineenTavoitteenOpetuksenTavoitteet;
}
