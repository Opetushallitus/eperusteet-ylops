package fi.vm.sade.eperusteet.ylops.dto.aipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIPESisaltoDto {
    private Long id;
    private List<AIPEVaiheKevytDto> vaiheet = new ArrayList<>();
}
