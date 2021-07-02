package fi.vm.sade.eperusteet.ylops.repository;

import java.io.Serializable;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    default T findOne(ID id) {
        return findById(id).orElse(null);
    }

    default void delete(ID id) {
        deleteById(id);
    }

    default void delete(Collection<ID> ids) {
        ids.forEach(id -> delete(id));
    }
}
