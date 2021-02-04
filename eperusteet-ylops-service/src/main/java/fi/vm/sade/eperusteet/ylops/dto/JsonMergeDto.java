package fi.vm.sade.eperusteet.ylops.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsonMergeDto<A, B> {
    @JsonUnwrapped
    private A a;

    @JsonUnwrapped
    private B b;

    public JsonMergeDto(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public JsonMergeDto() {
    }
}
