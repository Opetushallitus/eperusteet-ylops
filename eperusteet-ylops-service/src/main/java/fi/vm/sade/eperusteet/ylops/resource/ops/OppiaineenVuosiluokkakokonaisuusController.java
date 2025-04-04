package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetuksenTavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.resource.util.CacheControl;
import fi.vm.sade.eperusteet.ylops.resource.util.Responses;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/opetussuunnitelmat/{opsId}/oppiaineet/{oppiaineId}/vuosiluokkakokonaisuudet")
@Tag(name = "OppiaineenVuosiluokkakokonaisuudet")
public class OppiaineenVuosiluokkakokonaisuusController {

    @Autowired
    private OppiaineService oppiaineService;

    @Autowired
    private OpetussuunnitelmaService ops;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<OppiaineenVuosiluokkakokonaisuusDto> getOppiaineenVuosiluokkakokonaisuus(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id) {

        OppiaineDto oa = oppiaineService.get(opsId, oppiaineId).getOppiaine();
        return Responses.of(oa.getVuosiluokkakokonaisuudet().stream()
                .filter(vk -> vk.getId().equals(id))
                .findAny());
    }

    @RequestMapping(value = "/{id}/tavoitteet", method = RequestMethod.GET)
    public Map<Vuosiluokka, Set<UUID>> getVuosiluokkienTavoitteet(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id) {

        OppiaineDto oa = oppiaineService.get(opsId, oppiaineId).getOppiaine();
        return oa.getVuosiluokkakokonaisuudet().stream()
                .filter(vk -> vk.getId().equals(id))
                .flatMap(vk -> vk.getVuosiluokat().stream())
                .collect(Collectors.toMap(
                        OppiaineenVuosiluokkaDto::getVuosiluokka,
                        l -> l.getTavoitteet().stream().map(OpetuksenTavoiteDto::getTunniste).collect(Collectors.toSet())));
    }

    @RequestMapping(value = "/{id}/tavoitteet", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVuosiluokkienTavoitteet(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id,
            @RequestBody Map<Vuosiluokka, Set<UUID>> tavoitteet) {
        oppiaineService.updateVuosiluokkienTavoitteet(opsId, oppiaineId, id, tavoitteet);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public OppiaineenVuosiluokkakokonaisuusDto updateVuosiluokkakokonaisuudenSisalto(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id,
            @RequestBody OppiaineenVuosiluokkakokonaisuusDto dto) {
        dto.setId(id);
        return oppiaineService.updateVuosiluokkakokonaisuudenSisalto(opsId, oppiaineId, dto);
    }

    @RequestMapping(value = "/{id}/peruste", method = RequestMethod.GET)
    @CacheControl(nonpublic = false, age = 3600)
    public ResponseEntity<PerusteOppiaineenVuosiluokkakokonaisuusDto> getOppiaineenVuosiluokkakokonaisuudenPerusteSisalto(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id) {

        final PerusteDto peruste = ops.getPeruste(opsId);
        final Optional<OppiaineDto> aine = Optional.ofNullable(oppiaineService.get(opsId, oppiaineId).getOppiaine());

        Optional<PerusteOppiaineenVuosiluokkakokonaisuusDto> dto = aine.flatMap(a -> a.getVuosiluokkakokonaisuudet().stream()
                .filter(vk -> vk.getId().equals(id))
                .findAny()
                .flatMap(ovk -> peruste.getPerusopetus().getOppiaine(a.getTunniste())
                        .flatMap(poa -> poa.getVuosiluokkakokonaisuus(ovk.getVuosiluokkakokonaisuus()))));

        return Responses.of(dto);
    }
}
