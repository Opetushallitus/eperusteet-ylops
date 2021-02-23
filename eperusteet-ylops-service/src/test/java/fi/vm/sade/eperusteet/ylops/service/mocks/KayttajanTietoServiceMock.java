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
package fi.vm.sade.eperusteet.ylops.service.mocks;

import fi.vm.sade.eperusteet.ylops.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanProjektitiedotDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

/**
 * @author mikkom
 */
@Service
public class KayttajanTietoServiceMock implements KayttajanTietoService {
    @Override
    public KayttajanTietoDto hae(String oid) {
        return null;
    }

    @Override
    public String haeKayttajanimi(String oid) {
        return null;
    }

    @Override
    public Future<KayttajanTietoDto> haeAsync(String oid) {
        return new AsyncResult<>(hae(oid));
    }

    @Override
    public KayttajanTietoDto haeKirjautaunutKayttaja() {
        return hae(null);
    }

    @Override
    public List<KayttajanProjektitiedotDto> haeOpetussuunnitelmat(String oid) {
        return Collections.emptyList();
    }

    @Override
    public KayttajanProjektitiedotDto haeOpetussuunnitelma(String oid, Long opsId) {
        return null;
    }

    @Override
    public Set<String> haeOrganisaatioOikeudet() {
        return null;
    }

    @Override
    public EtusivuDto haeKayttajanEtusivu() {
        return null;
    }

    @Override
    public List<KayttajanTietoDto> haeKayttajatiedot(List<String> oid) {
        return null;
    }

}
