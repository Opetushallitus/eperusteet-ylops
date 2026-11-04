package fi.vm.sade.eperusteet.ylops.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoSisaltoViiteDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TPOOpetuksenSisaltoDto {
    private TpoSisaltoViiteDto sisalto;

    @JsonIgnore
    public List<TpoPerusteenTaiteenalaDto> getTaiteenalat() {
        return sisalto != null ? sisalto.getTaiteenalat() : new ArrayList<>();
    }

    @JsonIgnore
    public TekstiKappaleViiteDto getTekstiKappaleViiteSisalto() {
        return sisalto != null ? sisalto.getTekstiKappaleViiteSisalto() : null;
    }

    /**
     * Tekstikappaleet ja taiteenalat vaiheineen samassa puussa.
     * Taiteenalat liitetään juuren lapsiksi tekstikappaleiden jälkeen.
     */
    @JsonIgnore
    public TekstiKappaleViiteDto getSisallotTekstikappaleViitteina() {
        if (sisalto == null) {
            return null;
        }

        TekstiKappaleViiteDto tekstit = sisalto.getTekstiKappaleViiteSisalto();
        List<TekstiKappaleViiteDto> lapset = new ArrayList<>();
        if (tekstit.getLapset() != null) {
            lapset.addAll(tekstit.getLapset());
        }
        lapset.addAll(sisalto.getTaiteenalat().stream()
                .map(TPOOpetuksenSisaltoDto::taiteenalaTekstiKappaleViitteeksi)
                .collect(Collectors.toList()));
        return new TekstiKappaleViiteDto(tekstit.getId(), tekstit.getTekstiKappale(), lapset);
    }

    private static TekstiKappaleViiteDto taiteenalaTekstiKappaleViitteeksi(TpoPerusteenTaiteenalaDto taiteenala) {
        List<TekstiKappaleViiteDto> vaiheet = taiteenala.getVaiheet() == null
                ? new ArrayList<>()
                : taiteenala.getVaiheet().stream()
                .map(TPOOpetuksenSisaltoDto::vaiheTekstiKappaleViitteeksi)
                .collect(Collectors.toList());
        return new TekstiKappaleViiteDto(
                taiteenala.getId(),
                tekstiKappale(taiteenala.getId(), taiteenala.getNimi(), taiteenala.getTeksti()),
                vaiheet);
    }

    private static TekstiKappaleViiteDto vaiheTekstiKappaleViitteeksi(TpoPerusteenTaiteenalaVaiheDto vaihe) {
        return new TekstiKappaleViiteDto(
                vaihe.getId(),
                tekstiKappale(vaihe.getId(), vaihe.getNimi(), vaihe.getKuvaus()),
                new ArrayList<>());
    }

    private static TekstiKappaleDto tekstiKappale(Long id, PerusteenLokalisoituTekstiDto nimi, PerusteenLokalisoituTekstiDto teksti) {
        TekstiKappaleDto kappale = new TekstiKappaleDto();
        kappale.setId(id);
        kappale.setNimi(nimi);
        kappale.setTeksti(teksti);
        kappale.setOsanTyyppi("tekstikappale");
        return kappale;
    }
}
