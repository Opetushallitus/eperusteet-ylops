package fi.vm.sade.eperusteet.ylops.service.ops.lukio;

import fi.vm.sade.eperusteet.ylops.dto.lukio.*;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface LukioOpetussuunnitelmaService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    LukioOpetussuunnitelmaRakenneOpsDto getRakenne(@P("opsId") long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    LukioOppiaineTiedotDto getOppiaineTiedot(@P("opsId") long opsId, long oppiaineId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    AihekokonaisuudetPerusteOpsDto getAihekokonaisuudet(@P("opsId") long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetuksenYleisetTavoitteetPerusteOpsDto getOpetuksenYleisetTavoitteet(@P("opsId") long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void updateOpetuksenYleisetTavoitteet(@P("opsId") long opsId, OpetuksenYleisetTavoitteetUpdateDto tavoitteet);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    long saveOppiaine(@P("opsId") long opsId, LukioOppiaineSaveDto oppiaine);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void updateTreeStructure(@P("opsId") Long opsId, OppaineKurssiTreeStructureDto structureDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void updateOppiaine(@P("opsId") long opsId, LukioOppiaineSaveDto oppiaine);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    long addOppimaara(@P("opsId") long opsId, long oppiaineId, LukioKopioiOppimaaraDto kt);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    long addAbstraktiOppiaine(@P("opsId") long opsId, LukioAbstraktiOppiaineTuontiDto tuonti);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    long saveKurssi(@P("opsId") long opsId, LukiokurssiSaveDto kurssi);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void updateKurssi(@P("opsId") long opsId, long kurssiId, LukiokurssiUpdateDto kurssi);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    long disconnectKurssi(Long kurssiId, Long oppiaineId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    long reconnectKurssi(Long kurssiId, Long oppiaineId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    long saveAihekokonaisuus(@P("opsId") long opsId, AihekokonaisuusSaveDto kokonaisuus);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void reArrangeAihekokonaisuudet(@P("opsId") long opsId, AihekokonaisuudetJarjestaDto jarjestys);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void updateAihekokonaisuusYleiskuvaus(@P("opsId") long opsId, AihekokonaisuusSaveDto yleiskuvaus);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void updateAihekokonaisuus(@P("opsId") long opsId, long id, AihekokonaisuusSaveDto kokonaisuus);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void deleteAihekokonaisuus(@P("opsId") long opsId, long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void removeKurssi(@P("opsId") long opsId, long kurssiId);
}
