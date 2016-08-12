window.onload = function() {
    var params = getUrlFragmentParameters();
    var accessToken = params["access_token"];
    if (accessToken == undefined) {
        var error = parseQueryString();
        $("#info").html("<pre>" + JSON.stringify(error, undefined, 2) + "</pre>");
        return;
    }
    var expiresIn = params["expires_in"];
    var state = params["state"];
    window.location.replace(vendorBaseUrl + "/api-vendor-sample/show-result?access_token=" + accessToken + "&expires_in=" + expiresIn + "&state=" + state);
};

function getUrlFragmentParameters() {
    var parameters = {};
    var urlFragment = location.hash.substring(1);
    var regex = /([^&=]+)=([^&]*)/g, m;
    while (m = regex.exec(urlFragment)) {
        parameters[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
    }
    return parameters;
}
