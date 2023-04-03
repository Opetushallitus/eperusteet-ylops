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

import fi.vm.sade.eperusteet.ylops.domain.LaajaalainenosaaminenViite;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Keskeinensisaltoalue;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OpetuksenKeskeinensisaltoalue;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Opetuksentavoite;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaineenvuosiluokka;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaineenvuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsVuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus.Laajaalainenosaaminen;
import fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus.Vuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusopetuksenPerusteenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteKeskeinensisaltoalueDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteLaajaalainenosaaminenDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOpetuksentavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiOsaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteVuosiluokkakokonaisuudenLaajaalainenosaaminenDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.LocalizedMessagesService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.PerusopetusService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiBase;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiRivi;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiTaulukko;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.addHeader;
import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.addLokalisoituteksti;
import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.addTeksti;
import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.addTekstiosa;
import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.cleanHtml;
import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.getTextString;
import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.hasLokalisoituteksti;

/**
 * @author isaul
 */
@Service
public class PerusopetusServiceImpl implements PerusopetusService {

    @Autowired
    private LocalizedMessagesService messages;

    @Autowired
    private DtoMapper mapper;

    @Override
    public void addVuosiluokkakokonaisuudet(DokumenttiBase docBase) {
        Set<OpsVuosiluokkakokonaisuus> opsVlkset = docBase.getOps().getVuosiluokkakokonaisuudet();

        // Haetaan vuosiluokkkakokonaisuudet
        ArrayList<Vuosiluokkakokonaisuus> vlkset = new ArrayList<>();
        for (OpsVuosiluokkakokonaisuus opsVlk : opsVlkset) {
            vlkset.add(opsVlk.getVuosiluokkakokonaisuus());
        }

        // Järjestetään aakkosjärjestykseen
        vlkset = vlkset.stream()
                .sorted(Comparator.comparing(vlk2 -> vlk2.getNimi().getTeksti().get(docBase.getKieli())))
                .collect(Collectors.toCollection(ArrayList::new));

        vlkset.forEach(vlk -> {
            String teksti = getTextString(docBase, vlk.getNimi());
            addHeader(docBase, !teksti.isEmpty() ? teksti : "Vuosiluokkakokonaisuuden otsikko puuttuu");

            PerusopetuksenPerusteenSisaltoDto poPerusteenSisaltoDto = docBase.getPerusteDto().getPerusopetus();
            if (poPerusteenSisaltoDto != null && vlk.getTunniste().getId() != null) {
                Optional<PerusteVuosiluokkakokonaisuusDto> optPerusteVlkDto =
                        poPerusteenSisaltoDto.getVuosiluokkakokonaisuudet(vlk.getTunniste().getId());

                if (optPerusteVlkDto.isPresent()) {

                    PerusteVuosiluokkakokonaisuusDto perusteVlk = optPerusteVlkDto.get();

                    // Vuosiluokkan sisältö
                    docBase.getGenerator().increaseDepth();


                    // Vuosiluokkakokonaisuuden kohdat

                    addVlkYleisetOsiot(docBase, perusteVlk.getSiirtymaEdellisesta(), vlk.getSiirtymaEdellisesta());

                    addVlkYleisetOsiot(docBase, perusteVlk.getTehtava(), vlk.getTehtava());

                    addVlkYleisetOsiot(docBase, perusteVlk.getSiirtymaSeuraavaan(), vlk.getSiirtymaSeuraavaan());

                    addVlkYleisetOsiot(docBase, perusteVlk.getLaajaalainenOsaaminen(), vlk.getLaajaalainenOsaaminen());

                    addVlkLaajaalaisetOsaamisenAlueet(docBase, perusteVlk, vlk);

                    addOppiaineet(docBase, vlk);

                    docBase.getGenerator().decreaseDepth();

                    docBase.getGenerator().increaseNumber();
                }
            }
        });
    }

    private void addVlkYleisetOsiot(DokumenttiBase docBase,
                                    PerusteTekstiOsaDto perusteTekstiOsaDto,
                                    Tekstiosa tekstiosa) {
        // Otsikko
        if (perusteTekstiOsaDto != null && perusteTekstiOsaDto.getOtsikko() != null) {
            addHeader(docBase,
                    getTextString(docBase, perusteTekstiOsaDto.getOtsikko()));

            // Perusteen teksi
            if (perusteTekstiOsaDto.getTeksti() != null) {
                addLokalisoituteksti(docBase, perusteTekstiOsaDto.getTeksti(), "cite");
            }

            // Opsin teksti
            if (tekstiosa != null && tekstiosa.getTeksti() != null) {
                addLokalisoituteksti(docBase, tekstiosa.getTeksti(), "div");
            }

            docBase.getGenerator().increaseNumber();
        }
    }

    private void addVlkLaajaalaisetOsaamisenAlueet(DokumenttiBase docBase,
                                                   PerusteVuosiluokkakokonaisuusDto perusteVlk,
                                                   Vuosiluokkakokonaisuus vlk) {
        if (perusteVlk.getLaajaalaisetOsaamiset() != null) {

            addHeader(docBase, messages.translate("laaja-alaisen-osaamisen-alueet", docBase.getKieli()));


            List<PerusteVuosiluokkakokonaisuudenLaajaalainenosaaminenDto> perusteLaajaalaisetOsaamiset = perusteVlk.getLaajaalaisetOsaamiset().stream()
                    .filter((lao -> lao.getLaajaalainenOsaaminen() != null))
                    .sorted(Comparator.comparing(lao -> lao.getLaajaalainenOsaaminen().getNimi().getTekstit().get(docBase.getKieli())))
                    .collect(Collectors.toCollection(ArrayList::new));

            for (PerusteVuosiluokkakokonaisuudenLaajaalainenosaaminenDto perusteLaajaalainenosaaminen : perusteLaajaalaisetOsaamiset) {
                PerusteLaajaalainenosaaminenDto perusteLaajaalainenosaaminenDto = perusteLaajaalainenosaaminen.getLaajaalainenOsaaminen();

                if (perusteLaajaalainenosaaminenDto != null) {
                    docBase.getGenerator().increaseDepth();
                    docBase.getGenerator().increaseDepth();
                    docBase.getGenerator().increaseDepth();
                    docBase.getGenerator().increaseDepth();

                    // otsikko
                    addHeader(docBase, getTextString(docBase, perusteLaajaalainenosaaminenDto.getNimi()));

                    // Perusteen osa
                    addLokalisoituteksti(docBase, perusteLaajaalainenosaaminen.getKuvaus(), "cite");

                    // Opsin osa
                    if (perusteLaajaalainenosaaminen.getLaajaalainenOsaaminen() != null
                            && perusteLaajaalainenosaaminen.getLaajaalainenOsaaminen().getTunniste() != null) {
                        Optional<Laajaalainenosaaminen> optLaajaalainenosaaminen = vlk.getLaajaalaisetosaamiset().stream()
                                .filter((l -> l.getLaajaalainenosaaminen().getViite().equals(
                                        perusteLaajaalainenosaaminenDto.getTunniste().toString())))
                                .findFirst();

                        optLaajaalainenosaaminen.ifPresent(laajaalainenosaaminen ->
                                addLokalisoituteksti(docBase, laajaalainenosaaminen.getKuvaus(), "div"));
                    }

                    docBase.getGenerator().decreaseDepth();
                    docBase.getGenerator().decreaseDepth();
                    docBase.getGenerator().decreaseDepth();
                    docBase.getGenerator().decreaseDepth();
                }
            }

            docBase.getGenerator().increaseNumber();
        }
    }

    private void addOppiaineet(DokumenttiBase docBase, Vuosiluokkakokonaisuus vlk) {
        if (docBase.getOps() != null && docBase.getOps().getOppiaineet() != null) {
            addHeader(docBase, messages.translate("oppiaineet", docBase.getKieli()));

            Set<OpsOppiaine> oppiaineet = docBase.getOps().getOppiaineet();

            List<OpsOppiaine> oppiaineetAsc = oppiaineet.stream()
                    .filter(oa -> oa.getOppiaine() != null
                            && oa.getOppiaine().getNimi() != null
                            && oa.getOppiaine().getNimi().getTeksti() != null
                            && oa.getOppiaine().getNimi().getTeksti().get(docBase.getKieli()) != null)
                    .sorted((oa1, oa2) -> {
                        Integer jarj1 = oa1.getJnro() != null ? oa1.getJnro() : 1;
                        Integer jarj2 = oa2.getJnro() != null ? oa2.getJnro() : 1;
                        return jarj1.compareTo(jarj2);
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

            docBase.getGenerator().increaseDepth();

            // Oppiaineet akkosjärjestyksessä
            for (OpsOppiaine opsOppiaine : oppiaineetAsc) {
                Oppiaine oppiaine = opsOppiaine.getOppiaine();

                Set<Oppiaineenvuosiluokkakokonaisuus> oaVlkset = oppiaine.getVuosiluokkakokonaisuudet();

                UUID tunniste = oppiaine.getTunniste();

                PerusteOppiaineDto perusteOppiaineDto = null;
                PerusteOppiaineenVuosiluokkakokonaisuusDto perusteOaVlkDto = null;
                Oppiaineenvuosiluokkakokonaisuus oaVlk = null;
                OppiaineenVuosiluokkakokonaisuusDto oaPohjanVlk = new OppiaineenVuosiluokkakokonaisuusDto();

                Optional<Oppiaineenvuosiluokkakokonaisuus> optOaVlk = oaVlkset.stream()
                        .filter(o -> o.getVuosiluokkakokonaisuus().getId() == vlk.getTunniste().getId())
                        .findFirst();

                if (oppiaine.getPohjanOppiaine() != null) {
                    oaPohjanVlk = oppiaine.getPohjanOppiaine()
                            .getVuosiluokkakokonaisuus(vlk.getTunniste().getId())
                            .map(ovlk -> mapper.map(ovlk, OppiaineenVuosiluokkakokonaisuusDto.class))
                            .orElse(new OppiaineenVuosiluokkakokonaisuusDto());
                }

                if (optOaVlk.isPresent()) {
                    oaVlk = optOaVlk.get();
                    Optional<PerusteOppiaineDto> optPerusteOppiaineDto = docBase.getPerusteDto().getPerusopetus().getOppiaine(tunniste);
                    if (optPerusteOppiaineDto.isPresent()) {
                        perusteOppiaineDto = optPerusteOppiaineDto.get();
                        Optional<PerusteOppiaineenVuosiluokkakokonaisuusDto> optPerusteOaVlkDto =
                                perusteOppiaineDto.getVuosiluokkakokonaisuus(oaVlk.getVuosiluokkakokonaisuus().getId());
                        if (optPerusteOaVlkDto.isPresent()) {
                            perusteOaVlkDto = optPerusteOaVlkDto.get();
                        }
                    }
                }

                // Oppiaine nimi
                if (oppiaine.isKoosteinen() || optOaVlk.isPresent()) {
                    addHeader(docBase, getTextString(docBase, oppiaine.getNimi()), false);

                    docBase.getGenerator().increaseDepth();
                    docBase.getGenerator().increaseDepth();


                    // Tehtävä
                    addOppiaineTehtava(docBase, oppiaine, perusteOppiaineDto);

                    // Oppiaineen vuosiluokkakokonaiuuden kohtaiset
                    addOppiaineVuosiluokkkakokonaisuus(docBase, perusteOaVlkDto, oaVlk, oaPohjanVlk);

                    docBase.getGenerator().decreaseDepth();

                    // Oppimäärät
                    Set<Oppiaine> oppimaarat = oppiaine.getOppimaarat();
                    if (oppimaarat != null) {

                        Set<PerusteOppiaineDto> perusteOppimaarat = null;
                        if (perusteOppiaineDto != null) {
                            perusteOppimaarat = perusteOppiaineDto.getOppimaarat();
                        }

                        addOppimaarat(docBase, perusteOppimaarat, oppimaarat, vlk);
                    }

                    docBase.getGenerator().decreaseDepth();

                    docBase.getGenerator().increaseNumber();
                }
            }
            docBase.getGenerator().decreaseDepth();

            docBase.getGenerator().increaseNumber();
        }
    }

    private void addOppiaineTehtava(DokumenttiBase docBase, Oppiaine oppiaine, PerusteOppiaineDto perusteOppiaineDto) {
        if (perusteOppiaineDto != null) {
            PerusteTekstiOsaDto tehtava = perusteOppiaineDto.getTehtava();
            if (tehtava != null) {
                addHeader(docBase, getTextString(docBase, tehtava.getOtsikko()));
                addLokalisoituteksti(docBase, tehtava.getTeksti(), "cite");
            }
        }

        if (oppiaine.getPohjanOppiaine() != null) {
            addTekstiosa(docBase, oppiaine.getPohjanOppiaine().getTehtava(), "div");
        }

        addTekstiosa(docBase, oppiaine.getTehtava(), "div");
    }

    private void addOppiaineVuosiluokkkakokonaisuus(DokumenttiBase docBase,
                                                    PerusteOppiaineenVuosiluokkakokonaisuusDto perusteOaVlkDto,
                                                    Oppiaineenvuosiluokkakokonaisuus oaVlkDto,
                                                    OppiaineenVuosiluokkakokonaisuusDto pohjanVlkDto) {

        if (oaVlkDto == null) {
            return;
        }

        if (perusteOaVlkDto != null) {
            addOppiaineYleisetOsiot(docBase, oaVlkDto.getTehtava(), pohjanVlkDto.getTehtava(), perusteOaVlkDto.getTehtava());
            addOppiaineYleisetOsiot(docBase, oaVlkDto.getYleistavoitteet(), pohjanVlkDto.getYleistavoitteet(), null);
            addOppiaineYleisetOsiot(docBase, oaVlkDto.getTyotavat(), pohjanVlkDto.getTyotavat(), perusteOaVlkDto.getTyotavat());
            addOppiaineYleisetOsiot(docBase, oaVlkDto.getOhjaus(), pohjanVlkDto.getOhjaus(), perusteOaVlkDto.getOhjaus());
            addOppiaineYleisetOsiot(docBase, oaVlkDto.getArviointi(), pohjanVlkDto.getArviointi(), perusteOaVlkDto.getArviointi());
            addTavoitteetJaSisaltoalueet(docBase, perusteOaVlkDto, oaVlkDto);
        } else {
            addOppiaineYleisetOsiot(docBase, oaVlkDto.getTehtava(), pohjanVlkDto.getTehtava(), null);
            addOppiaineYleisetOsiot(docBase, oaVlkDto.getYleistavoitteet(), pohjanVlkDto.getYleistavoitteet(), null);
            addOppiaineYleisetOsiot(docBase, oaVlkDto.getTyotavat(), pohjanVlkDto.getTyotavat(), null);
            addOppiaineYleisetOsiot(docBase, oaVlkDto.getOhjaus(), pohjanVlkDto.getOhjaus(), null);
            addOppiaineYleisetOsiot(docBase, oaVlkDto.getArviointi(), pohjanVlkDto.getArviointi(), null);
            addTavoitteetJaSisaltoalueet(docBase, null, oaVlkDto);
        }
    }

    private void addTavoitteetJaSisaltoalueet(DokumenttiBase docBase,
                                              PerusteOppiaineenVuosiluokkakokonaisuusDto perusteOaVlkDto,
                                              Oppiaineenvuosiluokkakokonaisuus oaVlkDto) {

        // Tavoitteet vuosiluokittain
        if (oaVlkDto.getVuosiluokat() != null) {
            ArrayList<Oppiaineenvuosiluokka> vuosiluokat = oaVlkDto.getVuosiluokat().stream()
                    .sorted(Comparator.comparing(el -> el.getVuosiluokka().toString()))
                    .collect(Collectors.toCollection(ArrayList::new));
            // Vuosiluokka otsikko
            vuosiluokat.stream()
                    .filter(oaVuosiluokka -> oaVuosiluokka.getVuosiluokka() != null)
                    .forEach(oaVuosiluokka -> {
                        // Vuosiluokka otsikko
                        addHeader(docBase, messages.translate(oaVuosiluokka.getVuosiluokka().toString(), docBase.getKieli()));
                        addVuosiluokanTavoitteetJaKeskeisetsisallot(docBase, oaVuosiluokka, perusteOaVlkDto);
                    });
        }
    }

    @Deprecated
    private void addVuosiluokkaSisaltoalueet(DokumenttiBase docBase,
                                             Oppiaineenvuosiluokka oaVuosiluokka,
                                             PerusteOppiaineenVuosiluokkakokonaisuusDto perusteOaVlkDto) {
        if (oaVuosiluokka.getSisaltoalueet() != null && !oaVuosiluokka.getSisaltoalueet().isEmpty()) {
            addHeader(docBase, messages.translate("vuosiluokan-keskeiset-sisaltoalueet", docBase.getKieli()));

            oaVuosiluokka.getSisaltoalueet().stream()
                    .filter(s -> s.getPiilotettu() == null || !s.getPiilotettu())
                    .forEach(ksa -> {
                        docBase.getGenerator().increaseDepth();

                        // Sisältöalue otsikko
                        addHeader(docBase, getTextString(docBase, ksa.getNimi()));

                        // Sisältöalue peruste
                        if (perusteOaVlkDto != null && perusteOaVlkDto.getSisaltoalueet() != null) {
                            Optional<PerusteKeskeinensisaltoalueDto> optPerusteKsa = perusteOaVlkDto.getSisaltoalueet().stream()
                                    .filter(pKsa -> pKsa.getTunniste().equals(ksa.getTunniste()))
                                    .findFirst();

                            optPerusteKsa.ifPresent(perusteKeskeinensisaltoalueDto -> addLokalisoituteksti(docBase,
                                    perusteKeskeinensisaltoalueDto.getKuvaus(), "cite"));
                            // Sisältöalue ops
                            addLokalisoituteksti(docBase, ksa.getKuvaus(), "div");
                        }

                        docBase.getGenerator().decreaseDepth();
                    });
        }
    }

    @Deprecated
    private void addVuosiluokkaTaulukko(DokumenttiBase docBase,
                                        Oppiaineenvuosiluokka oaVuosiluokka,
                                        PerusteOppiaineenVuosiluokkakokonaisuusDto perusteOaVlkDto) {
        // Opetuksen tavoitteet taulukko
        if (oaVuosiluokka.getTavoitteet() != null) {
            DokumenttiTaulukko taulukko = new DokumenttiTaulukko();

            taulukko.addOtsikkoSarake(messages.translate("opetuksen-tavoitteet", docBase.getKieli()));
            taulukko.addOtsikkoSarake(messages.translate("tavoitteisiin-liittyvat-sisaltoalueet", docBase.getKieli()));
            taulukko.addOtsikkoSarake(messages.translate("laaja-alainen-osaaminen", docBase.getKieli()));

            for (Opetuksentavoite opetuksentavoite : oaVuosiluokka.getTavoitteet()) {
                DokumenttiRivi rivi = new DokumenttiRivi();

                // Opsin tavoitetta vastaava perusteen tavoite
                if (perusteOaVlkDto != null) {
                    List<PerusteOpetuksentavoiteDto> perusteTavoitteet = perusteOaVlkDto.getTavoitteet();
                    Optional<PerusteOpetuksentavoiteDto> optPerusteOpetuksentavoiteDto = perusteTavoitteet.stream()
                            .filter((o) -> o.getTunniste().equals(opetuksentavoite.getTunniste()))
                            .findFirst();
                    if (optPerusteOpetuksentavoiteDto.isPresent()) {
                        PerusteOpetuksentavoiteDto perusteOpetuksentavoiteDto = optPerusteOpetuksentavoiteDto.get();

                        if (perusteOpetuksentavoiteDto.getTavoite() != null
                                && perusteOpetuksentavoiteDto.getTavoite().get(docBase.getKieli()) != null) {
                            String tavoite = cleanHtml(perusteOpetuksentavoiteDto.getTavoite().get(docBase.getKieli()));
                            if (tavoite.indexOf(" ") != -1) {
                                rivi.addSarake(tavoite.substring(0, tavoite.indexOf(" ")));
                            } else {
                                rivi.addSarake(tavoite);
                            }
                        }

                        // Tavoitteisiin liittyvät sisltöalueet
                        Set<OpetuksenKeskeinensisaltoalue> sisaltoalueet = opetuksentavoite.getSisaltoalueet();

                        if (sisaltoalueet != null) {
                            List<OpetuksenKeskeinensisaltoalue> sisaltoalueetAsc = sisaltoalueet.stream()
                                    .filter(s -> s.getSisaltoalueet() != null
                                            && (s.getSisaltoalueet().getPiilotettu() == null
                                            || !s.getSisaltoalueet().getPiilotettu()))
                                    .sorted(Comparator.comparing(s -> s.getSisaltoalueet()
                                            .getNimi().getTeksti().get(docBase.getKieli())))
                                    .collect(Collectors.toCollection(ArrayList::new));

                            StringBuilder sisaltoalueetBuilder = new StringBuilder();

                            for (OpetuksenKeskeinensisaltoalue opetuksenKeskeinensisaltoalue : sisaltoalueetAsc) {
                                Keskeinensisaltoalue keskeinensisaltoalue
                                        = opetuksenKeskeinensisaltoalue.getSisaltoalueet();
                                if (keskeinensisaltoalue != null) {
                                    if (keskeinensisaltoalue.getNimi() != null
                                            && keskeinensisaltoalue.getNimi().getTeksti() != null
                                            && keskeinensisaltoalue.getNimi().getTeksti().containsKey(docBase.getKieli())) {
                                        String nimi = keskeinensisaltoalue.getNimi().getTeksti().get(docBase.getKieli());
                                        if (nimi.contains(" ")) {
                                            sisaltoalueetBuilder.append(nimi.substring(0, nimi.indexOf(" ")));
                                            sisaltoalueetBuilder.append(", ");
                                        }
                                    }
                                }
                            }
                            String sisaltoalueetString = sisaltoalueetBuilder.toString().replaceAll(", $", "");
                            rivi.addSarake(sisaltoalueetString);
                        }

                        // Laaja-alainen osaaminen
                        Set<LaajaalainenosaaminenViite> laajaalainenosaamisenViitteet = opetuksentavoite.getLaajattavoitteet();
                        if (laajaalainenosaamisenViitteet != null) {
                            StringBuilder laajaalainenOsaaminenBuilder = new StringBuilder();
                            List<String> laajaalaisetLista = new ArrayList<>();

                            for (LaajaalainenosaaminenViite laajaalainenosaaminenViite : laajaalainenosaamisenViitteet) {
                                String viite = laajaalainenosaaminenViite.getViite();
                                if (viite != null) {
                                    // Haetaan laaja-alainen
                                    PerusopetuksenPerusteenSisaltoDto perusopetusPerusteSisaltoDto
                                            = docBase.getPerusteDto().getPerusopetus();
                                    Optional<PerusteVuosiluokkakokonaisuusDto> perusteVlk = perusopetusPerusteSisaltoDto
                                            .getVuosiluokkakokonaisuudet(
                                                    oaVuosiluokka.getKokonaisuus().getVuosiluokkakokonaisuus().getId());

                                    if (perusteVlk.isPresent()) {
                                        if (perusteVlk.get().getLaajaalaisetOsaamiset() != null) {
                                            Optional<PerusteVuosiluokkakokonaisuudenLaajaalainenosaaminenDto> optPerusteLao
                                                    = perusteVlk.get().getLaajaalaisetOsaamiset().stream()
                                                    .filter(Objects::nonNull)
                                                    .filter(lao -> lao.getLaajaalainenOsaaminen() != null
                                                            || lao.getLaajaalainenOsaaminen().getTunniste() != null)
                                                    .filter(lao -> lao.getLaajaalainenOsaaminen().getTunniste().toString()
                                                            .equals(laajaalainenosaaminenViite.getViite()))
                                                    .findFirst();

                                            if (optPerusteLao.isPresent()) {
                                                PerusteVuosiluokkakokonaisuudenLaajaalainenosaaminenDto perusteLao = optPerusteLao.get();
                                                if (perusteLao.getLaajaalainenOsaaminen() != null
                                                        || perusteLao.getLaajaalainenOsaaminen().getNimi() != null
                                                        || perusteLao.getLaajaalainenOsaaminen()
                                                        .getNimi().get(docBase.getKieli()) != null) {

                                                    String nimi = perusteLao.getLaajaalainenOsaaminen()
                                                            .getNimi().get(docBase.getKieli());

                                                    if (nimi.contains(" ")) {
                                                        laajaalaisetLista.add(nimi.substring(0, nimi.indexOf(" ")));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // Laaja-alaiset aakkosjärjestykseen
                            laajaalaisetLista.stream()
                                    .sorted(String::compareTo)
                                    .forEach(el -> {
                                        laajaalainenOsaaminenBuilder.append(el);
                                        laajaalainenOsaaminenBuilder.append(", ");
                                    });
                            rivi.addSarake(laajaalainenOsaaminenBuilder.toString().replaceAll(", $", ""));
                        }

                        taulukko.addRivi(rivi);
                    }
                }
            }

            taulukko.addToDokumentti(docBase);
        }
    }

    private void addVuosiluokanTavoitteetJaKeskeisetsisallot(DokumenttiBase docBase,
                                                             Oppiaineenvuosiluokka oaVuosiluokka,
                                                             PerusteOppiaineenVuosiluokkakokonaisuusDto perusteOaVlkDto) {
        if (oaVuosiluokka.getTavoitteet() != null && !oaVuosiluokka.getTavoitteet().isEmpty()) {

            addTeksti(docBase, messages.translate("docgen.vuosiluokan-tavoitteet-ja-keskeiset-sisallot", docBase.getKieli()), "tavoitteet-otsikko");

            for (Opetuksentavoite opetuksentavoite : oaVuosiluokka.getTavoitteet()) {

                // Opsin tavoitetta vastaava perusteen tavoite
                PerusteOpetuksentavoiteDto perusteOpetuksentavoiteDto = null;
                if (perusteOaVlkDto != null) {
                    List<PerusteOpetuksentavoiteDto> perusteTavoitteet = perusteOaVlkDto.getTavoitteet();
                    Optional<PerusteOpetuksentavoiteDto> optPerusteOpetuksentavoiteDto = perusteTavoitteet.stream()
                            .filter((o) -> o.getTunniste().equals(opetuksentavoite.getTunniste()))
                            .findFirst();
                    if (optPerusteOpetuksentavoiteDto.isPresent()) {
                        perusteOpetuksentavoiteDto = optPerusteOpetuksentavoiteDto.get();
                    }
                }

                // Tavoitteen otsikko
                if (perusteOpetuksentavoiteDto != null) {
                    addLokalisoituteksti(docBase, perusteOpetuksentavoiteDto.getTavoite(), "h5");

                    addLokalisoituteksti(docBase, opetuksentavoite.getTavoite(), "div");

                    // Tavoitteen arviointi
                    DokumenttiTaulukko taulukko = new DokumenttiTaulukko();
                    taulukko.addOtsikko(messages.translate("arviointi-vuosiluokan-paatteeksi", docBase.getKieli()));

                    if (perusteOpetuksentavoiteDto.getArvioinninkohteet().size() == 1
                            && perusteOpetuksentavoiteDto.getArvioinninkohteet().stream().findFirst().get().getHyvanOsaamisenKuvaus() != null) {

                        taulukko.addOtsikkoSarake(messages.translate("arvioinnin-kohde", docBase.getKieli()));
                        taulukko.addOtsikkoSarake(messages.translate("arvion-hyva-osaaminen", docBase.getKieli()));

                        perusteOpetuksentavoiteDto.getArvioinninkohteet().forEach(perusteenTavoitteenArviointi -> {
                            DokumenttiRivi rivi = new DokumenttiRivi();
                            String kohde = "";
                            if (perusteenTavoitteenArviointi.getArvioinninKohde() != null
                                    && perusteenTavoitteenArviointi.getArvioinninKohde().get(docBase.getKieli()) != null) {
                                kohde = cleanHtml(perusteenTavoitteenArviointi.getArvioinninKohde().get(docBase.getKieli()));
                            }
                            rivi.addSarake(kohde);
                            String kuvaus = "";
                            if (perusteenTavoitteenArviointi.getHyvanOsaamisenKuvaus() != null
                                    && perusteenTavoitteenArviointi.getHyvanOsaamisenKuvaus().get(docBase.getKieli()) != null) {
                                kuvaus = cleanHtml(perusteenTavoitteenArviointi.getHyvanOsaamisenKuvaus().get(docBase.getKieli()));
                            }
                            rivi.addSarake(kuvaus);
                            taulukko.addRivi(rivi);
                        });
                    } else {
                        taulukko.addOtsikkoSarake(messages.translate("osaamisen-kuvaus", docBase.getKieli()));
                        taulukko.addOtsikkoSarake(messages.translate("arvion-kuvaus", docBase.getKieli()));

                        perusteOpetuksentavoiteDto.getArvioinninkohteet()
                                .stream().sorted(Comparator.comparing(arv -> arv.getArvosana() != null ? arv.getArvosana() : 0))
                                .forEach(perusteenTavoitteenArviointi -> {
                                    DokumenttiRivi rivi = new DokumenttiRivi();
                                    String kohde = "";
                                    if (perusteenTavoitteenArviointi.getArvosana() != null) {
                                        kohde = messages.translate("osaamisen-kuvaus-arvosanalle-" + perusteenTavoitteenArviointi.getArvosana(), docBase.getKieli());
                                    }
                                    rivi.addSarake(kohde);

                                    String kuvaus = "";
                                    if (perusteenTavoitteenArviointi.getOsaamisenKuvaus() != null
                                            && perusteenTavoitteenArviointi.getOsaamisenKuvaus().get(docBase.getKieli()) != null) {
                                        kuvaus = cleanHtml(perusteenTavoitteenArviointi.getOsaamisenKuvaus().get(docBase.getKieli()));
                                    }
                                    rivi.addSarake(kuvaus);
                                    taulukko.addRivi(rivi);
                                });
                    }

                    taulukko.addToDokumentti(docBase);
                } else {
                    addLokalisoituteksti(docBase, opetuksentavoite.getTavoite(), "h5");
                }

                if (perusteOaVlkDto != null && perusteOaVlkDto.getSisaltoalueet() != null) {
                    perusteOaVlkDto.getSisaltoalueet()
                            .stream()
                            .filter(perusteenKeskeinenSisaltoalue -> {
                                Optional<OpetuksenKeskeinensisaltoalue> opetuksenKeskeinenSisaltoalue = opetuksentavoite.getSisaltoalueet()
                                        .stream()
                                        .filter(sisaltoalue -> sisaltoalue.getSisaltoalueet().getTunniste().equals(perusteenKeskeinenSisaltoalue.getTunniste()))
                                        .findFirst();
                                Optional<Keskeinensisaltoalue> keskeinenSisaltoalue = oaVuosiluokka.getSisaltoalueet()
                                        .stream()
                                        .filter(sisaltoalue -> sisaltoalue.getTunniste().equals(perusteenKeskeinenSisaltoalue.getTunniste()))
                                        .sorted(Comparator.comparing(Keskeinensisaltoalue::getId))
                                        .findFirst();
                                return opetuksenKeskeinenSisaltoalue.isPresent()
                                        && keskeinenSisaltoalue.isPresent()
                                        && (keskeinenSisaltoalue.get().getPiilotettu() == null || !keskeinenSisaltoalue.get().getPiilotettu());
                            })
                            .sorted(Comparator.comparing(perusteenKeskeinenSisaltoalue -> perusteenKeskeinenSisaltoalue.getNimi().get(docBase.getKieli())))
                            .forEach(perusteenKeskeinenSisaltoalue -> {

                                addLokalisoituteksti(docBase, perusteenKeskeinenSisaltoalue.getNimi(), "h6");
                                addLokalisoituteksti(docBase, perusteenKeskeinenSisaltoalue.getKuvaus(), "cite");

                                Optional<OpetuksenKeskeinensisaltoalue> opetuksenKeskeinenSisaltoalue = opetuksentavoite.getSisaltoalueet()
                                        .stream()
                                        .filter(sisaltoalue -> sisaltoalue.getSisaltoalueet().getTunniste().equals(perusteenKeskeinenSisaltoalue.getTunniste()))
                                        .sorted(Comparator.comparing(OpetuksenKeskeinensisaltoalue::getId))
                                        .findFirst();

                                if (opetuksenKeskeinenSisaltoalue.isPresent()) {
                                    OpetuksenKeskeinensisaltoalue sisaltoalue = opetuksenKeskeinenSisaltoalue.get();
                                    if (hasLokalisoituteksti(docBase, sisaltoalue.getOmaKuvaus()) || hasLokalisoituteksti(docBase, sisaltoalue.getSisaltoalueet().getKuvaus())) {
                                        addTeksti(docBase, messages.translate("paikallinen-tarkennus", docBase.getKieli()), "h6");

                                        if (hasLokalisoituteksti(docBase, sisaltoalue.getOmaKuvaus())) {
                                            addLokalisoituteksti(docBase, sisaltoalue.getOmaKuvaus(), "div");
                                        } else if (hasLokalisoituteksti(docBase, sisaltoalue.getSisaltoalueet().getKuvaus())) {
                                            addLokalisoituteksti(docBase, sisaltoalue.getSisaltoalueet().getKuvaus(), "div");
                                        }
                                    }

                                }
                            });
                }
            }
        }
    }

    @Deprecated
    private void addVuosiluokkaTavoitteet(DokumenttiBase docBase,
                                          Oppiaineenvuosiluokka oaVuosiluokka,
                                          PerusteOppiaineenVuosiluokkakokonaisuusDto perusteOaVlkDto) {
        if (oaVuosiluokka.getTavoitteet() != null && !oaVuosiluokka.getTavoitteet().isEmpty()) {

            if (perusteOaVlkDto != null && perusteOaVlkDto.getOpetuksenTavoitteetOtsikko() != null) {
                addLokalisoituteksti(docBase, perusteOaVlkDto.getOpetuksenTavoitteetOtsikko(), "tavoitteet-otsikko");
            } else {
                addTeksti(docBase, messages.translate("vuosiluokan-tavoitteet", docBase.getKieli()), "tavoitteet-otsikko");
            }

            for (Opetuksentavoite opetuksentavoite : oaVuosiluokka.getTavoitteet()) {

                // Opsin tavoitetta vastaava perusteen tavoite
                PerusteOpetuksentavoiteDto perusteOpetuksentavoiteDto = null;
                if (perusteOaVlkDto != null) {
                    List<PerusteOpetuksentavoiteDto> perusteTavoitteet = perusteOaVlkDto.getTavoitteet();
                    Optional<PerusteOpetuksentavoiteDto> optPerusteOpetuksentavoiteDto = perusteTavoitteet.stream()
                            .filter((o) -> o.getTunniste().equals(opetuksentavoite.getTunniste()))
                            .findFirst();
                    if (optPerusteOpetuksentavoiteDto.isPresent()) {
                        perusteOpetuksentavoiteDto = optPerusteOpetuksentavoiteDto.get();
                    }
                }

                // Tavoitteen otsikko
                if (perusteOpetuksentavoiteDto != null) {
                    addLokalisoituteksti(docBase, perusteOpetuksentavoiteDto.getTavoite(), "h5");

                    addLokalisoituteksti(docBase, opetuksentavoite.getTavoite(), "div");

                    // Tavoitteen arviointi
                    DokumenttiTaulukko taulukko = new DokumenttiTaulukko();
                    taulukko.addOtsikko(messages.translate("arviointi-vuosiluokan-paatteeksi", docBase.getKieli()));

                    if (perusteOpetuksentavoiteDto.getArvioinninkohteet().size() == 1
                            && perusteOpetuksentavoiteDto.getArvioinninkohteet().stream().findFirst().get().getHyvanOsaamisenKuvaus() != null) {

                        taulukko.addOtsikkoSarake(messages.translate("arvioinnin-kohde", docBase.getKieli()));
                        taulukko.addOtsikkoSarake(messages.translate("arvion-hyva-osaaminen", docBase.getKieli()));

                        perusteOpetuksentavoiteDto.getArvioinninkohteet().forEach(perusteenTavoitteenArviointi -> {
                            DokumenttiRivi rivi = new DokumenttiRivi();
                            String kohde = "";
                            if (perusteenTavoitteenArviointi.getArvioinninKohde() != null
                                    && perusteenTavoitteenArviointi.getArvioinninKohde().get(docBase.getKieli()) != null) {
                                kohde = cleanHtml(perusteenTavoitteenArviointi.getArvioinninKohde().get(docBase.getKieli()));
                            }
                            rivi.addSarake(kohde);
                            String kuvaus = "";
                            if (perusteenTavoitteenArviointi.getHyvanOsaamisenKuvaus() != null
                                    && perusteenTavoitteenArviointi.getHyvanOsaamisenKuvaus().get(docBase.getKieli()) != null) {
                                kuvaus = cleanHtml(perusteenTavoitteenArviointi.getHyvanOsaamisenKuvaus().get(docBase.getKieli()));
                            }
                            rivi.addSarake(kuvaus);
                            taulukko.addRivi(rivi);
                        });
                    } else {
                        taulukko.addOtsikkoSarake(messages.translate("osaamisen-kuvaus", docBase.getKieli()));
                        taulukko.addOtsikkoSarake(messages.translate("arvion-kuvaus", docBase.getKieli()));

                        perusteOpetuksentavoiteDto.getArvioinninkohteet()
                                .forEach(perusteenTavoitteenArviointi -> {
                                    DokumenttiRivi rivi = new DokumenttiRivi();
                                    String kohde = "";
                                    if (perusteenTavoitteenArviointi.getArvosana() != null) {
                                        kohde = messages.translate("osaamisen-kuvaus-arvosanalle-" + perusteenTavoitteenArviointi.getArvosana(), docBase.getKieli());
                                    }
                                    rivi.addSarake(kohde);

                                    String kuvaus = "";
                                    if (perusteenTavoitteenArviointi.getOsaamisenKuvaus() != null
                                            && perusteenTavoitteenArviointi.getOsaamisenKuvaus().get(docBase.getKieli()) != null) {
                                        kuvaus = cleanHtml(perusteenTavoitteenArviointi.getOsaamisenKuvaus().get(docBase.getKieli()));
                                    }
                                    rivi.addSarake(kuvaus);
                                    taulukko.addRivi(rivi);
                                });
                    }

                    taulukko.addToDokumentti(docBase);
                } else {
                    addLokalisoituteksti(docBase, opetuksentavoite.getTavoite(), "h5");
                }

                // Tavoitteen sisaltoalueet
                addVuosiluokkaTavoitteenSisaltoalueet(docBase, opetuksentavoite);

                // Sisältöalue ops
                opetuksentavoite.getSisaltoalueet()
                        .stream()
                        .sorted(Comparator.comparing(sisaltoAlue -> sisaltoAlue.getSisaltoalueet().getNimi().getTeksti().get(docBase.getKieli())))
                        .forEach(sisaltoAlue -> addLokalisoituteksti(
                                docBase, sisaltoAlue.getSisaltoalueet().getKuvaus(), "div"));
            }
        }
    }

    private void addVuosiluokkaTavoitteenSisaltoalueet(DokumenttiBase docBase,
                                                       Opetuksentavoite opetuksentavoite) {

        Set<OpetuksenKeskeinensisaltoalue> sisaltoalueet = opetuksentavoite.getSisaltoalueet();
        List<OpetuksenKeskeinensisaltoalue> sisaltoalueetAsc = sisaltoalueet.stream()
                .filter(Objects::nonNull)
                .filter(s -> s.getSisaltoalueet() != null
                        && (s.getSisaltoalueet().getPiilotettu() == null
                        || !s.getSisaltoalueet().getPiilotettu()))
                .sorted(Comparator.comparing(s1 -> s1.getSisaltoalueet().getNimi().getTeksti().get(docBase.getKieli())))
                .collect(Collectors.toCollection(ArrayList::new));

        if (sisaltoalueetAsc.size() > 0) {

            sisaltoalueetAsc.forEach(s -> {
                if (s.getSisaltoalueet() != null) {

                    // Tavoitteen sisältöalue
                    if (s.getOmaKuvaus() != null) {
                        addLokalisoituteksti(docBase, s.getSisaltoalueet().getNimi(), "h6");
                        addLokalisoituteksti(docBase, s.getOmaKuvaus(), "div");
                    }

                }
            });
        }
    }

    private void addOppiaineYleisetOsiot(DokumenttiBase docBase, Tekstiosa tekstiosa, TekstiosaDto pohjanTekstiosa, PerusteTekstiOsaDto perusteTekstiOsaDto) {
        if (tekstiosa != null) {
            LokalisoituTeksti otsikko = tekstiosa.getOtsikko();
            if (otsikko != null) {
                addHeader(docBase, getTextString(docBase, otsikko));
            } else if (perusteTekstiOsaDto != null) {
                addHeader(docBase, getTextString(docBase, perusteTekstiOsaDto.getOtsikko()));
                addLokalisoituteksti(docBase, perusteTekstiOsaDto.getTeksti(), "cite");
            }

            if (pohjanTekstiosa != null && pohjanTekstiosa.getTeksti() != null && pohjanTekstiosa.getTeksti() != null) {
                addLokalisoituteksti(docBase, pohjanTekstiosa.getTeksti(), "div");
            }

            addLokalisoituteksti(docBase, tekstiosa.getTeksti(), "div");
        }
    }

    private void addOppimaarat(DokumenttiBase docBase, Set<PerusteOppiaineDto> perusteOppimaarat,
                               Set<Oppiaine> oppimaarat, Vuosiluokkakokonaisuus vlk) {
        if (oppimaarat != null) {
            for (Oppiaine oppimaara : oppimaarat) {
                PerusteOppiaineDto perusteOppiaineDto = null;
                if (perusteOppimaarat != null) {
                    Optional<PerusteOppiaineDto> optPerusteOppimaara = perusteOppimaarat.stream()
                            .filter(perusteOppiaine -> perusteOppiaine.getTunniste().equals(oppimaara.getTunniste()))
                            .findFirst();

                    if (optPerusteOppimaara.isPresent()) {
                        perusteOppiaineDto = optPerusteOppimaara.get();
                    }
                }

                // Jos on koosteinen oppimäärä ja oppimäärälle ei löydy perustetta
                // perusteen oppiaineesta, käytetään opsin perusteen oppiainetta
                if (oppimaara.getOppiaine().isKoosteinen() && perusteOppiaineDto == null) {
                    Optional<PerusteOppiaineDto> optPerusteOppiaineDto = docBase.getPerusteDto().getPerusopetus()
                            .getOppiaine(oppimaara.getTunniste());
                    if (optPerusteOppiaineDto.isPresent()) {
                        perusteOppiaineDto = optPerusteOppiaineDto.get();
                    }
                }

                addOppimaara(docBase, perusteOppiaineDto, oppimaara, vlk);
            }
        }
    }

    private void addOppimaara(DokumenttiBase docBase, PerusteOppiaineDto perusteOppiaineDto,
                              Oppiaine oppiaine, Vuosiluokkakokonaisuus vlk) {
        Optional<Oppiaineenvuosiluokkakokonaisuus> optOaVlk
                = oppiaine.getVuosiluokkakokonaisuus(vlk.getTunniste().getId());
        OppiaineenVuosiluokkakokonaisuusDto oaPohjanVlk = new OppiaineenVuosiluokkakokonaisuusDto();
        OpsVuosiluokkakokonaisuus opsVuosiluokkakokonaisuus = docBase.getOps().getVuosiluokkakokonaisuudet().stream()
                .filter(opsv -> opsv.getVuosiluokkakokonaisuus().getTunniste().getId().equals(vlk.getTunniste().getId())).findFirst().orElse(null);

        if (oppiaine.getPohjanOppiaine() != null) {
            oaPohjanVlk = oppiaine.getPohjanOppiaine()
                    .getVuosiluokkakokonaisuus(vlk.getTunniste().getId())
                    .map(ovlk -> mapper.map(ovlk, OppiaineenVuosiluokkakokonaisuusDto.class))
                    .orElse(new OppiaineenVuosiluokkakokonaisuusDto());
        }

        if (!optOaVlk.isPresent()) {
            return;
        }

        if (optOaVlk.get().getPiilotettu() != null && optOaVlk.get().getPiilotettu()) {
            return;
        }

        if (opsVuosiluokkakokonaisuus != null
                && opsVuosiluokkakokonaisuus.getLisatieto() != null
                && opsVuosiluokkakokonaisuus.getLisatieto().getPiilotetutOppiaineet().contains(oppiaine.getId())) {
            return;
        }

        // Oppimäärä otsikko
        addHeader(docBase, getTextString(docBase, oppiaine.getNimi()));

        docBase.getGenerator().increaseDepth();

        PerusteTekstiOsaDto perusteTehtavaDto = null;
        if (perusteOppiaineDto != null) {
            perusteTehtavaDto = perusteOppiaineDto.getTehtava();
        }

        // Tehtävä
        addOppiaineYleisetOsiot(
                docBase,
                oppiaine.getTehtava(),
                oppiaine.getPohjanOppiaine() != null ? mapper.map(oppiaine.getPohjanOppiaine().getTehtava(), TekstiosaDto.class) : null,
                perusteTehtavaDto);

        // Peruste
        PerusteOppiaineenVuosiluokkakokonaisuusDto perusteOaVlkDto = null;

        if (perusteOppiaineDto != null) {
            Optional<PerusteOppiaineenVuosiluokkakokonaisuusDto> optPerusteOaVlkDto
                    = perusteOppiaineDto.getVuosiluokkakokonaisuus(vlk.getTunniste().getId());
            if (optPerusteOaVlkDto.isPresent()) {
                perusteOaVlkDto = optPerusteOaVlkDto.get();
            }
        }

        // Oppimäärän vuosiluokkakokonaiuuden kohtaiset
        addOppiaineVuosiluokkkakokonaisuus(docBase, perusteOaVlkDto, optOaVlk.get(), oaPohjanVlk);

        docBase.getGenerator().decreaseDepth();
        docBase.getGenerator().increaseNumber();
    }
}
