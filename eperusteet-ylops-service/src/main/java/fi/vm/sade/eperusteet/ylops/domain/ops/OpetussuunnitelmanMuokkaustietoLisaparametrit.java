package fi.vm.sade.eperusteet.ylops.domain.ops;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class OpetussuunnitelmanMuokkaustietoLisaparametrit implements Serializable {

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private NavigationType kohde;

    @Column(name = "kohde_id")
    @NotNull
    private Long kohdeId;

}
