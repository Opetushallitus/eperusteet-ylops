package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.dto.ops.Kommentti2019Dto;
import fi.vm.sade.eperusteet.ylops.dto.ops.Kommentti2019LuontiDto;
import fi.vm.sade.eperusteet.ylops.service.ops.Kommentti2019Service;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Api("Kommentointi")
@RequestMapping("/opetussuunnitelma/{opsId}/kommentointi")
public class KommentointiController {

    @Autowired
    private Kommentti2019Service kommenttiService;

    @RequestMapping(method = RequestMethod.POST)
    Kommentti2019Dto addKommentti(final Long opsId, Kommentti2019LuontiDto kommentti) {
        return kommenttiService.add(opsId, kommentti);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    Kommentti2019Dto getKommentit(final Long opsId, UUID uuid) {
        return kommenttiService.get(opsId, uuid);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.POST)
    Kommentti2019Dto updateKommentti(final Long opsId, UUID uuid, Kommentti2019Dto kommenttiDto) {
        return kommenttiService.update(opsId, uuid, kommenttiDto);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    void deleteKommentti(final Long opsId, UUID uuid) {
        kommenttiService.remove(opsId, uuid);
    }

}
