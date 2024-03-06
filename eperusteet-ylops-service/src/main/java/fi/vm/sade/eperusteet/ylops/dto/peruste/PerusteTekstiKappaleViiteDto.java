package fi.vm.sade.eperusteet.ylops.dto.peruste;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PerusteTekstiKappaleViiteDto extends PerusteTekstiKappaleViiteMatalaDto {
    private List<PerusteTekstiKappaleViiteDto> lapset = new ArrayList<>();
}
