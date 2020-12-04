package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Poistettava;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Opintojakso;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Poistettu;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.PoistetunTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiainePalautettuDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksoRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OppiaineRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.PoistetutRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.ops.PoistoService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class PoistoServiceImpl implements PoistoService {

    @Autowired
    private PoistetutRepository poistetutRepository;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private Lops2019OppiaineService lops2019OppiaineService;

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
    private OppiaineService oppiaineService;

    @Override
    public void restore(Long opsId, Long poistoId) {
        Poistettu poistettuInfo = poistetutRepository.getOne(poistoId);
        Opetussuunnitelma opetussuunnitelma = getOpetussuunnitelma(opsId);
        if (poistettuInfo.getOpetussuunnitelma() != opetussuunnitelma) {
            throw new BusinessRuleViolationException("vain-oman-voi-palauttaa");
        }

        switch (poistettuInfo.getTyyppi()) {
            case LOPS2019OPPIAINE:
                palautaLops2019Oppiaine(opsId, poistettuInfo);
                break;
            case OPINTOJAKSO:
                palautaOpintojakso(opsId, poistettuInfo);
                break;
            case OPPIAINE:
                palautaOppiaine(opsId, poistettuInfo);
                break;
            case TUOTU_OPINTOJAKSO:
            case TUOTU_OPPIMAARA:
                poistetutRepository.delete(poistettuInfo);
                break;
            default:
                throw new BusinessRuleViolationException("tunnistamaton-poistotyyppi");
        }
    }

    @Override
    public OppiainePalautettuDto restoreOppiaine(Long opsId, Long id) {
        Poistettu poistettuInfo = poistetutRepository.findOne(id);
        return palautaOppiaine(opsId, poistettuInfo);
    }

    @Override
    public List<Lops2019PoistettuDto> getRemoved(Long opsId) {
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        List<Poistettu> poistetut = poistetutRepository.findAllByOpetussuunnitelma(ops);
        return mapper.mapAsList(poistetut, Lops2019PoistettuDto.class);
    }

    @Override
    public List<Lops2019PoistettuDto> getRemoved(Long opsId, PoistetunTyyppi tyyppi) {
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        List<Poistettu> poistetut = poistetutRepository.findAllByOpetussuunnitelmaAndTyyppi(ops, tyyppi);
        return mapper.mapAsList(poistetut, Lops2019PoistettuDto.class);
    }

    @Override
    public Lops2019PoistettuDto getRemoved(Long opsId, Long poistettuId, PoistetunTyyppi tyyppi) {
        return mapper.map(poistetutRepository.findByOpetussuunnitelmaIdAndPoistettuIdAndTyyppi(opsId, poistettuId, tyyppi), Lops2019PoistettuDto.class);
    }

    @Override
    public Lops2019PoistettuDto remove(Opetussuunnitelma ops, Poistettava poistettava, PoistetunTyyppi tyyppi) {
        Poistettu poistettu = new Poistettu();
        poistettu.setNimi(poistettava.getNimi());
        poistettu.setOpetussuunnitelma(ops);
        poistettu.setPoistettuId(poistettava.getId());
        poistettu.setTyyppi(tyyppi);
        return mapper.map(poistetutRepository.save(poistettu), Lops2019PoistettuDto.class);
    }

    @Override
    public Lops2019PoistettuDto remove(Opetussuunnitelma ops, Poistettava poistettava) {
        return remove(ops, poistettava, poistettava.getPoistetunTyyppi());
    }

    private void palautaLops2019Oppiaine(Long opsId, Poistettu poistettuInfo) {
        Lops2019Oppiaine latest = oppiaineRepository.getLatestNotNull(poistettuInfo.getPoistettuId());
        Lops2019Oppiaine oppiaine = Lops2019Oppiaine.copy(latest);
        Lops2019PaikallinenOppiaineDto uusi = mapper.map(oppiaine, Lops2019PaikallinenOppiaineDto.class);
        lops2019OppiaineService.addOppiaine(opsId, uusi, MuokkausTapahtuma.PALAUTUS);
        poistetutRepository.delete(poistettuInfo);
    }

    private void palautaOpintojakso(Long opsId, Poistettu poistettuInfo) {
        Lops2019Opintojakso latest = opintojaksoRepository.getLatestNotNull(poistettuInfo.getPoistettuId());
        Lops2019Opintojakso opintojakso = Lops2019Opintojakso.copy(latest);
        Lops2019OpintojaksoDto opintojaksoDto = mapper.map(opintojakso, Lops2019OpintojaksoDto.class);
        opintojaksoService.addOpintojakso(opsId, opintojaksoDto, MuokkausTapahtuma.PALAUTUS);
        poistetutRepository.delete(poistettuInfo);
    }

    private OppiainePalautettuDto palautaOppiaine(Long opsId, Poistettu poistettuInfo) {
        OppiainePalautettuDto palautettuDto = oppiaineService.restore(opsId, poistettuInfo.getPoistettuId(), null);
        poistetutRepository.delete(poistettuInfo);
        return palautettuDto;
    }

    private Opetussuunnitelma getOpetussuunnitelma(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("ops-ei-loydy");
        }
        return ops;
    }
}
