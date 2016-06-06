window.onload = function() {
    var authRequestParameters = {
        "response_type": "code",
        "client_id": client_id,
        "realm": client_realm,
        "scope": "profile",
        "redirect_uri": vendorBaseUrl + "/api-vendor-sample/authorization-handler",
        "state": state
    };
    var url = oamBaseUrl + "/oauth2/authorize?" + encodeQueryData(authRequestParameters);
    $("#authorize_link").attr("href", url);
};
