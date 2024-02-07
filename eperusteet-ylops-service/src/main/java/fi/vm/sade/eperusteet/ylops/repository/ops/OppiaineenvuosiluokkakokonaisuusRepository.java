package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaineenvuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OppiaineenvuosiluokkakokonaisuusRepository extends JpaWithVersioningRepository<Oppiaineenvuosiluokkakokonaisuus, Long> {

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Oppiaine o JOIN o.vuosiluokkakokonaisuudet vk WHERE o.id = ?1 AND vk.id = ?2")
    boolean exists(long oppiaineId, long kokonaisuusId);


}
