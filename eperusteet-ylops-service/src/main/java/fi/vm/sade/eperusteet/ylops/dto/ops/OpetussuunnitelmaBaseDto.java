package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.ops.Identifiable;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsIdentifiable;
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
@Schema(description = "Opetussuunnitelman yhteiset perustiedot.")
public class OpetussuunnitelmaBaseDto implements Serializable, Identifiable, OpsIdentifiable {

    @Schema(description = "Opetussuunnitelman yksilöivä tunniste.")
    private Long id;

    @Schema(description = "Kielet, joilla opetussuunnitelma on julkaistu.")
    private Set<Kieli> julkaisukielet;

    @Schema(description = "Opetussuunnitelmaan liitetyt organisaatiot.")
    private Set<OrganisaatioDto> organisaatiot;

    @Schema(description = "Koulutuksen järjestäjäorganisaatio.")
    private OrganisaatioDto koulutuksenjarjestaja;

    @Schema(description = "Opetussuunnitelmaan liitetyt kunnat (koodisto).")
    private Set<KoodistoDto> kunnat;

    @Schema(description = "Opetussuunnitelman kuvaus lokalisoituna.")
    private LokalisoituTekstiDto kuvaus;

    @Schema(description = "Opetussuunnitelman luoneen käyttäjän tunniste.")
    private String luoja;

    @Schema(description = "Luontiaika.")
    private Date luotu;

    @Schema(description = "Viimeisin muokkausaika.")
    private Date muokattu;

    @Schema(description = "Viimeksi muokanneen käyttäjän tunniste.")
    private String muokkaaja;

    @Schema(description = "Hyväksyjätaho.")
    private String hyvaksyjataho;

    @Schema(description = "Opetussuunnitelman nimi lokalisoituna.")
    private LokalisoituTekstiDto nimi;

    @Schema(description = "Opetussuunnitelman perusteen diaarinumero.")
    private String perusteenDiaarinumero;

    @Schema(description = "Opetussuunnitelman perusteen tunniste ePerusteet-palvelussa.")
    private Long perusteenId;

    @Schema(description = "Opetussuunnitelman tila (esim. `luonnos`, `valmis`, `julkaistu`).")
    private Tila tila;

    @Schema(description = "Opetussuunnitelman tyyppi: `ops` (varsinaisen opetussuunnitelman) tai `pohja` (pohjaopetussuunnitelma).")
    private Tyyppi tyyppi;

    @Schema(description = "Koulutustyyppi (koodistoarvo, esim. `koulutustyyppi_16`).")
    private KoulutusTyyppi koulutustyyppi;

    @Schema(description = "Koulutustyypin toteutusmalli (esim. `perusopetus`, `lops2019`).")
    private KoulutustyyppiToteutus toteutus;

    @Schema(description = "Opetussuunnitelman päätöspäivämäärä.")
    private Date paatospaivamaara;

    @Schema(description = "Ryhmän OID, jos opetussuunnitelma kuuluu ryhmään.")
    private String ryhmaoid;

    @Schema(description = "Ryhmän nimi.")
    private String ryhmanNimi;

    @Schema(description = "Onko opetussuunnitelma esikatseltavissa ennen varsinaista julkaisua.")
    private boolean esikatseltavissa;

    @Schema(description = "Onko opetussuunnitelma ainepainotteinen.")
    private boolean ainepainoitteinen;

    @Schema(description = "Perusteen voimassaolon alkupäivä.")
    private Date perusteenVoimassaoloAlkaa;

    @Schema(description = "Perusteen voimassaolon päättymispäivä.")
    private Date perusteenVoimassaoloLoppuu;

    @Schema(description = "Tuodaanko pohjaopetussuunnitelman opintojaksot tähän opetussuunnitelmaan.")
    private boolean tuoPohjanOpintojaksot;

    @Schema(description = "Tuodaanko pohjaopetussuunnitelman oppimäärät tähän opetussuunnitelmaan.")
    private boolean tuoPohjanOppimaarat;

    @Schema(description = "Perustetietojen viimeisin tuontiaika.")
    private Date perusteDataTuontiPvm = new Date();

    @Schema(description = "Viimeisin synkronointiaika perusteen kanssa.")
    private Date viimeisinSyncPvm;

    @Schema(description = "Viimeisimmän julkaisun ajankohta.")
    private Date viimeisinJulkaisuAika;
}
