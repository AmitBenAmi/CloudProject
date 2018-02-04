
function getItems() {
    $.ajax({
        type : 'GET',
        url : '/api/items/items',
        dataType: "json"
    }).done(function(result) {
        var productsList = $("#our-products");
        for (var i =0; i < result.length; i++) {
            var item = result[i];
            var template = getCloedItemTemplate();
            // TODO: If want to add details page, set the link here
            //template.find("a.ref-img")
            // Set image in base64
            template.find("img.ref-img").attr('src', 'data:image/jpeg;base64,' + item.image);
            // Set name
            template.find("h5.item-name").text(item.name);
            // Set on click function
            template.find("a.add-to-cart").click(addToCartFunction(item._id));
            // Set price
            template.find("a.item-price").text(item.price + item.currency);
            // Remove hidden
            template.show();

            // Add item to product list
            productsList.append(template);
        }
    }).fail(function(result) {
        alert('error loading items');
    })
}

function getCloedItemTemplate() {
    return $("#item-template").clone();
}

function addToCartFunction(itemId) {
    return function() {        
        if (!getCookie('jwt')) {
            alert("Must be logged in to add to cart");
            return;
        }

        $.ajax({
            type : 'POST',
            url : 'api/cart/item',
            data : JSON.stringify({
                'itemid' : itemId,
                'quantity' : 1
            }),
            xhrFields: {
                withCredentials: true
            },
            contentType: "application/json"
        }).done(function(result) {
            alert('added to cart! OMG!! :O')
        }).fail(function(result) {
            alert('fail to add to cart');
        })
    };
}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

$(document).ready(getItems);