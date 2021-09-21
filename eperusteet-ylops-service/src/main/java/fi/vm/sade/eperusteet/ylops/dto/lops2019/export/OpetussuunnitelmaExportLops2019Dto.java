package fi.vm.sade.eperusteet.ylops.dto.lops2019.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineJarjestysDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.laajaalainenosaaminen.Lops2019LaajaAlainenOsaaminenDto;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpetussuunnitelmaExportLops2019Dto extends OpetussuunnitelmaExportDto {
    private List<Lops2019LaajaAlainenOsaaminenDto> laajaAlaisetOsaamiset = new ArrayList<>();
    private List<Lops2019OpintojaksoExportDto> opintojaksot = new ArrayList<>();
    private List<Lops2019OppiaineExportDto> valtakunnallisetOppiaineet = new ArrayList<>();
    private List<Lops2019PaikallinenOppiaineExportDto> paikallisetOppiaineet = new ArrayList<>();
    private Set<Lops2019OppiaineJarjestysDto> oppiaineJarjestykset = new HashSet<>();
}
