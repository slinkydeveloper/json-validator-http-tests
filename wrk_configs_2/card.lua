-- example HTTP POST script which demonstrates setting the
-- HTTP method, body, and adding a header

wrk.method = "POST"
wrk.scheme  = "http"
wrk.host = "localhost"
wrk.path = "/card"
wrk.body = '{"familyName":"elit nisi cillum","givenName":"sit","fn":"esse ad in aliqua","url":"http://helloworld.com","email":{"type":"incididunt enim"},"nickname":"aliqua Duis velit","tz":"aliqua esse cillum non","role":"veniam laboris"}'
wrk.headers["Content-Type"] = "application/json"
