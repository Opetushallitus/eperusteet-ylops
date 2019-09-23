package fi.vm.sade.eperusteet.ylops.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class VirkailijaQueryDto {
    private Set<String> oid = new HashSet<>();
}
