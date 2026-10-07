package fi.vm.sade.eperusteet.ylops.service.util;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaisuTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaistuQuery;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkinenDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.UusiJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.mocks.EperusteetServiceMock;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.test.docker.AbstractDockerIntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testataan docker-tietokantaa vasten, johon ajetaan migraatiot.
 */
@DirtiesContext
@ActiveProfiles(profiles = {"docker"})
@Transactional
@SpringBootTest
public class JulkaisuServiceDockerIT extends AbstractDockerIntegrationTest {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private JulkaisuService julkaisuService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    private OpetussuunnitelmaDto pohja;

    @Before
    public void setUp() {
        pohja = luoPohja();
    }

    @After
    public void clean() {
        arkistoiOpetussuunnitelmat();
    }

    @Test
    public void testJulkaise() {
        assertThat(julkaistujenMaara()).as("ei julkaisuja ennen julkaisua").isZero();

        startNewTransaction();
        OpetussuunnitelmaDto ops1 = luoOpetussuunnitelma(pohja.getId(), "ops-yksi");
        endTransaction();

        julkaisuService.addJulkaisu(ops1.getId(), julkaisuDto());
        assertThat(julkaisuService.viimeisinJulkaisuTila(ops1.getId())).isEqualTo(JulkaisuTila.JULKAISTU);
        assertThat(julkaistujenMaara()).as("ensimmaisen julkaisun jalkeen").isEqualTo(1);
    }

    @Test
    public void testJulkaiseUudelleen() {
        startNewTransaction();
        OpetussuunnitelmaDto ops = luoOpetussuunnitelma(pohja.getId(), "ops-uudelleen");
        endTransaction();

        julkaisuService.addJulkaisu(ops.getId(), julkaisuDto());
        julkaisuService.addJulkaisu(ops.getId(), julkaisuDto());

        assertThat(julkaisuService.getJulkaisut(ops.getId())).hasSize(2);
    }

    @Test
    public void testJulkaiseJaPoistaListauksesta() {
        startNewTransaction();
        OpetussuunnitelmaDto ops1 = luoOpetussuunnitelma(pohja.getId(), "ops-yksi");
        OpetussuunnitelmaDto ops2 = luoOpetussuunnitelma(pohja.getId(), "ops-kaksi");
        assertThat(julkaistujenMaara()).as("kaksi luonnos-opsia ei nay julkaistuissa").isZero();
        endTransaction();

        julkaisuService.addJulkaisu(ops1.getId(), julkaisuDto());
        julkaisuService.addJulkaisu(ops2.getId(), julkaisuDto());
        assertThat(julkaistujenMaara()).as("toisen julkaisun jalkeen").isEqualTo(2);

        opetussuunnitelmaService.updateTila(ops2.getId(), Tila.POISTETTU);
        assertThat(julkaistujenMaara()).as("poiston jalkeen").isEqualTo(1);
        assertThat(julkaistutIds()).containsExactly(ops1.getId());
    }

    private long julkaistujenMaara() {
        return julkaistut().getTotalElements();
    }

    private List<Long> julkaistutIds() {
        return julkaistut().getContent().stream()
                .map(OpetussuunnitelmaJulkinenDto::getId)
                .toList();
    }

    private Page<OpetussuunnitelmaJulkinenDto> julkaistut() {
        OpetussuunnitelmaJulkaistuQuery query = new OpetussuunnitelmaJulkaistuQuery();
        query.setOrganisaatio("");
        return opetussuunnitelmaService.getAllJulkaistutOpetussuunnitelmat(query);
    }

    private OpetussuunnitelmaDto luoPohja() {
        // Oma transaction, jotta löytyy addJulkaisuAsyncin haussa
        startNewTransaction();

        OpetussuunnitelmaLuontiDto pohjaLuonti = new OpetussuunnitelmaLuontiDto();
        pohjaLuonti.setPerusteenDiaarinumero(EperusteetServiceMock.PERUSOPETUS_DIAARINUMERO);
        pohjaLuonti.setNimi(lt(uniikkiString()));
        pohjaLuonti.setKuvaus(lt(uniikkiString()));
        pohjaLuonti.setTyyppi(Tyyppi.POHJA);
        pohjaLuonti.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        OpetussuunnitelmaDto luotu = opetussuunnitelmaService.addPohja(pohjaLuonti);
        OpetussuunnitelmaDto valmis = opetussuunnitelmaService.updateTila(luotu.getId(), Tila.VALMIS);

        endTransaction();
        return valmis;
    }

    private OpetussuunnitelmaDto luoOpetussuunnitelma(Long pohjaId, String nimi) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(nimi));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        ops.setPohja(Reference.of(pohjaId));

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));

        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        ops.setJulkaisukielet(Sets.newHashSet(Kieli.FI));
        ops.setPaatospaivamaara(new Date());
        ops.setHyvaksyjataho("hyvaksyja");
        return opetussuunnitelmaService.addOpetussuunnitelma(ops);
    }

    private UusiJulkaisuDto julkaisuDto() {
        return UusiJulkaisuDto.builder()
                .julkaisutiedote(lt("julkaisutiedote"))
                .build();
    }

    private void arkistoiOpetussuunnitelmat() {
        opetussuunnitelmaRepository.findAll().forEach(ops -> {
            if (ops.getTila() != Tila.POISTETTU) {
                opetussuunnitelmaService.updateTila(ops.getId(), Tila.POISTETTU);
            }
        });
    }
}
