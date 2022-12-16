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
package fi.vm.sade.eperusteet.ylops.dto.peruste.lukio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.eperusteet.ylops.domain.lukio.LukiokurssiTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiOsaDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by jsikio.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LukioPerusteOppiaineDto implements PerusteenOsa {
    private Long id;
    private UUID tunniste;
    private String koodiUri;
    private String koodiArvo;
    private boolean koosteinen;
    private Integer jarjestys;
    private Boolean abstrakti;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto kuvaus;
    private LokalisoituTekstiDto pakollinenKurssiKuvaus;
    private LokalisoituTekstiDto syventavaKurssiKuvaus;
    private LokalisoituTekstiDto soveltavaKurssiKuvaus;
    private PerusteTekstiOsaDto tehtava;
    private PerusteTekstiOsaDto tavoitteet;
    private PerusteTekstiOsaDto arviointi;
    private Set<LukioPerusteOppiaineDto> oppimaarat = new HashSet<>();
    private Set<LukiokurssiPerusteDto> kurssit = new HashSet<>();

    @Override
    public Stream<? extends PerusteenOsa> osat() {
        return Stream.concat(oppimaarat.stream(), kurssit.stream());
    }

    @JsonIgnore
    public Map<LukiokurssiTyyppi, Optional<LokalisoituTekstiDto>> getKurssiTyyppiKuvaukset() {
        Map<LukiokurssiTyyppi, Optional<LokalisoituTekstiDto>> map = new HashMap<>();
        map.put(LukiokurssiTyyppi.VALTAKUNNALLINEN_PAKOLLINEN, Optional.ofNullable(pakollinenKurssiKuvaus));
        map.put(LukiokurssiTyyppi.VALTAKUNNALLINEN_SYVENTAVA, Optional.ofNullable(syventavaKurssiKuvaus));
        map.put(LukiokurssiTyyppi.VALTAKUNNALLINEN_SOVELTAVA, Optional.ofNullable(soveltavaKurssiKuvaus));
        return map;
    }

    public Stream<LukioPerusteOppiaineDto> maarat() {
        return oppimaarat.stream();
    }

    public Stream<LukioPerusteOppiaineDto> maarineen() {
        return Stream.concat(Stream.of(this), maarat().flatMap(LukioPerusteOppiaineDto::maarineen));
    }
}
