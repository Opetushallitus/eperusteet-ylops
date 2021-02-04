package fi.vm.sade.eperusteet.ylops.dto.ops;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganisaationKoodi {
    Long opsId;
    String koodi;

    @Getter
    public static class Opintojakso extends OrganisaationKoodi {
        Long opintojaksoId;

        public Opintojakso(Long opsId, String koodi, Long opintojaksoId) {
            super(opsId, koodi);
            this.opintojaksoId = opintojaksoId;
        }
    }
}
