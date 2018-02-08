function getCartItemsOfUser() {
    let username = getUserName();

    if (username) {
        $.ajax({
            type: 'GET',
            url: `/api/cart/items`,
            dataType: "json",
            crossDomain: true,
            xhrFields: {
                withCredentials: true
            }
        }).done(function (cartItems) {
            if (cartItems.length > 0) {
                getCartItemsDetails(cartItems);
            }
        }).fail(function (result) {
            alert('error loading items in the cart');
        })
    }
}

function getNumberOfItem(cb) {
    let username = getUserName();

    if (username) {
        $.ajax({
            type: 'GET',
            url: `/api/cart/items/number`,
            dataType: "json",
            crossDomain: true,
            xhrFields: {
                withCredentials: true
            }
        }).done(function (number) {
            $('#toCart').text("[ " + number + " ] Items in your cart");
            $('#cart-products__items-count').text(number);
            if (number === 0) {
                $('#checkOutbtn').hide();
            }

            if (typeof(cb) === 'function') {
                cb();
            }
        }).fail(function (result) {
            alert('error loading items in the cart');
            if (typeof(cb) === 'function') {
                cb();
            }
        })
    }
}

function checkOut() {
    let username = getUserName();

    if (username) {
        $.ajax({
            type: 'POST',
            url: `/api/cart/checkout`,
            dataType: "text",
            crossDomain: true,
            xhrFields: {
                withCredentials: true
            }
        }).done(function (data) {
            getNumberOfItem(function () {
                window.location.reload();
            });
            alert('Thank for your order! Please check your mail');
        }).fail(function (result) {
            alert('error loading items in the cart');
        })
    }
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
    if (username) {
        for (let i = 0; i < items.length; i++) {
            let item = items[i];
            let template = getClonedCartItemTemplate();
            let cartItem = getCartItemForItem(item, cartItems);

            template.find('img.ref-img').attr('src', `data:image/jpeg;base64,${item.image}`);
            template.find('td.cart-item__name').text(item.name);
            updateCartItemNumbers(template, cartItem, item);

            template.find('button.quantity-increase').click(increaseQuantity(template, item, cartItem));
            template.find('button.quantity-decrease').click(decreaseQuantity(template, item, cartItem));
            template.find('button.item-remove').click(removeItem(template, username, item));

            cartItemsList.append(template);
            template.show();
        }
    }
}

function updateCartItemNumbers(element, cartItem, item) {
    element.find('input.cart-item__quantity').attr('placeholder', cartItem.quantity);
    element.find('td.cart-item__price').text(`${item.price}${item.currency}`);
    element.find('td.cart-item__total').text(`${item.price * cartItem.quantity}${item.currency}`);
}

function increaseQuantity(element, item, cartItem) {
    return function () {
        let username = getUserName();

        if (username) {
            $.ajax({
                type: 'POST',
                url: '/api/cart/item',
                dataType: "json",
                data: JSON.stringify({
                    itemid: item._id,
                    quantity: cartItem.quantity + 1
                }),
                xhrFields: {
                    withCredentials: true
                }
            }).done(function () {
                cartItem.quantity++;
                getNumberOfItem();
                updateCartItemNumbers(element, cartItem, item);
            }).fail(function (result) {
                alert('error updating item');
            })
        }
    }
}

function decreaseQuantity(element, item, cartItem) {
    return function () {
        let username = getUserName();

        if (username) {
            $.ajax({
                type: 'POST',
                url: '/api/cart/item',
                dataType: "json",
                data: JSON.stringify({
                    itemid: item._id,
                    quantity: cartItem.quantity - 1
                }),
                xhrFields: {
                    withCredentials: true
                }
            }).done(function () {
                cartItem.quantity--;
                getNumberOfItem();
                updateCartItemNumbers(element, cartItem, item);
            }).fail(function (result) {
                alert('error updating item');
            })
        }
    }
}

function removeItem(element, username, item) {
    return function () {
        $.ajax({
            type: 'DELETE',
            url: '/api/cart/item',
            dataType: "json",
            data: JSON.stringify({
                itemid: item._id
            }),
            xhrFields: {
                withCredentials: true
            }
        }).done(function () {
            getNumberOfItem();
            element.remove();
        }).fail(function (result) {
            alert('error removing item from cart');
        })
    }
}

function getCartItemForItem(item, cartItems) {
    for (let i = 0; i < cartItems.length; i++) {
        let cartItem = cartItems[i];
        let cartItemId = cartItem._id.split(':')[1];

        if (cartItemId === item._id) {
            return cartItem;
        }
    }

    return undefined;
}

function getClonedCartItemTemplate() {
    return $("#cart-item-template").clone();
}

$(document).ready(function () {
    if (window.location.pathname === '/product_summary.html') {
        getCartItemsOfUser();
    }

    getNumberOfItem();
});