package fi.vm.sade.eperusteet.ylops.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString(of="tapahtuma")
@Getter
@AllArgsConstructor
public enum AikatauluTapahtuma {

    LUOMINEN("luominen"),
    TAVOITE("tavoite"),
    JULKAISU("julkaisu");

    private String tapahtuma;

    public String toString() {
        return tapahtuma;
    }
}
