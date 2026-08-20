package fi.vm.sade.eperusteet.ylops.dto.aipe.export;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.ylops.dto.export.OpetussuunnitelmaExportAipeDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.resource.config.InitJacksonConverter;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AIPEExportDtoSerializationTest {

    private final ObjectMapper mapper = InitJacksonConverter.createMapper();

    @Test
    public void julkaisuJsonSailyttääAipeSisallon() throws Exception {
        PerusteAIPEVaiheSisaltoDto vaihePeruste = new PerusteAIPEVaiheSisaltoDto();
        vaihePeruste.setId(17101L);
        vaihePeruste.setNimi(LokalisoituTekstiDto.of("Vaihe"));

        PerusteAIPEOppiaineSisaltoDto oppiainePeruste = new PerusteAIPEOppiaineSisaltoDto();
        oppiainePeruste.setId(17401L);
        oppiainePeruste.setNimi(LokalisoituTekstiDto.of("Oppiaine"));

        PerusteAIPEKurssiSisaltoDto kurssiPeruste = new PerusteAIPEKurssiSisaltoDto();
        kurssiPeruste.setId(17501L);
        kurssiPeruste.setNimi(LokalisoituTekstiDto.of("Kurssi"));
        PerusteAIPEOpetuksentavoiteKevytSisaltoDto kurssiTavoite = new PerusteAIPEOpetuksentavoiteKevytSisaltoDto();
        kurssiTavoite.setId(17401L);
        kurssiTavoite.setTavoite(LokalisoituTekstiDto.of("Tavoite T1"));
        kurssiPeruste.getTavoitteet().add(kurssiTavoite);

        AIPEKurssiExportDto kurssi = new AIPEKurssiExportDto();
        kurssi.setId(2L);
        kurssi.setPerusteSisalto(kurssiPeruste);

        AIPEOppiaineExportDto oppiaine = new AIPEOppiaineExportDto();
        oppiaine.setId(1L);
        oppiaine.setPerusteSisalto(oppiainePeruste);
        oppiaine.getKurssit().add(kurssi);

        AIPEVaiheExportDto vaihe = new AIPEVaiheExportDto();
        vaihe.setId(10L);
        vaihe.setPerusteenVaiheId(17101L);
        vaihe.setPerusteSisalto(vaihePeruste);
        vaihe.getOppiaineet().add(oppiaine);

        AIPESisaltoExportDto sisalto = new AIPESisaltoExportDto();
        sisalto.setId(100L);
        sisalto.getVaiheet().add(vaihe);

        OpetussuunnitelmaExportAipeDto export = new OpetussuunnitelmaExportAipeDto();
        export.setAipe(sisalto);

        JsonNode json = mapper.valueToTree(export);
        OpetussuunnitelmaExportAipeDto luettu = mapper.treeToValue(json, OpetussuunnitelmaExportAipeDto.class);

        AIPEVaiheExportDto luettuVaihe = luettu.getAipe().getVaiheet().get(0);
        assertThat(luettuVaihe.getPerusteSisalto()).isNotNull();
        assertThat(luettuVaihe.getPerusteSisalto().getId()).isEqualTo(17101L);
        assertThat(luettuVaihe.getOppiaineet()).hasSize(1);
        assertThat(luettuVaihe.getOppiaineet().get(0).getPerusteSisalto()).isNotNull();
        assertThat(luettuVaihe.getOppiaineet().get(0).getKurssit()).hasSize(1);
        assertThat(luettuVaihe.getOppiaineet().get(0).getKurssit().get(0).getPerusteSisalto()).isNotNull();
        assertThat(luettuVaihe.getOppiaineet().get(0).getKurssit().get(0).getPerusteSisalto().getTavoitteet())
                .extracting(PerusteAIPEOpetuksentavoiteKevytSisaltoDto::getId)
                .containsExactly(17401L);
    }

    @Test
    public void laajaalaisetSerialisoituvatKokonaisinaJokaTavoitteessa() throws Exception {
        PerusteAIPELaajaalainenosaaminenSisaltoDto lao = new PerusteAIPELaajaalainenosaaminenSisaltoDto();
        lao.setId(2500111L);
        lao.setTunniste(UUID.fromString("1f09f2a0-906b-4a64-a2ec-76049481533b"));
        lao.setNimi(LokalisoituTekstiDto.of("Kulttuurinen osaaminen ja vuorovaikutus"));

        PerusteAIPEOpetuksentavoiteSisaltoDto t1 = new PerusteAIPEOpetuksentavoiteSisaltoDto();
        t1.setId(1L);
        t1.setLaajattavoitteet(new LinkedHashSet<>(List.of(lao)));

        PerusteAIPEOpetuksentavoiteSisaltoDto t2 = new PerusteAIPEOpetuksentavoiteSisaltoDto();
        t2.setId(2L);
        t2.setLaajattavoitteet(new LinkedHashSet<>(List.of(lao)));

        PerusteAIPEOppiaineSisaltoDto oppiaine = new PerusteAIPEOppiaineSisaltoDto();
        oppiaine.getTavoitteet().add(t1);
        oppiaine.getTavoitteet().add(t2);

        JsonNode json = mapper.valueToTree(oppiaine);
        JsonNode laaja1 = json.get("tavoitteet").get(0).get("laajaalaisetosaamiset").get(0);
        JsonNode laaja2 = json.get("tavoitteet").get(1).get("laajaalaisetosaamiset").get(0);

        assertThat(laaja1.isObject()).isTrue();
        assertThat(laaja1.get("nimi")).isNotNull();
        assertThat(laaja2.isObject()).isTrue();
        assertThat(laaja2.get("nimi")).isNotNull();
    }
}
