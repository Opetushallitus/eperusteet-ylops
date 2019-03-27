package fi.vm.sade.eperusteet.ylops.dto.peruste;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

@Getter
@Setter
public class TiedoteQueryDto {
    private int sivu = 0;
    private int sivukoko = 25;
    private List<Kieli> kieli;
    private String nimi;
    private Long perusteId;
    private Boolean perusteeton;
    private Boolean julkinen; // Jos null, haetaan julkiset ja sisäiset
    private Boolean yleinen; // Jos halutaan esittää mm. etusivulla

    public String toRequestParams() {
        StringJoiner joiner = new StringJoiner("&");

        for (Field field : TiedoteQueryDto.class.getDeclaredFields()) {
            String name = field.getName();
            try {
                Object value = field.get(this);
                if (value instanceof Collection<?>){
                    for (Object element : (Collection<?>) value) {
                        if (!ObjectUtils.isEmpty(value)) {
                            joiner.add(name.concat("=").concat(element.toString()));
                        }
                    }
                } else {
                    if (!ObjectUtils.isEmpty(value)) {
                        joiner.add(name.concat("=").concat(value.toString()));
                    }
                }
            } catch (IllegalAccessException e) {
                // Ignore
            }
        }

        return joiner.toString();
    }
}
