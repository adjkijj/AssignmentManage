# 📚 Hệ Thống Quản Lý Bài Tập (Assignment Management System)

> Ứng dụng web quản lý bài tập trực tuyến dành cho trường đại học, được xây dựng theo mô hình **MVC** sử dụng **Java Servlet/JSP**.

---

## 📋 Mục Lục

- [Giới Thiệu](#-giới-thiệu)
- [Tính Năng](#-tính-năng)
- [Công Nghệ Sử Dụng](#-công-nghệ-sử-dụng)
- [Kiến Trúc Dự Án](#-kiến-trúc-dự-án)
- [Cấu Trúc Thư Mục](#-cấu-trúc-thư-mục)
- [Cơ Sở Dữ Liệu](#-cơ-sở-dữ-liệu)
- [Hướng Dẫn Cài Đặt](#-hướng-dẫn-cài-đặt)
- [Tài Khoản Mẫu](#-tài-khoản-mẫu)
- [Ảnh Chụp Màn Hình](#-ảnh-chụp-màn-hình)
- [Đóng Góp](#-đóng-góp)
- [Giấy Phép](#-giấy-phép)

---

## 🎯 Giới Thiệu

**Assignment Management System** là một hệ thống quản lý bài tập toàn diện, hỗ trợ 3 vai trò người dùng: **Admin**, **Giảng viên (Instructor)** và **Sinh viên (Student)**. Hệ thống cho phép giảng viên tạo bài tập, quản lý lớp học, chấm điểm; sinh viên nộp bài, xem điểm; và admin quản trị toàn bộ hệ thống.

Dự án được phát triển trong khuôn khổ môn học **PRJ301 - Web Development with Java** tại trường **FPT University**, học kỳ Spring 2026.

---

## ✨ Tính Năng

### 👨‍💼 Admin
- Dashboard thống kê tổng quan hệ thống
- Quản lý người dùng (CRUD, kích hoạt/vô hiệu hóa tài khoản)
- Import người dùng hàng loạt từ file CSV
- Quản lý khóa học, học kỳ, môn học
- Quản lý đăng ký (enrollment) sinh viên vào lớp
- Quản lý thông báo
- Xem nhật ký hoạt động (Audit Log)

### 👨‍🏫 Giảng Viên (Instructor)
- Dashboard thống kê khóa học và bài tập
- Tạo, chỉnh sửa, xóa bài tập (hỗ trợ soft/hard deadline)
- Nhân bản (duplicate) bài tập
- Chấm điểm bài nộp (hỗ trợ chấm theo Rubric)
- Quản lý nhóm (tạo nhóm, thêm/xóa thành viên)
- Chấm điểm theo nhóm (Quick Fill cho tất cả thành viên)
- Quản lý tài liệu khóa học (upload file, thêm link)
- Xuất dữ liệu (export) điểm số
- Gửi nhắc nhở cho sinh viên
- Xem thống kê bài tập và bảng điểm (Gradebook)
- Tạo thông báo cho lớp

### 👨‍🎓 Sinh Viên (Student)
- Dashboard cá nhân
- Đăng ký khóa học
- Xem danh sách bài tập và nộp bài (hỗ trợ nhiều phiên bản)
- Xem lịch sử nộp bài
- Xem điểm và phản hồi từ giảng viên
- Xem Rubric chấm điểm
- Xem nhóm của mình và danh sách thành viên
- Xem tài liệu khóa học
- Nhận thông báo

### 🔐 Xác Thực & Bảo Mật
- Đăng nhập / Đăng ký
- Quên mật khẩu / Đặt lại mật khẩu
- Đổi mật khẩu
- Phân quyền theo vai trò (Filter-based Authorization)
- Mã hóa mật khẩu (BCrypt)
- Xác thực file upload (kiểm tra định dạng, kích thước)

---

## 🛠 Công Nghệ Sử Dụng

| Thành phần | Công nghệ |
|---|---|
| **Ngôn ngữ** | Java 8+ |
| **Web Framework** | Java Servlet 4.0, JSP |
| **View Engine** | JSP + JSTL + Custom Tags |
| **Cơ sở dữ liệu** | Microsoft SQL Server |
| **Kết nối DB** | JDBC (sqljdbc4) |
| **Giao diện** | Bootstrap 5, HTML5, CSS3 |
| **Build Tool** | Apache Ant |
| **Server** | Apache Tomcat 9+ |
| **IDE** | Apache NetBeans |
| **Bảo mật** | BCrypt (jBCrypt) |

---

## 🏗 Kiến Trúc Dự Án

Dự án tuân theo mô hình **MVC (Model - View - Controller)**:

```
┌─────────────────────────────────────────────┐
│                   Client                     │
│              (Trình duyệt Web)               │
└──────────────────┬──────────────────────────┘
                   │ HTTP Request/Response
┌──────────────────▼──────────────────────────┐
│              Filter Layer                    │
│  ┌──────────────┐  ┌─────────────────────┐  │
│  │Authentication│  │  Authorization      │  │
│  │   Filter     │  │    Filter           │  │
│  └──────────────┘  └─────────────────────┘  │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│           Controller (Servlet)               │
│  Xử lý request, điều hướng, gọi Service/DAO │
└──────────────────┬──────────────────────────┘
                   │
       ┌───────────┼───────────┐
       │           │           │
┌──────▼──┐ ┌─────▼────┐ ┌───▼──────┐
│ Service │ │   DAO    │ │  Model   │
│  Layer  │ │  Layer   │ │  Layer   │
└─────────┘ └─────┬────┘ └──────────┘
                  │
          ┌───────▼───────┐
          │  SQL Server   │
          │  Database     │
          └───────────────┘

┌─────────────────────────────────────────────┐
│            View (JSP + JSTL)                 │
│  ┌──────┐ ┌──────────┐ ┌─────────────────┐ │
│  │Admin │ │Instructor│ │    Student       │ │
│  │Pages │ │  Pages   │ │    Pages         │ │
│  └──────┘ └──────────┘ └─────────────────┘ │
└─────────────────────────────────────────────┘
```

---

## 📁 Cấu Trúc Thư Mục

```
AssignmentManage/
├── database/                          # Script SQL
│   ├── schema.sql                     # Schema chính + dữ liệu mẫu
│   ├── announcement.sql               # Bảng thông báo
│   ├── audit_log.sql                  # Bảng nhật ký
│   ├── feedback_templates.sql         # Mẫu phản hồi
│   └── rubric.sql                     # Bảng rubric
│
├── src/java/                          # Mã nguồn Java
│   ├── config/
│   │   └── DBContext.java             # Kết nối cơ sở dữ liệu
│   │
│   ├── model/                         # Lớp Model (POJO)
│   │   ├── User.java
│   │   ├── Course.java
│   │   ├── Assignment.java
│   │   ├── Submission.java
│   │   ├── Group.java
│   │   ├── Rubric.java
│   │   ├── RubricCriteria.java
│   │   ├── RubricGrade.java
│   │   ├── Announcement.java
│   │   ├── AuditLog.java
│   │   ├── CourseMaterial.java
│   │   ├── Enrollment.java
│   │   ├── Notification.java
│   │   ├── Comment.java
│   │   ├── Semester.java
│   │   ├── Subject.java
│   │   └── ... (các model khác)
│   │
│   ├── dao/                           # Lớp truy cập dữ liệu
│   │   ├── UserDAO.java
│   │   ├── CourseDAO.java
│   │   ├── AssignmentDAO.java
│   │   ├── SubmissionDAO.java
│   │   ├── GroupDAO.java
│   │   ├── RubricDAO.java
│   │   ├── EnrollmentDAO.java
│   │   ├── NotificationDAO.java
│   │   └── ... (các DAO khác)
│   │
│   ├── controller/                    # Servlet Controllers
│   │   ├── LoginServlet.java
│   │   ├── RegisterServlet.java
│   │   ├── DashboardServlet.java
│   │   ├── AssignmentServlet.java
│   │   ├── SubmissionServlet.java
│   │   ├── GradebookServlet.java
│   │   ├── GroupServlet.java
│   │   ├── RubricServlet.java
│   │   ├── AdminDashboardServlet.java
│   │   ├── AdminUserServlet.java
│   │   ├── AdminCourseServlet.java
│   │   └── ... (các servlet khác)
│   │
│   ├── service/                       # Lớp nghiệp vụ
│   │   ├── AuditLogService.java
│   │   ├── DashboardService.java
│   │   ├── RubricService.java
│   │   └── ... (các service khác)
│   │
│   ├── filter/                        # Servlet Filters
│   │   ├── AuthenticationFilter.java  # Kiểm tra đăng nhập
│   │   └── AuthorizationFilter.java   # Kiểm tra phân quyền
│   │
│   ├── tag/                           # Custom JSP Tags
│   │   ├── DateFormatTag.java         # Tag định dạng ngày
│   │   └── RoleBadgeTag.java          # Tag hiển thị badge vai trò
│   │
│   └── util/                          # Tiện ích
│       ├── PasswordUtil.java          # Mã hóa/giải mã mật khẩu
│       ├── FileValidationUtil.java    # Xác thực file upload
│       └── PasswordMigrationRunner.java
│
├── web/                               # Giao diện JSP
│   ├── index.jsp                      # Trang chủ (redirect đến login)
│   ├── auth/                          # Trang xác thực
│   │   ├── login.jsp
│   │   ├── register.jsp
│   │   ├── forgot-password.jsp
│   │   └── reset-password.jsp
│   │
│   ├── admin/                         # Trang Admin
│   │   ├── dashboard.jsp
│   │   ├── users.jsp
│   │   ├── courses.jsp
│   │   ├── semesters.jsp
│   │   ├── subjects.jsp
│   │   ├── enrollments.jsp
│   │   ├── announcements.jsp
│   │   ├── audit-log.jsp
│   │   └── ...
│   │
│   ├── instructor/                    # Trang Giảng viên
│   │   ├── dashboard.jsp
│   │   ├── assignments.jsp
│   │   ├── grade-submissions.jsp
│   │   ├── grade-group-members.jsp
│   │   ├── gradebook.jsp
│   │   ├── groups.jsp
│   │   ├── rubric-create.jsp
│   │   ├── rubric-grade.jsp
│   │   ├── materials-manage.jsp
│   │   └── ...
│   │
│   ├── student/                       # Trang Sinh viên
│   │   ├── dashboard.jsp
│   │   ├── assignments.jsp
│   │   ├── submit.jsp
│   │   ├── grades.jsp
│   │   ├── my-groups.jsp
│   │   ├── materials.jsp
│   │   └── ...
│   │
│   ├── common/                        # Component dùng chung
│   ├── css/                           # File CSS
│   ├── img/                           # Hình ảnh
│   └── WEB-INF/
│       ├── web.xml                    # Cấu hình Servlet
│       └── tags/                      # TLD files
│
├── build.xml                          # Build script (Apache Ant)
├── import_users.csv                   # File CSV mẫu import người dùng
└── README.md                          # File này
```

---

## 🗄 Cơ Sở Dữ Liệu

### Sơ Đồ Quan Hệ (ER Diagram)

Hệ thống sử dụng **Microsoft SQL Server** với 12 bảng chính:

```
┌──────────┐     ┌───────────┐     ┌──────────────┐
│  Users   │────<│Enrollments│>────│   Courses    │
│          │     └───────────┘     │              │
│ user_id  │                       │ course_id    │
│ username │──────────────────────<│ instructor_id│
│ password │                       │ semester_id  │>──┌──────────┐
│ full_name│                       │ subject_id   │>──│Semesters │
│ email    │                       └──────┬───────┘   └──────────┘
│ role     │                              │              ┌────────┐
└────┬─────┘                              │>─────────────│Subjects│
     │                                    │              └────────┘
     │         ┌─────────────┐            │
     │────────<│ Assignments │>───────────┘
     │         │             │
     │         │assignment_id│
     │         └──────┬──────┘
     │                │
     │    ┌───────────┼──────────────┐
     │    │           │              │
     │  ┌─▼────────┐ ┌▼──────────┐ ┌▼────────┐
     │──<│Submissions│ │ Comments  │ │ Groups  │
     │   │          │ │           │ │         │
     │   └──────────┘ └───────────┘ └────┬────┘
     │                                    │
     │         ┌──────────────┐           │
     │────────<│Group_Members │>──────────┘
     │         └──────────────┘
     │
     │    ┌──────────────┐  ┌──────────────┐  ┌───────────────────┐
     │───<│Notifications │  │ Audit_Logs   │  │PasswordResetTokens│
     │    └──────────────┘  └──────────────┘  └───────────────────┘
     │
     │    ┌────────────────┐
     └───<│CourseMaterials │
          └────────────────┘
```

### Danh Sách Bảng

| STT | Tên Bảng | Mô Tả |
|-----|----------|--------|
| 1 | `Users` | Người dùng (admin, giảng viên, sinh viên) |
| 2 | `Semesters` | Học kỳ |
| 3 | `Subjects` | Môn học |
| 4 | `Courses` | Khóa học / Lớp học |
| 5 | `Enrollments` | Đăng ký sinh viên vào lớp |
| 6 | `Assignments` | Bài tập |
| 7 | `Submissions` | Bài nộp (hỗ trợ nhiều phiên bản) |
| 8 | `Comments` | Bình luận (hỗ trợ threading) |
| 9 | `Notifications` | Thông báo |
| 10 | `Audit_Logs` | Nhật ký hoạt động |
| 11 | `Groups` | Nhóm bài tập |
| 12 | `Group_Members` | Thành viên nhóm |
| 13 | `PasswordResetTokens` | Token đặt lại mật khẩu |
| 14 | `CourseMaterials` | Tài liệu khóa học |

---

## 🚀 Hướng Dẫn Cài Đặt

### Yêu Cầu Hệ Thống

- **JDK** 8 trở lên
- **Apache Tomcat** 9.x trở lên
- **Microsoft SQL Server** 2019 trở lên
- **Apache NetBeans** 12+ (khuyến nghị) hoặc IDE tương đương
- **SQL Server Management Studio (SSMS)** để quản lý DB

### Các Bước Cài Đặt

#### 1️⃣ Clone dự án

```bash
git clone <repository-url>
cd AssignmentManage
```

#### 2️⃣ Thiết lập Cơ sở dữ liệu

1. Mở **SQL Server Management Studio (SSMS)**
2. Chạy file `database/schema.sql` để tạo database và dữ liệu mẫu
3. Chạy các file SQL bổ sung (nếu cần):
   - `database/announcement.sql`
   - `database/audit_log.sql`
   - `database/rubric.sql`
   - `database/feedback_templates.sql`

#### 3️⃣ Cấu hình kết nối Database

Mở file `src/java/config/DBContext.java` và cập nhật thông tin kết nối:

```java
private static final String SERVER_NAME = "localhost";
private static final String DB_NAME = "AssignmentManageDB";
private static final String PORT = "1433";
private static final String USER = "<tên_đăng_nhập_sql>";
private static final String PASSWORD = "<mật_khẩu_sql>";
```

#### 4️⃣ Mở dự án trong NetBeans

1. Mở **Apache NetBeans**
2. Chọn **File → Open Project**
3. Chọn thư mục `AssignmentManage`
4. Đảm bảo thêm các thư viện cần thiết vào project:
   - `sqljdbc4.jar` (JDBC Driver cho SQL Server)
   - `jstl-1.2.jar`
   - `jbcrypt-0.4.jar`
   - Các thư viện servlet API (thường có sẵn trong Tomcat)

#### 5️⃣ Deploy và chạy

1. Chuột phải vào project → **Run**
2. Ứng dụng sẽ chạy tại: `http://localhost:8080/AssignmentManage/`

---

## 🔑 Tài Khoản Mẫu

Sau khi chạy `schema.sql`, hệ thống sẽ có sẵn các tài khoản sau:

| Vai trò | Tên đăng nhập | Mật khẩu | Họ tên |
|---------|---------------|-----------|--------|
| **Admin** | `admin` | `admin123` | System Administrator |
| **Giảng viên** | `instructor1` | `instructor123` | Dr. John Smith |
| **Sinh viên** | `student1` | `student123` | Alice Johnson |
| **Sinh viên** | `student2` | `student123` | Bob Williams |

> ⚠️ **Lưu ý:** Mật khẩu mẫu ở trên chỉ dùng cho môi trường phát triển. Trong môi trường thực tế, hãy sử dụng mật khẩu mạnh và hệ thống sẽ tự động mã hóa bằng BCrypt.

---

## 📸 Ảnh Chụp Màn Hình

> *Sẽ được cập nhật sau.*

---

## 🤝 Đóng Góp

Nếu bạn muốn đóng góp cho dự án, vui lòng:

1. **Fork** repository này
2. Tạo branch mới: `git checkout -b feature/ten-tinh-nang`
3. Commit thay đổi: `git commit -m "Thêm tính năng mới"`
4. Push lên branch: `git push origin feature/ten-tinh-nang`
5. Tạo **Pull Request**

---

## 📄 Giấy Phép

Dự án này được phát triển cho mục đích học tập trong khuôn khổ môn **PRJ301** tại **FPT University**.

---

## 👤 Tác Giả

- **Sinh viên FPT University**
- **Môn học:** PRJ301 - Web Development with Java
- **Học kỳ:** Spring 2026

---

<p align="center">
  ⭐ Nếu dự án này hữu ích, hãy cho một sao nhé! ⭐
</p>
