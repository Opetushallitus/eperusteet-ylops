package fi.vm.sade.eperusteet.ylops.resource.util;

import fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsAudit;
import fi.vm.sade.eperusteet.ylops.service.audit.LogMessage;
import fi.vm.sade.eperusteet.ylops.service.revision.RevisionMetaService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Aspect
@Component
public class AuditLogger {
    @Autowired
    private EperusteetYlopsAudit audit;

    @Autowired
    private RevisionMetaService revisionMetaService;

    private boolean hasPathVariable(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof PathVariable) {
                return true;
            }
        }
        return false;
    }

    @Around("execution(* *(..)) && @annotation(fi.vm.sade.eperusteet.ylops.resource.util.AuditLogged)")
    public Object auditLog(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getStaticPart().getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature method = (MethodSignature) signature;
            RequestMapping mapping = method.getMethod().getAnnotation(RequestMapping.class);
            if (mapping != null && mapping.method() != null && mapping.method().length > 0) {
                String[] parameterNames = method.getParameterNames();
                Object[] parameters = point.getArgs();
                Annotation[][] parameterAnnotations = method.getMethod().getParameterAnnotations();
                String name = method.getName();
                String controllerName = method.getMethod().getDeclaringClass().getName();
                Map<String, Object> params = new HashMap<>();
                for (int idx = 0; idx < parameters.length; ++idx) {
                    if (hasPathVariable(parameterAnnotations[idx])) {
                        params.put(parameterNames[idx], parameters[idx]);
                    }
                }

                LogMessage.LogMessageBuilder message = LogMessage
                        .builder(params, controllerName, name)
                        .beforeRevision(revisionMetaService.getCurrentRevision());
                try {
                    return point.proceed();
                }
                catch (RuntimeException ex) {
                    message.add("failed", true);
                    throw ex;
                }
                finally {
                    message.afterRevision(revisionMetaService.getCurrentRevision()).log();
                }
            }
        }
        return point.proceed();
    }
}
