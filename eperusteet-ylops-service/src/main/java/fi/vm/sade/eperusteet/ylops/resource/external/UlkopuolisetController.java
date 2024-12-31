package fi.vm.sade.eperusteet.ylops.resource.external;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.VirkailijaQueryDto;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.LokalisointiDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioQueryDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.TiedoteQueryDto;
import fi.vm.sade.eperusteet.ylops.resource.util.KieliConverter;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.LokalisointiService;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.ylops.service.external.KoodistoService;
import fi.vm.sade.eperusteet.ylops.service.external.OrganisaatioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/ulkopuoliset")
@Tag(name = "Ulkopuoliset")
public class UlkopuolisetController {

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @Autowired
    private LokalisointiService lokalisointiService;

    @InitBinder
    public void initBinder(final WebDataBinder webdataBinder) {
        webdataBinder.registerCustomEditor(Kieli.class, new KieliConverter());
    }

    @RequestMapping(value = "/kayttajatiedot/{oid:.+}", method = GET)
    @ResponseBody
    public ResponseEntity<KayttajanTietoDto> getKayttajanTiedot(@PathVariable("oid") final String oid) {
        return new ResponseEntity<>(kayttajanTietoService.hae(oid), HttpStatus.OK);
    }

    @RequestMapping(value = "/julkaistutperusteet", method = GET)
    @ResponseBody
    public ResponseEntity<List<PerusteInfoDto>> getPerusteet() {
        return new ResponseEntity<>(eperusteetService.findPerusteet(true), HttpStatus.OK);
    }

    @RequestMapping(value = "/perusopetusperusteet", method = GET)
    @Deprecated
    @ResponseBody
    public ResponseEntity<List<PerusteInfoDto>> getPerusopetusperusteet() {
        return new ResponseEntity<>(eperusteetService.findPerusopetuksenPerusteet(), HttpStatus.OK);
    }

    @RequestMapping(value = "/perusopetusperusteet/{id}", method = GET)
    @Deprecated
    @ResponseBody
    public PerusteDto getPerusopetusperuste(@PathVariable(value = "id") final Long id) {
        return eperusteetService.getPerusteById(id);
    }

    @RequestMapping(value = "/peruste/{id}", method = GET)
    @ResponseBody
    public PerusteDto getYlopsPeruste(@PathVariable(value = "id") final Long id) {
        return eperusteetService.getPerusteById(id);
    }

    @RequestMapping(value = "/peruste/{id}/julkaisuhetki/{julkaisuhetki}", method = GET)
    @ResponseBody
    public PerusteDto getOpetussuunnitelmanJulkaisuhetkenPeruste(
            @PathVariable(value = "id") final Long id,
            @PathVariable(value = "julkaisuhetki") final Long julkaisuhetki) {
        return eperusteetService.getPerusteenJulkaisuByGlobalversionMuutosaikaAsDto(id, new java.util.Date(julkaisuhetki));
    }

    @RequestMapping(value = "/lukiokoulutusperusteet", method = GET)
    @Deprecated
    @ResponseBody
    public ResponseEntity<List<PerusteInfoDto>> getLukiokoulutusperusteet() {
        return new ResponseEntity<>(eperusteetService.findLukiokoulutusPerusteet(), HttpStatus.OK);
    }

    @RequestMapping(value = "/lukiokoulutusperusteet/{id}", method = GET)
    @Deprecated
    @ResponseBody
    public PerusteDto getLukiokoulutusperuste(@PathVariable(value = "id") final Long id) {
        return eperusteetService.getPerusteById(id);
    }

    @RequestMapping(value = "/tiedotteet", method = GET)
    @ResponseBody
    public ResponseEntity<JsonNode> getTiedotteet(@RequestParam(value = "jalkeen", required = false) final Long jalkeen) {
        return new ResponseEntity<>(eperusteetService.getTiedotteet(jalkeen), HttpStatus.OK);
    }

    @RequestMapping(value = "/tiedotteet/haku", method = GET)
    @ResponseBody
    @Operation(summary = "tiedotteiden haku")
    @Parameters({
            @Parameter(name = "sivu", schema = @Schema(implementation = Integer.class), in = ParameterIn.QUERY),
            @Parameter(name = "sivukoko", schema = @Schema(implementation = Integer.class), in = ParameterIn.QUERY),
            @Parameter(name = "kieli", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "tiedotteen kieli"),
            @Parameter(name = "nimi", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY, description = "hae nimellä"),
            @Parameter(name = "perusteId", schema = @Schema(implementation = Long.class), in = ParameterIn.QUERY, description = "hae perusteeseen liitetyt tiedotteet"),
            @Parameter(name = "perusteeton", schema = @Schema(implementation = Boolean.class), in = ParameterIn.QUERY, description = "hae perusteettomat tiedotteet"),
            @Parameter(name = "julkinen", schema = @Schema(implementation = Boolean.class), in = ParameterIn.QUERY, description = "hae julkiset tiedotteet"),
            @Parameter(name = "yleinen", schema = @Schema(implementation = Boolean.class), in = ParameterIn.QUERY, description = "hae yleiset tiedotteet"),
            @Parameter(name = "tiedoteJulkaisuPaikka", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "tiedotteen julkaisupaikat"),
            @Parameter(name = "perusteIds", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "number")), description = "tiedotteen perusteiden"),
            @Parameter(name = "koulutusTyyppi", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "tiedotteen koulutustyypit"),
            @Parameter(name = "jarjestys", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY, description = "tiedotteen jarjestys"),
            @Parameter(name = "jarjestysNouseva", schema = @Schema(implementation = Boolean.class), in = ParameterIn.QUERY, description = "tiedotteen jarjestyksen suunta")
    })
    public ResponseEntity<JsonNode> getTiedotteetHaku(@Parameter(hidden = true) TiedoteQueryDto queryDto) {
        return ResponseEntity.ok(eperusteetService.getTiedotteetHaku(queryDto));
    }

    @RequestMapping(value = "/organisaatiot/koulutustoimijat", method = GET)
    @ResponseBody
    @Parameters({
            @Parameter(name = "kunta", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string"))),
            @Parameter(name = "oppilaitostyyppi", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")))
    })
    public ResponseEntity<List<OrganisaatioLaajaDto>> getKoulutustoimijat(@Parameter(hidden = true) OrganisaatioQueryDto query) {
        List<OrganisaatioLaajaDto> toimijat = organisaatioService.getKoulutustoimijat(query);
        return new ResponseEntity<>(toimijat, HttpStatus.OK);
    }

    @RequestMapping(value = "/organisaatiot/{oid}", method = GET)
    @ResponseBody
    public ResponseEntity<JsonNode> getOrganisaatio(@PathVariable(value = "oid") final String organisaatioOid) {
        JsonNode peruskoulut = organisaatioService.getOrganisaatio(organisaatioOid);
        return new ResponseEntity<>(peruskoulut, HttpStatus.OK);
    }

    @RequestMapping(value = "/organisaatiot/virkailijat", method = GET)
    @ResponseBody
    @Operation(summary = "virkailijoiden haku")
    @Parameters({
            @Parameter(name = "oid", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "organisaation oid")
    })
    public ResponseEntity<JsonNode> getOrganisaatioVirkailijat(@Parameter(hidden = true) VirkailijaQueryDto dto) {
        JsonNode virkailijat = organisaatioService.getOrganisaatioVirkailijat(dto.getOid());
        return ResponseEntity.ok(virkailijat);
    }

    @RequestMapping(value = "/organisaatiot", method = GET)
    @ResponseBody
    public List<JsonNode> getUserOrganisations() {
        return kayttajanTietoService.haeOrganisaatioOikeudet().stream()
                .map(organisaatioService::getOrganisaatio)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/organisaatiot/peruskoulutoimijat/{kuntaIdt}", method = GET)
    @ResponseBody
    @Deprecated
    public ResponseEntity<JsonNode> getPeruskoulut(@PathVariable(value = "kuntaIdt") final List<String> kuntaIdt) {
        JsonNode peruskoulut = organisaatioService.getPeruskoulutoimijat(kuntaIdt);
        return new ResponseEntity<>(peruskoulut, HttpStatus.OK);
    }

    @RequestMapping(value = "/organisaatiot/lukiotoimijat/{kuntaIdt}", method = GET)
    @ResponseBody
    @Deprecated
    public ResponseEntity<JsonNode> getLukiot(@PathVariable(value = "kuntaIdt") final List<String> kuntaIdt) {
        JsonNode lukiot = organisaatioService.getLukiotoimijat(kuntaIdt);
        return new ResponseEntity<>(lukiot, HttpStatus.OK);
    }

    @RequestMapping(value = "/organisaatiot/peruskoulut/oid/{oid}", method = GET)
    @ResponseBody
    @Deprecated
    public ResponseEntity<JsonNode> getPeruskoulutByOid(@PathVariable(value = "oid") final String oid) {
        JsonNode peruskoulut = organisaatioService.getPeruskoulutByOid(oid);
        return new ResponseEntity<>(peruskoulut, HttpStatus.OK);
    }

    @RequestMapping(value = "/organisaatiot/lukiot/oid/{oid}", method = GET)
    @ResponseBody
    @Deprecated
    public ResponseEntity<JsonNode> getLukiotByOid(@PathVariable(value = "oid") final String oid) {
        JsonNode lukiot = organisaatioService.getLukioByOid(oid);
        return new ResponseEntity<>(lukiot, HttpStatus.OK);
    }

    @RequestMapping(value = "/organisaatiot/peruskoulut/{kuntaId}", method = GET)
    @ResponseBody
    @Deprecated
    public ResponseEntity<JsonNode> getPeruskoulutByKuntaId(@PathVariable(value = "kuntaId") final String kuntaId) {
        JsonNode peruskoulut = organisaatioService.getPeruskoulutByKuntaId(kuntaId);
        return new ResponseEntity<>(peruskoulut, HttpStatus.OK);
    }

    @RequestMapping(value = "/organisaatiot/lukiot/{kuntaId}", method = GET)
    @ResponseBody
    @Deprecated
    public ResponseEntity<JsonNode> getLukiotByKuntaId(@PathVariable(value = "kuntaId") final String kuntaId) {
        JsonNode lukiot = organisaatioService.getLukiotByKuntaId(kuntaId);
        return new ResponseEntity<>(lukiot, HttpStatus.OK);
    }

    @RequestMapping(value = "/koodisto/{koodisto}", method = GET)
    @ResponseBody
    public ResponseEntity<List<KoodistoKoodiDto>> kaikkiKoodistonKoodit(
            @PathVariable("koodisto") final String koodisto,
            @RequestParam(value = "haku", required = false) final String haku) {
        return new ResponseEntity<>(haku == null || haku.isEmpty()
                ? koodistoService.getAll(koodisto)
                : koodistoService.filterBy(koodisto, haku), HttpStatus.OK);
    }

    @RequestMapping(value = "/koodisto/{koodisto}/{koodi}", method = GET)
    @ResponseBody
    public ResponseEntity<KoodistoKoodiDto> yksiKoodistokoodi(
            @PathVariable("koodisto") final String koodisto,
            @PathVariable("koodi") final String koodi) {
        return new ResponseEntity<>(koodistoService.get(koodisto, koodi), HttpStatus.OK);
    }

    @RequestMapping(value = "/koodisto/relaatio/sisaltyy-alakoodit/{koodi}", method = GET)
    @ResponseBody
    public ResponseEntity<List<KoodistoKoodiDto>> koodinAlarelaatiot(
            @PathVariable("koodi") final String koodi) {
        return new ResponseEntity<>(koodistoService.getAlarelaatio(koodi), HttpStatus.OK);
    }

    @RequestMapping(value = "/koodisto/relaatio/sisaltyy-ylakoodit/{koodi}", method = GET)
    @ResponseBody
    public ResponseEntity<List<KoodistoKoodiDto>> koodinYlarelaatiot(
            @PathVariable("koodi") final String koodi) {
        return new ResponseEntity<>(koodistoService.getYlarelaatio(koodi), HttpStatus.OK);
    }

    /**
     * Hakee käyttäjälle liitetyt organisaatioryhmät
     *
     * @return Organisaatioryhmät
     */
    @RequestMapping(value = "/organisaatioryhmat", method = GET)
    @ResponseBody
    public ResponseEntity<List<JsonNode>> getOrganisaatioRyhmat() {
        List<JsonNode> ryhmat = organisaatioService.getRyhmat();
        return new ResponseEntity<>(ryhmat, HttpStatus.OK);
    }

    /**
     * Lataa palvelun käännösavaimet
     *
     * @return Organisaatioryhmät
     */
    @RequestMapping(value = "/lokalisoinnit", method = GET)
    @ResponseBody
    public ResponseEntity<Map<Kieli, List<LokalisointiDto>>> getLokalisoinnit() {
        return ResponseEntity.ok(lokalisointiService.getAll());
    }


}
