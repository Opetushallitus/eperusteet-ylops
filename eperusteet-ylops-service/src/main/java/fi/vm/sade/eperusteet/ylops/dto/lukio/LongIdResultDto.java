package fi.vm.sade.eperusteet.ylops.dto.lukio;

import fi.vm.sade.eperusteet.ylops.dto.IdHolder;
import lombok.Getter;

@Getter
public class LongIdResultDto implements IdHolder {
    private final Long id;

    public LongIdResultDto(Long id) {
        this.id = id;
    }
}
