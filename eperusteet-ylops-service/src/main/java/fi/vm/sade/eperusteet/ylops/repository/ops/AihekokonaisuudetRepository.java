package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.lukio.Aihekokonaisuudet;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AihekokonaisuudetRepository extends JpaWithVersioningRepository<Aihekokonaisuudet, Long> {
}
