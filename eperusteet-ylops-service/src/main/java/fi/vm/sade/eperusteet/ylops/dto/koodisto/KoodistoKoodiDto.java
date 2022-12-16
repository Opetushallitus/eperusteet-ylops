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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mikkom
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KoodistoKoodiDto {
    private Long id;
    private String koodiUri;
    private String koodiArvo;
    private String versio;
    private String voimassaAlkuPvm;
    private String voimassaLoppuPvm;
    private KoodistoMetadataDto[] metadata;

    public LokalisoituTekstiDto getNimi() {
        Map<String, String> tekstit = new HashMap<>();
        if (this.getMetadata() != null) {
            for (KoodistoMetadataDto metadata : this.getMetadata()) {
                try {
                    tekstit.put(metadata.getKieli(), metadata.getNimi());
                } catch (IllegalArgumentException ignored) {
                }
            }
            return new LokalisoituTekstiDto(tekstit);
        }

        return null;
    }
}
