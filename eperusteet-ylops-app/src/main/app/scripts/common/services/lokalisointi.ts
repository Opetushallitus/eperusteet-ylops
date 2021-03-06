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
    .service("Lokalisointi", function($http, $q) {
        var translations = {};
        var PREFIX = "localisation/locale-",
            SUFFIX = ".json";

        this.init = function() {
            var deferred = [];
            _.each(["fi", "sv"], function(key) {
                deferred.push(
                    $http({
                        url: PREFIX + key + SUFFIX,
                        method: "GET",
                        params: ""
                    }).success(function(data) {
                        translations[key] = data;
                    })
                );
            });
            return $q.all(deferred);
        };
    })
    .factory("LokalisointiResource", function(LOKALISOINTI_SERVICE_LOC, $resource) {
        return $resource(
            LOKALISOINTI_SERVICE_LOC,
            {},
            {
                get: {
                    method: "GET",
                    isArray: true,
                    cache: true
                }
            }
        );
    })
    .factory("LokalisointiLoader", function($q, $http, LokalisointiResource, $window, $rootScope) {
        var PREFIX = "localisation/locale-",
            SUFFIX = ".json",
            BYPASS_REMOTE = $window.location.host.indexOf("localhost") === 0;
        return function(options) {
            var deferred = $q.defer();
            var translations = {};
            $http({
                url: PREFIX + options.key + SUFFIX,
                method: "GET",
                params: ""
            })
                .success(function(data) {
                    _.extend(translations, data);
                    if (BYPASS_REMOTE) {
                        deferred.resolve(translations);
                        $rootScope.lokalisointiInited = true;
                    } else {
                        LokalisointiResource.get(
                            { locale: options.key },
                            function(res) {
                                var remotes = _.zipObject(_.map(res, "key"), _.map(res, "value"));
                                _.extend(translations, remotes);
                                deferred.resolve(translations);
                                $rootScope.lokalisointiInited = true;
                            },
                            function() {
                                deferred.reject(options.key);
                            }
                        );
                    }
                })
                .error(function() {
                    deferred.reject(options.key);
                });
            return deferred.promise;
        };
    });
