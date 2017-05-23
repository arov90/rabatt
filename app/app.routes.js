app.config(['$routeProvider', '$locationProvider',
  function ($routeProvider, $locationProvider) {
    $routeProvider.when('/', {
      templateUrl: '/app/components/home/homeView.html',
      controller: 'homeController',
    });

    $locationProvider.html5Mode(true);
  },
]);
