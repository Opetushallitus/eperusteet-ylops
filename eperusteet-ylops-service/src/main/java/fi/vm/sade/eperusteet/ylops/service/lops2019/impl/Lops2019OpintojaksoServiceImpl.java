package fi.vm.sade.eperusteet.ylops.service.lops2019.impl;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Opintojakso;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019OpintojaksonModuuli;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Sisalto;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.revision.Revision;
import fi.vm.sade.eperusteet.ylops.dto.PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoPerusteDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksoRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019SisaltoRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.util.UpdateWrapperDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class Lops2019OpintojaksoServiceImpl implements Lops2019OpintojaksoService {

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
        return mapper.mapAsList(opetussuunnitelma.getLops2019().getOpintojaksot(), Lops2019OpintojaksoDto.class);
    }

    @Override
    public Lops2019OpintojaksoDto getOne(Long opsId, Long opintojaksoId) {
        Lops2019Opintojakso opintojakso = getOpintojakso(opsId, opintojaksoId);
        return mapper.map(opintojakso, Lops2019OpintojaksoDto.class);
    }

    @Override
    public Lops2019OpintojaksoDto addOpintojakso(Long opsId, Lops2019OpintojaksoDto opintojaksoDto) {
        opintojaksoDto.setId(null);
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        Lops2019Opintojakso opintojakso = mapper.map(opintojaksoDto, Lops2019Opintojakso.class);
        opintojakso = opintojaksoRepository.save(opintojakso);
        ops.getLops2019().addOpintojakso(opintojakso);
        return mapper.map(opintojakso, Lops2019OpintojaksoDto.class);
    }

    @Override
    public Lops2019OpintojaksoDto updateOpintojakso(Long opsId, Long opintojaksoId, UpdateWrapperDto<Lops2019OpintojaksoDto> opintojaksoDto) {
        opintojaksoDto.getData().setId(opintojaksoId);
        Lops2019Opintojakso opintojakso = getOpintojakso(opsId, opintojaksoId);
        opintojakso = mapper.map(opintojaksoDto.getData(), opintojakso);
        opintojakso = opintojaksoRepository.save(opintojakso);
        return mapper.map(opintojakso, Lops2019OpintojaksoDto.class);
    }

    @Override
    public void removeOne(Long opsId, Long opintojaksoId) {
        Lops2019Opintojakso opintojakso = getOpintojakso(opsId, opintojaksoId);
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        ops.getLops2019().getOpintojaksot().remove(opintojakso);
    }

    @Override
    public List<RevisionDto> getVersions(Long opsId, Long opintojaksoId) {
        getOpintojakso(opsId, opintojaksoId);
        List<Revision> revisions = opintojaksoRepository.getRevisions(opintojaksoId);
        return mapper.mapAsList(revisions, RevisionDto.class);
    }

    @Override
    public List<PoistettuDto> getRemoved(Long opsId) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Lops2019OpintojaksoDto getVersion(Long opsId, Long opintojaksoId, Integer versio) {
        Lops2019Opintojakso opintojakso = getOpintojakso(opsId, opintojaksoId);
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
