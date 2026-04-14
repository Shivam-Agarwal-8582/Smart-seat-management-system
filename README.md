# Smart Seat Booking System 🚀

A modern, automated Hybrid Workspace Management System designed to solve post-COVID seating logistics. This full-stack application strictly enforces office attendance rules, manages dynamic batches, and handles seat reservations asynchronously with custom waitlists.

![Smart Seat Booking UI](https://via.placeholder.com/800x400.png?text=Smart+Seat+Booking+System)

## 🌟 Key Features

*   **Rule-Driven Reservations:** Enforces strict booking logic. Users can only book on days their assigned "Batch" is allowed in the office.
*   **Time-Locked Windows:** Fair-play booking system locking the calendar until exactly 3:00 PM for the following day.
*   **Automated Waitlist Queue:** When a booked seat is canceled, the backend automatically finds the first person in the waitlist queue and assigns the seat to them.
*   **Concurrency Safe:** Utilizes Database-level `@UniqueConstraint` to ensure 100% data integrity and prevent double-booking race conditions during high traffic.
*   **Seat Types:** Supports both "Floating" (first-come, first-served) and "Fixed" (permanently assigned) desks.
*   **Real-Time Analytics:** Dynamic occupancy tracking and calendar integration.

## 🛠️ Technology Stack

**Frontend:**
*   React + Vite
*   Framer Motion (Animations)
*   Lucide React (Icons)
*   Custom Glassmorphism CSS Design

**Backend:**
*   Java 17
*   Spring Boot 3
*   Spring Data JPA
*   Hibernate

**Database:**
*   MySQL (Relational mapping & transaction concurrency)

## 📦 Project Structure

```text
smart-seat-booking/
├── backend/          # Spring Boot Application
│   ├── src/main/java/com/smartbooking/
│   │   ├── config/      # Data Initialization 
│   │   ├── controller/  # REST APIs
│   │   ├── model/       # Entities (User, Booking, Seat, etc.)
│   │   ├── repository/  # JPA Repositories
│   │   └── service/     # Business Logic & Rules
│   └── pom.xml
└── frontend/         # React Application
    ├── src/
    │   ├── App.jsx      # Main Application UI & State
    │   ├── index.css    # Global Styles & Glassmorphism
    │   └── main.jsx
    └── package.json
```

## 🚀 Getting Started

### Prerequisites
*   Node.js (v18+)
*   Java (Version 17+)
*   Maven
*   MySQL Server (Running on default port `3306`)

### 1. Database Setup
Create the MySQL database before starting the backend:
```sql
CREATE DATABASE smart_booking;
```

### 2. Backend Setup
Navigate to the backend directory and run the Spring Boot application. 
*(Note: The embedded `DataInitializer` will automatically populate the database with default Batches, Seats, and Users on the first run).*
```bash
cd backend
mvn spring-boot:run
```
*The backend will run on `http://localhost:8080`*

### 3. Frontend Setup
Navigate to the frontend directory, install dependencies, and start the Vite dev server:
```bash
cd frontend
npm install
npm run dev
```
*The frontend will run on `http://localhost:5173`*

## 🧑‍💻 Usage & Testing

When the application loads:
1.  **Switch Users:** Use the dropdown in the header to switch between different employees (User 1-10 -> Batches A & B).
2.  **Test the Rules:** Try to book a seat on a day not assigned to your Batch. The system will reject it.
3.  **Test the Time Lock:** Try to book a seat before 3:00 PM. The system will block the booking. *(For testing, you can modify `BookingService.java` to bypass this constraint).*
4.  **Test the Waitlist:** Have User A book a seat. Have User B join the waitlist for that same seat. When User A cancels their booking, User B will automatically receive the reservation.

## 📝 License
This project is open-source and available under the MIT License.
