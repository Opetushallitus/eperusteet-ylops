/*
 *  Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 *  This program is free software: Licensed under the EUPL, Version 1.1 or - as
 *  soon as they will be approved by the European Commission - subsequent versions
 *  of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 */

package fi.vm.sade.eperusteet.ylops.dto.lukio;

import fi.vm.sade.eperusteet.ylops.domain.lukio.LukiokurssiTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.LukioPerusteOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.PerusteenOsa;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * User: tommiratamaa
 * Date: 12.1.2016
 * Time: 14.34
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class LukioOppiaineTiedotDto extends LukioOppiaineRakenneDto<LukioOppimaaraPerusTiedotDto, LukiokurssiOpsDto>
        implements PerusteeseenViittaava<LukioPerusteOppiaineDto> {
    private LukioPerusteOppiaineDto perusteen;
    @Setter
    private TekstiosaDto tehtava;
    @Setter
    private TekstiosaDto tavoitteet;
    @Setter
    private TekstiosaDto arviointi;
    @Setter
    private List<LukioOppimaaraPerusTiedotDto> pohjanTarjonta = new ArrayList<>();
    @Setter
    private Map<LukiokurssiTyyppi, Optional<LokalisoituTekstiDto>> kurssiTyyppiKuvaukset = new HashMap<>();

    public void setPerusteen(LukioPerusteOppiaineDto perusteen) {
        this.perusteen = perusteen;
        PerusteenOsa.map(this.perusteen, this);
    }

    @Override
    public Stream<? extends PerusteeseenViittaava<?>> viittaukset() {
        return kurssit.stream();
    }
}
