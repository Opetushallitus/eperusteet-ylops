package fi.vm.sade.eperusteet.ylops.resource.ops.lukio;

import fi.vm.sade.eperusteet.ylops.dto.lukio.AihekokonaisuudetJarjestaDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.AihekokonaisuudetPerusteOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.AihekokonaisuusSaveDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LongIdResultDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioAbstraktiOppiaineTuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioKopioiOppimaaraDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOpetussuunnitelmaRakenneOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineSaveDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineTiedotDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukiokurssiSaveDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukiokurssiUpdateDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.OpetuksenYleisetTavoitteetPerusteOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.OpetuksenYleisetTavoitteetUpdateDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.OppaineKurssiTreeStructureDto;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioOpetussuunnitelmaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/opetussuunnitelmat/lukio/{opsId}")
@Tag(name = "LukioOpetussuunnitelmat")
public class LukioOpetussuunnitelmatController {

    @Autowired
    private LukioOpetussuunnitelmaService lukioOpetussuunnitelmaService;

    @ResponseBody
    @RequestMapping(value = "/rakenne", method = RequestMethod.GET)
    public LukioOpetussuunnitelmaRakenneOpsDto getRakenne(@PathVariable("opsId") Long opsId) {
        return lukioOpetussuunnitelmaService.getRakenne(opsId);
    }

    @ResponseBody
    @RequestMapping(value = "/oppiaine/{oppiaineId}", method = RequestMethod.GET)
    public LukioOppiaineTiedotDto getLukioOppiaine(@PathVariable("opsId") Long opsId,
                                                   @PathVariable("oppiaineId") Long oppiaineId) {
        return lukioOpetussuunnitelmaService.getOppiaineTiedot(opsId, oppiaineId);
    }

    @RequestMapping(value = "/rakenne", method = POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStructure(@PathVariable("opsId") final Long opsId,
                                @RequestBody OppaineKurssiTreeStructureDto structureDto) {
        lukioOpetussuunnitelmaService.updateTreeStructure(opsId, structureDto);
    }

    @ResponseBody
    @RequestMapping(value = "/aihekokonaisuudet", method = RequestMethod.GET)
    public AihekokonaisuudetPerusteOpsDto getAihekokonaisuudet(@PathVariable("opsId") Long opsId) {
        return lukioOpetussuunnitelmaService.getAihekokonaisuudet(opsId);
    }

    @ResponseBody
    @RequestMapping(value = "/aihekokonaisuudet/kokonaisuus", method = RequestMethod.POST)
    public LongIdResultDto saveAihekokonaisuus(@PathVariable("opsId") Long opsId,
                                               @RequestBody AihekokonaisuusSaveDto kokonaisuus) {
        return new LongIdResultDto(lukioOpetussuunnitelmaService.saveAihekokonaisuus(opsId, kokonaisuus));
    }

    @RequestMapping(value = "/aihekokonaisuudet/jarjesta", method = RequestMethod.POST)
    public void reArrangeAihekokonaisuudet(@PathVariable("opsId") Long opsId,
                                           @RequestBody AihekokonaisuudetJarjestaDto jarjestys) {
        lukioOpetussuunnitelmaService.reArrangeAihekokonaisuudet(opsId, jarjestys);
    }

    @RequestMapping(value = "/aihekokonaisuudet/yleiskuvaus", method = RequestMethod.POST)
    public void updateAihekokonaisuudetYleiskuvaus(@PathVariable("opsId") Long opsId,
                                                   @RequestBody AihekokonaisuusSaveDto kokonaisuus) {
        lukioOpetussuunnitelmaService.updateAihekokonaisuusYleiskuvaus(opsId, kokonaisuus);
    }

    @RequestMapping(value = "/aihekokonaisuudet/kokonaisuus/{aihekokonaisuusId}", method = RequestMethod.POST)
    public void updateAihekokonaisuus(@PathVariable("opsId") Long opsId,
                                      @PathVariable("aihekokonaisuusId") Long id,
                                      @RequestBody AihekokonaisuusSaveDto kokonaisuus) {
        lukioOpetussuunnitelmaService.updateAihekokonaisuus(opsId, id, kokonaisuus);
    }

    @RequestMapping(value = "/aihekokonaisuudet/kokonaisuus/{aihekokonaisuusId}", method = RequestMethod.DELETE)
    public void deleteAihekokonaisuus(@PathVariable("opsId") Long opsId,
                                      @PathVariable("aihekokonaisuusId") Long id) {
        lukioOpetussuunnitelmaService.deleteAihekokonaisuus(opsId, id);
    }

    @ResponseBody
    @RequestMapping(value = "/opetuksenYleisetTavoitteet", method = RequestMethod.GET)
    public OpetuksenYleisetTavoitteetPerusteOpsDto getOpetuksenYleisetTavoitteet(@PathVariable("opsId") Long opsId) {
        return lukioOpetussuunnitelmaService.getOpetuksenYleisetTavoitteet(opsId);
    }

    @ResponseBody
    @RequestMapping(value = "/opetuksenYleisetTavoitteet", method = RequestMethod.POST)
    public void updateOpetuksenYleisetTavoitteet(@PathVariable("opsId") Long opsId,
                                                 @RequestBody OpetuksenYleisetTavoitteetUpdateDto tavoitteet) {
        lukioOpetussuunnitelmaService.updateOpetuksenYleisetTavoitteet(opsId, tavoitteet);
    }

    @ResponseBody
    @RequestMapping(value = "/oppiaine", method = RequestMethod.POST)
    public LongIdResultDto saveLukioOppiaine(@PathVariable("opsId") Long opsId,
                                             @RequestBody LukioOppiaineSaveDto oppiaine) {
        return new LongIdResultDto(lukioOpetussuunnitelmaService.saveOppiaine(opsId, oppiaine));
    }

    @ResponseBody
    @RequestMapping(value = "/oppiaine", method = RequestMethod.PUT)
    public void updateLukioOppiaine(@PathVariable("opsId") Long opsId,
                                    @RequestBody LukioOppiaineSaveDto oppiaine) {
        lukioOpetussuunnitelmaService.updateOppiaine(opsId, oppiaine);
    }

    @ResponseBody
    @RequestMapping(value = "/oppiaine/{oppiaineId}/kielitarjonta", method = RequestMethod.POST)
    public LongIdResultDto addLukioOppimaara(@PathVariable("opsId") final Long opsId,
                                             @PathVariable("oppiaineId") final Long oppiaineId,
                                             @RequestBody LukioKopioiOppimaaraDto kt) {
        return new LongIdResultDto(lukioOpetussuunnitelmaService.addOppimaara(opsId, oppiaineId, kt));
    }

    @ResponseBody
    @RequestMapping(value = "/oppiaine/abstrakti", method = RequestMethod.POST)
    public LongIdResultDto addAbstraktiOppiaine(@PathVariable("opsId") final Long opsId,
                                                @RequestBody LukioAbstraktiOppiaineTuontiDto tuonti) {
        return new LongIdResultDto(lukioOpetussuunnitelmaService.addAbstraktiOppiaine(opsId, tuonti));
    }

    @ResponseBody
    @RequestMapping(value = "/kurssi", method = RequestMethod.POST)
    public LongIdResultDto saveKurssi(@PathVariable("opsId") final Long opsId,
                                      @RequestBody LukiokurssiSaveDto kurssi) {
        return new LongIdResultDto(lukioOpetussuunnitelmaService.saveKurssi(opsId, kurssi));
    }

    @RequestMapping(value = "/kurssi/{kurssiId}", method = RequestMethod.POST)
    public void updateKurssi(@PathVariable("opsId") final Long opsId,
                             @PathVariable("kurssiId") final Long kurssiId,
                             @RequestBody LukiokurssiUpdateDto kurssi) {
        lukioOpetussuunnitelmaService.updateKurssi(opsId, kurssiId, kurssi);
    }

    @RequestMapping(value = "/oppiaine/{oppiaineId}/kurssi/{kurssiId}/disconnect", method = RequestMethod.POST)
    public LongIdResultDto disconnectKurssi(@PathVariable final Long opsId,
                                            @PathVariable final Long oppiaineId,
                                            @PathVariable final Long kurssiId) {
        return new LongIdResultDto(lukioOpetussuunnitelmaService.disconnectKurssi(kurssiId, oppiaineId, opsId));
    }

    @RequestMapping(value = "/oppiaine/{oppiaineId}/kurssi/{kurssiId}/reconnect", method = RequestMethod.POST)
    public LongIdResultDto reconnectKurssi(@PathVariable final Long opsId,
                                           @PathVariable final Long oppiaineId,
                                           @PathVariable final Long kurssiId) {
        return new LongIdResultDto(lukioOpetussuunnitelmaService.reconnectKurssi(kurssiId, oppiaineId, opsId));
    }

    @RequestMapping(value = "/kurssi/{kurssiId}/remove", method = RequestMethod.DELETE)
    public void removeKurssi(@PathVariable("opsId") final Long opsId,
                             @PathVariable("kurssiId") final Long kurssiId) {
        lukioOpetussuunnitelmaService.removeKurssi(opsId, kurssiId);
    }
}
