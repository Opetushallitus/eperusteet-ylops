package fi.vm.sade.eperusteet.ylops.repository.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DokumenttiRepository extends JpaRepository<Dokumentti, Long> {
    List<Dokumentti> findByOpsIdAndKieliAndTila(Long opsId, Kieli kieli, DokumenttiTila tila, Sort sort);

    Dokumentti findFirstByOpsIdAndKieliOrderByAloitusaikaDesc(Long opsId, Kieli kieli);

    Dokumentti findByIdInAndKieli(Set<Long> id, Kieli kieli);

    Dokumentti findById(Long id);
}
