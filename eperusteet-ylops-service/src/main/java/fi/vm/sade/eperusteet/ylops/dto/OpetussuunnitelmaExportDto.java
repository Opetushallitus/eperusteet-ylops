package fi.vm.sade.eperusteet.ylops.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaNimiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpetussuunnitelmaExportDto extends OpetussuunnitelmaBaseDto {
    private PerusteInfoDto peruste;
    private OpetussuunnitelmaNimiDto pohja;
    private TekstiKappaleViiteExportDto.Puu tekstit;
}
