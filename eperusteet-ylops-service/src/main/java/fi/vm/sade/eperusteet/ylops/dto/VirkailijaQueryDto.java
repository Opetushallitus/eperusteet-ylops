package fi.vm.sade.eperusteet.ylops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirkailijaQueryDto {
    private Set<String> oid = new HashSet<>();
}
