package fi.vm.sade.eperusteet.ylops.resource.tpo;

import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenOpintokokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalanVaiheDto;
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
    public List<TpoPerusteenTaiteenalaDto> getPerusteTaiteenalat(@PathVariable final Long opsId) {
        return taiteenperusopetusService.getPerusteTaiteenalat(opsId);
    }

    @RequestMapping(value = "/peruste/taiteenalat/{perusteenTaiteenalaId}", method = RequestMethod.GET)
    public TpoPerusteenTaiteenalaDto getPerusteTaiteenala(@PathVariable final Long opsId,
                                                          @PathVariable final Long perusteenTaiteenalaId) {
        return taiteenperusopetusService.getPerusteTaiteenala(opsId, perusteenTaiteenalaId);
    }

    @RequestMapping(value = "/peruste/vaiheet/{perusteenVaiheId}", method = RequestMethod.GET)
    public TpoPerusteenTaiteenalaVaiheDto getPerusteTaiteenalaVaihe(@PathVariable final Long opsId,
                                                                    @PathVariable final Long perusteenVaiheId) {
        return taiteenperusopetusService.getPerusteVaihe(opsId, perusteenVaiheId);
    }

    @RequestMapping(value = "/taiteenalat", method = RequestMethod.GET)
    public List<TaiteenalaKevytDto> getTaiteenalat(@PathVariable final Long opsId) {
        return taiteenperusopetusService.getTaiteenalat(opsId);
    }

    @RequestMapping(value = "/taiteenalat", method = RequestMethod.POST)
    public List<TaiteenalaKevytDto> addTaiteenalat(@PathVariable final Long opsId,
                                                   @RequestBody final List<Long> perusteenTaiteenalaIdt) {
        return taiteenperusopetusService.addTaiteenalat(opsId, perusteenTaiteenalaIdt);
    }

    @RequestMapping(value = "/taiteenalat/{taiteenalaId}", method = RequestMethod.GET)
    public TaiteenalaDto getTaiteenala(@PathVariable final Long opsId, @PathVariable final Long taiteenalaId) {
        return taiteenperusopetusService.getTaiteenala(opsId, taiteenalaId);
    }

    @RequestMapping(value = "/taiteenalat/{taiteenalaId}", method = RequestMethod.POST)
    public TaiteenalaDto updateTaiteenala(@PathVariable final Long opsId,
                                          @PathVariable final Long taiteenalaId,
                                          @RequestBody final TaiteenalaDto dto) {
        return taiteenperusopetusService.updateTaiteenala(opsId, taiteenalaId, dto);
    }

    @RequestMapping(value = "/taiteenalat/{taiteenalaId}", method = RequestMethod.DELETE)
    public void removeTaiteenala(@PathVariable final Long opsId, @PathVariable final Long taiteenalaId) {
        taiteenperusopetusService.removeTaiteenala(opsId, taiteenalaId);
    }

    @RequestMapping(value = "/vaiheet/{vaiheId}", method = RequestMethod.GET)
    public TaiteenalanVaiheDto getTaiteenalanVaihe(@PathVariable final Long opsId, @PathVariable final Long vaiheId) {
        return taiteenperusopetusService.getVaihe(opsId, vaiheId);
    }

    @RequestMapping(value = "/vaiheet/{vaiheId}", method = RequestMethod.POST)
    public TaiteenalanVaiheDto updateTaiteenalanVaihe(@PathVariable final Long opsId,
                                                      @PathVariable final Long vaiheId,
                                                      @RequestBody final TaiteenalanVaiheDto dto) {
        return taiteenperusopetusService.updateVaihe(opsId, vaiheId, dto);
    }

    @RequestMapping(value = "/vaiheet/{vaiheId}/opintokokonaisuudet", method = RequestMethod.POST)
    public TaiteenOpintokokonaisuusDto addTaiteenOpintokokonaisuus(@PathVariable final Long opsId,
                                                                   @PathVariable final Long vaiheId,
                                                                   @RequestBody final TaiteenOpintokokonaisuusDto dto) {
        return taiteenperusopetusService.addOpintokokonaisuus(opsId, vaiheId, dto);
    }

    @RequestMapping(value = "/opintokokonaisuudet/{opintokokonaisuusId}", method = RequestMethod.GET)
    public TaiteenOpintokokonaisuusDto getTaiteenOpintokokonaisuus(@PathVariable final Long opsId,
                                                                   @PathVariable final Long opintokokonaisuusId) {
        return taiteenperusopetusService.getOpintokokonaisuus(opsId, opintokokonaisuusId);
    }

    @RequestMapping(value = "/opintokokonaisuudet/{opintokokonaisuusId}", method = RequestMethod.POST)
    public TaiteenOpintokokonaisuusDto updateTaiteenOpintokokonaisuus(@PathVariable final Long opsId,
                                                                      @PathVariable final Long opintokokonaisuusId,
                                                                      @RequestBody final TaiteenOpintokokonaisuusDto dto) {
        return taiteenperusopetusService.updateOpintokokonaisuus(opsId, opintokokonaisuusId, dto);
    }

    @RequestMapping(value = "/opintokokonaisuudet/{opintokokonaisuusId}", method = RequestMethod.DELETE)
    public void removeTaiteenOpintokokonaisuus(@PathVariable final Long opsId,
                                               @PathVariable final Long opintokokonaisuusId) {
        taiteenperusopetusService.removeOpintokokonaisuus(opsId, opintokokonaisuusId);
    }
}
