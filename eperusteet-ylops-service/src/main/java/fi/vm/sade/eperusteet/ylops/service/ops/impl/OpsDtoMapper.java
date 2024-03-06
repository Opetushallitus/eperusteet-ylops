package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaineenvuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ops.LaajaalainenosaaminenDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetuksenKohdealueDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.VuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOpetuksenkohdealueDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OpsDtoMapper {

    @Autowired
    private DtoMapper mapper;

    public Oppiaine fromDto(OppiaineLaajaDto dto) {
        Oppiaine oppiaine = new Oppiaine(dto.getTunniste());

        mapper.map(dto, oppiaine);
        if (dto.getOppimaarat() != null) {
            dto.getOppimaarat().forEach(o -> oppiaine.addOppimaara(fromDto(o)));
        }

        if (dto.getVuosiluokkakokonaisuudet() != null) {
            for (OppiaineenVuosiluokkakokonaisuusDto ovk : dto.getVuosiluokkakokonaisuudet()) {
                oppiaine.addVuosiluokkaKokonaisuus(
                        mapper.map(ovk, Oppiaineenvuosiluokkakokonaisuus.class));
            }
        }

        return oppiaine;
    }

    public Oppiaine fromDto(OppiaineDto dto) {
        return fromDto(dto, true);
    }

    public Oppiaine fromDto(OppiaineDto dto, boolean includeVlk) {
        Oppiaine oppiaine = dto.getTyyppi() != OppiaineTyyppi.YHTEINEN && dto.getTunniste() == null ?
                new Oppiaine(dto.getTyyppi()) : // Uusi valinnainen aine, luodaan uusi tunniste
                new Oppiaine(dto.getTunniste());

        mapper.map(dto, oppiaine);
        if (dto.getOppimaarat() != null) {
            dto.getOppimaarat().forEach(o -> {
                Oppiaine om = new Oppiaine(o.getTunniste());
                mapper.map(o, om);
                oppiaine.addOppimaara(om);
            });
        }

        if (includeVlk && dto.getVuosiluokkakokonaisuudet() != null) {
            dto.getVuosiluokkakokonaisuudet()
                    .forEach(ovk -> oppiaine.addVuosiluokkaKokonaisuus(
                            mapper.map(ovk, Oppiaineenvuosiluokkakokonaisuus.class)));
        }

        return oppiaine;
    }

    public static VuosiluokkakokonaisuusDto fromEperusteet(
            PerusteVuosiluokkakokonaisuusDto dto) {
        VuosiluokkakokonaisuusDto vk = new VuosiluokkakokonaisuusDto(new Reference(dto.getTunniste().toString()));
        vk.setNimi(dto.getNimi());
        vk.setTunniste(Reference.of(dto.getTunniste()));
        if (dto.getLaajaalaisetOsaamiset() != null) {
            vk.setLaajaalaisetosaamiset(new HashSet<>());
            dto.getLaajaalaisetOsaamiset().forEach(lo -> {
                LaajaalainenosaaminenDto l = new LaajaalainenosaaminenDto();
                l.setLaajaalainenosaaminen(Reference.of(lo.getLaajaalainenOsaaminen().getTunniste()));
                vk.getLaajaalaisetosaamiset().add(l);
            });
        }
        return vk;
    }

    public static OppiaineDto oppimaaraFromEperusteet(
            PerusteOppiaineDto oa) {
        OppiaineDto dto = new OppiaineDto();
        dto.setTila(Tila.LUONNOS);

        dto.setNimi(oa.getNimi());
        dto.setTyyppi(OppiaineTyyppi.YHTEINEN);
        dto.setTunniste(oa.getTunniste());
        dto.setKoosteinen(oa.getKoosteinen());
        dto.setKoodiArvo(oa.getKoodiArvo());
        dto.setKoodiUri(oa.getKoodiUri());

        Set<OpetuksenKohdealueDto> kohdealueet
                = oa.getKohdealueet().stream()
                .map(OpsDtoMapper::fromEperusteet)
                .collect(Collectors.toSet());
        dto.setKohdealueet(kohdealueet);

        // Ollaan oppimäärässä, täällä ei pitäisi enää olla alioppimääriä
        assert (!oa.getKoosteinen());

        if (oa.getVuosiluokkakokonaisuudet() != null) {
            dto.setVuosiluokkakokonaisuudet(
                    oa.getVuosiluokkakokonaisuudet().stream()
                            .map(OpsDtoMapper::fromEperusteet)
                            .collect(Collectors.toSet()));
        }

        return dto;
    }

    public static OppiaineLaajaDto fromEperusteet(
            PerusteOppiaineDto oa) {
        OppiaineLaajaDto dto = new OppiaineLaajaDto();
        dto.setTila(Tila.LUONNOS);

        dto.setNimi(oa.getNimi());
        dto.setTyyppi(OppiaineTyyppi.YHTEINEN);
        dto.setKoosteinen(oa.getKoosteinen());
        dto.setTunniste(oa.getTunniste());
        dto.setKoodiArvo(oa.getKoodiArvo());
        dto.setKoodiUri(oa.getKoodiUri());

        dto.setKohdealueet(
                oa.getKohdealueet().stream()
                        .map(OpsDtoMapper::fromEperusteet)
                        .collect(Collectors.toSet()));

        if (oa.getOppimaarat() != null) {
            dto.setOppimaarat(
                    oa.getOppimaarat().stream()
                            .map(OpsDtoMapper::oppimaaraFromEperusteet)
                            .collect(Collectors.toSet()));
        }

        if (oa.getVuosiluokkakokonaisuudet() != null) {
            dto.setVuosiluokkakokonaisuudet(
                    oa.getVuosiluokkakokonaisuudet().stream()
                            .map(OpsDtoMapper::fromEperusteet)
                            .collect(Collectors.toSet()));
        }

        return dto;
    }

    public static OppiaineenVuosiluokkakokonaisuusDto fromEperusteet(PerusteOppiaineenVuosiluokkakokonaisuusDto ovk) {
        OppiaineenVuosiluokkakokonaisuusDto dto = new OppiaineenVuosiluokkakokonaisuusDto();
        dto.setTehtava(new TekstiosaDto());
        dto.setTyotavat(new TekstiosaDto());
        dto.setOhjaus(new TekstiosaDto());
        dto.setArviointi(new TekstiosaDto());

        dto.setVuosiluokkakokonaisuus(Reference.of(ovk.getVuosiluokkaKokonaisuus().getTunniste()));

        return dto;
    }

    public static OpetuksenKohdealueDto fromEperusteet(
            PerusteOpetuksenkohdealueDto dto) {
        return new OpetuksenKohdealueDto(dto.getNimi());
    }
}
