function money(value) {
    return `$${Number(value || 0).toFixed(2)}`;
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#39;");
}

function setFlash(type, message) {
    if (!message) {
        return;
    }

    showCartToast(message, type);
}

let cartToastTimer;

function showCartToast(message, type = "success") {
    const toast = document.getElementById("cart-toast");
    if (!toast) {
        return;
    }

    toast.textContent = message;
    toast.classList.remove("success", "error");
    toast.classList.add(type === "error" ? "error" : "success");
    toast.classList.add("show");

    window.clearTimeout(cartToastTimer);
    cartToastTimer = window.setTimeout(() => {
        toast.classList.remove("show");
    }, 1700);
}

function triggerAddToCartAnimation(form) {
    const productCard = form.closest(".product-card");
    const cartPanel = document.querySelector("#cart-panel .cart-panel");
    const cartToggle = document.getElementById("cart-toggle");
    const quantityInput = form.querySelector('input[name="quantity"]');
    const quantity = Number(quantityInput?.value || 1);

    if (productCard) {
        productCard.classList.remove("cart-pop");
        void productCard.offsetWidth;
        productCard.classList.add("cart-pop");
        spawnAddedBadge(productCard, quantity);
    }

    if (cartPanel) {
        cartPanel.classList.remove("cart-bounce");
        void cartPanel.offsetWidth;
        cartPanel.classList.add("cart-bounce");
    }

    if (cartToggle) {
        cartToggle.classList.remove("cart-bounce");
        void cartToggle.offsetWidth;
        cartToggle.classList.add("cart-bounce");
    }

    if (productCard && cartToggle) {
        animateProductToCart(productCard, cartToggle, quantity);
    }
}

function animateProductToCart(productCard, cartToggle, quantity) {
    const startRect = productCard.getBoundingClientRect();
    const endRect = cartToggle.getBoundingClientRect();
    const flyer = document.createElement("div");
    flyer.className = "cart-flyer";
    flyer.textContent = `+${quantity}`;
    const startX = startRect.left + startRect.width / 2 - 18;
    const startY = startRect.top + startRect.height / 2 - 18;
    const endX = endRect.left + endRect.width / 2 - 18;
    const endY = endRect.top + endRect.height / 2 - 18;
    flyer.style.left = `${startX}px`;
    flyer.style.top = `${startY}px`;
    document.body.appendChild(flyer);

    requestAnimationFrame(() => {
        flyer.style.transform = `translate(${endX - startX}px, ${endY - startY}px) scale(0.4)`;
        flyer.style.opacity = "0";
    });

    window.setTimeout(() => {
        flyer.remove();
    }, 720);
}

function spawnAddedBadge(productCard, quantity) {
    const badge = document.createElement("div");
    badge.className = "added-badge";
    badge.textContent = `Added x${quantity}`;
    productCard.appendChild(badge);

    requestAnimationFrame(() => {
        badge.classList.add("show");
    });

    window.setTimeout(() => {
        badge.classList.remove("show");
        window.setTimeout(() => badge.remove(), 260);
    }, 850);
}

function updateStats(state) {
    const productsCount = document.getElementById("products-count");
    const cartUnitsCount = document.getElementById("cart-units-count");
    const ordersCount = document.getElementById("orders-count");

    if (productsCount) {
        productsCount.textContent = state.stats.products;
    }
    if (cartUnitsCount) {
        cartUnitsCount.textContent = state.stats.cartUnits;
    }
    if (ordersCount) {
        ordersCount.textContent = state.stats.orders;
    }
}

function updateCart(state) {
    const cartPanel = document.querySelector("#cart-panel .cart-panel");
    const cartItemsHtml = state.cart.empty
        ? ""
        : state.cart.items.map((item) => `
            <div class="cart-row">
                <div>
                    <strong>${escapeHtml(item.productName)}</strong>
                    <span>${item.quantity} unit</span>
                </div>
                <div class="cart-row-actions">
                    <span>${money(item.lineTotal)}</span>
                    <form action="/api/cart/remove" method="post" data-async-form="cart-remove">
                        <input type="hidden" name="productId" value="${escapeHtml(item.productId)}">
                        <button type="submit" class="cart-remove-button">Remove</button>
                    </form>
                </div>
            </div>
        `).join("");

    cartPanel.innerHTML = `
        <div class="section-head">
            <h2>Cart</h2>
            <p>${state.cart.empty ? "No products selected yet." : "Current basket before order creation."}</p>
        </div>
        <div id="cart-items" class="cart-items">${cartItemsHtml}</div>
        <div class="cart-total">
            <span>Total</span>
            <strong id="cart-total-value">${money(state.cart.payableAmount)}</strong>
        </div>
    `;
}

function buildProductCard(product, index) {
    const isFiveStar = ["P-109", "P-110", "P-209", "P-210"].includes(product.id);
    const saleLabel = index % 2 === 0 ? "Sale" : "Top Pick";
    const color = index % 4 === 0 ? "black" : index % 4 === 1 ? "white" : index % 4 === 2 ? "green" : "orange";
    const feature = index % 3 === 0 ? "wireless" : index % 3 === 1 ? "compact" : "pro";
    const brand = index % 4 === 0 ? "lumicore" : index % 4 === 1 ? "nova" : index % 4 === 2 ? "orbit" : "vexa";
    const sales = Number(product.sales ?? 0);

    return `
        <article
            class="product-card"
            data-product-index="${index}"
            data-product-id="${escapeHtml(product.id.toLowerCase())}"
            data-name="${escapeHtml(product.name.toLowerCase())}"
            data-category="${escapeHtml(product.category.toLowerCase())}"
            data-price="${product.finalPrice}"
            data-sale="${index % 2 === 0}"
            data-sales="${sales}"
            data-rating="${isFiveStar ? "5.0" : "4.8"}"
            data-color="${color}"
            data-feature="${feature}"
            data-brand="${brand}">
            <div class="card-glow"></div>
            <div class="product-flag-row">
                <span class="product-flag sale-flag">${saleLabel}</span>
                <span class="product-flag shipping-flag">Fast Ship</span>
            </div>
            <div class="product-meta">
                <span class="badge">${escapeHtml(product.category)}</span>
                <span class="product-id">${escapeHtml(product.id)}</span>
            </div>
            <h3>${escapeHtml(product.name)}</h3>
            <p class="product-copy">Modern pick for everyday convenience, styled for a quick checkout decision.</p>
            <p class="price-row">
                <span class="final-price">${money(product.finalPrice)}</span>
                <span class="base-price">${money(product.basePrice)}</span>
            </p>
            <div class="rating-row">
                <span class="stars">★★★★★</span>
                <span>${isFiveStar ? "5.0 rating" : "4.8 rating"}</span>
            </div>
            <div class="sales-row">
                <span>Sold</span>
                <strong>${sales} pcs</strong>
            </div>
            <form action="/api/cart/add" method="post" class="add-form" data-async-form="cart-add">
                <input type="hidden" name="productId" value="${escapeHtml(product.id)}">
                <label>
                    Qty
                    <input type="number" name="quantity" min="1" value="1">
                </label>
                <button type="submit">Add to Cart</button>
            </form>
        </article>
    `;
}

function updateCatalog(state) {
    const grid = document.getElementById("catalog-grid");
    if (!grid || !state.products) {
        return;
    }

    grid.innerHTML = state.products.map((product, index) => buildProductCard(product, index)).join("");
    applyCatalogFilters();
}

function updateWallet(state) {
    const walletContent = document.getElementById("wallet-content");
    if (!walletContent) {
        return;
    }

    const user = state.currentUser;
    if (!user) {
        walletContent.innerHTML = `
            <div class="section-head">
                <h2>Wallet</h2>
                <p>Login to view your balance.</p>
            </div>
            <div class="wallet-balance-card">
                <span>Balance</span>
                <strong>$0.00</strong>
                <p>Please sign in to access wallet details.</p>
            </div>
        `;
        return;
    }

    const savedCardOption = user.hasSavedCard
        ? `<option value="SAVED">Use saved card (${escapeHtml(user.maskedCardNumber)})</option>`
        : "";
    const newCardVisible = user.hasSavedCard ? "display:none;" : "";

    walletContent.innerHTML = `
        <div class="section-head">
            <h2>Wallet</h2>
            <p>Available balance for ${escapeHtml(user.fullName)}.</p>
        </div>
        <div class="wallet-balance-card">
            <span>Current Balance</span>
            <strong>${money(user.balance)}</strong>
            <p>${user.hasSavedCard ? `Saved card: ${escapeHtml(user.maskedCardNumber)}` : "No saved card yet. Add one below."}</p>
        </div>
        <form action="/api/wallet/topup" method="post" class="checkout-form wallet-form" data-async-form="wallet-topup">
            <label>
                Card Option
                <select name="fundingSource" id="wallet-funding-source">
                    ${savedCardOption}
                    <option value="NEW" ${user.hasSavedCard ? "" : "selected"}>Add new card</option>
                </select>
            </label>
            <p class="field-error" data-error-for="fundingSource"></p>
            <label>
                Amount
                <input type="number" name="amount" min="1" step="0.01" placeholder="50">
            </label>
            <p class="field-error" data-error-for="amount"></p>
            <label id="wallet-new-card-field" style="${newCardVisible}">
                New Card Number
                <input type="text" name="cardNumber" placeholder="1111 2222 3333 4444">
            </label>
            <p class="field-error" data-error-for="cardNumber"></p>
            <div class="wallet-summary">
                <span>Final Funds</span>
                <strong>${money(user.balance)}</strong>
            </div>
            <button type="submit">Add Balance</button>
        </form>
    `;

    bindWalletFundingSource();
}

function updateCampaigns(state) {
    const heroStats = document.querySelector(".hero-stats");
    if (!heroStats || !state.campaigns) {
        return;
    }

    heroStats.innerHTML = state.campaigns.map((campaign, index) => `
        <div class="stat-card campaign-card${campaign.primary || index === 0 ? " campaign-primary" : ""}">
            <span>${escapeHtml(campaign.title)}</span>
            <strong>${escapeHtml(campaign.headline)}</strong>
            <p>${escapeHtml(campaign.description)}</p>
        </div>
    `).join("");
}

function updateAdmin(state) {
    const topActions = document.querySelector(".top-actions");
    const adminContent = document.getElementById("admin-content");
    const user = state.currentUser;
    const isAdmin = Boolean(user && user.role === "ADMIN");
    const existingAdminAction = document.querySelector(".admin-action-item");

    if (topActions) {
        if (isAdmin && !existingAdminAction) {
            const wrapper = document.createElement("div");
            wrapper.className = "quick-action-item admin-action-item";
            wrapper.innerHTML = `
                <button id="admin-toggle" class="quick-toggle" type="button" aria-label="Open admin panel">
                    <span class="quick-toggle-icon">
                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M12 4.5L18.5 7.7V12.8C18.5 16.3 15.9 19.2 12 20.5C8.1 19.2 5.5 16.3 5.5 12.8V7.7L12 4.5Z" fill="none" stroke="currentColor" stroke-width="1.8"></path>
                            <path d="M12 8.3V13.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path>
                            <path d="M9.5 11H14.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path>
                        </svg>
                    </span>
                </button>
                <span class="quick-action-label">Admin</span>
            `;
            const accountToggle = document.getElementById("account-toggle");
            topActions.insertBefore(wrapper, accountToggle);
            setupTopPanels();
        } else if (!isAdmin && existingAdminAction) {
            existingAdminAction.remove();
        }
    }

    if (!adminContent) {
        return;
    }

    if (!isAdmin) {
        adminContent.innerHTML = `
            <div class="section-head">
                <h2>Admin Controls</h2>
                <p>Login as admin to manage products and campaigns.</p>
            </div>
            <div class="wallet-balance-card">
                <span>Admin Access</span>
                <strong>Restricted</strong>
                <p>Only admin sessions can open management tools.</p>
            </div>
        `;
        return;
    }

    adminContent.innerHTML = `
        <div class="section-head">
            <h2>Admin Controls</h2>
            <p>Add new catalog items and fresh campaign cards.</p>
        </div>
        <div class="admin-grid">
            <form action="/api/admin/product" method="post" class="checkout-form admin-form" data-async-form="admin-product">
                <div class="section-head">
                    <h2>Add Product</h2>
                    <p>Create a new electronics or clothing item.</p>
                </div>
                <label>
                    Category
                    <select name="category">
                        <option value="ELECTRONICS">Electronics</option>
                        <option value="CLOTHING">Clothing</option>
                    </select>
                </label>
                <p class="field-error" data-error-for="category"></p>
                <label>
                    Product Name
                    <input type="text" name="name" placeholder="Aurora Headset">
                </label>
                <p class="field-error" data-error-for="name"></p>
                <label>
                    Price
                    <input type="number" name="price" min="1" step="0.01" placeholder="149">
                </label>
                <p class="field-error" data-error-for="price"></p>
                <label>
                    Primary Detail
                    <input type="text" name="primaryDetail" placeholder="Brand or Size">
                </label>
                <p class="field-error" data-error-for="primaryDetail"></p>
                <label>
                    Secondary Detail
                    <input type="text" name="secondaryDetail" placeholder="Warranty months or Color">
                </label>
                <p class="field-error" data-error-for="secondaryDetail"></p>
                <button type="submit">Add Product</button>
            </form>
            <form action="/api/admin/campaign" method="post" class="checkout-form admin-form" data-async-form="admin-campaign">
                <div class="section-head">
                    <h2>Add Campaign</h2>
                    <p>Replace the visible campaign cards with a new promo.</p>
                </div>
                <label>
                    Title
                    <input type="text" name="title" placeholder="Midnight Deal">
                </label>
                <p class="field-error" data-error-for="title"></p>
                <label>
                    Headline
                    <input type="text" name="headline" placeholder="2 for 1 Accessories">
                </label>
                <p class="field-error" data-error-for="headline"></p>
                <label>
                    Description
                    <input type="text" name="description" placeholder="Short campaign description">
                </label>
                <p class="field-error" data-error-for="description"></p>
                <button type="submit">Add Campaign</button>
            </form>
        </div>
    `;
}

function updateLogin(state) {
    const loginPanel = document.getElementById("login");
    const accountToggle = document.getElementById("account-toggle");
    const accountToggleIcon = document.getElementById("account-toggle-icon");
    const accountToggleLabel = document.getElementById("account-toggle-label");
    const user = state.currentUser;

    if (accountToggle) {
        accountToggle.classList.toggle("account-toggle-guest", !user);
    }
    if (accountToggleIcon) {
        accountToggleIcon.innerHTML = user
            ? escapeHtml(user.fullName.charAt(0).toUpperCase())
            : `
                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <circle cx="12" cy="8.2" r="3.4" fill="none" stroke="currentColor" stroke-width="1.8"></circle>
                    <path d="M6.2 18.4C7.4 15.8 9.3 14.5 12 14.5C14.7 14.5 16.6 15.8 17.8 18.4" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path>
                </svg>
            `;
    }
    if (accountToggleLabel) {
        accountToggleLabel.textContent = user ? "" : "Login";
    }

    let content = `
        <div class="section-head">
            <h2>Login</h2>
            <p>${user ? "Your demo session is active." : "Sign in to personalize the store demo."}</p>
        </div>
    `;

    if (user) {
        content += `
            <div id="login-state" class="login-state">
                <div class="login-avatar">${escapeHtml(user.fullName.charAt(0).toUpperCase())}</div>
                <div class="login-meta">
                    <strong>${escapeHtml(user.fullName)}</strong>
                    <span>${escapeHtml(user.email)}</span>
                    <span class="role-pill">${escapeHtml(user.role)}</span>
                </div>
                <a class="secondary-button profile-link-button" href="/profile">Edit Profile</a>
                <form class="login-action" action="/api/logout" method="post" data-async-form="logout">
                    <button type="submit" class="secondary-button">Logout</button>
                </form>
            </div>
        `;
    } else {
        content += `
            <form id="login-form" action="/api/login" method="post" class="checkout-form" data-async-form="login">
                <label>
                    Full Name
                    <input type="text" name="fullName" placeholder="Leyla Aliyeva">
                </label>
                <p class="field-error" data-error-for="fullName"></p>
                <label>
                    Email
                    <input type="email" name="email" placeholder="leyla@example.com">
                </label>
                <p class="field-error" data-error-for="email"></p>
                <label>
                    Role
                    <select name="role">
                        <option value="CUSTOMER">Customer</option>
                        <option value="ADMIN">Admin</option>
                    </select>
                </label>
                <p class="field-error" data-error-for="role"></p>
                <button type="submit">Sign In</button>
            </form>
        `;
    }

    loginPanel.innerHTML = content;
}

function updateCheckout(state) {
    const checkoutPanel = document.getElementById("checkout");
    if (!checkoutPanel) {
        return;
    }
    const user = state.currentUser;
    const savedCardOption = user && user.hasSavedCard
        ? `<option value="SAVED">Use saved card (${escapeHtml(user.maskedCardNumber)})</option>`
        : "";
    const newCardStyle = user && user.hasSavedCard ? "display:none;" : "";

    checkoutPanel.innerHTML = `
        <div class="section-head">
            <h2>Checkout</h2>
            <p>${user ? `Ordering as ${escapeHtml(user.fullName)} (${escapeHtml(user.email)})` : "Login first to continue with checkout."}</p>
        </div>
        <form id="checkout-form" action="/api/checkout" method="post" class="checkout-form" data-async-form="checkout">
            <label>
                Shipping Address
                <input type="text" name="shippingAddress" placeholder="Baku, Azerbaijan">
            </label>
            <p class="field-error" data-error-for="shippingAddress"></p>
            <label>
                Payment Method
                <select name="paymentMethod">
                    <option value="CARD">Card Payment</option>
                    <option value="COD">Cash On Delivery</option>
                    <option value="WALLET">Wallet Balance</option>
                </select>
            </label>
            <p class="field-error" data-error-for="paymentMethod"></p>
            ${user ? `
                <div id="checkout-card-section" class="checkout-card-section">
                    <label>
                        Card Option
                        <select name="cardOption" id="checkout-card-option">
                            ${savedCardOption}
                            <option value="NEW" ${user.hasSavedCard ? "" : "selected"}>Add new card</option>
                        </select>
                    </label>
                    <label id="checkout-new-card-field" style="${newCardStyle}">
                        New Card Number
                        <input type="text" name="checkoutCardNumber" placeholder="1111 2222 3333 4444">
                    </label>
                </div>
            ` : ""}
            ${user ? `
                <div class="wallet-summary">
                    <span>Wallet Funds</span>
                    <strong>${money(user.balance)}</strong>
                </div>
            ` : ""}
            <button type="submit" class="checkout-button" ${user ? "" : "disabled"}>Place Order</button>
        </form>
    `;

    bindCheckoutPaymentFields();
}

function updateOrders(state) {
    const empty = document.getElementById("orders-empty");
    const list = document.getElementById("orders-list");
    const panel = document.querySelector("#orders-panel .account-panel-inner");

    if (state.orders.length === 0) {
        if (list) {
            list.remove();
        }
        if (!empty) {
            const div = document.createElement("div");
            div.id = "orders-empty";
            div.className = "orders-empty";
            div.textContent = "No orders yet.";
            panel.appendChild(div);
        }
        return;
    }

    if (empty) {
        empty.remove();
    }

    const html = state.orders.map((order) => `
        <article class="order-card">
            <div class="order-top">
                <div>
                    <h3>${escapeHtml(order.orderId)}</h3>
                    <p>${escapeHtml(order.customerName)} | ${escapeHtml(order.shippingAddress)}</p>
                </div>
                <span class="status">${escapeHtml(order.status)}</span>
            </div>
            <div class="order-items">
                ${order.items.map((item) => `
                    <div class="order-item-row">
                        <span>${escapeHtml(item.productName)} x${item.quantity}</span>
                        <span>${money(item.lineTotal)}</span>
                    </div>
                `).join("")}
            </div>
            <div class="order-bottom">
                <span>${escapeHtml(order.paymentMethod)}</span>
                <strong>${money(order.payableAmount)}</strong>
            </div>
        </article>
    `).join("");

    if (list) {
        list.innerHTML = html;
    } else {
        const div = document.createElement("div");
        div.id = "orders-list";
        div.className = "orders-list";
        div.innerHTML = html;
        panel.appendChild(div);
    }
}

function clearFieldErrors(form) {
    form.querySelectorAll("[data-error-for]").forEach((node) => {
        node.textContent = "";
    });
}

function applyFieldErrors(form, fieldErrors) {
    Object.entries(fieldErrors || {}).forEach(([field, message]) => {
        const target = form.querySelector(`[data-error-for="${field}"]`);
        if (target) {
            target.textContent = message;
        }
    });
}

function refreshState(state) {
    updateStats(state);
    updateCampaigns(state);
    updateCatalog(state);
    updateLogin(state);
    updateAdmin(state);
    updateCart(state);
    updateWallet(state);
    updateCheckout(state);
    updateOrders(state);
}

async function submitAsyncForm(form) {
    clearFieldErrors(form);

    const response = await fetch(form.action, {
        method: form.method || "post",
        body: new FormData(form),
        headers: {
            "X-Requested-With": "fetch"
        }
    });

    const payload = await response.json();

    if (!payload.success) {
        setFlash("error", payload.message);
        applyFieldErrors(form, payload.fieldErrors);
        return;
    }

    setFlash("success", payload.message);
    refreshState(payload.state);
    if (form.dataset.asyncForm === "cart-add") {
        triggerAddToCartAnimation(form);
        showCartToast("Added to cart");
    }
    closePanels();
}

function closePanels() {
    document.querySelectorAll(".account-panel, .modal-panel").forEach((panel) => {
        panel.classList.remove("open");
    });
}

function bindWalletFundingSource() {
    const fundingSource = document.getElementById("wallet-funding-source");
    const newCardField = document.getElementById("wallet-new-card-field");
    if (!fundingSource || !newCardField) {
        return;
    }

    const syncVisibility = () => {
        newCardField.style.display = fundingSource.value === "NEW" ? "grid" : "none";
    };

    fundingSource.addEventListener("change", syncVisibility);
    syncVisibility();
}

function bindCheckoutPaymentFields() {
    const paymentMethod = document.querySelector('#checkout-form select[name="paymentMethod"]');
    const cardSection = document.getElementById("checkout-card-section");
    const cardOption = document.getElementById("checkout-card-option");
    const newCardField = document.getElementById("checkout-new-card-field");

    if (!paymentMethod || !cardSection) {
        return;
    }

    const syncCardVisibility = () => {
        const showCardFields = paymentMethod.value === "CARD";
        cardSection.style.display = showCardFields ? "grid" : "none";
        if (newCardField && cardOption) {
            newCardField.style.display = showCardFields && cardOption.value === "NEW" ? "grid" : "none";
        }
    };

    paymentMethod.addEventListener("change", syncCardVisibility);
    cardOption?.addEventListener("change", syncCardVisibility);
    syncCardVisibility();
}

const catalogState = {
    dealsOnly: false,
    sort: "relevance",
    category: "all",
    color: "all",
    feature: "all",
    brand: "all",
    search: "",
    fiveStarOnly: false,
    bestSellersOnly: false,
    newInOnly: false
};

const filterCycles = {
    category: ["all", "electronics", "clothing"],
    color: ["all", "black", "white", "green", "orange"],
    feature: ["all", "wireless", "compact", "pro"],
    brand: ["all", "lumicore", "nova", "orbit", "vexa"],
    sort: ["relevance", "price-low", "price-high"]
};

function nextCycleValue(values, current) {
    const currentIndex = values.indexOf(current);
    return values[(currentIndex + 1) % values.length];
}

function formatFilterLabel(value) {
    if (value === "all") {
        return "All";
    }
    if (value === "price-low") {
        return "Price Low";
    }
    if (value === "price-high") {
        return "Price High";
    }
    return value.charAt(0).toUpperCase() + value.slice(1);
}

function hasActiveCatalogFilter() {
    return catalogState.dealsOnly
        || catalogState.sort !== "relevance"
        || catalogState.category !== "all"
        || catalogState.color !== "all"
        || catalogState.feature !== "all"
        || catalogState.brand !== "all"
        || catalogState.fiveStarOnly
        || catalogState.bestSellersOnly
        || catalogState.newInOnly
        || Boolean(catalogState.search);
}

function syncNavHighlights() {
    document.getElementById("best-sellers-link")?.classList.toggle("nav-active", catalogState.bestSellersOnly);
    document.getElementById("five-star-link")?.classList.toggle("nav-active", catalogState.fiveStarOnly);
    document.getElementById("new-in-link")?.classList.toggle("nav-active", catalogState.newInOnly);
}

function resetCatalogState() {
    catalogState.dealsOnly = false;
    catalogState.sort = "relevance";
    catalogState.category = "all";
    catalogState.color = "all";
    catalogState.feature = "all";
    catalogState.brand = "all";
    catalogState.search = "";
    catalogState.fiveStarOnly = false;
    catalogState.bestSellersOnly = false;
    catalogState.newInOnly = false;

    const searchInput = document.getElementById("catalog-search");
    if (searchInput) {
        searchInput.value = "";
    }

    syncNavHighlights();
}

function normalizeText(value) {
    return String(value ?? "").trim().toLowerCase();
}

function productMatches(card) {
    const searchableText = [
        card.dataset.name,
        card.dataset.category,
        card.dataset.brand,
        card.dataset.feature,
        card.dataset.color,
        card.dataset.rating,
        card.dataset.productId
    ].map(normalizeText).join(" ");

    const matchesDeals = !catalogState.dealsOnly || card.dataset.sale === "true";
    const matchesCategory = catalogState.category === "all" || card.dataset.category === catalogState.category;
    const matchesColor = catalogState.color === "all" || card.dataset.color === catalogState.color;
    const matchesFeature = catalogState.feature === "all" || card.dataset.feature === catalogState.feature;
    const matchesBrand = catalogState.brand === "all" || card.dataset.brand === catalogState.brand;
    const matchesSearch = !catalogState.search || searchableText.includes(catalogState.search);
    const matchesFiveStar = !catalogState.fiveStarOnly || card.dataset.rating === "5.0";
    const matchesBestSellers = !catalogState.bestSellersOnly || Number(card.dataset.sales) >= 120;
    const matchesNewIn = !catalogState.newInOnly || Number(card.dataset.productIndex) >= 16;

    return matchesDeals
        && matchesCategory
        && matchesColor
        && matchesFeature
        && matchesBrand
        && matchesSearch
        && matchesFiveStar
        && matchesBestSellers
        && matchesNewIn;
}

function applyCatalogFilters() {
    const grid = document.getElementById("catalog-grid");
    if (!grid) {
        return;
    }

    const cards = Array.from(grid.querySelectorAll(".product-card"));
    let visibleCount = 0;

    cards.forEach((card) => {
        const visible = productMatches(card);
        card.style.display = visible ? "" : "none";
        if (visible) {
            visibleCount += 1;
        }
    });

    const sortedCards = cards.slice().sort((left, right) => {
        if (catalogState.sort === "price-low") {
            return Number(left.dataset.price) - Number(right.dataset.price);
        }
        if (catalogState.sort === "price-high") {
            return Number(right.dataset.price) - Number(left.dataset.price);
        }
        if (catalogState.bestSellersOnly) {
            return Number(right.dataset.sales) - Number(left.dataset.sales);
        }
        return Number(left.dataset.productIndex) - Number(right.dataset.productIndex);
    });

    sortedCards.forEach((card) => {
        grid.appendChild(card);
    });

    const filtersToggle = document.getElementById("filters-toggle");
    const dealsToggle = document.getElementById("deals-toggle");
    const sortToggle = document.getElementById("sort-toggle");
    const categoryToggle = document.getElementById("category-toggle");
    const colorToggle = document.getElementById("color-toggle");
    const featureToggle = document.getElementById("feature-toggle");
    const brandToggle = document.getElementById("brand-toggle");

    if (filtersToggle) {
        filtersToggle.textContent = hasActiveCatalogFilter() ? "Clear Filters" : "Filters";
        filtersToggle.classList.toggle("active-chip", hasActiveCatalogFilter());
    }
    if (dealsToggle) {
        dealsToggle.textContent = `Deals: ${catalogState.dealsOnly ? "On" : "Off"}`;
        dealsToggle.classList.toggle("active-chip", catalogState.dealsOnly);
    }
    if (sortToggle) {
        sortToggle.textContent = `Sort: ${formatFilterLabel(catalogState.sort)}`;
        sortToggle.classList.toggle("active-chip", catalogState.sort !== "relevance");
    }
    if (categoryToggle) {
        categoryToggle.textContent = `Category: ${formatFilterLabel(catalogState.category)}`;
        categoryToggle.classList.toggle("active-chip", catalogState.category !== "all");
    }
    if (colorToggle) {
        colorToggle.textContent = `Color: ${formatFilterLabel(catalogState.color)}`;
        colorToggle.classList.toggle("active-chip", catalogState.color !== "all");
    }
    if (featureToggle) {
        featureToggle.textContent = `Feature: ${formatFilterLabel(catalogState.feature)}`;
        featureToggle.classList.toggle("active-chip", catalogState.feature !== "all");
    }
    if (brandToggle) {
        brandToggle.textContent = `Brand: ${formatFilterLabel(catalogState.brand)}`;
        brandToggle.classList.toggle("active-chip", catalogState.brand !== "all");
    }

    const emptyState = document.getElementById("catalog-empty");
    if (emptyState) {
        emptyState.style.display = visibleCount === 0 ? "block" : "none";
    }
}

function setupCatalogFilters() {
    const filtersToggle = document.getElementById("filters-toggle");
    const dealsToggle = document.getElementById("deals-toggle");
    const sortToggle = document.getElementById("sort-toggle");
    const categoryToggle = document.getElementById("category-toggle");
    const colorToggle = document.getElementById("color-toggle");
    const featureToggle = document.getElementById("feature-toggle");
    const brandToggle = document.getElementById("brand-toggle");

    if (!filtersToggle) {
        return;
    }

    filtersToggle.addEventListener("click", () => {
        if (!hasActiveCatalogFilter()) {
            showCartToast("No active filters");
            return;
        }

        resetCatalogState();
        applyCatalogFilters();
        showCartToast("Filters cleared");
    });

    dealsToggle?.addEventListener("click", () => {
        catalogState.dealsOnly = !catalogState.dealsOnly;
        applyCatalogFilters();
    });

    sortToggle?.addEventListener("click", () => {
        catalogState.sort = nextCycleValue(filterCycles.sort, catalogState.sort);
        applyCatalogFilters();
    });

    categoryToggle?.addEventListener("click", () => {
        catalogState.category = nextCycleValue(filterCycles.category, catalogState.category);
        applyCatalogFilters();
    });

    colorToggle?.addEventListener("click", () => {
        catalogState.color = nextCycleValue(filterCycles.color, catalogState.color);
        applyCatalogFilters();
    });

    featureToggle?.addEventListener("click", () => {
        catalogState.feature = nextCycleValue(filterCycles.feature, catalogState.feature);
        applyCatalogFilters();
    });

    brandToggle?.addEventListener("click", () => {
        catalogState.brand = nextCycleValue(filterCycles.brand, catalogState.brand);
        applyCatalogFilters();
    });

    applyCatalogFilters();
}

function setupSearchAndFeaturedActions() {
    const searchInput = document.getElementById("catalog-search");
    const searchButton = document.getElementById("search-button");
    const bestSellersLink = document.getElementById("best-sellers-link");
    const fiveStarLink = document.getElementById("five-star-link");
    const newInLink = document.getElementById("new-in-link");

    if (searchInput) {
        searchInput.addEventListener("input", () => {
            catalogState.search = normalizeText(searchInput.value);
            applyCatalogFilters();
        });
    }

    if (searchButton && searchInput) {
        searchButton.addEventListener("click", () => {
            catalogState.search = normalizeText(searchInput.value);
            applyCatalogFilters();
            if (catalogState.search) {
                showCartToast(`Search: ${searchInput.value.trim()}`);
            } else {
                showCartToast("Search cleared");
            }
        });
    }

    if (bestSellersLink) {
        bestSellersLink.addEventListener("click", (event) => {
            event.preventDefault();
            catalogState.bestSellersOnly = !catalogState.bestSellersOnly;
            syncNavHighlights();
            applyCatalogFilters();
            document.getElementById("catalog")?.scrollIntoView({ behavior: "smooth", block: "start" });
            showCartToast(catalogState.bestSellersOnly ? "Showing best sellers" : "Showing all products");
        });
    }

    if (fiveStarLink) {
        fiveStarLink.addEventListener("click", (event) => {
            event.preventDefault();
            catalogState.fiveStarOnly = !catalogState.fiveStarOnly;
            syncNavHighlights();
            applyCatalogFilters();
            document.getElementById("catalog")?.scrollIntoView({ behavior: "smooth", block: "start" });
            showCartToast(catalogState.fiveStarOnly ? "Showing 5-star rated items" : "Showing all ratings");
        });
    }

    if (newInLink) {
        newInLink.addEventListener("click", (event) => {
            event.preventDefault();
            catalogState.newInOnly = !catalogState.newInOnly;
            syncNavHighlights();
            applyCatalogFilters();
            document.getElementById("catalog")?.scrollIntoView({ behavior: "smooth", block: "start" });
            showCartToast(catalogState.newInOnly ? "Showing new arrivals" : "Showing all products");
        });
    }
}

document.addEventListener("keydown", (event) => {
    if (event.key !== "Enter") {
        return;
    }
    const target = event.target;
    if (!(target instanceof HTMLInputElement) || target.id !== "catalog-search") {
        return;
    }
    event.preventDefault();
    catalogState.search = normalizeText(target.value);
    applyCatalogFilters();
});

function setupTopPanels() {
    const accountToggle = document.getElementById("account-toggle");
    const accountPanel = document.getElementById("account-panel");
    const cartToggle = document.getElementById("cart-toggle");
    const cartPanel = document.getElementById("cart-panel");
    const walletToggle = document.getElementById("wallet-toggle");
    const walletPanel = document.getElementById("wallet-panel");
    const adminToggle = document.getElementById("admin-toggle");
    const adminPanel = document.getElementById("admin-panel");
    const checkoutToggle = document.getElementById("checkout-toggle");
    const checkoutPanel = document.getElementById("checkout-panel");
    const ordersToggle = document.getElementById("orders-toggle");
    const ordersPanel = document.getElementById("orders-panel");

    if (accountToggle && accountPanel && !accountToggle.dataset.bound) {
        accountToggle.dataset.bound = "true";
        accountToggle.addEventListener("click", (event) => {
            event.stopPropagation();
            const willOpen = !accountPanel.classList.contains("open");
            closePanels();
            if (willOpen) {
                accountPanel.classList.add("open");
            }
        });
    }

    if (cartToggle && cartPanel && !cartToggle.dataset.bound) {
        cartToggle.dataset.bound = "true";
        cartToggle.addEventListener("click", (event) => {
            event.stopPropagation();
            const willOpen = !cartPanel.classList.contains("open");
            closePanels();
            if (willOpen) {
                cartPanel.classList.add("open");
            }
        });
    }

    if (walletToggle && walletPanel && !walletToggle.dataset.bound) {
        walletToggle.dataset.bound = "true";
        walletToggle.addEventListener("click", (event) => {
            event.stopPropagation();
            const willOpen = !walletPanel.classList.contains("open");
            closePanels();
            if (willOpen) {
                walletPanel.classList.add("open");
            }
        });
    }

    if (adminToggle && adminPanel && !adminToggle.dataset.bound) {
        adminToggle.dataset.bound = "true";
        adminToggle.addEventListener("click", (event) => {
            event.stopPropagation();
            const willOpen = !adminPanel.classList.contains("open");
            closePanels();
            if (willOpen) {
                adminPanel.classList.add("open");
            }
        });
    }

    if (checkoutToggle && checkoutPanel && !checkoutToggle.dataset.bound) {
        checkoutToggle.dataset.bound = "true";
        checkoutToggle.addEventListener("click", (event) => {
            event.stopPropagation();
            const willOpen = !checkoutPanel.classList.contains("open");
            closePanels();
            if (willOpen) {
                checkoutPanel.classList.add("open");
            }
        });
    }

    if (ordersToggle && ordersPanel && !ordersToggle.dataset.bound) {
        ordersToggle.dataset.bound = "true";
        ordersToggle.addEventListener("click", (event) => {
            event.stopPropagation();
            const willOpen = !ordersPanel.classList.contains("open");
            closePanels();
            if (willOpen) {
                ordersPanel.classList.add("open");
            }
        });
    }

    document.querySelectorAll("[data-close-panel]").forEach((button) => {
        if (button.dataset.bound) {
            return;
        }
        button.dataset.bound = "true";
        button.addEventListener("click", () => {
            const panelId = button.getAttribute("data-close-panel");
            const panel = panelId ? document.getElementById(panelId) : null;
            if (panel) {
                panel.classList.remove("open");
            }
        });
    });

    if (!document.body.dataset.panelBound) {
        document.body.dataset.panelBound = "true";
        document.addEventListener("click", (event) => {
            const isInsideAccountPanel = event.target.closest(".account-panel");
            const isInsideModal = event.target.closest(".modal-window");
            const isToggle = event.target.closest(".account-toggle, .quick-toggle");
            if (!isInsideModal && !isInsideAccountPanel && !isToggle) {
                closePanels();
            }
        });
    }
}

document.addEventListener("submit", async (event) => {
    const form = event.target;
    if (!(form instanceof HTMLFormElement) || !form.dataset.asyncForm) {
        return;
    }

    event.preventDefault();

    try {
        await submitAsyncForm(form);
    } catch (error) {
        setFlash("error", "Something went wrong. Please try again.");
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const flashBootstrap = document.getElementById("flash-bootstrap");
    if (!flashBootstrap) {
        return;
    }

    const message = flashBootstrap.dataset.message;
    const error = flashBootstrap.dataset.error;

    if (error) {
        showCartToast(error, "error");
        return;
    }

    if (message) {
        showCartToast(message, "success");
    }

    bindWalletFundingSource();
    bindCheckoutPaymentFields();
});

setupTopPanels();
setupCatalogFilters();
setupSearchAndFeaturedActions();
