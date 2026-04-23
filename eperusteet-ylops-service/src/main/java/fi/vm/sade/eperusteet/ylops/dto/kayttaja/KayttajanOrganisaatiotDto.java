package fi.vm.sade.eperusteet.ylops.dto.kayttaja;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KayttajanOrganisaatiotDto implements Serializable {
  private Set<String> kunnat;
  private Set<String> organisaatioOids;
}
