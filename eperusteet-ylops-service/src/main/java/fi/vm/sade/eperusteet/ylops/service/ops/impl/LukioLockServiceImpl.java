package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.LockingException;
import fi.vm.sade.eperusteet.ylops.service.locking.AbstractLockService;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioLockCtx;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class LukioLockServiceImpl extends AbstractLockService<LukioLockCtx>
        implements LukioLockService {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Override
    protected Long getLockId(LukioLockCtx ctx) {
        if (ctx.getLukittavaOsa().isFromOps()) {
            return ctx.getLukittavaOsa().getFromOps().get()
                    .apply(opetussuunnitelmaRepository.findOne(ctx.getOpsId())).getId();
        }
        Object val = getRepo(ctx).findOne(ctx.getId());
        return val == null ? null : ctx.getId();
    }

    @Override
    protected Long validateCtx(LukioLockCtx ctx, boolean readOnly) {
        Long id = getLockId(ctx);
        if (id != null) {
            return id;
        }
        throw new LockingException("virheellinen lukitus");
    }

    protected JpaWithVersioningRepository getRepo(LukioLockCtx ctx) {
        return applicationContext.getBean(ctx.getLukittavaOsa().getRepository());
    }

    @Override
    protected int latestRevision(LukioLockCtx ctx) {
        return getRepo(ctx).getLatestRevisionId(getLockId(ctx));
    }
}
