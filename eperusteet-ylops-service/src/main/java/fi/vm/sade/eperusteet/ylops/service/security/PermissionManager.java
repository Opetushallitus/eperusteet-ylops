package fi.vm.sade.eperusteet.ylops.service.security;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

public interface PermissionManager {

    boolean hasPermission(Authentication authentication, Serializable targetId, TargetType target,
                          Permission perm);

    @PreAuthorize("isAuthenticated()")
    Map<TargetType, Set<Permission>> getOpsPermissions();

    @PreAuthorize("isAuthenticated()")
    Map<TargetType, Set<Permission>> getOpsPermissions(Long id);
}
