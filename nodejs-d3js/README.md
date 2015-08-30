```
$ cd nodejs-d3js
$ npm install
$ foreman start web
```

* Problems Left:
    * scalable ML, save (user, movie, rating) triple in Redis, only learn new user's new movie rating in min-batch 
    * freebase: filte genre  
    * ML: tuning parameters
    * to safely kill threads
    * Disable log to save disk size
    * Mute "GET http://x.x.x.x:8289/socket.io/?EIO=3&transport=polling&t=1436165788388-4 net::ERR_CONNECTION_REFUSED"
    * D3.js: svg [resize](http://stackoverflow.com/questions/11942500/how-to-make-force-layout-graph-in-d3-js-responsive-to-screen-browser-size) to screen browser size
