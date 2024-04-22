package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOpetuksentavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTavoitteenArviointiDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class VuosiluokkaistusIT extends AbstractIntegrationTest {

    @Autowired
    OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    TekstiKappaleViiteService tekstiKappaleViiteService;

    @Autowired
    OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    OppiaineService oppiaineet;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private EperusteetService eperusteetService;

    private static Long opsId = null;

    @Before
    public void setUp() throws IOException {
//        try (InputStream json = getClass().getResourceAsStream()) {
//            eperusteetService.setPeruste(json);
//        }

        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setPerusteenDiaarinumero("104/011/2014");
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTyyppi(Tyyppi.POHJA);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        OpetussuunnitelmaDto luotu = opetussuunnitelmaService.addPohja(ops);
        opetussuunnitelmaService.updateTila(luotu.getId(), Tila.VALMIS);

        ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        ops.setPohja(Reference.of(luotu.getId()));

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid(SecurityUtil.OPH_OID);
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        luotu = opetussuunnitelmaService.addOpetussuunnitelma(ops);
        opsId = luotu.getId();
    }

    @Test
    public void testVuosiluokkaistus() {
        for (int i = 0; i < 3; i++) {
            OpetussuunnitelmaDto opsDto = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);
            PerusteDto peruste = opetussuunnitelmaService.getPeruste(opsId);
            opsDto.getOppiaineet().stream()
                    .flatMap(o -> Stream.of(o.getOppiaine()))
                    .forEach(oa -> {
                        PerusteOppiaineDto po = peruste.getPerusopetus().getOppiaine(oa.getTunniste()).get();
                        oa.getVuosiluokkakokonaisuudet().forEach(vk -> {
                            PerusteOppiaineenVuosiluokkakokonaisuusDto pvk =
                                    po.getVuosiluokkakokonaisuus(UUID.fromString(vk.getVuosiluokkakokonaisuus().toString())).get();
                            Map<Vuosiluokka, Set<UUID>> tavoitteet = new HashMap<>();
                            pvk.getVuosiluokkaKokonaisuus().getVuosiluokat().forEach(
                                    l -> tavoitteet.put(l, pvk.getTavoitteet().stream()
                                            .map(PerusteOpetuksentavoiteDto::getTunniste).collect(Collectors.toSet())));
                            oppiaineet.updateVuosiluokkienTavoitteet(opsId, oa.getId(), vk.getId(), tavoitteet);
                        });
                    });

            opsDto = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);
            opsDto.getOppiaineet().stream()
                    .flatMap(o -> Stream.of(o.getOppiaine()))
                    .forEach(oa -> {
                        PerusteOppiaineDto po = peruste.getPerusopetus().getOppiaine(oa.getTunniste()).get();
                        oa.getVuosiluokkakokonaisuudet().forEach(vk -> {
                            PerusteOppiaineenVuosiluokkakokonaisuusDto pvk =
                                    po.getVuosiluokkakokonaisuus(UUID.fromString(vk.getVuosiluokkakokonaisuus().toString())).get();
                            Map<Vuosiluokka, Set<UUID>> tavoitteet = new HashMap<>();
                            pvk.getVuosiluokkaKokonaisuus().getVuosiluokat().forEach(
                                    l -> tavoitteet.put(l, pvk.getTavoitteet().stream()
                                            .map(PerusteOpetuksentavoiteDto::getTunniste).collect(Collectors.toSet())));
                            assertEquals(tavoitteet.values().stream().filter(s -> !s.isEmpty()).count(), vk.getVuosiluokat().size());
                            vk.getVuosiluokat().forEach(l -> {
                                assertEquals(pvk.getTavoitteet().size(), l.getTavoitteet().size());
                                assertEquals(pvk.getSisaltoalueet().size(), l.getSisaltoalueet().size());
                            });
                        });
                    });
        }

    }

    @Test
    public void testArvioinninKohteet() {

        PerusteDto peruste = opetussuunnitelmaService.getPeruste(opsId);

        Assertions.assertThat(peruste.getPerusopetus()).isNotNull();
        Assertions.assertThat(peruste.getPerusopetus().getOppiaineet()).hasSize(1);
        Assertions.assertThat(peruste.getPerusopetus().getOppiaineet().iterator().next().getVuosiluokkakokonaisuudet()).hasSize(1);
        Assertions.assertThat(peruste.getPerusopetus().getOppiaineet().iterator().next().getVuosiluokkakokonaisuudet().get(0).getTavoitteet()).hasSize(3);

        List<PerusteOpetuksentavoiteDto> tavoitteet = peruste.getPerusopetus().getOppiaineet().iterator().next().getVuosiluokkakokonaisuudet().get(0).getTavoitteet();

        Assertions.assertThat(tavoitteet.stream().map(tavoite -> tavoite.getVapaaTeksti().get(Kieli.FI)))
                .hasSize(3)
                .containsExactlyInAnyOrder("<p>vapaata tekstikenttää</p>", "<p>teksti</p>", "<p>teksti</p>");

        Assertions.assertThat(tavoitteet.stream().map(PerusteOpetuksentavoiteDto::getArvioinninKuvaus))
                .hasSize(3)
                .doesNotContainNull();

        Assertions.assertThat(tavoitteet.get(0).getVapaaTeksti().get(Kieli.FI)).isEqualTo("<p>vapaata tekstikenttää</p>");
        Assertions.assertThat(tavoitteet.get(0).getArvioinninKuvaus().get(Kieli.FI)).isEqualTo("arvioinnin kohde (vanha taulunn otsikko)");

        Assertions.assertThat(tavoitteet.get(0).getArvioinninkohteet()).hasSize(3);

        Assertions.assertThat(tavoitteet.get(0).getArvioinninkohteet().stream().map(PerusteTavoitteenArviointiDto::getArvosana))
                .hasSize(3)
                .containsExactlyInAnyOrder(5,6,8);

        Assertions.assertThat(tavoitteet.get(0).getArvioinninkohteet().stream().map(PerusteTavoitteenArviointiDto::getOsaamisenKuvaus))
                .hasSize(3)
                .doesNotContainNull();

        Assertions.assertThat(tavoitteet.get(0).getArvioinninkohteet()
                .stream()
                .filter(kohde -> kohde.getHyvanOsaamisenKuvaus() != null)
                .map(kohde -> kohde.getHyvanOsaamisenKuvaus().get(Kieli.FI)))
                .hasSize(1);
    }

}
