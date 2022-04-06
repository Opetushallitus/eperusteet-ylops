/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.ylops.test.util;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetuksenTavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineSuppeaDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import fi.vm.sade.eperusteet.ylops.service.mocks.EperusteetServiceMock;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

/**
 * @author mikkom
 */
public abstract class TestUtils {
    public static LokalisoituTeksti lokalisoituTekstiOf(Kieli kieli, String teksti) {
        return LokalisoituTeksti.of(Collections.singletonMap(kieli, teksti));
    }

    static Long uniikki = (long) 0;

    static public String uniikkiString() {
        return "uniikki" + (++uniikki).toString();
    }

    static public Long uniikkiId() {
        return ++uniikki;
    }

    public static LokalisoituTekstiDto lt(String teksti) {
        return new LokalisoituTekstiDto(null, Collections.singletonMap(Kieli.FI, teksti));
    }

    public static Optional<LokalisoituTekstiDto> olt(String teksti) {
        return Optional.of(lt(teksti));
    }

    public static OppiaineDto createOppiaine(String nimi){
        return createOppiaine(nimi, false);
    }

    public static OppiaineDto createKoosteinenOppiaine(String nimi){
        return createOppiaine(nimi, true);
    }

    private static OppiaineDto createOppiaine(String nimi, boolean isKoosteinen) {
        OppiaineDto oppiaineDto = new OppiaineDto();
        oppiaineDto.setTyyppi(OppiaineTyyppi.YHTEINEN);
        oppiaineDto.setNimi(lt(nimi));
        oppiaineDto.setKoodiUri("koodikoodi");
        oppiaineDto.setTunniste(UUID.randomUUID());
        oppiaineDto.setKoosteinen(isKoosteinen);
        oppiaineDto.setKoodiArvo("VK");
        return oppiaineDto;
    }

    public static OppiaineSuppeaDto createOppimaara(String nimi) {
        OppiaineSuppeaDto oppimaara = new OppiaineSuppeaDto();
        oppimaara.setTyyppi(OppiaineTyyppi.YHTEINEN);
        oppimaara.setNimi(lt(nimi));
        oppimaara.setKoodiUri("oppimaarakoodi");
        oppimaara.setTunniste(UUID.randomUUID());
        oppimaara.setKoosteinen(false);
        return oppimaara;
    }

    public static OpetuksenTavoiteDto createTavoite() {
        OpetuksenTavoiteDto tavoite = new OpetuksenTavoiteDto();
        tavoite.setTunniste(UUID.randomUUID());
        return tavoite;
    }

    public static TekstiosaDto createTekstiosa(String nimi, String otsikko) {
        TekstiosaDto result = new TekstiosaDto();
        result.setTeksti(Optional.of(lt(nimi)));
        result.setOtsikko(Optional.of(lt(otsikko)));
        return result;
    }

    public static TekstiKappaleDto createTekstiKappale() {
        TekstiKappaleDto tk = new TekstiKappaleDto();
        tk.setTeksti(lt(uniikkiString()));
        return tk;
    }

    public static TekstiKappaleViiteDto.Matala createTekstiKappaleViite() {
        TekstiKappaleViiteDto.Matala tkv = new TekstiKappaleViiteDto.Matala();
        tkv.setTekstiKappale(createTekstiKappale());
        return tkv;
    }

    public static OpetussuunnitelmaLuontiDto createOps() {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setPerusteenDiaarinumero(EperusteetServiceMock.DIAARINUMERO);
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.15252345624572462");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        return ops;
    }

    public static OpetussuunnitelmaLuontiDto createOpsPohja() {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setPerusteenDiaarinumero(EperusteetServiceMock.DIAARINUMERO);
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTyyppi(Tyyppi.POHJA);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        return ops;
    }
}
