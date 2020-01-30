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
package fi.vm.sade.eperusteet.ylops.service.teksti.impl;

import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtumaAuditointitiedoilla;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.domain.teksti.PoistettuTekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.PoistettuTekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.teksti.TekstiKappaleService;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

/**
 * @author mikkom
 */
@Service
@Transactional
public class TekstiKappaleServiceImpl implements TekstiKappaleService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private TekstiKappaleRepository repository;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private PoistettuTekstiKappaleRepository poistettuTekstiKappaleRepository;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkaustietoService;

    @Override
    @Transactional(readOnly = true)
    public TekstiKappaleDto get(Long opsId, Long id) {
        TekstiKappale tekstiKappale = repository.findOne(id);
        assertExists(tekstiKappale, "Pyydettyä tekstikappaletta ei ole olemassa");
        return mapper.map(tekstiKappale, TekstiKappaleDto.class);
    }

    @Override
    public TekstiKappaleDto add(Long opsId, TekstiKappaleViite viite, TekstiKappaleDto tekstiKappaleDto) {
        TekstiKappale tekstiKappale = mapper.map(tekstiKappaleDto, TekstiKappale.class);
        tekstiKappale.setTila(Tila.LUONNOS);
        viite.setTekstiKappale(tekstiKappale);
        tekstiKappale = repository.saveAndFlush(tekstiKappale);
        mapper.map(tekstiKappale, tekstiKappaleDto);
        return tekstiKappaleDto;
    }

    @Override
    public TekstiKappaleDto update(Long opsId, TekstiKappaleDto tekstiKappaleDto, MuokkausTapahtuma tapahtuma) {
        Long id = tekstiKappaleDto.getId();
        TekstiKappale current = assertExists(repository.findOne(id), "Tekstikappaletta ei ole olemassa");
        repository.lock(current);
        mapper.map(tekstiKappaleDto, current);
        current.updateMuokkaustiedot();
        TekstiKappale tekstiKappale = repository.save(current);
        if (tapahtuma != null) {
            muokkaustietoService.addOpsMuokkausTieto(opsId, tekstiKappale, tapahtuma);
        }
        return mapper.map(tekstiKappale, TekstiKappaleDto.class);
    }

    @Override
    public TekstiKappaleDto mergeNew(Long opsId, TekstiKappaleViite viite, TekstiKappaleDto tekstiKappaleDto) {
        if (viite.getTekstiKappale() == null || viite.getTekstiKappale().getId() == null) {
            throw new IllegalArgumentException("Virheellinen viite");
        }
        Long id = viite.getTekstiKappale().getId();
        TekstiKappale clone = assertExists(repository.findOne(id), "Tekstikappaletta ei ole olemassa").copy();
        mapper.map(tekstiKappaleDto, clone);
        clone = repository.save(clone);

        viite.setTekstiKappale(clone);
        viite.setOmistussuhde(Omistussuhde.OMA);

        mapper.map(clone, tekstiKappaleDto);
        return tekstiKappaleDto;
    }

    @Override
    public void removeTekstiKappaleFromOps(Long opsId, Long id) {
        PoistettuTekstiKappale poistettu = new PoistettuTekstiKappale();
        TekstiKappale tekstiKappale = repository.findOne(id);
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

        poistettu.setOpetussuunnitelma(ops);
        poistettu.setTekstiKappale(tekstiKappale.getId());
        poistettuTekstiKappaleRepository.save(poistettu);
    }

    @Override
    public void delete(Long opsId, Long id) {
        repository.delete(id);
    }
}
