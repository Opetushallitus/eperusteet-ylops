package fi.vm.sade.eperusteet.ylops.domain.lukio;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.ylops.domain.utils.KoodistoUtils;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml.WhitelistType;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioKurssiParentDto;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copyable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Audited
@Table(name = "lukiokurssi", schema = "public")
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "lukioKurssiParentDto",
                classes = {
                        @ConstructorResult(
                                targetClass = LukioKurssiParentDto.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "parent_id", type = Long.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "parentViewByOps",
                query = "select oa_lk.kurssi_id as id, findParentKurssi(?1, oa_lk.kurssi_id) as parent_id" +
                        "   from oppiaine_lukiokurssi oa_lk" +
                        " where oa_lk.opetussuunnitelma_id = ?1" +
                        " group by oa_lk.kurssi_id order by oa_lk.kurssi_id",
                resultSetMapping = "lukioKurssiParentDto"
        )
})
public class Lukiokurssi extends Kurssi implements Copyable<Lukiokurssi> {

    @Getter
    @Setter
    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LukiokurssiTyyppi tyyppi;

    @Getter
    @Setter
    @Column(name = "laajuus", nullable = true,
            precision = 4, scale = 2, columnDefinition = "DECIMAL(4,2)")
    private BigDecimal laajuus = BigDecimal.ONE;

    @Setter
    @ValidHtml(whitelist = WhitelistType.MINIMAL)
    @JoinColumn(name = "lokalisoitava_koodi_id", nullable = true)
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti lokalisoituKoodi;

    @Getter
    @Setter
    @Valid
    @JoinColumn(name = "tavoitteet_id", nullable = true)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa tavoitteet;

    @Getter
    @Setter
    @Valid
    @JoinColumn(name = "keskeinen_sisalto_id", nullable = true)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa keskeinenSisalto;

    @Getter
    @Setter
    @Valid
    @JoinColumn(name = "tavoitteet_ja_keskeinen_sisalto_id", nullable = true)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa tavoitteetJaKeskeinenSisalto;

    @Getter
    @Audited
    @OneToMany(mappedBy = "kurssi", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OppiaineLukiokurssi> oppiaineet = new HashSet<>(0);

    public LokalisoituTeksti getLokalisoituKoodi() {
        if (this.oppiaineet.size() > 0) {
            OppiaineLukiokurssi oa = this.oppiaineet.iterator().next();
            String kieliKoodiUri = oa.getOppiaine().getKieliKoodiUri();
            if (kieliKoodiUri != null && this.koodiArvo != null && kieliKoodiUri.startsWith("lukiokielitarjonta")) {
                String kurssiosa = this.koodiArvo.substring(2);
                Map<Kieli, String> kielet = new HashMap<>();
                kielet.put(Kieli.FI, KoodistoUtils.getVieraskielikoodi(kieliKoodiUri, Kieli.FI) + kurssiosa);
                kielet.put(Kieli.SV, KoodistoUtils.getVieraskielikoodi(kieliKoodiUri, Kieli.SV) + kurssiosa);
                LokalisoituTeksti teksti = LokalisoituTeksti.of(kielet);
                return teksti;
            }
        }
        return lokalisoituKoodi;
    }

    public Lukiokurssi() {
        super(UUID.randomUUID());
    }

    public Lukiokurssi(UUID tunniste) {
        super(tunniste);
    }

    public Lukiokurssi copy() {
        return copyInto(new Lukiokurssi(this.getTunniste()));
    }

    public Lukiokurssi copyInto(Lukiokurssi lukiokurssi) {
        super.copyInto(lukiokurssi);
        lukiokurssi.setTyyppi(this.tyyppi);
        lukiokurssi.setLaajuus(this.laajuus);
        lukiokurssi.setLokalisoituKoodi(this.lokalisoituKoodi);
        lukiokurssi.setTavoitteet(Tekstiosa.copyOf(this.tavoitteet));
        lukiokurssi.setKeskeinenSisalto(Tekstiosa.copyOf(this.keskeinenSisalto));
        lukiokurssi.setTavoitteetJaKeskeinenSisalto(Tekstiosa.copyOf(this.tavoitteetJaKeskeinenSisalto));
        return lukiokurssi;
    }

}
