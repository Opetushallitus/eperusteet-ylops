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
package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteMatalaDto;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.LocalizedMessagesService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.YleisetOsuudetService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiBase;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.addHeader;
import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.addLokalisoituteksti;
import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.getTextString;

/**
 * @author isaul
 */
@Slf4j
@Service
public class YleisetOsuudetServiceImpl implements YleisetOsuudetService {

    @Autowired
    private LocalizedMessagesService messages;

    @Autowired
    private Lops2019Service lopsService;

    public void addYleisetOsuudet(DokumenttiBase docBase) {
        Optional.ofNullable(docBase.getOps().getTekstit())
                .ifPresent(tekstit -> {
                    if (Objects.equals(docBase.getOps().getKoulutustyyppi(), KoulutusTyyppi.LUKIOKOULUTUS)) {
                        addTekstiKappale(docBase, tekstit, false);
                    } else {
                        addTekstiKappale(docBase, tekstit, true);
                    }
                });
    }

    private void addTekstiKappale(DokumenttiBase docBase, TekstiKappaleViite viite, boolean paataso) {
        addTekstiKappale(docBase, viite, paataso, false);
    }

    private void addTekstiKappale(DokumenttiBase docBase, TekstiKappaleViite viite, boolean paataso, boolean liite) {
        for (TekstiKappaleViite lapsi : viite.getLapset()) {
            if (lapsi != null && lapsi.getTekstiKappale() != null) {

                if ((liite != (lapsi.isLiite() ||  (lapsi.getTekstiKappale() != null
                        && lapsi.getTekstiKappale().getNimi() != null
                        && lapsi.getTekstiKappale().getNimi().getTeksti() != null
                        && lapsi.getTekstiKappale().getNimi().getTeksti().get(docBase.getKieli()) != null
                        && lapsi.getTekstiKappale().getNimi().getTeksti().get(docBase.getKieli())
                        .equals(messages.translate("liitteet", docBase.getKieli())))))) {
                    // Jos on liitteet päätasolla niin siirrytään seuraavaan tekstiin
                    continue;
                }

                // Ei näytetä yhteisen osien Pääkappaleiden otsikoita
                // Opetuksen järjestäminen ja Opetuksen toteuttamisen lähtökohdat
                if (paataso) {
                    addTekstiKappale(docBase, lapsi, false, liite);
                } else {

                    if (lapsi.getTekstiKappale().getNimi() != null) {
                        addHeader(docBase, getTextString(docBase, lapsi.getTekstiKappale().getNimi()));
                    }

                    // Perusteen teksti luvulle jos valittu esittäminen
                    Long pTekstikappaleId = lapsi.getPerusteTekstikappaleId();
                    if (lapsi.isNaytaPerusteenTeksti() && pTekstikappaleId != null) {
                        try {
                            PerusteTekstiKappaleViiteMatalaDto perusteTekstikappale = lopsService
                                    .getPerusteTekstikappale(docBase.getOps().getId(), pTekstikappaleId);

                            if (perusteTekstikappale != null && perusteTekstikappale.getPerusteenOsa() != null) {
                                addLokalisoituteksti(docBase,
                                        perusteTekstikappale.getPerusteenOsa().getTeksti(),
                                        "cite");
                            }

                        } catch (BusinessRuleViolationException | NotExistsException e) {
                            // Ohitetaan. Voi olla toisen tyyppinen ops.
                        }
                    }

                    // Opsin teksti luvulle
                    if (lapsi.getTekstiKappale().getTeksti() != null) {
                        addLokalisoituteksti(docBase, lapsi.getTekstiKappale().getTeksti(), "div");
                    }

                    if (lapsi.getTekstiKappale().getNimi() != null) {
                        docBase.getGenerator().increaseDepth();
                    }

                    // Rekursiivisesti
                    addTekstiKappale(docBase, lapsi, false, liite);

                    if (lapsi.getTekstiKappale().getNimi() != null) {
                        docBase.getGenerator().decreaseDepth();
                        docBase.getGenerator().increaseNumber();
                    }
                }
            }
        }
    }

    public void addLiitteet(DokumenttiBase docBase) {
        if (docBase.getOps().getTekstit() != null) {
            addTekstiKappale(docBase, docBase.getOps().getTekstit(), false, true);
        }
    }
}
