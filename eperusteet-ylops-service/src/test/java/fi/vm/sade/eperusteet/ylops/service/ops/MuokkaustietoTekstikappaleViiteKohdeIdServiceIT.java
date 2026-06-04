package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.MuokkaustietoKayttajallaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.mocks.EperusteetServiceMock;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class MuokkaustietoTekstikappaleViiteKohdeIdServiceIT extends AbstractIntegrationTest {

    private static final String PERUSTEEN_TEKSTI_NIMI = "Uudistuva lukiokoulutus";

    @Autowired
    private MuokkaustietoTekstikappaleViiteKohdeIdService kohdeIdService;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkaustietoService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private TekstiKappaleViiteService tekstiKappaleViiteService;

    @Autowired
    private OpsPohjaSynkronointi opsPohjaSynkronointi;

    private Long opsId;

    @Before
    public void setUp() {
        OpetussuunnitelmaLuontiDto pohja = new OpetussuunnitelmaLuontiDto();
        pohja.setPerusteenDiaarinumero(EperusteetServiceMock.LOPS2019_DIAARINUMERO);
        pohja.setNimi(lt(uniikkiString()));
        pohja.setKuvaus(lt(uniikkiString()));
        pohja.setTyyppi(Tyyppi.POHJA);
        pohja.setKoulutustyyppi(KoulutusTyyppi.LUKIOKOULUTUS);
        OpetussuunnitelmaDto luotuPohja = opetussuunnitelmaService.addPohja(pohja);
        opetussuunnitelmaService.updateTila(luotuPohja.getId(), Tila.VALMIS);

        Long ops1Id = createOps(luotuPohja.getId(), OpetussuunnitelmaLuontiDto.Luontityyppi.KOPIO);
        this.opsId = createOps(ops1Id, OpetussuunnitelmaLuontiDto.Luontityyppi.VIITTEILLA);

        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @Test
    public void korjaaPaivittaaVanhentuneenViiteKohdeIdnTekstiIdnPerusteella() {
        TestTransaction.start();

        Opetussuunnitelma ops = opetussuunnitelmaRepository.getOne(opsId);
        TekstiKappaleViiteDto.Matala perusteenTekstiDto = tekstiKappaleViiteService.getTekstiKappaleViite(
                opsId, findTkNimi(ops, PERUSTEEN_TEKSTI_NIMI).getId());
        perusteenTekstiDto.getTekstiKappale().setTeksti(lt("ops2 teksti"));
        tekstiKappaleViiteService.updateTekstiKappaleViite(opsId, perusteenTekstiDto.getId(), perusteenTekstiDto);

        Long vanhaViiteId = perusteenTekstiDto.getId();

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        opsPohjaSynkronointi.syncTekstitPohjasta(opsId, ops.getPohja().getId());

        TekstiKappaleViite uusiViite = findTkNimi(opetussuunnitelmaRepository.getOne(opsId), PERUSTEEN_TEKSTI_NIMI);
        assertThat(uusiViite.getId()).isNotEqualTo(vanhaViiteId);

        MuokkaustietoKayttajallaDto muokkaustieto = new MuokkaustietoKayttajallaDto();
        muokkaustieto.setKohde(NavigationType.viite);
        muokkaustieto.setKohdeId(vanhaViiteId);

        kohdeIdService.korjaa(opsId, List.of(muokkaustieto));

        assertThat(muokkaustieto.getKohdeId()).isEqualTo(uusiViite.getId());

        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @Test
    public void getOpsMuokkausTietosKorjaaVanhentuneenKohdeIdn() {
        TestTransaction.start();

        Opetussuunnitelma ops = opetussuunnitelmaRepository.getOne(opsId);
        TekstiKappaleViiteDto.Matala perusteenTekstiDto = tekstiKappaleViiteService.getTekstiKappaleViite(
                opsId, findTkNimi(ops, PERUSTEEN_TEKSTI_NIMI).getId());
        perusteenTekstiDto.getTekstiKappale().setTeksti(lt("ops2 teksti muokkaustieto"));
        tekstiKappaleViiteService.updateTekstiKappaleViite(opsId, perusteenTekstiDto.getId(), perusteenTekstiDto);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        opsPohjaSynkronointi.syncTekstitPohjasta(opsId, ops.getPohja().getId());

        Long uusiViiteId = findTkNimi(opetussuunnitelmaRepository.getOne(opsId), PERUSTEEN_TEKSTI_NIMI).getId();
        Date hakuaika = Timestamp.valueOf(LocalDateTime.now().plusDays(1));

        List<MuokkaustietoKayttajallaDto> muokkaustiedot = muokkaustietoService.getOpsMuokkausTietos(opsId, hakuaika, 10);

        assertThat(muokkaustiedot)
                .isNotEmpty()
                .anyMatch(mt -> mt.getKohde() == NavigationType.viite
                        && mt.getTapahtuma() == MuokkausTapahtuma.PAIVITYS
                        && mt.getKohdeId().equals(uusiViiteId));

        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @Test
    public void korjaaEiMuutaKunKohdeIdOnNykyisessaPuussa() {
        TestTransaction.start();

        Opetussuunnitelma ops = opetussuunnitelmaRepository.getOne(opsId);
        Long nykyinenViiteId = findTkNimi(ops, PERUSTEEN_TEKSTI_NIMI).getId();

        MuokkaustietoKayttajallaDto muokkaustieto = new MuokkaustietoKayttajallaDto();
        muokkaustieto.setKohde(NavigationType.viite);
        muokkaustieto.setKohdeId(nykyinenViiteId);

        kohdeIdService.korjaa(opsId, List.of(muokkaustieto));

        assertThat(muokkaustieto.getKohdeId()).isEqualTo(nykyinenViiteId);

        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @Test
    public void korjaaEiMuutaMuunTyyppistaKohdetta() {
        TestTransaction.start();

        MuokkaustietoKayttajallaDto muokkaustieto = new MuokkaustietoKayttajallaDto();
        muokkaustieto.setKohde(NavigationType.tiedot);
        muokkaustieto.setKohdeId(12345L);

        kohdeIdService.korjaa(opsId, List.of(muokkaustieto));

        assertThat(muokkaustieto.getKohdeId()).isEqualTo(12345L);

        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    private Long createOps(Long pohjaId, OpetussuunnitelmaLuontiDto.Luontityyppi luontityyppi) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(KoulutusTyyppi.LUKIOKOULUTUS);
        ops.setPohja(Reference.of(pohjaId));
        ops.setLuontityyppi(luontityyppi);

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        return opetussuunnitelmaService.addOpetussuunnitelma(ops).getId();
    }

    private TekstiKappaleViite findTkNimi(Opetussuunnitelma ops, String nimi) {
        return CollectionUtil.treeToStream(ops.getTekstit(), TekstiKappaleViite::getLapset)
                .filter(viite -> viite.getVanhempi() != null)
                .filter(viite -> viite.getTekstiKappale() != null
                        && viite.getTekstiKappale().getNimi().getTeksti().get(Kieli.FI).equals(nimi))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Tekstikappaletta ei löydy: " + nimi));
    }
}
