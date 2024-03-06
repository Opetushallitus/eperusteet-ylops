package fi.vm.sade.eperusteet.ylops.dto.liite;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiiteDto {
    private UUID id;
    private String tyyppi;
    private String nimi;
    private Date luotu;
}
