# Blog Service - Empowering Content Creation with Java Spring Boot 🚀

Welcome to the **Blog Service**, a robust backend solution built with **Java Spring Boot**. This application allows users to create, retrieve, update, and manage blogs efficiently. With AI-powered text summarization and seamless AWS deployment, this project is designed for scalability and high performance.

## 🌟 Features

### ✅ Blog Management (Spring Boot + JPA)

- Create and manage blog posts with **title, content, author, and timestamps**.
- Retrieve all blogs with **pagination support**.
- Fetch a **single blog** by ID.
- **Update** existing blogs and **delete** if needed.
- Uses **Spring Data JPA** for database operations with PostgreSQL/MySQL.

### 🤖 AI-Powered Summarization (Basic NLP Integration)

- Generate **concise summaries** of blog content using the **Gemini LLM**.
- Saves time by providing **quick insights** into lengthy blog posts.

## 🎁 Quick Enhancement

- ⚡ **Redis caching** to bo
- **Dockerized** for easy deployment.ost performance on frequently accessed blogs.

## 🛠️ Getting Started

### Prerequisites

Before you begin, ensure you have:

- **Java 17+**
- **Spring Boot**
- **PostgreSQL/MySQL**
- **Docker** (for containerization)
- **AWS Account** (for cloud deployment)

### Environment Variables

This project requires an environment variable named `API_KEY` that contains the Gemini API key. Make sure to add this to your `.env` file:

```env
API_KEY=your_gemini_api_key_here
```

### 🔧 Setup & Running Locally

1. **Clone the repository:**
   ```sh
   git clone https://github.com/blog-service/blog-service.git
   cd blog-service
   ```
2. **Configure database connection** in `application.properties` or `application.yml`.
3. **Run the application:**
   ```sh
   docker-compose --env-file .env up
   ```

## 🚀 Deployment Guide

### 🌍 Docker Deployment

1. **Push the code to GitHub Repository**
   ```sh
   git add .
   git commit -m "Initial commit"
   git push origin main
   ```
2. **Create an EC2 instance**
   - Launch an EC2 instance (Ubuntu) from the AWS Management Console.
   - Configure the security group to allow inbound traffic on port `8080`.

3. **Install Docker and Git on EC2**
   ```sh
   sudo apt update
   sudo apt install docker.io git -y
   sudo systemctl start docker
   sudo systemctl enable docker
   ```

4. **Clone the repository**
   ```sh
   git clone https://github.com/blog-service/blog-service.git
   cd blog-service
   ```

5. **Add environment variable file**
   - Create a `.env` file in the project root directory and add the required environment variables.
   ```env
   API_KEY=your_gemini_api_key_here
   ```

6. **Run Docker Compose**
   ```sh
   sudo docker-compose --env-file .env up
   ```

Your application is now deployed on EC2 and accessible on port `8080`.

### Why EC2?

While Kubernetes and AWS Lambda are excellent choices for deployment, EC2 was chosen for the following reasons:

- **Simplicity**: EC2 provides a straightforward way to deploy and manage applications, making it ideal for small, monolithic applications with a few endpoints.
- **Control**: EC2 offers more control over the underlying infrastructure compared to AWS Lambda, which is beneficial for applications that may need specific configurations or optimizations.
- **Scalability**: Although Kubernetes excels in managing containerized applications at scale, EC2 can also handle scaling needs effectively for smaller applications without the added complexity of managing a Kubernetes cluster.
- **Cost-Effectiveness**: For small applications, EC2 can be more cost-effective than Kubernetes, which may require additional resources and management overhead.

By choosing EC2, we balance simplicity, control, and cost, making it a suitable option for deploying this blog service application.

> We can use the CI/CD pipeline and Terraform to automate the deployment process, but it will be more reasonable if we have a microservice application.

## API Reference

### Create a new blog

```http
   POST /api/blogs
```

**Request body -**

```json
{
  "title": "string",
  "author": "string",
  "content": "string"
}
```

### Fetch all blogs (paginated)

```http
   GET /api/blogs?page=0&size=10
```

| Parameter | Type  | Description               |
| :-------- | :---- | :------------------------ |
| `page`    | `int` | **Optional**. Page number |
| `size`    | `int` | **Optional**. Page size   |

### Fetch a blog by ID

```http
   GET /api/blogs/${id}
```

| Parameter | Type     | Description           |
| :-------- | :------- | :-------------------- |
| `id`      | `string` | **Required**. Blog ID |

### Update a blog

```http
   PUT /api/blogs/${id}
```

| Parameter | Type     | Description           |
| :-------- | :------- | :-------------------- |
| `id`      | `string` | **Required**. Blog ID |

**Request body -**

```json
{
  "title": "string",
  "author": "string",
  "content": "string"
}
```

### Delete a blog

```http
   DELETE /api/blogs/${id}
```

| Parameter | Type     | Description           |
| :-------- | :------- | :-------------------- |
| `id`      | `string` | **Required**. Blog ID |

### Generate blog summary

```http
   POST /api/blogs/${id}/summary
```

| Parameter | Type     | Description           |
| :-------- | :------- | :-------------------- |
| `id`      | `string` | **Required**. Blog ID |

The response for all the request will be in following format:

```json
{
   "status": "SUCCESS",
   "message": "Response message",
   "data": "Response data"
   "timestamp": "2025-03-14T04:19:34.961016178",
   "statusCode": 200
}
````

## 👤 Author

**[Vikas Singh](https://github.com/xanderbilla)**
