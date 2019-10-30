package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Transactional
public class OpsDispatcher {


    @Autowired
    private OpetussuunnitelmaRepository opsRepository;

    @Autowired
    private List<OpsToteutus> kaikkiToteutukset;

    private HashMap<Class, OpsToteutus> defaults = new HashMap<>();
    private Map<Class, HashMap<KoulutustyyppiToteutus, OpsToteutus>> toteutuksetMap = new HashMap<>();

    @PostConstruct
    public void postConstruct() {
        for (OpsToteutus toteutus : kaikkiToteutukset) {
            Set<KoulutustyyppiToteutus> toteutukset = toteutus.getTyypit();
            Class impl = toteutus.getImpl();
            if (toteutukset.isEmpty()) {
                defaults.put(impl, toteutus);
            }
            else {
                if (!toteutuksetMap.containsKey(impl)) {
                    toteutuksetMap.put(impl, new HashMap<>());
                }
                HashMap<KoulutustyyppiToteutus, OpsToteutus> map = toteutuksetMap.get(impl);
                toteutukset.forEach(t -> {
                    map.put(t, toteutus);
                });
            }
        }
    }

    @PreAuthorize("permitAll()")
    public <T extends OpsToteutus> T get(Long opsId, Class<T> clazz) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("Perustetta ei ole");
        }
        return get(ops, clazz);
    }

    @PreAuthorize("permitAll()")
    public <I extends OpsIdentifiable & Identifiable, T extends OpsToteutus> T get(I ops, Class<T> clazz) {
        return get(ops.getToteutus(), clazz);
    }

    @PreAuthorize("permitAll()")
    public <T extends OpsToteutus> T get(Class<T> clazz) {
        return get((KoulutustyyppiToteutus)null, clazz);
    }

    @PreAuthorize("permitAll()")
    public <T extends OpsToteutus> T get(KoulutustyyppiToteutus toteutus, Class<T> clazz) {
        if (toteutus != null) {
            HashMap<KoulutustyyppiToteutus, OpsToteutus> toteutukset = this.toteutuksetMap.getOrDefault(clazz, null);
            if (toteutukset != null && toteutukset.containsKey(toteutus)) {
                OpsToteutus impl = toteutukset.getOrDefault(toteutus, null);
                if (impl != null) {
                    return (T) impl;
                }
            }
        }
        OpsToteutus impl = defaults.getOrDefault(clazz, null);
        if (impl != null) {
            return (T) impl;
        }
        throw new BusinessRuleViolationException("Toteutusta ei löytynyt: "
                + clazz.getSimpleName()
                + " " + (toteutus != null ? toteutus.toString() : ""));
    }
}
