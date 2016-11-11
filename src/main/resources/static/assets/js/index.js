window.onload = function() {
    $("#authorization_code_grant_link").attr("href", getAuthorizationCodeGrantUrl());
    $("#implicit_grant_link").attr("href", getImplicitGrantUrl());
};
