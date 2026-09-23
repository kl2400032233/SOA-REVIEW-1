# Frontend Application Specification (Future Implementation)

> **Note for SOA Review 1**:
> This folder is a designated placeholder for the future client-side user interface. In accordance with Review 1 specifications, the frontend will be developed in subsequent phases using **React.js** and **JavaScript**.

---

## Architecture & Communication Strategy

In accordance with Service-Oriented Architecture (SOA) design principles:
- The React frontend will **NEVER** communicate directly with the backend microservice internal ports (`8081`, `8082`, `8083`).
- All HTTP/REST communications **MUST** be routed exclusively through the **API Gateway**:
  ```
  http://localhost:8080
  ```
- Authentication is strictly token-based via **JSON Web Tokens (JWT)**.
- Once authenticated via `/api/auth/login`, the frontend will store the Bearer JWT (e.g., in `sessionStorage` or application state) and attach it to the `Authorization` header for all subsequent protected API calls:
  ```http
  Authorization: Bearer <JWT_TOKEN>
  ```

---

## Target Frontend Tech Stack

- **Framework**: React.js (v18+)
- **Language**: JavaScript (ES6+)
- **State Management**: Context API / Redux Toolkit
- **HTTP Client**: Axios with request/response interceptors for automatic JWT injection and 401 handling
- **Routing**: React Router DOM (v6+) with Protected Routes based on user role (`CUSTOMER`, `ADMIN`, `STAFF`)

---

## Expected API Endpoints (Gateway Port :8080)

### 1. Authentication & User Profile
| Action | Method | Gateway Path | Required Role / Auth |
|---|---|---|---|
| Register User | `POST` | `/api/auth/register` | Public |
| User Login | `POST` | `/api/auth/login` | Public |
| Get User by ID | `GET` | `/api/users/{id}` | Authenticated (Bearer Token) |
| Get All Users | `GET` | `/api/users` | Authenticated (Admin/Staff) |

### 2. Menu Management
| Action | Method | Gateway Path | Required Role / Auth |
|---|---|---|---|
| View Available Menu | `GET` | `/api/menu` | Public / Customer / Staff / Admin |
| View Menu Item by ID | `GET` | `/api/menu/{id}` | Authenticated (Bearer Token) |
| Add Menu Item | `POST` | `/api/menu` | `ADMIN` only |
| Update Menu Item | `PUT` | `/api/menu/{id}` | `ADMIN` only |
| Delete Menu Item | `DELETE`| `/api/menu/{id}` | `ADMIN` only |
| Update Availability | `PATCH`| `/api/menu/{id}/availability` | `ADMIN` only |

### 3. Order Placement & Kitchen Execution
| Action | Method | Gateway Path | Required Role / Auth |
|---|---|---|---|
| Place Order | `POST` | `/api/orders` | `CUSTOMER` |
| View Order by ID | `GET` | `/api/orders/{id}` | `CUSTOMER` (own order), `STAFF`, `ADMIN` |
| View Customer Orders | `GET` | `/api/orders/user/{userId}` | `CUSTOMER` (own orders), `STAFF`, `ADMIN` |
| View All Orders (Kitchen) | `GET` | `/api/orders` | `STAFF`, `ADMIN` |
| Update Order Status | `PUT` | `/api/orders/{id}/status` | `STAFF` only |

---

## Planned User Interface Channels

1. **Customer Channel (Self-Service Ordering)**:
   - Dynamic catalog showing currently available items.
   - Interactive cart with quantity adjustments.
   - Real-time order placement and live kitchen status tracker (`PLACED` -> `PREPARING` -> `READY` -> `DELIVERED`).

2. **Staff / Kitchen Execution Display (KDS)**:
   - Live incoming order queue with item breakdown.
   - One-click order status transitions (`CONFIRMED` -> `PREPARING` -> `READY` -> `OUT_FOR_DELIVERY` -> `DELIVERED`).

3. **Admin Portal**:
   - Menu inventory CRUD panel.
   - Instant toggle for item availability (`AVAILABLE` / `UNAVAILABLE`).
   - Customer and system monitoring.
