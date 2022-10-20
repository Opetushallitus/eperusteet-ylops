package fi.vm.sade.eperusteet.ylops.dto.ops;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpetussuunnitelmaJulkaistuQuery implements Serializable {
    private String nimi = "";
    private String kieli = "fi";
    private String perusteenDiaarinumero = "";
    private int sivu = 0;
    private int sivukoko = 10;
}
