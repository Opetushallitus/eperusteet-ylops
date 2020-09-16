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

package fi.vm.sade.eperusteet.ylops.dto.koodisto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

/**
 * @author nkala
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisaatioLaajaDto extends OrganisaatioDto {
    private List<String> tyypit;
    private LokalisoituTekstiDto nimi;
    private String kotipaikkaUri;
    private String oppilaitosKoodi;
    private String oppilaitostyyppi;
    private Set<String> organisaatiotyypit;
    private String parentOid;
    private String parentOidPath;
    private List<OrganisaatioLaajaDto> children;

    public List<String> getParentPath() {
        if (StringUtils.isEmpty(this.parentOidPath)) {
            return new ArrayList<>();
        }
        else {
            String[] split = this.parentOidPath.split("\\|");
            return Arrays.stream(split)
                    .filter(x -> !x.isEmpty())
                    .collect(Collectors.toList());
        }
    }
}
