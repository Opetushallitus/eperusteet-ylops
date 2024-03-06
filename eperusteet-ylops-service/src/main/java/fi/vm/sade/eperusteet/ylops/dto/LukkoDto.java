package fi.vm.sade.eperusteet.ylops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.ylops.domain.Lukko;

import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode
public class LukkoDto {

    public LukkoDto(Lukko lukko) {
        this(lukko, null);
    }

    public LukkoDto(Lukko lukko, Integer revisio) {
        this.haltijaOid = lukko.getHaltijaOid();
        this.haltijaNimi = "";
        this.luotu = lukko.getLuotu();
        this.vanhentuu = lukko.getVanhentuu();
        this.oma = lukko.isOma();
        this.revisio = revisio;
    }

    final String haltijaOid;

    @Setter
    String haltijaNimi;
    final Instant luotu;
    final Instant vanhentuu;
    final Boolean oma;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer revisio;

    public static LukkoDto of(Lukko lukko) {
        return lukko == null ? null : new LukkoDto(lukko);
    }

    public static LukkoDto of(Lukko lukko, int revisio) {
        return lukko == null ? null : new LukkoDto(lukko, revisio);
    }
}
