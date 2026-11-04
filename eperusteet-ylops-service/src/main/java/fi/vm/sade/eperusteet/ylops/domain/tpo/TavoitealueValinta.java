package fi.vm.sade.eperusteet.ylops.domain.tpo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TavoitealueValinta implements Serializable {

    @Column(name = "tavoitealue_id")
    private Long tavoitealueId;

    @Column(name = "koodi_uri")
    private String koodiUri;
}
