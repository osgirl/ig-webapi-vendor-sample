window.onload = function() {
    $("#authorization_code_grant_link").attr("href", getAuthorizationCodeGrantUrl());
    $("#implicit_grant_link").attr("href", getImplicitGrantUrl());
};

function getAuthorizationCodeGrantUrl() {
    var authRequestParameters = {
        "response_type": "code",
        "client_id": client_id,
        "realm": client_realm,
        "scope": "profile",
        "redirect_uri": vendorBaseUrl + "/api-vendor-sample/authorization-handler",
        "state": state
    };
    return oamBaseUrl + "/oauth2/authorize?" + encodeQueryData(authRequestParameters);
}

function getImplicitGrantUrl() {
    var authRequestImplicitParameters = {
        "response_type": "token",
        "client_id": client_id,
        "realm": client_realm,
        "scope": "profile",
        "redirect_uri": vendorBaseUrl + "/api-vendor-sample/implicit-grant-handler.html",
        "state": state
    };
    return oamBaseUrl + "/oauth2/authorize?" + encodeQueryData(authRequestImplicitParameters);
}