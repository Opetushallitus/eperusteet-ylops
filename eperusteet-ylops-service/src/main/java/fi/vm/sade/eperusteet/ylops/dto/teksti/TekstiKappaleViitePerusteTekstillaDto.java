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

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author nkala
 */
@Getter
@Setter
public class TekstiKappaleViitePerusteTekstillaDto {
    private Long id;
    @JsonProperty("_tekstiKappale")
    private Reference tekstiKappaleRef;
    private TekstiKappaleKevytDto tekstiKappale;
    private Omistussuhde omistussuhde;
    private boolean pakollinen;
    private boolean valmis;
    private List<TekstiKappaleViitePerusteTekstillaDto> lapset;
    private boolean liite;
    private Long perusteTekstikappaleId;
    private TekstiKappaleDto perusteenTekstikappale;
}
