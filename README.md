ePerusteet-ylops
================


Yleissivistävän koulutuksen paikallisten opetussuunnitelmien laadintatyökalu.

Kehitysympäristön vaatimukset
-----------------------------

- JDK 8
- Maven 3
- Nodejs sekä yo front-end-kehitystä varten
  - <http://nodejs.org/download/>
  - (sudo) npm -g install yo
  - Jos bower ja/tai grunt puuttuvat niin aja myös
    (sudo) npm -g install bower
    (sudo) npm -g install grunt-cli
- PostgreSQL 9.3 (luo tietokanta paikallista kehitystä varten)
- Tomcat [7.0.42,8)

Riippuvuuksien takia käännösaikana tarvitaan pääsy sisäiseen pakettien hallintaan, koska osa paketeista (lähinnä build-parent) ei ole julkisissa repoissa.

Ajoaikana riippuu mm. keskitetystä autentikaatiosta (CAS), käyttäjähallinnasta, organisaatiopalvelusta, koodistosta ja eperusteista joihin täytyy olla ajoympäristöstä pääsy.


Ajaminen paikallisesti
----------------------

eperusteet-ylops-app: 

    cd eperusteet-ylops-app/yo
    npm install
    bower install
    grunt server

eperusteet-ylops-service: 

    mvn install
    cd eperusteet-ylops-service
    (jos muisti loppuu: MAVEN_OPTS="-Xmx2048m")
    mvn tomcat7:run -Deperusteet-ylops.devdb.user=<user> -Deperusteet-ylops.devdb.password=<password> -Deperusteet-ylops.devdb.jdbcurl=<jdbcurl>
    
Sovelluksen voi myös kääntää kahdeksi eri war-paketiksi joita voi aja erillisessä Tomcatissa. 

Konfiguraatioon tarvitaan seuraavat muutokset:

  - URIEncoding="UTF-8": 
```
    <Connector port="8080" protocol="HTTP/1.1" (...) URIEncoding="UTF-8"/>
```
  - PostgreSQL 9.3 JDBC-ajuri lib-hakemistoon
  - Kehityskannan resurssi:    
```
    <GlobalNamingResources>
    ...
    <Resource name="jdbc/eperusteet-ylops" auth="Container" type="javax.sql.DataSource"
                 maxActive="100" maxIdle="30" maxWait="10000"
                 username="..." password="..." driverClassName="org.postgresql.Driver"
                 url="jdbc:postgresql://localhost:5432/..."/>
    ...
    </GlobalNamingResources>
```

Web-sovelluksen (app) osalta maven käyttää yeoman-maven-plugin:ia joka tarvitsee nodejs:n ja yo:n toimiakseen.


# Lukio

```sh

# Lataa perusteita testikäyttöön
$ mkdir -p eperusteet-ylops-service/src/main/resources/fakedata/
$ http "https://eperusteet.opintopolku.fi/eperusteet-service/api/perusteet/1266381/kaikki" > eperusteet-ylops-service/src/main/resources/fakedata/varhaiskasvatus.json

# Käynnistä palvelu e2e-profiililla
$ mvn  tomcat7:run -Dspring.profiles.active=e2e

```

# Uusi käyttöliittymä

Uusi käyttöliittymä rakennetaan kansioon /uusi.
