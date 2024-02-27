package fi.vm.sade.eperusteet.ylops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface CustomJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    default T findOne(ID id) {
        return findById(id).orElse(null);
    }

}
