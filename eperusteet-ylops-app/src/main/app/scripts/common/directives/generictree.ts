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

interface GenericTreeNode {
    $$nodeParent?: GenericTreeNode;
    $$depth?: number;
    $$hasChildren?: boolean;
    $$collapsed?: boolean;
}
interface VanillaGenericTreeConfig<NodeType extends GenericTreeNode> {
    root: () => IPromise<NodeType>;
    children: (node: NodeType) => NodeType[];
    useUiSortable: () => boolean;
    acceptDrop?: (from: NodeType, to: NodeType) => boolean;
    sortableClass?: (node: NodeType) => string;
    template?: (node: NodeType) => JQuery;
    hidden: (node: NodeType) => boolean;
    collapsed: (node: NodeType) => boolean;
    extension: (node: NodeType, $scope: any) => void;
}
interface SortableConfig {
    connectWith?: string;
    handle?: string;
    placeholder?: string;
    cursorAt?: { top?: number | string; left?: number | string; bottom?: number | string; right?: number | string };
    helper?: string;
    option?: string;
    cursor?: string;
    delay?: number;
    disabled?: boolean;
    tolerance?: string;
    update?: (event: any, ui: any) => void;
}

angular
    .module("eGenericTree", [])
    .directive("genericTreeNode", function($compile, $templateCache) {
        return {
            restrict: "E",
            replace: true,
            template: "",
            scope: {
                node: "=",
                treeProvider: "=",
                uiSortableConfig: "="
            },
            controller: function($scope) {
                $scope.treeProvider.extension($scope.node, $scope);
                $scope.isHidden = function(node) {
                    return $scope.treeProvider.hidden(node);
                };
            },
            link: function(scope: any, element) {
                function setContext(node, children) {
                    node.$$hasChildren = !_.isEmpty(children);
                    _.each(children, function(cnode) {
                        cnode.$$depth = node.$$depth + 1;
                        cnode.$$nodeParent = node;
                    });
                }

                function getTemplate(node) {
                    var template = scope.treeProvider.template ? scope.treeProvider.template(node) : undefined;
                    return $templateCache.get(template) || template || "<pre>{{ node | json }}</pre>";
                }

                var node = scope.node;
                var children = scope.treeProvider.children(node).then(function(children) {
                    setContext(node, children);
                    var template = "";
                    template += getTemplate(node);
                    if (children) {
                        template +=
                            '<div ui-sortable="uiSortableConfig" class="' +
                            scope.treeProvider.sortableClass(node) +
                            ' recursivetree" ng-model="children">';
                        scope.children = children;
                        scope.parentNode = node;
                        if (!_.isEmpty(children)) {
                            template +=
                                "" +
                                '<div ng-repeat="node in children">' +
                                '    <generic-tree-node node="node" ng-show="!isHidden(node)" ui-sortable-config="uiSortableConfig"' +
                                '                       tree-provider="treeProvider"></generic-tree-node>' +
                                "</div>";
                        }
                        template += "</div>";
                    }
                    var templateEl = angular.element(template);
                    if (element.children().length) {
                        angular.element(element.children()[0]).replaceWith(templateEl);
                    } else {
                        element.append(templateEl);
                    }
                    $compile(templateEl)(scope);
                });
            }
        };
    })
    .directive("genericTree", function($compile, $log) {
        return {
            restrict: "E",
            replace: true,
            template: "",
            scope: {
                treeProvider: "=",
                uiSortableConfig: "=?"
            },
            controller: function($scope) {
                function run(provider) {
                    // Sane defaults
                    provider.sortableClass = provider.sortableClass || _.constant("");
                    provider.acceptDrop = provider.acceptDrop || _.constant(true);

                    $scope.tprovider = provider;
                    provider
                        .root()
                        .then(function(root) {
                            $scope.root = root;
                            return provider.children(root);
                        })
                        .then(function(children) {
                            $scope.children = children;
                        })
                        .catch(function(err) {
                            $log.error(err);
                        });
                }

                $scope.treeProvider.then(run).catch(function(err) {
                    $log.error(err);
                });
            },
            link: function(scope: any, element) {
                var setupMinHeightBycontainer = function(el) {
                    var $parent: any = $(el).parent(),
                        height = $parent.outerHeight();
                    $parent.prop("original-min-height", $parent.css("minHeight"));
                    //console.log('setting min height:', height, 'for tree container', $parent);
                    $parent.css("minHeight", height);
                };
                var setupMinHeightForAllGenericTrees = function() {
                    // (including all connected as well cause may be dragged from tree to another)
                    $("generic-tree").each(function() {
                        setupMinHeightBycontainer($(this).parent());
                    });
                };
                var restoreParentHeight = function(el) {
                    var $parent: any = $(el).parent(),
                        height = $parent.prop("original-min-height") || "inherit";
                    //console.log('restoring min height:', height, 'for tree container', $parent);
                    $parent.css("minHeight", height);
                };
                var restoreParentHeightForAllGenericTrees = function() {
                    $("generic-tree").each(function() {
                        restoreParentHeight($(this).parent());
                    });
                };

                function refresh(tree) {
                    if (tree) {
                        _.each(scope.children, function(child) {
                            child.$$nodeParent = undefined;
                            child.$$depth = 0;
                        });

                        scope.sortableConfig = _.merge(
                            {
                                connectWith: ".recursivetree",
                                handle: ".treehandle",
                                cursorAt: { top: 2, left: 2 },
                                helper: "clone",
                                option: "x",
                                cursor: "move",
                                delay: 100,
                                disabled: scope.tprovider.useUiSortable(),
                                tolerance: "pointer",
                                start: function() {
                                    setupMinHeightForAllGenericTrees();
                                },
                                stop: function() {
                                    restoreParentHeightForAllGenericTrees();
                                },
                                update: function(e, ui) {
                                    if (scope.tprovider.acceptDrop) {
                                        var dropTarget = ui.item.sortable.droptarget;
                                        if (dropTarget) {
                                            var listItem = dropTarget.closest(".recursivetree");
                                            var parentScope = listItem ? listItem.scope() : null;
                                            var parentNode =
                                                parentScope && parentScope.node ? parentScope.node : scope.root;
                                            if (
                                                !parentNode ||
                                                !scope.tprovider.acceptDrop(
                                                    ui.item.sortable.model,
                                                    parentNode,
                                                    parentScope,
                                                    e,
                                                    ui
                                                )
                                            ) {
                                                ui.item.sortable.cancel();
                                            }
                                        }
                                    }
                                }
                                // cancel: '.ui-state-disabled'
                            },
                            scope.uiSortableConfig || {}
                        );

                        var templateEl = angular.element(
                            "" +
                                '<div ui-sortable="sortableConfig" class="' +
                                scope.tprovider.sortableClass(scope.root) +
                                ' recursivetree" ng-model="children">' +
                                '    <div ng-repeat="node in children">' +
                                '       <generic-tree-node node="node" ui-sortable-config="sortableConfig" tree-provider="tprovider"></generic-tree-node>' +
                                "    </div>" +
                                "</div>"
                        );
                        if (element.children().length) {
                            angular.element(element.children()[0]).replaceWith(templateEl);
                        } else {
                            element.append(templateEl);
                        }
                        $compile(templateEl)(scope);
                    }
                }

                scope.$on("genericTree:refresh", function() {
                    refresh(scope.children);
                });
                scope.$on("genericTree:beforeChange", function() {
                    setupMinHeightForAllGenericTrees();
                });
                scope.$on("genericTree:afterChange", function() {
                    restoreParentHeightForAllGenericTrees();
                });
                scope.$watch("children", refresh, true);
            }
        };
    })


    .directive("genericTreeVanilla", function($compile, $log, $templateCache) {
        return {
            restrict: "E",
            replace: true,
            template: "",
            scope: {
                treeProvider: "=",
                uiSortableConfig: "=?"
            },
            controller: function($scope) {
                function run(provider) {
                    // Sane defaults
                    provider.sortableClass = provider.sortableClass || _.constant("");
                    provider.acceptDrop = provider.acceptDrop || _.constant(true);
                    $scope.treeProvider.extension(null, $scope);
                    $scope.tprovider = provider;
                    provider
                        .root()
                        .then(function(root) {
                            $scope.root = root;
                            $scope.children = provider.children(root);
                        })
                        .catch(function(err) {
                            $log.error(err);
                        });
                }
                run($scope.treeProvider);
            },
            link: function(scope: any, element) {
                var setupMinHeightBycontainer = function(el) {
                    var $parent: any = $(el).parent(),
                        height = $parent.outerHeight();
                    $parent.prop("original-min-height", $parent.css("minHeight"));
                    //console.log('setting min height:', height, 'for tree container', $parent);
                    $parent.css("minHeight", height);
                };
                var setupMinHeightForAllGenericTrees = function() {
                    // (including all connected as well cause may be dragged from tree to another)
                    $("generic-tree-vanilla").each(function() {
                        setupMinHeightBycontainer($(this).parent());
                    });
                };
                var restoreParentHeight = function(el) {
                    var $parent: any = $(el).parent(),
                        height = $parent.prop("original-min-height") || "inherit";
                    //console.log('restoring min height:', height, 'for tree container', $parent);
                    $parent.css("minHeight", height);
                };
                var restoreParentHeightForAllGenericTrees = function() {
                    $("generic-tree-vanilla").each(function() {
                        restoreParentHeight($(this).parent());
                    });
                };

                function setContext(node, children) {
                    node.$$hasChildren = !_.isEmpty(children);
                    _.each(children, function(cnode) {
                        cnode.$$depth = node.$$depth + 1;
                        cnode.$$nodeParent = node;
                    });
                }
                function buildNode(node) {
                    var $el = $("<generic-tree-node-vanilla></generic-tree-node-vanilla>");
                    $el.prop("relatedNode", node);
                    if (scope.tprovider.hidden(node)) {
                        $el.css("display", "none");
                    }
                    var children = scope.tprovider.children(node);
                    setContext(node, children);
                    $el.append($(scope.tprovider.template(node)));
                    if (children) {
                        var $subSoratable = $(
                            '<div class="' + scope.tprovider.sortableClass(node) + ' recursivetree"></div>'
                        );
                        if (scope.tprovider.collapsed(node)) {
                            $subSoratable.css("display", "none");
                        }
                        (<any>$subSoratable).sortable(
                            angular.extend(
                                {
                                    model: children
                                },
                                scope.sortableConfig
                            )
                        );
                        $subSoratable.prop("sortableModel", children);
                        $el.append($subSoratable);
                        _.each(children, c => {
                            $subSoratable.append($("<div></div>").append(buildNode(c)));
                        });
                    }
                    return $el;
                }
                function updateNode(el) {
                    var $el = $(el),
                        node = $el.prop("relatedNode");
                    $el.replaceWith(buildNode(node));
                }
                function updateVisibility(el) {
                    var $el = $(el),
                        node = $el.prop("relatedNode"),
                        hide = scope.tprovider.hidden(node),
                        $childrenContainer = $el.find(".recursivetree").first(),
                        collapsed = scope.tprovider.collapsed(node);
                    if (hide) {
                        $el.hide();
                    } else {
                        $el.show();
                    }
                    if (_.isEmpty($childrenContainer)) {
                        $childrenContainer.find(".collapse-based").each(() => {
                            var $c: any = $(this),
                                unCollapseClass = $c.attr("data-uncollapse-class"),
                                collapseClass = $c.attr("data-collapse-class");
                            if (collapsed) {
                                $c.addClass(collapseClass).removeClass(unCollapseClass);
                            } else {
                                $c.addClass(unCollapseClass).removeClass(collapseClass);
                            }
                        });
                        if (collapsed) {
                            $childrenContainer.hide();
                        }
                    }
                }

                function refresh(tree) {
                    if (tree) {
                        _.each(scope.children, function(child) {
                            child.$$nodeParent = undefined;
                            child.$$depth = 0;
                        });

                        scope.sortableConfig = _.merge(
                            {
                                connectWith: ".recursivetree",
                                handle: ".treehandle",
                                cursorAt: { top: 2, left: 2 },
                                helper: "clone",
                                option: "x",
                                cursor: "move",
                                delay: 100,
                                disabled: scope.tprovider.useUiSortable(),
                                tolerance: "pointer",
                                start: function(e, ui) {
                                    setupMinHeightForAllGenericTrees();
                                    var $el = $(ui.item.context)
                                            .find("generic-tree-node-vanilla")
                                            .first(),
                                        $parentEl = $(ui.item.parent()[0]),
                                        $parentParent = $parentEl.parent("generic-tree-node-vanilla").first();
                                    ui.item.sortable = {
                                        source: $parentEl,
                                        sourceList: $parentEl.prop("sortableModel"),
                                        sourceNode: _.isEmpty($parentParent)
                                            ? scope.root
                                            : $parentParent.prop("relatedNode"),
                                        node: $el.prop("relatedNode"),
                                        index: ui.item.index()
                                    };
                                },
                                stop: function() {
                                    restoreParentHeightForAllGenericTrees();
                                },
                                update: function(e, ui) {
                                    if (_.isObject(ui.item.sortable)) {
                                        ui.item.sortable.dropindex = ui.item.index();
                                        var $parentEl = $(ui.item.parent()[0]),
                                            $parentParent = $parentEl.parent("generic-tree-node-vanilla").first();
                                        ui.item.sortable.droptarget = $parentEl;
                                        ui.item.sortable.droptargetList = $parentEl.prop("sortableModel");
                                        ui.item.sortable.droptargetNode = _.isEmpty($parentParent)
                                            ? scope.root
                                            : $parentParent.prop("relatedNode");
                                        if (scope.tprovider.acceptDrop) {
                                            if (
                                                !scope.tprovider.acceptDrop(
                                                    ui.item.sortable.node,
                                                    ui.item.sortable.droptargetNode
                                                )
                                            ) {
                                                (<any>$(ui.sender)).sortable("cancel");
                                                ui.item.sortable = true;
                                                //$log.info('cancled');
                                                return;
                                            }
                                        }
                                        scope.$apply(function() {
                                            ui.item.sortable.sourceList.splice(ui.item.sortable.index, 1);
                                            ui.item.sortable.droptargetList.splice(
                                                ui.item.sortable.dropindex,
                                                0,
                                                ui.item.sortable.node
                                            );
                                            setContext(ui.item.sortable.droptargetNode, ui.item.sortable.droptargetList);
                                            setContext(ui.item.sortable.sourceNode, ui.item.sortable.sourceList);
                                            //$log.info('moved', ui.item.sortable.node, ' to ', ui.item.sortable.droptargetList,
                                            //        ' from ', ui.item.sortable.sourceList);
                                            ui.item.sortable = true;
                                        });
                                    }
                                }
                            },
                            scope.uiSortableConfig || {}
                        );

                        var $template = $(
                            '<div class="' +
                                scope.tprovider.sortableClass(scope.root) +
                                ' recursivetree" ui-sortable=""></div>'
                        );
                        (<any>$template).sortable(
                            angular.extend(
                                {
                                    model: scope.children
                                },
                                scope.sortableConfig
                            )
                        );
                        $template.prop("sortableModel", scope.children);
                        _.each(scope.children, c => {
                            $template.append($("<div></div>").append(buildNode(c)));
                        });
                        var templateEl = angular.element($template);
                        //$compile(templateEl)(scope);
                        if (element.children().length) {
                            angular.element(element.children()[0]).replaceWith(templateEl);
                        } else {
                            element.append(templateEl);
                        }
                    }
                }

                // Use this event to efficiently update singe node (or it and tree under it):
                scope.$on("genericTree:updateNode", function(e, el) {
                    if (scope.root) {
                        if (el) {
                            var closest = $(el).closest("generic-tree-node-vanilla");
                            if (!_.isEmpty(closest)) {
                                updateNode(closest);
                            }
                        } else {
                            refresh(scope.children);
                        }
                    }
                });
                // Use this event to update hidden/collapsed states of the whole tree / subtree:
                scope.$on("genericTree:refreshVisibility", function(e, el) {
                    if (scope.root) {
                        if (el) {
                            var closest = $(el).closest("generic-tree-node-vanilla");
                            if (!_.isEmpty(closest)) {
                                updateVisibility(closest);
                            }
                        } else {
                            $(element)
                                .find("generic-tree-node-vanilla")
                                .each(function() {
                                    updateVisibility(this);
                                });
                        }
                    }
                });
                scope.$on("genericTree:updateVisibilityGiven", function(e, on) {
                    if (scope.root && (!on || on(scope.root))) {
                        $(element)
                            .find("generic-tree-node-vanilla")
                            .each(function() {
                                updateVisibility(this);
                            });
                    }
                });
                // Use this to fully refresh the tree:
                scope.$on("genericTree:refresh", function() {
                    refresh(scope.children);
                });
                // Call before an action that might change the height of the tree (e.g. removing from it):
                scope.$on("genericTree:beforeChange", function() {
                    setupMinHeightForAllGenericTrees();
                });
                // Call after an action that might change the height of the tree (e.g. removing from it):
                scope.$on("genericTree:afterChange", function() {
                    restoreParentHeightForAllGenericTrees();
                });
                scope.$watch("children", refresh, true);
            }
        };
    });
