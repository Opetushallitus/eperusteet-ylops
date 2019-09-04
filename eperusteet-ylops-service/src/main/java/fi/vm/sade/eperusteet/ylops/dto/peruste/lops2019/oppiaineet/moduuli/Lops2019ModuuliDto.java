package fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class Lops2019ModuuliDto extends Lops2019ModuuliBaseDto {
    private LokalisoituTekstiDto kuvaus;
    private BigDecimal laajuus;
    private Lops2019ModuuliTavoiteDto tavoitteet;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Lops2019ModuuliSisaltoDto> sisallot;

    private Reference oppiaine;
}
