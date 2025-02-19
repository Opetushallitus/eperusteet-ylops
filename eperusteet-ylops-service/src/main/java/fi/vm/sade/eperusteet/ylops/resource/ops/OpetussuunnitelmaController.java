package fi.vm.sade.eperusteet.ylops.resource.ops;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.dto.JarjestysDto;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.OppiaineOpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaNimiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaStatistiikkaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaTilastoDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsVuosiluokkakokonaisuusKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteLaajaalainenosaaminenDto;
import fi.vm.sade.eperusteet.ylops.service.external.KoodistoService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.security.Permission;
import fi.vm.sade.eperusteet.ylops.service.security.PermissionManager;
import fi.vm.sade.eperusteet.ylops.service.security.TargetType;
import fi.vm.sade.eperusteet.ylops.service.util.JulkaisuService;
import fi.vm.sade.eperusteet.ylops.service.util.Validointi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/opetussuunnitelmat")
@Tag(name = "Opetussuunnitelmat")
public class OpetussuunnitelmaController {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private PermissionManager permissionManager;

    @Autowired
    private JulkaisuService julkaisuService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<OpetussuunnitelmaInfoDto> getAll(
            @RequestParam(value = "tyyppi", required = false) Tyyppi tyyppi,
            @RequestParam(value = "tila", required = false) Tila tila) {
        return opetussuunnitelmaService.getAll(tyyppi == null ? Tyyppi.OPS : tyyppi, tila);
    }

    @RequestMapping(value = "/sivutettu", method = RequestMethod.GET)
    @ResponseBody
    public Page<OpetussuunnitelmaInfoDto> getSivutettu(
            @RequestParam(value = "tyyppi") String tyyppi,
            @RequestParam(value = "tila") String tila,
            @RequestParam(value = "koulutustyyppi", required = false) String koulutusTyyppi,
            @RequestParam(value = "nimi", required = false) String nimi,
            @RequestParam(value = "jarjestys", required = false, defaultValue = "luotu") String jarjestys,
            @RequestParam(value = "jarjestyssuunta", required = false, defaultValue = "DESC") String jarjestysSuunta,
            @RequestParam(value = "sivu", required = false) final int sivu,
            @RequestParam(value = "sivukoko", required = false, defaultValue = "10") final int sivukoko,
            @RequestParam(value = "kieli", required = false, defaultValue = "fi") final String kieli) {
        return opetussuunnitelmaService.getSivutettu(
                Tyyppi.of(tyyppi),
                Tila.of(tila),
                StringUtils.isEmpty(koulutusTyyppi) ? null : KoulutusTyyppi.of(koulutusTyyppi),
                nimi, jarjestys, jarjestysSuunta, kieli,
                sivu, sivukoko);
    }

    @RequestMapping(value = "/pohjat", method = RequestMethod.GET)
    public List<OpetussuunnitelmaInfoDto> getOpetussuunnitelmienOpsPohjat() {
        return opetussuunnitelmaService.getOpetussuunnitelmaOpsPohjat();
    }

    @RequestMapping(value = "/{id}/organisaatiotarkistus", method = RequestMethod.GET)
    public OpetussuunnitelmaKevytDto getOpetussuunnitelmaOrganisaatiotarkistuksella(@PathVariable("id") final Long id) {
        return opetussuunnitelmaService.getOpetussuunnitelmaOrganisaatiotarkistuksella(id);
    }

    @RequestMapping(value = "/{id}/peruste", method = GET)
    @ResponseBody
    public PerusteInfoDto getOpetussuunnitelmanPeruste(
            @PathVariable(value = "id") final Long id) {
        return opetussuunnitelmaService.getPerusteBase(id);
    }

    @RequestMapping(value = "/statistiikka", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<OpetussuunnitelmaStatistiikkaDto> getStatistiikka() {
        return new ResponseEntity<>(opetussuunnitelmaService.getStatistiikka(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tilastot", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<OpetussuunnitelmaTilastoDto>> getOpetussuunnitelmaTilastot() {
        return new ResponseEntity<>(opetussuunnitelmaService.getOpetussuunnitelmaTilastot(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<OpetussuunnitelmaKevytDto> getOpetussuunnitelma(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(opetussuunnitelmaService.getOpetussuunnitelma(id));
    }

    @RequestMapping(value = "/{id}/nimi", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<OpetussuunnitelmaNimiDto> getOpetussuunnitelmaNimi(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(opetussuunnitelmaService.getOpetussuunnitelmaNimi(id));
    }

    @RequestMapping(value = "/{id}/pohja/vuosiluokkakokonaisuudet", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Set<OpsVuosiluokkakokonaisuusKevytDto>> getOpetussuunnitelmanPohjanVuosiluokkakokonaisuudet(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(opetussuunnitelmaService.getOpetussuunnitelmanPohjanVuosiluokkakokonaisuudet(id));
    }

    @RequestMapping(value = "/{id}/sisalto", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<JsonNode> getOpetussuunnitelmaSisalto(
            @PathVariable("id") final Long id,
            @RequestParam String query) {
        JsonNode result = julkaisuService.queryOpetussuunnitelmaJulkaisu(id, query);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/{id}/kaikki", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<OpetussuunnitelmaExportDto> getKaikki(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(opetussuunnitelmaService.getExportedOpetussuunnitelma(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/opetussuunnitelmat", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<OpetussuunnitelmaInfoDto>> getLapsiOpetussuunnitelmat(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(opetussuunnitelmaService.getLapsiOpetussuunnitelmat(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/laajaalaisetosaamiset", method = RequestMethod.GET)
    @ResponseBody
    public Collection<PerusteLaajaalainenosaaminenDto> getLaajalaisetosamiset(@PathVariable("id") final Long id) {
        return opetussuunnitelmaService.getLaajaalaisetosaamiset(id);
    }

    @RequestMapping(value = "/{id}/sync", method = RequestMethod.POST)
    public ResponseEntity sync(@PathVariable("id") final Long id) {
        opetussuunnitelmaService.syncPohja(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/vaihda/{pohjaId}", method = RequestMethod.POST)
    public ResponseEntity vaihdaPohja(
            @PathVariable("id") final Long id,
            @PathVariable("pohjaId") final Long pohjaId) {
        opetussuunnitelmaService.vaihdaPohja(id, pohjaId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/syncTekstitPohjasta", method = RequestMethod.POST)
    public ResponseEntity syncTekstitPohjasta(
            @PathVariable("id") final Long id) {
        opetussuunnitelmaService.syncTekstitPohjasta(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/opetussuunnitelmanPohjallaUusiaTeksteja", method = RequestMethod.GET)
    public ResponseEntity<Boolean> opetussuunnitelmanPohjallaUusiaTeksteja(
            @PathVariable("id") final Long id) {
        return ResponseEntity.ok(opetussuunnitelmaService.opetussuunnitelmanPohjallaUusiaTeksteja(id));
    }

    @RequestMapping(value = "/{id}/pohjanperustepaivittynyt", method = RequestMethod.GET)
    public ResponseEntity<Boolean> pohjanperustepaivittynyt(
            @PathVariable("id") final Long id) {
        return ResponseEntity.ok(opetussuunnitelmaService.pohjanPerustePaivittynyt(id));
    }

    @RequestMapping(value = "/{id}/pohjavaihtoehdot", method = RequestMethod.GET)
    public ResponseEntity<Set<OpetussuunnitelmaInfoDto>> haePohjavaihtoehdot(
            @PathVariable("id") final Long id) {
        Set<OpetussuunnitelmaInfoDto> opetussuunnitelmat = opetussuunnitelmaService.vaihdettavatPohjat(id);
        return ResponseEntity.ok(opetussuunnitelmat);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<OpetussuunnitelmaBaseDto> addOpetussuunnitelma(
            @RequestBody OpetussuunnitelmaLuontiDto opetussuunnitelmaDto) {
        if (opetussuunnitelmaDto.getTyyppi() == null) {
            opetussuunnitelmaDto.setTyyppi(Tyyppi.OPS);
        }

        if (opetussuunnitelmaDto.getTyyppi().equals(Tyyppi.POHJA)) {
            return new ResponseEntity<>(opetussuunnitelmaService.addPohja(opetussuunnitelmaDto),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(opetussuunnitelmaService.addOpetussuunnitelma(opetussuunnitelmaDto),
                    HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{opsId}/koodisto/{koodisto}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<KoodistoKoodiDto>> getKoodistonKoodit(
            @PathVariable("opsId") final Long opsId,
            @PathVariable final String koodisto) {
        List<KoodistoKoodiDto> koodit = koodistoService.getAll(koodisto);
        return new ResponseEntity<>(koodit, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/oppiainejarjestys", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity updateOppiainejarjestys(
            @PathVariable("id") final Long id,
            @RequestBody List<JarjestysDto> oppiainejarjestys) {
        opetussuunnitelmaService.updateOppiainejarjestys(id, oppiainejarjestys);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/oppiaineopintojaksojarjestys", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity updateOppiaineJaOpintojaksojarjestys(
            @PathVariable("id") final Long id,
            @RequestBody List<OppiaineOpintojaksoDto> oppiaineopintojaksojarjestys) {
        opetussuunnitelmaService.updateOppiaineJaOpintojaksojarjestys(id, oppiaineopintojaksojarjestys);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<OpetussuunnitelmaDto> updateOpetussuunnitelma(
            @PathVariable("id") final Long id,
            @RequestBody OpetussuunnitelmaDto opetussuunnitelmaDto) {
        opetussuunnitelmaDto.setId(id);
        return new ResponseEntity<>(opetussuunnitelmaService.updateOpetussuunnitelma(opetussuunnitelmaDto),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/importperustetekstit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<OpetussuunnitelmaDto> importPerusteTekstit(
            @PathVariable("id") final Long id,
            @RequestParam(value = "skip", required = false) boolean skip) {
        return new ResponseEntity<>(opetussuunnitelmaService.importPerusteTekstit(id, skip),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/opetussuunnitelmat", method = RequestMethod.POST)
    public ResponseEntity updateLapsiOpetussuunnitelmat(
            @PathVariable("id") final Long id) {
        opetussuunnitelmaService.updateLapsiOpetussuunnitelmat(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/tila/{tila}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<OpetussuunnitelmaDto> updateTila(
            @PathVariable final Long id,
            @PathVariable Tila tila) {
        return new ResponseEntity<>(opetussuunnitelmaService.updateTila(id, tila), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/validoi", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Validointi>> validoiOpetussuunnitelma(
            @PathVariable("id") final Long id) {
        return new ResponseEntity<>(opetussuunnitelmaService.validoiOpetussuunnitelma(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/oikeudet", method = RequestMethod.GET)
    public ResponseEntity<Map<TargetType, Set<Permission>>> getOikeudet() {
        return new ResponseEntity<>(permissionManager.getOpsPermissions(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/oikeudet", method = RequestMethod.GET)
    public ResponseEntity<Map<TargetType, Set<Permission>>> getOikeudetById(
            @PathVariable("id") final Long id) {
        return new ResponseEntity<>(permissionManager.getOpsPermissions(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/navigaatio", method = GET)
    public NavigationNodeDto getNavigation(
            @PathVariable final Long id,
            @RequestParam(value = "kieli", required = false, defaultValue = "fi") final String kieli
    ) {
        return opetussuunnitelmaService.buildNavigation(id, kieli);
    }

    @RequestMapping(value = "/{id}/navigaatio/public", method = GET)
    public NavigationNodeDto getNavigationPublic(
            @PathVariable final Long id,
            @RequestParam(value = "kieli", required = false, defaultValue = "fi") final String kieli,
            @RequestParam(value = "revision", required = false) final Integer revision
    ) {
        return opetussuunnitelmaService.buildNavigationPublic(id, kieli, revision);
    }

    @RequestMapping(value = "/{id}/palauteTekstirakenne", method = GET)
    @ResponseStatus(HttpStatus.OK)
    public void palautaTekstirakenne(@PathVariable final Long id) {
        opetussuunnitelmaService.palautaTekstirakenne(id);
    }
}
