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
    .service("Utils", function($window, Kieli, Kaanna) {
        this.scrollTo = function(selector, offset) {
            var element = angular.element(selector);
            if (element.length) {
                $window.scrollTo(0, element.eq(0).offset().top + (offset || 0));
            }
        };

        this.perusteFilter = (perusteet: any[]) => _.chain(perusteet)
            .filter((peruste) => peruste.tila === "poistettu") // Poistettuja ei voi käyttää
            .filter((peruste) => peruste.toteutus !== "lops2019") // lops2019 tuki vain uudessa työkalussa
            .value();

        this.opsFilter = (opetussuunnitelmat: any[]) => _.chain(opetussuunnitelmat)
            .filter((ops) => ops.tila !== "poistettu") // Poistettuja ei voi käyttää
            .filter((ops) => ops.toteutus !== "lops2019") // lops2019 tuki vain uudessa työkalussa
            .value();

        this.hasLocalizedText = function(field) {
            if (!_.isObject(field)) {
                return false;
            }
            var hasContent = false;
            var langs = _.values(Kieli.SISALTOKIELET);
            _.each(langs, function(key) {
                if (!_.isEmpty(field[key])) {
                    hasContent = true;
                }
            });
            return hasContent;
        };

        this.compareLocalizedText = function(t1, t2) {
            var langs = _.values(Kieli.SISALTOKIELET);
            return _.isEqual(_.pick(t1, langs), _.pick(t2, langs));
        };

        this.supportsFileReader = function() {
            return !_.isUndefined($window.FormData);
        };

        this.sort = function(item) {
            return Kaanna.kaanna(item.nimi, false, true).toLowerCase();
        };

        this.nameSort = function(item, key) {
            return Kaanna.kaanna(key ? item[key] : item.nimi).toLowerCase();
        };
    })
    /* Easily clone/restore object with specific keys. */
    .service("CloneHelper", function() {
        function CloneHelperImpl(keys) {
            this.keys = keys;
            this.stash = {};
        }
        CloneHelperImpl.prototype.clone = function(source, destination) {
            var dest = destination || this.stash;
            var src = source || this.stash;
            _.each(this.keys, function(key) {
                dest[key] = _.cloneDeep(src[key]);
            });
        };
        CloneHelperImpl.prototype.restore = function(destination) {
            this.clone(null, destination);
            this.stash = {};
        };
        CloneHelperImpl.prototype.get = function() {
            return this.stash;
        };
        this.init = function(keys) {
            return new CloneHelperImpl(keys);
        };
    })
    /* Shows "back to top" link when scrolled beyond cutoff point */
    .directive("backtotop", function($window, $document, Utils) {
        var CUTOFF_PERCENTAGE = 33;

        return {
            restrict: "AE",
            scope: {},
            template:
                '<div id="backtotop" ng-hide="hidden" title="{{tooltip.label | kaanna}}">' +
                '<a class="action-link" icon-role="arrow-up" ng-click="backToTop()"></a></div>',
            link: function(scope: any) {
                var active = true;
                scope.backToTop = function() {
                    Utils.scrollTo("#ylasivuankkuri");
                };

                scope.hidden = true;
                var window = angular.element($window);
                var document = angular.element($document);
                var scroll = function() {
                    var fitsOnScreen = document.height() <= window.height() * 1.5;
                    var scrollDistance = document.height() - window.height();
                    var inTopArea = window.scrollTop() < scrollDistance * CUTOFF_PERCENTAGE / 100;
                    scope.$apply(function() {
                        scope.hidden = !active || fitsOnScreen || inTopArea;
                    });
                };
                window.on("scroll", scroll);
                // Disable when in edit mode
                scope.$on("enableEditing", function() {
                    active = false;
                });
                scope.$on("disableEditing", function() {
                    active = true;
                });
                scope.$on("$destroy", function() {
                    window.off("scroll", scroll);
                });
            },
            controller: function($scope) {
                $scope.tooltip = {
                    label: "takaisin-ylos"
                };
            }
        };
    })
    /*.directive('ngEnter', function() {
    return function(scope, element, attrs) {
      element.bind('keydown keypress', function(event) {
        if (event.which === 13) {
          scope.$apply(function(){
            scope.$eval(attrs.ngEnter);
          });
          event.preventDefault();
        }
      });
    };
  })*/

    /*.directive('ngEsc', function() {
    return function(scope, element, attrs) {
      element.bind('keydown keypress', function(event) {
        if (event.which === 27) {
          scope.$apply(function(){
            scope.$eval(attrs.ngEsc);
          });
          event.preventDefault();
        }
      });
    };
  })*/

    .directive("dateformatvalidator", function() {
        return {
            restrict: "A",
            require: "ngModel",
            link: function(scope: any, element: any, attrs: any, ngModel: any) {
                var parsedMoment: any = "";

                ngModel.$parsers.unshift(function(viewValue) {
                    return validate(viewValue);
                });

                ngModel.$formatters.unshift(function(viewValue) {
                    return validate(viewValue);
                });

                function validate(viewValue) {
                    if (
                        viewValue instanceof Date ||
                        viewValue === "" ||
                        viewValue === null ||
                        viewValue === undefined
                    ) {
                        ngModel.$setValidity("dateformatvalidator", true);
                        return viewValue;
                    } else if (typeof viewValue === "string") {
                        parsedMoment = moment(viewValue, "D.M.YYYY", true);
                    } else if (typeof viewValue === "number") {
                        parsedMoment = moment(viewValue);
                    } else {
                        ngModel.$setValidity("dateformatvalidator", false);
                        return undefined;
                    }

                    if (parsedMoment.isValid()) {
                        ngModel.$setValidity("dateformatvalidator", true);
                        return viewValue;
                    } else {
                        ngModel.$setValidity("dateformatvalidator", false);
                        return undefined;
                    }
                }
            }
        };
    });
