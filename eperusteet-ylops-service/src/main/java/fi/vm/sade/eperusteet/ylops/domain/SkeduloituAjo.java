package fi.vm.sade.eperusteet.ylops.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
@Setter
@Entity
@Table(name = "skeduloitu_ajo")
@AllArgsConstructor
@NoArgsConstructor
public class SkeduloituAjo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "nimi", nullable = false, unique = true, updatable = false)
    private String nimi;

    @Enumerated(EnumType.STRING)
    @NotNull
    private SkeduloituAjoStatus status = SkeduloituAjoStatus.PYSAYTETTY;

    @Column(name = "viimeisin_ajo_kaynnistys")
    private Date viimeisinAjoKaynnistys;

    @Column(name = "viimeisin_ajo_lopetus")
    private Date viimeisinAjoLopetus;

    private boolean kaytossa = true;

}
