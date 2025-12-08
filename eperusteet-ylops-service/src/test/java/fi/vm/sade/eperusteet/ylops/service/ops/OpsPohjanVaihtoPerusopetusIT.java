package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.mocks.EperusteetServiceMock;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OpsPohjanVaihtoPerusopetusIT extends AbstractIntegrationTest {

    @Autowired
    private OpsPohjanVaihto opsPohjanVaihtoPerusopetus;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    private Long pohja1Id;
    private Long pohja2Id;
    private Long opsId;
    private Long pohja3DifferentPerusteId;

    @Before
    public void setUp() {
        // Create first pohja (base curriculum)
        OpetussuunnitelmaLuontiDto pohja1Luonti = createPohjaLuonti(EperusteetServiceMock.PERUSOPETUS_DIAARINUMERO);
        OpetussuunnitelmaDto pohja1 = opetussuunnitelmaService.addPohja(pohja1Luonti);
        opetussuunnitelmaService.updateTila(pohja1.getId(), Tila.VALMIS);
        pohja1Id = pohja1.getId();

        // Create second pohja with the same peruste
        OpetussuunnitelmaLuontiDto pohja2Luonti = createPohjaLuonti(EperusteetServiceMock.PERUSOPETUS_DIAARINUMERO);
        OpetussuunnitelmaDto pohja2 = opetussuunnitelmaService.addPohja(pohja2Luonti);
        opetussuunnitelmaService.updateTila(pohja2.getId(), Tila.VALMIS);
        pohja2Id = pohja2.getId();

        // Create third pohja with a different peruste
        OpetussuunnitelmaLuontiDto pohja3Luonti = createPohjaLuonti("104/011/2014");
        OpetussuunnitelmaDto pohja3 = opetussuunnitelmaService.addPohja(pohja3Luonti);
        opetussuunnitelmaService.updateTila(pohja3.getId(), Tila.VALMIS);
        pohja3DifferentPerusteId = pohja3.getId();

        // Create an ops based on pohja1
        OpetussuunnitelmaLuontiDto opsLuonti = createOpsLuonti();
        opsLuonti.setPohja(Reference.of(pohja1Id));
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.addOpetussuunnitelma(opsLuonti);
        opsId = ops.getId();
    }

    @Test
    public void testVaihdaPohjaSuccessfully() {
        // Change pohja from pohja1 to pohja2
        opsPohjanVaihtoPerusopetus.vaihdaPohja(opsId, pohja2Id);

        // Verify the change
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findById(opsId).orElseThrow();
        assertNotNull(ops.getPohja());
        assertEquals(pohja2Id, ops.getPohja().getId());
    }

    @Test
    public void testVaihdaPohjaToSamePohja() {
        // Try to change pohja to the same pohja (should fail)
        assertThatThrownBy(() -> opsPohjanVaihtoPerusopetus.vaihdaPohja(opsId, pohja1Id))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("virheellinen-pohja");
    }

    @Test
    public void testVaihdaPohjaToOpsItself() {
        // Try to set ops itself as pohja (should fail)
        assertThatThrownBy(() -> opsPohjanVaihtoPerusopetus.vaihdaPohja(opsId, opsId))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("virheellinen-pohja");
    }

    @Test
    public void testVaihdaPohjaToDifferentPeruste() {
        // Try to change pohja to one with a different peruste (should fail)
        assertThatThrownBy(() -> opsPohjanVaihtoPerusopetus.vaihdaPohja(opsId, pohja3DifferentPerusteId))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("pohja-vaihdettavissa-vain-samaan-perusteeseen");
    }

    @Test
    public void testVaihdaPohjaVerifyOldPohjaNotChanged() {
        // Get initial state
        Opetussuunnitelma pohja1Before = opetussuunnitelmaRepository.findById(pohja1Id).orElseThrow();
        
        // Change pohja
        opsPohjanVaihtoPerusopetus.vaihdaPohja(opsId, pohja2Id);
        
        // Verify old pohja is not affected
        Opetussuunnitelma pohja1After = opetussuunnitelmaRepository.findById(pohja1Id).orElseThrow();
        assertNotNull(pohja1After);
        assertEquals(pohja1Before.getId(), pohja1After.getId());
    }

    @Test
    public void testVaihdaPohjaVerifySamePeruste() {
        // Change pohja
        opsPohjanVaihtoPerusopetus.vaihdaPohja(opsId, pohja2Id);
        
        // Verify that the new pohja has the same peruste as the old one
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findById(opsId).orElseThrow();
        Opetussuunnitelma pohja1 = opetussuunnitelmaRepository.findById(pohja1Id).orElseThrow();
        Opetussuunnitelma pohja2 = opetussuunnitelmaRepository.findById(pohja2Id).orElseThrow();
        
        assertThat(ops.getCachedPeruste().getPerusteId())
                .isEqualTo(pohja1.getCachedPeruste().getPerusteId())
                .isEqualTo(pohja2.getCachedPeruste().getPerusteId());
    }

    @Test
    public void testHaeVaihtoehdot() {
        // Currently returns empty set
        Set<OpetussuunnitelmaInfoDto> vaihtoehdot = opsPohjanVaihtoPerusopetus.haeVaihtoehdot(opsId);
        
        assertNotNull(vaihtoehdot);
        assertThat(vaihtoehdot).isEmpty();
    }
    
    @Test
    public void testVaihdaPohjaMultipleTimes() {
        // Change pohja from pohja1 to pohja2
        opsPohjanVaihtoPerusopetus.vaihdaPohja(opsId, pohja2Id);
        
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findById(opsId).orElseThrow();
        assertEquals(pohja2Id, ops.getPohja().getId());
        
        // Change pohja back from pohja2 to pohja1
        opsPohjanVaihtoPerusopetus.vaihdaPohja(opsId, pohja1Id);
        
        ops = opetussuunnitelmaRepository.findById(opsId).orElseThrow();
        assertEquals(pohja1Id, ops.getPohja().getId());
        
        // Change again to pohja2
        opsPohjanVaihtoPerusopetus.vaihdaPohja(opsId, pohja2Id);
        
        ops = opetussuunnitelmaRepository.findById(opsId).orElseThrow();
        assertEquals(pohja2Id, ops.getPohja().getId());
    }

    @Test
    public void testVaihdaPohjaWithNonExistentOps() {
        Long nonExistentOpsId = 999999L;
        
        assertThatThrownBy(() -> opsPohjanVaihtoPerusopetus.vaihdaPohja(nonExistentOpsId, pohja2Id))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void testVaihdaPohjaWithNonExistentPohja() {
        Long nonExistentPohjaId = 999999L;
        
        assertThatThrownBy(() -> opsPohjanVaihtoPerusopetus.vaihdaPohja(opsId, nonExistentPohjaId))
                .isInstanceOf(Exception.class);
    }

    // Helper methods

    private OpetussuunnitelmaLuontiDto createPohjaLuonti(String diaarinumero) {
        OpetussuunnitelmaLuontiDto pohja = new OpetussuunnitelmaLuontiDto();
        pohja.setPerusteenDiaarinumero(diaarinumero);
        pohja.setNimi(lt(uniikkiString()));
        pohja.setKuvaus(lt(uniikkiString()));
        pohja.setTyyppi(Tyyppi.POHJA);
        pohja.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        return pohja;
    }

    private OpetussuunnitelmaLuontiDto createOpsLuonti() {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        
        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        
        OrganisaatioDto organisaatio = new OrganisaatioDto();
        organisaatio.setOid("1.2.246.562.10.83037752777");
        organisaatio.setNimi(lt("Testikunta"));
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(organisaatio)));
        
        return ops;
    }
}

