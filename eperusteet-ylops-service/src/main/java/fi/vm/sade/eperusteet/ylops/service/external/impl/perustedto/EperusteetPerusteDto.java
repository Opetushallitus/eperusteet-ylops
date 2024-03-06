package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.Lops2019SisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.LukiokoulutuksenPerusteenSisaltoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class EperusteetPerusteDto extends PerusteDto {
    private Long id;
    private PerusteVersionDto globalVersion;
    private PerusopetuksenPerusteenSisaltoDto perusopetus;
    private EsiopetuksenPerusteenSisaltoDto esiopetus;
    private LukiokoulutuksenPerusteenSisaltoDto lukiokoulutus;
    private Lops2019SisaltoDto lops2019;
    private TPOOpetuksenSisaltoDto tpo;
    private AipePerusteenSisaltoDto aipe;
}
