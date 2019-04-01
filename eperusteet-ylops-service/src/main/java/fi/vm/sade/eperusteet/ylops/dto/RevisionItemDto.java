package fi.vm.sade.eperusteet.ylops.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RevisionItemDto<T> {
    private RevisionDto revision;
    private String muokkaaja = "";
    private T data;
}
