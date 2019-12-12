2 type de données sont gérées par cette API:
-data: les données récoltées par les capteurs
-post: un objet exemple sur lequel nous nous sommes basé afin de comprendre et créer l'objet "data". Celui-ci doit rester là en tant que guide.

Ci-dessous, la description initale du projet démo.

# Play REST API

This is the example project for [Making a REST API in Play](http://developer.lightbend.com/guides/play-rest-api/index.html).

## Appendix

### Running

You need to download and install sbt for this application to run.

Once you have sbt installed, the following at the command prompt will start up Play in development mode:

```bash
sbt run
```

Play will start up on the HTTP port at <http://localhost:9000/>.   You don't need to deploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request.

### Usage

If you call the same URL from the command line, you’ll see JSON. Using [httpie](https://httpie.org/), we can execute the command:

```bash
http --verbose http://localhost:9000/v1/posts
```

and get back:

```routes
GET /v1/posts HTTP/1.1
```

Likewise, you can also send a POST directly as JSON:

```bash
http --verbose POST http://localhost:9000/v1/posts title="hello" body="world"
```

and get:

```routes
POST /v1/posts HTTP/1.1
```
