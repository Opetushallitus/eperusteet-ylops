package fi.vm.sade.eperusteet.ylops.service.security;

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ukk.KysymysRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.ylops.service.util.Pair;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractPermissionManager implements PermissionManager {

    public static final String MSG_OPS_EI_OLEMASSA = "Pyydettyä opetussuunnitelmaa ei ole olemassa";

    @Autowired
    protected OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    protected KysymysRepository kysymysRepository;

    @Transactional(readOnly = true)
    public Map<TargetType, Set<Permission>> getOpsPermissions() {

        Map<TargetType, Set<Permission>> permissionMap = new HashMap<>();
        Set<Permission> opsPermissions =
                EnumSet.allOf(PermissionEvaluator.RolePermission.class).stream()
                        .map(p -> new Pair<>(p, SecurityUtil.getOrganizations(Collections.singleton(p))))
                        .filter(pair -> !pair.getSecond().isEmpty())
                        .flatMap(pair -> fromRolePermission(pair.getFirst()).stream())
                        .collect(Collectors.toSet());
        permissionMap.put(TargetType.OPETUSSUUNNITELMA, opsPermissions);

        Set<Permission> pohjaPermissions =
                EnumSet.allOf(PermissionEvaluator.RolePermission.class).stream()
                        .map(p -> new Pair<>(p, SecurityUtil.getOrganizations(Collections.singleton(p))))
                        .filter(pair -> pair.getSecond().contains(SecurityUtil.OPH_OID))
                        .flatMap(pair -> fromRolePermission(pair.getFirst()).stream())
                        .collect(Collectors.toSet());
        permissionMap.put(TargetType.POHJA, pohjaPermissions);

        return permissionMap;
    }

    @Transactional(readOnly = true)
    public Map<TargetType, Set<Permission>> getOpsPermissions(Long id) {

        Map<TargetType, Set<Permission>> permissionMap = new HashMap<>();
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        if (ops == null) {
            throw new NotExistsException(MSG_OPS_EI_OLEMASSA);
        }
        boolean isPohja = ops.getTyyppi() == Tyyppi.POHJA;
        Set<String> organisaatiot = ops.getOrganisaatiot();
        Set<Permission> permissions
                = EnumSet.allOf(PermissionEvaluator.RolePermission.class).stream()
                .map(p -> new Pair<>(p, SecurityUtil.getOrganizations(Collections.singleton(p))))
                .filter(pair -> !CollectionUtil.intersect(pair.getSecond(), organisaatiot).isEmpty())
                .flatMap(pair -> fromRolePermission(pair.getFirst()).stream())
                .filter(permission -> ops.getTila() == Tila.LUONNOS ||
                        (permission == Permission.TILANVAIHTO &&
                                !ops.getTila().mahdollisetSiirtymat(isPohja).isEmpty()) ||
                        fromRolePermission(PermissionEvaluator.RolePermission.READ).contains(permission))
                .collect(Collectors.toSet());

        permissionMap.put(TargetType.OPETUSSUUNNITELMA, permissions);
        if (ops.getTyyppi() == Tyyppi.POHJA) {
            permissionMap.put(TargetType.POHJA, permissions);
        }

        return permissionMap;
    }

    private static Set<Permission> fromRolePermission(PermissionEvaluator.RolePermission rolePermission) {
        Set<Permission> permissions = new HashSet<>();
        switch (rolePermission) {
            case ADMIN:
                permissions.add(Permission.HALLINTA);
            case CRUD:
                permissions.add(Permission.LUONTI);
                permissions.add(Permission.POISTO);
                permissions.add(Permission.TILANVAIHTO);
            case READ_UPDATE:
                permissions.add(Permission.LUKU);
                permissions.add(Permission.MUOKKAUS);
            case READ:
                permissions.add(Permission.LUKU);
                permissions.add(Permission.KOMMENTOINTI);
                break;
        }
        return permissions;
    }
}
