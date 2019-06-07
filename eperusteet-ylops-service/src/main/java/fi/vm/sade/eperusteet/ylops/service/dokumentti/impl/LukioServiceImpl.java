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

import fi.vm.sade.eperusteet.ylops.domain.lukio.*;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOpetussuunnitelmaRakenneOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineRakenneListausDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.OppiaineJarjestysDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiOsaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.*;
import fi.vm.sade.eperusteet.ylops.repository.ops.LukioOppiaineJarjestysRepository;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.LocalizedMessagesService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.LukioService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiBase;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioOpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.*;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * @author isaul
 */
@Service
public class LukioServiceImpl implements LukioService {

    @Autowired
    private LocalizedMessagesService messages;

    @Autowired
    private LukioOpetussuunnitelmaService lukioOpetussuunnitelmaService;

    @Autowired
    private LukioOppiaineJarjestysRepository jarjestysRepository;

    public void addOppimistavoitteetJaOpetuksenKeskeisetSisallot(DokumenttiBase docBase) throws ParserConfigurationException, SAXException, IOException {
        addHeader(docBase, messages.translate("oppimistavoitteet-ja-opetuksen-keskeiset-sisallot", docBase.getKieli()));
        docBase.getGenerator().increaseDepth();

        addOpetuksenYleisetTavoitteet(docBase);
        addAihekokonaisuudet(docBase);
        addOppiaineet(docBase);

        docBase.getGenerator().decreaseDepth();
    }

    private void addOpetuksenYleisetTavoitteet(DokumenttiBase docBase) {
        OpetuksenYleisetTavoitteet yleisetTavoitteet = docBase.getOps().getOpetuksenYleisetTavoitteet();
        LukiokoulutuksenPerusteenSisaltoDto lukiokoulutus = docBase.getPerusteDto().getLukiokoulutus();
        if (lukiokoulutus == null) {
            return;
        }

        OpetuksenYleisetTavoitteetDto perusteYleisetTavoitteet = lukiokoulutus.getOpetuksenYleisetTavoitteet();

        if (perusteYleisetTavoitteet == null) {
            return;
        }

        addHeader(docBase, getTextString(docBase, perusteYleisetTavoitteet.getOtsikko()));
        addLokalisoituteksti(docBase, perusteYleisetTavoitteet.getKuvaus(), "cite");
        if (yleisetTavoitteet != null) {
            addLokalisoituteksti(docBase, yleisetTavoitteet.getKuvaus(), "div");
        }

        docBase.getGenerator().increaseNumber();
    }

    private void addAihekokonaisuudet(DokumenttiBase docBase) {
        Aihekokonaisuudet aihekokonaisuudet = docBase.getOps().getAihekokonaisuudet();
        LukiokoulutuksenPerusteenSisaltoDto lukiokoulutus = docBase.getPerusteDto().getLukiokoulutus();
        if (lukiokoulutus == null) {
            return;
        }

        AihekokonaisuudetDto perusteAihekokonaisuudet = lukiokoulutus.getAihekokonaisuudet();
        if (aihekokonaisuudet == null || perusteAihekokonaisuudet == null) {
            return;
        }

        addHeader(docBase, getTextString(docBase, perusteAihekokonaisuudet.getOtsikko()));
        addLokalisoituteksti(docBase, perusteAihekokonaisuudet.getYleiskuvaus(), "cite");
        addLokalisoituteksti(docBase, aihekokonaisuudet.getYleiskuvaus(), "div");

        Set<Aihekokonaisuus> aihekokonaisuudetLista = aihekokonaisuudet.getAihekokonaisuudet();
        List<AihekokonaisuusDto> perusteAihekokonaisuudetLista = perusteAihekokonaisuudet.getAihekokonaisuudet();
        if (aihekokonaisuudetLista != null) {
            aihekokonaisuudetLista.stream()
                    .map(o -> {
                        if (o.getJnro() == null) {
                            o.setJnro(Long.MAX_VALUE);
                        }
                        return o;
                    })
                    .sorted((o1, o2) -> Long.compare(o1.getJnro(), o2.getJnro()))
                    .forEach(aihekokonaisuus -> {
                        AihekokonaisuusDto perusteAihekokokonaisuusDto = null;
                        Optional<AihekokonaisuusDto> optPerusteAihekokokonaisuus = perusteAihekokonaisuudetLista.stream()
                                .filter(dto -> dto.getTunniste().equals(aihekokonaisuus.getTunniste()))
                                .findAny();
                        if (optPerusteAihekokokonaisuus.isPresent()) {
                            perusteAihekokokonaisuusDto = optPerusteAihekokokonaisuus.get();
                        }
                        addAihekokonaisuus(docBase, aihekokonaisuus, perusteAihekokokonaisuusDto);
                    });
        }

        docBase.getGenerator().increaseNumber();
    }

    private void addAihekokonaisuus(DokumenttiBase docBase, Aihekokonaisuus aihekokonaisuus, AihekokonaisuusDto perusteAihekokonaisuusDto) {
        // Näytetään opsin otsikko jos saatavilla, muuten käytetään perusteen otsikkoa.
        // Jos kumpaakaan ei ole, näytetään tieto puuttuvasta otsikosta.
        if (aihekokonaisuus.getOtsikko() != null) {
            addLokalisoituteksti(docBase, aihekokonaisuus.getOtsikko(), "h6");
        } else if (perusteAihekokonaisuusDto != null) {
            addLokalisoituteksti(docBase, perusteAihekokonaisuusDto.getOtsikko(), "h6");
        } else {
            addTeksti(docBase, "Aihekokonaisuuden otsikko puuttuu", "h6");
        }

        if (perusteAihekokonaisuusDto != null) {
            addLokalisoituteksti(docBase, perusteAihekokonaisuusDto.getYleiskuvaus(), "cite");
        }
        addLokalisoituteksti(docBase, aihekokonaisuus.getYleiskuvaus(), "div");
    }

    private void addOppiaineet(DokumenttiBase docBase) {
        LukioOpetussuunnitelmaRakenneOpsDto lukioRakenne = lukioOpetussuunnitelmaService.getRakenne(docBase.getOps().getId());

        LukiokoulutuksenPerusteenSisaltoDto lukiokoulutus = docBase.getPerusteDto().getLukiokoulutus();
        if (lukiokoulutus == null) {
            return;
        }

        LukioOpetussuunnitelmaRakenneDto perusteRakenne = lukiokoulutus.getRakenne();
        if (perusteRakenne == null || lukioRakenne == null) {
            return;
        }

        // Rakenteen mukaisesti oppiaineet
        lukioRakenne.getOppiaineet().forEach(oaRakenne -> docBase.getOps().getOppiaineet().stream()
                .map(OpsOppiaine::getOppiaine)
                .filter(oa -> oa.getTunniste().equals(oaRakenne.getTunniste()))
                .findAny()
                .ifPresent(oa -> {
                    LukioPerusteOppiaineDto perusteOppiaineDto = null;
                    Optional<LukioPerusteOppiaineDto> optPerusteOppiaine
                            = perusteRakenne.getOppiaineet().stream()
                            .filter(perusteOa -> perusteOa.getTunniste().equals(oa.getTunniste()))
                            .findAny();
                    if (optPerusteOppiaine.isPresent()) {
                        perusteOppiaineDto = optPerusteOppiaine.get();
                    }
                    addOppiaine(docBase, oa, perusteOppiaineDto, oaRakenne);
                }));
    }

    private void addOppiaine(DokumenttiBase docBase, Oppiaine oppiaine, LukioPerusteOppiaineDto perusteOppiaine, LukioOppiaineRakenneListausDto oaRakenne) {
        // Oppiaineen nimi
        addHeader(docBase, getTextString(docBase, oppiaine.getNimi()));

        if (perusteOppiaine != null) {
            addLokalisoituteksti(docBase, perusteOppiaine.getPakollinenKurssiKuvaus(), "cite");
            addLokalisoituteksti(docBase, oppiaine.getValtakunnallinenPakollinenKuvaus(), "div");

            addLokalisoituteksti(docBase, perusteOppiaine.getSyventavaKurssiKuvaus(), "cite");
            addLokalisoituteksti(docBase, oppiaine.getValtakunnallinenSyventavaKurssiKuvaus(), "div");

            addLokalisoituteksti(docBase, perusteOppiaine.getSoveltavaKurssiKuvaus(), "cite");
            addLokalisoituteksti(docBase, oppiaine.getValtakunnallinenSoveltavaKurssiKuvaus(), "div");

            addLokalisoituteksti(docBase, oppiaine.getPaikallinenSyventavaKurssiKuvaus(), "div");

            addLokalisoituteksti(docBase, oppiaine.getPaikallinenSoveltavaKurssiKuvaus(), "div");

            addYleinenKuvaus(docBase, oppiaine.getTehtava(), perusteOppiaine.getTehtava());
            addYleinenKuvaus(docBase, oppiaine.getTavoitteet(), perusteOppiaine.getTavoitteet());
            addYleinenKuvaus(docBase, oppiaine.getArviointi(), perusteOppiaine.getArviointi());
        } else {
            addLokalisoituteksti(docBase, oppiaine.getValtakunnallinenPakollinenKuvaus(), "div");
            addLokalisoituteksti(docBase, oppiaine.getValtakunnallinenSyventavaKurssiKuvaus(), "div");
            addLokalisoituteksti(docBase, oppiaine.getValtakunnallinenSoveltavaKurssiKuvaus(), "div");
            addLokalisoituteksti(docBase, oppiaine.getPaikallinenSyventavaKurssiKuvaus(), "div");
            addLokalisoituteksti(docBase, oppiaine.getPaikallinenSoveltavaKurssiKuvaus(), "div");

            addYleinenKuvaus(docBase, oppiaine.getTehtava(), null);
            addYleinenKuvaus(docBase, oppiaine.getTavoitteet(), null);
            addYleinenKuvaus(docBase, oppiaine.getArviointi(), null);
        }

        docBase.getGenerator().increaseDepth();

        // Oppimäärät
        if (oppiaine.getOppimaarat() != null) {
            Map<Long, OppiaineJarjestysDto> jarjestykset = jarjestysRepository
                    .findJarjestysDtosByOpetussuunnitelmaId(docBase.getOps().getId(),
                            oppiaine.maarineen().map(Oppiaine::getId).collect(toSet())).stream()
                    .collect(toMap(OppiaineJarjestysDto::getId, o -> o));

            oppiaine.getOppimaarat().stream()
                    .sorted(compareOppiaineet(jarjestys(jarjestykset)))
                    .forEach(om -> {
                        LukioPerusteOppiaineDto perusteOm = null;

                        if (perusteOppiaine != null && perusteOppiaine.getOppimaarat() != null) {
                            Optional<LukioPerusteOppiaineDto> optPerusteOm = perusteOppiaine.getOppimaarat().stream()
                                    .filter(dto -> dto.getTunniste().equals(om.getTunniste()))
                                    .findAny();

                            if (optPerusteOm.isPresent()) {
                                perusteOm = optPerusteOm.get();
                            }
                        }

                        addOppiaine(docBase, om, perusteOm, oaRakenne);
                    });
        }

        // Valtakunnallinen pakolliset
        addKurssitByTyyppi(docBase, oppiaine, perusteOppiaine, LukiokurssiTyyppi.VALTAKUNNALLINEN_PAKOLLINEN);

        // Valtakunnalliset syventävät
        addKurssitByTyyppi(docBase, oppiaine, perusteOppiaine, LukiokurssiTyyppi.VALTAKUNNALLINEN_SYVENTAVA);

        // Valtakunnalliset soveltavat
        addKurssitByTyyppi(docBase, oppiaine, perusteOppiaine, LukiokurssiTyyppi.VALTAKUNNALLINEN_SOVELTAVA);

        // Paikalliset syventävät
        addKurssitByTyyppi(docBase, oppiaine, perusteOppiaine, LukiokurssiTyyppi.PAIKALLINEN_SYVENTAVA);

        // Paikalliset soveltavat
        addKurssitByTyyppi(docBase, oppiaine, perusteOppiaine, LukiokurssiTyyppi.PAIKALLINEN_SOVELTAVA);

        docBase.getGenerator().decreaseDepth();

        docBase.getGenerator().increaseNumber();
    }

    private void addKurssitByTyyppi(DokumenttiBase docBase, Oppiaine oppiaine, LukioPerusteOppiaineDto perusteOppiaine, LukiokurssiTyyppi tyyppi) {
        Set<LukiokurssiPerusteDto> perusteKurssit = perusteOppiaine != null ? perusteOppiaine.getKurssit() : null;
        List<OppiaineLukiokurssi> kurssit = docBase.getOps().lukiokurssitByOppiaine().apply(oppiaine.getId());

        if (kurssit.stream().filter(kurssi -> kurssi.getKurssi().getTyyppi().equals(tyyppi)).count() > 0) {
            addTeksti(docBase, messages.translate(tyyppi.toString(), docBase.getKieli()), "h6");
        }

        kurssit.stream()
                .filter(kurssi -> kurssi.getKurssi().getTyyppi().equals(tyyppi))
                .forEach(kurssi -> {
                    LukiokurssiPerusteDto perusteKurssi = null;
                    if (perusteKurssit != null) {
                        Optional<LukiokurssiPerusteDto> optPerusteKurssi = perusteKurssit.stream()
                                .filter(pKurssi -> pKurssi.getTunniste().equals(kurssi.getKurssi().getTunniste()))
                                .findFirst();
                        if (optPerusteKurssi.isPresent()) {
                            perusteKurssi = optPerusteKurssi.get();
                        }
                    }
                    addKurssi(docBase, kurssi, perusteKurssi);
                });
    }

    private void addKurssi(DokumenttiBase docBase, OppiaineLukiokurssi oppiaineLukiokurssi, LukiokurssiPerusteDto perusteKurssi) {
        if (oppiaineLukiokurssi.getKurssi() == null) {
            return;
        }

        Lukiokurssi lukiokurssi = oppiaineLukiokurssi.getKurssi();

        // Kurssin otsikko
        StringBuilder otsikko = new StringBuilder();
        if (oppiaineLukiokurssi.getJarjestys() != null) {
            otsikko.append(oppiaineLukiokurssi.getJarjestys());
            otsikko.append(". ");
        }
        otsikko.append(getTextString(docBase, lukiokurssi.getNimi()));
        if (lukiokurssi.getLokalisoituKoodi() != null) {
            otsikko.append(" (");
            otsikko.append(getTextString(docBase, lukiokurssi.getLokalisoituKoodi()));
            otsikko.append(")");
        }
        addTeksti(docBase, otsikko.toString(), "h5");

        // Kuvaus
        if (perusteKurssi != null) {
            addLokalisoituteksti(docBase, perusteKurssi.getKuvaus(), "cite");
        }
        addLokalisoituteksti(docBase, lukiokurssi.getKuvaus(), "div");

        if (perusteKurssi != null) {
            addYleinenKuvaus(docBase, lukiokurssi.getTavoitteet(), perusteKurssi.getTavoitteet());
            addYleinenKuvaus(docBase, lukiokurssi.getKeskeinenSisalto(), perusteKurssi.getKeskeisetSisallot());
            addYleinenKuvaus(docBase, lukiokurssi.getTavoitteetJaKeskeinenSisalto(), perusteKurssi.getTavoitteetJaKeskeisetSisallot());
        } else {
            addYleinenKuvaus(docBase, lukiokurssi.getTavoitteet(), null);
            addYleinenKuvaus(docBase, lukiokurssi.getKeskeinenSisalto(), null);
            addYleinenKuvaus(docBase, lukiokurssi.getTavoitteetJaKeskeinenSisalto(), null);
        }

        docBase.getGenerator().increaseNumber();
    }

    private void addYleinenKuvaus(DokumenttiBase docBase, Tekstiosa tekstiosa, PerusteTekstiOsaDto perusteTekstiosa) {
        if (tekstiosa != null && tekstiosa.getOtsikko() != null) {
            addLokalisoituteksti(docBase, tekstiosa.getOtsikko(), "h6");
        } else if (perusteTekstiosa != null && perusteTekstiosa.getOtsikko() != null) {
            addLokalisoituteksti(docBase, perusteTekstiosa.getOtsikko(), "h6");
        }

        if (perusteTekstiosa != null) {
            addLokalisoituteksti(docBase, perusteTekstiosa.getTeksti(), "cite");
        }

        if (tekstiosa != null) {
            addLokalisoituteksti(docBase, tekstiosa.getTeksti(), "div");
        }
    }

    private Function<Long, Integer> jarjestys(Map<Long, OppiaineJarjestysDto> jarjestykset) {
        return LambdaUtil.map(jarjestykset, OppiaineJarjestysDto::getJarjestys);
    }

    private Comparator<Oppiaine> compareOppiaineet(Function<Long, Integer> jarjestys) {
        return comparing((Oppiaine oa) -> ofNullable(jarjestys.apply(oa.getId())).orElse(Integer.MAX_VALUE))
                .thenComparing(comparing((Oppiaine oa) -> {
                    if (oa == null || oa.getNimi() == null) {
                        return "";
                    }
                    return oa.getNimi().firstByKieliOrder().orElse("");
                }));
    }

}
