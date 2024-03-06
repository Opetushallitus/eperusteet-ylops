package fi.vm.sade.eperusteet.ylops.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "tunniste")
public class PerusteLaajaalainenosaaminenDto implements ReferenceableDto {
    private Long id;
    private UUID tunniste;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto kuvaus;
}
