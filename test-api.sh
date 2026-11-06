#!/usr/bin/env bash
# =============================================================================
# NexusMarket - End-to-end API demonstration (business flow of section 6.1)
#
# Usage:
#   Terminal 1:  ./mvnw spring-boot:run
#   Terminal 2:  ./test-api.sh            (optionally: BASE_URL=http://host:port ./test-api.sh)
#
# Every request (except buyer self-registration and login) identifies the
# authenticated user with the X-User-Email header (RG-01).
# The script stops at the first unexpected HTTP status.
# =============================================================================
set -u

BASE_URL="${BASE_URL:-http://localhost:8080}"
ADMIN="admin@nexusmarket.com"          # created automatically at startup
SELLER="seller@techstore.com"
BUYER="buyer@mail.com"
OPERATOR="operator@nexusmarket.com"
SUPERVISOR="supervisor@nexusmarket.com"

GREEN='\033[0;32m'; RED='\033[0;31m'; CYAN='\033[0;36m'; NC='\033[0m'
STEP=0

# request <expected-status> <description> <method> <path> <user-email|-> [json-body]
request() {
  local expected="$1" description="$2" method="$3" path="$4" user="$5" body="${6:-}"
  STEP=$((STEP + 1))
  echo -e "\n${CYAN}[$STEP] $description${NC}"
  echo "    $method $path   (user: $user)"
  local args=(-s -o /tmp/nexusmarket_response.json -w "%{http_code}" -X "$method" "$BASE_URL$path")
  [ "$user" != "-" ] && args+=(-H "X-User-Email: $user")
  [ -n "$body" ] && args+=(-H "Content-Type: application/json" -d "$body")
  local status
  status=$(curl "${args[@]}")
  if command -v python3 >/dev/null 2>&1; then
    python3 -m json.tool /tmp/nexusmarket_response.json 2>/dev/null | head -40 || cat /tmp/nexusmarket_response.json
  else
    cat /tmp/nexusmarket_response.json
  fi
  if [ "$status" == "$expected" ]; then
    echo -e "    ${GREEN}HTTP $status (expected $expected)${NC}"
  else
    echo -e "    ${RED}HTTP $status (expected $expected) - stopping${NC}"
    exit 1
  fi
}

echo "NexusMarket API demo against $BASE_URL"

# --- 1. Users (Domain 1, 2) --------------------------------------------------
request 401 "RG-01: a request without an authenticated user is rejected" GET /api/products -
request 200 "Login of the initial Administrator" POST /api/auth/login - "{\"email\":\"$ADMIN\"}"
request 201 "Admin registers a Logistics Operator" POST /api/users "$ADMIN" \
  "{\"identifier\":\"OP-001\",\"identityDocument\":\"CC-2000000001\",\"fullName\":\"Olivia Operator\",\"email\":\"$OPERATOR\",\"role\":\"OPERATOR_LOGISTIC\"}"
request 201 "Admin registers a Supervisor" POST /api/users "$ADMIN" \
  "{\"identifier\":\"SUP-001\",\"identityDocument\":\"CC-3000000001\",\"fullName\":\"Sam Supervisor\",\"email\":\"$SUPERVISOR\",\"role\":\"SUPERVISOR\"}"
request 201 "Buyer self-registration" POST /api/users/register - \
  "{\"identifier\":\"BUY-001\",\"identityDocument\":\"CC-4000000001\",\"fullName\":\"Bob Buyer\",\"email\":\"$BUYER\",\"mainAddress\":\"742 Evergreen Terrace, Springfield\",\"additionalAddresses\":[\"Office 12, Main St\"],\"commercialStatus\":\"ENABLED\"}"
request 409 "Identity document must be unique" POST /api/users/register - \
  "{\"identifier\":\"BUY-002\",\"identityDocument\":\"CC-4000000001\",\"fullName\":\"Copy Cat\",\"email\":\"copy@mail.com\",\"mainAddress\":\"Somewhere\",\"commercialStatus\":\"ENABLED\"}"
request 400 "Sellers cannot self-register / be created as plain users" POST /api/users "$ADMIN" \
  "{\"identifier\":\"SEL-X\",\"identityDocument\":\"NIT-X\",\"fullName\":\"X\",\"email\":\"x@x.com\",\"role\":\"SELLER\"}"

# --- 2. Incorporation: seller + first warehouse (Domain 3, 4) ----------------
request 403 "RG-03: the Supervisor cannot register sellers" POST /api/sellers "$SUPERVISOR" '{}'
request 201 "Admin incorporates the seller together with its first warehouse" POST /api/sellers "$ADMIN" \
  "{\"identifier\":\"SEL-001\",\"identityDocument\":\"NIT-900123456\",\"fullName\":\"Tech Store\",\"email\":\"$SELLER\",\"taxIdentifier\":\"TAX-900123456\",\"corporateName\":\"Tech Store S.A.S.\",\"firstWarehouse\":{\"warehouseId\":\"WH-SEL-001\",\"name\":\"Tech Store Warehouse\",\"location\":\"Industrial Zone 5, Bogota\"}}"
request 201 "Admin registers a Marketplace warehouse" POST /api/warehouses "$ADMIN" \
  '{"warehouseId":"WH-MKT-001","name":"NexusMarket Hub","location":"Central Avenue 100, Bogota","type":"MARKETPLACE"}'

# --- 3. Catalog (Domain 5) ---------------------------------------------------
request 201 "Seller registers a PHYSICAL product (status SUSPENDED: not visible yet)" POST /api/products "$SELLER" \
  '{"productId":"PROD-001","sku":"LAPTOP-PRO-16","name":"Laptop Pro 16","description":"16GB RAM, 1TB SSD","price":1500.00,"type":"PHYSICAL","status":"SUSPENDED","variants":[{"name":"Color","value":"Silver"}]}'
request 201 "Seller registers a DIGITAL product (PUBLISHED)" POST /api/products "$SELLER" \
  '{"productId":"PROD-002","sku":"EBOOK-JAVA","name":"Clean Java (e-book)","price":25.00,"type":"DIGITAL","status":"PUBLISHED"}'
request 403 "RG-03: the Administrator cannot register products" POST /api/products "$ADMIN" \
  '{"productId":"PROD-X","sku":"X","name":"X","price":1,"type":"DIGITAL","status":"PUBLISHED"}'
request 201 "Seller adds a variant" POST /api/products/PROD-001/variants "$SELLER" '{"name":"Size","value":"16 inch"}'

# --- 4. Inventory (Domain 6) -------------------------------------------------
request 201 "Seller registers initial stock (INFLOW) in its warehouse" POST /api/inventory/add "$SELLER" \
  '{"productId":"PROD-001","warehouseId":"WH-SEL-001","quantity":10}'
request 400 "Digital products do not have inventory" POST /api/inventory/add "$SELLER" \
  '{"productId":"PROD-002","warehouseId":"WH-SEL-001","quantity":10}'
request 403 "Buyers never manage inventory" POST /api/inventory/add "$BUYER" \
  '{"productId":"PROD-001","warehouseId":"WH-SEL-001","quantity":10}'
request 200 "Operator marks 2 units as damaged (ADJUSTMENT)" POST /api/inventory/mark-damaged "$OPERATOR" \
  '{"productId":"PROD-001","warehouseId":"WH-SEL-001","quantity":2}'

# --- 5. Publication ----------------------------------------------------------
request 400 "A non-published product cannot be added to the cart" POST /api/carts/BUY-001/items "$BUYER" '{"productId":"PROD-001","quantity":1}'
request 200 "Seller publishes the product" PUT /api/products/PROD-001/publish "$SELLER"
request 200 "Public catalog (only PUBLISHED products)" GET /api/products "$BUYER"

# --- 6. Purchase: cart -> pending payment (Domain 7) -------------------------
request 200 "Buyer adds 2 laptops to its cart (price taken from the catalog)" POST /api/carts/BUY-001/items "$BUYER" '{"productId":"PROD-001","quantity":2}'
request 200 "Buyer adds the e-book to its cart" POST /api/carts/BUY-001/items "$BUYER" '{"productId":"PROD-002","quantity":1}'
request 400 "Quantity must be greater than zero" POST /api/carts/BUY-001/items "$BUYER" '{"productId":"PROD-001","quantity":0}'
request 201 "Checkout: CART -> PENDING_PAYMENT, stock is RESERVED" POST /api/orders/checkout/BUY-001 "$BUYER" '{"orderId":"ORD-001"}'
request 200 "Inventory after the reservation" GET "/api/inventory?productId=PROD-001&warehouseId=WH-SEL-001" "$OPERATOR"

# --- 7. Transaction ----------------------------------------------------------
request 200 "Payment validated: PENDING_PAYMENT -> PAID, invoice issued" POST /api/orders/ORD-001/pay "$BUYER" '{"paymentToken":"tok_visa_4242"}'
request 200 "Buyer consults the invoice of its order" GET /api/invoices/order/ORD-001 "$BUYER"
request 200 "Seller consults the orders that contain its products" GET /api/orders "$SELLER"

# --- 8. Logistics ------------------------------------------------------------
request 201 "Operator prepares (packs) the shipment" POST /api/shipments/prepare "$OPERATOR" '{"shipmentId":"SHP-001","orderId":"ORD-001"}'
request 200 "Operator dispatches: PAID -> DISPATCHED (OUTFLOW_SALE)" PUT /api/shipments/SHP-001/dispatch "$OPERATOR" '{"carrier":"Servientrega","trackingNumber":"SRV-123456"}'
request 200 "Operator confirms delivery: DISPATCHED -> DELIVERED_FINALIZED" PUT /api/shipments/SHP-001/confirm-delivery "$OPERATOR"
request 200 "Final state of the order" GET /api/orders/ORD-001 "$BUYER"

# --- 9. Digital product: immediate delivery after payment --------------------
request 200 "Buyer adds another e-book" POST /api/carts/BUY-001/items "$BUYER" '{"productId":"PROD-002","quantity":1}'
request 201 "Checkout of a digital-only order" POST /api/orders/checkout/BUY-001 "$BUYER" '{"orderId":"ORD-002"}'
request 200 "Payment: the digital order is DELIVERED_FINALIZED immediately" POST /api/orders/ORD-002/pay "$BUYER" '{"paymentToken":"tok_visa_4242"}'

# --- 10. Returns and refunds -------------------------------------------------
request 201 "Buyer requests the return of the laptops" POST /api/returns/request "$BUYER" '{"returnId":"RET-001","orderId":"ORD-001","productId":"PROD-001","reason":"DEFECTIVE"}'
request 200 "Admin approves the return" PUT /api/returns/RET-001/approve "$ADMIN"
request 201 "Admin receives the items (RETURN movement) and refunds the amount paid" POST /api/returns/process-refund "$ADMIN" '{"refundId":"REF-001","returnId":"RET-001","reusable":true}'

# --- 11. Administrative consultation -----------------------------------------
request 200 "Supervisor consults the administrative report" GET /api/reports/summary "$SUPERVISOR"
request 200 "Supervisor consults the audit trail of the order (MongoDB)" GET /api/audit/aggregate/ORD-001 "$SUPERVISOR"
request 403 "The Supervisor cannot execute operations (only consult)" PUT /api/users/BUY-001/block "$SUPERVISOR"

echo -e "\n${GREEN}All $STEP steps finished with the expected HTTP status.${NC}"
