package fi.vm.sade.eperusteet.ylops.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaNimiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Julkaistun opetussuunnitelman sisältö julkiseen rajapintaan.")
public class OpetussuunnitelmaExportDto extends OpetussuunnitelmaBaseDto {

    @Schema(description = "Opetussuunnitelman perusteen tiedot julkaisuhetken version mukaan.")
    private PerusteInfoDto peruste;

    @Schema(description = "Pohjaopetussuunnitelman nimitiedot, jos opetussuunnitelma on luotu pohjalle.")
    private OpetussuunnitelmaNimiDto pohja;

    @Schema(description = "Opetussuunnitelman tekstirakenne puumuotona.")
    private TekstiKappaleViiteExportDto.Puu tekstit;
}
