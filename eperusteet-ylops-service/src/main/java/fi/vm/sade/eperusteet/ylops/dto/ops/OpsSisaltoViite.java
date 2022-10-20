package fi.vm.sade.eperusteet.ylops.dto.ops;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class OpsSisaltoViite {
    Long opsId;
    String koodi;

    @Getter
    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Opintojakso extends OpsSisaltoViite {
        Long opintojaksoId;

        public Opintojakso(Long opsId, String koodi, Long opintojaksoId) {
            super(opsId, koodi);
            this.opintojaksoId = opintojaksoId;
        }
    }
}
