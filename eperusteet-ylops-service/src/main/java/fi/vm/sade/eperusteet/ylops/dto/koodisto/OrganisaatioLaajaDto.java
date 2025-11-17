package fi.vm.sade.eperusteet.ylops.dto.koodisto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisaatioLaajaDto extends OrganisaatioDto {
    private List<String> tyypit;
    private LokalisoituTekstiDto nimi;
    private String kotipaikkaUri;
    private String oppilaitosKoodi;
    private String oppilaitostyyppi;
    private Set<String> organisaatiotyypit;
    private String parentOid;
    private String parentOidPath;
    private List<OrganisaatioLaajaDto> children;
    private String status;

    public List<String> getParentPath() {
        if (StringUtils.isEmpty(this.parentOidPath)) {
            return new ArrayList<>();
        }
        else {
            String[] split = this.parentOidPath.split("\\|");
            return Arrays.stream(split)
                    .filter(x -> !x.isEmpty())
                    .collect(Collectors.toList());
        }
    }
}
