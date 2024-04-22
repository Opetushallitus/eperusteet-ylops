package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.LukiokoulutuksenPerusteenSisaltoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class LukiokoulutusPerusteDto extends PerusteDto {
    private LukiokoulutuksenPerusteenSisaltoDto lukiokoulutus;
}
