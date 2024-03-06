package fi.vm.sade.eperusteet.ylops.service.internal;

import fi.vm.sade.eperusteet.ylops.domain.revision.RevisionInfo;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;

public class AuditRevisionListener implements org.hibernate.envers.RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        if (revisionEntity instanceof RevisionInfo) {
            RevisionInfo ri = (RevisionInfo) revisionEntity;
            ri.setMuokkaajaOid(SecurityUtil.getAuthenticatedPrincipal().getName());
        }
    }

}
