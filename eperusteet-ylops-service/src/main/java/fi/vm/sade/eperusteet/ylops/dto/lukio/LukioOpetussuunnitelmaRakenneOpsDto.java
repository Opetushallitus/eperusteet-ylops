package fi.vm.sade.eperusteet.ylops.dto.lukio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LukioOpetussuunnitelmaRakenneOpsDto implements Serializable {
    @Setter
    private boolean root;
    @Setter
    private Date muokattu;
    @Setter
    private Long opsId;
    @Setter
    private List<LukioOppiaineRakenneListausDto> oppiaineet = new ArrayList<>();
    @Setter
    private List<LukioOppiaineRakenneListausDto> pohjanTarjonta = new ArrayList<>();
}
