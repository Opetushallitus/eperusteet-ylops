package fi.vm.sade.eperusteet.ylops.service.ops;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanJulkaisu;
import fi.vm.sade.eperusteet.ylops.dto.JarjestysDto;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.OppiaineOpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaistuQuery;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkinenDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaNimiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaQuery;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaStatistiikkaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaTilastoDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsVuosiluokkakokonaisuusKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteLaajaalainenosaaminenDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViitePerusteTekstillaDto;
import fi.vm.sade.eperusteet.ylops.service.util.Validointi;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface OpetussuunnitelmaService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Collection<PerusteLaajaalainenosaaminenDto> getLaajaalaisetosaamiset(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(null, 'tarkastelu', 'HALLINTA') ||" +
            "(#tyyppi == T(fi.vm.sade.eperusteet.ylops.domain.Tyyppi).OPS and (hasPermission(null, 'opetussuunnitelma', 'LUKU'))) || " +
            "(#tyyppi == T(fi.vm.sade.eperusteet.ylops.domain.Tyyppi).POHJA and hasPermission(null, 'pohja', 'LUKU'))")
    List<OpetussuunnitelmaInfoDto> getAll(Tyyppi tyyppi, Tila tila);

    @PreAuthorize("hasPermission(null, 'tarkastelu', 'HALLINTA') ||" +
            "(#tyyppi == T(fi.vm.sade.eperusteet.ylops.domain.Tyyppi).OPS and (hasPermission(null, 'opetussuunnitelma', 'LUKU'))) || " +
            "(#tyyppi == T(fi.vm.sade.eperusteet.ylops.domain.Tyyppi).POHJA and hasPermission(null, 'pohja', 'LUKU'))")
    Page<OpetussuunnitelmaInfoDto> getSivutettu(Tyyppi tyyppi, Tila tila, KoulutusTyyppi koulutustyyppi, String nimi, int sivu, int sivukoko);

    @PreAuthorize("hasPermission(null, 'tarkastelu', 'HALLINTA') ||" +
            "(#tyyppi == T(fi.vm.sade.eperusteet.ylops.domain.Tyyppi).OPS and (hasPermission(null, 'opetussuunnitelma', 'LUKU'))) || " +
            "(#tyyppi == T(fi.vm.sade.eperusteet.ylops.domain.Tyyppi).POHJA and hasPermission(null, 'pohja', 'LUKU'))")
    Page<OpetussuunnitelmaInfoDto> getSivutettu(Tyyppi tyyppi, Tila tila, KoulutusTyyppi koulutustyyppi, String nimi, String jarjestys, String jarjestysSuunta, String kieli, int sivu, int sivukoko);

    @PreAuthorize("permitAll()")
    Long getAmount(Tyyppi tyyppi, Set<Tila> tila);

    @PreAuthorize("permitAll()")
    List<OpetussuunnitelmaJulkinenDto> getAllJulkiset(OpetussuunnitelmaQuery query);

    @PreAuthorize("permitAll()")
    Page<OpetussuunnitelmaJulkinenDto> getAllJulkaistutOpetussuunnitelmat(OpetussuunnitelmaJulkaistuQuery query);

    @PreAuthorize("permitAll()")
    Object getJulkaistuSisaltoObjectNode(Long id, List<String> queryList);

    @PreAuthorize("isAuthenticated()")
    List<OpetussuunnitelmaJulkinenDto> getKaikkiJulkaistutOpetussuunnitelmat();

    @PreAuthorize("permitAll()")
    OpetussuunnitelmaJulkinenDto getOpetussuunnitelmaJulkinen(Long opsId);

    @PreAuthorize("isAuthenticated()")
    List<OpetussuunnitelmaInfoDto> getAll(Tyyppi tyyppi);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    OpetussuunnitelmaStatistiikkaDto getStatistiikka();

    @PreAuthorize("isAuthenticated()")
    List<OpetussuunnitelmaTilastoDto> getOpetussuunnitelmaTilastot();

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaKevytDto getOpetussuunnitelma(@P("opsId") Long opsId);

    @PreAuthorize("isAuthenticated()")
    OpetussuunnitelmaNimiDto getOpetussuunnitelmaNimi(Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Set<OpsVuosiluokkakokonaisuusKevytDto> getOpetussuunnitelmanPohjanVuosiluokkakokonaisuudet(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaDto getOpetussuunnitelmaKaikki(@P("opsId") Long opsId);

    @PreAuthorize("isAuthenticated()")
    List<OpetussuunnitelmaInfoDto> getOpetussuunnitelmaOpsPohjat();

    @PreAuthorize("isAuthenticated()")
    OpetussuunnitelmaKevytDto getOpetussuunnitelmaOrganisaatiotarkistuksella(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#ops.id, 'opetussuunnitelma', 'LUKU')")
    void fetchKuntaNimet(@P("ops") OpetussuunnitelmaBaseDto opetussuunnitelmaDto);

    @PreAuthorize("hasPermission(#ops.id, 'opetussuunnitelma', 'LUKU')")
    void fetchOrganisaatioNimet(@P("ops") OpetussuunnitelmaBaseDto opetussuunnitelmaDto);

    @PreAuthorize("hasPermission(null, 'opetussuunnitelma', 'LUONTI')")
    OpetussuunnitelmaDto addOpetussuunnitelma(OpetussuunnitelmaLuontiDto opetussuunnitelmaDto);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    OpetussuunnitelmaDto addPohja(OpetussuunnitelmaLuontiDto opetussuunnitelmaDto);

    @PreAuthorize("hasPermission(#ops.id, 'opetussuunnitelma', 'POISTO')")
    OpetussuunnitelmaDto updateOpetussuunnitelma(@P("ops") OpetussuunnitelmaDto opetussuunnitelmaDto);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'MUOKKAUS')")
    OpetussuunnitelmaDto importPerusteTekstit(@P("id") Long id);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'MUOKKAUS')")
    OpetussuunnitelmaDto importPerusteTekstit(@P("id") Long id, boolean skip);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'TILANVAIHTO')")
    OpetussuunnitelmaDto updateTila(@P("id") Long id, Tila tila);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'HALLINTA')")
    OpetussuunnitelmaDto restore(@P("id") Long id);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'MUOKKAUS')")
    List<OpetussuunnitelmaInfoDto> getLapsiOpetussuunnitelmat(@P("id") Long id);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'LUKU')")
    List<Validointi> validoiOpetussuunnitelma(@P("id") Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void updateLapsiOpetussuunnitelmat(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#pohjaId, 'opetussuunnitelma', 'HALLINTA')")
    void syncPohja(@P("pohjaId") Long pohjaId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'POISTO')")
    void updateOppiainejarjestys(@P("opsId") Long opsId, List<JarjestysDto> oppiainejarjestys);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'POISTO')")
    void updateOppiaineJaOpintojaksojarjestys(
            @P("opsId") Long opsId,
            List<OppiaineOpintojaksoDto> oppiaineopintojaksojarjestys
    );

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T> T getTekstit(@P("opsId") final Long opsId, Class<T> t);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TekstiKappaleViitePerusteTekstillaDto getTekstitPerusteenTeksteilla(@P("opsId") final Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto.Matala addTekstiKappale(@P("opsId") final Long opsId, TekstiKappaleViiteDto.Matala viite);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto.Matala addTekstiKappaleLapsi(@P("opsId") final Long opsId, final Long parentId,
                                                       TekstiKappaleViiteDto.Matala viite);

    /**
     * Hakee opetussuunnitelmaan liittyvän opetussuunnitelman perusteen
     *
     * @param opsId
     * @return Peruste
     */
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    PerusteDto getPeruste(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    PerusteInfoDto getPerusteBase(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    NavigationNodeDto buildNavigation(@P("opsId") Long opsId, String kieli);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    NavigationNodeDto buildNavigationPublic(@P("opsId") Long opsId, String kieli, Integer revision);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    void vaihdaPohja(@P("opsId") Long id, Long pohjaId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void syncTekstitPohjasta(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    boolean opetussuunnitelmanPohjallaUusiaTeksteja(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    boolean pohjanPerustePaivittynyt(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    Set<OpetussuunnitelmaInfoDto> vaihdettavatPohjat(@P("opsId") Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaExportDto getExportedOpetussuunnitelma(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaExportDto getOpetussuunnitelmaJulkaistuSisalto(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaExportDto getOpetussuunnitelmaJulkaistuSisalto(@P("opsId") Long opsId, Integer revision);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaExportDto getOpetussuunnitelmanJulkaisuWithData(Long opsId, OpetussuunnitelmanJulkaisu julkaisu);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TekstiKappaleDto getPerusteTekstikappale(Long opsId, Long tekstikappaleId);

    @PreAuthorize("hasPermission(null, 'pohja', 'HALLINTA')")
    void palautaTekstirakenne(@P("opsId") Long id);

    @PreAuthorize("permitAll()")
    JsonNode getJulkaistuOpetussuunnitelmaPeruste(Long opsId);
}
