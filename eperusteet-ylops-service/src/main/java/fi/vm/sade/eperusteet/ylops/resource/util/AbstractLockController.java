package fi.vm.sade.eperusteet.ylops.resource.util;

import fi.vm.sade.eperusteet.ylops.dto.LukkoDto;
import fi.vm.sade.eperusteet.ylops.resource.config.InternalApi;
import fi.vm.sade.eperusteet.ylops.service.locking.LockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@InternalApi
public abstract class AbstractLockController<T> {

    @RequestMapping(method = GET)
    public ResponseEntity<LukkoDto> checkLock(T ctx) {
        LukkoDto lock = service().getLock(ctx);
        return lock == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(lock, Etags.eTagHeader(lock.getRevisio()), HttpStatus.OK);
    }

    @RequestMapping(method = POST)
    public ResponseEntity<LukkoDto> lock(T ctx,
                                         @RequestHeader(value = "If-Match", required = false) String eTag) {
        LukkoDto lock = service().lock(ctx, Etags.revisionOf(eTag));
        if (lock == null) {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        } else {
            return new ResponseEntity<>(lock, Etags.eTagHeader(lock.getRevisio()), HttpStatus.CREATED);
        }

    }

    @RequestMapping(method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlock(T ctx) {
        service().unlock(ctx);
    }

    protected abstract LockService<T> service();
}
