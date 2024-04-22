package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiStateService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DokumenttiStateServiceImpl implements DokumenttiStateService {

    @Autowired
    private DokumenttiRepository dokumenttiRepository;

    @Autowired
    private DtoMapper mapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Dokumentti save(DokumenttiDto dto) {
        Dokumentti dokumentti = dokumenttiRepository.findOne(dto.getId());

        if (dokumentti != null) {
            mapper.map(dto, dokumentti);
            return dokumenttiRepository.save(dokumentti);
        }

        return null;
    }

}
