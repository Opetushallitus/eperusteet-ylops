package fi.vm.sade.eperusteet.ylops.service.ukk.impl;

import fi.vm.sade.eperusteet.ylops.domain.ukk.Kysymys;
import fi.vm.sade.eperusteet.ylops.dto.ukk.KysymysDto;
import fi.vm.sade.eperusteet.ylops.repository.ukk.KysymysRepository;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ukk.KysymysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class KysymysServiceImpl implements KysymysService {

    @Autowired
    private KysymysRepository repository;

    @Autowired
    private DtoMapper mapper;

    @Override
    public List<KysymysDto> getKysymykset() {
        return mapper.mapAsList(repository.findAll(), KysymysDto.class);
    }

    @Override
    public KysymysDto createKysymys(KysymysDto dto) {
        Kysymys kysymys = mapper.map(dto, Kysymys.class);
        kysymys = repository.save(kysymys);
        return mapper.map(kysymys, KysymysDto.class);
    }

    @Override
    public KysymysDto updateKysymys(KysymysDto dto) {
        Kysymys kysymys = repository.findOne(dto.getId());
        kysymys = mapper.map(dto, kysymys);
        return mapper.map(kysymys, dto);
    }

    @Override
    public void deleteKysymys(Long id) {
        repository.delete(id);
    }
}
