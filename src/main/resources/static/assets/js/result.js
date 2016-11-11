window.onload = function() {
    $("#refresh_implicit_grant_token").attr("href", getImplicitGrantUrl());

    var query = parseQueryString();
    var clientId = query['clientId'];
    var accessToken = query['accessToken'];
    var expiresIn = query['expiresIn'];
    $("#clientId").text(clientId);
    $("#accesstoken").text(accessToken);
    $("#expiresIn").text(expiresIn);
    $("#clientAccountDetails").text("Loading...");

    loadClientAccountDetails(accessToken);
};

function loadClientAccountDetails(accessToken) {
    $.ajax({
        url: vendorBaseUrl + "/api-vendor-sample/accounts",
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("X-IG-OAUTH-TOKEN", accessToken);
        },
        accept: "application/json"
    }).done(function (data, status, xhr) {
        $("#clientAccountDetails").text(JSON.stringify(data, undefined, 2));
    }).fail(function (data, status, xhr) {
        $("#clientAccountDetails").text(JSON.stringify(data, undefined, 2));
    });
}
