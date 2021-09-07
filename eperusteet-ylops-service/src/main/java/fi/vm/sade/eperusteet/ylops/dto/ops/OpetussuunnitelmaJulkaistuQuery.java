package fi.vm.sade.eperusteet.ylops.dto.ops;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpetussuunnitelmaJulkaistuQuery implements Serializable {
    private String nimi = "";
    private String kieli = "fi";
    private String perusteenDiaarinumero = "";
    private int sivu = 0;
    private int sivukoko = 10;
}
