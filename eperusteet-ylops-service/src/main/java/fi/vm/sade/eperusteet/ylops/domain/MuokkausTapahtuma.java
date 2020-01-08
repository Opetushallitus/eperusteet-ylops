package fi.vm.sade.eperusteet.ylops.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString(of="tapahtuma")
@Getter
@AllArgsConstructor
public enum MuokkausTapahtuma {
    LUONTI("luonti"),
    PAIVITYS("paivitys"),
    POISTO("poisto");

    private String tapahtuma;

    public String toString() {
        return tapahtuma;
    }
}
