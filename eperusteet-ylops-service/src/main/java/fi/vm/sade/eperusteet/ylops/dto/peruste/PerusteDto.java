/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.ylops.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.Lops2019SisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.LukiokoulutuksenPerusteenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteVersionDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author nkala
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PerusteDto extends PerusteBaseDto {
    private PerusteVersionDto globalVersion;
    private PerusopetuksenPerusteenSisaltoDto perusopetus;
    private LukiokoulutuksenPerusteenSisaltoDto lukiokoulutus;
    private Lops2019SisaltoDto lops2019;
    private EsiopetuksenPerusteenSisaltoDto esiopetus;
    private TPOOpetuksenSisaltoDto tpo;
    private AipePerusteenSisaltoDto aipe;

    @JsonIgnore
    public TekstiKappaleViiteDto getTekstiKappaleViiteSisalto() {
        if (getPerusopetus() != null) {
            return getPerusopetus().getSisalto();
        }

        if (getEsiopetus() != null) {
            return getEsiopetus().getSisalto();
        }

        if (getTpo() != null) {
            return getTpo().getSisalto();
        }

        if (getAipe() != null) {
            return getAipe().getSisalto();
        }

        if (getLops2019() != null) {
            return getLops2019().getSisalto();
        }

        return null;
    }
}
