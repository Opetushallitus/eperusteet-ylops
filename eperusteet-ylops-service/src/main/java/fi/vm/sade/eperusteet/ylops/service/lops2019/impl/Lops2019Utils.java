package fi.vm.sade.eperusteet.ylops.service.lops2019.impl;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019OppiaineJarjestys;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PerustePaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019SortableOppiaineDto;

import java.util.*;
import java.util.function.Function;

import static java.util.Comparator.comparing;

public class Lops2019Utils {
    public static void sortOppiaineet(
            Set<Lops2019OppiaineJarjestys> oppiaineJarjestykset,
            List<? extends Lops2019SortableOppiaineDto> oppiaineet,
            List<Lops2019PaikallinenOppiaineDto> paikallisetOppiaineet,
            Function<Lops2019SortableOppiaineDto, Boolean> oaFunction,
            Function<Lops2019PaikallinenOppiaineDto, Boolean> poaFunction
    ) {

        Map<String, Lops2019PerustePaikallinenOppiaineDto> oppiaineJarjestyksetMap = new HashMap<>();

        if (oppiaineJarjestykset != null) {
            for (Lops2019OppiaineJarjestys oppiaineJarjestys : oppiaineJarjestykset) {
                String koodi = oppiaineJarjestys.getKoodi();
                Integer jarjestys = oppiaineJarjestys.getJarjestys();
                if (oppiaineJarjestyksetMap.containsKey(koodi)) {
                    Lops2019PerustePaikallinenOppiaineDto dto = oppiaineJarjestyksetMap.get(koodi);
                    dto.setJarjestys(jarjestys);
                } else {
                    Lops2019PerustePaikallinenOppiaineDto dto = new Lops2019PerustePaikallinenOppiaineDto();
                    dto.setJarjestys(jarjestys);
                    oppiaineJarjestyksetMap.put(koodi, dto);
                }
            }
        }

        // Perusteen oppiaineet
        if (oppiaineet != null) {
            oppiaineet.forEach(oa -> {
                String koodi = (oa).getKoodi().getUri();
                if (oppiaineJarjestyksetMap.containsKey(koodi)) {
                    Lops2019PerustePaikallinenOppiaineDto dto = oppiaineJarjestyksetMap.get(koodi);
                    dto.setOa(oa);
                } else {
                    Lops2019PerustePaikallinenOppiaineDto dto = new Lops2019PerustePaikallinenOppiaineDto();
                    dto.setOa(oa);
                    oppiaineJarjestyksetMap.put(koodi, dto);
                }
            });
        }

        // Paikalliset oppiaineet
        if (paikallisetOppiaineet != null) {
            paikallisetOppiaineet.forEach(poa -> {
                String koodi = poa.getKoodi();
                if (oppiaineJarjestyksetMap.containsKey(koodi)) {
                    Lops2019PerustePaikallinenOppiaineDto dto = oppiaineJarjestyksetMap.get(koodi);
                    dto.setPoa(poa);
                } else {
                    Lops2019PerustePaikallinenOppiaineDto dto = new Lops2019PerustePaikallinenOppiaineDto();
                    dto.setPoa(poa);
                    oppiaineJarjestyksetMap.put(koodi, dto);
                }
            });
        }

        // Paikalliset ja perusteen oppiaineet
        oppiaineJarjestyksetMap.values().stream()
                // Toissijainen
                .sorted(comparing(dto -> dto.getOa() != null
                        ? dto.getOa().getKoodi().getUri()
                        : (dto.getPoa() != null ? dto.getPoa().getKoodi() : "")))
                // Ensisisijainen
                .sorted(comparing((Lops2019PerustePaikallinenOppiaineDto dto) -> Optional
                        .ofNullable(dto.getJarjestys()).orElse(0)))
                .forEach(dto -> {
                    Lops2019SortableOppiaineDto oa = dto.getOa();
                    Lops2019PaikallinenOppiaineDto poa = dto.getPoa();
                    if (oa != null) {
                        oaFunction.apply(oa);
                    } else if (poa != null) {
                        poaFunction.apply(poa);
                    }
                });
    }
}
