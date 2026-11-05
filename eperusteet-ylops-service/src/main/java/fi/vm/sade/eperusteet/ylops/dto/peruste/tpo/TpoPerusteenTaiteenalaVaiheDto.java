package fi.vm.sade.eperusteet.ylops.dto.peruste.tpo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fi.vm.sade.eperusteet.ylops.dto.peruste.aipe.PerusteKevytTekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenOsaDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonTypeName("taiteenalavaihe")
public class TpoPerusteenTaiteenalaVaiheDto extends PerusteenOsaDto.Laaja {
    private Integer laajuus;
    private PerusteenLokalisoituTekstiDto kuvaus;
    private List<PerusteTavoiteAlueDto> tavoitealueet = new ArrayList<>();
    private List<PerusteKevytTekstiKappaleDto> vapaatTekstit = new ArrayList<>();
}
