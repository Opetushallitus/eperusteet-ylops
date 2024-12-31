package fi.vm.sade.eperusteet.ylops.dto.lukio;

import fi.vm.sade.eperusteet.ylops.dto.IdHolder;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LukiokurssiOppaineMuokkausDto implements Serializable, IdHolder {
    @NotNull
    private Long id;
    private List<KurssinOppiaineDto> oppiaineet = new ArrayList<>();

    public LukiokurssiOppaineMuokkausDto(Long id) {
        this.id = id;
    }
}
