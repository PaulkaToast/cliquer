
var cacheName = 'app';
var filesToCache = [
  './',
  './index.html',
  './manifest.json',
  './static/js/bundle.js'
];

self.addEventListener('install', function(e) {
  e.waitUntil(caches.open(cacheName)
    .then(function(cache) {
      return cache.addAll(filesToCache)
        .then(function() {
          self.skipWaiting();
        });
      }));
});

self.addEventListener('activate', function(e) {
  e.waitUntil(caches.keys()
    .then(function(keyList) {
      return Promise.all(keyList.map(function(key) {
        if (key !== cacheName)
          return caches.delete(key);
      }));
  }));
  return self.clients.claim();
});

self.addEventListener('fetch', function(e) {
  e.respondWith(caches.match(e.request)
    .then(function(response) {
      return response || fetch(e.request)
        .then(function (resp){
          return caches.open(cacheName)
            .then(function(cache){
              cache.put(e.request, resp.clone());
              return resp;
          })
        }).catch(function(event){
          console.log('Error fetching data!');
        })
      })
  );
});