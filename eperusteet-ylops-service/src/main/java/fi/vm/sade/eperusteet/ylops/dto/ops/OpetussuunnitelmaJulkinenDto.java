package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Julkaistun opetussuunnitelman perustiedot julkisessa rajapinnassa (esim. hakulistan tulos).")
public class OpetussuunnitelmaJulkinenDto implements Serializable {

    @Schema(description = "Opetussuunnitelman yksilöivä tunniste.")
    private Long id;

    @Schema(description = "Kielet, joilla julkaisu on saatavilla.")
    private Set<Kieli> julkaisukielet;

    @Schema(description = "Julkaisuun liitetyt organisaatiot.")
    private Set<OrganisaatioDto> organisaatiot;

    @Schema(description = "Julkaisuun liitetyt kunnat (koodisto).")
    private Set<KoodistoDto> kunnat;

    @Schema(description = "Opetussuunnitelman nimi lokalisoituna.")
    private LokalisoituTekstiDto nimi;

    @Schema(description = "Koulutustyyppi (koodistoarvo, esim. `koulutustyyppi_16`).")
    private KoulutusTyyppi koulutustyyppi;

    @Schema(description = "Opetussuunnitelman tila (esim. `julkaistu`).")
    private Tila tila;

    @Schema(description = "Opetussuunnitelman tyyppi: `ops` tai `pohja`.")
    private Tyyppi tyyppi;

    @Schema(description = "Onko opetussuunnitelma esikatseltavissa ennen varsinaista julkaisua.")
    private boolean esikatseltavissa;

    @Schema(description = "Koulutustyypin toteutusmalli (esim. `perusopetus`, `lops2019`).")
    private KoulutustyyppiToteutus toteutus;

    @Schema(description = "Viimeisimmän julkaisun ajankohta.")
    private Date julkaisuaika;
}
