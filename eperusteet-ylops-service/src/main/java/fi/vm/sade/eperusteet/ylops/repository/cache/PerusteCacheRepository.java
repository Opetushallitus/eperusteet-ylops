package fi.vm.sade.eperusteet.ylops.repository.cache;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface PerusteCacheRepository extends JpaRepository<PerusteCache, Long> {
    String NEWEST_BY_AIKALEIMA = "c.aikaleima = (" +
            "   select max(c2.aikaleima) from PerusteCache c2 where c2.perusteId = c.perusteId" +
            ")";

    @Query("select c from PerusteCache c left join c.nimi nimi left join nimi.teksti fin on fin.kieli = 'FI' " +
            " where " + NEWEST_BY_AIKALEIMA +
            " order by fin.teksti, c.diaarinumero, c.aikaleima")
    List<PerusteCache> findNewestEntrie();

    @Query("select c from PerusteCache c left join c.nimi nimi left join nimi.teksti fin on fin.kieli = 'FI' " +
            " where c.koulutustyyppi in (?1) and " + NEWEST_BY_AIKALEIMA +
            " order by fin.teksti, c.diaarinumero, c.aikaleima")
    List<PerusteCache> findNewestEntrieByKoulutustyyppis(Set<KoulutusTyyppi> tyypit);

    @Query("select c from PerusteCache c left join c.nimi nimi left join nimi.teksti fin on fin.kieli = 'FI' " +
            " where c.diaarinumero not in (?2) and c.koulutustyyppi in (?1) and " + NEWEST_BY_AIKALEIMA +
            " order by fin.teksti, c.diaarinumero, c.aikaleima")
    List<PerusteCache> findNewestEntrieByKoulutustyyppisExceptDiaarit(Set<KoulutusTyyppi> tyypit,
                                                                      Set<String> diaariNotIn);

    @Query(value = "select * from peruste_cache c where c.diaarinumero = ?1 and c.aikaleima = (select max(c2.aikaleima) from peruste_cache c2 where c2.peruste_id = c.peruste_id) ORDER BY c.aikaleima DESC LIMIT 1", nativeQuery = true)
    PerusteCache findNewestEntryForPerusteByDiaarinumero(String diaarinumero);

    @Query("select c from PerusteCache c where c.perusteId = ?1 and " + NEWEST_BY_AIKALEIMA)
    PerusteCache findNewestEntryForPeruste(long eperusteetPerusteId);

    @Query("select c.aikaleima from PerusteCache c where c.perusteId = ?1 and " + NEWEST_BY_AIKALEIMA)
    Date findNewestEntryAikaleimaForPeruste(long eperusteetPerusteId);
}
