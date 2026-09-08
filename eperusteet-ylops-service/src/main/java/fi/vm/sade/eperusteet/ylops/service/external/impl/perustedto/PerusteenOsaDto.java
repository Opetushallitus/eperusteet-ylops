package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import fi.vm.sade.eperusteet.utils.dto.peruste.lops2019.tutkinnonrakenne.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class PerusteenOsaDto {
    private Long id;
    private Date luotu;
    private Date muokattu;
    private String muokkaaja;
    private String muokkaajanNimi;
    private PerusteenLokalisoituTekstiDto nimi;
    private String tila;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tunniste;
    private KoodiDto koodi;

    /**
     * Perusteen sisältöpuun osa kaikkine tietoineen. Konkreettinen tyyppi ratkaistaan
     * osanTyyppi-kentästä. Osat, joita ylops ei mallinna omana tyyppinään, luetaan
     * tekstikappaleina, mutta osanTyyppi säilyy perusteen antamana.
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonTypeInfo(
            use = Id.NAME,
            include = As.EXISTING_PROPERTY,
            property = "osanTyyppi",
            visible = true,
            defaultImpl = TekstiKappaleDto.class)
    @JsonSubTypes(value = {
            @JsonSubTypes.Type(value = TekstiKappaleDto.class),
            @JsonSubTypes.Type(value = TpoPerusteenTaiteenalaDto.class)
    })
    public abstract static class Laaja extends PerusteenOsaDto {
        private String osanTyyppi;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Suppea extends PerusteenOsaDto {
        private String osanTyyppi;
    }
}
