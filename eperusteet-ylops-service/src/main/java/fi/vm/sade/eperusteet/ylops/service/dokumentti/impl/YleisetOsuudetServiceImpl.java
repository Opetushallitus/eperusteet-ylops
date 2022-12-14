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
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.LocalizedMessagesService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.YleisetOsuudetService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiBase;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private TekstiKappaleViiteService tekstiKappaleViiteService;

    public void addYleisetOsuudet(DokumenttiBase docBase) {
        Optional.ofNullable(docBase.getOps().getTekstit())
                .ifPresent(tekstit -> {
                    addTekstiKappale(docBase, tekstit, opetussuunnitelmaVanhaaRakennetta(docBase));
                });
    }

    // EP-2745 - tekstikappaleen päätason näkyvyys vain "uudemmille" opetussuunnitelmille
    private boolean opetussuunnitelmaVanhaaRakennetta(DokumenttiBase docBase) {

        if (KoulutusTyyppi.PERUSOPETUS.equals(docBase.getOps().getKoulutustyyppi())) {
            return docBase.getOps().getTekstit().getLapset().stream().noneMatch(tekstiKappaleViite -> tekstiKappaleViite.getPerusteTekstikappaleId() != null);
        }

        return false;
    }

    private void addTekstiKappale(DokumenttiBase docBase, TekstiKappaleViite viite, boolean paataso) {
        addTekstiKappale(docBase, viite, paataso, false);
    }

    private void addTekstiKappale(DokumenttiBase docBase, TekstiKappaleViite viite, boolean paataso, boolean liite) {
        for (TekstiKappaleViite lapsi : viite.getLapset()) {
            if (lapsi != null && lapsi.getTekstiKappale() != null && !lapsi.isPiilotettu()) {

                if (liite != isLiite(lapsi, docBase)) {
                    continue;
                }

                // Ei näytetä yhteisen osien Pääkappaleiden otsikoita
                // Opetuksen järjestäminen ja Opetuksen toteuttamisen lähtökohdat
                if (paataso) {
                    addTekstiKappale(docBase, lapsi, false, liite);
                } else {

                    if (hasTekstiSisalto(docBase, lapsi) || hasTekstiSisaltoRecursive(docBase, lapsi)) {

                        TekstiKappaleDto perusteenTekstikappale = null;
                        if (lapsi.getPerusteTekstikappaleId() != null) {
                            perusteenTekstikappale = opetussuunnitelmaService.getPerusteTekstikappale(docBase.getOps().getId(), lapsi.getPerusteTekstikappaleId());
                            if (perusteenTekstikappale != null) {
                                addHeader(docBase, getTextString(docBase, perusteenTekstikappale.getNimi()));
                            }
                        }

                        if(perusteenTekstikappale == null && lapsi.getTekstiKappale().getNimi() != null)  {
                            addHeader(docBase, getTextString(docBase, lapsi.getTekstiKappale().getNimi()));

                        }

                        if (!opetussuunnitelmaVanhaaRakennetta(docBase)) {

                            // Perusteen teksti luvulle jos valittu esittäminen
                            if (lapsi.isNaytaPerusteenTeksti() && perusteenTekstikappale != null) {
                                addLokalisoituteksti(docBase, perusteenTekstikappale.getTeksti(),"cite");
                            }

                            if (lapsi.isNaytaPohjanTeksti()) {
                                List<TekstiKappaleViiteDto.Matala> pohjaTekstit = tekstiKappaleViiteService.getTekstiKappaleViiteOriginals(docBase.getOps().getId(), lapsi.getId());
                                pohjaTekstit.stream()
                                        .filter(pohjaTeksti -> pohjaTeksti != null && pohjaTeksti.getTekstiKappale() != null && pohjaTeksti.getTekstiKappale().getTeksti() != null)
                                        .forEach(pohjaTeksti -> addLokalisoituteksti(docBase, pohjaTeksti.getTekstiKappale().getTeksti(), "cite"));
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
    }

    private boolean hasTekstiSisaltoRecursive(DokumenttiBase docBase, TekstiKappaleViite tekstiKappaleViite) {
        return CollectionUtil.treeToStream(tekstiKappaleViite, TekstiKappaleViite::getLapset).anyMatch(viite -> hasTekstiSisalto(docBase, viite));
    }

    private boolean hasTekstiSisalto(DokumenttiBase docBase, TekstiKappaleViite viite) {
        Long pTekstikappaleId = viite.getPerusteTekstikappaleId();
        if (viite.isNaytaPerusteenTeksti() && pTekstikappaleId != null) {
            try {
                if (KoulutustyyppiToteutus.LOPS2019.equals(docBase.getOps().getToteutus())) {
                    fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto perusteTekstikappale = lopsService
                            .getPerusteTekstikappale(docBase.getOps().getId(), pTekstikappaleId);

                    if (perusteTekstikappale != null && perusteTekstikappale.getTekstiKappale() != null) {
                        return true;
                    }
                } else {
                    TekstiKappaleDto tekstikappale = opetussuunnitelmaService.getPerusteTekstikappale(docBase.getOps().getId(), pTekstikappaleId);
                    if (tekstikappale != null) {
                        return true;
                    }
                }
            } catch (BusinessRuleViolationException | NotExistsException e) {
            }
        }

        if (viite.isNaytaPohjanTeksti()) {
            List<TekstiKappaleViiteDto.Matala> pohjaTekstit = tekstiKappaleViiteService.getTekstiKappaleViiteOriginals(docBase.getOps().getId(), viite.getId());
            boolean pohjateksti = pohjaTekstit.stream()
                    .anyMatch(pohjaTeksti -> pohjaTeksti != null && pohjaTeksti.getTekstiKappale() != null && pohjaTeksti.getTekstiKappale().getTeksti() != null);

            if (pohjateksti) {
                return true;
            }
        }

        // Opsin teksti luvulle
        if (viite.getTekstiKappale() != null && viite.getTekstiKappale().getTeksti() != null) {
            return true;
        }

        return false;
    }

    public void addLiitteet(DokumenttiBase docBase) {
        if (docBase.getOps().getTekstit() != null) {
            addTekstiKappale(docBase, docBase.getOps().getTekstit(), false, true);
        }
    }

    private boolean isLiite(TekstiKappaleViite viite, DokumenttiBase docBase) {
        return viite.isLiite()
                || (viite.getTekstiKappale() != null
                    && viite.getTekstiKappale().getNimi() != null
                    && viite.getTekstiKappale().getNimi().getTeksti() != null
                    && viite.getTekstiKappale().getNimi().getTeksti().get(docBase.getKieli()) != null
                    && viite.getTekstiKappale().getNimi().getTeksti().get(docBase.getKieli())
                    .equals(messages.translate("liitteet", docBase.getKieli())))
                || (viite.getVanhempi() != null && isLiite(viite.getVanhempi(), docBase));
    }
}
