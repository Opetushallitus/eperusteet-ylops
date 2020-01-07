package fi.vm.sade.eperusteet.ylops.service.lops2019.impl;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.*;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.*;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.util.UpdateWrapperDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class Lops2019OppiaineServiceImpl implements Lops2019OppiaineService {

    @Autowired
    private Lops2019PoistetutRepository poistetutRepository;

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

    private Opetussuunnitelma getOpetussuunnitelma(@P("opsId") Long opsId) {
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
    public List<Lops2019PaikallinenOppiaineDto> getAll(Long opsId) {
        List<Lops2019Oppiaine> oppiaineet = oppiaineRepository.findAllBySisalto(getOpetussuunnitelma(opsId).getLops2019());
        return mapper.mapAsList(oppiaineet, Lops2019PaikallinenOppiaineDto.class);
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
    public Lops2019PaikallinenOppiaineDto addOppiaine(Long opsId, Lops2019PaikallinenOppiaineDto oppiaineDto) {
        Opetussuunnitelma opetussuunnitelma = getOpetussuunnitelma(opsId);
        Lops2019Oppiaine oppiaine = mapper.map(oppiaineDto, Lops2019Oppiaine.class);
        oppiaine.setId(null);
        oppiaine.setSisalto(opetussuunnitelma.getLops2019());
        oppiaine = oppiaineRepository.save(oppiaine);
        return mapper.map(oppiaine, Lops2019PaikallinenOppiaineDto.class);
    }

    @Override
    public Lops2019PaikallinenOppiaineDto updateOppiaine(Long opsId, Long oppiaineId, UpdateWrapperDto<Lops2019PaikallinenOppiaineDto> oppiaineDto) {
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        Lops2019Oppiaine oppiaine = getOppiaine(opsId, oppiaineId);
        String oppiaineenKoodi = oppiaine.getKoodi();
        mapper.map(oppiaineDto.getData(), oppiaine);
        oppiaine = oppiaineRepository.save(oppiaine);
        if (!Objects.equals(oppiaineenKoodi, oppiaineDto.getData().getKoodi())) {
            for (Lops2019OpintojaksonOppiaine ojOa : opintojaksonOppiaineRepository
                    .findAllByKoodi(oppiaineenKoodi)) {
                ojOa.setKoodi(oppiaine.getKoodi());
                opintojaksonOppiaineRepository.save(ojOa);
            }
        }

        return mapper.map(oppiaine, Lops2019PaikallinenOppiaineDto.class);
    }

    @Override
    public void removeOne(Long opsId, Long oppiaineId) {
        Lops2019Oppiaine oppiaine = getOppiaine(opsId, oppiaineId);
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        Lops2019Poistettu poistettu = new Lops2019Poistettu();
        poistettu.setNimi(oppiaine.getNimi());
        poistettu.setOpetussuunnitelma(ops);
        poistettu.setPoistettu_id(oppiaineId);
        poistettu.setPalautettu(false);
        poistettu.setTyyppi(PoistetunTyyppi.LOPS2019OPPIAINE);
        poistetutRepository.save(poistettu);
        oppiaineRepository.delete(oppiaine);
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
        throw new UnsupportedOperationException("not implemented yet");
    }
}
