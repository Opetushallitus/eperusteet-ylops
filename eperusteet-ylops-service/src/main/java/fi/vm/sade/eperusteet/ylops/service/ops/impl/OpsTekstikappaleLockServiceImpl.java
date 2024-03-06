package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.LockingException;
import fi.vm.sade.eperusteet.ylops.service.locking.AbstractLockService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsTekstikappaleCtx;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsTekstikappaleLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpsTekstikappaleLockServiceImpl extends AbstractLockService<OpsTekstikappaleCtx> implements OpsTekstikappaleLockService {

    @Autowired
    private TekstikappaleviiteRepository viitteet;

    @Autowired
    private TekstiKappaleRepository kappaleet;

    @Override
    protected Long getLockId(OpsTekstikappaleCtx ctx) {
        TekstiKappaleViite viite = viitteet.findOne(ctx.getViiteId());
        return viite == null ? null : viite.getTekstiKappale().getId();
    }

    @Override
    protected int latestRevision(OpsTekstikappaleCtx ctx) {
        return kappaleet.getLatestRevisionId(viitteet.findOne(ctx.getViiteId()).getTekstiKappale().getId());
    }

    @Override
    protected Long validateCtx(OpsTekstikappaleCtx ctx, boolean readOnly) {
        TekstiKappaleViite viite = viitteet.findInOps(ctx.getOpsId(), ctx.getViiteId());
        if (viite != null && viite.getTekstiKappale() != null) {
            return viite.getTekstiKappale().getId();
        }
        throw new LockingException("Virheellinen lukitus");
    }
}
