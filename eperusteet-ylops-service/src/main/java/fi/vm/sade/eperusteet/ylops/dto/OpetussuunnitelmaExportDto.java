package fi.vm.sade.eperusteet.ylops.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaNimiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpetussuunnitelmaExportDto extends OpetussuunnitelmaBaseDto {
    private PerusteInfoDto peruste;
    private OpetussuunnitelmaNimiDto pohja;
    private TekstiKappaleViiteExportDto.Puu tekstit;
}
