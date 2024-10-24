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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/ulkopuoliset")
@ApiIgnore
@Api("Ulkopuoliset")
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
    @ApiOperation(value = "tiedotteiden haku")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "kieli", dataType = "string", paramType = "query", allowMultiple = true, value = "tiedotteen kieli"),
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query", value = "hae nimellä"),
            @ApiImplicitParam(name = "perusteId", dataType = "long", paramType = "query", value = "hae perusteeseen liitetyt tiedotteet"),
            @ApiImplicitParam(name = "perusteeton", dataType = "boolean", paramType = "query", value = "hae perusteettomat tiedotteet"),
            @ApiImplicitParam(name = "julkinen", dataType = "boolean", paramType = "query", value = "hae julkiset tiedotteet"),
            @ApiImplicitParam(name = "yleinen", dataType = "boolean", paramType = "query", value = "hae yleiset tiedotteet"),
            @ApiImplicitParam(name = "tiedoteJulkaisuPaikka", dataType = "string", paramType = "query", allowMultiple = true, value = "tiedotteen julkaisupaikat"),
            @ApiImplicitParam(name = "perusteIds", dataType = "long", paramType = "query", allowMultiple = true, value = "tiedotteen perusteiden"),
            @ApiImplicitParam(name = "koulutusTyyppi", dataType = "string", paramType = "query", allowMultiple = true, value = "tiedotteen koulutustyypit"),
            @ApiImplicitParam(name = "jarjestys", dataType = "string", paramType = "query", allowMultiple = false, value = "tiedotteen jarjestys"),
            @ApiImplicitParam(name = "jarjestysNouseva", dataType = "boolean", paramType = "query", allowMultiple = false, value = "tiedotteen jarjestyksen suunta")
    })
    public ResponseEntity<JsonNode> getTiedotteetHaku(@ApiIgnore TiedoteQueryDto queryDto) {
        return ResponseEntity.ok(eperusteetService.getTiedotteetHaku(queryDto));
    }

    @RequestMapping(value = "/organisaatiot/koulutustoimijat", method = GET)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "kunta", dataType = "string", allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "oppilaitostyyppi", dataType = "string", allowMultiple = true, paramType = "query")
    })
    public ResponseEntity<List<OrganisaatioLaajaDto>> getKoulutustoimijat(
            OrganisaatioQueryDto query) {
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
    @ApiOperation(value = "virkailijoiden haku")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oid", dataType = "string", paramType = "query", allowMultiple = true, value = "organisaation oid")
    })
    public ResponseEntity<JsonNode> getOrganisaatioVirkailijat(VirkailijaQueryDto dto) {
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
