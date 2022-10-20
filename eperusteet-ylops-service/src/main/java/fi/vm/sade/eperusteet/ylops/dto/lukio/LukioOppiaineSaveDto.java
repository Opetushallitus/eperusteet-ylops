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
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * User: tommiratamaa
 * Date: 3.12.2015
 * Time: 11.09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LukioOppiaineSaveDto implements Serializable {
    private Long oppiaineId;
    private LokalisoituTekstiDto nimi;
    private String laajuus;
    private boolean koosteinen;
    private String koodiUri;
    private String koodiArvo;
    private TekstiosaDto tehtava;
    private TekstiosaDto tavoitteet;
    private TekstiosaDto arviointi;
    private Map<LukiokurssiTyyppi, Optional<LokalisoituTekstiDto>> kurssiTyyppiKuvaukset = new HashMap<>();
}
