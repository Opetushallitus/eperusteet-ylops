package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Lops2019OpintojaksoKevytDto extends Lops2019OpintojaksoBaseDto {

    @Builder.Default
    private Set<Lops2019OpintojaksonOppiaineDto> oppiaineet = new HashSet<>();
}
