package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtumaAuditointitiedoilla;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.revision.Revision;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.domain.teksti.PoistettuTekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.RevisionKayttajaDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.PoistettuTekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.PoistettuTekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.ylops.service.locking.LockManager;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsFeaturesFactory;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsStrategy;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;
import fi.vm.sade.eperusteet.ylops.service.teksti.TekstiKappaleService;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

@Service
@Transactional(readOnly = true)
public class TekstiKappaleViiteServiceImpl implements TekstiKappaleViiteService {

    @Autowired
    private OpsFeaturesFactory<OpsStrategy> opsFeaturesFactory;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private TekstikappaleviiteRepository tekstikappaleviiteRepository;

    @Autowired
    private TekstiKappaleRepository tekstiKappaleRepository;

    @Autowired
    private TekstiKappaleService tekstiKappaleService;

    @Autowired
    private PoistettuTekstiKappaleRepository poistettuTekstiKappaleRepository;

    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private LockManager lockMgr;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkaustietoService;

    @Lazy
    @Autowired
    private TekstiKappaleViiteService self;

    @Override
    public <T> T getTekstiKappaleViite(Long opsId, Long viiteId, Class<T> t) {
        TekstiKappaleViite viite = findViite(opsId, viiteId);
        return mapper.map(viite, t);
    }

    @Override
    public fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto getPerusteTekstikappale(
            Long opsId,
            Long viiteId
    ) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("ops-ei-loydy");
        }

        PerusteCache perusteCached = ops.getCachedPeruste();
        PerusteDto perusteDto = null;
        if (perusteCached == null) {
            perusteDto = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
        } else {
            perusteDto = eperusteetService.getPerusteById(perusteCached.getPerusteId());
        }

        if (perusteDto == null) {
            throw new BusinessRuleViolationException("peruste-cache-puuttuu");
        }

        TekstiKappaleViiteDto.Matala tekstiKappaleViite = getTekstiKappaleViite(opsId, viiteId);

        if (perusteDto.getTekstiKappaleViiteSisalto() != null) {
            return CollectionUtil.treeToStream(
                        perusteDto.getTekstiKappaleViiteSisalto(),
                        fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto::getLapset)
                .filter(viiteDto -> viiteDto.getTekstiKappale() != null && tekstiKappaleViite != null
                        && Objects.equals(tekstiKappaleViite.getPerusteTekstikappaleId(), viiteDto.getTekstiKappale().getId()))
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    @Override
    public TekstiKappaleViiteDto.Matala getTekstiKappaleViite(Long opsId, Long viiteId) {
        TekstiKappaleViite viite = findViite(opsId, viiteId);
        TekstiKappaleViiteDto.Matala viiteDto = mapper.map(viite, TekstiKappaleViiteDto.Matala.class);

        if (SecurityUtil.isAuthenticated()) {
            viiteDto.getTekstiKappale().setMuokkaaja(kayttajanTietoService.haeKayttajanimi(viiteDto.getTekstiKappale().getMuokkaaja()));
        }

        return viiteDto;
    }

    @Override
    @Transactional(readOnly = false)
    public TekstiKappaleViiteDto.Matala addTekstiKappaleViite(
            Long opsId,
            Long parentViiteId,
            TekstiKappaleViiteDto.Matala viiteDto
    ) {
        return this.addTekstiKappaleViite(opsId, parentViiteId, viiteDto, null);
    }

    @Override
    @Transactional(readOnly = false)
    public TekstiKappaleViiteDto.Matala addTekstiKappaleViite(
            Long opsId,
            Long parentViiteId,
            TekstiKappaleViiteDto.Matala viiteDto,
            MuokkausTapahtuma tapahtuma
    ) {
        TekstiKappaleViite parentViite = findViite(opsId, parentViiteId);
        TekstiKappaleViite uusiViite = new TekstiKappaleViite(Omistussuhde.OMA);
        if (viiteDto != null) {
            uusiViite.setPakollinen(viiteDto.isPakollinen());
        }

        tekstikappaleviiteRepository.lock(parentViite.getRoot());

        List<TekstiKappaleViite> lapset = parentViite.getLapset();
        if (lapset == null) {
            lapset = new ArrayList<>();
            parentViite.setLapset(lapset);
        }
        lapset.add(uusiViite);
        uusiViite.setVanhempi(parentViite);
        uusiViite = tekstikappaleviiteRepository.save(uusiViite);

        if (viiteDto == null || (viiteDto.getTekstiKappaleRef() == null && viiteDto.getTekstiKappale() == null)) {
            // Luodaan kokonaan uusi tekstikappale
            TekstiKappale uusiKappale = new TekstiKappale();
            uusiKappale = tekstiKappaleRepository.save(uusiKappale);
            uusiViite.setTekstiKappale(uusiKappale);
        } else {
            // Viittessä on mukana tekstikappale ja/tai lapsiviitteet
            TekstiKappaleViite viiteEntity = mapper.map(viiteDto, TekstiKappaleViite.class);
            uusiViite.setLapset(viiteEntity.getLapset());
            uusiViite.setPerusteTekstikappaleId(viiteEntity.getPerusteTekstikappaleId());

            if (viiteDto.getTekstiKappaleRef() != null) {
                // TODO: Lisää tähän tekstikappaleiden lukuoikeuden tarkistelu
                uusiViite.setTekstiKappale(viiteEntity.getTekstiKappale());
            } else if (viiteDto.getTekstiKappale() != null) {
                tekstiKappaleService.add(opsId, uusiViite, viiteDto.getTekstiKappale());
            }
        }

        tekstikappaleviiteRepository.flush();


        muokkaustietoService.addOpsMuokkausTieto(opsId, uusiViite,
                tapahtuma != null ? tapahtuma : MuokkausTapahtuma.LUONTI);
        return mapper.map(uusiViite, TekstiKappaleViiteDto.Matala.class);
    }

    @Override
    @Transactional(readOnly = false)
    public TekstiKappaleViiteDto updateTekstiKappaleViite(
            Long opsId,
            Long viiteId,
            TekstiKappaleViiteDto uusi
    ) {
        TekstiKappaleViite viite = findViite(opsId, viiteId);
        // Nopea ratkaisu sisällön häviämiseen, korjaantuu oikein uuden näkymän avulla
        if (uusi.getTekstiKappale().getTeksti() == null) {
            uusi.getTekstiKappale().setTeksti(mapper.map(viite.getTekstiKappale(), TekstiKappaleDto.class).getTeksti());
        }
        tekstikappaleviiteRepository.lock(viite.getRoot());
        lockMgr.lock(viite.getTekstiKappale().getId());
        updateTekstiKappale(opsId, viite, uusi.getTekstiKappale(), false /* TODO: pakota lukitus */);
        viite.setPakollinen(uusi.isPakollinen());
        viite.setValmis(uusi.isValmis());
        viite.setPerusteTekstikappaleId(uusi.getPerusteTekstikappaleId());
        viite.setNaytaPerusteenTeksti(uusi.isNaytaPerusteenTeksti());
        viite.setNaytaPohjanTeksti(uusi.isNaytaPohjanTeksti());
        viite.setPiilotettu(uusi.isPiilotettu());
        viite.setLiite(uusi.isLiite());
        viite = tekstikappaleviiteRepository.save(viite);
        muokkaustietoService.addOpsMuokkausTieto(opsId, viite, MuokkausTapahtuma.PAIVITYS);
        return mapper.map(viite, TekstiKappaleViiteDto.class);
    }

    @Override
    @Transactional(readOnly = false)
    public void reorderSubTree(Long opsId, Long rootViiteId, TekstiKappaleViiteDto.Puu uusi) {
        TekstiKappaleViite viite = findViite(opsId, rootViiteId);
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

        if (ops == null) {
            throw new BusinessRuleViolationException("Opetussuunnitelmaa ei olemassa.");
        }

        OpsStrategy strategy = opsFeaturesFactory.getStrategy(ops.getToteutus());
        strategy.reorder(uusi, ops);

        tekstikappaleviiteRepository.lock(viite.getRoot());
        Set<TekstiKappaleViite> refs = Collections.newSetFromMap(new IdentityHashMap<>());
        refs.add(viite);
        TekstiKappaleViite parent = viite.getVanhempi();
        clearChildren(viite, refs);
        updateTraverse(opsId, parent, uusi, refs);

        muokkaustietoService.addOpsMuokkausTieto(opsId, new HistoriaTapahtumaAuditointitiedoilla(ops), MuokkausTapahtuma.PAIVITYS, NavigationType.opetussuunnitelma_rakenne);
    }

    @Override
    @Transactional
    public void removeTekstiKappaleViite(Long opsId, Long viiteId) {
        TekstiKappaleViite viite = findViite(opsId, viiteId);

        if (viite.getVanhempi() == null) {
            throw new BusinessRuleViolationException("sisallon-juurielementtia-ei-voi-poistaa");
        }

        if (getPerusteTekstikappale(opsId, viiteId) != null) {
            throw new BusinessRuleViolationException("pakollista-tekstikappaletta-ei-voi-poistaa");
        }

        if (viite.getLapset() != null && !viite.getLapset().isEmpty()) {
            Sets.newHashSet(viite.getLapset()).forEach(lapsi -> removeTekstiKappaleViite(opsId, lapsi.getId()));
        }

        tekstikappaleviiteRepository.lock(viite.getRoot());
        TekstiKappale tekstiKappale = viite.getTekstiKappale();
        if (tekstiKappale != null && tekstiKappale.getTila().equals(Tila.LUONNOS) && findViitteet(opsId, viiteId).size() == 1) {
            lockMgr.lock(tekstiKappale.getId());
            tekstiKappaleService.removeTekstiKappaleFromOps(opsId, tekstiKappale.getId(), viite.getVanhempi().getId());
        }

        muokkaustietoService.addOpsMuokkausTieto(opetussuunnitelmaRepository.findOne(opsId), new HistoriaTapahtumaAuditointitiedoilla(viite), MuokkausTapahtuma.POISTO);

        viite.setTekstiKappale(null);
        viite.getVanhempi().getLapset().remove(viite);
        viite.setVanhempi(null);
        tekstikappaleviiteRepository.delete(viite);

        poistaTekstikappaleAlaOpetussuunnitelmista(opsId, tekstiKappale.getTunniste());
    }

    @Override
    public Integer alaOpetussuunnitelmaLukumaaraTekstikappaleTunniste(Long opsId, UUID tunniste) {
        return opetussuunnitelmaRepository.findAllByPohjaId(opsId).stream()
                .mapToInt(opetussuunnitelma -> tekstikappaleviiteRepository.findByOpetussuunnitelmaIdAndTekstikappaleTunniste(opetussuunnitelma.getId(), tunniste.toString()) != null ? 1 : 0)
                .sum();
    }

    @Override
    public TekstiKappaleViiteDto.Matala getTekstiKappaleViiteByTunniste(@P("opsId") Long opsId, UUID tunniste) {
        return mapper.map(tekstikappaleviiteRepository.findByOpetussuunnitelmaIdAndTekstikappaleTunniste(opsId, tunniste.toString()), TekstiKappaleViiteDto.Matala.class);
    }

    private void poistaTekstikappaleAlaOpetussuunnitelmista(Long opsId, UUID tunniste) {
        opetussuunnitelmaRepository.findAllByPohjaId(opsId).forEach(opetussuunnitelma -> {
            TekstiKappaleViite viite = tekstikappaleviiteRepository.findByOpetussuunnitelmaIdAndTekstikappaleTunniste(opetussuunnitelma.getId(), tunniste.toString());
            if (viite != null) {
                removeTekstiKappaleViite(opetussuunnitelma.getId(), viite.getId());
            }
        });
    }

    @Override
    @Transactional(readOnly = false)
    public TekstiKappaleViiteDto.Puu kloonaaTekstiKappale(Long opsId, Long viiteId) {
        return kloonaaTekstiKappale(opsId, viiteId, TekstiKappaleViiteDto.Puu.class);
    }

    @Override
    @Transactional(readOnly = false)
    public <T> T kloonaaTekstiKappale(Long opsId, Long viiteId, Class<T> t) {
        TekstiKappaleViite viite = findViite(opsId, viiteId);
        TekstiKappale originaali = viite.getTekstiKappale();
        // TODO: Tarkista onko tekstikappaleeseen lukuoikeutta
        TekstiKappale klooni = originaali.copy();
        klooni.setTila(Tila.LUONNOS);
        viite.setTekstiKappale(tekstiKappaleRepository.save(klooni));
        viite.setOmistussuhde(Omistussuhde.OMA);
        viite = tekstikappaleviiteRepository.save(viite);
        return mapper.map(viite, t);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevisionKayttajaDto> getVersions(Long opsId, long viiteId) {
        List<RevisionKayttajaDto> revisions = mapper.mapAsList(tekstiKappaleRepository.getRevisions(viiteId), RevisionKayttajaDto.class);

        Map<String, KayttajanTietoDto> kayttajanTiedot = kayttajanTietoService.haeKayttajatiedot(revisions.stream()
                        .map(RevisionKayttajaDto::getMuokkaajaOid)
                        .collect(Collectors.toList())).stream()
                .collect(Collectors.toMap(KayttajanTietoDto::getOidHenkilo, kayttajanTieto -> kayttajanTieto));

        revisions.forEach(revision -> revision.setKayttajanTieto(kayttajanTiedot.get(revision.getMuokkaajaOid())));
        return revisions;
    }

    @Override
    @Transactional(readOnly = true)
    public TekstiKappaleDto findTekstikappaleVersion(long opsId, long viiteId, long versio) {
        Long kappaleId = getTekstiKappaleViite(opsId, viiteId).getTekstiKappale().getId();
        TekstiKappale tekstiKappale = tekstiKappaleRepository.findRevision(kappaleId, (int) versio);
        TekstiKappaleDto tekstiKappaleDto = mapper.map(tekstiKappale, TekstiKappaleDto.class);
        tekstiKappaleDto.setMuokkaaja(kayttajanTietoService.haeKayttajanimi(tekstiKappaleDto.getMuokkaaja()));
        return tekstiKappaleDto;
    }

    @Override
    @Transactional
    public void revertToVersion(Long opsId, Long viiteId, Integer versio) {
        Long kappaleId = getTekstiKappaleViite(opsId, viiteId).getTekstiKappale().getId();
        TekstiKappale tekstiKappale = tekstiKappaleRepository.findRevision(kappaleId, versio);
        TekstiKappaleDto dto = mapper.map(tekstiKappale, TekstiKappaleDto.class);
        tekstiKappaleService.update(opsId, dto, MuokkausTapahtuma.PALAUTUS);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PoistettuTekstiKappaleDto> getRemovedTekstikappaleetForOps(Long opsId) {
        connectMissingTekstikappaleetIfAny(opsId);
        List<PoistettuTekstiKappaleDto> list = mapper.mapAsList(poistettuTekstiKappaleRepository.findPoistetutByOpsId(opsId), PoistettuTekstiKappaleDto.class);
        list.forEach(poistettuTekstiKappaleDto -> {
            TekstiKappaleDto teksti = tekstiKappaleService.get(opsId, poistettuTekstiKappaleDto.getTekstiKappale());
            poistettuTekstiKappaleDto.setMuokkaaja(kayttajanTietoService.haeKayttajanimi(poistettuTekstiKappaleDto.getMuokkaaja()));
            poistettuTekstiKappaleDto.setNimi(teksti.getNimi());
            poistettuTekstiKappaleDto.setTekstiKappale(teksti.getId());
        });
        return list;
    }

    private void connectMissingTekstikappaleetIfAny(Long opsId) {
        poistettuTekstiKappaleRepository.findPoistetutByOpsId(opsId)
                .stream()
                .filter(poistettuTekstiKappale -> {
                    TekstiKappale tk = tekstiKappaleRepository.findOne(poistettuTekstiKappale.getTekstiKappale());
                    return (tk == null);
                })
                .collect(Collectors.toList())
                .forEach(poistettuTekstiKappale -> {
                    List<Revision> revs = tekstiKappaleRepository.getRevisions(poistettuTekstiKappale.getTekstiKappale());
                    if (revs.size() > 1) {
                        TekstiKappale rev = tekstiKappaleRepository.findRevision(revs.get(1).getId(), revs.get(1).getNumero());
                        rev = tekstiKappaleRepository.save(rev);
                        poistettuTekstiKappale.setTekstiKappale(rev.getId());
                    }
                });
    }

    @Override
    public TekstiKappaleViiteDto.Matala getTekstiKappaleViiteOriginal(Long opsId, Long viiteId) {
        TekstiKappaleViite viite = findViite(opsId, viiteId);
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);
        if (opetussuunnitelma.getPohja() != null) {
            TekstiKappaleViite pohjaViite = tekstikappaleviiteRepository.findByOpetussuunnitelmaIdAndTekstikappaleTunniste(
                    opetussuunnitelma.getPohja().getId(),
                    viite.getTekstiKappale().getTunniste().toString());

            if (pohjaViite != null) {
                return mapper.map(pohjaViite, TekstiKappaleViiteDto.Matala.class);
            }
        }

        return null;
    }

    @Override
    public List<TekstiKappaleViiteDto.Matala> getTekstiKappaleViiteOriginals(Long opsId, Long viiteId) {
        return getTekstiKappaleViiteOriginals(opsId, viiteId, TekstiKappaleViiteDto.Matala.class);
    }

    @Override
    public <T> List<T> getTekstiKappaleViiteOriginals(Long opsId, Long viiteId, Class<T> clazz) {
        List<T> viiteList = new ArrayList<>();
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);

        TekstiKappaleViite viite = findViite(opsId, viiteId);
        if (opetussuunnitelma.getPohja() != null) {
            TekstiKappaleViite pohjanViite = tekstikappaleviiteRepository.findByOpetussuunnitelmaIdAndTekstikappaleTunniste(
                opetussuunnitelma.getPohja().getId(),
                viite.getTekstiKappale().getTunniste().toString());

            if (pohjanViite != null) {
                viiteList.add(mapper.map(pohjanViite, clazz));

                if (pohjanViite.isNaytaPohjanTeksti() && opetussuunnitelma.getPohja().getPohja() != null) {
                    TekstiKappaleViite pohjanPohjanViite = tekstikappaleviiteRepository.findByOpetussuunnitelmaIdAndTekstikappaleTunniste(
                        opetussuunnitelma.getPohja().getPohja().getId(),
                        viite.getTekstiKappale().getTunniste().toString());

                    if (pohjanPohjanViite != null) {
                        viiteList.add(mapper.map(pohjanPohjanViite, clazz));
                    }
                }
            }
        }

        return viiteList;
    }

    @Override
    @Transactional
    public TekstiKappaleDto returnRemovedTekstikappale(Long opsId, Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

        PoistettuTekstiKappale poistettu = poistettuTekstiKappaleRepository.findOne(id);
        TekstiKappaleViite parentViite = ops.getTekstit();
        if (poistettu.getParentTekstiKappaleViite() != null) {
            parentViite = CollectionUtil.treeToStream(ops.getTekstit(), TekstiKappaleViite::getLapset)
                    .filter(viite -> viite.getId().equals(poistettu.getParentTekstiKappaleViite())).findFirst()
                    .orElse(parentViite);
        }

        tekstikappaleviiteRepository.lock(parentViite.getRoot());
        TekstiKappale tekstikappale = tekstiKappaleRepository.findOne(poistettu.getTekstiKappale());
        TekstiKappaleDto dto = mapper.map(tekstikappale, TekstiKappaleDto.class);
        dto.setId(null);
        addTekstiKappaleViite(opsId, parentViite.getId(), new TekstiKappaleViiteDto.Matala(dto), MuokkausTapahtuma.PALAUTUS);
        poistettu.setPalautettu(true);

        opetussuunnitelmaRepository.findAllByPohjaId(opsId).forEach(opetussuunnitelma -> {
            List<PoistettuTekstiKappale> poistetut = poistettuTekstiKappaleRepository.findPoistetutByOpsIdAndTunniste(opetussuunnitelma.getId(), tekstikappale.getTunniste());
            poistetut.forEach(poistettuTekstiKappale -> {
                returnRemovedTekstikappale(opetussuunnitelma.getId(), poistettuTekstiKappale.getId());
            });
        });

        return dto;
    }

    private List<TekstiKappaleViite> findViitteet(Long opsId, Long viiteId) {
        TekstiKappaleViite viite = findViite(opsId, viiteId);
        return tekstikappaleviiteRepository.findAllByTekstiKappale(viite.getTekstiKappale());
    }

    private TekstiKappaleViite findViite(Long opsId, Long viiteId) {
        return assertExists(tekstikappaleviiteRepository.findInOps(opsId, viiteId), "Tekstikappaleviitettä ei ole olemassa");
    }

    private void clearChildren(TekstiKappaleViite viite, Set<TekstiKappaleViite> refs) {
        for (TekstiKappaleViite lapsi : viite.getLapset()) {
            refs.add(lapsi);
            clearChildren(lapsi, refs);
        }
        viite.setVanhempi(null);
        viite.getLapset().clear();
    }

    private void updateTekstiKappale(Long opsId, TekstiKappaleViite viite, TekstiKappaleDto uusiTekstiKappale, boolean requireLock) {
        if (uusiTekstiKappale != null) {
                if (viite.getTekstiKappale() != null) {
                    final Long tid = viite.getTekstiKappale().getId();
                    if (requireLock || lockMgr.getLock(tid) != null) {
                        lockMgr.ensureLockedByAuthenticatedUser(tid);
                    }
                }
                tekstiKappaleService.update(opsId, uusiTekstiKappale, requireLock, null);
        }
    }

    private TekstiKappaleViite updateTraverse(Long opsId, TekstiKappaleViite parent, TekstiKappaleViiteDto.Puu uusi,
                                              Set<TekstiKappaleViite> refs) {
        TekstiKappaleViite viite;
        if (uusi.getId() != null) {
            viite = tekstikappaleviiteRepository.findOne(uusi.getId());
        }
        else {
            uusi.setNaytaPerusteenTeksti(false);
            uusi.setOmistussuhde(Omistussuhde.OMA);
            uusi.setPerusteTekstikappaleId(null);
            TekstiKappaleViite uusiViite = mapper.map(uusi, TekstiKappaleViite.class);
            uusiViite.getTekstiKappale().setValmis(false);
            uusiViite.getTekstiKappale().setTila(Tila.LUONNOS);
            uusiViite.getLapset().clear();
            uusiViite.setTekstiKappale(tekstiKappaleRepository.save(uusiViite.getTekstiKappale()));
            viite = tekstikappaleviiteRepository.save(uusiViite);
            refs.add(viite);
        }

        if (viite == null || !refs.remove(viite)) {
            throw new BusinessRuleViolationException("Viitepuun päivitysvirhe, annettua alipuun juuren viitettä ei löydy");
        }
        viite.setVanhempi(parent);

        List<TekstiKappaleViite> lapset = viite.getLapset();
        lapset.clear();

        if (uusi.getLapset() != null) {
            lapset.addAll(uusi.getLapset()
                    .stream()
                    .map(elem -> updateTraverse(opsId, viite, elem, refs))
                    .collect(Collectors.toList()));
        }
        return tekstikappaleviiteRepository.save(viite);
    }

}
