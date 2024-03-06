package fi.vm.sade.eperusteet.ylops.service.locking;

import fi.vm.sade.eperusteet.ylops.domain.Lukko;
import fi.vm.sade.eperusteet.ylops.dto.LukkoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractLockService<T extends OpsCtx> implements LockService<T> {

    @Autowired
    private LockManager manager;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#ctx.opsId,'opetussuunnitelma','luku')")
    public LukkoDto getLock(T ctx) {
        Lukko lock = manager.getLock(validateCtx(ctx, true));
        return lock == null ? null : LukkoDto.of(lock, latestRevision(ctx));
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#ctx.opsId,'opetussuunnitelma','muokkaus')")
    public LukkoDto lock(@P("ctx") T ctx) {
        return lock(ctx, null);
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#ctx.opsId,'opetussuunnitelma','muokkaus')")
    public LukkoDto lock(T ctx, Integer ifMatchRevision) {
        Long key = validateCtx(ctx, false);
        final int latestRevision = latestRevision(ctx);
        if (ifMatchRevision == null || latestRevision == ifMatchRevision) {
            return LukkoDto.of(manager.lock(key), latestRevision);
        }
        return null;
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void unlock(T ctx) {
        Long lockId = getLockId(ctx);
        if (lockId != null) {
            manager.unlock(lockId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void assertLock(T ctx) {
        manager.ensureLockedByAuthenticatedUser(validateCtx(ctx, true));
    }

    /**
     * Palauttaa kontekstia vastaavan lukittavan entiteetin pääavaimen tai null jos tätä ei voi selvittää (entiteetti on poistettu tms.).
     *
     * @param ctx
     * @return kontekstia vastaavan lukittavan entiteetin pääavaimen
     */
    protected abstract Long getLockId(T ctx);

    /**
     * Varmistaa että lukituskonteksti on validi ja käyttäjällä on oikeudet lukitukseen tai sen kyselyyn.
     *
     * @param ctx
     * @return kontekstia vastaavan lukittavan entiteetin pääavaimen (Mahdollistaa optimoinnin jos getLockId joutuu tekemään tietokantahakuja).
     */
    protected abstract Long validateCtx(T ctx, boolean readOnly);

    /**
     * Varmistaa että lukituskonteksti on validi
     *
     * @param ctx
     * @return kontekstia vastaavan lukittavan entiteetin
     */
    protected abstract int latestRevision(T ctx);

}
