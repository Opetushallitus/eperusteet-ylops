package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.lukio.Lukiokurssi;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioKurssiParentDto;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LukiokurssiRepository extends JpaWithVersioningRepository<Lukiokurssi, Long> {
    @Query(value = "select findParentKurssi(?1, ?2)", nativeQuery = true)
    Long findParentKurssiIdIfExists(long opsId, long kurssiId);

    @Query(name = "parentViewByOps", nativeQuery = true)
    List<LukioKurssiParentDto> findParentKurssisByOps(long opsId);
}
