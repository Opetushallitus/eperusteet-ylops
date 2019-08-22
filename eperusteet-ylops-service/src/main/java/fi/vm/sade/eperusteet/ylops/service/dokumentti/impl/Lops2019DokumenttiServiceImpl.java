package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Sisalto;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.*;
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
        addHeader(docBase, messages.translate("oppimistavoitteet-ja-opetuksen-keskeiset-sisallot", docBase.getKieli()));

        docBase.getGenerator().increaseDepth();
        addOppiaineet(docBase);
        docBase.getGenerator().decreaseDepth();
    }

    private void addOppiaineet(DokumenttiBase docBase) {
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

        Map<String, Lops2019LaajaAlainenDto> laajaAlaisetOsaamisetMap = new HashMap<>();
        Lops2019LaajaAlainenOsaaminenDto laajaAlainenOsaaminen = perusteenSisalto.getLaajaAlainenOsaaminen();
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
            Lops2019OppiaineDto oa,
            List<Lops2019OpintojaksoDto> opintojaksot,
            Map<String, Lops2019LaajaAlainenDto> laajaAlaisetOsaamisetMap
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

        // Kuvaus
        addLokalisoituteksti(docBase, oa.getKuvaus(), "div");

        // Tehtävä
        Lops2019OppiaineenTehtava tehtava = oa.getTehtava();
        if (tehtava != null) {
            addTeksti(docBase, messages.translate("oppiaineen-tehtava", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, tehtava.getKuvaus(), "div");
        }

        // Laaja-alainen osaaminen
        Lops2019OppimaaranLaajaAlaisetOsaamisetDto laoKokonaisuus = oa.getLaajaAlaisetOsaamiset();
        if (laoKokonaisuus != null) {
            addTeksti(docBase, messages.translate("laaja-alainen-osaaminen", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, laoKokonaisuus.getKuvaus(), "div");

            List<Lops2019OppimaaranLaajaAlainenOsaaminenDto> oaLaajaAlaisetOsaamiset = laoKokonaisuus.getLaajaAlaisetOsaamiset();
            if (!ObjectUtils.isEmpty(oaLaajaAlaisetOsaamiset)) {
                oaLaajaAlaisetOsaamiset.forEach(osaamiset -> {
                    addLokalisoituteksti(docBase, osaamiset.getKuvaus(), "div");
                    Reference laoRef = osaamiset.getLaajaAlaisetOsaamiset();
                    if (laoRef != null && laajaAlaisetOsaamisetMap.containsKey(laoRef.getId())) {
                        Lops2019LaajaAlainenDto lao = laajaAlaisetOsaamisetMap.get(laoRef.getId());
                        addLokalisoituteksti(docBase, lao.getNimi(), "h6");
                        addLokalisoituteksti(docBase, lao.getKuvaus(), "div");
                    }
                });
            }
        }

        // Tavoitteet
        Lops2019OppiaineenTavoitteetDto tavoitteet = oa.getTavoitteet();
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
                        ta.getTavoitteet().forEach(tavoite -> {
                            Element li = docBase.getDocument().createElement("li");
                            li.setTextContent(getTextString(docBase, tavoite.getTavoite()));
                            ul.appendChild(li);
                        });
                        docBase.getBodyElement().appendChild(ul);
                    }
                });
            }
        }

        // Arviointi
        Lops2019OppiaineenArviointi arviointi = oa.getArviointi();
        if (arviointi != null) {
            addTeksti(docBase, messages.translate("arviointi", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, arviointi.getKuvaus(), "div");
        }

        // Pakollisten moduulien kuvaus
        LokalisoituTekstiDto pakollistenModuulienKuvaus = oa.getPakollistenModuulienKuvaus();
        if (pakollistenModuulienKuvaus != null) {
            addTeksti(docBase, messages.translate("pakollisten-moduulien-kuvaus", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, pakollistenModuulienKuvaus, "div");
        }

        // Valinnaisten moduulien kuvaus
        LokalisoituTekstiDto valinnaistenModuulienKuvaus = oa.getValinnaistenModuulienKuvaus();
        if (valinnaistenModuulienKuvaus != null) {
            addTeksti(docBase, messages.translate("valinnaisten-moduulien-kuvaus", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, valinnaistenModuulienKuvaus, "div");
        }

        // Opintojaksot
        if (!ObjectUtils.isEmpty(opintojaksot)) {
            addTeksti(docBase, messages.translate("opintojaksot", docBase.getKieli()), "h6");
            opintojaksot.forEach(oj -> addOpintojakso(docBase, oj, oa));
        }

        // Oppimäärät
        docBase.getGenerator().increaseDepth();
        oa.getOppimaarat().forEach(om -> addOppiaine(docBase, om, opintojaksot, laajaAlaisetOsaamisetMap));
        docBase.getGenerator().decreaseDepth();
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
        Lops2019TehtavaDto tehtava = poa.getTehtava();
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
                        ta.getTavoitteet().forEach(tavoite -> {
                            Element li = docBase.getDocument().createElement("li");
                            li.setTextContent(getTextString(docBase, tavoite.getTavoite()));
                            ul.appendChild(li);
                        });
                        docBase.getBodyElement().appendChild(ul);
                    }
                });
            }
        }

        // Arviointi
        Lops2019ArviointiDto arviointi = poa.getArviointi();
        if (arviointi != null && arviointi.getKuvaus() != null) {
            addTeksti(docBase, messages.translate("arviointi", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, arviointi.getKuvaus(), "div");
        }

        // Laaja-alainen osaaminen
        Lops2019LaajaAlainenOsaaminenDto laoKokonaisuus = poa.getLaajaAlainenOsaaminen();
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

                    addLokalisoituteksti(docBase, lao.getOpinnot(), "div");

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
            Lops2019OppiaineDto oa
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

        addTeksti(docBase, nimiBuilder.toString(), "h5");

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


        // Tavoitteet
        LokalisoituTekstiDto tavoitteet = oj.getTavoitteet();
        if (tavoitteet != null) {
            addTeksti(docBase, messages.translate("tavoitteet", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, oj.getTavoitteet(), "div");
        }

        // Keskeiset sisällöt
        LokalisoituTekstiDto keskeisetSisallot = oj.getKeskeisetSisallot();
        if (keskeisetSisallot != null) {
            addTeksti(docBase, messages.translate("keskeiset-sisallot", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, keskeisetSisallot, "div");
        }

        // Laaja-alainen osaaminen
        LokalisoituTekstiDto laajaAlainenOsaaminen = oj.getLaajaAlainenOsaaminen();
        if (laajaAlainenOsaaminen != null) {
            addTeksti(docBase, messages.translate("laaja-alainen-osaaminen", docBase.getKieli()), "h6");
            addLokalisoituteksti(docBase, laajaAlainenOsaaminen, "div");
        }
    }

    private void addModuuli(
            DokumenttiBase docBase,
            Lops2019ModuuliDto m
    ) {
        // Nimi
        StringBuilder nimiBuilder = new StringBuilder();
        nimiBuilder.append(getTextString(docBase, m.getNimi()));

        // Opintopisteet
        Integer laajuus = m.getLaajuus();
        if (laajuus != null) {
            nimiBuilder.append(", ");
            nimiBuilder.append(laajuus.toString());
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
        addTeksti(docBase, messages.translate("pakollisuus", docBase.getKieli()), "h6");
        addTeksti(docBase, messages.translate(m.isPakollinen() ? "pakollinen" : "valinnainen", docBase.getKieli()), "div");

        // Yleiset tavoitteet
        Lops2019ModuulinTavoitteetDto tavoitteet = m.getTavoitteet();
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
        List<Lops2019ModuulinSisallotDto> sisallot = m.getSisallot();
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
