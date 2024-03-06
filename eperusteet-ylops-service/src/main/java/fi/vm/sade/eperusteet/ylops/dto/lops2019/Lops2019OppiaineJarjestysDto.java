package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lops2019OppiaineJarjestysDto {
    private Long id;
    private String koodi;
    private Integer jarjestys;
}
