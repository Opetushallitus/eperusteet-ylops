package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.mocks.EperusteetServiceMock;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class OpetussuunnitelmaHierarkiaKopiointiServiceIT extends AbstractIntegrationTest {

    @Autowired
    private OpetussuunnitelmaHierarkiaKopiointiService hierarkiaKopiointiService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private TekstiKappaleViiteService tekstiKappaleViiteService;

    @Autowired
    private OpsPohjaSynkronointi opsPohjaSynkronointi;

    @Autowired
    private TekstikappaleviiteRepository tekstikappaleviiteRepository;

    private Long pohjaOpsId;
    private Long ops1Id;
    private Long ops2Id;

    @Before
    public void setUp() {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setPerusteenDiaarinumero(EperusteetServiceMock.LOPS2019_DIAARINUMERO);
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTyyppi(Tyyppi.POHJA);
        ops.setKoulutustyyppi(KoulutusTyyppi.LUKIOKOULUTUS);
        ops.setRakennePohjasta(true);
        OpetussuunnitelmaDto luotu = opetussuunnitelmaService.addPohja(ops);
        opetussuunnitelmaService.updateTila(luotu.getId(), Tila.VALMIS);
        this.pohjaOpsId = luotu.getId();
        this.ops1Id = createOps(luotu.getId());
        this.ops2Id = createOps(ops1Id);
    }

    private Long createOps(Long pohjaId) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(KoulutusTyyppi.LUKIOKOULUTUS);
        ops.setPohja(Reference.of(pohjaId));

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        OpetussuunnitelmaDto createdOps = opetussuunnitelmaService.addOpetussuunnitelma(ops);
        return createdOps.getId();
    }

    @Test
    public void testPohjalta() {
        Opetussuunnitelma pohjaOps = opetussuunnitelmaRepository.getOne(pohjaOpsId);
        Opetussuunnitelma ops1 = opetussuunnitelmaRepository.getOne(ops1Id);
        Opetussuunnitelma ops2 = opetussuunnitelmaRepository.getOne(ops2Id);

        {
            assertThat(tkViitteet(pohjaOps)).hasSize(17);

            TekstiKappaleViiteDto.Matala tk1 = addTekstikappale(pohjaOpsId);
            TekstiKappaleViiteDto.Matala tk2 = addTekstikappale(pohjaOpsId);
            TekstiKappaleViiteDto.Matala tk21 = addTekstikappaleLapsi(pohjaOpsId, tk2.getId());

            assertThat(tkViitteet(pohjaOps)).hasSize(20);
            assertThat(perusteTekstikappaleIdt(pohjaOps)).hasSize(17);
        }

        {
            assertThat(tkViitteet(ops1)).hasSize(17);

            TekstiKappaleViiteDto.Matala tk1 = addTekstikappale(pohjaOpsId);
            TekstiKappaleViiteDto.Matala tk2 = addTekstikappale(pohjaOpsId);
            TekstiKappaleViiteDto.Matala tk21 = addTekstikappaleLapsi(pohjaOpsId, tk2.getId());

            TekstiKappaleViiteDto.Matala tk3 = addTekstikappale(ops1Id);
            TekstiKappaleViiteDto.Matala tk4 = addTekstikappale(ops1Id);
            setTekstikappaleOriginal(ops1Id, tk4.getId(), tk3.getId());

            TekstiKappaleViiteDto.Matala tk31 = addTekstikappaleLapsi(ops1Id, tk3.getId());

            assertThat(tkViitteet(ops1)).hasSize(20);

            opsPohjaSynkronointi.syncTekstitPohjasta(ops1Id);

            assertThat(tkViitteet(ops1)).hasSize(26);
            assertThat(perusteTekstikappaleIdt(ops1)).hasSize(17);
            assertThat(tekstikappaleviiteRepository.findOne(tk4.getId()).getOriginal().getId()).isEqualTo(tk3.getId());
        }

        {
            assertThat(tkViitteet(ops2)).hasSize(17);

            TekstiKappaleViiteDto.Matala tk1 = addTekstikappale(ops1Id);
            TekstiKappaleViiteDto.Matala tk2 = addTekstikappale(ops1Id);
            TekstiKappaleViiteDto.Matala tk21 = addTekstikappaleLapsi(ops1Id, tk2.getId());

            TekstiKappaleViiteDto.Matala tk3 = addTekstikappale(ops2Id);
            TekstiKappaleViiteDto.Matala tk4 = addTekstikappale(ops2Id);
            TekstiKappaleViiteDto.Matala tk31 = addTekstikappaleLapsi(ops2Id, tk3.getId());

            assertThat(tkViitteet(ops2)).hasSize(20);

            opsPohjaSynkronointi.syncTekstitPohjasta(ops2Id);

            assertThat(tkViitteet(ops2)).hasSize(32);
            assertThat(perusteTekstikappaleIdt(ops2)).hasSize(17);
        }
    }

    @Test
    public void testPohjaltaPoistettuPerusteTeksti() {
        Opetussuunnitelma ops1 = opetussuunnitelmaRepository.getOne(ops1Id);
        Opetussuunnitelma ops2 = opetussuunnitelmaRepository.getOne(ops2Id);

        TekstiKappaleViiteDto.Matala perusteenTekstiDto = tekstiKappaleViiteService.getTekstiKappaleViite(ops1Id, findTkNimi(ops1, "Uudistuva lukiokoulutus").getId());

        TekstiKappaleViite viite = tekstikappaleviiteRepository.findOne(perusteenTekstiDto.getId());

        List<TekstiKappaleViite> viittaavat = tekstikappaleviiteRepository.findAllByOriginalId(perusteenTekstiDto.getId());
        viittaavat.forEach(vierasViite -> {
            vierasViite.updateOriginal(null);
        });
        viite.setTekstiKappale(null);
        viite.setVanhempi(null);
        tekstikappaleviiteRepository.delete(viite);

        assertThat(tkViitteet(ops1)).hasSize(16);

        addTekstikappaleLapsi("perustetekstin alla oleva teksti", ops2Id, findTkNimi(ops2, "Uudistuva lukiokoulutus").getId());
        assertThat(tkViitteet(ops2)).hasSize(18);

        opsPohjaSynkronointi.syncTekstitPohjasta(ops2Id);
        assertThat(tkViitteet(ops2)).hasSize(17);

        assertThat(findTkNimi(ops1, "Uudistuva lukiokoulutus")).isNull();
        assertThat(findTkNimi(ops2, "Uudistuva lukiokoulutus")).isNull();
        assertThat(findTkNimi(ops2, "perustetekstin alla oleva teksti")).isNotNull();
    }

    @Test
    public void testTekstienSailyvyys() {
        Opetussuunnitelma ops1 = opetussuunnitelmaRepository.getOne(ops1Id);
        Opetussuunnitelma ops2 = opetussuunnitelmaRepository.getOne(ops2Id);

        {
            TekstiKappaleViiteDto.Matala perusteenTekstiDto = tekstiKappaleViiteService.getTekstiKappaleViite(ops1Id, findTkNimi(ops1, "Uudistuva lukiokoulutus").getId());
            perusteenTekstiDto.getTekstiKappale().setTeksti(lt("ops1 teksti"));
            tekstiKappaleViiteService.updateTekstiKappaleViite(ops1Id, perusteenTekstiDto.getId(), perusteenTekstiDto);
            TekstiKappaleViiteDto.Matala tk1 = addTekstikappale("ops1 oma tekstikappale", ops1Id);
        }

        {
            TekstiKappaleViiteDto.Matala perusteenTekstiDto = tekstiKappaleViiteService.getTekstiKappaleViite(ops2Id, findTkNimi(ops2, "Uudistuva lukiokoulutus").getId());
            perusteenTekstiDto.getTekstiKappale().setTeksti(lt("ops2 teksti"));
            tekstiKappaleViiteService.updateTekstiKappaleViite(ops2Id, perusteenTekstiDto.getId(), perusteenTekstiDto);
            TekstiKappaleViiteDto.Matala tk21 = addTekstikappaleLapsi("ops2.1 oma tekstikappale perusteen tekstikappaleen alla", ops2Id, perusteenTekstiDto.getId());
            TekstiKappaleViiteDto.Matala tk22 = addTekstikappaleLapsi("ops2.2 oma tekstikappale perusteen tekstikappaleen alla", ops2Id, perusteenTekstiDto.getId());
            TekstiKappaleViiteDto.Matala tk211 = addTekstikappaleLapsi("ops2.1.1 oma tekstikappale perusteen tekstikappaleen alla", ops2Id, tk21.getId());
            TekstiKappaleViiteDto.Matala tk212 = addTekstikappaleLapsi("ops2.1.2 oma tekstikappale perusteen tekstikappaleen alla", ops2Id, tk21.getId());
            TekstiKappaleViiteDto.Matala tk2121 = addTekstikappaleLapsi("ops2.1.2.1 oma tekstikappale perusteen tekstikappaleen alla", ops2Id, tk212.getId());
            TekstiKappaleViiteDto.Matala tk2122 = addTekstikappaleLapsi("ops2.1.2.2 oma tekstikappale perusteen tekstikappaleen alla", ops2Id, tk212.getId());

            TekstiKappaleViiteDto.Matala perusteenTekstiDto2 = tekstiKappaleViiteService.getTekstiKappaleViite(ops2Id, findTkNimi(ops2, "Opetussuunnitelman laatiminen ja sisältö").getId());
            TekstiKappaleViiteDto.Matala tk31 = addTekstikappaleLapsi("ops3.1 oma tekstikappale perusteen tekstikappaleen alla", ops2Id, perusteenTekstiDto2.getId());
            TekstiKappaleViiteDto.Matala tk32 = addTekstikappaleLapsi("ops3.2 oma tekstikappale perusteen tekstikappaleen alla", ops2Id, perusteenTekstiDto2.getId());
            TekstiKappaleViiteDto.Matala tk311 = addTekstikappaleLapsi("ops3.1.1 oma tekstikappale perusteen tekstikappaleen alla", ops2Id, tk31.getId());
            TekstiKappaleViiteDto.Matala tk312 = addTekstikappaleLapsi("ops3.1.2 oma tekstikappale perusteen tekstikappaleen alla", ops2Id, tk31.getId());

            TekstiKappaleViiteDto.Matala tk4 = addTekstikappale("ops4 oma tekstikappale", ops2Id);
            TekstiKappaleViiteDto.Matala tk5 = addTekstikappale("ops5 oma tekstikappale", ops2Id);
            TekstiKappaleViiteDto.Matala tk41 = addTekstikappaleLapsi("ops4.1 oma tekstikappale", ops2Id, tk4.getId());
            TekstiKappaleViiteDto.Matala tk42 = addTekstikappaleLapsi("ops4.2 oma tekstikappale", ops2Id, tk4.getId());
        }

        opsPohjaSynkronointi.syncTekstitPohjasta(ops2Id);

        assertThat(findTkNimi(ops1, "Uudistuva lukiokoulutus").getTekstiKappale().getTeksti().getTeksti().get(Kieli.FI)).isEqualTo("ops1 teksti");
        assertThat(findTkNimi(ops1, "ops1 oma tekstikappale")).isNotNull();

        assertThat(findTkNimi(ops2, "Uudistuva lukiokoulutus").getTekstiKappale().getTeksti().getTeksti().get(Kieli.FI)).isEqualTo("ops2 teksti");
        assertThat(findTkNimi(ops2, "Uudistuva lukiokoulutus").getOriginal()).isNotNull();
        assertThat(findTkNimi(ops2, "Uudistuva lukiokoulutus").getOriginal().getTekstiKappale().getTeksti().getTeksti().get(Kieli.FI)).isEqualTo("ops1 teksti");
        assertThat(findTkNimi(ops2, "Uudistuva lukiokoulutus").getLapset()).hasSize(2);
        assertThat(findTkNimi(ops2, "Uudistuva lukiokoulutus").getLapset().get(0).getId()).isEqualTo(findTkNimi(ops2, "ops2.1 oma tekstikappale perusteen tekstikappaleen alla").getId());
        assertThat(findTkNimi(ops2, "Uudistuva lukiokoulutus").getLapset().get(1).getId()).isEqualTo(findTkNimi(ops2, "ops2.2 oma tekstikappale perusteen tekstikappaleen alla").getId());

        assertThat(findTkNimi(ops2, "ops2.1 oma tekstikappale perusteen tekstikappaleen alla")).isNotNull();
        assertThat(findTkNimi(ops2, "ops2.1 oma tekstikappale perusteen tekstikappaleen alla").getLapset()).hasSize(2);
        assertThat(findTkNimi(ops2, "ops2.1 oma tekstikappale perusteen tekstikappaleen alla").getLapset().get(0).getId()).isEqualTo(findTkNimi(ops2, "ops2.1.1 oma tekstikappale perusteen tekstikappaleen alla").getId());
        assertThat(findTkNimi(ops2, "ops2.1 oma tekstikappale perusteen tekstikappaleen alla").getLapset().get(1).getId()).isEqualTo(findTkNimi(ops2, "ops2.1.2 oma tekstikappale perusteen tekstikappaleen alla").getId());

        assertThat(findTkNimi(ops2, "ops2.1.2 oma tekstikappale perusteen tekstikappaleen alla")).isNotNull();
        assertThat(findTkNimi(ops2, "ops2.1.2 oma tekstikappale perusteen tekstikappaleen alla").getLapset()).hasSize(2);
        assertThat(findTkNimi(ops2, "ops2.1.2 oma tekstikappale perusteen tekstikappaleen alla").getLapset().get(0).getId()).isEqualTo(findTkNimi(ops2, "ops2.1.2.1 oma tekstikappale perusteen tekstikappaleen alla").getId());
        assertThat(findTkNimi(ops2, "ops2.1.2 oma tekstikappale perusteen tekstikappaleen alla").getLapset().get(1).getId()).isEqualTo(findTkNimi(ops2, "ops2.1.2.2 oma tekstikappale perusteen tekstikappaleen alla").getId());

        assertThat(findTkNimi(ops2, "Opetussuunnitelman laatiminen ja sisältö").getLapset()).hasSize(6);
        assertThat(findTkNimi(ops2, "Opetussuunnitelman laatiminen ja sisältö").getLapset().get(0).getId()).isEqualTo(findTkNimi(ops2, "Uudistuva lukiokoulutus").getId());
        assertThat(findTkNimi(ops2, "Opetussuunnitelman laatiminen ja sisältö").getLapset().get(1).getId()).isEqualTo(findTkNimi(ops2, "Opetussuunnitelman laatiminen").getId());
        assertThat(findTkNimi(ops2, "Opetussuunnitelman laatiminen ja sisältö").getLapset().get(2).getId()).isEqualTo(findTkNimi(ops2, "Opetussuunnitelman sisältö").getId());
        assertThat(findTkNimi(ops2, "Opetussuunnitelman laatiminen ja sisältö").getLapset().get(3).getId()).isEqualTo(findTkNimi(ops2, "Lukiokoulutuksen tehtävä").getId());
        assertThat(findTkNimi(ops2, "Opetussuunnitelman laatiminen ja sisältö").getLapset().get(4).getId()).isEqualTo(findTkNimi(ops2, "ops3.1 oma tekstikappale perusteen tekstikappaleen alla").getId());
        assertThat(findTkNimi(ops2, "Opetussuunnitelman laatiminen ja sisältö").getLapset().get(5).getId()).isEqualTo(findTkNimi(ops2, "ops3.2 oma tekstikappale perusteen tekstikappaleen alla").getId());
        assertThat(findTkNimi(ops2, "ops3.1 oma tekstikappale perusteen tekstikappaleen alla").getLapset()).hasSize(2);
        assertThat(findTkNimi(ops2, "ops3.1 oma tekstikappale perusteen tekstikappaleen alla").getLapset().get(0).getId()).isEqualTo(findTkNimi(ops2, "ops3.1.1 oma tekstikappale perusteen tekstikappaleen alla").getId());
        assertThat(findTkNimi(ops2, "ops3.1 oma tekstikappale perusteen tekstikappaleen alla").getLapset().get(1).getId()).isEqualTo(findTkNimi(ops2, "ops3.1.2 oma tekstikappale perusteen tekstikappaleen alla").getId());

        assertThat(findTkNimi(ops2, "ops4 oma tekstikappale")).isNotNull();
        assertThat(findTkNimi(ops2, "ops4 oma tekstikappale").getLapset()).hasSize(2);
        assertThat(findTkNimi(ops2, "ops4 oma tekstikappale").getLapset().get(0).getId()).isEqualTo(findTkNimi(ops2, "ops4.1 oma tekstikappale").getId());
        assertThat(findTkNimi(ops2, "ops4 oma tekstikappale").getLapset().get(1).getId()).isEqualTo(findTkNimi(ops2, "ops4.2 oma tekstikappale").getId());

        assertThat(findTkNimi(ops2, "ops5 oma tekstikappale")).isNotNull();
    }

    @Test
    public void lukumaaratSamat() {
        assertThat(opsPohjaSynkronointi.opetussuunnitelmanPohjallaUusiaTeksteja(ops2Id)).isFalse();

        TekstiKappaleViiteDto.Matala tk1 = addTekstikappale("ops1 oma tekstikappale", ops1Id);

        assertThat(opsPohjaSynkronointi.opetussuunnitelmanPohjallaUusiaTeksteja(ops2Id)).isFalse();
    }

    @Test
    public void pohjallaYlimaarainenPerusteTeksti() {
        TekstiKappaleViiteDto.Matala tk1 = addTekstikappale("ops1 oma tekstikappale", ops1Id);
        tk1.setPerusteTekstikappaleId(5666l);
        tekstiKappaleViiteService.updateTekstiKappaleViite(ops1Id, tk1.getId(), tk1);

        assertThat(opsPohjaSynkronointi.opetussuunnitelmanPohjallaUusiaTeksteja(ops2Id)).isTrue();
    }

    @Test
    public void opsillaYlimaarainenPerusteTeksti() {
        TekstiKappaleViiteDto.Matala tk1 = addTekstikappale("ops2 oma tekstikappale", ops2Id);
        tk1.setPerusteTekstikappaleId(5666l);
        tekstiKappaleViiteService.updateTekstiKappaleViite(ops2Id, tk1.getId(), tk1);

        assertThat(opsPohjaSynkronointi.opetussuunnitelmanPohjallaUusiaTeksteja(ops2Id)).isTrue();
    }

    private List<TekstiKappaleViite> tkViitteet(Opetussuunnitelma ops) {
        return CollectionUtil.treeToStream(ops.getTekstit(), TekstiKappaleViite::getLapset)
                .filter(viite -> viite.getVanhempi() != null)
                .collect(Collectors.toList());
    }

    private TekstiKappaleViite findTkNimi(Opetussuunnitelma ops, String nimi) {
        return CollectionUtil.treeToStream(ops.getTekstit(), TekstiKappaleViite::getLapset)
                .filter(viite -> viite.getVanhempi() != null)
                .filter(viite -> viite.getTekstiKappale().getNimi().getTeksti().get(Kieli.FI).equals(nimi))
                .findFirst()
                .orElse(null);
    }

    private List<Long> perusteTekstikappaleIdt(Opetussuunnitelma ops) {
        return CollectionUtil.treeToStream(ops.getTekstit(), TekstiKappaleViite::getLapset)
                .filter(viite -> viite.getVanhempi() != null && viite.getPerusteTekstikappaleId() != null)
                .map(TekstiKappaleViite::getPerusteTekstikappaleId)
                .collect(Collectors.toList());
    }

    private TekstiKappaleViiteDto.Matala addTekstikappaleLapsi(String nimi, Long opsId, Long parentId) {
        TekstiKappaleViiteDto.Matala viiteDto = createTekstikappaleViite(nimi);
        return opetussuunnitelmaService.addTekstiKappaleLapsi(opsId, parentId, viiteDto);
    }

    private TekstiKappaleViiteDto.Matala addTekstikappale(String nimi, Long opsId) {
        TekstiKappaleViiteDto.Matala viiteDto = createTekstikappaleViite(nimi);
        return opetussuunnitelmaService.addTekstiKappale(opsId, viiteDto);
    }

    private TekstiKappaleViiteDto.Matala addTekstikappaleLapsi(Long opsId, Long parentId) {
        TekstiKappaleViiteDto.Matala viiteDto = createTekstikappaleViite(null);
        return opetussuunnitelmaService.addTekstiKappaleLapsi(opsId, parentId, viiteDto);
    }

    private TekstiKappaleViiteDto.Matala addTekstikappale(Long opsId) {
        TekstiKappaleViiteDto.Matala viiteDto = createTekstikappaleViite(null);
        return opetussuunnitelmaService.addTekstiKappale(opsId, viiteDto);
    }

    private TekstiKappaleViiteDto.Matala createTekstikappaleViite(String nimi) {
        TekstiKappaleDto tekstiKappale = new TekstiKappaleDto();
        tekstiKappale.setNimi(lt(nimi != null ? nimi : uniikkiString()));
        tekstiKappale.setTeksti(lt(uniikkiString()));

        TekstiKappaleViiteDto.Matala viiteDto = new TekstiKappaleViiteDto.Matala();
        viiteDto.setPakollinen(true);
        viiteDto.setTekstiKappale(tekstiKappale);
        return viiteDto;
    }

    private void setTekstikappaleOriginal(Long opsId, Long tkId, Long originalId) {
        TekstiKappaleViite viite = tekstikappaleviiteRepository.findOne(tkId);
        TekstiKappaleViite original = tekstikappaleviiteRepository.findOne(originalId);
        viite.updateOriginal(original);
        tekstikappaleviiteRepository.save(viite);
    }
}
