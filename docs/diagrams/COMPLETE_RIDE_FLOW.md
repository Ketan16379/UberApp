# Complete UberApp ride flow

This is the complete working flow implemented by the project. It follows a user
from signup to a completed ride and payment.

## 1. Actors and records

There are three important actors:

- **Rider**: requests and pays for a ride.
- **Driver**: accepts and performs the ride.
- **Admin**: onboards an existing user as a driver.

The main records created during the flow are:

```text
User
 ├── Rider profile
 ├── Driver profile (after admin onboarding)
 └── Wallet

Rider -> RideRequest -> Ride -> Payment
                              └── Rating
```

## 2. Application startup

Before using the APIs:

1. Start PostgreSQL with PostGIS enabled.
2. Check the datasource URL, username and password in
   `src/main/resources/application.properties`.
3. Start the application:

```powershell
.\mvnw.cmd spring-boot:run
```

The configured `spring.jpa.hibernate.ddl-auto=create` recreates the schema on
startup. `data.sql` is loaded afterward, so this configuration is intended for
development/demo use.

## 3. Step 1 - rider signup

**Request**

```http
POST /auth/signup
Content-Type: application/json
```

Example body:

```json
{
  "name": "Rider One",
  "email": "rider@example.com",
  "password": "Password123"
}
```

**Processing**

`AuthController` delegates to `AuthServiceImpl.signup`.

1. The email is checked in `UserRepository`.
2. The password is encoded with `BCryptPasswordEncoder`.
3. A `User` is created with the `RIDER` role.
4. A `Rider` profile is created with an initial rating.
5. A `Wallet` is created for the user.
6. The user, rider profile and wallet are saved in one transaction.

**Result**

The API returns `UserDto` with HTTP `201 Created`.

```text
User(id, email, BCrypt password, RIDER)
Rider(user, rating = 0.0)
Wallet(user, balance = 0)
```

## 4. Step 2 - driver onboarding

A driver profile is created for an existing user by an administrator.

**Request**

```http
POST /auth/onBoardNewDriver/{userId}
Authorization: Bearer <admin-access-token>
Content-Type: application/json
```

Example body:

```json
{
  "vehicleId": "CAR-1001"
}
```

**Processing**

1. `@Secured("ROLE_ADMIN")` verifies the caller.
2. `AuthServiceImpl` loads the user.
3. It rejects the operation if the user is already a driver.
4. The `DRIVER` role is added to the user's roles.
5. A `Driver` profile is saved with:
   - the vehicle id;
   - rating `0.0`;
   - `available = true`.

The same user can now act as both rider and driver because roles are stored as a
set.

## 5. Step 3 - rider login

**Request**

```http
POST /auth/login
Content-Type: application/json
```

Example body:

```json
{
  "email": "rider@example.com",
  "password": "Password123"
}
```

**Processing**

1. `AuthenticationManager` loads the user by email.
2. Spring Security compares the submitted password with the BCrypt hash.
3. `JWTService` generates:
   - an access token valid for about 10 minutes;
   - a refresh token valid for about 6 months.
4. The access token is returned in `LoginResponseDTO`.
5. The refresh token is written to an HTTP-only `token` cookie.

Use the access token in later requests:

```http
Authorization: Bearer <access-token>
```

The same login process is used by the driver and admin accounts.

## 6. Step 4 - rider requests a ride

**Request**

```http
POST /riders/requestRide
Authorization: Bearer <rider-access-token>
Content-Type: application/json
```

Example body:

```json
{
  "pickupLocation": {
    "coordinates": [77.2167, 28.6667]
  },
  "dropOffLocation": {
    "coordinates": [77.2500, 28.5500]
  },
  "paymentMethod": "WALLET"
}
```

Coordinates are `[longitude, latitude]`. `GeometryUtil` and `MapperConfig`
convert the coordinate DTO into a JTS/PostGIS `Point` with SRID 4326.

### Internal processing

`RiderController` calls `RiderServiceImpl.requestRide`.

1. The current rider is obtained from the authenticated `User` in
   `SecurityContextHolder`.
2. The DTO is mapped to a `RideRequest`.
3. The rider is attached and status is set to `PENDING`.
4. `RideStrategyManager` selects the fare strategy:
   - 18:00-21:00: surge pricing;
   - otherwise: default pricing.
5. The selected fare strategy calls `DistanceServiceOSRMImpl`.
6. `DistanceServiceOSRMImpl` calls the OSRM routing API:

```text
pickup longitude,latitude;drop-off longitude,latitude
```

7. OSRM returns distance in metres; the service converts it to kilometres.
8. Fare is calculated:

```text
normal fare = distance in km * RIDE_FARE_MULTIPLIER
surge fare  = distance in km * RIDE_FARE_MULTIPLIER * 2
```

9. The `RideRequest` is saved in `RideRequestRepository`.
10. Driver matching is selected from the rider rating:
    - rating >= 4.8: nearby top-rated available drivers;
    - rating < 4.8: nearest available drivers.
11. PostGIS queries search available drivers using `ST_DWithin`.
12. The request is returned as `RideRequestDto`.

At this point:

```text
RideRequest.status = PENDING
RideRequest.fare   = calculated fare
RideRequest.rider  = logged-in rider
```

The current implementation finds matching drivers but does not send a
notification. A driver must therefore call the accept endpoint directly.

## 7. Step 5 - driver accepts the request

The driver logs in and uses the returned ride request id.

**Request**

```http
POST /drivers/acceptRide/{rideRequestId}
Authorization: Bearer <driver-access-token>
```

### Internal processing

`DriverServiceImpl.acceptRide`:

1. Loads the `RideRequest`.
2. Verifies that its status is `PENDING`.
3. Loads the current driver from the authenticated user.
4. Verifies that the driver is available.
5. Sets driver availability to `false`.
6. `RideServiceImpl.createNewRide` changes the request status to `CONFIRMED`.
7. A new `Ride` is copied from the request.
8. The driver is attached to the ride.
9. The ride status is set to `CONFIRMED`.
10. A random four-digit OTP is generated.
11. The `Ride` is saved.

The response is a `RideDto`. Save its `id`; subsequent start, end and rating
operations use the ride id.

```text
RideRequest.status = CONFIRMED
Ride.status        = CONFIRMED
Driver.available   = false
Ride.otp            = four-digit code
```

## 8. Step 6 - driver starts the ride with OTP

The rider shares the OTP with the driver.

**Request**

```http
POST /drivers/startRide/{rideRequestId}
Authorization: Bearer <driver-access-token>
Content-Type: application/json
```

Example body:

```json
{
  "otp": "4821"
}
```

Although the controller parameter is named `rideRequestId`, the service loads
the already-created `Ride` by that id.

### Internal processing

`DriverServiceImpl.startRide`:

1. Loads the ride.
2. Verifies that the current driver owns the ride.
3. Verifies that ride status is `CONFIRMED`.
4. Compares the submitted OTP with the stored OTP.
5. Sets `startedAt`.
6. Changes the ride status to `ONGOING`.
7. Creates a pending `Payment` using the ride fare and payment method.
8. Creates an empty `Rating` record for the ride.

```text
Ride.status      = ONGOING
Ride.startedAt   = current time
Payment.status   = PENDING
Rating           = created for rider and driver
```

## 9. Step 7 - driver completes the ride

**Request**

```http
POST /drivers/endRide/{rideId}
Authorization: Bearer <driver-access-token>
```

### Internal processing

`DriverServiceImpl.endRide`:

1. Loads the ride.
2. Verifies that the current driver owns it.
3. Verifies that status is `ONGOING`.
4. Sets `endedAt`.
5. Changes ride status to `ENDED`.
6. Sets the driver back to `available = true`.
7. Loads the pending payment.
8. Selects a payment strategy based on `PaymentMethod`.
9. Processes the payment.

```text
Ride.status      = ENDED
Ride.endedAt     = current time
Driver.available = true
```

## 10. Step 8 - payment processing

`PaymentServiceImpl.processPayment` loads the payment for the ride and delegates
to `PaymentStrategyManager`.

### Wallet payment

For `paymentMethod = WALLET`:

```text
Rider wallet   -= ride fare
Driver wallet  += ride fare * (1 - platform commission)
Payment.status = CONFIRMED
```

The platform commission is retained by the platform and is not credited to the
driver.

### Cash payment

For `paymentMethod = CASH`:

```text
Rider pays driver in cash
Driver wallet  -= ride fare * platform commission
Payment.status = CONFIRMED
```

The rider's wallet is not charged for a cash ride. The driver wallet is used to
record the platform commission.

Wallet changes are intended to be transactional, and wallet transactions are
associated with the ride. Review the current wallet implementation before using
it for real financial accounting.

## 11. Step 9 - mutual ratings

Ratings are allowed only after the ride reaches `ENDED`.

### Rider rates driver

```http
POST /riders/rateDriver
Authorization: Bearer <rider-access-token>
Content-Type: application/json
```

```json
{
  "rideId": 1,
  "rating": 5
}
```

The service verifies rider ownership, stores `driverRating`, prevents a duplicate
driver rating, calculates the driver's average rating, and updates the driver.

### Driver rates rider

```http
POST /drivers/rateRider
Authorization: Bearer <driver-access-token>
Content-Type: application/json
```

```json
{
  "rideId": 1,
  "rating": 5
}
```

The service performs the equivalent operation for `riderRating` and updates the
rider's average rating.

## 12. Complete state timeline

```text
Signup
  -> User created with RIDER role
  -> Rider profile created
  -> Wallet created

Admin onboarding
  -> DRIVER role added
  -> Driver profile created

Login
  -> Access JWT issued
  -> Refresh token cookie issued

Rider requests ride
  -> Fare calculated through OSRM
  -> Driver candidates selected
  -> RideRequest = PENDING

Driver accepts
  -> Driver unavailable
  -> RideRequest = CONFIRMED
  -> Ride = CONFIRMED
  -> OTP generated

Driver starts with OTP
  -> Ride = ONGOING
  -> Payment = PENDING
  -> Rating record created

Driver ends ride
  -> Ride = ENDED
  -> Driver available
  -> Payment strategy runs
  -> Payment = CONFIRMED

Rider and driver rate each other
  -> Historical averages updated
```

## 13. Alternative cancellation path

Before starting, either participant can cancel a confirmed ride:

```http
POST /riders/cancelRide/{rideId}
POST /drivers/cancelRide/{rideId}
```

The service verifies ownership, changes the ride to `CANCELLED`, and makes the
driver available again. A ride that is already `ONGOING` cannot be cancelled by
these endpoints.

## 14. Response and error flow

Every request passes through Spring MVC and security before reaching a controller.
`GlobalResponseHandler` wraps normal response bodies in `ApiResponse`, except for
Actuator and OpenAPI routes.

`GlobalExceptionHandler` converts:

- `ResourceNotFoundException` to HTTP 404;
- `RuntimeConflictException` to HTTP 409;
- validation failures to HTTP 400;
- other exceptions to HTTP 500.

Ownership checks, invalid ride states and invalid OTPs currently use generic
`RuntimeException`, so those cases are reported as HTTP 500 by the current global
handler.
