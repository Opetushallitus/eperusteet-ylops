package fi.vm.sade.eperusteet.ylops.service.teksti.impl;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kommentti;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.KommenttiDto;
import fi.vm.sade.eperusteet.ylops.repository.teksti.KommenttiRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.security.Permission;
import fi.vm.sade.eperusteet.ylops.service.security.PermissionManager;
import fi.vm.sade.eperusteet.ylops.service.security.TargetType;
import fi.vm.sade.eperusteet.ylops.service.teksti.KommenttiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

@Service
@Transactional(readOnly = false)
public class KommenttiServiceImpl implements KommenttiService {

    @Autowired
    private KommenttiRepository repository;

    @Autowired
    private KayttajanTietoService kayttajat;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private PermissionManager permissionManager;

    @Override
    @Transactional(readOnly = true)
    public List<KommenttiDto> getAllByTekstiKappaleViite(Long opsId, Long tekstiKappaleViiteId) {
        List<Kommentti> kommentit = repository.findByTekstiKappaleViiteId(opsId, tekstiKappaleViiteId);
        return mapper.mapAsList(kommentit, KommenttiDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KommenttiDto> getAllByOppiaine(Long opsId, Long vlkId, Long oppiaineId) {
        List<Kommentti> kommentit = repository.findByOppiaine(opsId, vlkId, oppiaineId);
        return mapper.mapAsList(kommentit, KommenttiDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KommenttiDto> getAllByVuosiluokka(Long opsId, Long vlkId, Long oppiaineId, Long vlId) {
        List<Kommentti> kommentit = repository.findByVuosiluokka(opsId, vlkId, oppiaineId, vlId);
        return mapper.mapAsList(kommentit, KommenttiDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KommenttiDto> getAllByOpetussuunnitelma(Long opsId) {
        List<Kommentti> kommentit = repository.findByOpetussuunnitelmaId(opsId);
        return mapper.mapAsList(kommentit, KommenttiDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KommenttiDto> getAllByParent(Long id) {
        List<Kommentti> kommentit = repository.findByParentId(id);
        return mapper.mapAsList(kommentit, KommenttiDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KommenttiDto> getAllByYlin(Long id) {
        List<Kommentti> kommentit = repository.findByYlinId(id);
        return mapper.mapAsList(kommentit, KommenttiDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public KommenttiDto get(Long kommenttiId) {
        Kommentti kommentti = repository.findOne(kommenttiId);
        return mapper.map(kommentti, KommenttiDto.class);
    }

    @Transactional
    private void addName(Kommentti kommentti) {
        KayttajanTietoDto ktd = kayttajat.hae(kommentti.getLuoja());
        if (ktd != null) {
            kommentti.setNimi(ktd.getKutsumanimi() + " " + ktd.getSukunimi());
        }
    }

    private static String clip(String kommentti) {
        if (kommentti != null) {
            int length = kommentti.length();
            return kommentti.substring(0, Math.min(length, 1024));
        } else {
            return "";
        }
    }

    private void assertRights(Kommentti kommentti, Permission p) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KayttajanTietoDto kirjautunut = kayttajat.haeKirjautaunutKayttaja();
        if (kirjautunut.getOidHenkilo().equals(kommentti.getLuoja())) {
            return;
        }
        if (!permissionManager.hasPermission(authentication, kommentti.getOpetussuunnitelmaId(), TargetType.OPETUSSUUNNITELMA, p)) {
            throw new BusinessRuleViolationException("Ei oikeutta");
        }
    }

    @Override
    public KommenttiDto add(KommenttiDto kommenttiDto) {
        Kommentti kommentti = mapper.map(kommenttiDto, Kommentti.class);
        kommentti.setSisalto(clip(kommenttiDto.getSisalto()));
        if (kommentti.getParentId() != null) {
            Kommentti parent = repository.findOne(kommentti.getParentId());
            kommentti.setYlinId(parent.getYlinId() == null ? parent.getId() : parent.getYlinId());
        }
        kommentti = repository.save(kommentti);
        addName(kommentti);
        return mapper.map(kommentti, KommenttiDto.class);
    }

    @Override
    public KommenttiDto update(Long kommenttiId, KommenttiDto kommenttiDto) {
        Kommentti kommentti = repository.findOne(kommenttiId);
        assertExists(kommentti, "Päivitettävää kommenttia ei ole olemassa");
        assertRights(kommentti, Permission.LUKU);
        kommentti.setSisalto(clip(kommenttiDto.getSisalto()));
        return mapper.map(repository.save(kommentti), KommenttiDto.class);
    }

    @Override
    public void delete(Long kommenttiId) {
        Kommentti kommentti = repository.findOne(kommenttiId);
        assertExists(kommentti, "Poistettavaa kommenttia ei ole olemassa");
        assertRights(kommentti, Permission.LUONTI);
        kommentti.setSisalto(null);
        kommentti.setPoistettu(true);
    }

    @Override
    public void deleteReally(Long kommenttiId) {
        Kommentti kommentti = repository.findOne(kommenttiId);
        assertExists(kommentti, "Poistettavaa kommenttia ei ole olemassa");
        assertRights(kommentti, Permission.HALLINTA);
        repository.deleteById(kommenttiId);
    }

}
