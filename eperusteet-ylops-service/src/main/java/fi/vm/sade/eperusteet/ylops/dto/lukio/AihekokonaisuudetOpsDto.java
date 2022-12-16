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

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.AihekokonaisuudetBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.AihekokonaisuudetDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.AihekokonaisuusOpsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * User: tommiratamaa
 * Date: 19.11.2015
 * Time: 14.40
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AihekokonaisuudetOpsDto extends AihekokonaisuudetBaseDto
        implements PerusteeseenViittaava<AihekokonaisuudetDto> {
    @JsonIgnore
    private AihekokonaisuudetDto perusteen;
    private AihekokonaisuudetBaseDto parent;
    private List<AihekokonaisuusOpsDto> aihekokonaisuudet = new ArrayList<>();

    @Override
    @JsonIgnore // already uuidTunniste
    public UUID getTunniste() {
        return getUuidTunniste();
    }

    @Override
    public Stream<? extends PerusteeseenViittaava<?>> viittaukset() {
        return aihekokonaisuudet.stream();
    }
}
