package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaineenvuosiluokka;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OppiaineenvuosiluokkaRepository extends JpaWithVersioningRepository<Oppiaineenvuosiluokka, Long> {
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Oppiaine o JOIN o.vuosiluokkakokonaisuudet k JOIN k.vuosiluokat v WHERE o.id = ?1 AND k.id=?2 AND v.id = ?3")
    boolean exists(long oppiaineId, long kokonaisuusId, long vuosiluokkaId);

    @Query(value = "SELECT vl FROM Oppiaine o JOIN o.vuosiluokkakokonaisuudet k JOIN k.vuosiluokat vl WHERE o.id = ?1 AND vl.id = ?2")
    Oppiaineenvuosiluokka findByOppiaine(Long oppiaineId, Long vuosiluokkaId);

    @Query(value = "SELECT vl FROM Oppiaine o JOIN o.vuosiluokkakokonaisuudet k JOIN k.vuosiluokat vl WHERE o.id = ?1 AND vl.id = ?2")
    Oppiaineenvuosiluokka findByOpsAndOppiaine(Long opsId, Long oppiaineId, Long vuosiluokkaId);
}
