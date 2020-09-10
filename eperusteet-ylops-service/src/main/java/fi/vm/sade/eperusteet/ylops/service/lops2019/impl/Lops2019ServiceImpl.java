package fi.vm.sade.eperusteet.ylops.service.lops2019.impl;

import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019LaajaAlainenDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019LaajaAlainenOsaaminenDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteMatalaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.Lops2019SisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksoRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OppiaineRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.KoodistoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private Lops2019OppiaineRepository oppiaineRepository;

    @Autowired
    private Lops2019OpintojaksoService opintojaksoService;

    @Autowired
    private Lops2019OpintojaksoRepository opintojaksoRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private KoodistoService koodistoService;

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
        if (perusteCached == null) {
            throw new BusinessRuleViolationException("peruste-cache-puuttuu");
        }
        return eperusteetService.getPerusteById(perusteCached.getPerusteId());
    }

    @Override
    public PerusteInfoDto getPeruste(Long opsId) {
        return mapper.map(getPerusteImpl(opsId), PerusteInfoDto.class);
    }

    @Override
    public Lops2019SisaltoDto getPerusteSisalto(Long opsId) {
        PerusteDto perusteDto = getPerusteImpl(opsId);
        return perusteDto.getLops2019();
    }

    @Override
    public List<Lops2019OpintojaksoDto> getOpintojaksot(Long opsId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Lops2019OppiaineKaikkiDto> getPerusteOppiaineet(Long opsId) {
        PerusteDto perusteDto = getPerusteImpl(opsId);
        return perusteDto.getLops2019()
                .getOppiaineet();
    }

    @Override
    public List<Lops2019OppiaineKaikkiDto> getPerusteOppiaineetAndOppimaarat(Long opsId) {
        PerusteDto peruste = getPerusteImpl(opsId);
        return peruste.getLops2019().getOppiaineet().stream()
                .map(oa -> Stream.concat(Stream.of(oa), oa.getOppimaarat().stream()))
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }

    @Override
    public List<Lops2019ModuuliDto> getPerusteModuulit(Long opsId) {
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
    public Lops2019OppiaineKaikkiDto getPerusteOppiaine(Long opsId, Long oppiaineId) {
        return getPerusteOppiaineetAndOppimaarat(opsId).stream()
                .filter(oa -> oppiaineId.equals(oa.getId()))
                .findFirst().orElseThrow(() -> new BusinessRuleViolationException("oppiainetta-ei-loytynyt"));
    }

    @Override
    public Set<Lops2019OppiaineKaikkiDto> getPerusteenOppiaineet(Long opsId, Set<String> koodiUrit) {
        return getPerusteOppiaineetAndOppimaarat(opsId).stream()
                .filter(oa -> koodiUrit.contains(oa.getKoodi().getUri()))
                .collect(Collectors.toSet());
    }

    @Override
    public Lops2019ModuuliDto getPerusteModuuli(Long opsId, Long oppiaineId, Long moduuliId) {
        List<Lops2019ModuuliDto> moduulit = getPerusteOppiaineetAndOppimaarat(opsId).stream()
                .filter((oa) -> Objects.equals(oppiaineId, oa.getId()))
                .findFirst()
                .map(Lops2019OppiaineKaikkiDto::getModuulit)
                .orElse(new ArrayList<>());
        return moduulit.stream()
            .filter((moduuli) -> Objects.equals(moduuliId, moduuli.getId()))
            .findFirst()
            .orElseThrow(() -> new BusinessRuleViolationException("moduulia-ei-loytynyt"));
    }

    @Override
    public Lops2019ModuuliDto getPerusteModuuli(Long opsId, String koodiUri) {
        return getPerusteModuulit(opsId).stream()
                .filter(moduuli -> koodiUri.equals(moduuli.getKoodi().getUri()))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException("moduulia-ei-loytynyt"));
    }

    @Override
    public Set<Lops2019ModuuliDto> getPerusteModuulit(Long opsId, Set<String> koodiUrit) {
        return getPerusteModuulit(opsId).stream()
                .filter(moduuli -> koodiUrit.contains(moduuli.getKoodi().getUri()))
                .collect(Collectors.toSet());
    }

    @Override
    public List<Lops2019ModuuliDto> getPerusteOppiaineenModuulit(Long opsId, String oppiaineUri) {
        Lops2019OppiaineKaikkiDto oppiaine = getPerusteOppiaine(opsId, oppiaineUri);
        return oppiaine.getModuulit();
    }

    @Override
    public Lops2019OppiaineKaikkiDto getPerusteOppiaine(Long opsId, String koodiUri) {
        return getPerusteOppiaineetAndOppimaarat(opsId).stream()
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
        PerusteDto perusteDto = getPerusteImpl(opsId);
        if (perusteDto.getLops2019() != null) {
            PerusteTekstiKappaleViiteDto sisalto = perusteDto.getLops2019().getSisalto();
            return CollectionUtil.treeToStream(
                    sisalto,
                    PerusteTekstiKappaleViiteDto::getLapset)
                    .filter(viiteDto -> viiteDto.getPerusteenOsa() != null
                            && Objects.equals(tekstikappaleId, viiteDto.getPerusteenOsa().getId()))
                    .findFirst()
                    .orElseThrow(() -> new NotExistsException("tekstikappaletta-ei-ole"));
        }
        throw new NotExistsException("tekstikappaletta-ei-ole");
    }

    @Override
    public Map<String, List<Lops2019OpintojaksoDto>> getModuuliToOpintojaksoMap(List<Lops2019OpintojaksoDto> opintojaksot) {
        Map<String, List<Lops2019OpintojaksoDto>> liitokset = new HashMap<>();
        for (Lops2019OpintojaksoDto oj : opintojaksot) {
            for (Lops2019OpintojaksonModuuliDto moduuli : oj.getModuulit()) {
                if (!liitokset.containsKey(moduuli.getKoodiUri())) {
                    liitokset.put(moduuli.getKoodiUri(), new ArrayList<>());
                }
                liitokset.get(moduuli.getKoodiUri()).add(oj);
            }
        }
        return liitokset;
    }

    @Override
    public Lops2019LaajaAlainenOsaaminenDto getLaajaAlaisetOsaamiset(Kieli kieli) {

        List<KoodistoKoodiDto> laajaAlaisetkoodit = koodistoService.getAll("laajaalainenosaaminenlops2021");

        Lops2019LaajaAlainenOsaaminenDto laajaAlaisetOsaamiset = new Lops2019LaajaAlainenOsaaminenDto();
        laajaAlaisetOsaamiset.setLaajaAlaisetOsaamiset(laajaAlaisetkoodit.stream()
                .map(laajaAlainenKoodi -> Lops2019LaajaAlainenDto
                        .of("laajaalainenosaaminenlops2021", laajaAlainenKoodi.getKoodiArvo(), laajaAlainenKoodi.getNimi().get(kieli)))
                .collect(Collectors.toList()));

        return laajaAlaisetOsaamiset;
    }

}
