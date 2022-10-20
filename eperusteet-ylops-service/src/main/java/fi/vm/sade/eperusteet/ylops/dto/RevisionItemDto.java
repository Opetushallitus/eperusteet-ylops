package fi.vm.sade.eperusteet.ylops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevisionItemDto<T> {
    private RevisionDto revision;
    private String muokkaaja = "";
    private T data;
}
