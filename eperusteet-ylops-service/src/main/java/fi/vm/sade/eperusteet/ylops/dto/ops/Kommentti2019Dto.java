package fi.vm.sade.eperusteet.ylops.dto.ops;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Kommentti2019Dto extends Kommentti2019LuontiDto {
    private UUID uuid;
    private Long opsId;
    private Date luotu;
    private Date muokattu;
    private String luoja;
    private List<Kommentti2019Dto> kommentit = new ArrayList<>();
}
