(function () {
    angular.module('aphrodite', [
        'ui.router',                    // Routing
        'oc.lazyLoad',                  // ocLazyLoad
        'ui.bootstrap',                 // Ui Bootstrap
        'ngIdle'                       // Idle timer
    ])
})();

// Other libraries are loaded dynamically in the config.js file using the library ocLazyLoad