package fi.vm.sade.eperusteet.ylops.resource.aipe;

import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEKurssiDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEPerusteVaiheKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPESisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEVaiheKevytDto;
import fi.vm.sade.eperusteet.ylops.service.aipe.AIPEService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/opetussuunnitelmat/{opsId}/aipe")
@Tag(name = "AIPE")
public class AIPEController {

    @Autowired
    private AIPEService aipeService;

    @RequestMapping(value = "/sisalto", method = RequestMethod.GET)
    public AIPESisaltoDto getSisalto(@PathVariable final Long opsId) {
        return aipeService.getSisalto(opsId);
    }

    @RequestMapping(value = "/peruste/vaiheet", method = RequestMethod.GET)
    public List<AIPEPerusteVaiheKevytDto> getPerusteVaiheet(@PathVariable final Long opsId) {
        return aipeService.getPerusteVaiheet(opsId);
    }

    @RequestMapping(value = "/vaiheet", method = RequestMethod.GET)
    public List<AIPEVaiheKevytDto> getVaiheet(@PathVariable final Long opsId) {
        return aipeService.getVaiheet(opsId);
    }

    @RequestMapping(value = "/vaiheet", method = RequestMethod.PUT)
    public List<AIPEVaiheKevytDto> updateVaiheJarjestys(@PathVariable final Long opsId,
                                                       @RequestBody final List<Long> vaiheIds) {
        return aipeService.updateVaiheJarjestys(opsId, vaiheIds);
    }

    @RequestMapping(value = "/vaiheet/{perusteenVaiheId}", method = RequestMethod.POST)
    public AIPEVaiheDto addVaihe(@PathVariable final Long opsId, @PathVariable final Long perusteenVaiheId) {
        return aipeService.addVaihe(opsId, perusteenVaiheId);
    }

    @RequestMapping(value = "/vaiheet/{vaiheId}", method = RequestMethod.GET)
    public AIPEVaiheDto getVaihe(@PathVariable final Long opsId, @PathVariable final Long vaiheId) {
        return aipeService.getVaihe(opsId, vaiheId);
    }

    @RequestMapping(value = "/vaiheet/{vaiheId}", method = RequestMethod.PUT)
    public AIPEVaiheDto updateVaihe(@PathVariable final Long opsId,
                                    @PathVariable final Long vaiheId,
                                    @RequestBody final AIPEVaiheDto dto) {
        return aipeService.updateVaihe(opsId, vaiheId, dto);
    }

    @RequestMapping(value = "/vaiheet/{vaiheId}", method = RequestMethod.DELETE)
    public void removeVaihe(@PathVariable final Long opsId, @PathVariable final Long vaiheId) {
        aipeService.removeVaihe(opsId, vaiheId);
    }

    @RequestMapping(value = "/oppiaineet/{oppiaineId}", method = RequestMethod.GET)
    public AIPEOppiaineDto getAipeOppiaine(@PathVariable final Long opsId, @PathVariable final Long oppiaineId) {
        return aipeService.getOppiaine(opsId, oppiaineId);
    }

    @RequestMapping(value = "/oppiaineet/{oppiaineId}", method = RequestMethod.PUT)
    public AIPEOppiaineDto updateAipeOppiaine(@PathVariable final Long opsId,
                                          @PathVariable final Long oppiaineId,
                                          @RequestBody final AIPEOppiaineDto dto) {
        return aipeService.updateOppiaine(opsId, oppiaineId, dto);
    }

    @RequestMapping(value = "/kurssit/{kurssiId}", method = RequestMethod.GET)
    public AIPEKurssiDto getAipeKurssi(@PathVariable final Long opsId, @PathVariable final Long kurssiId) {
        return aipeService.getKurssi(opsId, kurssiId);
    }

    @RequestMapping(value = "/kurssit/{kurssiId}", method = RequestMethod.PUT)
    public AIPEKurssiDto updateAipeKurssi(@PathVariable final Long opsId,
                                      @PathVariable final Long kurssiId,
                                      @RequestBody final AIPEKurssiDto dto) {
        return aipeService.updateKurssi(opsId, kurssiId, dto);
    }
}
