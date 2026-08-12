package fi.vm.sade.eperusteet.ylops.service.security;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("opsOikeus")
public class OpetussuunnitelmaOikeusTarkistus {

    @Autowired
    private org.springframework.security.access.PermissionEvaluator permissionEvaluator;

    @Transactional(readOnly = true)
    public boolean oikeusPohjahierarkiassa(Opetussuunnitelma opetussuunnitelma, Permission permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (opetussuunnitelma == null || authentication == null || permission == null) {
            return false;
        }

        return opetussuunnitelma.getPohjaHierarkia().stream()
                .anyMatch(ops -> permissionEvaluator.hasPermission(authentication, ops.getId(),
                        TargetType.OPETUSSUUNNITELMA.toString(), permission.toString()));
    }
}
