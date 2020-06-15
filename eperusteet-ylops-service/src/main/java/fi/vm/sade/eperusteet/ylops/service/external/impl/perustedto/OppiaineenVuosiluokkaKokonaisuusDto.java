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
package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jhyoty
 */
@Getter
@Setter
public class OppiaineenVuosiluokkaKokonaisuusDto implements ReferenceableDto {
    private Long id;
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("_vuosiluokkaKokonaisuus")
    private VuosiluokkakokonaisuusDto vuosiluokkaKokonaisuus;
    private TekstiOsaDto tehtava;
    private TekstiOsaDto tyotavat;
    private TekstiOsaDto ohjaus;
    private TekstiOsaDto arviointi;
    private TekstiOsaDto tavoitteistaJohdetutOppimisenTavoitteet;
    private TekstiOsaDto sisaltoalueinfo;
    private PerusteenLokalisoituTekstiDto vapaaTeksti;
    private List<OpetuksenTavoiteDto> tavoitteet;
    private List<KeskeinenSisaltoalueDto> sisaltoalueet;
}
