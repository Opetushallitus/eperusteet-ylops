package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KoulutusDto {
    private PerusteenLokalisoituTekstiDto nimi;
    private String koulutuskoodiArvo;
    private String koulutuskoodiUri;
    private String koulutusalakoodi;
    private String opintoalakoodi;
}
