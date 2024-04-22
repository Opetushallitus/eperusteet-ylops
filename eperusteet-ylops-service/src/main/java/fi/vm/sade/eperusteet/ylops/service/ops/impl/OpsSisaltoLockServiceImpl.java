package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.LockingException;
import fi.vm.sade.eperusteet.ylops.service.locking.AbstractLockService;
import fi.vm.sade.eperusteet.ylops.service.locking.OpsCtx;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsSisaltoLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpsSisaltoLockServiceImpl extends AbstractLockService<OpsCtx> implements OpsSisaltoLockService {

    @Autowired
    private OpetussuunnitelmaRepository suunnitelmat;

    @Autowired
    private TekstikappaleviiteRepository viitteet;

    @Override
    protected Long getLockId(OpsCtx ctx) {
        Opetussuunnitelma ops = suunnitelmat.findOne(ctx.getOpsId());
        return ops == null ? null : ops.getTekstit().getId();
    }

    @Override
    protected int latestRevision(OpsCtx ctx) {
        return viitteet.getLatestRevisionId(getLockId(ctx));
    }

    @Override
    protected Long validateCtx(OpsCtx ctx, boolean readOnly) {
        Long id = getLockId(ctx);
        if (id != null) {
            return id;
        }
        throw new LockingException("virheellinen lukitus");
    }

}
