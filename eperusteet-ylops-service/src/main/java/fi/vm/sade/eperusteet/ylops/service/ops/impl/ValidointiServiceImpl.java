package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.ValidationCategory;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.Lops2019ValidointiDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.ValidointiContext;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksoRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OppiaineRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.ValidointiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@Slf4j
public class ValidointiServiceImpl implements ValidointiService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private Lops2019OppiaineService oppiaineService;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private Lops2019OppiaineRepository oppiaineRepository;

    @Autowired
    private Lops2019OpintojaksoService opintojaksoService;

    @Autowired
    private Lops2019Service lops2019Service;

    @Autowired
    private Lops2019OpintojaksoRepository opintojaksoRepository;

    @Autowired
    private DtoMapper mapper;

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
    public Lops2019ValidointiDto getValidointi(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-ole");
        }

        Lops2019ValidointiDto validointi = new Lops2019ValidointiDto(mapper);
        ValidointiContext ctx = new ValidointiContext();
        ctx.setKielet(ops.getJulkaisukielet());

        List<Lops2019OpintojaksoDto> opintojaksot = opintojaksoService.getAll(opsId);
//        Map<String, Lops2019OpintojaksoDto> opintojaksotMap = opintojaksot.stream()
//                .collect(Collectors.toMap(
//                        Lops2019OpintojaksoBaseDto::getKoodi,
//                        Function.identity()));
//        List<Lops2019OppiaineDto> oppiaineetAndOppimaarat = lops2019Service.getPerusteOppiaineetAndOppimaarat(opsId);
        List<Lops2019ModuuliDto> moduulit = lops2019Service.getPerusteModuulit(ops.getId());
        Map<String, Lops2019ModuuliDto> moduulitMap = moduulit.stream().collect(Collectors.toMap(m -> m.getKoodi().getUri(), Function.identity()));
        Map<String, List<Lops2019OpintojaksoDto>> liitokset = lops2019Service.getModuuliToOpintojaksoMap(opintojaksot);

//        { // Validoi kiinnitetyt opintojaksot ja käytetyt moduulit
//            { // Liitetyt moduulit
//                validointi.setLiitetytModuulit(liitokset.entrySet().stream()
//                        .map(x -> new ModuuliLiitosDto(x.getKey(), mapper.mapAsList(x.getValue(), Lops2019OpintojaksoBaseDto.class)))
//                        .collect(Collectors.toSet()));
//            }
//
//            { // Kaikki moduulit
//                validointi.setKaikkiModuulit(oppiaineetAndOppimaarat.stream()
//                        .map(x -> x.getModuulit().stream())
//                        .flatMap(x -> x)
//                        .map(Lops2019ModuuliDto::getKoodi)
//                        .collect(Collectors.toSet()));
//            }
//        }

        ops.validate(validointi, ctx);

        if (!ops.isAinepainoitteinen()) {
            moduulit.forEach(moduuli -> {
                List<Lops2019OpintojaksoDto> moduulinOpintojaksot = liitokset.getOrDefault(
                        moduuli.getKoodi().getUri(),
                        new ArrayList<>());

                // - Moduuli vähintään yhdessä opintojaksossa
                validointi.virhe(ValidationCategory.MODUULI, "moduuli-kuuluttava-vahintaan-yhteen-opintojaksoon", moduuli.getId(), moduuli.getNimi(),
                        moduulinOpintojaksot.isEmpty());

                // - Pakollinen moduuli vähintään yhdessä opintojaksossa missä on vain muita saman oppiaineen pakollisia
                validointi.virhe(ValidationCategory.MODUULI, "pakollinen-moduuli-mahdollista-suorittaa-erillaan", moduuli.getId(), moduuli.getNimi(),
                        moduulinOpintojaksot.stream()
                                .anyMatch(oj -> oj.getOppiaineet().size() == 1 && oj.getModuulit().stream()
                                        .allMatch(ojm -> moduulitMap.get(ojm.getKoodiUri()).isPakollinen())));

                // - Valinnainen moduuli vähintään yhdessä opintojaksossa suoritettavissa kahden opintopisteen kokonaisuutena
                validointi.virhe(ValidationCategory.MODUULI, "valinnainen-moduuli-suoritettavissa-kahden-opintopisteen-kokonaisuutena", moduuli.getId(), moduuli.getNimi(),
                        !moduuli.isPakollinen() && moduulinOpintojaksot.stream()
                                .anyMatch(oj -> oj.getModuulit().stream().allMatch(ojm -> oj.getLaajuus() == 2L)));
            });
        }

        { // Opintojaksot
            ops.getLops2019().getOpintojaksot().forEach(oj -> oj.validate(validointi, ctx));

            // Opintojaksojen laajuus
            opintojaksot.forEach(oj -> {
                validointi.virhe(ValidationCategory.OPINTOJAKSO, "opintojakson-laajuus-1-4", oj.getId(), oj.getNimi(),
                        oj.getLaajuus() < 1L || oj.getLaajuus() > 4L);
            });
        }

        // Onko paikallinen oppiaine vähintään yhdessä opintojaksossa
        oppiaineRepository.findAllBySisalto(ops.getLops2019()).forEach(oa -> {
            oa.validate(validointi, ctx);
            validointi.virhe("oppiaineesta-opintojakso", oa,
                    opintojaksot.stream().anyMatch(oj -> !oj.getOppiaineet().stream()
                            .map(Lops2019OpintojaksonOppiaineDto::getKoodi)
                            .collect(Collectors.toSet())
                            .contains(oa.getKoodi())));
        });

        return validointi;
    }
}
