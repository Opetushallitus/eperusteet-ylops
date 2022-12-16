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

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * User: tommiratamaa
 * Date: 12.1.2016
 * Time: 17.45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LukioOppimaaraPerusTiedotDto implements Serializable {
    private Long id;
    private Long oppiaineId;
    private Date muokattu;
    private UUID tunniste;
    private Tila tila;
    private boolean oma;
    private boolean maariteltyPohjassa;
    private Integer jarjestys;
    private OppiaineTyyppi tyyppi;
    private String laajuus;
    private boolean koosteinen;
    private LokalisoituTekstiDto nimi;
    private Boolean abstrakti;
    private String koodiUri;
    private String koodiArvo;
    private String kieliKoodiUri;
    private String kieliKoodiArvo;
}
