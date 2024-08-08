package fi.vm.sade.eperusteet.ylops.service.teksti.impl;

import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.domain.teksti.PoistettuTekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.PoistettuTekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.teksti.TekstiKappaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

@Service
@Transactional
public class TekstiKappaleServiceImpl implements TekstiKappaleService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private TekstiKappaleRepository repository;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private PoistettuTekstiKappaleRepository poistettuTekstiKappaleRepository;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkaustietoService;

    @Override
    @Transactional(readOnly = true)
    public TekstiKappaleDto get(Long opsId, Long id) {
        TekstiKappale tekstiKappale = repository.findOne(id);
        assertExists(tekstiKappale, "Pyydettyä tekstikappaletta ei ole olemassa");
        return mapper.map(tekstiKappale, TekstiKappaleDto.class);
    }

    @Override
    public TekstiKappaleDto add(Long opsId, TekstiKappaleViite viite, TekstiKappaleDto tekstiKappaleDto) {
        TekstiKappale tekstiKappale = mapper.map(tekstiKappaleDto, TekstiKappale.class);
        tekstiKappale.setTila(Tila.LUONNOS);
        viite.setTekstiKappale(tekstiKappale);
        tekstiKappale = repository.saveAndFlush(tekstiKappale);
        mapper.map(tekstiKappale, tekstiKappaleDto);
        return tekstiKappaleDto;
    }

    @Override
    public TekstiKappaleDto update(Long opsId, TekstiKappaleDto tekstiKappaleDto, MuokkausTapahtuma tapahtuma) {
        return update(opsId, tekstiKappaleDto, true, tapahtuma);
    }

    @Override
    public TekstiKappaleDto update(Long opsId, TekstiKappaleDto tekstiKappaleDto, boolean requiredLock, MuokkausTapahtuma tapahtuma) {
        Long id = tekstiKappaleDto.getId();
        TekstiKappale current = assertExists(repository.findOne(id), "Tekstikappaletta ei ole olemassa");

        if (requiredLock) {
            repository.lock(current);
        }

        mapper.map(tekstiKappaleDto, current);
        current.updateMuokkaustiedot();
        TekstiKappale tekstiKappale = repository.save(current);
        if (tapahtuma != null) {
            muokkaustietoService.addOpsMuokkausTieto(opsId, tekstiKappale, tapahtuma);
        }
        return mapper.map(tekstiKappale, TekstiKappaleDto.class);
    }

    @Override
    public TekstiKappaleDto mergeNew(Long opsId, TekstiKappaleViite viite, TekstiKappaleDto tekstiKappaleDto) {
        if (viite.getTekstiKappale() == null || viite.getTekstiKappale().getId() == null) {
            throw new IllegalArgumentException("Virheellinen viite");
        }
        Long id = viite.getTekstiKappale().getId();
        TekstiKappale clone = assertExists(repository.findOne(id), "Tekstikappaletta ei ole olemassa").copy();
        mapper.map(tekstiKappaleDto, clone);
        clone = repository.save(clone);

        viite.setTekstiKappale(clone);
        viite.setOmistussuhde(Omistussuhde.OMA);

        mapper.map(clone, tekstiKappaleDto);
        return tekstiKappaleDto;
    }

    @Override
    public void removeTekstiKappaleFromOps(Long opsId, Long tekstikappaleId, Long viiteId) {
        PoistettuTekstiKappale poistettu = new PoistettuTekstiKappale();
        TekstiKappale tekstiKappale = repository.findOne(tekstikappaleId);
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

        poistettu.setOpetussuunnitelma(ops);
        poistettu.setTekstiKappale(tekstiKappale.getId());
        poistettu.setParentTekstiKappaleViite(viiteId);
        poistettuTekstiKappaleRepository.save(poistettu);
    }

    @Override
    public void delete(Long opsId, Long id) {
        repository.delete(id);
    }
}
