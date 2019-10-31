# ePerusteet-ylops

Yleissivistävän koulutuksen paikallisten opetussuunnitelmien laadintatyökalu.

## Kehitysympäristön vaatimukset

- JDK 8
- Maven 3
- [käyttäjäkohtaisien asetuksien pohjat - dev-settings](https://github.com/Opetushallitus/eperusteet/blob/master/dev-settings.md)    
- Nodejs sekä yo front-end-kehitystä varten
  - <http://nodejs.org/download/>
  - (sudo) npm -g install yo
  - Asenna riippupvuudet, jos puuttuvat 
    - (sudo) npm -g install bower
    - (sudo) npm -g install grunt-cli

Riippuvuuksien takia käännösaikana tarvitaan pääsy sisäiseen pakettien hallintaan, koska osa paketeista (lähinnä build-parent) ei ole julkisissa repoissa.

Ajoaikana riippuu mm. keskitetystä autentikaatiosta (CAS), käyttäjähallinnasta, organisaatiopalvelusta, koodistosta ja eperusteista joihin täytyy olla ajoympäristöstä pääsy.


## Ajaminen paikallisesti
----------------------

### eperusteet-ylops-service: 

  #### &nbsp;&nbsp;Käynnistys

  ```
  cd eperusteet-ylops-service
  mvn tomcat7:run
  ```  
    
  #### &nbsp;&nbsp;Testaus

  ```
  cd eperusteet-ylops-service
  mvn clean install -Poph
  ```

  #### &nbsp;&nbsp;API-generointi

  ```
  cd eperusteet-ylops-service
  mvn clean compile -Pgenerate-openapi
  specfile="$YLOPS_SERVICE_DIR/target/openapi/ylops.spec.json"
  npx openapi-generator generate -c ../../generator.config.json -i "$specfile" -g typescript-axios
  ```  

### eperusteet-ylops-app: 

  #### &nbsp;&nbsp;Käynnistys

    cd eperusteet-ylops-app/yo
    npm install
    npm run dev

  #### &nbsp;&nbsp;Testaus

  ```
  cd eperusteet-ylops-app/yo
  npm run test
  ```  
    
### Tietokannat (vaihtoehtoinen)
  
  #### &nbsp;&nbsp;Käynnistys

  docker-compose.yml tiedosto talteen ( [käyttäjäkohtaisien asetuksien pohjat - dev-settings](https://github.com/Opetushallitus/eperusteet/blob/master/dev-settings.md) )

  ```
  docker-compose up
  ```

## ePerusteet-projektit

  Projekti | Build status | Maintainability | Test Coverage | Known Vulnerabilities
  -------- | ------------ | --------------- | ------------- | ----------------------
  [ePerusteet](https://github.com/Opetushallitus/eperusteet) | [![Build Status](https://travis-ci.org/Opetushallitus/eperusteet.svg?branch=master)](https://travis-ci.org/Opetushallitus/eperusteet)
  [ePerusteet-amosaa](https://github.com/Opetushallitus/eperusteet-amosaa) | [![Build Status](https://travis-ci.org/Opetushallitus/eperusteet-amosaa.svg?branch=master)](https://travis-ci.org/Opetushallitus/eperusteet-amosaa)
  [ePerusteet-ylops](https://github.com/Opetushallitus/eperusteet-ylops) | [![Build Status](https://travis-ci.org/Opetushallitus/eperusteet-ylops.svg?branch=master)](https://travis-ci.org/Opetushallitus/eperusteet-ylops)
  [ePerusteet-ylops-lukio](https://github.com/Opetushallitus/eperusteet-ylops-lukio) | [![Build Status](https://travis-ci.org/Opetushallitus/eperusteet-ylops-lukio.svg?branch=master)](https://travis-ci.org/Opetushallitus/eperusteet-ylops-lukio) | [![Maintainability](https://api.codeclimate.com/v1/badges/eea9e59302df6e343d57/maintainability)](https://codeclimate.com/github/Opetushallitus/eperusteet-ylops-lukio/maintainability) | [![Test Coverage](https://api.codeclimate.com/v1/badges/eea9e59302df6e343d57/test_coverage)](https://codeclimate.com/github/Opetushallitus/eperusteet-ylops-lukio/test_coverage) | [![Known Vulnerabilities](https://snyk.io/test/github/Opetushallitus/eperusteet-ylops-lukio/badge.svg)](https://snyk.io/test/github/Opetushallitus/eperusteet-ylops-lukio)
  [ePerusteet-opintopolku](https://github.com/Opetushallitus/eperusteet-opintopolku) | [![Build Status](https://travis-ci.org/Opetushallitus/eperusteet-opintopolku.svg?branch=master)](https://travis-ci.org/Opetushallitus/eperusteet-opintopolku) | [![Maintainability](https://api.codeclimate.com/v1/badges/24fc0c3e2b968b432319/maintainability)](https://codeclimate.com/github/Opetushallitus/eperusteet-opintopolku/maintainability) | [![Test Coverage](https://api.codeclimate.com/v1/badges/24fc0c3e2b968b432319/test_coverage)](https://codeclimate.com/github/Opetushallitus/eperusteet-opintopolku/test_coverage)
  [ePerusteet-backend-utils](https://github.com/Opetushallitus/eperusteet-backend-utils) | [![Build Status](https://travis-ci.org/Opetushallitus/eperusteet-backend-utils.svg?branch=master)](https://travis-ci.org/Opetushallitus/eperusteet-backend-utils)
  [ePerusteet-frontend-utils](https://github.com/Opetushallitus/eperusteet-frontend-utils) | [![Build Status](https://travis-ci.org/Opetushallitus/eperusteet-frontend-utils.svg?branch=master)](https://travis-ci.org/Opetushallitus/eperusteet-frontend-utils) | [![Maintainability](https://api.codeclimate.com/v1/badges/f782a4a50622ae34a2bd/maintainability)](https://codeclimate.com/github/Opetushallitus/eperusteet-frontend-utils/maintainability) | [![Test Coverage](https://api.codeclimate.com/v1/badges/f782a4a50622ae34a2bd/test_coverage)](https://codeclimate.com/github/Opetushallitus/eperusteet-frontend-utils/test_coverage)
