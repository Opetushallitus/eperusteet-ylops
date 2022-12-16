package fi.vm.sade.eperusteet.ylops.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonMergeDto<A, B> {
    @JsonUnwrapped
    private A a;

    @JsonUnwrapped
    private B b;
}
