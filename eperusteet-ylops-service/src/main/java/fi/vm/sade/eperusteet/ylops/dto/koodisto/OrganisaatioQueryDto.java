package fi.vm.sade.eperusteet.ylops.dto.koodisto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganisaatioQueryDto {
    Set<String> kunta = new HashSet<>();
    Set<String> oppilaitostyyppi = Stream.of("11", "12", "15", "19", "21", "63", "64").collect(Collectors.toSet());
}
