package fi.vm.sade.eperusteet.ylops.dto.ops;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpetussuunnitelmaJulkaistuQuery implements Serializable {
    private List<String> koulutustyypit = new ArrayList<>();
    private String nimi = "";
    private String kieli = "fi";
    private String perusteenDiaarinumero = "";
    private int sivu = 0;
    private int sivukoko = 10;
}
