package fi.vm.sade.eperusteet.ylops.resource.tpo;

import fi.vm.sade.eperusteet.ylops.dto.peruste.TPOOpetuksenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.PerusteTaiteenosaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenosaDto;
import fi.vm.sade.eperusteet.ylops.service.tpo.TaiteenperusopetusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/opetussuunnitelmat/{opsId}/taiteenperusopetus")
@Tag(name = "Taiteenperusopetus")
public class TaiteenperusopetusController {

    @Autowired
    private TaiteenperusopetusService taiteenperusopetusService;

    @RequestMapping(value = "/peruste", method = RequestMethod.GET)
    public TPOOpetuksenSisaltoDto getPerusteSisalto(
            @PathVariable final Long opsId) {
        return taiteenperusopetusService.getPerusteSisalto(opsId);
    }

    @RequestMapping(value = "/peruste/taiteenalat/{koodiUri}", method = RequestMethod.GET)
    public TpoPerusteenTaiteenalaDto getPerusteenTaiteenala(
            @PathVariable final Long opsId,
            @PathVariable final String koodiUri) {
        return taiteenperusopetusService.getPerusteenTaiteenala(opsId, koodiUri);
    }

    @RequestMapping(value = "/peruste/taiteenosat/{perusteenTaiteenosanId}", method = RequestMethod.GET)
    public PerusteTaiteenosaDto getPerusteenTaiteenosa(
            @PathVariable final Long opsId,
            @PathVariable final Long perusteenTaiteenosanId) {
        return taiteenperusopetusService.getPerusteenTaiteenosa(opsId, perusteenTaiteenosanId);
    }

    @RequestMapping(value = "/taiteenalat", method = RequestMethod.GET)
    public List<TaiteenalaDto> getTaiteenalat(
            @PathVariable final Long opsId) {
        return taiteenperusopetusService.getTaiteenalat(opsId);
    }

    @RequestMapping(value = "/taiteenalat/{taiteenalaId}", method = RequestMethod.GET)
    public TaiteenalaDto getTaiteenala(
            @PathVariable final Long opsId,
            @PathVariable final Long taiteenalaId) {
        return taiteenperusopetusService.getTaiteenala(opsId, taiteenalaId);
    }

    @RequestMapping(value = "/taiteenosat/{taiteenosaId}", method = RequestMethod.GET)
    public TaiteenosaDto getTaiteenosa(
            @PathVariable final Long opsId,
            @PathVariable final Long taiteenosaId) {
        return taiteenperusopetusService.getTaiteenosa(opsId, taiteenosaId);
    }

    @RequestMapping(value = "/taiteenalat", method = RequestMethod.POST)
    public TaiteenalaDto addTaiteenala(
            @PathVariable final Long opsId,
            @RequestBody final TaiteenalaDto taiteenalaDto) {
        return taiteenperusopetusService.addTaiteenala(opsId, taiteenalaDto);
    }

    @RequestMapping(value = "/taiteenalat/{taiteenalaId}", method = RequestMethod.POST)
    public TaiteenalaDto updateTaiteenala(
            @PathVariable final Long opsId,
            @PathVariable final Long taiteenalaId,
            @RequestBody final TaiteenalaDto taiteenalaDto) {
        return taiteenperusopetusService.updateTaiteenala(opsId, taiteenalaId, taiteenalaDto);
    }

    @RequestMapping(value = "/taiteenosat/{taiteenosaId}", method = RequestMethod.POST)
    public TaiteenosaDto updateTaiteenosa(
            @PathVariable final Long opsId,
            @PathVariable final Long taiteenosaId,
            @RequestBody final TaiteenosaDto taiteenosaDto) {
        return taiteenperusopetusService.updateTaiteenosa(opsId, taiteenosaId, taiteenosaDto);
    }

    @RequestMapping(value = "/taiteenalat/{taiteenalaId}", method = RequestMethod.DELETE)
    public void removeTaiteenala(
            @PathVariable final Long opsId,
            @PathVariable final Long taiteenalaId) {
        taiteenperusopetusService.removeTaiteenala(opsId, taiteenalaId);
    }
}
