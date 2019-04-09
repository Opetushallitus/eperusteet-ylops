package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.ops.Kommentti2019;
import fi.vm.sade.eperusteet.ylops.dto.ops.Kommentti2019Dto;
import fi.vm.sade.eperusteet.ylops.dto.ops.Kommentti2019LuontiDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.Kommentti2019Repository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.Kommentti2019Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class Kommentti2019ServiceImpl implements Kommentti2019Service {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private Kommentti2019Repository kommenttiRepository;

    private String getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getName();
        }
        else {
            throw new BusinessRuleViolationException("kayttaja-ei-kirjautunut");
        }
    }

    @Override
    public Kommentti2019Dto get(Long opsId, UUID uuid) {
        return mapper.map(getImpl(opsId, uuid), Kommentti2019Dto.class);
    }

    @Override
    public Kommentti2019Dto add(Long opsId, Kommentti2019LuontiDto kommenttiLuontiDto) {
        Kommentti2019Dto kommenttiDto = mapper.map(kommenttiLuontiDto, Kommentti2019Dto.class);
        kommenttiDto.setOpsId(opsId);
        kommenttiDto.setLuoja(getUser());
        Date luotu = new Date();
        kommenttiDto.setLuotu(luotu);
        kommenttiDto.setMuokattu(luotu);
        kommenttiDto.setUuid(UUID.randomUUID());
        Kommentti2019 kommentti = mapper.map(kommenttiDto, Kommentti2019.class);
        kommentti = kommenttiRepository.save(kommentti);

        if (kommenttiDto.getParent() != null) {
            Kommentti2019 parent = getImpl(opsId, kommenttiDto.getParent());
            parent.getKommentit().add(kommentti);
        }

        return mapper.map(kommentti, Kommentti2019Dto.class);
    }


    @Override
    public Kommentti2019Dto update(Long opsId, UUID uuid, Kommentti2019Dto kommenttiUpdateDto) {
        Kommentti2019 kommentti = getImpl(opsId, uuid);
        if (!kommentti.getLuoja().equals(getUser())) {
            throw new BusinessRuleViolationException("vain-omaa-kommenttia-voi-muokata");
        }
        kommentti.setSisalto(kommenttiUpdateDto.getSisalto());
        kommentti.setMuokattu(new Date());
        kommenttiRepository.save(kommentti);
        return mapper.map(kommentti, Kommentti2019Dto.class);
    }

    private Kommentti2019 getImpl(Long opsId, UUID uuid) {
        Kommentti2019 kommentti = kommenttiRepository.findOneByUuid(uuid);
        if (kommentti == null) {
            throw new BusinessRuleViolationException("virheellinen-kiinnitys");
        }
        return kommentti;
    }

    @Override
    public void remove(Long opsId, UUID uuid) {
        Kommentti2019 kommentti = getImpl(opsId, uuid);
        if (!kommentti.getLuoja().equals(getUser())) {
            throw new BusinessRuleViolationException("vain-omaa-kommenttia-voi-muokata");
        }

        if (kommentti.getParent() != null) {
            Kommentti2019 parent = getImpl(opsId, kommentti.getParent());
            parent.getKommentit().remove(kommentti);
        }

        kommenttiRepository.delete(kommentti);
    }
}
