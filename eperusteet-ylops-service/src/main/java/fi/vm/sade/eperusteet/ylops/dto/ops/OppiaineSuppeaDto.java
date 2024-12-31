package fi.vm.sade.eperusteet.ylops.dto.ops;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class OppiaineSuppeaDto extends OppiaineBaseDto {

    @Schema(name = "oppimaarat")
    private Set<OppiaineSuppeaDto> oppimaarat;
    private Set<OppiaineenVuosiluokkakokonaisuusSuppeaDto> vuosiluokkakokonaisuudet;
    private String koodiUri;
    private String koodiArvo;
}

