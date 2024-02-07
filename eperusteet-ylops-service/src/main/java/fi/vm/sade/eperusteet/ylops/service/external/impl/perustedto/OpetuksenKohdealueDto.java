package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class OpetuksenKohdealueDto implements ReferenceableDto {
    public Long id;
    public PerusteenLokalisoituTekstiDto nimi;
    public PerusteenLokalisoituTekstiDto kuvaus;
}
