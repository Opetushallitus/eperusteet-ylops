package fi.vm.sade.eperusteet.ylops.dto.peruste.aipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineenTavoitteenOpetuksenTavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTavoitteenArviointiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerusteAIPEOpetuksentavoiteDto implements ReferenceableDto {
    private Long id;
    private UUID tunniste;
    private LokalisoituTekstiDto tavoite;
    private Set<Reference> sisaltoalueet = new HashSet<>();
    private Set<Reference> laajattavoitteet = new HashSet<>();
    private Set<Reference> kohdealueet = new HashSet<>();
    private Set<PerusteTavoitteenArviointiDto> arvioinninkohteet = new HashSet<>();
    private LokalisoituTekstiDto arvioinninKuvaus;
    private LokalisoituTekstiDto arvioinninOsaamisenKuvaus;
    private LokalisoituTekstiDto arvioinninOtsikko;
    private LokalisoituTekstiDto vapaaTeksti;
    private LokalisoituTekstiDto tavoitteistaJohdetutOppimisenTavoitteet;
    private List<PerusteOppiaineenTavoitteenOpetuksenTavoiteDto> oppiaineenTavoitteenOpetuksenTavoitteet = new ArrayList<>();
}
