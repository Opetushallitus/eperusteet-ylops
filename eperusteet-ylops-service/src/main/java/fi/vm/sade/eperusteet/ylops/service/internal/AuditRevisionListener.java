package fi.vm.sade.eperusteet.ylops.service.internal;

import fi.vm.sade.eperusteet.utils.revision.RevisioKommenttiHolder;
import fi.vm.sade.eperusteet.ylops.domain.revision.RevisionInfo;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;

import java.security.Principal;

public class AuditRevisionListener implements org.hibernate.envers.RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        if (revisionEntity instanceof RevisionInfo) {
            RevisionInfo ri = (RevisionInfo) revisionEntity;
            Principal principal = SecurityUtil.getAuthenticatedPrincipal();
            ri.setMuokkaajaOid(principal != null ? principal.getName() : "tuntematon");
            String kommentti = RevisioKommenttiHolder.poll();
            if (kommentti != null) {
                ri.addKommentti(kommentti);
            }
        }
    }

}
