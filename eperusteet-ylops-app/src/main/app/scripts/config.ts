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
    .config(function($urlRouterProvider, $sceProvider) {
        $sceProvider.enabled(true);
        $urlRouterProvider.when("", "/");
        $urlRouterProvider.otherwise(function($injector, $location) {
            $injector.get("VirheService").setData({ path: $location.path() });
            $injector.get("$state").go("root.virhe");
        });
    })
    .config(function($translateProvider, $urlRouterProvider) {
        var preferred = "fi";
        $urlRouterProvider.when("/", "/" + preferred);
        $translateProvider.useLoader("LokalisointiLoader");
        $translateProvider.preferredLanguage(preferred);
        moment.locale(preferred);
    })
    .config(function($httpProvider) {
        $httpProvider.defaults.headers.common["X-Requested-With"] = "XMLHttpRequest";
        $httpProvider.defaults.xsrfHeaderName = "CSRF";
        $httpProvider.defaults.xsrfCookieName = "CSRF";

        $httpProvider.interceptors.push([
            "$rootScope",
            "$q",
            "SpinnerService",
            function($rootScope, $q, Spinner) {
                return {
                    request: function(request) {
                        Spinner.enable();
                        return request;
                    },
                    response: function(response) {
                        Spinner.disable();
                        return response || $q.when(response);
                    },
                    responseError: function(error) {
                        Spinner.disable();
                        return $q.reject(error);
                    }
                };
            }
        ]);
    })
    .config(function(uiSelectConfig) {
        uiSelectConfig.theme = "bootstrap";
    });
