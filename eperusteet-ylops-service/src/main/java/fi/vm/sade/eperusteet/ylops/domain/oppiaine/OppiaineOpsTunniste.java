package fi.vm.sade.eperusteet.ylops.domain.oppiaine;

import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
public class OppiaineOpsTunniste {
    private final UUID tunniste;
    private final String kieliKoodiArvo;
    private final LokalisoituTeksti kieli;

    public OppiaineOpsTunniste(UUID tunniste, String kieliKoodiArvo, LokalisoituTeksti kieli) {
        this.tunniste = tunniste;
        this.kieliKoodiArvo = kieliKoodiArvo;
        this.kieli = kieli;
    }

    @Override
    public int hashCode() {
        int result = tunniste.hashCode();
        result = 31 * result + (kieliKoodiArvo != null ? kieliKoodiArvo.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OppiaineOpsTunniste)) return false;

        OppiaineOpsTunniste that = (OppiaineOpsTunniste) o;

        if (!tunniste.equals(that.tunniste)) return false;
        if (kieliKoodiArvo == null) {
            return that.kieliKoodiArvo == null;
        }
        if (!kieliKoodiArvo.equals(that.kieliKoodiArvo)) {
            return false;
        }
        if ("KX".equals(kieliKoodiArvo)) {
            return Objects.equals(kieli, that.kieli);
        }
        return true;
    }
}
