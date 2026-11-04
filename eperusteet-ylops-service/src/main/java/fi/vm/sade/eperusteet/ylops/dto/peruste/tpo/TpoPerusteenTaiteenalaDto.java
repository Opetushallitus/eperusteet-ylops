package fi.vm.sade.eperusteet.ylops.dto.peruste.tpo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@JsonTypeName("taiteenala")
public class TpoPerusteenTaiteenalaDto extends PerusteenOsaDto.Laaja {
    private PerusteenLokalisoituTekstiDto teksti;
    private PerusteKevytTekstiKappaleDto kasvatus;
    private PerusteKevytTekstiKappaleDto yhteisetOpinnot;
    private PerusteKevytTekstiKappaleDto teemaopinnot;
    private PerusteKevytTekstiKappaleDto aikuistenOpetus;
    private PerusteKevytTekstiKappaleDto tyotavatOpetuksessa;
    private PerusteKevytTekstiKappaleDto oppimisenArviointiOpetuksessa;
    private List<PerusteKevytTekstiKappaleDto> vapaatTekstit = new ArrayList<>();

    @JsonIgnore
    private List<TpoPerusteenTaiteenalaVaiheDto> vaiheet = new ArrayList<>();
}
