package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokkakokonaisuusviite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VuosiluokkakokonaisuusviiteRepository extends JpaRepository<Vuosiluokkakokonaisuusviite, Long> {

}
