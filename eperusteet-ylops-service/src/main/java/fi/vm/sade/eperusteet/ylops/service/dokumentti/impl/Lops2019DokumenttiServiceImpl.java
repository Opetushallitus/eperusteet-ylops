package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019OppiaineJarjestys;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.*;
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
import fi.vm.sade.eperusteet.ylops.service.external.KoodistoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.lops2019.impl.Lops2019Utils;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.util.Pair;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Element;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.*;
import static java.util.Comparator.comparing;

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

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private DtoMapper mapper;

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
            List<Lops2019OpintojaksoDto> opintojaksot = opintojaksoService.getAll(ops.getId(), Lops2019OpintojaksoDto.class);
            opintojaksot.addAll(opintojaksoService.getTuodut(ops.getId(), Lops2019OpintojaksoDto.class));
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

        List<Lops2019OppiaineKaikkiDto> perusteOppiaineet = getOppiaineet(ops.getId(), opintojaksotMap);

        Set<Lops2019OppiaineJarjestys> oppiaineJarjestykset = ops.getLops2019().getOppiaineJarjestykset();

        List<Lops2019OppiaineKaikkiDto> oppiaineetJaOppimaarat = perusteenSisalto.getOppiaineet().stream()
                .flatMap(oa -> Stream.concat(Stream.of(oa), oa.getOppimaarat().stream()))
                .collect(Collectors.toList());

        List<Lops2019ModuuliDto> moduulit = oppiaineetJaOppimaarat.stream()
                .map(Lops2019OppiaineKaikkiDto::getModuulit)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        List<Lops2019PaikallinenOppiaineDto> paikallisetOppiaineet = oppiaineService.getAll(ops.getId(), Lops2019PaikallinenOppiaineDto.class).stream()
                .filter(oppiaine -> opintojaksotMap.containsKey(oppiaine.getKoodi()))
                .filter(poa -> StringUtils.isEmpty(poa.getPerusteenOppiaineUri()))
                .collect(Collectors.toList());

        Lops2019Utils.sortOppiaineet(
                oppiaineJarjestykset,
                perusteOppiaineet,
                paikallisetOppiaineet,
                oa -> addOppiaine(docBase, (Lops2019OppiaineKaikkiDto) oa, opintojaksotMap.get(oa.getKoodi().getUri()), opintojaksotMap, oppiaineJarjestykset, moduulit),
                poa -> {
                    Lops2019OppiaineKaikkiDto oppimaaranOppiaine = oppiaineetJaOppimaarat.stream()
                            .filter(oppiaineTaiOppimaara -> oppiaineTaiOppimaara
                                    .getKoodi().getUri().equals(poa.getPerusteenOppiaineUri()))
                            .findFirst()
                            .orElse(null);
                    return addPaikallinenOppiaine(docBase, poa, opintojaksotMap.get(poa.getKoodi()), oppimaaranOppiaine, moduulit);
                }
        );

        // Integraatio opintojaksot
        addIntegraatioOpintojaksot(docBase, moduulit);

        docBase.getGenerator().decreaseDepth();
        docBase.getGenerator().increaseNumber();
    }

    private List<Lops2019OppiaineKaikkiDto> getOppiaineet(
            Long opsId,
            Map<String, List<Lops2019OpintojaksoDto>> opintojaksotMap
    ) {
        List<Lops2019OppiaineKaikkiDto> copyList = mapper.mapAsList(lops2019Service
                .getPerusteOppiaineet(opsId), Lops2019OppiaineKaikkiDto.class);
        return copyList.stream()
                .peek(oppiaine -> {
                    if (!CollectionUtils.isEmpty(oppiaine.getOppimaarat())) {
                        // Piilotetaan oppimäärät, joilla ei ole opintojaksoja
                        oppiaine.setOppimaarat(oppiaine.getOppimaarat().stream()
                                .filter(oppimaara -> opintojaksotMap.containsKey(oppimaara.getKoodi().getUri()))
                                .collect(Collectors.toList()));
                    }
                })
                .filter(oa -> {
                    if (opintojaksotMap.containsKey(oa.getKoodi().getUri())
                            || !CollectionUtils.isEmpty(oa.getOppimaarat())) {
                        return true;
                    } else if (!CollectionUtils.isEmpty(oa.getOppimaarat())) {
                        return oa.getOppimaarat().stream()
                                .filter(om -> om.getKoodi() != null)
                                .filter(om -> om.getKoodi().getUri() != null)
                                .anyMatch(om -> opintojaksotMap.containsKey(om.getKoodi().getUri()));
                    }

                    return oppiaineService.getAll(opsId, Lops2019PaikallinenOppiaineDto.class).stream()
                            .anyMatch(poa -> {
                                String parentKoodi = poa.getPerusteenOppiaineUri();
                                Optional<Lops2019OppiaineKaikkiDto> orgOaOpt = lops2019Service
                                        .getPerusteOppiaineet(opsId).stream()
                                        .filter(oaOrg -> oaOrg.getId().equals(oa.getId()))
                                        .findAny();
                                if (parentKoodi != null) {
                                    return (oa.getKoodi() != null
                                            && oa.getKoodi().getUri() != null
                                            && oa.getKoodi().getUri().equals(parentKoodi))
                                            || (orgOaOpt.isPresent() && orgOaOpt.get().getOppimaarat().stream()
                                            .filter(om -> om.getKoodi() != null)
                                            .filter(om -> om.getKoodi().getUri() != null)
                                            .anyMatch(om -> om.getKoodi().getUri().equals(parentKoodi)));
                                }
                                return false;
                            });

                })
                .collect(Collectors.toList());
    }

    private boolean addOppiaine(
            DokumenttiBase docBase,
            Lops2019OppiaineKaikkiDto oa,
            List<Lops2019OpintojaksoDto> opintojaksot,
            Map<String, List<Lops2019OpintojaksoDto>> opintojaksotMap,
            Set<Lops2019OppiaineJarjestys> oppiaineJarjestykset,
            List<Lops2019ModuuliDto> moduulit
    ) {
        StringBuilder nimiBuilder = new StringBuilder();
        String nimi = getTextString(docBase, oa.getNimi());
        nimiBuilder.append(StringUtils.isEmpty(nimi)
                ? messages.translate("nimeton-oppiaine", docBase.getKieli())
                : nimi);
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
        if (tavoitteet != null && (tavoitteet.getKuvaus() != null || !ObjectUtils.isEmpty(tavoitteet.getTavoitealueet()))) {
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
                    .map(oj -> {
                        Optional<Lops2019OpintojaksonOppiaineDto> ojOaOpt = oj.getOppiaineet().stream()
                                .filter(ojOa -> ojOa.getKoodi() != null)
                                .filter(ojOa -> ojOa.getKoodi().equals(oa.getKoodi().getUri()))
                                .findAny();
                        return new Pair<>(oj, ojOaOpt);
                    })
                    // Ensisijaisesti järjestetään opintojakson oppiaineen järjestyksen mukaan.
                    // Toissijaisesti opintojakson koodin mukaan.
                    .sorted(
                            comparing((Pair<Lops2019OpintojaksoDto, Optional<Lops2019OpintojaksonOppiaineDto>> p) -> {
                                Matcher matcher = Pattern.compile("([^0-9]*?)(\\d+$)").matcher(p.getFirst().getKoodi());
                                if(matcher.matches()) {
                                    return matcher.group(1);
                                }
                                return p.getFirst().getKoodi();
                            })
                            .thenComparingInt((Pair<Lops2019OpintojaksoDto, Optional<Lops2019OpintojaksonOppiaineDto>> p) -> {
                                Matcher matcher = Pattern.compile("([^0-9]*?)(\\d+$)").matcher(p.getFirst().getKoodi());
                                if(matcher.matches()) {
                                    return Integer.parseInt(matcher.group(2));
                                }
                                return 0;
                            }))
                    .sorted(comparing((Pair<Lops2019OpintojaksoDto, Optional<Lops2019OpintojaksonOppiaineDto>> p)
                            -> Optional.ofNullable(p.getSecond().isPresent()
                            ? p.getSecond().get().getJarjestys()
                            : null).orElse(Integer.MAX_VALUE)))
                    .forEach(p -> addOpintojakso(docBase, p.getFirst(), oa, null, moduulit));
            docBase.getGenerator().decreaseDepth();
        }

        Long opsId = docBase.getOps().getId();
        List<Lops2019PaikallinenOppiaineDto> paikallisetOppimaarat = oppiaineService
                .getAll(opsId, Lops2019PaikallinenOppiaineDto.class).stream()
                .filter(poa -> {
                    String parentKoodi = poa.getPerusteenOppiaineUri();
                    Optional<Lops2019OppiaineKaikkiDto> orgOaOpt = lops2019Service.getPerusteOppiaineet(opsId).stream()
                            .filter(oaOrg -> oaOrg.getId().equals(oa.getId()))
                            .findAny();
                    if (parentKoodi != null) {
                        return (oa.getKoodi() != null
                                && oa.getKoodi().getUri() != null
                                && oa.getKoodi().getUri().equals(parentKoodi))
                                || (orgOaOpt.isPresent() && orgOaOpt.get().getOppimaarat().stream()
                                .filter(om -> om.getKoodi() != null)
                                .filter(om -> om.getKoodi().getUri() != null)
                                .anyMatch(om -> om.getKoodi().getUri().equals(parentKoodi)));
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // Oppimäärät
        docBase.getGenerator().increaseDepth();

        Lops2019Utils.sortOppiaineet(
                oppiaineJarjestykset,
                oa.getOppimaarat().stream().filter(om -> om.getKoodi() != null && opintojaksotMap.containsKey(om.getKoodi().getUri())).collect(Collectors.toList()),
                paikallisetOppimaarat.stream().filter(poa -> opintojaksotMap.get(poa.getKoodi()) != null).collect(Collectors.toList()),
                om -> {
                    KoodiDto omKoodi = om.getKoodi();
                    return addOppiaine(docBase, (Lops2019OppiaineKaikkiDto) om, omKoodi != null ? opintojaksotMap.get(omKoodi.getUri()) : null, opintojaksotMap, oppiaineJarjestykset, moduulit);
                },
                pom -> {
                    Lops2019OppiaineKaikkiDto perusteOa = lops2019Service.getPerusteOppiaine(docBase.getOps().getId(), oa.getId());
                    ArrayList<Lops2019OppiaineKaikkiDto> oaJaOppimaarat = new ArrayList<>();
                    oaJaOppimaarat.add(perusteOa);
                    oaJaOppimaarat.addAll(perusteOa.getOppimaarat());
                    Lops2019OppiaineKaikkiDto oppimaaranOppiaine = oaJaOppimaarat.stream()
                            .filter(oppiaineTaiOppimaara -> oppiaineTaiOppimaara
                                    .getKoodi().getUri().equals(pom.getPerusteenOppiaineUri()))
                            .findFirst()
                            .orElse(null);
                    return addPaikallinenOppiaine(docBase, pom, opintojaksotMap.get(pom.getKoodi()), oppimaaranOppiaine, moduulit);
                }
        );

        docBase.getGenerator().decreaseDepth();
        docBase.getGenerator().increaseNumber();

        return true;
    }

    private boolean addPaikallinenOppiaine(
            DokumenttiBase docBase,
            Lops2019PaikallinenOppiaineDto poa,
            List<Lops2019OpintojaksoDto> opintojaksot,
            Lops2019OppiaineKaikkiDto oa,
            List<Lops2019ModuuliDto> moduulit
    ) {
        // Nimi
        StringBuilder nimiBuilder = new StringBuilder();
        String nimi = getTextString(docBase, poa.getNimi());
        nimiBuilder.append(StringUtils.isEmpty(nimi)
                ? messages.translate("nimeton-oppiaine", docBase.getKieli())
                : nimi);
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

        { // Tehtävä
            Lops2019TehtavaDto oppiaineenTehtava = oa != null ? oa.getTehtava() : null;
            fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019TehtavaDto tehtava = poa.getTehtava();

            if (hasLokalisoituTeksti(oppiaineenTehtava, Lops2019TehtavaDto::getKuvaus, docBase)
                    || hasLokalisoituTeksti(tehtava, fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019TehtavaDto::getKuvaus, docBase)) {
                addTeksti(docBase, messages.translate("oppiaineen-tehtava", docBase.getKieli()), "h6");

            }

            if (hasLokalisoituTeksti(oppiaineenTehtava, Lops2019TehtavaDto::getKuvaus, docBase)) {
                addLokalisoituteksti(docBase, oppiaineenTehtava.getKuvaus(), "div");
            }

            if (hasLokalisoituTeksti(tehtava, fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019TehtavaDto::getKuvaus, docBase)) {
                if (hasLokalisoituTeksti(oppiaineenTehtava, Lops2019TehtavaDto::getKuvaus, docBase)) {
                    addTeksti(docBase, messages.translate("paikallinen-lisays", docBase.getKieli()), "p");
                }
                addLokalisoituteksti(docBase, tehtava.getKuvaus(), "div");
            }
        }

        { // Tavoitteet

            Lops2019OppiaineTavoitteetDto oppiaineenTavoitteet = oa != null ? oa.getTavoitteet() : null;
            Lops2019OppiaineenTavoitteetDto tavoitteet = poa.getTavoitteet();
            if ((oppiaineenTavoitteet != null && (oppiaineenTavoitteet.getKuvaus() != null || !ObjectUtils.isEmpty(oppiaineenTavoitteet.getTavoitealueet())))
                    || (tavoitteet != null && (tavoitteet.getKuvaus() != null || !ObjectUtils.isEmpty(tavoitteet.getTavoitealueet())))) {
                addTeksti(docBase, messages.translate("tavoitteet", docBase.getKieli()), "h6");
            }

            if (oppiaineenTavoitteet != null && (oppiaineenTavoitteet.getKuvaus() != null || !ObjectUtils.isEmpty(oppiaineenTavoitteet.getTavoitealueet()))) {
                addLokalisoituteksti(docBase, oppiaineenTavoitteet.getKuvaus(), "div");

                List<Lops2019OppiaineTavoitealueDto> tavoitealueet = oppiaineenTavoitteet.getTavoitealueet();
                if (!ObjectUtils.isEmpty(tavoitealueet)) {
                    tavoitealueet.forEach(ta -> {
                        addLokalisoituteksti(docBase, ta.getNimi(), "h6");

                        LokalisoituTekstiDto kohde = ta.getKohde();
                        if (kohde != null && !ObjectUtils.isEmpty(ta.getTavoitteet())) {

                            addLokalisoituteksti(docBase, kohde, "p");
                            addList(docBase, ta.getTavoitteet());
                        }
                    });
                }

            }

            if (tavoitteet != null && (tavoitteet.getKuvaus() != null || !ObjectUtils.isEmpty(tavoitteet.getTavoitealueet()))) {
                if (hasLokalisoituTeksti(oppiaineenTavoitteet, Lops2019OppiaineTavoitteetDto::getKuvaus, docBase)
                        || (oppiaineenTavoitteet != null && !ObjectUtils.isEmpty(oppiaineenTavoitteet.getTavoitealueet()))) {
                    addTeksti(docBase, messages.translate("paikallinen-lisays", docBase.getKieli()), "p");
                }
                addLokalisoituteksti(docBase, tavoitteet.getKuvaus(), "div");

                List<Lops2019OppiaineenTavoitealueDto> tavoitealueet = tavoitteet.getTavoitealueet();
                if (!ObjectUtils.isEmpty(tavoitealueet)) {
                    tavoitealueet.forEach(ta -> {
                        addLokalisoituteksti(docBase, ta.getNimi(), "h6");

                        LokalisoituTekstiDto kohde = ta.getKohde();
                        if (kohde != null && !ObjectUtils.isEmpty(ta.getTavoitteet())) {

                            addLokalisoituteksti(docBase, kohde, "p");
                            addList(docBase, ta.getTavoitteet().stream()
                                    .filter(Objects::nonNull)
                                    .map(Lops2019TavoitealueenTavoite::getTavoite)
                                    .collect(Collectors.toList()));
                        }
                    });
                }
            }
        }

        { // Arviointi

            Lops2019ArviointiDto oppianeenArviointi = oa != null ? oa.getArviointi() : null;
            fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019ArviointiDto arviointi = poa.getArviointi();

            if (hasLokalisoituTeksti(oppianeenArviointi, Lops2019ArviointiDto::getKuvaus, docBase)
                    || hasLokalisoituTeksti(arviointi, fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019ArviointiDto::getKuvaus, docBase)) {
                addTeksti(docBase, messages.translate("arviointi", docBase.getKieli()), "h6");
            }


            if (hasLokalisoituTeksti(oppianeenArviointi, Lops2019ArviointiDto::getKuvaus, docBase)) {
                addLokalisoituteksti(docBase, oppianeenArviointi.getKuvaus(), "div");
            }

            if (hasLokalisoituTeksti(arviointi, fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019ArviointiDto::getKuvaus, docBase)) {
                if (hasLokalisoituTeksti(oppianeenArviointi, Lops2019ArviointiDto::getKuvaus, docBase)) {
                    addTeksti(docBase, messages.translate("paikallinen-lisays", docBase.getKieli()), "p");
                }
                addLokalisoituteksti(docBase, arviointi.getKuvaus(), "div");
            }
        }

        { // Laaja-alainen osaaminen

            Lops2019OppiaineLaajaAlainenOsaaminenDto oppiaineenLaajaAlainenOsaaminen = oa != null ? oa.getLaajaAlaisetOsaamiset() : null;
            List<Lops2019PaikallinenLaajaAlainenDto> laajaAlainenOsaaminen = poa.getLaajaAlainenOsaaminen();

            if (hasLokalisoituTeksti(oppiaineenLaajaAlainenOsaaminen, Lops2019OppiaineLaajaAlainenOsaaminenDto::getKuvaus, docBase)
                    || !ObjectUtils.isEmpty(laajaAlainenOsaaminen)) {
                addTeksti(docBase, messages.translate("laaja-alaiset-osaamiset", docBase.getKieli()), "h6");
            }

            if (hasLokalisoituTeksti(oppiaineenLaajaAlainenOsaaminen, Lops2019OppiaineLaajaAlainenOsaaminenDto::getKuvaus, docBase)) {
                addLokalisoituteksti(docBase, oppiaineenLaajaAlainenOsaaminen.getKuvaus(), "div");
            }

            if (!ObjectUtils.isEmpty(laajaAlainenOsaaminen)) {
                if (hasLokalisoituTeksti(oppiaineenLaajaAlainenOsaaminen, Lops2019OppiaineLaajaAlainenOsaaminenDto::getKuvaus, docBase)) {
                    addTeksti(docBase, messages.translate("paikallinen-lisays", docBase.getKieli()), "p");
                }
                laajaAlainenOsaaminen.forEach(lao -> {
                    KoodistoKoodiDto laoKoodi = koodistoService.get("laajaalainenosaaminenlops2021", lao.getKoodi());
                    if (laoKoodi != null) {
                        addLokalisoituteksti(docBase, laoKoodi.getNimi(), "h6");
                    }
                    addLokalisoituteksti(docBase, lao.getKuvaus(), "div");
                });
            }
        }

        if (!ObjectUtils.isEmpty(opintojaksot)) {
            addTeksti(docBase, messages.translate("opintojaksot", docBase.getKieli()), "h6");
            docBase.getGenerator().increaseDepth();

            opintojaksot.stream()
                    .map(oj -> {
                        Optional<Lops2019OpintojaksonOppiaineDto> ojOaOpt = oj.getOppiaineet().stream()
                                .filter(ojOa -> ojOa.getKoodi() != null)
                                .filter(ojOa -> ojOa.getKoodi().equals(poa.getKoodi()))
                                .findAny();
                        return new Pair<>(oj, ojOaOpt);
                    })
                    // Ensisijaisesti järjestetään opintojakson oppiaineen järjestyksen mukaan.
                    // Toissijaisesti opintojakson koodin mukaan.
                    .sorted(comparing(p -> p.getFirst().getKoodi()))
                    .sorted(comparing((Pair<Lops2019OpintojaksoDto, Optional<Lops2019OpintojaksonOppiaineDto>> p)
                            -> Optional.ofNullable(p.getSecond().isPresent()
                            ? p.getSecond().get().getJarjestys()
                            : null).orElse(Integer.MAX_VALUE)))
                    .forEach(p -> addOpintojakso(docBase, p.getFirst(), null, poa, moduulit));
            docBase.getGenerator().decreaseDepth();
        }

        docBase.getGenerator().increaseNumber();

        return true;
    }

    private <T> boolean hasLokalisoituTeksti(T objekti, Function<T, LokalisoituTekstiDto> getLokalisoituTeksti, DokumenttiBase docBase) {
        return objekti != null
                && getLokalisoituTeksti.apply(objekti) != null
                && !ObjectUtils.isEmpty(Jsoup.parse(getLokalisoituTeksti.apply(objekti).get(docBase.getKieli())).text());
    }

    private void addOpintojakso(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj,
            Lops2019OppiaineKaikkiDto oa,
            Lops2019PaikallinenOppiaineDto poa,
            List<Lops2019ModuuliDto> moduulit
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
            addIntegraatioOpintojakso(docBase, oj, moduulit);
        } else {
            if (oppiaineet.size() > 1) {
                // Integraatio opintojakso
                addIntegraatioOpintojaksoLinkki(docBase, oj);
            } else {
                // Yhden oppiaineen opintojakso
                addYhdenOppiaineenOpintojakso(docBase, oj, moduulit);
            }
        }

        // mahdollistetaan sivun vaihtuminen jos opintojaksolla vain h-tason otsikoita (ilman sisaltoja)
        addPlaceholder(docBase);
        docBase.getGenerator().increaseNumber();
    }

    private void addOpintojaksonOtsikko(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj,
            boolean isIntegraatioOpintojakso
    ) {
        StringBuilder nimiBuilder = new StringBuilder();

        // Nimi
        String nimi = getTextString(docBase, oj.getNimi());
        nimiBuilder.append(StringUtils.isEmpty(nimi)
                ? messages.translate("nimeton-opintojakso", docBase.getKieli())
                : nimi);

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
            addHeader(docBase, nimiBuilder.toString(), "opintojakso_" + koodi, false);
        } else {
            addHeader(docBase, nimiBuilder.toString(), null, false);
        }

    }

    private void addYhdenOppiaineenOpintojakso(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj,
            List<Lops2019ModuuliDto> moduulit
    ) {
        // Opintojakson moduulit
        List<Lops2019ModuuliDto> moduulitSorted = new ArrayList<>();
        addOpintojaksonModuulit(docBase, oj, moduulit, moduulitSorted);

        addOpintojaksonLisalaajuus(docBase, oj);

        // Opintojakson paikalliset opintojaksot
        addOpintojaksonPaikallisetOpintojaksot(docBase, oj);

        // Tavoitteet ja paikallinen lisäys
        addTeksti(docBase, messages.translate("tavoitteet", docBase.getKieli()), "h6");
        addOpintojaksonTavoitteet(docBase, moduulitSorted);
        addOpintojaksonTavoitteetPaikallinenLisays(docBase, oj);

        // Keskeiset sisällöt ja paikallinen lisäys
        addTeksti(docBase, messages.translate("keskeiset-sisallot", docBase.getKieli()), "h6");
        addOpintojaksonSisallot(docBase, moduulitSorted);
        addOpintojaksonSisallotPaikallinenLisays(docBase, oj);

        // Laaja-alainen osaaminen ja paikallinen lisäys
        // NOTE: mahdollisesti palautetaan tulevaisuudessa
        //addOpintojaksonOppiaineenLaajaAlainenOsaaminen(docBase, oppiaineet);
        //addOpintojaksonOppiaineenPaikallinenLaajaAlainenOsaaminen(docBase, paikallisetOppiaineet);
        addOpintojaksonLaajaAlainenOsaaminenPaikallinenLisays(docBase, oj);

        // Arviointi
        //addOpintojaksonArviointi(docBase, oppiaineet);
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
            DokumenttiBase docBase,
            List<Lops2019ModuuliDto> moduulit
    ) {
        Long id = docBase.getOps().getId();
        List<Lops2019OpintojaksoDto> integrattioOpintojaksot = opintojaksoService.getAll(id, Lops2019OpintojaksoDto.class).stream()
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
                .forEach(oj -> addOpintojakso(docBase, oj, null, null, moduulit));
        docBase.getGenerator().decreaseDepth();
        docBase.getGenerator().increaseNumber();
    }

    private void addIntegraatioOpintojakso(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj,
            List<Lops2019ModuuliDto> moduulit
    ) {
        List<Lops2019OpintojaksonOppiaineDto> oppiaineetSorted = oj.getOppiaineet().stream()
                .sorted(Comparator.comparing(Lops2019OpintojaksonOppiaineDto::getKoodi))
                .collect(Collectors.toList());

        // Haetaan perusteen oppiaineet
        Lops2019SisaltoDto sisaltoDto = docBase.getPerusteDto().getLops2019();
        List<Lops2019OppiaineKaikkiDto> oppiaineetKaikki = Stream.concat(
                sisaltoDto.getOppiaineet().stream(),
                sisaltoDto.getOppiaineet().stream().map(Lops2019OppiaineKaikkiDto::getOppimaarat).flatMap(Collection::stream))
            .collect(Collectors.toList());
        List<Lops2019PaikallinenOppiaineDto> poppiaineetKaikki = oppiaineService.getAll(docBase.getOps().getId(), Lops2019PaikallinenOppiaineDto.class);

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
        addOpintojaksonModuulit(docBase, oj, moduulit, moduulitSorted);

        addOpintojaksonLisalaajuus(docBase, oj);

        // Opintojakson paikalliset opintojaksot
        addOpintojaksonPaikallisetOpintojaksot(docBase, oj);

        // Tavoitteet ja paikallinen lisäys
        addTeksti(docBase, messages.translate("tavoitteet", docBase.getKieli()), "h6");
        addOpintojaksonTavoitteet(docBase, moduulitSorted);
        addOpintojaksonTavoitteetPaikallinenLisays(docBase, oj);

        // Keskeiset sisällöt ja paikallinen lisäys
        addTeksti(docBase, messages.translate("keskeiset-sisallot", docBase.getKieli()), "h6");
        addOpintojaksonSisallot(docBase, moduulitSorted);
        addOpintojaksonSisallotPaikallinenLisays(docBase, oj);

        // Laaja-alainen osaaminen ja paikallinen lisäys
        // NOTE: mahdollisesti palautetaan tulevaisuudessa
        //addOpintojaksonOppiaineenLaajaAlainenOsaaminen(docBase, oppiaineet);
        //addOpintojaksonOppiaineenPaikallinenLaajaAlainenOsaaminen(docBase, paikallisetOppiaineet);
        addOpintojaksonLaajaAlainenOsaaminenPaikallinenLisays(docBase, oj);

        // Arviointi
        //addOpintojaksonArviointi(docBase, oppiaineet);
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
                        .map(pair -> {
                            String nimi = getTextString(docBase, pair.getFirst());
                            return StringUtils.isEmpty(nimi)
                                    ? messages.translate("nimeton-oppiaine", docBase.getKieli())
                                    : nimi;
                        })
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
            List<Lops2019ModuuliDto> moduulit,
            List<Lops2019ModuuliDto> moduulitSorted
    ) {
        List<Lops2019OpintojaksonModuuliDto> opintojaksonModuulit = oj.getModuulit();
        if (!ObjectUtils.isEmpty(opintojaksonModuulit)) {
            Element ul = docBase.getDocument().createElement("ul");
            addTeksti(docBase, messages.translate("opintojakson-moduulit", docBase.getKieli()), "h6");
            opintojaksonModuulit.stream()
                    .sorted(Comparator.comparing(Lops2019OpintojaksonModuuliDto::getKoodiUri))
                    .forEach(ojm -> {
                        String koodiUri = ojm.getKoodiUri();
                        if (koodiUri != null && !ObjectUtils.isEmpty(moduulit)) {
                            moduulit.stream()
                                    .filter(moduuli -> koodiUri.equals(moduuli.getKoodi() != null ? moduuli.getKoodi().getUri() : null))
                                    .findAny()
                                    .ifPresent(m -> {
                                        moduulitSorted.add(m);
                                        addModuuli(docBase, m, ul);
                                    });
                        }
                    });
            docBase.getBodyElement().appendChild(ul);
        }
    }

    private void addOpintojaksonLisalaajuus(DokumenttiBase docBase, Lops2019OpintojaksoDto oj) {
        Long oppiainelaajuus = oj.getOppiaineet().stream()
                .filter(oppiaine -> oppiaine.getLaajuus() != null)
                .map(Lops2019OpintojaksonOppiaineDto::getLaajuus)
                .collect(Collectors.summingLong(Long::longValue));
        if (!ObjectUtils.isEmpty(oj.getModuulit()) && oppiainelaajuus > 0l) {
            addTeksti(docBase, messages.translate("lisalaajuus", docBase.getKieli()), "h6");
            addTeksti(docBase, oppiainelaajuus + " " + messages.translate("op", docBase.getKieli()), "p");
        }
    }

    private void addOpintojaksonPaikallisetOpintojaksot(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj) {

        if (!ObjectUtils.isEmpty(oj.getPaikallisetOpintojaksot())) {

            Element ul = docBase.getDocument().createElement("ul");
            addTeksti(docBase, messages.translate("opintojakson-paikalliset-opintojaksot", docBase.getKieli()), "h6");
            oj.getPaikallisetOpintojaksot().stream()
                    .sorted(Comparator.comparing(Lops2019OpintojaksoDto::getKoodi))
                    .forEach(paikallinenOpintojakso -> addPaikallinenOpintojakso(docBase, paikallinenOpintojakso, ul));
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
                        String nimi = getTextString(docBase, m.getNimi());
                        nimiEl.setTextContent(StringUtils.isEmpty(nimi)
                                ? messages.translate("nimeton-moduuli", docBase.getKieli())
                                : nimi);
                        cite.appendChild(nimiEl);

                        Lops2019ModuuliTavoiteDto tavoitteet = m.getTavoitteet();
                        if (!ObjectUtils.isEmpty(tavoitteet)) {

                            // Moduulit tavoitteet
                            if (!ObjectUtils.isEmpty(tavoitteet.getTavoitteet())) {

                                // Kohde
                                LokalisoituTekstiDto kohde = tavoitteet.getKohde();
                                if (kohde != null) {
                                    Element kohdeEl = docBase.getDocument().createElement("p");
                                    kohdeEl.setTextContent(getTextString(docBase, kohde));
                                    cite.appendChild(kohdeEl);
                                }

                                // Tavoitteet
                                cite.appendChild(getList(docBase, tavoitteet.getTavoitteet()));
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
            addList(docBase, tavoitteet.stream()
                    .filter(Objects::nonNull)
                    .map(Lops2019OpintojaksonTavoiteDto::getKuvaus)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }

        if (!ObjectUtils.isEmpty(oj.getPaikallisetOpintojaksot())) {
            oj.getPaikallisetOpintojaksot().forEach(paikallinenOpintojakso -> {

                if (!ObjectUtils.isEmpty(paikallinenOpintojakso.getTavoitteet())) {
                    String nimi = getTextString(docBase, paikallinenOpintojakso.getNimi());
                    addTeksti(docBase, StringUtils.isEmpty(nimi)
                            ? messages.translate("nimeton-opintojakso", docBase.getKieli())
                            : nimi, "p");
                    addList(docBase, paikallinenOpintojakso.getTavoitteet().stream()
                            .filter(Objects::nonNull)
                            .map(Lops2019OpintojaksonTavoiteDto::getKuvaus)
                            .collect(Collectors.toList()));
                }

            });
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
                        String nimi = getTextString(docBase, m.getNimi());
                        nimiEl.setTextContent(StringUtils.isEmpty(nimi)
                                ? messages.translate("nimeton-moduuli", docBase.getKieli())
                                : nimi);
                        cite.appendChild(nimiEl);

                        List<Lops2019ModuuliSisaltoDto> sisallot = m.getSisallot();
                        if (!ObjectUtils.isEmpty(sisallot)) {

                            // Moduulin keskeiset sisällöt
                            sisallot.stream()
                                    .filter(Objects::nonNull)
                                    .forEach(sisalto -> {
                                        if (!ObjectUtils.isEmpty(sisalto.getSisallot())) {

                                            LokalisoituTekstiDto kohde = sisalto.getKohde();
                                            if (kohde != null) {
                                                // Kohde
                                                Element kohdeEl = docBase.getDocument().createElement("p");
                                                kohdeEl.setTextContent(getTextString(docBase, kohde));
                                                cite.appendChild(kohdeEl);
                                            }

                                            // Sisallöt
                                            cite.appendChild(getList(docBase, sisalto.getSisallot()));
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
            addList(docBase, sisallot.stream()
                    .filter(Objects::nonNull)
                    .map(Lops2019OpintojaksonKeskeinenSisaltoDto::getKuvaus)
                    .collect(Collectors.toList()));
        }

        if (!ObjectUtils.isEmpty(oj.getPaikallisetOpintojaksot())) {
            oj.getPaikallisetOpintojaksot().forEach(paikallinenOpintojakso -> {

                if (!ObjectUtils.isEmpty(paikallinenOpintojakso.getKeskeisetSisallot())) {
                    String nimi = getTextString(docBase, paikallinenOpintojakso.getNimi());
                    addTeksti(docBase, StringUtils.isEmpty(nimi)
                            ? messages.translate("nimeton-moduuli", docBase.getKieli())
                            : nimi, "p");
                    addList(docBase, paikallinenOpintojakso.getKeskeisetSisallot().stream()
                            .filter(Objects::nonNull)
                            .map(Lops2019OpintojaksonKeskeinenSisaltoDto::getKuvaus)
                            .collect(Collectors.toList()));
                }

            });
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
            poppiaineet.forEach(poa -> addPaikallinenLaajaalainen(docBase, poa));
        }
    }

    private void addPaikallinenLaajaalainen(DokumenttiBase docBase, Lops2019PaikallinenOppiaineDto poa) {
        // Laaja-alainen osaaminen
        if (poa != null) {
            List<Lops2019PaikallinenLaajaAlainenDto> laajaAlainenOsaaminen = poa.getLaajaAlainenOsaaminen();
            if (!ObjectUtils.isEmpty(laajaAlainenOsaaminen)) {
                addTeksti(docBase, messages.translate("laaja-alaiset-osaamiset", docBase.getKieli()), "h6");
                laajaAlainenOsaaminen.forEach(lao -> {
                    KoodistoKoodiDto laoKoodi = koodistoService.get("laajaalainenosaaminenlops2021", lao.getKoodi());
                    if (laoKoodi != null) {
                        addLokalisoituteksti(docBase, laoKoodi.getNimi(), "h6");
                    }
                    addLokalisoituteksti(docBase, lao.getKuvaus(), "p");
                });
            }
        }
    }

    private void addOpintojaksonLaajaAlainenOsaaminenPaikallinenLisays(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto oj
    ) {
        boolean[] otsikkoLisatty = {false};

        List<Lops2019PaikallinenLaajaAlainenDto> laajaAlainenOsaaminen = oj.getLaajaAlainenOsaaminen();
        if (!ObjectUtils.isEmpty(laajaAlainenOsaaminen)) {
            addTeksti(docBase, messages.translate("paikallinen-lisays", docBase.getKieli()), "p");
            laajaAlainenOsaaminen.forEach(laajaAlainenDto -> lops2019Service
                    .getLaajaAlaisetOsaamiset(docBase.getKieli()).getLaajaAlaisetOsaamiset().stream()
                    .filter(lao -> lao.getKoodi() != null
                            && lao.getKoodi().getUri() != null
                            && Objects.equals(lao.getKoodi().getUri(), laajaAlainenDto.getKoodi()))
                    .findAny()
                    .ifPresent(l -> {
                        if (!otsikkoLisatty[0]) {
                            addTeksti(docBase, messages.translate("laaja-alainen-osaaminen", docBase.getKieli()), "h6");
                            otsikkoLisatty[0] = true;
                        }

                        // Laaja-alaisen osaaminen nimi
                        addLokalisoituteksti(docBase, l.getNimi(), "h6");

                        // Kuvaus
                        LokalisoituTekstiDto kuvaus = laajaAlainenDto.getKuvaus();
                        addLokalisoituteksti(docBase, kuvaus, "div");
                    }));
        }

        if (!ObjectUtils.isEmpty(oj.getPaikallisetOpintojaksot())) {
            oj.getPaikallisetOpintojaksot().forEach(paikallinenOpintojakso -> {

                if (!ObjectUtils.isEmpty(paikallinenOpintojakso.getLaajaAlainenOsaaminen())) {
                    String nimi = getTextString(docBase, paikallinenOpintojakso.getNimi());
                    addTeksti(docBase, StringUtils.isEmpty(nimi)
                            ? messages.translate("nimeton-opintojakso", docBase.getKieli())
                            : nimi, "p");
                    paikallinenOpintojakso.getLaajaAlainenOsaaminen().forEach(laajaAlainenDto -> lops2019Service
                            .getLaajaAlaisetOsaamiset(docBase.getKieli()).getLaajaAlaisetOsaamiset().stream()
                            .filter(lao -> lao.getKoodi() != null
                                    && lao.getKoodi().getUri() != null
                                    && Objects.equals(lao.getKoodi().getUri(), laajaAlainenDto.getKoodi()))
                            .findAny()
                            .ifPresent(l -> {
                                if (!otsikkoLisatty[0]) {
                                    addTeksti(docBase, messages.translate("laaja-alainen-osaaminen", docBase.getKieli()), "h6");
                                    otsikkoLisatty[0] = true;
                                }

                                // Laaja-alaisen osaaminen nimi
                                addLokalisoituteksti(docBase, l.getNimi(), "h6");

                                // Kuvaus
                                LokalisoituTekstiDto kuvaus = laajaAlainenDto.getKuvaus();
                                addLokalisoituteksti(docBase, kuvaus, "div");
                            }));
                }

            });
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
                        String nimi = getTextString(docBase, oa.getNimi());
                        addTeksti(docBase, StringUtils.isEmpty(nimi)
                                ? messages.translate("nimeton-oppiaine", docBase.getKieli())
                                : nimi, "h6");
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

        if (arviointi != null || (!ObjectUtils.isEmpty(oj.getPaikallisetOpintojaksot())
                && oj.getPaikallisetOpintojaksot().stream().anyMatch(paikallinenOpintojakso -> paikallinenOpintojakso.getArviointi() != null))) {
            addTeksti(docBase, messages.translate("opintojakson-arviointi", docBase.getKieli()), "h6");
        }

        if (arviointi != null) {
            addTeksti(docBase, messages.translate("paikallinen-lisays", docBase.getKieli()), "p");
            addLokalisoituteksti(docBase, arviointi, "div");
        }

        if (!ObjectUtils.isEmpty(oj.getPaikallisetOpintojaksot())) {
            oj.getPaikallisetOpintojaksot().forEach(paikallinenOpintojakso -> {
                LokalisoituTekstiDto paikallinenArviointi = paikallinenOpintojakso.getArviointi();
                if (paikallinenArviointi != null) {
                    String nimi = getTextString(docBase, paikallinenOpintojakso.getNimi());
                    addTeksti(docBase, StringUtils.isEmpty(nimi)
                            ? messages.translate("nimeton-opintojakso", docBase.getKieli())
                            : nimi, "p");
                    addLokalisoituteksti(docBase, paikallinenArviointi, "div");
                }
            });
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
        String nimi = getTextString(docBase, m.getNimi());
        stringBuilder.append(StringUtils.isEmpty(nimi)
                ? messages.translate("nimeton-moduuli", docBase.getKieli())
                : nimi);

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

    private void addPaikallinenOpintojakso(
            DokumenttiBase docBase,
            Lops2019OpintojaksoDto paikallinenOpintojakso,
            Element ul) {

        StringBuilder stringBuilder = new StringBuilder();

        // Nimi
        String nimi = getTextString(docBase, paikallinenOpintojakso.getNimi());
        stringBuilder.append(StringUtils.isEmpty(nimi)
                ? messages.translate("nimeton-opintojakso", docBase.getKieli())
                : nimi);

        // Opintopisteet
        Long laajuus = paikallinenOpintojakso.getLaajuus();
        if (laajuus != null) {
            stringBuilder.append(", ");
            stringBuilder.append(laajuus.toString());
            stringBuilder.append(" ");
            stringBuilder.append(messages.translate("op", docBase.getKieli()));
        }

        // Koodi
        String koodi = paikallinenOpintojakso.getKoodi();
        if (koodi != null) {
            stringBuilder.append(" (");
            stringBuilder.append(koodi);
            stringBuilder.append(")");
        }

        Element li = docBase.getDocument().createElement("li");
        li.setTextContent(stringBuilder.toString());
        ul.appendChild(li);
    }
}
