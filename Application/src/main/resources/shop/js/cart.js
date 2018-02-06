function getCartItemsOfUser() {
    let username = getUserName();

    $.ajax({
        type : 'GET',
        url : `/api/cart/items/${username}`,
        dataType: "json",
        xhrFields: {
            withCredentials: true
        }
    }).done(function(cartItems) {
        //$('#cart-products__items-count').text(items.length);

        if (cartItems.length > 0) {
            getCartItemsDetails(cartItems);
        }
    }).fail(function(result) {
        alert('error loading items in the cart');
    })
}

function getCartItemsDetails(cartItems) {
    let ids = [];
    for (let i = 0; i < cartItems.length; i++) {
        let cartItem = cartItems[i];
        let itemId = cartItem._id.split(':')[1];
        ids.push(itemId);
    }

    getItemsByIds(ids, function (items) {
        populateCartItems(items, cartItems);
    });
}

function populateCartItems(items, cartItems) {
    let cartItemsList = $('#cart-items');
    let username = getUserName();
    for (let i = 0; i < items.length; i++) {
        let item = items[i];
        let template = getClonedCartItemTemplate();

        let itemQuantity = getItemQuantity(item, cartItems);

        template.find('img.ref-img').attr('src', `data:image/jpeg;base64,${item.image}`);
        template.find('td.cart-item__name').text(item.name);
        template.find('input.cart-item__quantity').attr('placeholder', itemQuantity);
        template.find('input.cart-item__quantity').attr('placeholder', itemQuantity);
        template.find('td.cart-item__price').text(`${item.price}${item.currency}`);
        template.find('td.cart-item__total').text(`${item.price * itemQuantity}${item.currency}`);

        template.find('button.quantity-increase').click(increaseQuantity(item, itemQuantity));
        template.find('button.quantity-decrease').click(decreaseQuantity(item, itemQuantity));
        template.find('button.item-remove').click(removeItem(username, item));

        cartItemsList.append(template);
        template.show();
    }
}

function increaseQuantity(item, quantity) {
    $.ajax({
        type : 'POST',
        url : '/api/cart/item',
        dataType: "json",
        data: {
            itemid: item._id,
            quantity: quantity + 1
        },
        xhrFields: {
            withCredentials: true
        }
    }).done(function() {
        alert('updated');
    }).fail(function(result) {
        alert('error updating item');
    })
}

function decreaseQuantity(item) {
    $.ajax({
        type : 'POST',
        url : '/api/cart/item',
        dataType: "json",
        data: {
            itemid: item._id,
            quantity: quantity - 1
        },
        xhrFields: {
            withCredentials: true
        }
    }).done(function() {
        alert('updated');
    }).fail(function(result) {
        alert('error updating item');
    })
}

function removeItem(username, item) {
    $.ajax({
        type : 'DELETE',
        url : '/api/cart/item',
        dataType: "json",
        data: {
            username: username,
            itemid: item._id
        },
        xhrFields: {
            withCredentials: true
        }
    }).done(function() {
        alert('updated');
    }).fail(function(result) {
        alert('error removing item from cart');
    })
}

function getItemQuantity(item, cartItems) {
    for (let i = 0; i < cartItems.length; i++) {
        let cartItem = cartItems[i];
        let cartItemId = cartItem._id.split(':')[1];

        if (cartItemId === item._id) {
            return cartItem.quantity
        }
    }

    return 0;
}

function getClonedCartItemTemplate() {
    return $("#cart-item-template").clone();
}

$(document).ready(function() {
    getCartItemsOfUser();
});