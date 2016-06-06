window.onload = function() {

    var query = parseQueryString();
    var clientId = query['clientId'];
    var accessToken = query['accessToken'];
    var expiresIn = query['expiresIn'];
    $("#clientId").text(clientId);
    $("#accesstoken").text(accessToken);
    $("#expiresIn").text(expiresIn);
};

function loadClientAccountDetails() {
    var accessToken = $("#accesstoken").text();
    var accountId = $("#accountId").val();
    $.ajax({
        url: vendorBaseUrl + "/api-vendor-sample/accounts?accountId=" + accountId,
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
