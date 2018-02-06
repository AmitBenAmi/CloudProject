function getCartItemsOfUser() {
    let username = getUserName();

    $.ajax({
        type : 'GET',
        url : `/api/cart/items/${username}`,
        dataType: "json",
        xhrFields: {
            withCredentials: true
        }
    }).done(function(items) {
        console.log(items);
    }).fail(function(result) {
        alert('error loading items in the cart');
    })
}

function getNumberOfItem(){
    let username = getUserName();

    $.ajax({
        type : 'GET',
        url : `/api/cart/items/number/${username}`,
        dataType: "json",
        xhrFields: {
            withCredentials: true
        }
    }).done(function(number) {
        $('#toCart').text("[ " + number +" ] Items in your cart");
        console.log(number);
    }).fail(function(result) {
        alert('error loading items in the cart');
    })
}

$(document).ready(function() {
    getCartItemsOfUser();
    getNumberOfItem();
});