package fi.vm.sade.eperusteet.ylops.repository.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.repository.CustomJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DokumenttiRepository extends CustomJpaRepository<Dokumentti, Long> {
    List<Dokumentti> findByOpsIdAndKieliAndTila(Long opsId, Kieli kieli, DokumenttiTila tila, Sort sort);

    Dokumentti findFirstByOpsIdAndKieliOrderByAloitusaikaDesc(Long opsId, Kieli kieli);

    Dokumentti findByIdInAndKieli(Set<Long> id, Kieli kieli);
}
