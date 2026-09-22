# 🏥 CareConnect - Clinic Appointment & Patient Scheduling System

[![Java](https://img.shields.io/badge/Java-22%20%7C%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-6DB33F?style=for-the-badge&logo=hibernate&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208.0-00758F?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Frontend](https://img.shields.io/badge/Frontend-HTML5%20%7C%20CSS3%20%7C%20JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)](https://developer.mozilla.org/en-US/docs/Web/JavaScript)

**CareConnect** is a full-stack healthcare clinic appointment management portal built strictly using **HTML, CSS, JavaScript, Java, Spring Boot, and MySQL**. It features layered enterprise architecture, Role-Based Access Control (RBAC), database concurrency constraints, digital medical prescription issuance, and real-time clinic analytics in **Indian Rupees (₹ INR)**.

---

## 🛠️ Core Technology Stack

| Layer | Technology | Description |
| :--- | :--- | :--- |
| **Backend Language** | **Java (v22 / v17)** | Core business logic, object-oriented domain models, and validation |
| **Backend Framework** | **Spring Boot (v3.3.4)** | Layered MVC architecture (`Controller`, `Service`, `Repository`) |
| **ORM / Persistence** | **Spring Data JPA & Hibernate** | Relational mapping, transactional queries, and compound indexes |
| **Database** | **MySQL 8.0** | Relational database (`careconnect_db`), foreign keys & unique constraints |
| **Frontend UI** | **HTML5 & CSS3** | Responsive layouts, modern cards, glassmorphism modals, print stylesheets |
| **Frontend Logic** | **Vanilla JavaScript** | Asynchronous API communication using the native `fetch` API |
| **Currency** | **Indian Rupees (₹ INR)** | Authentic Indian clinic consultation fees (₹500 - ₹1,200) |

---

## 🚀 Key Highlights for Fresher Interviews

* **Layered Enterprise Architecture:** Clean separation of concerns across:
  $$\text{Controller} \longrightarrow \text{Service} \longrightarrow \text{Repository} \longrightarrow \text{Entity} \longrightarrow \text{MySQL Database}$$
* **Role-Based Access Control (RBAC):** Three distinct user personas:
  * 🧑‍💼 **Patient (Rahul Sharma):** Browse specialists, check real-time availability, book OPD slots, view history, cancel visits, and view/print digital prescriptions.
  * 🩺 **Doctor (Dr. Rajesh Iyer):** View daily schedule and patient queue, review reported symptoms, update consultation status, and write diagnosis notes and Rx prescriptions.
  * 🛡️ **Administrator (Arun Kumar):** Clinic overview dashboard with financial KPIs (in ₹), appointment status metrics (Chart.js), master appointment logs, and doctor directory management.
* **Database Concurrency & Conflict Prevention:** Strict relational constraint `UNIQUE(doctor_id, appointment_date, time_slot)` preventing double-booking at both the service layer and the MySQL database layer.
* **Auto Data Seeding:** Automatically seeds 6 certified Indian doctors from premier medical institutes (AIIMS, CMC Vellore, NIMHANS, MMC, PGIMER, KMC), demo patients, and appointments into MySQL on first boot.

---

## 📂 Project Structure

```
d:\project
├── pom.xml                                 # Maven dependencies (Spring Web, JPA, MySQL)
├── schema.sql                              # Pure MySQL 8.x schema DDL script
├── README.md                               # Project documentation & interview guide
├── .vscode/                                # VS Code 1-click Run & Debug configuration
│   ├── launch.json                         # F5 execution config
│   ├── tasks.json                          # Maven build tasks
│   ├── extensions.json                     # Recommended extensions
│   └── settings.json                       # Java compiler preferences
└── src
    └── main
        ├── java/com/careconnect
        │   ├── CareConnectApplication.java # Spring Boot entry point
        │   ├── config
        │   │   ├── DataInitializer.java    # Seeds Indian doctors, patients, and bookings into MySQL
        │   │   └── WebConfig.java          # Static resource handlers & CORS config
        │   ├── controller
        │   │   ├── AuthController.java     # Register, Login, User profile REST APIs
        │   │   ├── DoctorController.java   # Doctor search, slot availability, admin CRUD
        │   │   ├── AppointmentController.java # Booking, queue, status updates, prescriptions
        │   │   └── DashboardController.java # KPI stats & analytics data
        │   ├── dto                         # Clean Request & Response DTOs
        │   ├── entity                      # JPA Database Entities (User, Doctor, Appointment)
        │   ├── exception                   # Centralized REST Exception Handling (@RestControllerAdvice)
        │   ├── repository                  # Spring Data JPA Repositories
        │   └── service                     # Business Logic Layer
        └── resources
            ├── application.properties      # MySQL connection configuration
            └── static                      # Frontend Web Assets
                ├── index.html              # Pure HTML5 interface
                ├── css/style.css           # Custom CSS styling & status badges
                └── js/app.js               # Pure JavaScript with fetch API
```

---

## ⚡ How to Run in Visual Studio Code

### 1. Configure MySQL Database
Make sure your MySQL server is running. In [`src/main/resources/application.properties`](file:///d:/project/src/main/resources/application.properties), verify your MySQL credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/careconnect_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=Arun@2006
```

### 2. Run the Application in VS Code
* Open **`D:\project`** in VS Code.
* Open [`src/main/java/com/careconnect/CareConnectApplication.java`](file:///d:/project/src/main/java/com/careconnect/CareConnectApplication.java).
* Click the **`▶ Run`** button right above `public static void main` (or press **`F5`**).
* Alternatively, run in the VS Code terminal:
  ```powershell
  mvn spring-boot:run
  ```

### 3. Open in Browser
* **Web Portal:** **[http://localhost:8080](http://localhost:8080)**

---

## 🔑 Pre-Seeded Demo Credentials (Indian Context)

Use the **1-Click Quick Role Switcher** at the top bar of the webpage:

| Role | Name | Email | Password | Consultation Fee / Access |
| :--- | :--- | :--- | :--- | :--- |
| **Patient** | Rahul Sharma | `rahul@gmail.com` | `patient123` | Books OPD slots, views history, prints prescriptions |
| **Doctor** | Dr. Rajesh Iyer (Cardiology) | `dr.rajesh@careconnect.in` | `doctor123` | Fee: ₹1,000 &bull; AIIMS New Delhi &bull; Issues Rx |
| **Doctor** | Dr. Suresh Menon (Neurology) | `dr.suresh@careconnect.in` | `doctor123` | Fee: ₹1,200 &bull; NIMHANS Bangalore &bull; Issues Rx |
| **Doctor** | Dr. Priya Sharma (Dermatology) | `dr.priya@careconnect.in` | `doctor123` | Fee: ₹750 &bull; CMC Vellore &bull; Issues Rx |
| **Doctor** | Dr. Arvind Swaminathan (Ortho) | `dr.arvind@careconnect.in` | `doctor123` | Fee: ₹900 &bull; MMC Chennai &bull; Issues Rx |
| **Doctor** | Dr. Kavitha Raman (Pediatrics) | `dr.kavitha@careconnect.in` | `doctor123` | Fee: ₹650 &bull; PGIMER Chandigarh &bull; Issues Rx |
| **Doctor** | Dr. Amit Verma (General Med) | `dr.amit@careconnect.in` | `doctor123` | Fee: ₹500 &bull; KMC Manipal &bull; Issues Rx |
| **Admin** | Arun Kumar | `admin@careconnect.in` | `admin123` | Views revenue KPIs (₹), Chart.js graphs, registers doctors |

---

## 🎯 How to Explain This Project in an Interview

When asked: *"Tell me about your Java project."*

1. **Tech Stack Overview:**
   > *"I built CareConnect using a focused, industry-standard stack: Java and Spring Boot for the backend RESTful service layer, MySQL for relational database persistence, and HTML5, CSS3, and JavaScript on the frontend."*
2. **Architecture:**
   > *"I followed a clean layered architecture with separation of concerns: Controller handles HTTP requests and input validation (`@Valid`), Service implements business logic and transactional integrity (`@Transactional`), Repository interfaces with MySQL via Spring Data JPA, and the frontend consumes JSON APIs using modern JavaScript `fetch`."*
3. **Database Design & Concurrency:**
   > *"In MySQL, I designed normalized tables with foreign keys and indexes. To avoid double-booking doctor slots, I enforced compound unique constraints on `(doctor_id, appointment_date, time_slot)`, backed by service-level validation."*
