package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = { "koodi" })
public class Lops2019OpintojaksonOppiaineDto {
    private String koodi;
    private Long laajuus;
}
