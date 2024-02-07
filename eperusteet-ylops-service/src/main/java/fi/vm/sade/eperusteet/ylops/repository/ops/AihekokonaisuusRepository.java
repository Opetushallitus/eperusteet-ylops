package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.lukio.Aihekokonaisuus;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AihekokonaisuusRepository extends JpaWithVersioningRepository<Aihekokonaisuus, Long> {
    @Query(value = "select ak from Aihekokonaisuus ak where ak.parent.id = ?1 order by ak.id")
    List<Aihekokonaisuus> findByParent(long parentId);
}
