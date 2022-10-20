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

import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author jhyoty
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerusteOppiaineDto implements ReferenceableDto {

    private Long id;
    private UUID tunniste;
    private String koodiUri;
    private String koodiArvo;
    private Boolean koosteinen;
    private Boolean abstrakti;
    private LokalisoituTekstiDto nimi;
    private PerusteTekstiOsaDto tehtava;
    private Set<PerusteOppiaineDto> oppimaarat;
    private Set<PerusteOpetuksenkohdealueDto> kohdealueet;
    private List<PerusteOppiaineenVuosiluokkakokonaisuusDto> vuosiluokkakokonaisuudet;

    public Optional<PerusteOppiaineenVuosiluokkakokonaisuusDto> getVuosiluokkakokonaisuus(Reference tunniste) {
        return getVuosiluokkakokonaisuus(UUID.fromString(tunniste.toString()));
    }

    public Optional<PerusteOppiaineenVuosiluokkakokonaisuusDto> getVuosiluokkakokonaisuus(UUID tunniste) {
        return vuosiluokkakokonaisuudet.stream()
                .filter(v -> v.getVuosiluokkaKokonaisuus().getTunniste().equals(tunniste))
                .findAny();
    }
}
