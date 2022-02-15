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

var ylopsApp = angular.module("ylopsApp", [
    "ngRoute",
    "ngSanitize",
    "ui.router",
    "ngResource",
    "restangular",
    "ngAnimate",
    "pascalprecht.translate",
    "ui.bootstrap",
    "ui.utils",
    "ui.select",
    "ui.tree",
    "ui.sortable",
    "ngFileUpload",
    "eGenericTree",
    "angular-loading-bar",
    "angularFileUpload",
    "ngCookies"
]);

ylopsApp
    .run(function(
        $rootScope,
        $state,
        $window,
        VirheService,
        Editointikontrollit,
        Kaanna,
        Varmistusdialogi,
        paginationConfig
    ) {
        paginationConfig.firstText = "";
        paginationConfig.previousText = "";
        paginationConfig.nextText = "";
        paginationConfig.lastText = "";
        paginationConfig.maxSize = 5;
        paginationConfig.rotate = false;

        $rootScope.$on("$stateChangeError", function(event, toState, toParams, fromState, fromParams, error) {
            var details = {
                to: toState.name,
                from: fromState.name,
                toParams: toParams,
                fromParams: fromParams,
                message: error
            };
            VirheService.virhe({ state: toState.name }, details);
        });

        $rootScope.$on("$stateNotFound", function(event, toState, fromState, fromParams) {
            var details = { to: toState.to, from: fromState.name, fromParams: fromParams };
            VirheService.virhe({ state: toState.to }, details);
        });

        function redirectTo(event, toState, toParams) {
            if (_.isString(toState.redirectTo)) {
                event.preventDefault();
                $state.go(toState.redirectTo, toParams);
            }
        }

        $rootScope.$on("$stateChangeStart", function(event, toState, toParams, fromState, fromParams) {
            $rootScope.lastState = {
                state: _.clone(fromState),
                params: _.clone(fromParams)
            };
            if (
                fromState.name.indexOf("root.opetussuunnitelmat.") !== -1 &&
                fromState.name.indexOf("root.opetussuunnitelmat.") !== -1 &&
                fromParams.id &&
                !toParams.id
            ) {
                // Redirect within opetusuunnitelmat (probably out or to yksi-scope)
                toParams.id = fromParams.id;
            }

            if (Editointikontrollit.getEditMode()) {
                event.preventDefault();
                var data = { toState: toState, toParams: toParams };
                Varmistusdialogi.dialogi({
                    successCb: function(data) {
                        $state.go(data.toState, data.toParams);
                    },
                    data: data,
                    otsikko: "vahvista-liikkuminen",
                    teksti: "tallentamattomia-muutoksia",
                    lisaTeksti: "haluatko-jatkaa",
                    primaryBtn: "poistu-sivulta"
                })();
            } else {
                redirectTo(event, toState, toParams);
            }
        });

        $window.addEventListener("beforeunload", function(event) {
            if (Editointikontrollit.getEditMode()) {
                var confirmationMessage = Kaanna.kaanna("tallentamattomia-muutoksia");
                (event || window.event).returnValue = confirmationMessage;
                return confirmationMessage;
            }
        });
    })

    .config($httpProvider => {
        $httpProvider.defaults.headers.common["X-Requested-With"] = "XMLHttpRequest";
        $httpProvider.defaults.headers.common["Caller-Id"] = "1.2.246.562.10.00000000001.eperusteet-ylops";
        $httpProvider.defaults.xsrfHeaderName = "CSRF";
        $httpProvider.defaults.xsrfCookieName = "CSRF";
    })

    .config(function($tooltipProvider, cfpLoadingBarProvider) {
        $tooltipProvider.setTriggers({
            mouseenter: "mouseleave",
            click: "click",
            focus: "blur",
            never: "mouseleave",
            show: "hide"
        });
        $tooltipProvider.options({
            popupDelay: 500,
            placement: "bottom"
        });
        cfpLoadingBarProvider.parentSelector = "#loading-bar-container";
        cfpLoadingBarProvider.includeSpinner = true;
        cfpLoadingBarProvider.latencyThreshold = 100;
    })
    .run(function() {
        _.mixin({
            deepFlatten: function flattestMap(obj, accessor, op, depth) {
                depth = depth || 0;
                if (_.isArray(obj)) {
                    return _.map(obj, _.partial(flattestMap, _, accessor, op, depth));
                } else if (_.isObject(obj)) {
                    return [op(obj, depth)].concat(
                        _(accessor(obj))
                            .map(_.partial(flattestMap, _, accessor, op, depth + 1))
                            .value()
                    );
                } else {
                    return [];
                }
            },
            print: array => {
                _.each(array, (v, k) => {
                    if (k) {
                        console.log(k, v);
                    } else {
                        console.log(v);
                    }
                });
                return array;
            },
            mapValue: (array, f = _.noop) => {
                var obj = {};
                _.each(array, key => {
                    obj[key] = f(key);
                });
                return obj;
            },
            // TODO: Deprekoitunut, vaihda indexBy:hin
            zipBy: function(array, kfield, vfield) {
                if (_.isArray(array) && kfield) {
                    if (vfield) {
                        return _.zipObject(_.map(array, kfield), _.map(array, vfield));
                    } else {
                        return _.zipObject(_.map(array, kfield), array);
                    }
                } else {
                    return {};
                }
            },
            equals: function(expected, field) {
                return function(given) {
                    return field ? given[field] === expected : given === expected;
                };
            },
            flattenTree: function(obj, extractChildren) {
                if (!obj) {
                    return [];
                }
                if (!_.isArray(obj) && obj) {
                    obj = [obj];
                }
                if (_.isEmpty(obj)) {
                    return [];
                }
                return _.union(
                    obj,
                    _(obj)
                        .map(function(o) {
                            return _.flattenTree(extractChildren(o), extractChildren);
                        })
                        .flatten()
                        .value()
                );
            },
            reducedIndexOf: function(obj, extractor, combinator) {
                if (!_.isArray(obj)) {
                    obj = [obj];
                }
                var results = {};
                _.each(obj, function(o) {
                    var index = extractor(o);
                    if (results[index]) {
                        results[index] = combinator(results[index], o);
                    } else {
                        results[index] = o;
                    }
                });
                return results;
            }
        });
    });
