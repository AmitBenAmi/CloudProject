
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

function addToCartFunction(item) {
    return function() {
        // TODO
    };
}

$(document).ready(getItems);