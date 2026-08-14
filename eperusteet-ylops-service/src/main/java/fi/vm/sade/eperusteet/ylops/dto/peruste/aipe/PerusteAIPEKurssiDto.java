package fi.vm.sade.eperusteet.ylops.dto.peruste.aipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerusteAIPEKurssiDto {
    private Long id;
    private UUID tunniste;
    private LokalisoituTekstiDto nimi;
    private KoodiDto koodi;
    private LokalisoituTekstiDto kuvaus;
    private Set<Reference> tavoitteet = new HashSet<>();
}
