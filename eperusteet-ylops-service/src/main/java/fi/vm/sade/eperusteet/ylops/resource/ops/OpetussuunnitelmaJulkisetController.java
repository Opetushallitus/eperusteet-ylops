package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaistuQuery;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkinenDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaQuery;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/opetussuunnitelmat/julkiset")
@Tag(name = "Opetussuunnitelmat julkiset")
public class OpetussuunnitelmaJulkisetController {
    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @RequestMapping(method = RequestMethod.GET)
    @Parameters({
            @Parameter(name = "koulutustyyppi", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "koulutustyyppi (koodistokoodi)"),
            @Parameter(name = "organisaatio", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "organisaatio oid (organisaatiopalvelusta)"),
            @Parameter(name = "tyyppi", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "ops | pohja"),
            @Parameter(name = "perusteenId", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "perusterajaus"),
            @Parameter(name = "perusteenDiaarinumero", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "perusterajaus")
    })
    public List<OpetussuunnitelmaJulkinenDto> getAllJulkiset(@Parameter(hidden = true) OpetussuunnitelmaQuery query) {
        return opetussuunnitelmaService.getAllJulkiset(query);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<OpetussuunnitelmaJulkinenDto> getOpetussuunnitelmanJulkisetTiedot(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(opetussuunnitelmaService.getOpetussuunnitelmaJulkinen(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/julkaisut")
    @Parameters({
            @Parameter(name = "koulutustyypit", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "koulutustyypit"),
            @Parameter(name = "nimi", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY, description = "nimi"),
            @Parameter(name = "kieli", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY, description = "kieli"),
            @Parameter(name = "perusteenDiaarinumero", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY, description = "perusteenDiaarinumero"),
            @Parameter(name = "sivu", schema = @Schema(implementation = Integer.class), in = ParameterIn.QUERY),
            @Parameter(name = "sivukoko", schema = @Schema(implementation = Integer.class), in = ParameterIn.QUERY),
    })
    public Page<OpetussuunnitelmaJulkinenDto> getAllJulkaistutOpetussuunnitelmat(@Parameter(hidden = true) OpetussuunnitelmaJulkaistuQuery query) {
        return opetussuunnitelmaService.getAllJulkaistutOpetussuunnitelmat(query);
    }

    @RequestMapping(value = "/{id}/julkaisu", method = RequestMethod.GET)
    public ResponseEntity<OpetussuunnitelmaExportDto> getOpetussuunnitelmaJulkaistu(
            @PathVariable("id") final Long id,
            @RequestParam(required = false) Integer revision) {
        return new ResponseEntity<>(opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(id, revision), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/kaikki")
    public List<OpetussuunnitelmaJulkinenDto> getKaikkiJulkaistutOpetussuunnitelmat() {
        return opetussuunnitelmaService.getKaikkiJulkaistutOpetussuunnitelmat();
    }

}
