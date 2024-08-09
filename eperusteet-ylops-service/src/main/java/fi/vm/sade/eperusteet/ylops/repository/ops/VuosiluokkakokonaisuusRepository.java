package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus.Vuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VuosiluokkakokonaisuusRepository extends JpaWithVersioningRepository<Vuosiluokkakokonaisuus, Long> {

    @Query("SELECT v FROM Opetussuunnitelma o JOIN o.vuosiluokkakokonaisuudet ov JOIN ov.vuosiluokkakokonaisuus v WHERE o.id = ?1 AND v.id = ?2")
    Vuosiluokkakokonaisuus findBy(Long opsId, Long id);

    @Query("SELECT CASE COUNT(o) WHEN 0 THEN false ELSE true END FROM Opetussuunnitelma o JOIN o.vuosiluokkakokonaisuudet ov JOIN ov.vuosiluokkakokonaisuus v WHERE v.id = ?1")
    boolean isInUse(Long kokonaisuusId);

    @Query(value = "SELECT ov.oma FROM Opetussuunnitelma o JOIN o.vuosiluokkakokonaisuudet ov JOIN ov.vuosiluokkakokonaisuus v WHERE o.id = ?1 AND v.id = ?2")
    Boolean isOma(long opsId, long id);

    @Query("SELECT v FROM Opetussuunnitelma o " +
            "JOIN o.vuosiluokkakokonaisuudet ov " +
            "JOIN ov.vuosiluokkakokonaisuus v " +
            "JOIN v.tunniste t " +
            "WHERE o.id = :opsId AND t.id = :tunniste")
    Vuosiluokkakokonaisuus findByOpetussuunnitelmaIdAndTunniste(Long opsId, UUID tunniste);
}
