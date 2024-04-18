package fi.vm.sade.eperusteet.ylops.repository.teksti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.repository.CustomJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LokalisoituTekstiRepository extends CustomJpaRepository<LokalisoituTeksti, Long> {
}
