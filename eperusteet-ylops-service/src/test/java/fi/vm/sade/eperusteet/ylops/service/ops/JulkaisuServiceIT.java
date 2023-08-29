package fi.vm.sade.eperusteet.ylops.service.ops;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaistuQuery;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkinenDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.UusiJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.mocks.EperusteetServiceMock;
import fi.vm.sade.eperusteet.ylops.service.util.JulkaisuService;
import fi.vm.sade.eperusteet.ylops.test.AbstractDockerIntegrationTest;
import fi.vm.sade.eperusteet.ylops.test.util.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@Transactional
public class JulkaisuServiceIT extends AbstractDockerIntegrationTest {

    @Autowired
    private JulkaisuService julkaisuService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private OpetussuunnitelmaDto ops;

    @Before
    public void setUp() {
        // Oma transaction, jotta löytyy teeJulkaisuAsyncin haussa
        TestTransaction.end();
        TestTransaction.start();
        TestTransaction.flagForCommit();

        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setPerusteenDiaarinumero(EperusteetServiceMock.PERUSOPETUS_DIAARINUMERO);
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
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        ops.setJulkaisukielet(Sets.newHashSet(Kieli.FI));
        ops.setPaatospaivamaara(new Date());
        ops.setHyvaksyjataho("hyvaksyja");
        this.ops = opetussuunnitelmaService.addOpetussuunnitelma(ops);

        TestTransaction.end();
    }

    @Test
    @Rollback
    public void testJulkaise() throws ExecutionException, InterruptedException {
        assertThat(julkaisuService.getJulkaisut(this.ops.getId())).hasSize(0);
        CompletableFuture<Void> asyncResult = julkaisuService.addJulkaisu(this.ops.getId(), createJulkaisu());
        asyncResult.get();
        assertThat(julkaisuService.getJulkaisut(this.ops.getId())).hasSize(1);
    }

    @Test
    @Rollback
    public void testJulkaiseIlmanMuutoksia() throws ExecutionException, InterruptedException {
        expectedEx.expect(BusinessRuleViolationException.class);
        expectedEx.expectMessage("opetussuunnitelma-ei-muuttunut-viime-julkaisun-jalkeen");

        CompletableFuture<Void> asyncResult = julkaisuService.addJulkaisu(this.ops.getId(), createJulkaisu());
        asyncResult.get();
        julkaisuService.addJulkaisu(this.ops.getId(), createJulkaisu());
    }

    @Test
    @Rollback
    public void testJulkaiseUudelleen() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> asyncResult = julkaisuService.addJulkaisu(this.ops.getId(), createJulkaisu());
        asyncResult.get();

        this.ops.setNimi(TestUtils.lt("updated"));
        opetussuunnitelmaService.updateOpetussuunnitelma(this.ops);

        CompletableFuture<Void> asyncResult2 = julkaisuService.addJulkaisu(this.ops.getId(), createJulkaisu());
        asyncResult2.get();

        assertThat(julkaisuService.getJulkaisut(this.ops.getId())).hasSize(2);
    }

    @Test
    @Rollback
    public void testGetJulkisetJulkaisut() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> asyncResult = julkaisuService.addJulkaisu(this.ops.getId(), createJulkaisu());
        asyncResult.get();

        OpetussuunnitelmaJulkaistuQuery query = new OpetussuunnitelmaJulkaistuQuery();
        query.setNimi("");
        query.setKieli("fi");
        query.setPerusteenDiaarinumero(EperusteetServiceMock.PERUSOPETUS_DIAARINUMERO);
        query.setSivu(0);

        Page<OpetussuunnitelmaJulkinenDto> julkiset = opetussuunnitelmaService.getAllJulkaistutOpetussuunnitelmat(query);
        System.out.println("HUOM " + julkiset.getSize());
        assertThat(julkiset).hasSize(1);
    }


    private UusiJulkaisuDto createJulkaisu() {
        UusiJulkaisuDto julkaisu = new UusiJulkaisuDto();
        julkaisu.setJulkaisutiedote(LokalisoituTekstiDto.of("Tiedote", Kieli.FI));
        return julkaisu;
    }
}
