package fi.vm.sade.eperusteet.ylops.domain.ops;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "kommentti_2019")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Kommentti2019 {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @Column(updatable = false)
    private UUID uuid;

    @Getter
    @Setter
    @Column(updatable = false)
    private UUID parent;

    @Getter
    @Setter
    @Column(length = 1024)
    @Size(max = 1024, message = "Kommentin maksimipituus on {max} merkkiä")
    private String sisalto;

    @Getter
    @Setter
    @Column(updatable = false)
    private Long opsId;

    @Column(updatable = false)
    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;

    @Column
    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date muokattu;

    @Getter
    @Setter
    @Column(updatable = false)
    private String luoja;

    @Getter
    @Setter
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private List<Kommentti2019> kommentit = new ArrayList<>();

}
