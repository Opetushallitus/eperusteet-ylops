package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmaAikataulu;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmanAikatauluDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmanAikatauluRepository;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanAikatauluService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OpetussuunnitelmanAikatauluServiceImpl implements OpetussuunnitelmanAikatauluService {

    @Autowired
    private OpetussuunnitelmanAikatauluRepository opetussuunnitelmanAikatauluRepository;

    @Autowired
    private DtoMapper mapper;

    @Override
    public List<OpetussuunnitelmanAikatauluDto> getAll(Long opsId) {
        return mapper.mapAsList(opetussuunnitelmanAikatauluRepository.findByOpetussuunnitelmaId(opsId), OpetussuunnitelmanAikatauluDto.class);
    }

    @Override
    public OpetussuunnitelmanAikatauluDto add(Long opsId, OpetussuunnitelmanAikatauluDto opetussuunnitelmanAikatauluDto) {
        OpetussuunnitelmaAikataulu opetussuunnitelmaAikataulu = mapper.map(opetussuunnitelmanAikatauluDto, OpetussuunnitelmaAikataulu.class);
        return mapper.map(opetussuunnitelmanAikatauluRepository.save(opetussuunnitelmaAikataulu), OpetussuunnitelmanAikatauluDto.class);
    }

    @Override
    public OpetussuunnitelmanAikatauluDto update(Long opsId, OpetussuunnitelmanAikatauluDto opetussuunnitelmanAikatauluDto) {
        OpetussuunnitelmaAikataulu opetussuunnitelmaAikataulu = mapper.map(opetussuunnitelmanAikatauluDto, OpetussuunnitelmaAikataulu.class);
        return mapper.map(opetussuunnitelmanAikatauluRepository.save(opetussuunnitelmaAikataulu), OpetussuunnitelmanAikatauluDto.class);
    }

    @Override
    public void delete(Long opsId, OpetussuunnitelmanAikatauluDto opetussuunnitelmanAikatauluDto) {
        opetussuunnitelmanAikatauluRepository.delete(opetussuunnitelmanAikatauluDto.getId());
    }
}
