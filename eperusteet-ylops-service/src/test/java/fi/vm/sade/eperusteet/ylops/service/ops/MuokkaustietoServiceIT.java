package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.MuokkaustietoKayttajallaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import fi.vm.sade.eperusteet.ylops.test.util.TestUtils;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class MuokkaustietoServiceIT extends AbstractIntegrationTest {

    private final String TAPAHTUMA_TEKSTI = "tapahtuma tapahtui";
    private OpetussuunnitelmaDto dto;
    private final Date MUOKKAUS_AIKA = new Date();

    @Autowired
    MuokkaustietoService service;

    @Autowired
    OpetussuunnitelmaService opetussuunnitelmaService;

    @Before
    public void setup() {
        OpetussuunnitelmaDto luotu = opetussuunnitelmaService.addPohja(TestUtils.createOpsPohja());
        opetussuunnitelmaService.updateTila(luotu.getId(), Tila.VALMIS);
        OpetussuunnitelmaLuontiDto ops = TestUtils.createOps();
        ops.setPohja(Reference.of(luotu.getId()));
        this.dto = opetussuunnitelmaService.addOpetussuunnitelma(TestUtils.createOps());
    }

    @Test
    public void addOpsMuokkausTietoTest() {

        Date hakuaika = new Date();

        assertThat(service.getOpsMuokkausTietos(this.dto.getId(), hakuaika, 10)).hasSize(0);
        service.addOpsMuokkausTieto(this.dto.getId(), createHistoriaTapahtuma(), MuokkausTapahtuma.LUONTI);
        service.addOpsMuokkausTieto(this.dto.getId(), createHistoriaTapahtuma(), MuokkausTapahtuma.LUONTI, "lisatieto1");
        service.addOpsMuokkausTieto(this.dto.getId(), createHistoriaTapahtuma(), MuokkausTapahtuma.LUONTI, NavigationType.liite);
        service.addOpsMuokkausTieto(this.dto.getId(), createHistoriaTapahtuma(), MuokkausTapahtuma.LUONTI, NavigationType.liite, "lisatieto2");

        List<MuokkaustietoKayttajallaDto> dtos = service.getOpsMuokkausTietos(this.dto.getId(), hakuaika, 10);
        assertThat(dtos).hasSize(4);

        dtos = dtos.stream()
                .sorted(Comparator.comparing(MuokkaustietoKayttajallaDto::getId))
                .collect(Collectors.toList());

        assertThat(dtos.get(0).getId()).isNotNull();
        assertThat(dtos.get(0))
                .extracting(
                        "nimi.tekstit",
                        "tapahtuma",
                        "opetussuunnitelmaId",
                        "kohdeId",
                        "kohde",
                        "muokkaaja",
                        "luotu",
                        "kayttajanTieto.etunimet",
                        "kayttajanTieto.sukunimi",
                        "kayttajanTieto.oidHenkilo")
                .containsExactly(
                        LokalisoituTekstiDto.of(TAPAHTUMA_TEKSTI).getTekstit(),
                        MuokkausTapahtuma.LUONTI,
                        this.dto.getId(),
                        1l,
                        NavigationType.viite,
                        "test",
                        MUOKKAUS_AIKA,
                        "Teppo",
                        "Testaaja",
                        "test"
                );

        assertThat(dtos.get(1))
                .extracting(
                        "kohde",
                        "lisatieto")
                .containsExactly(
                        NavigationType.viite,
                        "lisatieto1"
                );

        assertThat(dtos.get(2))
                .extracting(
                        "kohde",
                        "lisatieto")
                .containsExactly(
                        NavigationType.liite,
                        null
                );

        assertThat(dtos.get(3))
                .extracting(
                        "kohde",
                        "lisatieto")
                .containsExactly(
                        NavigationType.liite,
                        "lisatieto2"
                );
    }

    private HistoriaTapahtuma createHistoriaTapahtuma() {

        HistoriaTapahtuma historiaTapahtuma = new HistoriaTapahtuma() {
            @Override
            public Date getLuotu() {
                return null;
            }

            @Override
            public Date getMuokattu() {
                return MUOKKAUS_AIKA;
            }

            @Override
            public String getLuoja() {
                return null;
            }

            @Override
            public String getMuokkaaja() {
                return "1.2.3.4";
            }

            @Override
            public Long getId() {
                return 1l;
            }

            @Override
            public LokalisoituTeksti getNimi() {
                return LokalisoituTeksti.of(Kieli.FI, TAPAHTUMA_TEKSTI);
            }

            @Override
            public NavigationType getNavigationType() {
                return NavigationType.viite;
            }
        };

        return historiaTapahtuma;
    }

}