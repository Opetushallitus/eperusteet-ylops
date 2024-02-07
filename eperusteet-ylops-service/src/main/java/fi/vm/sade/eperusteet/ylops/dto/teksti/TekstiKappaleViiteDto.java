package fi.vm.sade.eperusteet.ylops.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TekstiKappaleViiteDto {
    private Long id;

    @JsonProperty("_original")
    private Reference original;

    @JsonProperty("_tekstiKappale")
    private Reference tekstiKappaleRef;
    private TekstiKappaleDto tekstiKappale;
    private Omistussuhde omistussuhde;
    private boolean pakollinen;
    private boolean valmis;
    private Long perusteTekstikappaleId;
    private boolean naytaPerusteenTeksti = true;
    private boolean naytaPohjanTeksti = false;
    private boolean piilotettu = false;
    private boolean liite = false;

    public TekstiKappaleViiteDto(TekstiKappaleDto tekstiKappale) {
        this.tekstiKappale = tekstiKappale;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class Matala extends TekstiKappaleViiteDto {
        private List<Reference> lapset;

        public Matala(TekstiKappaleDto tekstiKappale) {
            super(tekstiKappale);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class Puu extends TekstiKappaleViiteDto {
        private List<Puu> lapset;
    }
}
