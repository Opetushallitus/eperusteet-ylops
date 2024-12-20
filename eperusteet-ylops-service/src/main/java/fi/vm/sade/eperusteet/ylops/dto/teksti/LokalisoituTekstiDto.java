package fi.vm.sade.eperusteet.ylops.dto.teksti;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import lombok.Data;
import lombok.Getter;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
public class LokalisoituTekstiDto {

    @Getter
    private final Long id;

    @Getter
    private UUID tunniste;

    @Getter
    private final Map<Kieli, String> tekstit;

    public static LokalisoituTekstiDto of(String value) {
        HashMap<Kieli, String> map = new HashMap<>();
        map.put(Kieli.FI, value);
        return new LokalisoituTekstiDto(null, null, map);
    }

    public static LokalisoituTekstiDto of(String value, Kieli kieli) {
        HashMap<Kieli, String> map = new HashMap<>();
        map.put(kieli, value);
        return new LokalisoituTekstiDto(null, null, map);
    }

    public LokalisoituTekstiDto(Long id, Map<Kieli, String> values) {
        this(id, null, values);
    }

    public LokalisoituTekstiDto(Long id, UUID tunniste, Map<Kieli, String> values) {
        this.id = id;
        this.tunniste = tunniste;
        this.tekstit = values == null ? null : new EnumMap<>(values);
    }

    @JsonCreator
    public LokalisoituTekstiDto(Map<String, String> values) {
        Long tmpId = null;
        EnumMap<Kieli, String> tmpValues = new EnumMap<>(Kieli.class);

        if (values != null) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                if ("_id".equals(entry.getKey())) {
                    tmpId = Long.valueOf(entry.getValue());
                } else if ("_tunniste".equals(entry.getKey())) {
                    this.tunniste = UUID.fromString(entry.getValue());
                } else {
                    Kieli k = Kieli.of(entry.getKey());
                    if (k != null) {
                        tmpValues.put(k, entry.getValue());
                    }
                }
            }
        }

        this.id = tmpId;
        this.tekstit = tmpValues;
    }

    @JsonValue
    public Map<String, String> asMap() {
        HashMap<String, String> map = new HashMap<>();
        if (id != null) {
            map.put("_id", id.toString());
        }
        if (tunniste != null) {
            map.put("_tunniste", tunniste.toString());
        }
        for (Map.Entry<Kieli, String> e : tekstit.entrySet()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        return map;
    }

    @JsonIgnore
    public String get(Kieli kieli) {
        return tekstit.get(kieli);
    }

    @JsonIgnore
    public String getOrDefault(Kieli kieli) {
        return tekstit.getOrDefault(
                kieli,
                tekstit.getOrDefault(
                        tekstit.keySet().stream().findAny().isPresent() ? tekstit.keySet().stream().findAny().get() : Kieli.FI,
                        ""));
    }

    @SuppressWarnings("DtoClassesNotContainEntities")
    public static <K> Map<K, Optional<LokalisoituTekstiDto>> ofOptionalMap(Map<K, Optional<LokalisoituTeksti>> map) {
        Map<K, Optional<LokalisoituTekstiDto>> result = new HashMap<>();
        for (Map.Entry<K, Optional<LokalisoituTeksti>> kv : map.entrySet()) {
            result.put(kv.getKey(), kv.getValue().map(teksti -> new LokalisoituTekstiDto(teksti.getId(), teksti.getTunniste(), teksti.getTeksti())));
        }
        return result;
    }

    public boolean hasKielet(Set<Kieli> kielet) {
        boolean hasSomething = false;
        Map<Kieli, String> mteksti = getTekstit();

        for (Kieli kieli : kielet) {
            String str = mteksti.get(kieli);
            if (str != null && !str.isEmpty()) {
                hasSomething = true;
                break;
            }
        }

        if (hasSomething) {
            for (Kieli kieli : kielet) {
                String sisalto = mteksti.get(kieli);
                if (sisalto == null || sisalto.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }
}
