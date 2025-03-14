package fi.vm.sade.eperusteet.ylops.service.mocks;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusopetuksenPerusteenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.cache.PerusteCacheRepository;
import fi.vm.sade.eperusteet.ylops.resource.config.ReferenceNamingStrategy;
import fi.vm.sade.eperusteet.ylops.service.external.impl.EperusteetLocalService;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.EperusteetPerusteDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteVersionDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@SuppressWarnings("TransactionalAnnotations")
public class EperusteetServiceMock extends EperusteetLocalService {

    public static final String DIAARINUMERO = "mock-diaarinumero";
    public static final String PERUSOPETUS_DIAARINUMERO = "perusopetus-diaarinumero";
    public static final String LOPS2019_DIAARINUMERO = "1/2/3";

    private EperusteetPerusteDto perusteDto = null;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private PerusteCacheRepository perusteCacheRepository;

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public List<PerusteInfoDto> findPerusopetuksenPerusteet() {
        PerusteInfoDto perusteInfo = new PerusteInfoDto();
        perusteInfo.setDiaarinumero(DIAARINUMERO);
        return Collections.singletonList(perusteInfo);
    }

    @Override
    public List<PerusteInfoDto> findLukiokoulutusPerusteet() {
        PerusteInfoDto perusteInfo = new PerusteInfoDto();
        perusteInfo.setDiaarinumero(DIAARINUMERO);
        return Collections.singletonList(perusteInfo);
    }

    @Override
    public PerusteDto getPerusteById(Long id) {
        PerusteDto peruste = super.getPerusteById(id);
        if (peruste != null) {
            return peruste;
        }
        else {
            peruste = new PerusteDto();
            peruste.setId(id);
            peruste.setNimi(mapper.map(LokalisoituTeksti.of(Kieli.FI, "mock-peruste"), LokalisoituTekstiDto.class));
            peruste.setDiaarinumero(DIAARINUMERO);
            peruste.setGlobalVersion(new PerusteVersionDto(new Date()));
            peruste.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
            PerusopetuksenPerusteenSisaltoDto sisalto = new PerusopetuksenPerusteenSisaltoDto();
            sisalto.setOppiaineet(Collections.emptySet());
            peruste.setPerusopetus(sisalto);
            return peruste;
        }
    }

    @Override
    public List<PerusteInfoDto> findPerusteet() {
        return Stream.concat(findPerusteet(null).stream(), super.findPerusteet().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<PerusteInfoDto> findPerusteet(Set<KoulutusTyyppi> tyypit) {
        PerusteInfoDto perusteInfo = new PerusteInfoDto();
        perusteInfo.setDiaarinumero(DIAARINUMERO);
        return Collections.singletonList(perusteInfo);
    }

    @Override
    public PerusteDto getPeruste(String diaariNumero) {
        if (DIAARINUMERO.equals(diaariNumero)) {
            return getPerusteByDiaari(diaariNumero);
        }
        else {
            return super.getPeruste(diaariNumero);
        }
    }

    @Override
    public PerusteDto getPerusteUpdateCache(String diaarinumero) {
        return getPerusteByDiaari(diaarinumero);
    }

    private PerusteDto getPerusteByDiaari(String diaariNumero) {
        if (perusteDto != null && diaariNumero.equals(perusteDto.getDiaarinumero())) {
            savePerusteCahceEntry(perusteDto);
            return mapper.map(perusteDto, PerusteDto.class);
        }
        PerusteDto peruste = getPerusteById(0L);

        savePerusteCahceEntry(mapper.map(peruste, EperusteetPerusteDto.class));
        return peruste;
    }

    @Override
    public JsonNode getTiedotteet(Long jalkeen) {
        return null;
    }

    private void savePerusteCahceEntry(EperusteetPerusteDto peruste) {
        PerusteCache cache = new PerusteCache();
        cache.setAikaleima(new Date());
        cache.setPerusteId(peruste.getId());
        cache.setKoulutustyyppi(peruste.getKoulutustyyppi());
        cache.setDiaarinumero(peruste.getDiaarinumero());
        cache.setVoimassaoloAlkaa(peruste.getVoimassaoloAlkaa());
        cache.setVoimassaoloLoppuu(peruste.getVoimassaoloLoppuu());
        cache.setNimi(LokalisoituTeksti.of(peruste.getNimi().getTekstit()));
        cache.setPerusteJson("{}");

        perusteCacheRepository.saveAndFlush(cache);
    }

    public void setPeruste(InputStream is) throws IOException {
        if (is == null) {
            perusteDto = null;
        } else {
            final ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
            om.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
            om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            om.registerModule(new Jdk8Module());
            om.setPropertyNamingStrategy(new ReferenceNamingStrategy());
            perusteDto = om.readValue(is, EperusteetPerusteDto.class);
        }
    }

}
