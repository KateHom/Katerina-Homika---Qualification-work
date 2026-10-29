// Инициализация модального окна корзины с помощью Bootstrap Modal API
// Initializing the cart modal popup using Bootstrap Modal API
const cartPopupElement = document.getElementById('cartPopup');
const cartModal = cartPopupElement ? new bootstrap.Modal(cartPopupElement) : null;

// Обработчик клика для открытия модального окна корзины
// Click event listener to open the cart modal
const cartButton = document.getElementById('cartButton');
if (cartButton && cartModal) {
    cartButton.addEventListener('click', () => {
        cartModal.show();
    });
}

// Обработчик клика для закрытия модального окна корзины
// Click event listener to close the cart modal
const closeCart = document.getElementById('closeCart');
if (closeCart && cartModal) {
    closeCart.addEventListener('click', () => {
        cartModal.hide();
    });
}

// Функция для динамического обновления счетчика товаров в шапке сайта
// Function to dynamically update the cart items count badge in the header
function updateCartCount(count) {
    const cartCountElement = document.getElementById('cartCount');
    if (cartCountElement) {
        cartCountElement.textContent = count;
    }
}