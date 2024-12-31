package fi.vm.sade.eperusteet.ylops.domain.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ops_vuosiluokkakokonaisuus_lisatieto")
public class OpsVuosiluokkakokonaisuusLisatieto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Getter
    @Setter
    @ElementCollection
    @Column(name = "piilotettu_oppiaine_id")
    @CollectionTable(name = "ops_vuosiluokkakokonaisuus_lisatieto_piilotetut_oppiaineet")
    private Set<Long> piilotetutOppiaineet = new HashSet<>();
}
