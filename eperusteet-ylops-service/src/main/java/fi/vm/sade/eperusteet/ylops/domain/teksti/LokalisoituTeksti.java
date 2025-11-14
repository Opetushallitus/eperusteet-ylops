package fi.vm.sade.eperusteet.ylops.domain.teksti;

import fi.vm.sade.eperusteet.ylops.domain.ops.KommenttiKahva;
import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
@Table(name = "lokalisoituteksti")
public class LokalisoituTeksti implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @Getter
    @Column(updatable = false)
    private UUID tunniste;

    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @Immutable
    @CollectionTable(name = "lokalisoituteksti_teksti")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Teksti> teksti;

    @Getter
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<KommenttiKahva> ketjut = new HashSet<>();

    public void setKetjut(Set<KommenttiKahva> ketjut) {
        if (ketjut != null) {
            this.ketjut = ketjut;
            for (KommenttiKahva kahva : ketjut) {
                kahva.setTeksti(this);
            }
        }
    }

    protected LokalisoituTeksti() {
    }

    private LokalisoituTeksti(Set<Teksti> tekstit, UUID tunniste) {
        this.teksti = tekstit;
        this.tunniste = tunniste != null ? tunniste : UUID.randomUUID();
    }

    public Long getId() {
        return id;
    }

    public Map<Kieli, String> getTeksti() {
        EnumMap<Kieli, String> map = new EnumMap<>(Kieli.class);
        for (Teksti t : this.teksti) {
            map.put(t.getKieli(), t.getTeksti());
        }
        return map;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.teksti);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof LokalisoituTeksti) {
            final LokalisoituTeksti other = (LokalisoituTeksti) obj;
            return Objects.equals(this.teksti, other.teksti);
        }
        return false;
    }

    public static LokalisoituTeksti of(Map<Kieli, String> tekstit, UUID tunniste) {
        if (tekstit == null) {
            return null;
        }
        HashSet<Teksti> tmp = new HashSet<>(tekstit.size());
        for (Map.Entry<Kieli, String> e : tekstit.entrySet()) {
            if (e.getValue() != null) {
                String v = Normalizer.normalize(e.getValue().trim(), Normalizer.Form.NFC);
                if (!v.isEmpty()) {
                    tmp.add(new Teksti(e.getKey(), v));
                }
            }
        }
        if (tmp.isEmpty()) {
            return null;
        }
        return new LokalisoituTeksti(tmp, tunniste);
    }

    public static LokalisoituTeksti concat(Object... objs) {
        HashSet<Teksti> tekstit = new HashSet<>();
        for (Kieli kieli : Kieli.values()) {
            StringBuilder builder = new StringBuilder();

            for (Object obj : objs) {
                if (obj instanceof LokalisoituTeksti) {
                    LokalisoituTeksti lt1 = (LokalisoituTeksti) obj;
                    if (lt1.getTeksti() != null && lt1.getTeksti().get(kieli) != null) {
                        builder.append(lt1.getTeksti().get(kieli));
                    }
                } else if (obj instanceof String) {
                    String s = (String) obj;
                    builder.append(s);
                }
            }

            tekstit.add(new Teksti(kieli, builder.toString()));
        }

        return new LokalisoituTeksti(tekstit, null);
    }

    public static class LokalisoituTekstiBuilder {

    }

    public static LokalisoituTeksti of(Map<Kieli, String> tekstit) {
        return of(tekstit, null);
    }

    public static LokalisoituTeksti of(Kieli kieli, String teksti) {
        return of(Collections.singletonMap(kieli, teksti));
    }

    @Override
    public String toString() {
        Map<Kieli, String> tekstit = getTeksti();
        if (tekstit.isEmpty()) {
            return "";
        }
        String fi = tekstit.get(Kieli.FI);
        if (fi != null) {
            return fi;
        }
        return tekstit.entrySet().iterator().next().getValue();
    }

    public boolean hasKielet(Set<Kieli> kielet) {
        if (!getTeksti().isEmpty()) {
            for (Kieli kieli : kielet) {
                String sisalto = getTeksti().get(kieli);
                if (sisalto == null || sisalto.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    public Optional<String> firstByKieliOrder() {
        Map<Kieli, String> map = getTeksti();
        for (Kieli k : Kieli.values()) {
            if (map.containsKey(k) && map.get(k).trim().length() > 0) {
                return Optional.of(map.get(k));
            }
        }
        return Optional.empty();
    }
}
