function getCartItemsOfUser() {
    let username = getUserName();

    $.ajax({
        type : 'GET',
        url : `/api/cart/items/${username}`,
        dataType: "json"
    }).done(function(items) {
        console.log(items);
    }).fail(function(result) {
        alert('error loading items in the cart');
    })
}

$(document).ready(function() {
    getCartItemsOfUser();
});