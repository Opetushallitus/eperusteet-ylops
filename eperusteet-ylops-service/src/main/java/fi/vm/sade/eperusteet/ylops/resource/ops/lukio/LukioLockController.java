/*
 *  Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 *  This program is free software: Licensed under the EUPL, Version 1.1 or - as
 *  soon as they will be approved by the European Commission - subsequent versions
 *  of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 */

package fi.vm.sade.eperusteet.ylops.resource.ops.lukio;

import fi.vm.sade.eperusteet.ylops.resource.util.AbstractLockController;
import fi.vm.sade.eperusteet.ylops.service.locking.LockService;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioLockCtx;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * User: tommiratamaa
 * Date: 16.12.2015
 * Time: 14.19
 */
@RestController
@RequestMapping(value = "/opetussuunnitelmat/lukio/{opsId}/lukko")
@ApiIgnore
public class LukioLockController extends AbstractLockController<LukioLockCtx> {
    @Autowired
    private LukioLockService lockService;

    @Override
    protected LockService<LukioLockCtx> service() {
        return lockService;
    }
}
