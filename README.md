## Cats Effect example application

### Run
```bash
$ sbt "runMain com.github.splusminusx.cet.Main"
```

### Healthcheck
```
$ curl -v  http://localhost:8080/health
```

### Put todo
```bash
$ curl -v -H'content-type:application/json' -XPOST -d'{"id": "1", "title":"First Task", "tags":[] }' http://localhost:8080/todo
```

### Get todo
```bash
curl -v http://localhost:8080/todo/1
```
