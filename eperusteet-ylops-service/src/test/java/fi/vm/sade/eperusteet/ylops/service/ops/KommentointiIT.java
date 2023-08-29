package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.KommenttiKahva;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.ops.KommenttiKahvaDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.teksti.LokalisoituTekstiRepository;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.test.AbstractH2IntegrationTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class KommentointiIT extends AbstractH2IntegrationTest {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private Kommentti2019Service kommenttiService;

    @Autowired
    private LokalisoituTekstiRepository lokalisoituTekstiRepository;

    @Test
    public void kommenttikahvanTallennus() {
        LokalisoituTeksti lt = LokalisoituTeksti.of(Kieli.FI, "<p>Tämä on tekstiä. <b>Toinen lause.</b>. Vielä yksi <i>ilman</i> boldausta.</p>");
        lt = lokalisoituTekstiRepository.save(lt);
        UUID vanhaTunniste = lt.getTunniste();
        Long vanhaId = lt.getId();
        Map<Kieli, String> vanhaTekstiRaw = lt.getTeksti();
        Map<Kieli, String> vanhaTeksti = lt.getTeksti();

        KommenttiKahvaDto kahva = kommenttiService.addKommenttiKahva(KommenttiKahvaDto.of(
                42L, lt.getId(), Kieli.FI, 3, 10, "x"));
        LokalisoituTeksti paivitetty = lokalisoituTekstiRepository.getOne(lt.getId());
        assertThat(paivitetty).isNotNull();

        Map<Kieli, String> teksti = mapper.map(paivitetty, LokalisoituTekstiDto.class).getTekstit();
        String s = teksti.get(Kieli.FI);

        Document parsed = Jsoup.parse(s);
        Set<UUID> kommentit = parsed.select("span[kommentti]").stream()
                .map(el -> el.attr("kommentti"))
                .map(UUID::fromString)
                .collect(Collectors.toSet());

        List<UUID> collected = paivitetty.getKetjut().stream()
                .map(KommenttiKahva::getThread)
                .collect(Collectors.toList());

        assertThat(paivitetty)
                .returns(vanhaId, LokalisoituTeksti::getId)
                .returns(vanhaTeksti, LokalisoituTeksti::getTeksti)
                .returns(vanhaTekstiRaw, LokalisoituTeksti::getTeksti)
                .returns(vanhaTunniste, LokalisoituTeksti::getTunniste);
        assertThat(kommentit).containsExactlyInAnyOrderElementsOf(collected);
    }

    @Test
    public void testVirheellisetKahvat() {
        LokalisoituTeksti lt = LokalisoituTeksti.of(Kieli.FI, "<p>Tämä on tekstiä. Toinen lause. Vielä yksi ilman boldausta.</p>");
        lt = lokalisoituTekstiRepository.save(lt);
        final Long ltId = lt.getId();

        kommenttiService.addKommenttiKahva(KommenttiKahvaDto.of(
                42L, lt.getId(), Kieli.FI, 24, 30, "a"));

        // Yli oikealta
        assertThatThrownBy(() -> {
            kommenttiService.addKommenttiKahva(KommenttiKahvaDto.of(
                    42L, ltId, Kieli.FI, 27, 35, "b"));
        }).hasMessage("virheellinen-kommentin-sijainti");

        // Sisällä
        assertThatThrownBy(() -> {
            kommenttiService.addKommenttiKahva(KommenttiKahvaDto.of(
                    42L, ltId, Kieli.FI, 25, 28, "b"));
        }).hasMessage("virheellinen-kommentin-sijainti");

        // Yli vasemmalta
        assertThatThrownBy(() -> {
            kommenttiService.addKommenttiKahva(KommenttiKahvaDto.of(
                    42L, ltId, Kieli.FI, 20, 25, "b"));
        }).hasMessage("kommentit-eivat-saa-olla-paallekkain");
    }

    @Test
    public void testUseampiKommentti() {
        String original = "<p>Tämä on tekstiä. Toinen lause. Vielä yksi ilman boldausta.</p>";
        LokalisoituTeksti lt = LokalisoituTeksti.of(Kieli.FI, original);
        lt = lokalisoituTekstiRepository.save(lt);

        KommenttiKahvaDto a = kommenttiService.addKommenttiKahva(KommenttiKahvaDto.of(
                42L, lt.getId(), Kieli.FI, 15, 18, "a"));
        em.flush();
        KommenttiKahvaDto b = kommenttiService.addKommenttiKahva(KommenttiKahvaDto.of(
                42L, lt.getId(), Kieli.FI, 5, 8, "b"));
        em.flush();
//        kommenttiService.addKommenttiKahva(KommenttiKahvaDto.of(
//                42L, lt.getId(), Kieli.FI, 5, 8, "a"));

        LokalisoituTeksti kahvallinen = lokalisoituTekstiRepository.getOne(lt.getId());
        Set<KommenttiKahva> ketjut = kahvallinen.getKetjut();
        String teksti = kahvallinen.getTeksti().get(Kieli.FI);
        assertThat(teksti).isEqualTo(original);
    }

    @Test
    public void testTallennettuSisaltoEiSisallaKommenttikahvoja() {
        LokalisoituTeksti lt = LokalisoituTeksti.of(Kieli.FI, "<p>Tämä on tekstiä. Toinen lause. Vielä yksi ilman boldausta.</p>");
        lt = lokalisoituTekstiRepository.save(lt);

        kommenttiService.addKommenttiKahva(KommenttiKahvaDto.of(
                42L, lt.getId(), Kieli.FI, 24, 30, "a"));

        LokalisoituTekstiDto dto = mapper.map(lokalisoituTekstiRepository.getOne(lt.getId()), LokalisoituTekstiDto.class);
        dto.getTekstit().put(Kieli.SV, "<p>X</p>");
        LokalisoituTeksti edited = lokalisoituTekstiRepository.save(mapper.map(dto, LokalisoituTeksti.class));

        {// Kommenttikahvat eivät tallennu tekstin joukkoon. Teksti pysyy samana tallennuksen jälkeen.
            String originalStr = lt.getTeksti().get(Kieli.FI);
            assertThat(lt.getId()).isNotEqualTo(edited.getId());
            String editedStr = edited.getTeksti().get(Kieli.FI);
            assertThat(originalStr).isEqualTo(editedStr);
        }

        { // Ketjut kopioituvat uuteen versioon
            Set<UUID> vanhatKetjut = lt.getKetjut().stream()
                    .map(KommenttiKahva::getThread)
                    .collect(Collectors.toSet());
            Set<UUID> uudetKetjut = edited.getKetjut().stream()
                    .map(KommenttiKahva::getThread)
                    .collect(Collectors.toSet());
            assertThat(uudetKetjut).containsExactlyInAnyOrderElementsOf(vanhatKetjut);
        }

    }

}
