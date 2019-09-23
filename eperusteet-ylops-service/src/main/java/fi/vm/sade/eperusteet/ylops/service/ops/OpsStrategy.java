package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;

public interface OpsStrategy {
    /**
     * Hook to control ordering of ops trees on update.
     *
     * Not required.
     *
     * @param tree Tree to be manipulated.
     * @param ops Owning ops.
     */
    default void reorder(TekstiKappaleViiteDto.Puu tree, Opetussuunnitelma ops) {}
}
