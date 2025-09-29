package fi.vm.sade.eperusteet.ylops.repository;

import fi.vm.sade.eperusteet.ylops.domain.SkeduloituAjo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkeduloituajoRepository extends JpaRepository<SkeduloituAjo, Long> {

    SkeduloituAjo findByNimi(String nimi);

}
