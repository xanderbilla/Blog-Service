# Blog Service - Empowering Content Creation with Java Spring Boot 🚀

Welcome to the **Blog Service**, a robust backend solution built with **Java Spring Boot**. This application allows users to create, retrieve, update, and manage blogs efficiently. With AI-powered text summarization and seamless AWS deployment, this project is designed for scalability and high performance. 

## 🌟 Features
### ✅ Blog Management (Spring Boot + JPA)
- Create and manage blog posts with **title, content, author, and timestamps**.
- Retrieve all blogs with **pagination support**.
- Fetch a **single blog** by ID.
- **Update** existing blogs and **delete** if needed.
- Uses **Spring Data JPA** for database operations with PostgreSQL/MySQL.

## 🛠️ Getting Started

### Prerequisites
Before you begin, ensure you have:
- **Java 17+**
- **Spring Boot**
- **PostgreSQL/MySQL**
- **Docker** (for containerization)

### 🔧 Setup & Running Locally
1. **Clone the repository:**
   ```sh
   git clone https://github.com/your-repo/blog-service.git
   cd blog-service
   ```
2. **Configure database connection** in `application.properties` or `application.yml`.
3. **Run the application:**
   ```sh
   ./mvnw spring-boot:run
   ```

## 📡 API Endpoints
| Method | Endpoint                   | Description                  |
|--------|----------------------------|------------------------------|
| **POST**   | `/api/blogs`               | Create a new blog           |
| **GET**    | `/api/blogs`               | Fetch all blogs (paginated) |
| **GET**    | `/api/blogs/{id}`          | Fetch a blog by ID          |
| **PUT**    | `/api/blogs/{id}`          | Update a blog               |
| **DELETE** | `/api/blogs/{id}`          | Delete a blog               |

## 👤 Author

**[Vikas Singh](https://github.com/xanderbilla)**