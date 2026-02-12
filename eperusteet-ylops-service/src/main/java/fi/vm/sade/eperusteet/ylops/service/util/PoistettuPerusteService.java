package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.domain.ops.PoistettuPeruste;
import fi.vm.sade.eperusteet.ylops.repository.ops.PoistettuPerusteRepository;
import fi.vm.sade.eperusteet.ylops.service.util.impl.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PoistettuPerusteService {

    @Autowired
    private PoistettuPerusteRepository poistettuPerusteRepository;

    @Autowired
    private ProfileService profileService;

    @Transactional
    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI') or @profileService.isDevProfileActive()")
    public void add(Long perusteId) {
        PoistettuPeruste entity = new PoistettuPeruste();
        entity.setPerusteId(perusteId);
        poistettuPerusteRepository.save(entity);
    }

    @Transactional
    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI') or @profileService.isDevProfileActive()")
    public void delete(Long perusteId) {
        poistettuPerusteRepository.deleteById(perusteId);
    }

    @PreAuthorize("isAuthenticated()")
    public boolean exists(Long perusteId) {
        return poistettuPerusteRepository.existsByPerusteId(perusteId);
    }

}
