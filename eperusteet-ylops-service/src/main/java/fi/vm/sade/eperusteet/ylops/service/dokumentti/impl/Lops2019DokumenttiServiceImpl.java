package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.*;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.Lops2019SisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019ArviointiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.*;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019TehtavaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliTavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.LocalizedMessagesService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.Lops2019DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiBase;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Element;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.*;

@Slf4j
@Service
public class Lops2019DokumenttiServiceImpl implements Lops2019DokumenttiService {

    @Autowired
    private LocalizedMessagesService messages;

    @Autowired
    private Lops2019Service lops2019Service;

    @Autowired
    private Lops2019OppiaineService oppiaineService;

    @Autowired
    private Lops2019OpintojaksoService opintojaksoService;

    @Override
    public void addLops2019Sisalto(DokumenttiBase docBase) {
        addOppiaineet(docBase);
    }

    private void addOppiaineet(DokumenttiBase docBase) {
        addHeader(docBase, messages.translate("oppiaineet", docBase.getKieli()));
        docBase.getGenerator().increaseDepth();

        Lops2019SisaltoDto perusteenSisalto = docBase.getPerusteDto().getLops2019();
        if (perusteenSisalto == null) {
            return;
        }

        Opetussuunnitelma ops = docBase.getOps();

        // Opintojaksot
        Map<String, List<Lops2019OpintojaksoDto>> opintojaksotMap = new HashMap<>();
        {
            List<Lops2019OpintojaksoDto> opintojaksot = opintojaksoService.getAll(ops.getId());
            opintojaksot.forEach(oj -> oj.getOppiaineet().stream()
                    .map(Lops2019OpintojaksonOppiaineDto::getKoodi)
                    .forEach(koodi -> {
                        if (!opintojaksotMap.containsKey(koodi)) {
                            opintojaksotMap.put(koodi, new ArrayList<>());
                        }
                        opintojaksotMap.get(koodi).add(oj);
                    })
            );
        }

        // Perusteen oppiaineet
        perusteenSisalto.getOppiaineet().forEach(oa -> {
            KoodiDto koodi = oa.getKoodi();
            addOppiaine(docBase, oa, koodi != null ? opintojaksotMap.get(koodi.getUri()) : null);
        });

        // Paikalliset oppiaineet
        List<Lops2019PaikallinenOppiaineDto> oppiaineet = oppiaineService.getAll(ops.getId());
        oppiaineet.forEach(poa -> addPaikallinenOppiaine(docBase, poa, opintojaksotMap.get(poa.getKoodi())));

        // Integraatio opintojaksot
        addIntegraatioOpintojaksot(docBase);

        docBase.getGenerator().decreaseDepth();
        docBase.getGenerator().increaseNumber();
    }

    private void addOppiaine(
            DokumenttiBase docBase,
            Lops2019OppiaineKaikkiDto oa,
            List<Lops2019OpintojaksoDto> opintojaksot
    ) {
        StringBuilder nimiBuilder = new StringBuilder();
        nimiBuilder.append(getTextString(docBase, oa.getNimi()));
        KoodiDto koodi = oa.getKoodi();
        if (koodi != null && koodi.getArvo() != null) {
            nimiBuilder.append(" (");
            nimiBuilder.append(koodi.getArvo());
            nimiBuilder.append(")");
        }
        addHeader(docBase, nimiBuilder.toString());

        // Tehtävä
        Lops2019TehtavaDto tehtava = oa.getTehtava();
        if (tehtava != null) {
            addTeksti(docBase, messages.translate("oppiaineen-tehtava", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, tehtava.getKuvaus(), "cite");
        }

        // Laaja-alainen osaaminen
        Lops2019OppiaineLaajaAlainenOsaaminenDto laoKokonaisuus = oa.getLaajaAlaisetOsaamiset();
        if (laoKokonaisuus != null) {
            addTeksti(docBase, messages.translate("laaja-alainen-osaaminen", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, laoKokonaisuus.getKuvaus(), "cite");
        }

        // Tavoitteet
        Lops2019OppiaineTavoitteetDto tavoitteet = oa.getTavoitteet();
        if (tavoitteet != null) {
            addTeksti(docBase, messages.translate("tavoitteet", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, tavoitteet.getKuvaus(), "cite");

            List<Lops2019OppiaineTavoitealueDto> tavoitealueet = tavoitteet.getTavoitealueet();
            if (!ObjectUtils.isEmpty(tavoitealueet)) {
                tavoitealueet.forEach(ta -> {
                    addLokalisoituteksti(docBase, ta.getNimi(), "h6");
                    LokalisoituTekstiDto kohde = ta.getKohde();
                    if (kohde != null && !ObjectUtils.isEmpty(ta.getTavoitteet())) {

                        Element kohdeEl = docBase.getDocument().createElement("p");
                        Element kohdeElCite = docBase.getDocument().createElement("cite");
                        kohdeElCite.setTextContent(getTextString(docBase, kohde));
                        kohdeEl.appendChild(kohdeElCite);
                        docBase.getBodyElement().appendChild(kohdeEl);

                        Element ul = docBase.getDocument().createElement("ul");
                        ta.getTavoitteet().forEach(tavoite -> {
                            Element li = docBase.getDocument().createElement("li");
                            Element liCite = docBase.getDocument().createElement("cite");
                            liCite.setTextContent(getTextString(docBase, tavoite));
                            li.appendChild(liCite);
                            ul.appendChild(li);
                        });
                        docBase.getBodyElement().appendChild(ul);
                    }
                });
            }
        }

        // Arviointi
        Lops2019ArviointiDto arviointi = oa.getArviointi();
        if (arviointi != null) {
            addTeksti(docBase, messages.translate("arviointi", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, arviointi.getKuvaus(), "cite");
        }

        // Moduulit?

        // Opintojaksot
        if (!ObjectUtils.isEmpty(opintojaksot)) {
            addTeksti(docBase, messages.translate("opintojaksot", docBase.getKieli()), "h6");
            docBase.getGenerator().increaseDepth();
            opintojaksot.stream()
                    .sorted(Comparator.comparing(Lops2019OpintojaksoDto::getKoodi))
                    .forEach(oj -> addOpintojakso(docBase, oj, oa, null));
            docBase.getGenerator().decreaseDepth();
        }

        // Oppimäärät
        docBase.getGenerator().increaseDepth();
        oa.getOppimaarat().forEach(om -> addOppiaine(docBase, om, opintojaksot));
        docBase.getGenerator().decreaseDepth();
        docBase.getGenerator().increaseNumber();
    }

    private void addPaikallinenOppiaine(
            DokumenttiBase docBase,
            Lops2019PaikallinenOppiaineDto poa,
            List<Lops2019OpintojaksoDto> opintojaksot
    ) {

        // Nimi
        StringBuilder nimiBuilder = new StringBuilder();
        nimiBuilder.append(getTextString(docBase, poa.getNimi()));
        String koodi = poa.getKoodi();
        if (!ObjectUtils.isEmpty(koodi)) {
            nimiBuilder.append(" (");
            nimiBuilder.append(koodi);
            nimiBuilder.append(")");
        }
        addHeader(docBase, nimiBuilder.toString());

        // Perusteen oppiaine
        String perusteenOppiaineUri = poa.getPerusteenOppiaineUri();
        if (perusteenOppiaineUri != null) {
            // Vastaa toista oppiainetta
        }

        // Tehtävä
        fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019TehtavaDto tehtava = poa.getTehtava();
        if (tehtava != null && tehtava.getKuvaus() != null) {
            addTeksti(docBase, messages.translate("oppiaineen-tehtava", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, tehtava.getKuvaus(), "div");
        }

        // Tavoitteet
        Lops2019OppiaineenTavoitteetDto tavoitteet = poa.getTavoitteet();
        if (tavoitteet != null) {
            addTeksti(docBase, messages.translate("tavoitteet", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, tavoitteet.getKuvaus(), "div");

            List<Lops2019OppiaineenTavoitealueDto> tavoitealueet = tavoitteet.getTavoitealueet();
            if (!ObjectUtils.isEmpty(tavoitealueet)) {
                tavoitealueet.forEach(ta -> {
                    addLokalisoituteksti(docBase, ta.getNimi(), "h6");

                    LokalisoituTekstiDto kohde = ta.getKohde();
                    if (kohde != null && !ObjectUtils.isEmpty(ta.getTavoitteet())) {

                        addLokalisoituteksti(docBase, kohde, "p");

                        Element ul = docBase.getDocument().createElement("ul");
                        ta.getTavoitteet().stream()
                                .filter(Objects::nonNull)
                                .map(Lops2019TavoitealueenTavoite::getTavoite).forEach(tavoite -> {
                            Element li = docBase.getDocument().createElement("li");
                            li.setTextContent(getTextString(docBase, tavoite));
                            ul.appendChild(li);
                        });
                        docBase.getBodyElement().appendChild(ul);
                    }
                });
            }
        }

        // Arviointi
        fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019ArviointiDto arviointi = poa.getArviointi();
        if (arviointi != null && arviointi.getKuvaus() != null) {
            addTeksti(docBase, messages.translate("arviointi", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, arviointi.getKuvaus(), "div");
        }

        // Laaja-alainen osaaminen
        LokalisoituTekstiDto laajaAlainenOsaaminen = poa.getLaajaAlainenOsaaminen();
        if (laajaAlainenOsaaminen != null) {
            addTeksti(docBase, messages.translate("laaja-alainen-osaaminen", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, laajaAlainenOsaaminen, "div");
        }

        if (!ObjectUtils.isEmpty(opintojaksot)) {
            addTeksti(docBase, messages.translate("opintojaksot", docBase.getKieli()), "h6");
            docBase.getGenerator().increaseDepth();
            opintojaksot.stream()
                    .sorted(Comparator.comparing(Lops2019OpintojaksoBaseDto::getKoodi))
                    .forEach(oj -> addOpintojakso(docBase, oj, null, poa));
            docBase.getGenerator().decreaseDepth();
        }

        docBase.getGenerator().increaseNumber();
    }

    private void addOpintojakso(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj,
            Lops2019OppiaineKaikkiDto oa,
            Lops2019PaikallinenOppiaineDto poa
    ) {
        // Ohitetaan jos opintojakso ei kuulu mihinkään oppiaineeseen. Ei mahdollista nykyisellä toteutuksella.
        Set<Lops2019OpintojaksonOppiaineDto> oppiaineet = oj.getOppiaineet();
        if (ObjectUtils.isEmpty(oppiaineet)) {
            return;
        }

        // Otsikko
        boolean isIntegraatioOpintojakso = oa == null && poa == null;
        addOpintojaksonOtsikko(docBase, oj, isIntegraatioOpintojakso);

        if (isIntegraatioOpintojakso) {
            addIntegraatioOpintojakso(docBase, oj);
        } else {
            if (oppiaineet.size() > 1) {
                // Integraatio opintojakso
                addIntegraatioOpintojaksoLinkki(docBase, oj);
            } else {
                // Yhden oppiaineen opintojakso

                // Oppiaine
                List<Lops2019OppiaineKaikkiDto> oaList = new ArrayList<>();
                oaList.add(oa);

                // Paikallinen oppiaine
                List<Lops2019PaikallinenOppiaineDto> poaList = new ArrayList<>();
                poaList.add(poa);

                addYhdenOppiaineenOpintojakso(docBase, oj, oaList, poaList);
            }
        }

        docBase.getGenerator().increaseNumber();
    }

    private void addOpintojaksonOtsikko(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj,
            boolean isIntegraatioOpintojakso
    ) {
        StringBuilder nimiBuilder = new StringBuilder();

        // Nimi
        nimiBuilder.append(getTextString(docBase, oj.getNimi()));

        // Opintopisteet
        Long laajuus = oj.getLaajuus();
        if (laajuus != null) {
            nimiBuilder.append(", ");
            nimiBuilder.append(laajuus.toString());
            nimiBuilder.append(" ");
            nimiBuilder.append(messages.translate("op", docBase.getKieli()));
        }

        // Koodi
        String koodi = oj.getKoodi();
        if (koodi != null) {
            nimiBuilder.append(" (");
            nimiBuilder.append(koodi);
            nimiBuilder.append(")");
        }

        if (isIntegraatioOpintojakso) {
            // Lisätään ankkuri integraatio opintojaksolle
            addHeader(docBase, nimiBuilder.toString(), "opintojakso_" + koodi);
        } else {
            addHeader(docBase, nimiBuilder.toString(), null);
        }

    }

    private void addYhdenOppiaineenOpintojakso(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj,
            List<Lops2019OppiaineKaikkiDto> oppiaineet,
            List<Lops2019PaikallinenOppiaineDto> paikallisetOppiaineet
    ) {
        // Opintojakson moduulit
        List<Lops2019ModuuliDto> moduulitSorted = new ArrayList<>();
        addOpintojaksonModuulit(docBase, oj, oppiaineet, moduulitSorted);

        // Tavoitteet ja paikallinen lisäys
        addTeksti(docBase, messages.translate("tavoitteet", docBase.getKieli()), "h6");
        addOpintojaksonTavoitteet(docBase, moduulitSorted);
        addOpintojaksonTavoitteetPaikallinenLisays(docBase, oj);

        // Keskeiset sisällöt ja paikallinen lisäys
        addTeksti(docBase, messages.translate("keskeiset-sisallot", docBase.getKieli()), "h6");
        addOpintojaksonSisallot(docBase, moduulitSorted);
        addOpintojaksonSisallotPaikallinenLisays(docBase, oj);

        // Laaja-alainen osaaminen ja paikallinen lisäys
        addTeksti(docBase, messages.translate("laaja-alainen-osaaminen", docBase.getKieli()), "h6");
        addOpintojaksonOppiaineenLaajaAlainenOsaaminen(docBase, oppiaineet);
        addOpintojaksonOppiaineenPaikallinenLaajaAlainenOsaaminen(docBase, paikallisetOppiaineet);
        addOpintojaksonLaajaAlainenOsaaminenPaikallinenLisays(docBase, oj);

        // Arviointi
        addTeksti(docBase, messages.translate("opintojakson-arviointi", docBase.getKieli()), "h6");
        addOpintojaksonArviointi(docBase, oppiaineet);
        addOpintojaksonArviointiPaikallinenLisays(docBase, oj);

        // Vapaa kuvaus
        addOpintojaksonVapaaKuvaus(docBase, oj);
    }

    private void addIntegraatioOpintojaksoLinkki(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj
    ) {
        Element div = docBase.getDocument().createElement("div");

        Element a = docBase.getDocument().createElement("a");

        a.setTextContent(messages.translate("integraatio-opintojakso-ohjaus", docBase.getKieli()));
        if (oj.getKoodi() != null) {
            a.setAttribute("href", "#opintojakso_" + oj.getKoodi());
        }

        div.appendChild(a);

        docBase.getBodyElement().appendChild(div);

    }

    private void addIntegraatioOpintojaksot(
            DokumenttiBase docBase
    ) {
        Long id = docBase.getOps().getId();
        List<Lops2019OpintojaksoDto> integrattioOpintojaksot = opintojaksoService.getAll(id).stream()
                .filter(oj -> oj.getOppiaineet() != null)
                .filter(oj -> oj.getOppiaineet().size() > 1)
                .collect(Collectors.toList());

        // Jos integraatio opintojaksoja ei ole, ohitetaan kohta
        if (ObjectUtils.isEmpty(integrattioOpintojaksot)) {
            return;
        }

        addHeader(docBase, messages.translate("integraatio-opintojaksot", docBase.getKieli()));
        docBase.getGenerator().increaseDepth();
        integrattioOpintojaksot.stream()
                .sorted(Comparator.comparing(Lops2019OpintojaksoDto::getKoodi))
                .forEach(oj -> addOpintojakso(docBase, oj, null, null));
        docBase.getGenerator().decreaseDepth();
        docBase.getGenerator().increaseNumber();
    }

    private void addIntegraatioOpintojakso(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj
    ) {
        List<Lops2019OpintojaksonOppiaineDto> oppiaineetSorted = oj.getOppiaineet().stream()
                .sorted(Comparator.comparing(Lops2019OpintojaksonOppiaineDto::getKoodi))
                .collect(Collectors.toList());

        // Haetaan perusteen oppiaineet
        Lops2019SisaltoDto sisaltoDto = docBase.getPerusteDto().getLops2019();
        List<Lops2019OppiaineKaikkiDto> oppiaineetKaikki = sisaltoDto.getOppiaineet();
        List<Lops2019PaikallinenOppiaineDto> poppiaineetKaikki = oppiaineService.getAll(docBase.getOps().getId());

        // Haetaan opintojakson perusteen oppiaineet
        List<Lops2019OppiaineKaikkiDto> oppiaineet = new ArrayList<>();
        oppiaineetSorted.forEach(oaList -> {
            List<Lops2019OppiaineKaikkiDto> oppiaineetFiltered = oppiaineetKaikki.stream()
                    .filter(Objects::nonNull)
                    .filter(oa -> oa.getKoodi() != null && oa.getKoodi().getUri() != null && oaList.getKoodi() != null)
                    .filter(oa -> Objects.equals(oa.getKoodi().getUri(), oaList.getKoodi()))
                    .collect(Collectors.toList());
            oppiaineet.addAll(oppiaineetFiltered);
        });
        // Haetaan opintojakson paikalliset oppiaineet
        List<Lops2019PaikallinenOppiaineDto> paikallisetOppiaineet = new ArrayList<>();
        oppiaineetSorted.forEach(oaList -> {
            List<Lops2019PaikallinenOppiaineDto> poppiaineetFiltered = poppiaineetKaikki.stream()
                    .filter(Objects::nonNull)
                    .filter(poa -> poa.getKoodi() != null && oaList.getKoodi() != null)
                    .filter(oa -> Objects.equals(oa.getKoodi(), oaList.getKoodi()))
                    .collect(Collectors.toList());
            paikallisetOppiaineet.addAll(poppiaineetFiltered);
        });

        // Opintojakson Ooppiaineet
        addOpintojaksonOppiaineet(docBase, oppiaineetSorted, oppiaineet, paikallisetOppiaineet);

        // Opintojakson moduulit
        List<Lops2019ModuuliDto> moduulitSorted = new ArrayList<>();
        addOpintojaksonModuulit(docBase, oj, oppiaineet, moduulitSorted);

        // Tavoitteet ja paikallinen lisäys
        addTeksti(docBase, messages.translate("tavoitteet", docBase.getKieli()), "h6");
        addOpintojaksonTavoitteet(docBase, moduulitSorted);
        addOpintojaksonTavoitteetPaikallinenLisays(docBase, oj);

        // Keskeiset sisällöt ja paikallinen lisäys
        addTeksti(docBase, messages.translate("keskeiset-sisallot", docBase.getKieli()), "h6");
        addOpintojaksonSisallot(docBase, moduulitSorted);
        addOpintojaksonSisallotPaikallinenLisays(docBase, oj);

        // Laaja-alainen osaaminen ja paikallinen lisäys
        addTeksti(docBase, messages.translate("laaja-alainen-osaaminen", docBase.getKieli()), "h6");
        addOpintojaksonOppiaineenLaajaAlainenOsaaminen(docBase, oppiaineet);
        addOpintojaksonOppiaineenPaikallinenLaajaAlainenOsaaminen(docBase, paikallisetOppiaineet);
        addOpintojaksonLaajaAlainenOsaaminenPaikallinenLisays(docBase, oj);

        // Arviointi
        addTeksti(docBase, messages.translate("opintojakson-arviointi", docBase.getKieli()), "h6");
        addOpintojaksonArviointi(docBase, oppiaineet);
        addOpintojaksonArviointiPaikallinenLisays(docBase, oj);

        // Vapaa kuvaus
        addOpintojaksonVapaaKuvaus(docBase, oj);
    }

    private void addOpintojaksonOppiaineet(
            DokumenttiBase docBase,
            List<Lops2019OpintojaksonOppiaineDto> oppiaineetSorted,
            List<Lops2019OppiaineKaikkiDto> oppiaineet,
            List<Lops2019PaikallinenOppiaineDto> paikallisetOppiaineet
    ) {
        addTeksti(docBase, messages.translate("oppiaineet", docBase.getKieli()), "h6");
        Element ul = docBase.getDocument().createElement("ul");
        oppiaineetSorted.forEach(oa -> {
            String koodiUri = oa.getKoodi();
            if (koodiUri != null) {
                Element li = docBase.getDocument().createElement("li");

                // Etsitään nimi perusteen oppiaineista tai paikallisista oppiaineista
                li.setTextContent(Stream.concat(oppiaineet.stream()
                                .map(oppiaine -> Pair.of(oppiaine.getNimi(), oppiaine.getKoodi().getUri())),
                        paikallisetOppiaineet.stream()
                                .map(oppiaine -> Pair.of(oppiaine.getNimi(), oppiaine.getKoodi())))
                        .filter(pair -> Objects.equals(pair.getSecond(), koodiUri))
                        .map(pair -> getTextString(docBase, pair.getFirst()))
                        .findAny()
                        .orElse(koodiUri));

                ul.appendChild(li);
            }
        });
        docBase.getBodyElement().appendChild(ul);
    }

    private void addOpintojaksonModuulit(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj,
            List<Lops2019OppiaineKaikkiDto> oaList,
            List<Lops2019ModuuliDto> moduulitSorted
    ) {
        List<Lops2019OpintojaksonModuuliDto> moduulit = oj.getModuulit();
        if (!ObjectUtils.isEmpty(moduulit)) {
            Element ul = docBase.getDocument().createElement("ul");
            addTeksti(docBase, messages.translate("opintojakson-moduulit", docBase.getKieli()), "h6");
            moduulit.stream()
                    .sorted(Comparator.comparing(Lops2019OpintojaksonModuuliDto::getKoodiUri))
                    .forEach(ojm -> {
                        String koodiUri = ojm.getKoodiUri();
                        if (koodiUri != null && !ObjectUtils.isEmpty(oaList)) {
                            oaList.forEach(oa -> oa.getModuulit().stream()
                                    .filter(m -> koodiUri.equals(m.getKoodi() != null ? m.getKoodi().getUri() : null))
                                    .findAny()
                                    .ifPresent(m -> {
                                        moduulitSorted.add(m);
                                        addModuuli(docBase, m, ul);
                                    }));
                        }
                    });
            docBase.getBodyElement().appendChild(ul);
        }
    }

    private void addOpintojaksonTavoitteet(
            DokumenttiBase docBase,
            List<Lops2019ModuuliDto> moduulitSorted
    ) {
        if (!ObjectUtils.isEmpty(moduulitSorted)) {
            Element cite = docBase.getDocument().createElement("cite");
            moduulitSorted.stream()
                    .filter(Objects::nonNull)
                    .forEach(m -> {

                        // Moduulin nimi
                        Element nimiEl = docBase.getDocument().createElement("p");
                        nimiEl.setTextContent(getTextString(docBase, m.getNimi()));
                        cite.appendChild(nimiEl);

                        Lops2019ModuuliTavoiteDto tavoitteet = m.getTavoitteet();
                        if (!ObjectUtils.isEmpty(tavoitteet)) {

                            // Moduulit tavoitteet
                            LokalisoituTekstiDto kohde = tavoitteet.getKohde();
                            if (kohde != null && !ObjectUtils.isEmpty(tavoitteet.getTavoitteet())) {

                                // Kohde
                                Element kohdeEl = docBase.getDocument().createElement("p");
                                kohdeEl.setTextContent(getTextString(docBase, kohde));
                                cite.appendChild(kohdeEl);

                                // Tavoitteet
                                Element ul = docBase.getDocument().createElement("ul");
                                tavoitteet.getTavoitteet().forEach(t -> {
                                    Element li = docBase.getDocument().createElement("li");
                                    li.setTextContent(getTextString(docBase, t));
                                    ul.appendChild(li);
                                });
                                cite.appendChild(ul);
                            }

                        }
                    });
            docBase.getBodyElement().appendChild(cite);
        }
    }

    private void addOpintojaksonTavoitteetPaikallinenLisays(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj
    ) {
        List<Lops2019OpintojaksonTavoiteDto> tavoitteet = oj.getTavoitteet();
        if (!ObjectUtils.isEmpty(tavoitteet)) {
            addTeksti(docBase, messages.translate("paikallinen-lisays", docBase.getKieli()), "p");
            Element ul = docBase.getDocument().createElement("ul");
            tavoitteet.stream()
                    .filter(Objects::nonNull)
                    .map(Lops2019OpintojaksonTavoiteDto::getKuvaus)
                    .filter(Objects::nonNull)
                    .forEach(kuvaus -> {
                        Element li = docBase.getDocument().createElement("li");
                        li.setTextContent(getTextString(docBase, kuvaus));
                        ul.appendChild(li);
                    });
            docBase.getBodyElement().appendChild(ul);
        }
    }

    private void addOpintojaksonSisallot(
            DokumenttiBase docBase,
            List<Lops2019ModuuliDto> moduulitSorted
    ) {
        if (!ObjectUtils.isEmpty(moduulitSorted)) {
            Element cite = docBase.getDocument().createElement("cite");
            moduulitSorted.stream()
                    .filter(Objects::nonNull)
                    .forEach(m -> {

                        // Moduulin nimi
                        Element nimiEl = docBase.getDocument().createElement("p");
                        nimiEl.setTextContent(getTextString(docBase, m.getNimi()));
                        cite.appendChild(nimiEl);

                        List<Lops2019ModuuliSisaltoDto> sisallot = m.getSisallot();
                        if (!ObjectUtils.isEmpty(sisallot)) {

                            // Moduulin keskeiset sisällöt
                            sisallot.stream()
                                    .filter(Objects::nonNull)
                                    .forEach(sisalto -> {
                                        LokalisoituTekstiDto kohde = sisalto.getKohde();
                                        if (kohde != null && !ObjectUtils.isEmpty(sisalto.getSisallot())) {
                                            // Kohde
                                            Element kohdeEl = docBase.getDocument().createElement("p");
                                            kohdeEl.setTextContent(getTextString(docBase, kohde));
                                            cite.appendChild(kohdeEl);

                                            // Sisallöt
                                            Element ul = docBase.getDocument().createElement("ul");
                                            sisalto.getSisallot().forEach(s -> {
                                                Element li = docBase.getDocument().createElement("li");
                                                li.setTextContent(getTextString(docBase, s));
                                                ul.appendChild(li);
                                            });
                                            cite.appendChild(ul);
                                        }
                                    });
                        }
                    });
            docBase.getBodyElement().appendChild(cite);
        }
    }

    private void addOpintojaksonSisallotPaikallinenLisays(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj
    ) {
        List<Lops2019OpintojaksonKeskeinenSisaltoDto> sisallot = oj.getKeskeisetSisallot();
        if (!ObjectUtils.isEmpty(sisallot)) {
            addTeksti(docBase, messages.translate("paikallinen-lisays", docBase.getKieli()), "p");
            Element ul = docBase.getDocument().createElement("ul");
            sisallot.stream()
                    .filter(Objects::nonNull)
                    .map(Lops2019OpintojaksonKeskeinenSisaltoDto::getKuvaus)
                    .filter(Objects::nonNull)
                    .forEach(kuvaus -> {
                        Element li = docBase.getDocument().createElement("li");
                        li.setTextContent(getTextString(docBase, kuvaus));
                        ul.appendChild(li);
                    });
            docBase.getBodyElement().appendChild(ul);
        }
    }

    private void addOpintojaksonOppiaineenLaajaAlainenOsaaminen(
            DokumenttiBase docBase,
            List<Lops2019OppiaineKaikkiDto> oppiaineet
    ) {
        if (!ObjectUtils.isEmpty(oppiaineet)) {
            oppiaineet.forEach(oa -> {
                if (oa != null) {
                    Lops2019OppiaineLaajaAlainenOsaaminenDto laoKokonaisuus = oa.getLaajaAlaisetOsaamiset();
                    if (laoKokonaisuus != null) {
                        addTeksti(docBase, getTextString(docBase, oa.getNimi()), "h6");
                        addLokalisoituteksti(docBase, laoKokonaisuus.getKuvaus(), "cite");
                    }
                }
            });
        }
    }

    private void addOpintojaksonOppiaineenPaikallinenLaajaAlainenOsaaminen(
            DokumenttiBase docBase,
            List<Lops2019PaikallinenOppiaineDto> poppiaineet
    ) {
        if (!ObjectUtils.isEmpty(poppiaineet)) {
            poppiaineet.forEach(poa -> {
                if (poa != null) {
                    LokalisoituTekstiDto laajaAlainenOsaaminen = poa.getLaajaAlainenOsaaminen();
                    if (laajaAlainenOsaaminen != null) {
                        addTeksti(docBase, getTextString(docBase, poa.getNimi()), "h6");
                        addLokalisoituteksti(docBase, laajaAlainenOsaaminen, "div");
                    }
                }
            });
        }
    }

    private void addOpintojaksonLaajaAlainenOsaaminenPaikallinenLisays(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj
    ) {
        List<Lops2019PaikallinenLaajaAlainenDto> laajaAlainenOsaaminen = oj.getLaajaAlainenOsaaminen();
        if (!ObjectUtils.isEmpty(laajaAlainenOsaaminen)) {
            addTeksti(docBase, messages.translate("paikallinen-lisays", docBase.getKieli()), "p");
            laajaAlainenOsaaminen.forEach(laajaAlainenDto -> lops2019Service
                    .getLaajaAlaisetOsaamiset().getLaajaAlaisetOsaamiset().stream()
                    .filter(lao -> lao.getKoodi() != null
                            && lao.getKoodi().getUri() != null
                            && Objects.equals(lao.getKoodi().getUri(), laajaAlainenDto.getKoodi()))
                    .findAny()
                    .ifPresent(l -> {
                        // Laaja-alaisen osaaminen nimi
                        addLokalisoituteksti(docBase, l.getNimi(), "h6");

                        // Kuvaus
                        LokalisoituTekstiDto kuvaus = laajaAlainenDto.getKuvaus();
                        addLokalisoituteksti(docBase, kuvaus, "div");
                    }));
        }
    }

    private void addOpintojaksonArviointi(
            DokumenttiBase docBase,
            List<Lops2019OppiaineKaikkiDto> oppiaineet
    ) {
        if (!ObjectUtils.isEmpty(oppiaineet)) {
            oppiaineet.forEach(oa -> {
                if (oa != null) {
                    Lops2019ArviointiDto arviointi = oa.getArviointi();
                    if (arviointi != null && arviointi.getKuvaus() != null) {
                        addTeksti(docBase, getTextString(docBase, oa.getNimi()), "h6");
                        LokalisoituTekstiDto kuvaus = arviointi.getKuvaus();
                        addLokalisoituteksti(docBase, kuvaus, "cite");
                    }
                }
            });
        }
    }

    private void addOpintojaksonArviointiPaikallinenLisays(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj
    ) {
        LokalisoituTekstiDto arviointi = oj.getArviointi();
        if (arviointi != null) {
            addTeksti(docBase, messages.translate("paikallinen-lisays", docBase.getKieli()), "p");
            addLokalisoituteksti(docBase, arviointi, "div");
        }
    }


    private void addOpintojaksonVapaaKuvaus(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj
    ) {
        LokalisoituTekstiDto kuvaus = oj.getKuvaus();
        if (kuvaus != null) {
            addTeksti(docBase, messages.translate("opintojakson-vapaa-kuvaus", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, kuvaus, "div");
        }
    }

    private void addModuuli(
            DokumenttiBase docBase,
            Lops2019ModuuliDto m,
            Element ul
    ) {
        StringBuilder stringBuilder = new StringBuilder();

        // Nimi
        stringBuilder.append(getTextString(docBase, m.getNimi()));

        // Opintopisteet
        BigDecimal laajuus = m.getLaajuus();
        if (laajuus != null) {
            stringBuilder.append(", ");
            stringBuilder.append(laajuus.stripTrailingZeros().toPlainString());
            stringBuilder.append(" ");
            stringBuilder.append(messages.translate("op", docBase.getKieli()));
        }

        // Koodi
        if (m.getKoodi() != null && m.getKoodi().getArvo() != null) {
            stringBuilder.append(" (");
            stringBuilder.append(m.getKoodi().getArvo());
            stringBuilder.append(")");
        }

        // Pakollisuus
        if (m.isPakollinen()) {
            stringBuilder.append(", ");
            stringBuilder.append(messages.translate("pakollinen", docBase.getKieli()));
        } else {
            stringBuilder.append(", ");
            stringBuilder.append(messages.translate("valinnainen", docBase.getKieli()));
        }


        Element li = docBase.getDocument().createElement("li");
        li.setTextContent(stringBuilder.toString());

        ul.appendChild(li);
    }
}
