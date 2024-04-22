package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.service.locking.OpsCtx;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpsOppiaineCtx extends OpsCtx {

    Long oppiaineId;
    Long kokonaisuusId;
    Long vuosiluokkaId;

    public OpsOppiaineCtx() {
    }

    public OpsOppiaineCtx(Long opsId, Long oppiaineId, Long kokonaisuusId, Long vuosiluokkaId) {
        super(opsId);
        this.oppiaineId = oppiaineId;
        this.kokonaisuusId = kokonaisuusId;
        this.vuosiluokkaId = vuosiluokkaId;
    }

    public boolean isValid() {
        return (isOppiane() || isKokonaisuus() || isVuosiluokka());
    }

    public boolean isOppiane() {
        return getOpsId() != null && oppiaineId != null && kokonaisuusId == null && vuosiluokkaId == null;
    }

    public boolean isKokonaisuus() {
        return getOpsId() != null && oppiaineId != null && kokonaisuusId != null && vuosiluokkaId == null;
    }

    public boolean isVuosiluokka() {
        return getOpsId() != null && oppiaineId != null && kokonaisuusId != null && vuosiluokkaId != null;
    }

}
