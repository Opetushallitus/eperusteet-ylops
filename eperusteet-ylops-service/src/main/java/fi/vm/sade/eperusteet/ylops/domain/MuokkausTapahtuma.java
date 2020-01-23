package fi.vm.sade.eperusteet.ylops.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MuokkausTapahtuma {
    LUONTI("luonti"),
    PAIVITYS("paivitys"),
    PALAUTUS("palautus"),
    POISTO("poisto");

    private String tapahtuma;

    @Override
    public String toString() {
        return tapahtuma;
    }
}
