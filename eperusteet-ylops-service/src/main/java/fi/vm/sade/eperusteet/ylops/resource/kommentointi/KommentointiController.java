package fi.vm.sade.eperusteet.ylops.resource.kommentointi;

import fi.vm.sade.eperusteet.ylops.dto.ops.Kommentti2019Dto;
import fi.vm.sade.eperusteet.ylops.dto.ops.KommenttiKahvaDto;
import fi.vm.sade.eperusteet.ylops.service.ops.Kommentti2019Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/api/kommentointi")
@Tag(name = "Kommentointi")
public class KommentointiController {

    @Autowired
    private Kommentti2019Service kommentti2019Service;

    @RequestMapping(method = POST)
    public KommenttiKahvaDto addKetju(
            @RequestBody final KommenttiKahvaDto kahvaDto
    ) {
        return kommentti2019Service.addKommenttiKahva(kahvaDto);
    }

    @RequestMapping(value = "/{ketjuUuid}", method = GET)
    public List<Kommentti2019Dto> getKommenttiByKetjuUuid(
            @PathVariable("ketjuUuid") final UUID uuid
    ) {
        return kommentti2019Service.get(uuid);
    }

    @RequestMapping(value = "/{ketjuUuid}", method = POST)
    public Kommentti2019Dto addKommentti2019(
            @PathVariable("ketjuUuid") final UUID uuid,
            @RequestBody final Kommentti2019Dto kommenttiDto
    ) {
        return kommentti2019Service.add(uuid, kommenttiDto);
    }

    @RequestMapping(value = "/{ketjuUuid}", method = PUT)
    public Kommentti2019Dto updateKommentti2019(
            @PathVariable("ketjuUuid") final UUID uuid,
            @RequestBody final Kommentti2019Dto kommenttiDto
    ) {
        Kommentti2019Dto result = kommentti2019Service.update(kommenttiDto);
        return result;
    }

    @RequestMapping(value = "/{ketjuUuid}", method = DELETE)
    public void poistaKommenttiKetju2019(
        @PathVariable("ketjuUuid") final UUID uuid
    ) {
        kommentti2019Service.remove(uuid);
    }

}
