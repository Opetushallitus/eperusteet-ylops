# ePerusteet-ylops


[![Build Status](https://github.com/Opetushallitus/eperusteet-ylops/actions/workflows/build.yml/badge.svg)](https://github.com/Opetushallitus/eperusteet-ylops/actions)
[![Build Status](https://github.com/Opetushallitus/eperusteet-ylops-ui/actions/workflows/build.yml/badge.svg)](https://github.com/Opetushallitus/eperusteet-ylops-ui/actions)


## 1. Palvelun tehtävä

Yleissivistävän koulutuksen paikallisten opetussuunnitelmien laadintatyökalu.

## 2. Ohjeet

Palvelu toimii identtisesti [eperusteet](https://github.com/Opetushallitus/eperusteet) palvelun 
kanssa, joten ylläpidetään ohjeita vain yhdessä paikassa. Korvaa ainoastaan 
[eperusteet](https://github.com/Opetushallitus/eperusteet) ohjeissa `eperusteet` sanat
`eperusteet-ylops` sanalla.

Esim. `eperusteet-service` -> `eperusteet-ylops-service`

### Lokaali tietokanta

`local`-profiili käynnistää ylops-kannan Docker Composella, jos se ei ole jo päällä. Compose-tiedosto on [`eperusteet-ylops-service/db/compose.yaml`](eperusteet-ylops-service/db/compose.yaml) (PostgreSQL 15, portti 5434). Docker Desktopin (tai vastaavan Docker-daemonin) on oltava käynnissä.

Kannan tyhjennys ja uudelleenluonti:

```bash
./eperusteet-ylops-service/db/db-reset.sh
```

Flyway-migraatiot ajetaan seuraavalla palvelun käynnistyksellä.
