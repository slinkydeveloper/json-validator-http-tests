-- example HTTP POST script which demonstrates setting the
-- HTTP method, body, and adding a header

wrk.method = "POST"
wrk.scheme  = "http"
wrk.host = "localhost"
wrk.path = "/card_id"
wrk.body = '{"id_card":"nostrud velit laboris"}'
wrk.headers["Content-Type"] = "application/json"
