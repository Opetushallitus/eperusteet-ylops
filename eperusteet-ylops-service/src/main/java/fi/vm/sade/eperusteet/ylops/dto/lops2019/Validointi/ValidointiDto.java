package fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi;

import fi.vm.sade.eperusteet.ylops.domain.Validable;
import fi.vm.sade.eperusteet.ylops.domain.ValidationCategory;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class ValidointiDto<T extends ValidointiDto> {
    private Map<String, List<ValidoinninKohdeDto>> validoinnit = new HashMap<>();
    private DtoMapper mapper;

    public ValidointiDto(DtoMapper mapper) {
        this.mapper = mapper;
    }

    private T entry(boolean failed, Long id, ValidationCategory kategoria, LokalisoituTekstiDto nimi, String kuvaus, boolean isFatal) {
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
            isFatal));
        return (T)this;
    }

    private T entry(boolean failed, Validable validable, String kuvaus, boolean isFatal) {
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
            isFatal));
        return (T)this;
    }

    public T virhe(ValidationCategory kategoria, String kuvaus, Long id, LokalisoituTekstiDto nimi, boolean failed) {
        return entry(failed, id, kategoria, nimi, kuvaus, true);
    }

    public T varoitus(ValidationCategory kategoria, String kuvaus, Long id, LokalisoituTekstiDto nimi, boolean failed) {
        return entry(failed, id, kategoria, nimi, kuvaus, false);
    }

    public T virhe(String kuvaus, Validable validable, boolean failed) {
        return entry(failed, validable, kuvaus, true);
    }

    public T varoitus(String kuvaus, Validable validable, boolean failed) {
        return entry(failed, validable, kuvaus, false);
    }

    public Map<String, List<ValidoinninKohdeDto>> getValidoinnit() {
        return validoinnit;
    }
}
