package fi.vm.sade.eperusteet.ylops.service.locking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpsCtx {
    private Long opsId;

    public OpsCtx() {
    }

    public OpsCtx(Long opsId) {
        this.opsId = opsId;
    }
}
