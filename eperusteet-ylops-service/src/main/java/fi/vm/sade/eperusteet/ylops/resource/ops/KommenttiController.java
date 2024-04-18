package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.KommenttiDto;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.ylops.service.teksti.KommenttiService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/api/kommentit")
@ApiIgnore
@Api("Kommentit")
@Deprecated
public class KommenttiController {

    @Autowired
    private KommenttiService service;

    @Autowired
    KayttajanTietoService kayttajanTietoService;

    private KommenttiDto rikastaKommentti(KommenttiDto kommentti) {
        if (kommentti != null) {
            KayttajanTietoDto kayttaja = kayttajanTietoService.hae(kommentti.getMuokkaaja());
            if (kayttaja != null) {
                String kutsumanimi = kayttaja.getKutsumanimi();
                String etunimet = kayttaja.getEtunimet();
                String etunimi = kutsumanimi != null ? kutsumanimi : etunimet;
                kommentti.setNimi(etunimi + " " + kayttaja.getSukunimi());
            }
        }
        return kommentti;
    }

    private KommenttiDto rikastaKommentti(KommenttiDto kommentti, Map<String, Future<KayttajanTietoDto>> kayttajat) {
        if (kayttajat.containsKey(kommentti.getMuokkaaja())) {
            try {
                KayttajanTietoDto kayttaja = kayttajat.get(kommentti.getMuokkaaja()).get(5, TimeUnit.SECONDS);
                if (kayttaja != null) {
                    String kutsumanimi = kayttaja.getKutsumanimi();
                    String etunimet = kayttaja.getEtunimet();
                    String etunimi = kutsumanimi != null ? kutsumanimi : etunimet;
                    kommentti.setNimi(etunimi + " " + kayttaja.getSukunimi());
                }
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                //ei välitetä epäonnistumisesta
            }
        }
        return kommentti;
    }

    private List<KommenttiDto> rikastaKommentit(List<KommenttiDto> kommentit) {
        Map<String, Future<KayttajanTietoDto>> kayttajat = kommentit.stream()
                .map(KommenttiDto::getMuokkaaja)
                .distinct()
                .collect(Collectors.toMap(s -> s, s -> kayttajanTietoService.haeAsync(s)));

        return kommentit.stream()
                .map(k -> rikastaKommentti(k, kayttajat))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/opetussuunnitelmat/{id}", method = GET)
    public ResponseEntity<List<KommenttiDto>> getAllKommentitByOpetussuunnitelma(@PathVariable("id") final long id) {
        List<KommenttiDto> t = service.getAllByOpetussuunnitelma(id);
        return new ResponseEntity<>(rikastaKommentit(t), HttpStatus.OK);
    }

    @RequestMapping(value = "/opetussuunnitelmat/{id}/tekstikappaleviitteet/{viiteId}", method = GET)
    public ResponseEntity<List<KommenttiDto>> getAllKommentitByTekstiKappaleViite(
            @PathVariable("id") final long id,
            @PathVariable("viiteId") final long viiteId) {
        List<KommenttiDto> t = service.getAllByTekstiKappaleViite(id, viiteId);
        return new ResponseEntity<>(rikastaKommentit(t), HttpStatus.OK);
    }

    @RequestMapping(value = "/opetussuunnitelmat/{id}/vuosiluokat/{vlkId}/oppiaine/{oppiaineId}", method = GET)
    public ResponseEntity<List<KommenttiDto>> getAllKommentitByOppiaine(
            @PathVariable("id") final long id,
            @PathVariable("vlkId") final long vlkId,
            @PathVariable("oppiaineId") final long oppiaineId) {
        List<KommenttiDto> t = service.getAllByOppiaine(id, vlkId, oppiaineId);
        return new ResponseEntity<>(rikastaKommentit(t), HttpStatus.OK);
    }

    @RequestMapping(value = {
            "/opetussuunnitelmat/{opsId}/opetus/vuosiluokat/{vlkId}/oppiaine/{oppiaineId}/vuosiluokka/{vlId}",
            "/opetussuunnitelmat/{opsId}/opetus/vuosiluokat/{vlkId}/oppiaine/{oppiaineId}/vuosiluokka/{vlId}/tavoitteet",
            "/opetussuunnitelmat/{opsId}/opetus/vuosiluokat/{vlkId}/oppiaine/{oppiaineId}/vuosiluokka/{vlId}/sisaltoalueet"
    }, method = GET)
    public ResponseEntity<List<KommenttiDto>> getAllKommentitByVuosiluokka(
            @PathVariable("opsId") final long opsId,
            @PathVariable("vlkId") final long vlkId,
            @PathVariable("oppiaineId") final long oppiaineId,
            @PathVariable("vlId") final long vlId) {
        List<KommenttiDto> t = service.getAllByVuosiluokka(opsId, vlkId, oppiaineId, vlId);
        return new ResponseEntity<>(rikastaKommentit(t), HttpStatus.OK);
    }

    @RequestMapping(value = "/ylin/{id}", method = GET)
    public ResponseEntity<List<KommenttiDto>> getAllKommentitByYlin(@PathVariable("id") final long id) {
        List<KommenttiDto> t = service.getAllByYlin(id);
        return new ResponseEntity<>(rikastaKommentit(t), HttpStatus.OK);
    }

    @RequestMapping(value = "/parent/{id}", method = GET)
    public ResponseEntity<List<KommenttiDto>> getAllKommentitByParent(@PathVariable("id") final long id) {
        List<KommenttiDto> t = service.getAllByParent(id);
        return new ResponseEntity<>(rikastaKommentit(t), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = GET)
    public ResponseEntity<KommenttiDto> getKommentti(@PathVariable("id") final long id) {
        KommenttiDto t = service.get(id);
        return new ResponseEntity<>(rikastaKommentti(t), t == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @RequestMapping(method = POST)
    public ResponseEntity<KommenttiDto> addKommentti(@RequestBody KommenttiDto body) {
        return new ResponseEntity<>(service.add(body), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    public ResponseEntity<KommenttiDto> updateKommentti(@PathVariable("id") final long id, @RequestBody KommenttiDto body) {
        return new ResponseEntity<>(service.update(id, body), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public void deleteKommentti(@PathVariable("id") final long id) {
        service.delete(id);
    }

}
