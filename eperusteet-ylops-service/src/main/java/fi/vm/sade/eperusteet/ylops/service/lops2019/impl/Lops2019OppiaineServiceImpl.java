package fi.vm.sade.eperusteet.ylops.service.lops2019.impl;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.*;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineJarjestysDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksoRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksonOppiaineRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OppiaineRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019SisaltoRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.PoistetutRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.PoistoService;
import fi.vm.sade.eperusteet.ylops.service.util.KoodiValidator;
import fi.vm.sade.eperusteet.ylops.service.util.UpdateWrapperDto;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

@Service
@Transactional
public class Lops2019OppiaineServiceImpl implements Lops2019OppiaineService {

    @Autowired
    private PoistoService poistoService;

    @Autowired
    private Lops2019OpintojaksoRepository opintojaksoRepository;

    @Autowired
    private Lops2019OppiaineRepository oppiaineRepository;

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
    private Lops2019OpintojaksonOppiaineRepository opintojaksonOppiaineRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkaustietoService;

    @Autowired
    private PoistetutRepository poistetutRepository;

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
    public <T> List<T> getAll(Long opsId, Class<T> clz) {
        List<Lops2019Oppiaine> oppiaineet = oppiaineRepository.findAllBySisalto(getOpetussuunnitelma(opsId).getLops2019());
        Opetussuunnitelma opetussuunnitelma = getOpetussuunnitelma(opsId);
        oppiaineet.addAll(getTuodut(opetussuunnitelma));
        return mapper.mapAsList(oppiaineet, clz);
    }

    @Override
    public List<Lops2019Oppiaine> getTuodut(Opetussuunnitelma opetussuunnitelma) {
        List<Lops2019Oppiaine> oppiaineet = new ArrayList<>();

        Set<Long> poistetut = poistetutRepository.findAllByOpetussuunnitelmaAndTyyppi(opetussuunnitelma, PoistetunTyyppi.TUOTU_OPPIMAARA).stream()
                .map(Poistettu::getPoistettuId)
                .collect(Collectors.toSet());

        while (opetussuunnitelma.getPohja() != null && opetussuunnitelma.isTuoPohjanOppimaarat()) {
            List<Lops2019Oppiaine> pohjanOppiaineet = oppiaineRepository.findAllBySisalto(opetussuunnitelma.getPohja().getLops2019()).stream()
                    .filter(oj -> !poistetut.contains(oj.getId()))
                    .collect(Collectors.toList());
            oppiaineet.addAll(pohjanOppiaineet);
            opetussuunnitelma = opetussuunnitelma.getPohja();
        }

        return oppiaineet;
    }

    private Lops2019Oppiaine getOppiaine(Long opsId, Long oppiaineId) {
        return oppiaineRepository.getOneBySisalto(getOpetussuunnitelma(opsId).getLops2019(), oppiaineId)
                .orElseThrow(() -> new NotExistsException("oppiainetta-ei-ole"));
    }

    @Override
    public Lops2019PaikallinenOppiaineDto getOne(Long opsId, Long oppiaineId) {
        return mapper.map(getOppiaine(opsId, oppiaineId), Lops2019PaikallinenOppiaineDto.class);
    }

    @Override
    public Lops2019PaikallinenOppiaineDto getTuotu(Long opsId, Long oppiaineId) {
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        Optional<Lops2019Oppiaine> tuotuOppiaine = getTuodut(ops).stream().filter(tuotu -> tuotu.getId().equals(oppiaineId)).findFirst();
        if (tuotuOppiaine.isPresent()) {
            return mapper.map(tuotuOppiaine.get(), Lops2019PaikallinenOppiaineDto.class);
        }

        return null;
    }

    @Override
    public Lops2019PaikallinenOppiaineDto addOppiaine(
            Long opsId,
            Lops2019PaikallinenOppiaineDto oppiaineDto
    ) {
        return this.addOppiaine(opsId, oppiaineDto, null);
    }

    @Override
    public Lops2019PaikallinenOppiaineDto addOppiaine(
            Long opsId,
            Lops2019PaikallinenOppiaineDto oppiaineDto,
            MuokkausTapahtuma tapahtuma
    ) {
        KoodiValidator.validate(oppiaineDto.getKoodi());
        Opetussuunnitelma opetussuunnitelma = getOpetussuunnitelma(opsId);
        Lops2019Oppiaine oppiaine = mapper.map(oppiaineDto, Lops2019Oppiaine.class);
        oppiaine.setId(null);
        oppiaine.setSisalto(opetussuunnitelma.getLops2019());
        oppiaine = oppiaineRepository.save(oppiaine);

        muokkaustietoService.addOpsMuokkausTieto(opsId,
                oppiaine,
                tapahtuma != null ? tapahtuma : MuokkausTapahtuma.LUONTI);
        return mapper.map(oppiaine, Lops2019PaikallinenOppiaineDto.class);
    }

    @Override
    public Lops2019PaikallinenOppiaineDto updateOppiaine(
            Long opsId,
            Long oppiaineId,
            UpdateWrapperDto<Lops2019PaikallinenOppiaineDto> oppiaineDto
    ) {
        return this.updateOppiaine(opsId, oppiaineId, oppiaineDto, null);
    }

    @Override
    public Lops2019PaikallinenOppiaineDto updateOppiaine(
            Long opsId,
            Long oppiaineId,
            UpdateWrapperDto<Lops2019PaikallinenOppiaineDto> oppiaineDto,
            MuokkausTapahtuma tapahtuma
    ) {
        KoodiValidator.validate(oppiaineDto.getData().getKoodi());
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        Lops2019Oppiaine oppiaine = getOppiaine(opsId, oppiaineId);
        String oppiaineenKoodi = oppiaine.getKoodi();
        mapper.map(oppiaineDto.getData(), oppiaine);
        oppiaine.updateMuokkaustiedot();
        oppiaine = oppiaineRepository.save(oppiaine);

        if (!Objects.equals(oppiaineenKoodi, oppiaineDto.getData().getKoodi())) {
            for (Lops2019OpintojaksonOppiaine ojOa : sisaltoRepository.findOpintojaksonOppiaineetByOpetussuunnitelma(opsId, oppiaineenKoodi)) {
                ojOa.setKoodi(oppiaine.getKoodi());
                opintojaksonOppiaineRepository.save(ojOa);
            }
        }

        muokkaustietoService.addOpsMuokkausTieto(opsId, oppiaine, tapahtuma != null ? tapahtuma : MuokkausTapahtuma.PAIVITYS);
        return mapper.map(oppiaine, Lops2019PaikallinenOppiaineDto.class);
    }

    @Override
    public List<Lops2019OppiaineJarjestysDto> getOppiaineJarjestys(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");

        Lops2019Sisalto lops2019Sisalto = ops.getLops2019();
        if (lops2019Sisalto != null) {
            return mapper.mapAsList(new ArrayList<>(lops2019Sisalto
                    .getOppiaineJarjestykset()), Lops2019OppiaineJarjestysDto.class);
        } else {
            return new ArrayList<>();
        }
    }

    private Set<Lops2019Opintojakso> getOpintojaksot(Lops2019Oppiaine oa) {
        return oa.getSisalto().getOpintojaksot().stream()
                .filter(oj -> oj.getOppiaineet().stream()
                            .map(Lops2019OpintojaksonOppiaine::getKoodi)
                            .anyMatch(koodi -> Objects.equals(koodi, oa.getKoodi())))
                .collect(Collectors.toSet());
    }

    private boolean omistaaOpintojaksoja(Lops2019Oppiaine oa) {
        return !getOpintojaksot(oa).isEmpty();
    }

    @Override
    public void removeOne(Long opsId, Long oppiaineId) {
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);

        Optional<Lops2019Oppiaine> tuotuOppiaine = getTuodut(ops).stream().filter(tuotu -> tuotu.getId().equals(oppiaineId)).findFirst();
        if (tuotuOppiaine.isPresent()) {
            poistoService.remove(ops, tuotuOppiaine.get(), PoistetunTyyppi.TUOTU_OPPIMAARA);
            muokkaustietoService.addOpsMuokkausTieto(opsId, tuotuOppiaine.get(), MuokkausTapahtuma.POISTO);
        } else {
            Lops2019Oppiaine oppiaine = getOppiaine(opsId, oppiaineId);
            if (omistaaOpintojaksoja(oppiaine)) {
                throw new BusinessRuleViolationException("oppaine-sisaltaa-opintojaksoja");
            }

            oppiaine.updateMuokkaustiedot();
            poistoService.remove(ops, oppiaine);
            oppiaineRepository.delete(oppiaine);
            muokkaustietoService.addOpsMuokkausTieto(opsId, oppiaine, MuokkausTapahtuma.POISTO);
        }
    }

    @Override
    public List<RevisionDto> getVersions(Long opsId, Long oppiaineId) {
        getOppiaine(opsId, oppiaineId);
        return mapper.mapAsList(oppiaineRepository.getRevisions(oppiaineId), RevisionDto.class).stream()
                .peek(rev -> {
                    String nimi = kayttajanTietoService.haeKayttajanimi(rev.getMuokkaajaOid());
                    rev.setNimi(nimi);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PoistettuDto> getRemoved(Long opsId) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Lops2019PaikallinenOppiaineDto getVersion(Long opsId, Long oppiaineId, Integer versio) {
        getOppiaine(opsId, oppiaineId);
        Lops2019Oppiaine revision = oppiaineRepository.findRevision(oppiaineId, versio);
        return mapper.map(revision, Lops2019PaikallinenOppiaineDto.class);
    }

    @Override
    public Lops2019PaikallinenOppiaineDto revertTo(Long opsId, Long oppiaineId, Integer versio) {
        getOppiaine(opsId, oppiaineId);
        Lops2019Oppiaine revision = oppiaineRepository.findRevision(oppiaineId, versio);
        Lops2019PaikallinenOppiaineDto dto = mapper.map(revision, Lops2019PaikallinenOppiaineDto.class);
        UpdateWrapperDto<Lops2019PaikallinenOppiaineDto> wrapperDto = new UpdateWrapperDto<>();
        wrapperDto.setData(dto);
        return updateOppiaine(opsId, oppiaineId, wrapperDto, MuokkausTapahtuma.PALAUTUS);
    }
}
