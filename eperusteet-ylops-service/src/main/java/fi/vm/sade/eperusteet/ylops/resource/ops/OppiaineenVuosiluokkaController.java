package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.dto.ops.OpetuksenTavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkaDto;
import fi.vm.sade.eperusteet.ylops.resource.util.Responses;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/opetussuunnitelmat/{opsId}/oppiaineet/{oppiaineId}/vuosiluokkakokonaisuudet/{kokonaisuusId}/vuosiluokat")
@Tag(name = "OppiaineenVuosiluokat")
public class OppiaineenVuosiluokkaController {

    @Autowired
    private OppiaineService oppiaineService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<OppiaineenVuosiluokkaDto> getOppiaineenvuosiluokka(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("kokonaisuusId") final Long kokonaisuusId,
            @PathVariable("id") final Long id) {
        OppiaineenVuosiluokkaDto oa = oppiaineService.getVuosiluokka(opsId, oppiaineId, id);
        return Responses.ofNullable(oa);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public OppiaineenVuosiluokkaDto updateVuosiluokanSisalto(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("kokonaisuusId") final Long kokonaisuusId,
            @PathVariable("id") final Long id,
            @RequestBody OppiaineenVuosiluokkaDto dto) {
        dto.setId(id);
        return oppiaineService.updateVuosiluokanSisalto(opsId, oppiaineId, kokonaisuusId, dto);
    }

    @RequestMapping(value = "/{id}/valinnainen", method = RequestMethod.POST)
    public OppiaineenVuosiluokkaDto updateValinnaisenVuosiluokanSisalto(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("kokonaisuusId") final Long kokonaisuusId,
            @PathVariable("id") final Long id,
            @RequestBody List<OpetuksenTavoiteDto> tavoitteetDto) {
        return oppiaineService.updateValinnaisenVuosiluokanSisalto(opsId, oppiaineId, id, tavoitteetDto);
    }
}
