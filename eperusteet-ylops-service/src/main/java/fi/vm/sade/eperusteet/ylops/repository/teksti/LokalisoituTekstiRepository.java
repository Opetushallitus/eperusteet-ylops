package fi.vm.sade.eperusteet.ylops.repository.teksti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LokalisoituTekstiRepository extends JpaRepository<LokalisoituTeksti, Long> {
}
