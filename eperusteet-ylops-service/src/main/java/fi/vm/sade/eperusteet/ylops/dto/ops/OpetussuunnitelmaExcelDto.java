package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.util.Pair;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpetussuunnitelmaExcelDto {
    private String koodi;
    private LokalisoituTekstiDto nimi;
    private List<Pair<String, LokalisoituTekstiDto>> tutkinnonOsat;
}
