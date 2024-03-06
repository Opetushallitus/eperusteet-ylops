package fi.vm.sade.eperusteet.ylops.service.ohje.impl;

import fi.vm.sade.eperusteet.ylops.domain.ohje.Ohje;
import fi.vm.sade.eperusteet.ylops.domain.ohje.OhjeTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.ohje.OhjeDto;
import fi.vm.sade.eperusteet.ylops.repository.ohje.OhjeRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ohje.OhjeService;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

@Service
@Transactional
public class OhjeServiceImpl implements OhjeService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OhjeRepository repository;

    @Override
    @Transactional(readOnly = true)
    public OhjeDto getOhje(Long id) {
        Ohje ohje = repository.findOne(id);
        assertExists(ohje, "Pyydettyä ohjetta ei ole olemassa");
        return mapper.map(ohje, OhjeDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OhjeDto> getTekstiKappaleOhjeet(UUID uuid) {
        List<Ohje> ohjeet = repository.findByKohde(uuid);
        return mapper.mapAsList(ohjeet, OhjeDto.class);
    }

    @Override
    public OhjeDto addOhje(OhjeDto ohjeDto) {
        if (ohjeDto.getTyyppi() == null) {
            ohjeDto.setTyyppi(OhjeTyyppi.PERUSTETEKSTI);
        }

        if (ohjeDto.getKohde() == null) {
            throw new BusinessRuleViolationException("kohdetta-ei-asetettu");
        }
        Ohje ohje = mapper.map(ohjeDto, Ohje.class);
        ohje = repository.save(ohje);
        return mapper.map(ohje, OhjeDto.class);
    }

    @Override
    public OhjeDto updateOhje(OhjeDto ohjeDto) {
        Ohje ohje = repository.findOne(ohjeDto.getId());
        assertExists(ohje, "Päivitettävää ohjetta ei ole olemassa");
        mapper.map(ohjeDto, ohje);
        ohje = repository.save(ohje);
        return mapper.map(ohje, OhjeDto.class);
    }

    @Override
    public void removeOhje(Long id) {
        Ohje ohje = repository.findOne(id);
        repository.delete(ohje);
    }

}
