package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.service.locking.OpsCtx;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpsTekstikappaleCtx extends OpsCtx {

    private Long viiteId;

    public OpsTekstikappaleCtx() {
        super();
    }

    public OpsTekstikappaleCtx(Long opsId, Long viiteId) {
        super(opsId);
        this.viiteId = viiteId;
    }

}
