package com.ecommerce.web;

import com.ecommerce.model.Campaign;
import com.ecommerce.model.order.Order;
import com.ecommerce.model.order.OrderStatus;
import com.ecommerce.model.user.Customer;
import com.ecommerce.service.CampaignService;
import com.ecommerce.service.CartService;
import com.ecommerce.service.CatalogService;
import com.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StoreController {
    private final CatalogService catalogService;
    private final CartService cartService;
    private final OrderService orderService;
    private final CampaignService campaignService;

    public StoreController(CatalogService catalogService, CartService cartService, OrderService orderService, CampaignService campaignService) {
        this.catalogService = catalogService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.campaignService = campaignService;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        SessionUser currentUser = (SessionUser) session.getAttribute("currentUser");
        if (!model.containsAttribute("loginForm")) {
            LoginForm loginForm = new LoginForm();
            if (currentUser != null) {
                loginForm.setFullName(currentUser.getFullName());
                loginForm.setEmail(currentUser.getEmail());
                loginForm.setRole(currentUser.getRole());
            } else {
                loginForm.setRole("CUSTOMER");
            }
            model.addAttribute("loginForm", loginForm);
        }
        if (!model.containsAttribute("checkoutForm")) {
            CheckoutForm form = new CheckoutForm();
            form.setPaymentMethod("CARD");
            model.addAttribute("checkoutForm", form);
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("products", catalogService.getAllProducts());
        model.addAttribute("salesCounts", catalogService.getSalesCounts());
        model.addAttribute("campaigns", campaignService.getCampaigns());
        model.addAttribute("cart", cartService.getCart());
        model.addAttribute("orders", orderService.getOrders());
        return "index";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser currentUser = (SessionUser) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/";
        }

        if (!model.containsAttribute("profileForm")) {
            model.addAttribute("profileForm", buildProfileForm(currentUser));
        }
        model.addAttribute("currentUser", currentUser);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileForm profileForm,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        SessionUser currentUser = (SessionUser) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            return "profile";
        }

        currentUser.setFirstName(profileForm.getFirstName().trim());
        currentUser.setLastName(profileForm.getLastName().trim());
        currentUser.setEmail(profileForm.getEmail().trim());
        currentUser.setShippingAddress(profileForm.getShippingAddress().trim());
        currentUser.setCardNumber(profileForm.getCardNumber().trim());
        session.setAttribute("currentUser", currentUser);

        redirectAttributes.addFlashAttribute("message", "Profile updated successfully.");
        return "redirect:/profile";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                        BindingResult bindingResult,
                        Model model,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateSharedModel(model, session);
            return "index";
        }

        SessionUser currentUser = new SessionUser(loginForm.getFullName(), loginForm.getEmail(), loginForm.getRole());
        session.setAttribute("currentUser", currentUser);
        redirectAttributes.addFlashAttribute("message", "Signed in as " + currentUser.getFullName() + ".");
        return "redirect:/";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public Map<String, Object> loginAsync(@Valid @ModelAttribute LoginForm loginForm,
                                          BindingResult bindingResult,
                                          HttpSession session) {
        if (bindingResult.hasErrors()) {
            return errorResponse("Please enter valid login details.", bindingResult);
        }

        SessionUser currentUser = new SessionUser(loginForm.getFullName(), loginForm.getEmail(), loginForm.getRole());
        session.setAttribute("currentUser", currentUser);
        return successResponse("Signed in as " + currentUser.getFullName() + ".", session);
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("currentUser");
        redirectAttributes.addFlashAttribute("message", "Logged out successfully.");
        return "redirect:/";
    }

    @PostMapping("/api/logout")
    @ResponseBody
    public Map<String, Object> logoutAsync(HttpSession session) {
        session.removeAttribute("currentUser");
        return successResponse("Logged out successfully.", session);
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam String productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {
        cartService.addProduct(catalogService.findById(productId), quantity);
        redirectAttributes.addFlashAttribute("message", "Product added to cart.");
        return "redirect:/";
    }

    @PostMapping("/api/cart/add")
    @ResponseBody
    public Map<String, Object> addToCartAsync(@RequestParam String productId,
                                              @RequestParam(defaultValue = "1") int quantity,
                                              HttpSession session) {
        cartService.addProduct(catalogService.findById(productId), quantity);
        return successResponse("Product added to cart.", session);
    }

    @PostMapping("/api/cart/remove")
    @ResponseBody
    public Map<String, Object> removeFromCartAsync(@RequestParam String productId,
                                                   HttpSession session) {
        cartService.removeProduct(productId);
        return successResponse("Product removed from cart.", session);
    }

    @PostMapping("/api/wallet/topup")
    @ResponseBody
    public Map<String, Object> walletTopUp(@ModelAttribute WalletTopUpForm walletTopUpForm,
                                           HttpSession session) {
        SessionUser currentUser = (SessionUser) session.getAttribute("currentUser");
        if (currentUser == null) {
            return errorResponse("Please login to add balance.");
        }

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        double amount = parseAmount(walletTopUpForm.getAmount(), fieldErrors);
        String fundingSource = walletTopUpForm.getFundingSource() == null ? "" : walletTopUpForm.getFundingSource().trim().toUpperCase();

        if (!"SAVED".equals(fundingSource) && !"NEW".equals(fundingSource)) {
            fieldErrors.put("fundingSource", "Choose a card option.");
        }

        if ("SAVED".equals(fundingSource) && !currentUser.hasSavedCard()) {
            fieldErrors.put("fundingSource", "No saved card found. Choose new card.");
        }

        if ("NEW".equals(fundingSource)) {
            String newCardNumber = walletTopUpForm.getCardNumber() == null ? "" : walletTopUpForm.getCardNumber().trim();
            if (newCardNumber.isBlank()) {
                fieldErrors.put("cardNumber", "Enter a new card number.");
            } else {
                currentUser.setCardNumber(newCardNumber);
            }
        }

        if (!fieldErrors.isEmpty()) {
            return errorResponse("Please complete wallet top up fields.", fieldErrors);
        }

        currentUser.setBalance(currentUser.getBalance() + amount);
        session.setAttribute("currentUser", currentUser);
        return successResponse("Balance updated to $" + String.format("%.2f", currentUser.getBalance()) + ".", session);
    }

    @PostMapping("/api/admin/product")
    @ResponseBody
    public Map<String, Object> addAdminProduct(@ModelAttribute AdminProductForm adminProductForm,
                                               HttpSession session) {
        SessionUser currentUser = (SessionUser) session.getAttribute("currentUser");
        if (!isAdmin(currentUser)) {
            return errorResponse("Admin access required.");
        }

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        String category = safe(adminProductForm.getCategory()).toUpperCase();
        String name = safe(adminProductForm.getName());
        String primaryDetail = safe(adminProductForm.getPrimaryDetail());
        String secondaryDetail = safe(adminProductForm.getSecondaryDetail());
        double price = parseAmount(adminProductForm.getPrice(), fieldErrors);

        if (name.isBlank()) {
            fieldErrors.put("name", "Enter product name.");
        }
        if (!"ELECTRONICS".equals(category) && !"CLOTHING".equals(category)) {
            fieldErrors.put("category", "Choose a valid category.");
        }
        if (primaryDetail.isBlank()) {
            fieldErrors.put("primaryDetail", "Enter primary detail.");
        }
        if (secondaryDetail.isBlank()) {
            fieldErrors.put("secondaryDetail", "Enter secondary detail.");
        }

        if (!fieldErrors.isEmpty()) {
            return errorResponse("Please complete product fields.", fieldErrors);
        }

        catalogService.addProduct(category, name, price, primaryDetail, secondaryDetail);
        return successResponse("Admin product added successfully.", session);
    }

    @PostMapping("/api/admin/campaign")
    @ResponseBody
    public Map<String, Object> addAdminCampaign(@ModelAttribute AdminCampaignForm adminCampaignForm,
                                                HttpSession session) {
        SessionUser currentUser = (SessionUser) session.getAttribute("currentUser");
        if (!isAdmin(currentUser)) {
            return errorResponse("Admin access required.");
        }

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        String title = safe(adminCampaignForm.getTitle());
        String headline = safe(adminCampaignForm.getHeadline());
        String description = safe(adminCampaignForm.getDescription());

        if (title.isBlank()) {
            fieldErrors.put("title", "Enter campaign title.");
        }
        if (headline.isBlank()) {
            fieldErrors.put("headline", "Enter campaign headline.");
        }
        if (description.isBlank()) {
            fieldErrors.put("description", "Enter campaign description.");
        }

        if (!fieldErrors.isEmpty()) {
            return errorResponse("Please complete campaign fields.", fieldErrors);
        }

        campaignService.addCampaign(new Campaign(title, headline, description, true));
        return successResponse("Campaign added successfully.", session);
    }

    @PostMapping("/checkout")
    public String checkout(@Valid @ModelAttribute("checkoutForm") CheckoutForm checkoutForm,
                           BindingResult bindingResult,
                           Model model,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        SessionUser currentUser = (SessionUser) session.getAttribute("currentUser");

        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Please login before checkout.");
            return "redirect:/";
        }

        if (cartService.getCart().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cart is empty.");
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            populateSharedModel(model, session);
            return "index";
        }

        if ("CARD".equalsIgnoreCase(checkoutForm.getPaymentMethod())) {
            String cardValidationError = validateCheckoutCard(currentUser, checkoutForm);
            if (cardValidationError != null) {
                redirectAttributes.addFlashAttribute("error", cardValidationError);
                return "redirect:/";
            }
        }

        if ("WALLET".equalsIgnoreCase(checkoutForm.getPaymentMethod())
                && currentUser.getBalance() < cartService.getCart().getPayableAmount()) {
            redirectAttributes.addFlashAttribute("error", "Insufficient wallet balance.");
            return "redirect:/";
        }

        Customer customer = new Customer(
                "CUS-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
                currentUser.getFullName(),
                currentUser.getEmail(),
                checkoutForm.getShippingAddress()
        );

        Order order = orderService.checkout(customer, cartService.getCart(), checkoutForm.getPaymentMethod());
        if (order.getStatus() == OrderStatus.PAID) {
            catalogService.recordPurchase(cartService.getCart());
        }
        if ("WALLET".equalsIgnoreCase(checkoutForm.getPaymentMethod()) && order.getStatus() == OrderStatus.PAID) {
            currentUser.setBalance(currentUser.getBalance() - order.getPayableAmount());
            session.setAttribute("currentUser", currentUser);
        }
        cartService.clear();
        redirectAttributes.addFlashAttribute("message", "Order " + order.getOrderId() + " created successfully.");
        return "redirect:/";
    }

    @PostMapping("/api/checkout")
    @ResponseBody
    public Map<String, Object> checkoutAsync(@Valid @ModelAttribute CheckoutForm checkoutForm,
                                             BindingResult bindingResult,
                                             HttpSession session) {
        SessionUser currentUser = (SessionUser) session.getAttribute("currentUser");

        if (currentUser == null) {
            return errorResponse("Please login before checkout.");
        }

        if (cartService.getCart().isEmpty()) {
            return errorResponse("Cart is empty.");
        }

        if (bindingResult.hasErrors()) {
            return errorResponse("Please complete the checkout form.", bindingResult);
        }

        if ("CARD".equalsIgnoreCase(checkoutForm.getPaymentMethod())) {
            String cardValidationError = validateCheckoutCard(currentUser, checkoutForm);
            if (cardValidationError != null) {
                return errorResponse(cardValidationError);
            }
        }

        if ("WALLET".equalsIgnoreCase(checkoutForm.getPaymentMethod())
                && currentUser.getBalance() < cartService.getCart().getPayableAmount()) {
            return errorResponse("Insufficient wallet balance.");
        }

        Customer customer = new Customer(
                "CUS-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
                currentUser.getFullName(),
                currentUser.getEmail(),
                checkoutForm.getShippingAddress()
        );

        Order order = orderService.checkout(customer, cartService.getCart(), checkoutForm.getPaymentMethod());
        if (order.getStatus() == OrderStatus.PAID) {
            catalogService.recordPurchase(cartService.getCart());
        }
        if ("WALLET".equalsIgnoreCase(checkoutForm.getPaymentMethod()) && order.getStatus() == OrderStatus.PAID) {
            currentUser.setBalance(currentUser.getBalance() - order.getPayableAmount());
            session.setAttribute("currentUser", currentUser);
        }
        cartService.clear();
        return successResponse("Order " + order.getOrderId() + " created successfully.", session);
    }

    @GetMapping("/api/state")
    @ResponseBody
    public Map<String, Object> state(HttpSession session) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("state", buildState(session));
        return response;
    }

    private void populateSharedModel(Model model, HttpSession session) {
        SessionUser currentUser = session == null ? null : (SessionUser) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("products", catalogService.getAllProducts());
        model.addAttribute("salesCounts", catalogService.getSalesCounts());
        model.addAttribute("campaigns", campaignService.getCampaigns());
        model.addAttribute("cart", cartService.getCart());
        model.addAttribute("orders", orderService.getOrders());
        if (!model.containsAttribute("loginForm")) {
            LoginForm loginForm = new LoginForm();
            loginForm.setRole(currentUser != null ? currentUser.getRole() : "CUSTOMER");
            if (currentUser != null) {
                loginForm.setFullName(currentUser.getFullName());
                loginForm.setEmail(currentUser.getEmail());
            }
            model.addAttribute("loginForm", loginForm);
        }
        if (!model.containsAttribute("checkoutForm")) {
            CheckoutForm checkoutForm = new CheckoutForm();
            checkoutForm.setPaymentMethod("CARD");
            model.addAttribute("checkoutForm", checkoutForm);
        }
    }

    private Map<String, Object> successResponse(String message, HttpSession session) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("state", buildState(session));
        return response;
    }

    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    private Map<String, Object> errorResponse(String message, BindingResult bindingResult) {
        Map<String, Object> response = errorResponse(message);
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        bindingResult.getFieldErrors().forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));
        response.put("fieldErrors", fieldErrors);
        return response;
    }

    private Map<String, Object> buildState(HttpSession session) {
        SessionUser currentUser = session == null ? null : (SessionUser) session.getAttribute("currentUser");

        Map<String, Object> state = new LinkedHashMap<>();
        state.put("currentUser", currentUser == null ? null : Map.of(
                "fullName", currentUser.getFullName(),
                "email", currentUser.getEmail(),
                "role", currentUser.getRole(),
                "balance", currentUser.getBalance(),
                "maskedCardNumber", currentUser.getMaskedCardNumber(),
                "hasSavedCard", currentUser.hasSavedCard()
        ));
        state.put("stats", Map.of(
                "products", catalogService.getAllProducts().size(),
                "cartUnits", cartService.getCart().getTotalUnits(),
                "orders", orderService.getOrders().size()
        ));
        state.put("campaigns", campaignService.getCampaigns().stream().map(campaign -> Map.of(
                "title", campaign.getTitle(),
                "headline", campaign.getHeadline(),
                "description", campaign.getDescription(),
                "primary", campaign.isPrimary()
        )).toList());
        state.put("products", catalogService.getAllProducts().stream().map(product -> Map.of(
                "id", product.getId(),
                "name", product.getName(),
                "category", product.getCategory(),
                "basePrice", product.getBasePrice(),
                "finalPrice", product.getFinalPrice(),
                "sales", catalogService.getSalesCount(product.getId())
        )).toList());
        state.put("cart", Map.of(
                "items", cartService.getCart().getItems().stream().map(item -> Map.of(
                        "productId", item.getProduct().getId(),
                        "productName", item.getProduct().getName(),
                        "quantity", item.getQuantity(),
                        "lineTotal", item.getLineTotal()
                )).toList(),
                "payableAmount", cartService.getCart().getPayableAmount(),
                "empty", cartService.getCart().isEmpty()
        ));
        state.put("orders", orderService.getOrders().stream().map(order -> Map.of(
                "orderId", order.getOrderId(),
                "customerName", order.getCustomer().getFullName(),
                "shippingAddress", order.getCustomer().getShippingAddress(),
                "status", order.getStatus().name(),
                "paymentMethod", order.getPaymentMethod(),
                "payableAmount", order.getPayableAmount(),
                "items", order.getItems().stream().map(item -> Map.of(
                        "productName", item.getProductName(),
                        "quantity", item.getQuantity(),
                        "lineTotal", item.getLineTotal()
                )).toList()
        )).toList());
        return state;
    }

    private ProfileForm buildProfileForm(SessionUser currentUser) {
        ProfileForm profileForm = new ProfileForm();
        profileForm.setFirstName(currentUser.getFirstName());
        profileForm.setLastName(currentUser.getLastName());
        profileForm.setEmail(currentUser.getEmail());
        profileForm.setShippingAddress(currentUser.getShippingAddress());
        profileForm.setCardNumber(currentUser.getCardNumber());
        return profileForm;
    }

    private double parseAmount(String rawAmount, Map<String, String> fieldErrors) {
        if (rawAmount == null || rawAmount.trim().isBlank()) {
            fieldErrors.put("amount", "Enter top up amount.");
            return 0;
        }

        try {
            double amount = Double.parseDouble(rawAmount.trim());
            if (amount <= 0) {
                fieldErrors.put("amount", "Amount must be greater than 0.");
            }
            return amount;
        } catch (NumberFormatException exception) {
            fieldErrors.put("amount", "Enter a valid amount.");
            return 0;
        }
    }

    private Map<String, Object> errorResponse(String message, Map<String, String> fieldErrors) {
        Map<String, Object> response = errorResponse(message);
        response.put("fieldErrors", fieldErrors);
        return response;
    }

    private boolean isAdmin(SessionUser currentUser) {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String validateCheckoutCard(SessionUser currentUser, CheckoutForm checkoutForm) {
        String cardOption = safe(checkoutForm.getCardOption()).toUpperCase();
        if ("SAVED".equals(cardOption)) {
            if (!currentUser.hasSavedCard()) {
                return "No saved card found. Choose new card.";
            }
            return null;
        }

        if ("NEW".equals(cardOption)) {
            String newCardNumber = safe(checkoutForm.getCheckoutCardNumber());
            if (newCardNumber.isBlank()) {
                return "Enter a new card number for payment.";
            }
            currentUser.setCardNumber(newCardNumber);
            return null;
        }

        return "Choose saved card or new card.";
    }
}
