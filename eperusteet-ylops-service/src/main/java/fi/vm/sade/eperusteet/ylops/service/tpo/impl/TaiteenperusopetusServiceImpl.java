package fi.vm.sade.eperusteet.ylops.service.tpo.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.tpo.Taiteenala;
import fi.vm.sade.eperusteet.ylops.domain.tpo.Taiteenosa;
import fi.vm.sade.eperusteet.ylops.domain.tpo.TpoSisalto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.TPOOpetuksenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.PerusteTaiteenosaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenosaDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.tpo.TaiteenalaRepository;
import fi.vm.sade.eperusteet.ylops.repository.tpo.TaiteenosaRepository;
import fi.vm.sade.eperusteet.ylops.repository.tpo.TpoSisaltoRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.tpo.TaiteenperusopetusService;

@Service
@Transactional
public class TaiteenperusopetusServiceImpl implements TaiteenperusopetusService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private TpoSisaltoRepository tpoSisaltoRepository;

    @Autowired
    private TaiteenalaRepository taiteenalaRepository;

    @Autowired
    private TaiteenosaRepository taiteenosaRepository;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private DtoMapper mapper;

    @Override
    public List<TaiteenalaDto> getTaiteenalat(Long opsId) {
        return getSisalto(opsId).getTaiteenalat().stream()
                .map(taiteenala -> mapper.map(taiteenala, TaiteenalaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public TaiteenalaDto getTaiteenala(Long opsId, Long taiteenalaId) {
        return mapper.map(getTaiteenalaEntity(opsId, taiteenalaId), TaiteenalaDto.class);
    }

    @Override
    public TaiteenosaDto getTaiteenosa(Long opsId, Long taiteenosaId) {
        return mapper.map(getTaiteenosaEntity(opsId, taiteenosaId), TaiteenosaDto.class);
    }

    @Override
    public TPOOpetuksenSisaltoDto getPerusteSisalto(Long opsId) {
        return getPeruste(getOpetussuunnitelma(opsId)).getTpo();
    }

    @Override
    public TpoPerusteenTaiteenalaDto getPerusteenTaiteenala(Long opsId, String koodiUri) {
        return perusteenTaiteenala(getOpetussuunnitelma(opsId), koodiUri)
                .orElseThrow(() -> new BusinessRuleViolationException("perusteen-taiteenalaa-ei-olemassa"));
    }

    @Override
    public PerusteTaiteenosaDto getPerusteenTaiteenosa(Long opsId, Long perusteenTaiteenosanId) {
        return perusteenTaiteenalat(getOpetussuunnitelma(opsId)).stream()
                .flatMap(taiteenala -> taiteenala.getTaiteenOsat().stream())
                .filter(taiteenosa -> Objects.equals(taiteenosa.getId(), perusteenTaiteenosanId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException("perusteen-taiteenosaa-ei-olemassa"));
    }

    @Override
    public TaiteenalaDto addTaiteenala(Long opsId, TaiteenalaDto taiteenalaDto) {
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        tarkistaKoodi(ops, taiteenalaDto.getKoodi());

        TpoSisalto sisalto = getSisalto(ops);
        if (sisalto.getTaiteenalat().stream()
                .anyMatch(olemassaoleva -> Objects.equals(olemassaoleva.getKoodi(), taiteenalaDto.getKoodi()))) {
            throw new BusinessRuleViolationException("taiteenala-on-jo-lisatty");
        }

        Taiteenala taiteenala = mapper.map(taiteenalaDto, Taiteenala.class);
        taiteenala.setKoodi(taiteenalaDto.getKoodi());
        taiteenala.setTaiteenosat(perusteenTaiteenosat(ops, taiteenalaDto.getKoodi()));
        taiteenala = taiteenalaRepository.save(taiteenala);
        sisalto.addTaiteenala(taiteenala);
        return mapper.map(taiteenala, TaiteenalaDto.class);
    }

    @Override
    public TaiteenalaDto updateTaiteenala(Long opsId, Long taiteenalaId, TaiteenalaDto taiteenalaDto) {
        Taiteenala taiteenala = getTaiteenalaEntity(opsId, taiteenalaId);
        taiteenala = mapper.map(taiteenalaDto, taiteenala);
        taiteenala.updateMuokkaustiedot();
        return mapper.map(taiteenalaRepository.save(taiteenala), TaiteenalaDto.class);
    }

    @Override
    public TaiteenosaDto updateTaiteenosa(Long opsId, Long taiteenosaId, TaiteenosaDto taiteenosaDto) {
        Taiteenosa taiteenosa = getTaiteenosaEntity(opsId, taiteenosaId);
        taiteenosa = mapper.map(taiteenosaDto, taiteenosa);
        taiteenosa.updateMuokkaustiedot();
        return mapper.map(taiteenosaRepository.save(taiteenosa), TaiteenosaDto.class);
    }

    @Override
    public void removeTaiteenala(Long opsId, Long taiteenalaId) {
        TpoSisalto sisalto = getSisalto(opsId);
        sisalto.getTaiteenalat().remove(getTaiteenalaEntity(opsId, taiteenalaId));
    }

    private Opetussuunnitelma getOpetussuunnitelma(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-loytynyt");
        }
        if (!KoulutustyyppiToteutus.TPO.equals(ops.getToteutus())) {
            throw new BusinessRuleViolationException("opetussuunnitelma-vaaran-tyyppinen");
        }
        return ops;
    }

    private TpoSisalto getSisalto(Long opsId) {
        return getSisalto(getOpetussuunnitelma(opsId));
    }

    /**
     * Taiteen perusopetuksen sisältö luodaan tarvittaessa, jotta myös ennen sisällön
     * käyttöönottoa luodut opetussuunnitelmat saadaan muokattaviksi.
     */
    private TpoSisalto getSisalto(Opetussuunnitelma ops) {
        if (ops.getTpo() == null) {
            TpoSisalto sisalto = new TpoSisalto();
            sisalto.setOpetussuunnitelma(ops);
            ops.setTpo(tpoSisaltoRepository.save(sisalto));
        }
        return ops.getTpo();
    }

    private Taiteenala getTaiteenalaEntity(Long opsId, Long taiteenalaId) {
        return taiteenalaRepository.findByIdAndOpetussuunnitelmaId(taiteenalaId, opsId)
                .orElseThrow(() -> new BusinessRuleViolationException("taiteenalaa-ei-ole"));
    }

    private Taiteenosa getTaiteenosaEntity(Long opsId, Long taiteenosaId) {
        return taiteenosaRepository.findByIdAndOpetussuunnitelmaId(taiteenosaId, opsId)
                .orElseThrow(() -> new BusinessRuleViolationException("taiteenosaa-ei-ole"));
    }

    private List<Taiteenosa> perusteenTaiteenosat(Opetussuunnitelma ops, String koodiUri) {
        return perusteenTaiteenala(ops, koodiUri)
                .map(TpoPerusteenTaiteenalaDto::getTaiteenOsat)
                .orElseGet(Collections::emptyList).stream()
                .map(PerusteTaiteenosaDto::getId)
                .filter(Objects::nonNull)
                .map(perusteenTaiteenosanId -> {
                    Taiteenosa taiteenosa = new Taiteenosa();
                    taiteenosa.setPerusteenTaiteenosanId(perusteenTaiteenosanId);
                    return taiteenosa;
                })
                .collect(Collectors.toList());
    }

    private void tarkistaKoodi(Opetussuunnitelma ops, String koodi) {
        if (StringUtils.isEmpty(koodi)) {
            throw new BusinessRuleViolationException("taiteenalan-koodi-puuttuu");
        }

        if (!perusteenTaiteenala(ops, koodi).isPresent()) {
            throw new BusinessRuleViolationException("perusteen-taiteenalaa-ei-olemassa");
        }
    }

    private Optional<TpoPerusteenTaiteenalaDto> perusteenTaiteenala(Opetussuunnitelma ops, String koodiUri) {
        return perusteenTaiteenalat(ops).stream()
                .filter(taiteenala -> taiteenala.getKoodi() != null)
                .filter(taiteenala -> Objects.equals(taiteenala.getKoodi().getUri(), koodiUri))
                .findFirst();
    }

    private List<TpoPerusteenTaiteenalaDto> perusteenTaiteenalat(Opetussuunnitelma ops) {
        TPOOpetuksenSisaltoDto perusteenSisalto = getPeruste(ops).getTpo();
        if (perusteenSisalto == null) {
            throw new BusinessRuleViolationException("perusteen-taiteenaloja-ei-loytynyt");
        }
        return perusteenSisalto.getTaiteenalat();
    }

    private PerusteDto getPeruste(Opetussuunnitelma ops) {
        PerusteCache cache = ops.getCachedPeruste();
        if (cache == null) {
            throw new BusinessRuleViolationException("peruste-cache-puuttuu");
        }
        return eperusteetService.getPerusteById(cache.getPerusteId());
    }
}
