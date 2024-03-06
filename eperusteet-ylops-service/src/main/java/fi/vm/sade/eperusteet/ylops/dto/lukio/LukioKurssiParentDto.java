package fi.vm.sade.eperusteet.ylops.dto.lukio;

import lombok.Getter;

@Getter
public class LukioKurssiParentDto {
    private final Long id;
    private final Long parentId;

    public LukioKurssiParentDto(Long id, Long parentId) {
        this.id = id;
        this.parentId = parentId;
    }
}
