package fi.vm.sade.eperusteet.ylops.repository.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiKuva;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DokumenttiKuvaRepository extends JpaRepository<DokumenttiKuva, Long> {
    DokumenttiKuva findFirstByOpsIdAndKieli(Long opsId, Kieli kieli);
}
