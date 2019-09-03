package fi.vm.sade.eperusteet.ylops.service.lops2019.impl;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.*;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.*;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OppiaineRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019PoistetutRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksoRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019SisaltoRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.util.UpdateWrapperDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class Lops2019OpintojaksoServiceImpl implements Lops2019OpintojaksoService {

    @Autowired
    private Lops2019OppiaineRepository oppiaineRepository;

    @Autowired
    private Lops2019PoistetutRepository poistetutRepository;

    @Autowired
    private Lops2019OpintojaksoRepository opintojaksoRepository;

    @Autowired
    private Lops2019SisaltoRepository sisaltoRepository;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private Lops2019Service lopsService;

    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private EntityManager em;

    private Opetussuunnitelma getOpetussuunnitelma(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-loytynyt");
        }
        else if (!KoulutustyyppiToteutus.LOPS2019.equals(ops.getToteutus())) {
            throw new BusinessRuleViolationException("opetussuunnitelma-vaaran-tyyppinen");
        }
        else {
            return ops;
        }
    }

    @Override
    public long getOpintojaksonLaajuus(Long opsId, Lops2019OpintojaksoDto opintojakso) {
        if (opintojakso.getModuulit().isEmpty()) {
            return opintojakso.getOppiaineet().stream()
                .filter(Objects::nonNull)
                .map(Lops2019OpintojaksonOppiaineDto::getLaajuus)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
        }
        else {
            Set<String> moduulikoodit = opintojakso.getModuulit().stream()
                    .map(Lops2019OpintojaksonModuuliDto::getKoodiUri)
                    .collect(Collectors.toSet());
            Set<Lops2019ModuuliDto> perusteModuulit = lopsService.getPerusteModuulit(opsId, moduulikoodit);
            return perusteModuulit.stream()
                    .map(Lops2019ModuuliDto::getLaajuus)
                    .mapToLong(Integer::longValue)
                    .sum();
        }
    }

    private Lops2019Opintojakso getOpintojakso(Long opsId, Long opintojaksoId) {
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        Lops2019Opintojakso opintojakso = ops.getLops2019().getOpintojakso(opintojaksoId);
        if (opintojakso == null) {
            throw new BusinessRuleViolationException("opintojaksoa-ei-ole");
        }
        return opintojakso;
    }

    @Override
    public List<Lops2019OpintojaksoDto> getAll(Long opsId) {
        Opetussuunnitelma opetussuunnitelma = getOpetussuunnitelma(opsId);
        return opetussuunnitelma.getLops2019().getOpintojaksot().stream()
                .map(oj -> mapper.map(oj, Lops2019OpintojaksoDto.class))
                .map(oj -> {
                    oj.setLaajuus(getOpintojaksonLaajuus(opsId, oj));
                    return oj;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Lops2019OpintojaksoDto getOne(Long opsId, Long opintojaksoId) {
        Lops2019Opintojakso opintojakso = getOpintojakso(opsId, opintojaksoId);
        Lops2019OpintojaksoDto result = mapper.map(opintojakso, Lops2019OpintojaksoDto.class);
        result.setLaajuus(getOpintojaksonLaajuus(opsId, result));
        return result;
    }

    @Override
    public Lops2019OpintojaksoDto addOpintojakso(Long opsId, Lops2019OpintojaksoDto opintojaksoDto) {
        opintojaksoDto.setId(null);
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        Set<Lops2019ModuuliDto> loydetytModuulit = lopsService.getPerusteModuulit(opsId, opintojaksoDto.getModuulit().stream()
                .map(Lops2019OpintojaksonModuuliDto::getKoodiUri)
                .collect(Collectors.toSet()));

        if (opintojaksoDto.getModuulit() != null && opintojaksoDto.getModuulit().size() != loydetytModuulit.size()) {
            throw new BusinessRuleViolationException("perusteen-moduulia-ei-olemassa");
        }

        if (opintojaksoDto.getOppiaineet() == null) {
            throw new BusinessRuleViolationException("perusteen-oppiainetta-ei-olemassa");
        }

        Set<Lops2019OppiaineDto> perusteenOppiaineet = lopsService.getPerusteenOppiaineet(opsId, opintojaksoDto.getOppiaineet().stream()
                .map(Lops2019OpintojaksonOppiaineDto::getKoodi)
                .collect(Collectors.toSet()));

        Set<String> oppiaineKoodit = Stream.concat(
                oppiaineRepository.findAllBySisalto(ops.getLops2019()).stream()
                    .map(Lops2019Oppiaine::getKoodi),
                perusteenOppiaineet.stream()
                        .flatMap(x -> Stream.concat(Stream.of(x), x.getOppimaarat().stream()))
                        .map(oa -> oa.getKoodi().getUri()))
                .collect(Collectors.toSet());

        if (!oppiaineKoodit.containsAll(opintojaksoDto.getOppiaineet().stream()
                .map(Lops2019OpintojaksonOppiaineDto::getKoodi)
                .collect(Collectors.toSet()))) {
            throw new BusinessRuleViolationException("oppiainetta-ei-ole");
        }

        if (perusteenOppiaineet.stream().anyMatch(oa -> !oa.getOppimaarat().isEmpty())) {
            throw new BusinessRuleViolationException("opintojaksoon-ei-voi-liittaa-abstraktia-oppiainetta");
        }

        { // Tarkistetaan että moduulit ovat oikeista oppiaineista
            Set<String> moduulikoodit = loydetytModuulit.stream()
                    .map(Lops2019ModuuliDto::getKoodi)
                    .map(KoodiDto::getUri)
                    .collect(Collectors.toSet());
            perusteenOppiaineet.forEach(oa -> {
                moduulikoodit.removeAll(oa.getModuulit().stream()
                        .map(Lops2019ModuuliDto::getKoodi)
                        .map(KoodiDto::getUri)
                        .collect(Collectors.toSet()));
            });

            if (!moduulikoodit.isEmpty()) {
                throw new BusinessRuleViolationException("liitetyt-moduulit-tulee-loytya-opintojakson-oppiaineilta");
            }
        }

        Lops2019Opintojakso opintojakso = mapper.map(opintojaksoDto, Lops2019Opintojakso.class);
        opintojakso = opintojaksoRepository.save(opintojakso);
        ops.getLops2019().addOpintojakso(opintojakso);
        Lops2019OpintojaksoDto result = mapper.map(opintojakso, Lops2019OpintojaksoDto.class);
        result.setLaajuus(getOpintojaksonLaajuus(opsId, result));
        return result;
    }

    @Override
    public Lops2019OpintojaksoDto updateOpintojakso(Long opsId, Long opintojaksoId, UpdateWrapperDto<Lops2019OpintojaksoDto> opintojaksoDto) {
        opintojaksoDto.getData().setId(opintojaksoId);
        Lops2019Opintojakso opintojakso = getOpintojakso(opsId, opintojaksoId);
        opintojakso = mapper.map(opintojaksoDto.getData(), opintojakso);
        opintojakso = opintojaksoRepository.save(opintojakso);
        Lops2019OpintojaksoDto result = mapper.map(opintojakso, Lops2019OpintojaksoDto.class);
        result.setLaajuus(getOpintojaksonLaajuus(opsId, result));
        return result;
    }

    @Override
    public void removeOne(Long opsId, Long opintojaksoId) {
        Lops2019Opintojakso opintojakso = getOpintojakso(opsId, opintojaksoId);
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        Lops2019Poistettu poistettu = new Lops2019Poistettu();
        poistettu.setNimi(opintojakso.getNimi());
        poistettu.setOpetussuunnitelma(ops);
        poistettu.setPoistettu_id(opintojaksoId);
        poistettu.setPalautettu(false);
        poistettu.setTyyppi(PoistetunTyyppi.OPINTOJAKSO);
        poistetutRepository.save(poistettu);
        ops.getLops2019().getOpintojaksot().remove(opintojakso);
    }

    @Override
    public List<RevisionDto> getVersions(Long opsId, Long opintojaksoId) {
        getOpintojakso(opsId, opintojaksoId);
        return mapper.mapAsList(opintojaksoRepository.getRevisions(opintojaksoId), RevisionDto.class).stream()
                .peek(rev -> {
                    String nimi = kayttajanTietoService.haeKayttajanimi(rev.getMuokkaajaOid());
                    rev.setNimi(nimi);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Lops2019OpintojaksoDto getVersion(Long opsId, Long opintojaksoId, Integer versio) {
        getOpintojakso(opsId, opintojaksoId);
        Lops2019Opintojakso revision = opintojaksoRepository.findRevision(opintojaksoId, versio);
        return mapper.map(revision, Lops2019OpintojaksoDto.class);
    }

    @Override
    public Lops2019OpintojaksoDto revertTo(Long opsId, Long opintojaksoId, Integer versio) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Lops2019OpintojaksoPerusteDto getOpintojaksonPeruste(Long opsId, Long opintojaksoId) {
        Lops2019Opintojakso opintojakso = getOpintojakso(opsId, opintojaksoId);
        Lops2019OpintojaksoPerusteDto opintojaksoPerusteDto = new Lops2019OpintojaksoPerusteDto();
        opintojaksoPerusteDto.setModuulit(opintojakso.getModuulit().stream()
                .map(Lops2019OpintojaksonModuuli::getKoodiUri)
                .map(koodi -> lopsService.getPerusteModuuli(opsId, koodi))
                .collect(Collectors.toList()));
        return opintojaksoPerusteDto;
    }
}
