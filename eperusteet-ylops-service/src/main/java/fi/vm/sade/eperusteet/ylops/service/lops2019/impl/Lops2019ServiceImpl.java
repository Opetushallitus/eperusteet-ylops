package fi.vm.sade.eperusteet.ylops.service.lops2019.impl;

import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.*;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.Lops2019ValidointiDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.ModuuliLiitosDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteMatalaDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@Slf4j
public class Lops2019ServiceImpl implements Lops2019Service {

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private Lops2019OppiaineService oppiaineService;

    @Autowired
    private Lops2019OpintojaksoService opintojaksoService;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    private Opetussuunnitelma getOpetussuunnitelma(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("ops-ei-loydy");
        }
        return ops;
    }

    private PerusteDto getPerusteImpl(Long opsId) {
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        PerusteCache perusteCached = ops.getCachedPeruste();
        return eperusteetService.getPerusteById(perusteCached.getId());
    }

    @Override
    public PerusteInfoDto getPeruste(Long opsId) {
        return mapper.map(getPerusteImpl(opsId), PerusteInfoDto.class);
    }

    @Override
    public Lops2019SisaltoDto getSisalto(Long opsId) {
        return getPerusteImpl(opsId).getLops2019();
    }

    private List<Lops2019OppiaineDto> getOppiaineetAndOppimaarat(Long opsId) {
        PerusteDto peruste = getPerusteImpl(opsId);
        return peruste.getLops2019().getOppiaineet().stream()
                .map(oa -> Stream.concat(Stream.of(oa), oa.getOppimaarat().stream()))
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }

    private List<Lops2019ModuuliDto> getModuulit(Long opsId) {
        PerusteDto peruste = getPerusteImpl(opsId);
        return peruste.getLops2019().getOppiaineet().stream()
                .map(oa -> Stream.concat(
                        oa.getModuulit().stream(),
                        oa.getOppimaarat().stream()
                                .map(om -> om.getModuulit().stream())
                                .flatMap(Function.identity())))
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }

    @Override
    public List<Lops2019OpintojaksoDto> getOpintojaksot(Long opsId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Lops2019OppiaineDto> getPerusteOppiaineet(Long opsId) {
        PerusteDto perusteDto = getPerusteImpl(opsId);
        return perusteDto.getLops2019()
                .getOppiaineet();
    }

    @Override
    public Lops2019OppiaineDto getPerusteOppiaine(Long opsId, Long oppiaineId) {
        return getOppiaineetAndOppimaarat(opsId).stream()
                .filter(oa -> oppiaineId.equals(oa.getId()))
                .findFirst().orElseThrow(() -> new BusinessRuleViolationException("oppiainetta-ei-loytynyt"));
    }

    @Override
    public Lops2019ModuuliDto getPerusteModuuli(Long opsId, Long oppiaineId, Long moduuliId) {
        List<Lops2019ModuuliDto> moduulit = getOppiaineetAndOppimaarat(opsId).stream()
                .filter((oa) -> Objects.equals(oppiaineId, oa.getId()))
                .findFirst()
                .map(Lops2019OppiaineDto::getModuulit)
                .orElse(new ArrayList<>());
        return moduulit.stream()
            .filter((moduuli) -> Objects.equals(moduuliId, moduuli.getId()))
            .findFirst()
            .orElseThrow(() -> new BusinessRuleViolationException("moduulia-ei-loytynyt"));
    }

    @Override
    public Lops2019ModuuliDto getPerusteModuuli(Long opsId, String koodiUri) {
        return getModuulit(opsId).stream()
                .filter(moduuli -> koodiUri.equals(moduuli.getKoodi().getUri()))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException("moduulia-ei-loytynyt"));
    }

    @Override
    public List<Lops2019ModuuliDto> getOppiaineenModuulit(Long opsId, String oppiaineUri) {
        Lops2019OppiaineDto oppiaine = getPerusteOppiaine(opsId, oppiaineUri);
        return oppiaine.getModuulit();
    }

    @Override
    public Lops2019OppiaineDto getPerusteOppiaine(Long opsId, String koodiUri) {
        return getOppiaineetAndOppimaarat(opsId).stream()
                .filter(oa -> koodiUri.equals(oa.getKoodi().getUri()))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException("oppiainetta-ei-ole"));
    }

    @Override
    public PerusteTekstiKappaleViiteDto getPerusteTekstikappaleet(Long opsId) {
        return getPerusteImpl(opsId).getLops2019().getSisalto();
    }

    @Override
    public PerusteTekstiKappaleViiteMatalaDto getPerusteTekstikappale(Long opsId, Long tekstikappaleId) {
        PerusteTekstiKappaleViiteDto sisalto = getPerusteImpl(opsId).getLops2019().getSisalto();
        return CollectionUtil.treeToStream(
                sisalto,
                PerusteTekstiKappaleViiteDto::getLapset)
                    .filter(viiteDto -> viiteDto.getPerusteenOsa() != null
                            && Objects.equals(tekstikappaleId, viiteDto.getPerusteenOsa().getId()))
                    .findFirst()
                    .orElseThrow(() -> new NotExistsException("tekstikappaletta-ei-ole"));
    }

    @Override
    public Lops2019ValidointiDto getValidointi(Long opsId) {
        Lops2019ValidointiDto validointi = new Lops2019ValidointiDto();

        { // Validoi kiinnitetyt opintojaksot ja käytetyt moduulit
            List<Lops2019OpintojaksoDto> opintojaksot = opintojaksoService.getAll(opsId);

            { // Liitetyt moduulit
                Map<String, List<Lops2019OpintojaksoBaseDto>> liitokset = new HashMap<>();
                for (Lops2019OpintojaksoDto oj : opintojaksot) {
                    for (Lops2019OpintojaksonModuuliDto moduuli : oj.getModuulit()) {
                        if (!liitokset.containsKey(moduuli.getKoodiUri())) {
                            liitokset.put(moduuli.getKoodiUri(), new ArrayList<>());
                        }
                        liitokset.get(moduuli.getKoodiUri()).add(oj);
                    }
                }

                validointi.setLiitetytModuulit(liitokset.entrySet().stream()
                        .map(x -> new ModuuliLiitosDto(x.getKey(), mapper.mapAsList(x.getValue(), Lops2019OpintojaksoBaseDto.class)))
                        .collect(Collectors.toSet()));

            }

            { // Kaikki moduulit
                List<Lops2019OppiaineDto> oppiaineetAndOppimaarat = getOppiaineetAndOppimaarat(opsId);
                validointi.setKaikkiModuulit(oppiaineetAndOppimaarat.stream()
                        .map(x -> x.getModuulit().stream())
                        .flatMap(x -> x)
                        .map(Lops2019ModuuliDto::getKoodi)
                        .collect(Collectors.toSet()));
            }
        }

        { // Validoi opetussuunnitelman / pohjan tiedot

        }

        { // Validoi opetussuunnitelman / pohjan tekstikappaleet

        }

        { // Validoi opetussuunnitelman paikalliset oppiaineet

        }

        return validointi;
    }


}
