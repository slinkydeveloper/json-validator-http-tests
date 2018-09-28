-- example HTTP POST script which demonstrates setting the
-- HTTP method, body, and adding a header

wrk.method = "POST"
wrk.scheme  = "http"
wrk.host = "localhost"
wrk.path = "/geo"
wrk.body = '{"latitude":55031880,"longitude":95886470}'
wrk.headers["Content-Type"] = "application/json"
