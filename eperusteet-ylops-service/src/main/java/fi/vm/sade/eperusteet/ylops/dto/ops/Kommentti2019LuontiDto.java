package fi.vm.sade.eperusteet.ylops.dto.ops;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Kommentti2019LuontiDto {
    private String sisalto;
    private UUID parent;
}
