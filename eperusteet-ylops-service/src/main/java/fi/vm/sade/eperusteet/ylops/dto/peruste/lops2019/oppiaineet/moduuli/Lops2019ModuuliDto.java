package fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli;

import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Lops2019ModuuliDto extends Lops2019ModuuliBaseDto {
    private LokalisoituTekstiDto kuvaus;
    private BigDecimal laajuus;
    private Lops2019ModuuliTavoiteDto tavoitteet;
    private List<Lops2019ModuuliSisaltoDto> sisallot = new ArrayList<>();
    private Reference oppiaine;
}
