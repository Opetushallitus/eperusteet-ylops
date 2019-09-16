package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Sisalto;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.*;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.Lops2019SisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.laajaalainenosaaminen.Lops2019LaajaAlainenOsaaminenDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.laajaalainenosaaminen.Lops2019LaajaAlainenOsaaminenKokonaisuusDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

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
    public void addLops2019Sisalto(DokumenttiBase docBase) throws ParserConfigurationException, SAXException, IOException {
        addHeader(docBase, messages.translate("oppiaineet", docBase.getKieli()));

        docBase.getGenerator().increaseDepth();
        addOppiaineet(docBase);
        docBase.getGenerator().decreaseDepth();
    }

    private void addOppiaineet(DokumenttiBase docBase) {
        // Todo: opsin sisältö
        Lops2019Sisalto lops2019Sisalto = docBase.getOps().getLops2019();

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

        Map<String, Lops2019LaajaAlainenOsaaminenDto> laajaAlaisetOsaamisetMap = new HashMap<>();
        Lops2019LaajaAlainenOsaaminenKokonaisuusDto laajaAlainenOsaaminen = perusteenSisalto.getLaajaAlainenOsaaminen();
        if (laajaAlainenOsaaminen != null && !ObjectUtils.isEmpty(laajaAlainenOsaaminen.getLaajaAlaisetOsaamiset())) {
            laajaAlainenOsaaminen.getLaajaAlaisetOsaamiset().forEach(lao -> {
                Long id = lao.getId();
                if (id != null) {
                    laajaAlaisetOsaamisetMap.put(id.toString(), lao);
                }
            });
        }

        // Perusteen oppiaineet
        perusteenSisalto.getOppiaineet().forEach(oa -> {
            KoodiDto koodi = oa.getKoodi();
            addOppiaine(docBase, oa, koodi != null ? opintojaksotMap.get(koodi.getUri()) : null, laajaAlaisetOsaamisetMap);
        });

        // Paikalliset oppiaineet
        List<Lops2019PaikallinenOppiaineDto> oppiaineet = oppiaineService.getAll(ops.getId());
        oppiaineet.forEach(poa -> addPaikallinenOppiaine(docBase, poa, opintojaksotMap.get(poa.getKoodi())));


    }

    private void addOppiaine(
            DokumenttiBase docBase,
            Lops2019OppiaineKaikkiDto oa,
            List<Lops2019OpintojaksoDto> opintojaksot,
            Map<String, Lops2019LaajaAlainenOsaaminenDto> laajaAlaisetOsaamisetMap
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

        // Opintojaksot
        if (!ObjectUtils.isEmpty(opintojaksot)) {
            addTeksti(docBase, messages.translate("opintojaksot", docBase.getKieli()), "h6");
            docBase.getGenerator().increaseDepth();
            opintojaksot.forEach(oj -> addOpintojakso(docBase, oj, oa));
            docBase.getGenerator().decreaseDepth();
        }

        // Oppimäärät
        docBase.getGenerator().increaseDepth();
        oa.getOppimaarat().forEach(om -> addOppiaine(docBase, om, opintojaksot, laajaAlaisetOsaamisetMap));
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

        // Kuvaus
        addLokalisoituteksti(docBase, poa.getKuvaus(), "div");

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
        fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019LaajaAlainenOsaaminenDto laoKokonaisuus = poa.getLaajaAlainenOsaaminen();
        if (laoKokonaisuus != null) {
            addTeksti(docBase, messages.translate("laaja-alainen-osaaminen", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, laoKokonaisuus.getKuvaus(), "div");
            List<Lops2019LaajaAlainenDto> laajaAlaisetOsaamiset = laoKokonaisuus.getLaajaAlaisetOsaamiset();
            if (!ObjectUtils.isEmpty(laajaAlaisetOsaamiset)) {
                laajaAlaisetOsaamiset.forEach(lao ->{
                    LokalisoituTekstiDto nimi = lao.getNimi();
                    if (nimi != null) {
                        addLokalisoituteksti(docBase, lao.getNimi(), "h6");
                    }

                    addLokalisoituteksti(docBase, lao.getKuvaus(), "div");

//                    addLokalisoituteksti(docBase, lao.getOpinnot(), "div");

                    // Painopisteet?

                    // Tavoitteet?
                });
            }
        }

        // Opintojaksot?
    }

    private void addOpintojakso(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj,
            Lops2019OppiaineKaikkiDto oa
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

        addHeader(docBase, nimiBuilder.toString());
        //addTeksti(docBase, nimiBuilder.toString(), "h5");

        // Kuvaus
        LokalisoituTekstiDto kuvaus = oj.getKuvaus();
        addLokalisoituteksti(docBase, kuvaus, "div");

        // Opintojakson moduulit
        addTeksti(docBase, messages.translate("opintojakson-moduulit", docBase.getKieli()), "h6");
        oj.getModuulit().stream()
                .sorted(Comparator.comparing(Lops2019OpintojaksonModuuliDto::getKoodiUri))
                .forEach(ojm -> {
                    String koodiUri = ojm.getKoodiUri();
                    if (koodiUri != null) {
                        oa.getModuulit().stream()
                                .filter(m -> koodiUri.equals(m.getKoodi() != null ? m.getKoodi().getUri() : null))
                                .findAny()
                                .ifPresent(m -> addModuuli(docBase, m));
                    }
                });

        // Laaja-alainen osaaminen
        LokalisoituTekstiDto laajaAlainenOsaaminen = oj.getLaajaAlainenOsaaminen();
        if (laajaAlainenOsaaminen != null) {
            addTeksti(docBase, messages.translate("laaja-alainen-osaaminen", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, laajaAlainenOsaaminen, "div");
        }

        docBase.getGenerator().increaseNumber();
    }

    private void addModuuli(
            DokumenttiBase docBase,
            Lops2019ModuuliDto m
    ) {
        // Nimi
        StringBuilder nimiBuilder = new StringBuilder();
        nimiBuilder.append(getTextString(docBase, m.getNimi()));

        // Opintopisteet
        BigDecimal laajuus = m.getLaajuus();
        if (laajuus != null) {
            nimiBuilder.append(", ");
            nimiBuilder.append(laajuus.stripTrailingZeros().toPlainString());
            nimiBuilder.append(" ");
            nimiBuilder.append(messages.translate("op", docBase.getKieli()));
        }

        if (m.getKoodi() != null && m.getKoodi().getArvo() != null) {
            nimiBuilder.append(" (");
            nimiBuilder.append(m.getKoodi().getArvo());
            nimiBuilder.append(")");
        }
        addTeksti(docBase, nimiBuilder.toString(), "h5");

        // Kuvaus
        addLokalisoituteksti(docBase, m.getKuvaus(), "div");

        // Pakollisuus
        addTeksti(docBase, messages.translate(m.isPakollinen() ? "pakollinen-moduuli" : "valinnainen-moduuli",
                docBase.getKieli()), "h6");

        // Yleiset tavoitteet
        Lops2019ModuuliTavoiteDto tavoitteet = m.getTavoitteet();
        if (tavoitteet != null) {
            LokalisoituTekstiDto kohde = tavoitteet.getKohde();
            if (kohde != null && !ObjectUtils.isEmpty(tavoitteet.getTavoitteet())) {
                addTeksti(docBase, messages.translate("yleiset-tavoitteet", docBase.getKieli()), "h6");

                // Kohde
                addLokalisoituteksti(docBase, kohde, "p");

                // Tavoitteet
                Element ul = docBase.getDocument().createElement("ul");
                tavoitteet.getTavoitteet().forEach(t -> {
                    Element li = docBase.getDocument().createElement("li");
                    li.setTextContent(getTextString(docBase, t));
                    ul.appendChild(li);
                });
                docBase.getBodyElement().appendChild(ul);
            }
        }

        // Keskeiset sisällöt
        List<Lops2019ModuuliSisaltoDto> sisallot = m.getSisallot();
        if (!ObjectUtils.isEmpty(sisallot)) {
            addTeksti(docBase, messages.translate("keskeiset-sisallot", docBase.getKieli()), "h6");

            sisallot.forEach(s -> {
                LokalisoituTekstiDto kohde = s.getKohde();
                if (kohde != null && !ObjectUtils.isEmpty(s.getSisallot())) {
                    // Kohde
                    addLokalisoituteksti(docBase, kohde, "p");

                    // Sisallöt
                    Element ul = docBase.getDocument().createElement("ul");
                    s.getSisallot().forEach(s2 -> {
                        Element li = docBase.getDocument().createElement("li");
                        li.setTextContent(getTextString(docBase, s2));
                        ul.appendChild(li);
                    });
                    docBase.getBodyElement().appendChild(ul);
                }
            });
        }
    }
}
