package fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.domain.Validable;
import fi.vm.sade.eperusteet.ylops.domain.ValidationCategory;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidointiDto<T extends ValidointiDto> {
    private DtoMapper mapper;

    @Getter
    @Setter
    private Map<String, List<ValidoinninKohdeDto>> validoinnit = new HashMap<>();

    @Getter
    @Setter
    private boolean valid = true;

    @JsonProperty("kaikkiValidoinnit")
    long getKaikkiValidoinnit() {
        return this.validoinnit.values().stream()
                .mapToInt(List::size)
                .sum() + kaikki();
    }

    @JsonProperty("onnistuneetValidoinnit")
    long getOnnistuneetValidoinnit() {
        return this.validoinnit.values().stream()
                .flatMap(Collection::stream)
                .filter(v -> v.isFailed() && v.isFatal())
                .count() + onnistuneet();
    }

    @JsonIgnore
    protected int kaikki() {
        return 0;
    }

    @JsonIgnore
    protected int onnistuneet() {
        return 0;
    }

    public ValidointiDto(DtoMapper mapper) {
        this.mapper = mapper;
    }

    private T entry(boolean failed, Long id, ValidationCategory kategoria, LokalisoituTekstiDto nimi, String kuvaus, boolean isFatal, Map<String, Object> meta) {
        if (failed && isFatal) {
            this.valid = false;
        }

        String category = "validation-category-" + kategoria;
        if (!validoinnit.containsKey(category)) {
            validoinnit.put(category, new ArrayList<>());
        }

        validoinnit.get(category).add(new ValidoinninKohdeDto(
            id,
            nimi,
            kuvaus,
            kategoria,
            failed,
            isFatal,
            meta));
        return (T)this;
    }

    private T entry(boolean failed, Validable validable, String kuvaus, boolean isFatal, Map<String, Object> meta) {
        if (failed && isFatal) {
            this.valid = false;
        }

        String category = "validation-category-" + validable.category();
        if (!validoinnit.containsKey(category)) {
            validoinnit.put(category, new ArrayList<>());
        }

        LokalisoituTekstiDto nimi = null;
        if (mapper != null) {
            nimi = mapper.map(validable.getNimi(), LokalisoituTekstiDto.class);
        }
        validoinnit.get(category).add(new ValidoinninKohdeDto(
            (Long)validable.getId(),
            nimi,
            kuvaus,
            validable.category(),
            failed,
            isFatal,
            meta));
        return (T)this;
    }

    public T virhe(ValidationCategory kategoria, String kuvaus, Long id, LokalisoituTekstiDto nimi, boolean failed, Map<String, Object> meta) {
        return entry(failed, id, kategoria, nimi, kuvaus, true, meta);
    }

    public T varoitus(ValidationCategory kategoria, String kuvaus, Long id, LokalisoituTekstiDto nimi, boolean failed, Map<String, Object> meta) {
        return entry(failed, id, kategoria, nimi, kuvaus, false, meta);
    }

    public T virhe(ValidationCategory kategoria, String kuvaus, Long id, LokalisoituTekstiDto nimi, boolean failed) {
        return entry(failed, id, kategoria, nimi, kuvaus, true, null);
    }

    public T varoitus(ValidationCategory kategoria, String kuvaus, Long id, LokalisoituTekstiDto nimi, boolean failed) {
        return entry(failed, id, kategoria, nimi, kuvaus, false, null);
    }

    public T virhe(String kuvaus, Validable validable, boolean failed) {
        return entry(failed, validable, kuvaus, true, null);
    }

    public T varoitus(String kuvaus, Validable validable, boolean failed) {
        return entry(failed, validable, kuvaus, false, null);
    }

    public Map<String, List<ValidoinninKohdeDto>> getValidoinnit() {
        return validoinnit;
    }

    public boolean isValid() {
        return this.valid;
    }
}
