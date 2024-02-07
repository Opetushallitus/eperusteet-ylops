package fi.vm.sade.eperusteet.ylops.service.ops.lukio;

import fi.vm.sade.eperusteet.ylops.service.locking.OpsCtx;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LukioLockCtx extends OpsCtx {
    private LukittavaOsa lukittavaOsa;
    private Long id;

    public LukioLockCtx() {
    }

    public LukioLockCtx(Long opsId, LukittavaOsa lukittavaOsa, Long id) {
        super(opsId);
        this.lukittavaOsa = lukittavaOsa;
        this.id = id;
    }
}
