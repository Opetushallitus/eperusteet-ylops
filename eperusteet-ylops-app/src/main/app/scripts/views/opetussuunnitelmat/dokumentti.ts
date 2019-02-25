/*
* Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
*
* This program is free software: Licensed under the EUPL, Version 1.1 or - as
* soon as they will be approved by the European Commission - subsequent versions
* of the EUPL (the "Licence");
*
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* European Union Public Licence for more details.
*/

ylopsApp
.controller("DokumenttiController", (
    $rootScope,
    $scope,
    $timeout,
    $http,
    $cookies,
    FileUploader,
    Api,
    Kieli,
    opsId
) => {
    $scope.kuva = {};

    $scope.poistaKuva = tyyppi => {
        $http({
            method: "DELETE",
            url: Api.all("dokumentit").getRestangularUrl() + "/kuva?opsId=" + opsId + "&tyyppi=" + tyyppi + "&kieli=" + Kieli.getSisaltokieli()
        }).then(() => {
            $scope.dokumentti[tyyppi] = null;
            paivitaKuva(tyyppi);
        });
    };

    // Kuvien päivitys
    const paivitaKuva = tyyppi => {
        $scope.kuva[tyyppi] =
            Api.all("dokumentit").getRestangularUrl() +
            "/kuva?opsId=" + opsId +
            "&tyyppi=" + tyyppi +
            "&kieli=" + Kieli.getSisaltokieli() +
            "&" + new Date().getTime();
    };

    // Kuvien latauskomponentti
    const createUploader = tyyppi => {
        var uploader = new FileUploader({
            url: Api.all("dokumentit").getRestangularUrl() + "/kuva?opsId=" + opsId + "&tyyppi=" + tyyppi + "&kieli=" + Kieli.getSisaltokieli(),
            headers: {
                CSRF: $cookies.CSRF
            },
            queueLimit: "1",
            removeAfterUpload: true
        });
        uploader.onSuccessItem = (item, res) => {
            $scope.dokumentti = res;
            paivitaKuva(tyyppi);
        };
        return uploader;
    };

    const init = async () => {
        $scope.kansikuvaUploader = createUploader("kansikuva");
        $scope.ylatunnisteUploader = createUploader("ylatunniste");
        $scope.alatunnisteUploader = createUploader("alatunniste");

        paivitaKuva("kansikuva");
        paivitaKuva("ylatunniste");
        paivitaKuva("alatunniste");

        try {
            $scope.dokumentti = await Api.all("dokumentit").customGET("", {
                opsId,
                kieli: Kieli.getSisaltokieli()
            });
        } catch (e) {
            // Not found - NOOP
        }
    };

    init();

});
