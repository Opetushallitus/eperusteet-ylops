package fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Lops2019ModuuliSisaltoDto {
    private LokalisoituTekstiDto kohde;
    private List<LokalisoituTekstiDto> sisallot = new ArrayList<>();
}
