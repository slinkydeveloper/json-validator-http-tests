-- example HTTP POST script which demonstrates setting the
-- HTTP method, body, and adding a header

wrk.method = "POST"
wrk.scheme  = "http"
wrk.host = "localhost"
wrk.path = "/address"
wrk.body = '{"locality":"dolore sunt sit et","region":"ea consectetur","country-name":"consequat cillum magna voluptate exercitation","post-office-box":"amet nulla magna Excepteur","postal-code":"ullamco in irure sed cillum"}'
wrk.headers["Content-Type"] = "application/json"
