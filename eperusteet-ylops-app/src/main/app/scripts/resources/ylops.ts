angular.module("ylopsApp").factory("Api", (Restangular, Notifikaatiot, Kaanna) =>
    Restangular.withConfig(config => {
        config.setBaseUrl("/eperusteet-ylops-service/api");
        config.setResponseInterceptor((data, operation, what, url, response, deferred) => {
            if (response && response.status >= 400) {
                if (response.status >= 500) {
                    // fataali(KaannaService.kaanna("jarjestelmavirhe-teksti", {
                    //     virhekoodi: response.status
                    // }), () => {
                    //     // TODO Ota käyttöön myöhemmin
                    //     // $state.go("root.virhe");
                    // });
                } else if (response.data && response.data.syy) {
                    let syy = response.data.syy;
                    Notifikaatiot.varoitus(_.isArray(syy) ? syy[0] : syy);
                } else {
                    Notifikaatiot.varoitus(Kaanna.kaanna("odottamaton-virhe"));
                }
            }
            return data;
        });
    })
);
