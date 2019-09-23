package fi.vm.sade.eperusteet.ylops.service.util;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

/**
 * Wraps revision information in the update data
 *
 *
 *
 * @param <T>
 */
@Getter
@Setter
public class UpdateWrapperDto<T> {
    private String kommentti;

    @JsonUnwrapped
    private T data;
}
