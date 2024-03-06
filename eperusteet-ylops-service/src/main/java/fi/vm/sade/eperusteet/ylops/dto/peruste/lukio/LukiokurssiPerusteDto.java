package fi.vm.sade.eperusteet.ylops.dto.peruste.lukio;

import fi.vm.sade.eperusteet.ylops.dto.peruste.KurssiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiOsaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class LukiokurssiPerusteDto extends KurssiDto implements PerusteenOsa {
    private Long oppiaineId;
    private Integer jarjestys;
    private PerusteenLukiokurssiTyyppi tyyppi;
    private LukioOpetussuunnitelmaRakenneDto opetussuunnitelma;
    private PerusteTekstiOsaDto tavoitteet;
    private PerusteTekstiOsaDto keskeisetSisallot;
    private PerusteTekstiOsaDto tavoitteetJaKeskeisetSisallot;
}
