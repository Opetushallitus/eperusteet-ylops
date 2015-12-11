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

package fi.vm.sade.eperusteet.ylops.dto.lukio;

import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.AihekokonaisuudetDto;

/**
 * User: tommiratamaa
 * Date: 19.11.2015
 * Time: 14.27
 */
public class AihekokonaisuudetPerusteOpsDto extends PerusteOpsDto<AihekokonaisuudetDto,
                    AihekokonaisuudetOpsDto> {
    public AihekokonaisuudetPerusteOpsDto(AihekokonaisuudetOpsDto paikallinen) {
        super(paikallinen);
    }

    public AihekokonaisuudetPerusteOpsDto(AihekokonaisuudetDto perusteen, AihekokonaisuudetOpsDto paikallinen) {
        super(perusteen, paikallinen);
    }
}
