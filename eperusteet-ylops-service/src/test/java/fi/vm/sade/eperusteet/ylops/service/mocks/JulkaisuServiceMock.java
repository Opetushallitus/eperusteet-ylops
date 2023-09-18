package fi.vm.sade.eperusteet.ylops.service.mocks;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaistuOpetussuunnitelmaTila;
import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaisuTila;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmanJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.UusiJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.dto.util.FieldComparisonFailureDto;
import fi.vm.sade.eperusteet.ylops.service.util.JulkaisuService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Profile("test")
public class JulkaisuServiceMock implements JulkaisuService {
    @Override
    public List<OpetussuunnitelmanJulkaisuDto> getJulkaisut(Long opsId) {
        return null;
    }

    @Override
    public List<OpetussuunnitelmanJulkaisuDto> getJulkaisutKevyt(Long opsId) {
        return null;
    }

    @Override
    public void addJulkaisu(Long opsId, UusiJulkaisuDto julkaisuDto) {

    }

    @Override
    public void addJulkaisuAsync(Long opsId, UusiJulkaisuDto julkaisuDto) {

    }

    @Override
    public OpetussuunnitelmanJulkaisuDto aktivoiJulkaisu(Long opsId, int revision) {
        return null;
    }

    @Override
    public JsonNode queryOpetussuunnitelmaJulkaisu(Long opsId, String query) {
        return null;
    }

    @Override
    public List<FieldComparisonFailureDto> julkaisuversioMuutokset(long opsId) {
        return Collections.emptyList();
    }

    @Override
    public JulkaisuTila viimeisinJulkaisuTila(long opsId) {
        return null;
    }

    @Override
    public void saveJulkaistuOpetussuunnitelmaTila(JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila) {

    }
}
