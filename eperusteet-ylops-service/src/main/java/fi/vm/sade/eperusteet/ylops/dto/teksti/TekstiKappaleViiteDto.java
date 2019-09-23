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
package fi.vm.sade.eperusteet.ylops.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.dto.Reference;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @author mikkom
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TekstiKappaleViiteDto {
    private Long id;

    @JsonProperty("_tekstiKappale")
    private Reference tekstiKappaleRef;
    private TekstiKappaleDto tekstiKappale;
    private Omistussuhde omistussuhde;
    private boolean pakollinen;
    private boolean valmis;
    private Long perusteTekstikappaleId;
    private boolean naytaPerusteenTeksti = true;

    public TekstiKappaleViiteDto() {
    }

    public TekstiKappaleViiteDto(TekstiKappaleDto tekstiKappale) {
        this.tekstiKappale = tekstiKappale;
    }

    @Getter
    @Setter
    public static class Matala extends TekstiKappaleViiteDto {
        private List<Reference> lapset;

        public Matala() {
        }

        public Matala(TekstiKappaleDto tekstiKappale) {
            super(tekstiKappale);
        }
    }

    @Getter
    @Setter
    public static class Puu extends TekstiKappaleViiteDto {
        private List<Puu> lapset;
    }
}
