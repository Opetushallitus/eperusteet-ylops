# eperusteet-ylops-service: parannussuunnitelma

Päivitetty: 2026-09-23
Kohde: `eperusteet-ylops-service`, Java-koodi (Spring Boot 4, Java 21, Hibernate 7). Noin 690 päätiedostoa, 38 testitiedostoa.
Rajaus: `src/main/java` ja `src/test/java`. SQL-migraatioita on katsottu vain uniikkiehtojen osalta. Käyttöliittymä, CI, Docker-skriptit ja `tools/` eivät kuulu tähän läpikäyntiin.

Sama läpikäynti on tehty sisarpalveluille: `eperusteet/docs/improvement_plan.md` ja `eperusteet-amosaa/improvement_plan.md`. Monet löydökset toistuvat kaikissa kolmessa, koska niissä on yhteinen pohja (tietoturvakonfiguraatio, PDF-takaisinkutsut, liitteet, ajastukset, HTTP-asiakkaat).

## 1. Lukuohje

Kaikki polut ovat suhteessa hakemistoon `eperusteet-ylops-service/src/main/java/fi/vm/sade/eperusteet/ylops/`, ellei toisin mainita. Rivinumerot viittaavat commitin `7e0b800b` tilaan.

Vakavuusluokat:

- **Kriittinen**: hyödynnettävissä ilman kirjautumista tai kenen tahansa kirjautuneen toimesta, ja vaikuttaa julkaistuun sisältöön.
- **Korkea**: todellinen bugi, tietovuoto kirjautuneille käyttäjille tai tuotantohäiriön riski.
- **Keskitaso**: virhetilanteet käsitellään väärin, piileviä vikoja tai suorituskykyongelma.
- **Matala**: ylläpidettävyys ja siisteys.

Työmäärä: S = alle päivä, M = 1–3 päivää, L = yli 3 päivää.

Merkintä **(V)** tarkoittaa, että löydös on tarkistettu käsin lähdekoodista tämän dokumentin kirjoittamisen yhteydessä. Muut löydökset ovat katselmoinnissa lähdekoodista vahvistettuja, mutta rivinumerot kannattaa tarkistaa ennen korjausta.

---

## 2. Yhteenveto ja korjausjärjestys

| # | Toimenpide | Vakavuus | Työ | Kohta |
|---|---|---|---|---|
| 1 | Rajaa PDF-datan ja -tilan päivitys PDF-palvelulle | Kriittinen | S | 3.1 |
| 2 | Lisää oikeustarkistukset `DokumenttiKuvaService`-palveluun | Kriittinen | S | 3.2 |
| 3 | Korjaa vuosiluokkakokonaisuuden kopiokonstruktori | Korkea | S | 4.1 |
| 4 | Korjaa julkaisuvirta: tila-tallennuksen itsekutsu, asynkroninen työ commitin jälkeen, KESKEN-esto, validointivirheiden välitys | Korkea | M | 5.1, 5.2 |
| 5 | Rajaa luonnosten PDF:t, liitteet, termistö ja kommentit oikeuksien mukaan | Korkea | S | 7.2–7.4 |
| 6 | Ota CSRF ja tietoturvaotsakkeet käyttöön, rajaa CAS-proxyt, oletuksena kielletty suodatinketju | Korkea | M | 7.1 |
| 7 | Tilasiirtymät: estä `POISTETTU -> JULKAISTU` ja `VALMIS -> JULKAISTU` ilman julkaisua, ja palauta virhe laittomasta siirtymästä | Korkea | S | 4.2 |
| 8 | Toteuta pohjan validointi tai poista se | Korkea | S | 4.3 |
| 9 | Ajastettujen tehtävien lukitus klusteriturvalliseksi | Korkea | M | 8.2 |
| 10 | HTTP-asiakkaat: aikakatkaisut, tuotanto-oletusten ja heittomerkkioletusten poisto, virhetulosten välimuistituksen esto | Korkea | M | 8.1 |
| 11 | Korjaa NPE-riskit | Keskitaso | M | 6 |
| 12 | Rajaa kuvakoko ja sivukoko, suojaa `Content-Disposition` | Keskitaso | S | 7.5 |
| 13 | Domain: `Oppiaine`-entiteetin id-pohjainen hashCode, kaskadit, kopioinnin jaetut viitteet | Keskitaso | M | 9 |
| 14 | Testikattavuus: oikeudet, julkaisu, integraatiot | Keskitaso | L | 11 |
| 15 | Refaktoroi suurimmat luokat, siirry pois Orikasta | Matala | L | 10, 12 |

---

## 3. Kriittiset löydökset

### 3.1 PDF-datan ja -tilan ylikirjoitus ilman oikeustarkistusta (V)

`service/dokumentti/DokumenttiService.java:48-50`, `resource/dokumentti/DokumenttiController.java:200-213`

```java
void updateDokumenttiTila(DokumenttiTila tila, Long dokumenttiId);
void updateDokumenttiPdfData(byte[] pdfData, Long dokumenttiId);
```

Metodeilta puuttuu `@PreAuthorize`, ja suodatinketju vaatii POST-pyynnöille vain kirjautumisen. Kuka tahansa kirjautunut virkailija voi korvata minkä tahansa opetussuunnitelman PDF:n tai muuttaa sen tilan. Paikallisessa profiilissa `config/WebSecurityConfigurationDev.java:41` sallii polun `/api/dokumentit/pdf/**` ilman kirjautumista.

**Korjaus:** rajaa kutsut PDF-palvelun palvelutunnukselle (oma rooli tai jaettu salaisuus otsakkeessa). Tarkista, että dokumentti on tilassa `JONOSSA` tai `LUODAAN`. Sama korjaus tarvitaan eperusteet- ja amosaa-palveluihin, joten se kannattaa tehdä yhteisenä toteutuksena (esimerkiksi `eperusteet-backend-utils`).

### 3.2 `DokumenttiKuvaService` ilman oikeustarkistuksia (V)

`service/dokumentti/DokumenttiKuvaService.java:10-19`, käyttö `resource/dokumentti/DokumenttiController.java` (kuvien POST, GET ja DELETE)

```java
public interface DokumenttiKuvaService {
    DokumenttiKuvaDto getDto(Long opsId, Kieli kieli);
    DokumenttiKuvaDto addImage(Long opsId, String tyyppi, Kieli kieli, MultipartFile file) throws IOException;
    ...
    void deleteImage(Long opsId, String tyyppi, Kieli kieli);
}
```

Rajapinnassa ei ole yhtään `@PreAuthorize`-annotaatiota. Kuka tahansa kirjautunut voi lisätä tai poistaa minkä tahansa opetussuunnitelman PDF-dokumentin kuvat (kansikuva, logot), ja ne päätyvät julkaistuun PDF:ään. GET-reitit ovat auki myös kirjautumattomille.

**Korjaus:** lisää `hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')` kirjoittaville metodeille ja `LUKU` lukeville.

---

## 4. Logiikkavirheet

### 4.1 Vuosiluokkakokonaisuuden kopio hävittää kaksi tekstikenttää (V), korkea

`domain/vuosiluokkakokonaisuus/Vuosiluokkakokonaisuus.java:99-104`

```java
this.siirtymaEdellisesta = Tekstiosa.copyOf(other.getSiirtymaEdellisesta());
this.siirtymaSeuraavaan = Tekstiosa.copyOf(other.getSiirtymaEdellisesta());
this.tehtava = Tekstiosa.copyOf(other.getSiirtymaEdellisesta());
```

`siirtymaSeuraavaan` ja `tehtava` saavat `siirtymaEdellisesta`-kentän sisällön. Kopiokonstruktoria kutsutaan `copyOf`-metodin kautta opetussuunnitelman luonnissa pohjasta (`service/ops/impl/OpetussuunnitelmaServiceImpl.java:1287, 1792`) ja kun vuosiluokkakokonaisuus kopioidaan omaksi (`service/ops/impl/VuosiluokkakokonaisuusServiceImpl.java:188`). Jokaisessa kopiossa kahden kentän alkuperäinen sisältö katoaa.

**Korjaus:** `other.getSiirtymaSeuraavaan()` ja `other.getTehtava()`. Selvitä lisäksi tietokannasta, kuinka moneen opetussuunnitelmaan virhe on jo vaikuttanut, ja tarvitaanko korjausajo.

### 4.2 Tilasiirtymät ohittavat julkaisun ja nielevät laittomat siirtymät (V), korkea

`domain/Tila.java:16-33` ja `service/ops/impl/OpetussuunnitelmaServiceImpl.java:2027-2049` (`updateTila`)

```java
VALMIS    -> isPohja ? {LUONNOS, POISTETTU} : {LUONNOS, POISTETTU, JULKAISTU}
POISTETTU -> {LUONNOS, POISTETTU, JULKAISTU}
...
if (tila != ops.getTila() && ops.getTila().mahdollisetSiirtymat(...).contains(tila)) {
    ...
    ops.setTila(tila);
}
... save, muokkaustieto "tapahtuma-opetussuunnitelma-tila-" + tila
```

- `updateTila` (`PUT .../tila/{tila}`, `resource/ops/OpetussuunnitelmaController.java:288-291`) voi asettaa OPS:n tilaan `JULKAISTU` suoraan tiloista `VALMIS` tai `POISTETTU` ilman validointia ja ilman julkaisuriviä. Varsinainen julkaisu tehdään `JulkaisuService.addJulkaisu`-polussa, joka asettaa tilan itse (`service/util/impl/JulkaisuServiceImpl.java:249`).
- Laiton siirtymä ohitetaan hiljaa: metodi tallentaa, kirjaa muokkaustiedon pyydetyllä tilalla ja palauttaa 200.

**Korjaus:** poista `JULKAISTU` `updateTila`-metodin sallituista kohteista, jotta tilan voi asettaa vain julkaisupolun kautta. Jos `POISTETTU -> JULKAISTU` on tarkoitettu palautukseksi, salli se vain, kun OPS:lla on julkaisuja. Heitä `BusinessRuleViolationException` laittomasta siirtymästä.

### 4.3 Pohjan validointi ei tee mitään (V), korkea

`service/ops/impl/ValidointiServiceImpl.java:137-144, 423-425`

```java
if (ops.getTyyppi().equals(Tyyppi.POHJA)) {
    result.add(validoiPohja(ops));
}
return result.stream().filter(validointi -> validointi.getKategoria() != null) ...

private Validointi validoiPohja(Opetussuunnitelma ops) {
    return new Validointi();
}
```

`validoiPohja` palauttaa tyhjän validoinnin ilman kategoriaa, ja se suodatetaan pois. Pohjat voidaan siirtää valmiiksi ilman sisältötarkistuksia.

**Korjaus:** toteuta pohjan tarkistukset (esimerkiksi nimi kaikilla kielillä ja pakolliset tekstikappaleet), tai poista metodi, jos tarkistuksia ei tarvita. Vertaa eperusteen `ValidatorOpas`-virheeseen (tyhjä lista), joka on samaa tyyppiä.

### 4.4 Muut, matala

- `service/ops/impl/OpetussuunnitelmaServiceImpl.java:1174` (`checkValidPohja`): `&` eikä `&&`, joten molemmat puolet evaluoidaan aina. Toiminnallisesti oikein, mutta hauras.

---

## 5. Julkaisuvirta

### 5.1 Tila-tallennuksen itsekutsu ja asynkroninen työ avoimessa transaktiossa (V), korkea

`service/util/impl/JulkaisuServiceImpl.java:74-75, 163-181, 298-301`

```java
@Transactional
public class JulkaisuServiceImpl implements JulkaisuService {
    public void addJulkaisu(Long opsId, UusiJulkaisuDto julkaisuDto) {
        ...
        julkaistuOpetussuunnitelmaTila.setJulkaisutila(JulkaisuTila.KESKEN);
        saveJulkaistuOpetussuunnitelmaTila(julkaistuOpetussuunnitelmaTila);   // ei self-proxya
        self.addJulkaisuAsync(opsId, julkaisuDto);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveJulkaistuOpetussuunnitelmaTila(...)
```

- `saveJulkaistuOpetussuunnitelmaTila` kutsutaan ilman `self`-proxya, joten `REQUIRES_NEW` ei toteudu. `KESKEN`-tila tallentuu vasta, kun ulompi transaktio commitoidaan.
- `addJulkaisuAsync` käynnistyy toisessa säikeessä ennen tätä committia. Asynkroninen säie lukee tilan uudelleen (`getOrCreateTila`) ja voi nähdä vanhan tilan. Jos asynkroninen julkaisu valmistuu ensin, ulompi commit voi palauttaa tilan arvoon `KESKEN`.
- `aktivoiJulkaisu` (rivi 292) tekee saman itsekutsun.
- `addJulkaisu` ei tarkista, onko julkaisu jo tilassa `KESKEN`, joten rinnakkaiset julkaisut ovat mahdollisia. Taulussa on uniikkiehto `opetussuunnitelman_julkaisu_uniq_ops_rev` (`src/main/resources/db/migration/V1_20220309101335__julkaisutaulu_korjaus.sql:36`), joten duplikaattirevisio kaatuu kannassa. Käyttäjä näkee kuitenkin vain geneerisen virheen, ja tila jää arvoon `VIRHE`.

**Korjaus:**

- Käytä `self.saveJulkaistuOpetussuunnitelmaTila(...)` kaikissa kutsuissa.
- Käynnistä asynkroninen työ vasta commitin jälkeen (`TransactionSynchronizationManager.registerSynchronization(... afterCommit ...)` tai `@TransactionalEventListener(phase = AFTER_COMMIT)`).
- Hylkää uusi julkaisu, jos tila on `KESKEN` eikä aikakatkaisu ole ylittynyt.
- Määritä `julkaisuTaskExecutor` (`config/AsyncConfig.java:17-23`) eksplisiittisellä poolin koolla ja jonon pituudella.

### 5.2 Validointivirhe näkyy käyttäjälle tallennusvirheenä (V), korkea

`service/util/impl/JulkaisuServiceImpl.java:203-206, 242-247`

```java
if (validoinnit.stream().anyMatch(v -> CollectionUtils.isNotEmpty(v.getVirheet()))) {
    throw new BusinessRuleViolationException("opetussuunnitelma-ei-validi");
};
...
} catch (Exception e) {
    ...
    throw new BusinessRuleViolationException("julkaisun-tallennus-epaonnistui");
}
```

Validointivirhe kääritään geneeriseksi tallennusvirheeksi, joten käyttöliittymä ei voi kertoa syytä. Poikkeus heitetään lisäksi asynkronisessa säikeessä, jolloin se päätyy vain lokiin.

**Korjaus:** tallenna virhekoodi `JulkaistuOpetussuunnitelmaTila`-olioon, jotta käyttöliittymä voi näyttää sen. Heitä `BusinessRuleViolationException` sellaisenaan.

### 5.3 PDF-virheet nielaistaan, keskitaso

`service/util/impl/JulkaisuServiceImpl.java:232-238`: `DokumenttiException` lokitetaan, ja julkaisu merkitään onnistuneeksi ilman PDF:ää. Päätä tuoteomistajan kanssa, pitääkö julkaisun epäonnistua. Vähintään puuttuva dokumentti pitää merkitä näkyviin.

### 5.4 `DokumenttiServiceImpl.query` kirjoittaa read-only-transaktiossa (V), keskitaso

`service/dokumentti/impl/DokumenttiServiceImpl.java:177-190`: `@Transactional(readOnly = true)`, mutta aikakatkaisun tila tallennetaan `dokumenttiStateService.save(dto)`-kutsulla. `dokumentti` voi myös olla `null`, jolloin `dto.getOpsId()` kaatuu lokitusrivillä. **Korjaus:** tallenna tila erillisessä `REQUIRES_NEW`-transaktiossa tai ajastetussa siivouksessa (`cleanStuckPrintings` on jo olemassa).

---

## 6. NullPointerException-riskit

| Sijainti | Ongelma | Korjaus |
|---|---|---|
| `service/util/impl/JulkaisuServiceImpl.java:257-267` (V) | `aktivoiJulkaisu`: `vanhaJulkaisu` voi olla `null` | `assertExists` |
| `service/util/impl/JulkaisuServiceImpl.java:172, 323-326` | `isValidTiedote(null)` kaatuu | `if (tiedote == null) return true;` tai vaadi tiedote |
| `service/ops/impl/OppiaineServiceImpl.java:1026` | `if (!oppiaineet.isOma(...))`: repository palauttaa `Boolean`, joka voi olla `null` | `Boolean.TRUE.equals(...)` |
| `service/ops/impl/OppiaineServiceImpl.java:1052-1054` | `.getTavoite(...).get().getOpetuksenkeskeinenSisaltoalueById(...).get()` | `orElseThrow(NotExistsException::new)` |
| `service/dokumentti/impl/DokumenttiServiceImpl.java:180-183` (V) | `query`: `dokumentti` voi olla `null` ennen lokitusta | `null`-tarkistus |
| `service/external/impl/KoodistoServiceImpl.java:63-65` | `Arrays.stream(koodistot)`, kun vastaus voi olla `null` | `null`-tarkistus |
| `repository/CustomJpaRepository.java:11-13` | `findOne` palauttaa `null`, mikä on edellisten päälähde | `findByIdOrThrow`-apumetodi (kohta 12) |

---

## 7. Tietoturva

### 7.1 Suodatinketjun asetukset, korkea

`config/WebSecurityConfiguration.java:98-101, 141-149`

- `.headers(AbstractHttpConfigurer::disable)` ja `.csrf(AbstractHttpConfigurer::disable)`: ilman CSRF-suojausta toinen sivusto voi tehdä kirjautuneen käyttäjän nimissä tilaa muuttavia pyyntöjä. Myös `X-Frame-Options`, `X-Content-Type-Options` ja HSTS kytkeytyvät pois.
- `ticketValidator.setAcceptAnyProxy(true)`: mikä tahansa CAS-asiakas voi hankkia proxy-tiketin tähän palveluun.
- `GET /api/**` on `permitAll()`: GET-reittien suojaus nojaa kokonaan metoditason annotaatioihin (ks. 3.2, jossa annotaatiot puuttuvat).
- Tilaa muuttavat GET-reitit: `resource/hallinta/EraAjoController.java:22-25` ja `resource/hallinta/MaintenanceController.java:38-57`. Ne on suojattu ylläpitäjän oikeudella, mutta CSRF:n puuttuessa ylläpitäjän istunnolla voi laukaista ne upotetulla kuvalla tai linkillä.

**Korjaus:** CSRF käyttöön (`CookieCsrfTokenRepository.withHttpOnlyFalse()`, käyttöliittymä lähettää `X-XSRF-TOKEN`-otsakkeen), otsakkeiden oletukset takaisin, `setAllowedProxyChains`, oletuksena kielletty suodatinketju ja eksplisiittinen lista julkisista reiteistä, sekä tilaa muuttavat GET-reitit POST-metodiin.

### 7.2 Luonnosten PDF:t näkyvät kaikille kirjautuneille (V), korkea

`service/dokumentti/impl/DokumenttiServiceImpl.java:163-175`

```java
return ops.getTila().equals(Tila.JULKAISTU) || ops.isEsikatseltavissa() || !name.equals("anonymousUser");
```

`hasPermission` palauttaa `true` jokaiselle kirjautuneelle, joten kuka tahansa virkailija voi ladata minkä tahansa organisaation keskeneräisen opetussuunnitelman PDF:n dokumentin id:llä. `get` ja `hasPermission` ovat rajapinnassa `permitAll()`.

**Korjaus:** korvaa viimeinen ehto `permissionManager.hasPermission(..., dokumentti.getOpsId(), OPETUSSUUNNITELMA, LUKU)`-kutsulla.

### 7.3 Liitteet ja termistö: `or isAuthenticated()` (V), korkea

- `service/ops/LiiteService.java:17-29`: `get`, `getAll`, `export` ja `exportLiitePerusteelta` hyväksyvät `hasPermission(#opsId, ..., 'LUKU') or isAuthenticated()`. Kuka tahansa kirjautunut voi listata ja ladata minkä tahansa opetussuunnitelman kuvat ja liitteet.
- `service/ops/TermistoService.java:14-15`: sama ehto termistön lukemiselle.

**Korjaus:** poista `or isAuthenticated()`. `PermissionManagerImpl` (rivit 54-61) antaa jo LUKU-oikeuden kaikille julkaistuihin ja esikatseltaviin opetussuunnitelmiin, joten julkinen käyttö ei rikkoudu.

### 7.4 Kommentit, keskitaso

`service/teksti/KommenttiService.java:22-45` ja `service/teksti/impl/KommenttiServiceImpl.java:106-146`

- (V) `get`, `getAllByParent` ja `getAllByYlin` vaativat vain kirjautumisen. Kommentin tai ketjun id:llä voi lukea minkä tahansa opetussuunnitelman sisäiset kommentit.
- (V) `update` kutsuu `assertRights(kommentti, Permission.LUKU)`. Jos käyttäjä ei ole kirjoittaja, riittää LUKU-oikeus, ja `PermissionManagerImpl` antaa sen kaikille julkaistuihin opetussuunnitelmiin. Kuka tahansa kirjautunut voi siis muokata muiden kommentteja julkaistussa opetussuunnitelmassa. `delete` vaatii `LUONTI`-oikeuden.
- `service/ops/Kommentti2019Service.java:13-14` ja `Kommentti2019ServiceImpl.java:174-177`: `get` ei kutsu `hasOpsPermissions`-tarkistusta.

**Korjaus:** vaadi lukumetodeille `hasPermission(..., 'opetussuunnitelma', 'LUKU')` kommentin opetussuunnitelman kautta. Vaadi päivitykselle kirjoittajuus tai `MUOKKAUS`-oikeus.

### 7.5 Palvelunestoriskit ja syötteet, keskitaso

- **Kuvien skaalaus:** `resource/ops/LiitetiedostoController.java:80-81, 115-118`. Asiakkaan antamille `width`- ja `height`-arvoille ei ole ylärajaa. Aseta yläraja (esimerkiksi 4000 px).
- **Sivukoko:** `resource/ops/OpetussuunnitelmaController.java:82-99` (`getSivutettu`) ilman ylärajaa. Julkinen haku rajaa jo 50:een (`OpetussuunnitelmaJulkaistuQuery`); käytä samaa rajaa.
- **`Content-Disposition`:** `resource/dokumentti/DokumenttiController.java:100-108` liittää opetussuunnitelman nimen otsakkeeseen sellaisenaan. Lainausmerkki tai rivinvaihto rikkoo otsakkeen. Käytä `ContentDisposition.attachment().filename(nimi, UTF_8)`.
- **Julkaisun sisältöhaku:** `resource/ops/OpetussuunnitelmaController.java:149-156` välittää `query`-parametrin polkuna julkaistuun JSON-dataan (`repository/impl/JulkaisuRepositoryImpl.java:26-32`, `#> CAST(:query AS text[])`). Parametri on sidottu, joten SQL-injektiota ei ole, ja data on julkaistua. Validoi polku silti (esimerkiksi `[\w,.]+`), jotta virheellinen syöte ei aiheuta 500-virhettä.

### 7.6 Muut, matala

- **HTML-sanitointi:** `domain/validation/ValidHtml.java:44-56`. `href`- ja `src`-attribuuteille ei rajata protokollia. Lisää `addProtocols("a", "href", "http", "https", "mailto")` ja vastaava `img`-elementin `src`-attribuutille.
- **`PermissionManagerLocal`:** `service/security/PermissionManagerLocal.java:8-14` sallii kaiken. Kaada käynnistys, jos se on aktiivinen muussa kuin `local`-profiilissa.
- **Dev-konfiguraatio:** `config/WebSecurityConfigurationDev.java:41-42` avaa PDF- ja arkistointireitit ilman kirjautumista. `PoistettuPerusteService` sallii `@profileService.isDevProfileActive()`-ehdolla. Hyväksyttävää vain paikallisesti.

---

## 8. Integraatiot ja taustatyöt

### 8.1 HTTP-asiakkaat ja välimuisti, korkea

- **Aikakatkaisut puuttuvat:** `new RestTemplate()` ilman aikakatkaisuja kohdissa `service/external/impl/EperusteetServiceImpl.java:102`, `KoodistoServiceImpl.java:42` ja `LokalisointiServiceImpl.java:38, 81`. `RestClientFactoryImpl` asettaa `OphHttpClient`-asiakkaalle 60 sekunnin aikakatkaisun; käytä samaa kaikkialla.
- **Tuotanto-URL:t oletuksina:** `KoodistoServiceImpl.java:28` ja `LokalisointiServiceImpl.java:26`. Väärin konfiguroitu ympäristö kutsuu hiljaa tuotantoa. Poista oletukset.
- **Heittomerkit oletusarvoissa (V):** esimerkiksi `service/util/RestClientFactoryImpl.java:19-26` (`@Value("${...oph_username:''}")`), `EperusteetServiceImpl.java:69-72`, `KayttajaClientImpl.java:31`, `ExternalPdfServiceImpl.java:36` ja `OrganisaatioServiceImpl.java:69-107`. Spring tulkitsee oletusarvon kirjaimellisesti merkkijonoksi `''`. Puuttuva asetus tuottaa siksi rikkinäisen URL:n tai tunnuksen eikä kaada käynnistystä. Käytä muotoa `${prop:}` ja validoi pakolliset asetukset käynnistyksessä.
- **`RestClientFactoryImpl`-välimuistin avain ei sisällä CAS-tietoa (V):** `service/util/RestClientFactoryImpl.java:38-63`. Välimuistin avain on pelkkä palvelun URL, joten ensimmäinen `get(url, false)`-kutsu määrää, käyttääkö saman URL:n asiakas CAS-tunnistautumista jatkossakin. Käytä avaimena `service + requireCas`.
- **Virhetulokset välimuistiin:** `KayttajaClientImpl.java:42-67` palauttaa 401- ja 403-vastauksissa tyhjän `KayttajanTietoDto(oid)`-olion, joka välimuistitetaan (`kayttajat`, TTL 1 h). `OrganisaatioServiceImpl.java:139-157` (`organisaatiot`), `OrganisaatioServiceImpl.java:174-195` (`getOrganisaatioChildOids`) ja `LokalisointiServiceImpl.java:36-55` tallentavat virheen sattuessa `null`-arvon tai tyhjän tuloksen välimuistiin. Lisää `unless = "#result == null || #result.isEmpty()"`, äläkä välimuistita virhetilanteiden tuloksia.
- **N+1-HTTP-kutsut:** `OpetussuunnitelmaServiceImpl.java:606-618, 1013-1031` (tilastot, organisaatio- ja kuntanimet opetussuunnitelma kerrallaan), `KayttajanTietoServiceImpl.java:119-137` ja `OrganisaatioServiceImpl.java:394-404`. Hae erissä tai rinnakkain.
- **Hiljainen katkaisu:** `EperusteetServiceImpl.java:179-182` hakee perusteet kiinteällä `sivukoko=100`-arvolla ilman sivutusta. Jos perusteita on yli 100 koulutustyyppiä kohden, lista jää vajaaksi ilman virhettä.
- **URL-koodaus puuttuu:** `LokalisointiServiceImpl.java:39, 82-84`, `KoodistoServiceImpl.java:60, 79, 100, 107` ja `OrganisaatioServiceImpl.java:142, 221-225`. Käytä `UriComponentsBuilder`-luokkaa, kuten `OrganisaatioServiceImpl.java:180-183` jo tekee.

### 8.2 Ajastetut tehtävät, korkea

`service/scheduled/task/AbstractScheduledTask.java:27-44`, `service/scheduled/SkeduloituajoService.java:20-54`, `config/ScheduledConfiguration.java:51-63`

- Tila luetaan, tarkistetaan ja päivitetään erillisinä operaatioina ilman lukitusta. Kaksi solmua tai kaksi `EraAjoController`-pyyntöä voi käynnistää saman ajon.
- Kaatuneen ajon `AJOSSA`-tila vapautuu vasta kuuden tunnin jälkeen.
- `cleanOphSession` ja `fixStuckPrintings` ajetaan jokaisella solmulla.
- Virheet kirjataan tasolla `debug` (rivit 60-61).

**Korjaus:** ShedLock (JDBC) tai atominen varaus (`UPDATE ... SET status='AJOSSA' WHERE nimi=? AND (status<>'AJOSSA' OR aloitettu < now() - interval ...)`). Lokitaso `error`-tasoksi.

---

## 9. Domain-malli ja persistointi

### 9.1 equals/hashCode, keskitaso

- `domain/oppiaine/Oppiaine.java:508-528`: `equals` ja `hashCode` perustuvat `getId()`-arvoon. Ennen tallennusta id on `null`, ja tallennuksen jälkeen hash muuttuu. Joukkoon ennen tallennusta lisätty oppiaine "katoaa" joukosta. Käytä luokkakohtaista vakio-`hashCode`-arvoa ja `id != null && id.equals(o.id)`-vertailua.
- `domain/vuosiluokkakokonaisuus/Vuosiluokkakokonaisuus.java:162-168` käyttää tarkoituksella identiteettivertailua (Hibernate-kiertotie). Kirjaa syy kommenttiin, jos sitä ei ole.

### 9.2 Kaskadit ja haut, keskitaso

- `CascadeType.ALL` ilman `orphanRemoval`-asetusta: `domain/teksti/TekstiKappaleViite.java:72`, `domain/ops/Aihekokonaisuudet.java:75`, `domain/vuosiluokkakokonaisuus/Vuosiluokkakokonaisuus.java:56, 66`. Kahdessa viimeisessä kaskadi on `@ManyToOne`-liitoksella, jolloin lapsen poisto voi kaskadoitua jaettuun vanhempaan. Käytä `@ManyToOne`-liitoksilla korkeintaan `PERSIST`/`MERGE`-kaskadia.
- EAGER-hakuja on 5: `LokalisoituTeksti.java:51, 55` (jokaisen tekstin lataus), `Lops2019Opintojakso.java:96, 104` ja `OpetussuunnitelmanMuokkaustieto.java:76`. `LokalisoituTeksti` on kuumin polku; mittaa ennen muutosta.

### 9.3 Kopiointi jakaa viitteitä, keskitaso

- (V) `service/ops/impl/OpetussuunnitelmaServiceImpl.java:1160-1162`: kopioitu opetussuunnitelma liittää pohjan `Liite`-entiteetit itseensä (`ops.attachLiite(liite)`). Liitteen poisto tai muutos yhdessä opetussuunnitelmassa vaikuttaa muihin. Varmista, onko jakaminen tarkoituksellista (liitteet ovat muuttumattomia binäärejä). Jos on, estä poisto, kun liitteellä on useita omistajia.
- `domain/teksti/TekstiKappaleViite.java:165` (`kopioiHierarkia`) jakaa `TekstiKappale`-olion.
- `domain/lops2019/PaikallinenLaajaAlainenOsaaminen.java:38` (`copy`) jakaa `kuvaus`-tekstin. `LokalisoituTeksti` on muuttumaton, joten jakaminen on yleensä turvallista, mutta `setKetjut`-metodi muuttaa sitä.

### 9.4 Repository-kerros, keskitaso/matala

- `getOne()`-kutsuja on noin 10, esimerkiksi `OpsPohjanVaihtoImpl.java:45-47, 72`, `OpsPohjaSynkronointiDefaultImpl.java:72, 78`, `Kommentti2019ServiceImpl.java:104, 144`, `PoistoServiceImpl.java:66`, `NavigationBuilderDefaultImpl.java:65` ja `Lops2019OpintojaksoServiceImpl.java:232`. Puuttuva entiteetti huomataan vasta laiskassa latauksessa. Korvaa `findById`- tai `getReferenceById`-kutsulla.
- `repository/liite/impl/LiiteRepositoryImpl.java:24`: `IOUtils.toByteArray(is)` lukee koko tiedoston muistiin. Käytä `BlobProxy.generateProxy(is, length)`.
- `repository/impl/JulkaisuRepositoryImpl.java:38, 40`: tyhjät `catch`-lohkot.

### 9.5 Mappaus, keskitaso

- `service/mapping/LokalisoituTekstiConverter.java:169-175`: asiakkaan antama `dto.getId()` aiheuttaa tietokantahaun. Koodin kommentti tunnistaa riskin. Rajoita tai ohita tuntemattomat id:t.
- `service/mapping/LokalisoituTekstiConverter.java:86-93`: kommenttimerkinnät upotetaan tekstisisältöön `<span kommentti=...>`-elementteinä. Varmista, että arvo koodataan.
- `service/mapping/ReferenceableEntityConverter.java:43-44`: `em.getReference` palauttaa laiskan proxyn, joten puuttuva viite huomataan myöhään.
- Orika on yhä käytössä (`service/mapping/DtoMapperConfig.java`), eikä sitä enää ylläpidetä.

---

## 10. Koodin laatu

Suurimmat luokat (riviä):

| Luokka | Rivit |
|---|---:|
| `service/ops/impl/OpetussuunnitelmaServiceImpl` | 2292 |
| `service/ops/impl/OppiaineServiceImpl` | 1483 |
| `service/aipe/impl/AIPEServiceImpl` | 860 |
| `service/ops/impl/LukioOpetussuunnitelmaServiceImpl` | 696 |
| `service/ops/impl/ValidointiServiceImpl` | 616 |
| `service/ops/impl/TekstiKappaleViiteServiceImpl` | 543 |
| `domain/oppiaine/Oppiaine` | 529 |

Muut mittarit:

- TODO- ja FIXME-merkintöjä 14.
- `printStackTrace`-kutsuja 5: `JsonBType` (kaksi), `OrganisaatioServiceImpl`, `EperusteetLocalService` ja `MaintenanceServiceImpl`. `System.out`-kutsuja ei ole.
- Tyhjiä `catch`-lohkoja 5: `OrganisaatioServiceImpl:293`, `AIPEServiceImpl:630`, `JulkaisuRepositoryImpl:38, 40` ja `KoodistoKoodiDto:31`.
- `java.util.Date`-kenttiä domain-luokissa 20.
- `EperusteetServiceImpl.java:251`: `Throwables.getStackTraceAsString(e)` lasketaan, mutta tulosta ei kirjata lokiin.

---

## 11. Testaus

Nykytila: 38 testitiedostoa ja 136 `@Test`-metodia.

Puutteet ja toimenpiteet:

1. **Oikeustestit puuttuvat kokonaan.** Testeissä käytetty `TestPermissionEvaluator` sallii kaiken kirjautuneelle, joten mikään testi ei tarkista oikeuksia. Lisää integraatiotesti, joka käyttää oikeaa `PermissionManagerImpl`-toteutusta ja käy läpi kaikki reitit kirjautumattomana ja ilman oikeuksia. Testi olisi havainnut kohdat 3.1, 3.2 ja 7.2–7.4.
2. **Domain-logiikka:** yksikkötesti `Vuosiluokkakokonaisuus.copyOf`-metodille, joka varmistaa jokaisen tekstikentän (kohta 4.1).
3. **Julkaisu:** integraatiotestit `JulkaisuServiceImpl.addJulkaisu`- ja `aktivoiJulkaisu`-poluille, mukaan lukien validointivirhe, PDF-virhe ja samanaikainen julkaisu.
4. **Integraatiot:** `EperusteetServiceImpl`, `OrganisaatioServiceImpl`, `KoodistoServiceImpl`, `LokalisointiServiceImpl`, `KayttajaClientImpl` ja `ExternalPdfServiceImpl` on testattu vain mockeilla. Lisää WireMock-testit virhevastauksille ja välimuistikäytökselle.
5. **Ajastukset:** `AbstractScheduledTask` ja `SkeduloituajoService` ilman testejä.
6. **Ohitetut testit:** `SmokeTestIT` (koko luokka), `TekstiKappaleViiteServiceIT:90`, `OppiaineServiceIT:116` ja `OpetussuunnitelmaHierarkiaKopiointiServiceIT:102` (FIXME). Korjaa tai poista.
7. **Assertiot puuttuvat:** `VuosiluokkakokonaisuusServiceIT.java:50-61` (`crudTest`) kutsuu metodeja tarkistamatta tulosta.

---

## 12. Parannusideat

- **Yhteiset korjaukset sisarpalveluiden kanssa.** PDF-takaisinkutsujen palvelutunnistus, suodatinketjun oletuskielto, CSRF, `RestClientFactoryImpl`-välimuistin avain, jaettu HTTP-asiakas aikakatkaisuilla ja ShedLock kannattaa toteuttaa kerran `eperusteet-backend-utils`-kirjastoon ja ottaa käyttöön kaikissa kolmessa palvelussa.
- **`findByIdOrThrow`** `CustomJpaRepository`-rajapintaan, ja vaiheittainen `findOne`-kutsujen korvaus.
- **Julkaisun tilakone** yhteen paikkaan: tilat `JULKAISEMATON`, `KESKEN`, `JULKAISTU` ja `VIRHE`, asynkroninen työ commitin jälkeen, KESKEN-esto ja virhekoodi käyttöliittymälle.
- **Tilasiirtymät yhteen paikkaan.** `Tila.mahdollisetSiirtymat` sallii nyt siirtymiä, jotka pitäisi tehdä vain julkaisupolun kautta. Erota käyttäjän tekemät siirtymät järjestelmän tekemistä.
- **Staattinen analyysi** (Error Prone tai SpotBugs) CI:hin tunnistamaan tyhjät `catch`-lohkot, `printStackTrace`-kutsut ja `Boolean`-unboxingin.
- **Mappaus:** vaiheittainen siirtymä Orikasta MapStructiin.
- **Suurten luokkien jako:** `OpetussuunnitelmaServiceImpl` vastuualueittain (luonti ja kopiointi, tilat, haku, tilastot).

---

## 13. Tarkistetut väitteet, jotka eivät ole ongelmia

- **Ajastettujen tehtävien käynnistys ilman kirjautumista.** Toisin kuin eperusteessa, `resource/hallinta/EraAjoController.java` on suojattu `@PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")`-annotaatiolla. Maintenance-toiminnot on suojattu samalla oikeudella, `clearOpetussuunnitelmaCaches` lukuun ottamatta (`isAuthenticated()`).
- **Käänteinen oikeustarkistus.** Eperusteen `getKaikkiSisalto`-virhettä vastaavaa ehtoa ei löytynyt. `PermissionManagerImpl` antaa LUKU-oikeuden julkaistuihin tarkoituksella.
- **Julkaisun revisioiden duplikaatit.** Taulussa on uniikkiehto `opetussuunnitelman_julkaisu_uniq_ops_rev`, joten duplikaatit estyvät kannassa (toisin kuin eperusteessa ja amosaassa).
- **`OppiaineOpsTunniste`-luokan equals/hashCode** (`domain/oppiaine/OppiaineOpsTunniste.java:24-48`). `equals` vertaa kieltä KX-tapauksessa, mutta `hashCode` ei. Tämä on sallittua: yhtä suurilla olioilla on aina sama `tunniste` ja `kieliKoodiArvo`, joten niiden hash on sama.
- **CAS-palvelun URL eroaa kutsuosoitteesta** (`service/external/impl/EperusteetServiceImpl.java:335-336`). CAS-tiketti haetaan julkiselle palvelun osoitteelle ja kutsu tehdään sisäverkon osoitteeseen. Tämä on OPH-palveluiden tavanomainen malli (sama kuin eperusteen `YlopsClientImpl`-luokassa).
- **Async-säikeen tietoturvakonteksti.** `AsyncConfig`, `DokumenttiAsyncConfig` ja `DefaultConfigs` käyttävät `DelegatingSecurityContextAsyncTaskExecutor`-luokkaa.
- **Julkinen sivukoko** on rajattu 50:een (`OpetussuunnitelmaJulkaistuQuery`).
- **`TekstiKappaleViiteServiceImpl`:** luokka on `readOnly = true`, mutta kirjoittavilla metodeilla on oma `readOnly = false`.
- **Lops2019 `copyFrom`** tekee syväkopion opintojaksoista ja oppiaineista.
- **`addValinnainen`:** `findFirst().get()` on suojattu `isPresent()`-tarkistuksella.
- **Liitteiden ja dokumenttien binäärit** (`Liite.data`, `Dokumentti.data`, `DokumenttiKuva`) ladataan laiskasti.
- **S3 ja tiedostonimet:** palvelu ei käytä S3:a, joten eperusteen Lampi-viennin tiedostonimiongelmaa ei ole.
