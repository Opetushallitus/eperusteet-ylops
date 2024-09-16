package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.UnwrappedOpsVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.VuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.resource.util.Responses;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.VuosiluokkakokonaisuusService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/opetussuunnitelmat/{opsId}/vuosiluokkakokonaisuudet")
@Api(value = "Vuosiluokkakokonaisuudet")
public class VuosiluokkakokonaisuusController {

    @Autowired
    private VuosiluokkakokonaisuusService vuosiluokkakokonaisuudet;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmat;

    @RequestMapping(method = RequestMethod.POST)
    public VuosiluokkakokonaisuusDto add(
            @PathVariable final Long opsId,
            @RequestBody VuosiluokkakokonaisuusDto dto) {
        return vuosiluokkakokonaisuudet.add(opsId, dto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<UnwrappedOpsVuosiluokkakokonaisuusDto> getVuosiluokkakokonaisuus(
            @PathVariable final Long opsId,
            @PathVariable final Long id
    ) {
        return Responses.ofNullable(UnwrappedOpsVuosiluokkakokonaisuusDto.of(vuosiluokkakokonaisuudet.get(opsId, id)));
    }

    @RequestMapping(value = "/{tunniste}/pohja", method = RequestMethod.GET)
    public UnwrappedOpsVuosiluokkakokonaisuusDto getPohjanVuosiluokkakokonaisuus(
            @PathVariable final Long opsId,
            @PathVariable final UUID tunniste
    ) {
        return UnwrappedOpsVuosiluokkakokonaisuusDto.of(vuosiluokkakokonaisuudet.getPohjanVuosiluokkakokonaisuus(opsId, tunniste));
    }

    @RequestMapping(value = "/{id}/peruste", method = RequestMethod.GET)
    public PerusteVuosiluokkakokonaisuusDto getVuosiluokkakokonaisuudenPerusteSisalto(
            @PathVariable final Long opsId,
            @PathVariable final Long id
    ) {
        final PerusteDto peruste = opetussuunnitelmat.getPeruste(opsId);
        final VuosiluokkakokonaisuusDto v = vuosiluokkakokonaisuudet.get(opsId, id).getVuosiluokkakokonaisuus();

        Optional<PerusteVuosiluokkakokonaisuusDto> vkDto = peruste.getPerusopetus().getVuosiluokkakokonaisuudet()
                .stream()
                .filter(vk -> Reference.of(vk.getTunniste()).equals(v.getTunniste()))
                .findAny();

        return vkDto.orElse(null);
    }

    @RequestMapping(value = "/{id}/oppiaineet", method = RequestMethod.GET)
    public Set<OppiaineDto> findOppiaineet(@PathVariable final Long opsId, @PathVariable final Long id) {
        throw new UnsupportedOperationException("Ei ole toteutettu");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public UnwrappedOpsVuosiluokkakokonaisuusDto updateVuosiluokkakokonaisuus(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @RequestBody VuosiluokkakokonaisuusDto dto) {
        dto.setId(id);
        return new UnwrappedOpsVuosiluokkakokonaisuusDto(vuosiluokkakokonaisuudet.update(opsId, dto));
    }

    @RequestMapping(value = "/{id}/muokattavakopio", method = RequestMethod.POST)
    public UnwrappedOpsVuosiluokkakokonaisuusDto kopioiVuosiluokkakokonaisuusMuokattavaksi(
            @PathVariable final Long opsId,
            @PathVariable final Long id) {
        return new UnwrappedOpsVuosiluokkakokonaisuusDto(vuosiluokkakokonaisuudet.kopioiMuokattavaksi(opsId, id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVuosiluokkakokonaisuus(
            @PathVariable final Long opsId,
            @PathVariable final Long id) {
        vuosiluokkakokonaisuudet.delete(opsId, id);
    }

    @RequestMapping(value = "/{vuosiluokkakokonaisuusId}/{oppiaineId}/piilota", method = RequestMethod.POST)
    public void piilotaOppiaine(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("vuosiluokkakokonaisuusId") final Long vuosiluokkakokonaisuusId) {
        vuosiluokkakokonaisuudet.piilotaOppiaine(opsId, oppiaineId, vuosiluokkakokonaisuusId, true);
    }

    @RequestMapping(value = "/{vuosiluokkakokonaisuusId}/{oppiaineId}/palauta", method = RequestMethod.POST)
    public void palautaOppiaine(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("vuosiluokkakokonaisuusId") final Long vuosiluokkakokonaisuusId) {
        vuosiluokkakokonaisuudet.piilotaOppiaine(opsId, oppiaineId, vuosiluokkakokonaisuusId, false);
    }
}
