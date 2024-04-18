package fi.vm.sade.eperusteet.ylops.repository.koodisto;

import fi.vm.sade.eperusteet.ylops.domain.koodisto.KoodistoKoodi;
import fi.vm.sade.eperusteet.ylops.repository.CustomJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KoodistoKoodiRepository extends CustomJpaRepository<KoodistoKoodi, Long> {
    Optional<KoodistoKoodi> findByKoodiUri(String koodiUri);
}
