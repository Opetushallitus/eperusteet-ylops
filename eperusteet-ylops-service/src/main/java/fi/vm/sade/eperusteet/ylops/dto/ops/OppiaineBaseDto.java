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
package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineValinnainenTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

/**
 * @author mikkom
 */
@Getter
@Setter
public abstract class OppiaineBaseDto {
    private Long id;
    private UUID tunniste;
    private Tila tila;
    private OppiaineTyyppi tyyppi;
    private OppiaineValinnainenTyyppi valinnainenTyyppi = OppiaineValinnainenTyyppi.EI_MAARITETTY;
    @JsonProperty("_liittyvaOppiaine")
    private Reference liittyvaOppiaine;
    private Long jnro;
    private String laajuus;
    private boolean koosteinen;
    private LokalisoituTekstiDto nimi;
    private Boolean abstrakti;

    public OppiaineValinnainenTyyppi getValinnainenTyyppi() {
        return valinnainenTyyppi != null ? valinnainenTyyppi : OppiaineValinnainenTyyppi.EI_MAARITETTY;
    }
}
