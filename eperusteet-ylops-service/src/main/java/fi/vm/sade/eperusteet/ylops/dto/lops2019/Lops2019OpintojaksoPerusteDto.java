package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lops2019OpintojaksoPerusteDto {
    @Builder.Default
    private List<Lops2019ModuuliDto> moduulit = new ArrayList<>();
}
