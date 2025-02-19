package fi.vm.sade.eperusteet.ylops.domain.ops;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/**
 * Kommenttikahvaan kiinnitetty kommentti. Kommentit haetaan tekstisisältöjen relaationa löytyvästä ketjusta.
 */
@Entity
@Table(name = "kommentti_2019")
@Audited
public class Kommentti2019 extends AbstractAuditedReferenceableEntity {

    @Getter
    @Setter
    @Column(updatable = false)
    @NotNull
    private UUID tunniste;

    @Getter
    @Setter
    @Column(updatable = false)
    @NotNull
    private UUID thread;

    @Getter
    @Setter
    @Column(updatable = false)
    private UUID reply;

    @Getter
    @Setter
    @Column(length = 1024)
    @Size(max = 1024, message = "Kommentin maksimipituus on {max} merkkiä")
    private String sisalto;

}
