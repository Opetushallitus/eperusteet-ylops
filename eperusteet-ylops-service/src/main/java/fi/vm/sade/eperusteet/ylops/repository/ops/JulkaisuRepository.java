package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanJulkaisu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JulkaisuRepository extends JpaRepository<OpetussuunnitelmanJulkaisu, Long> {
    List<OpetussuunnitelmanJulkaisu> findAllByOpetussuunnitelma(Opetussuunnitelma ops);

    OpetussuunnitelmanJulkaisu findFirstByOpetussuunnitelmaOrderByRevisionDesc(Opetussuunnitelma opetussuunnitelma);
}
