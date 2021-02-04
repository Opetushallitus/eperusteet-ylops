package fi.vm.sade.eperusteet.ylops.dto.ops;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OpsSisaltoViite {
    Long opsId;
    String koodi;

    @Getter
    public static class Opintojakso extends OpsSisaltoViite {
        Long opintojaksoId;

        public Opintojakso(Long opsId, String koodi, Long opintojaksoId) {
            super(opsId, koodi);
            this.opintojaksoId = opintojaksoId;
        }
    }
}
