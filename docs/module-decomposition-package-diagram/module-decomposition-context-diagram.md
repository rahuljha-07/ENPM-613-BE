```plantuml
graph TB
    subgraph "ILIM System"
        subgraph "ilim-frontend"
            UI[User Interface]
            PL[Presentation Logic]
            AC[API Consumption]
        end

        subgraph "ilim-backend"

            subgraph "Auth Module"
                AuthController
                AuthService
                CustomUserDetailsService
            end

            subgraph "Course Module"
                CourseController
                CourseService
                CoursePurchaseService
            end

            subgraph "Quiz Module"
                QuizController
                QuizService
            end

            subgraph "Video Module"
                VideoController
                VideoService
            end

            subgraph "User Module"
                UserController
                UserService
            end

            subgraph "Module Module"
                ModuleController
                ModuleService
                ModuleItemService
            end

            subgraph "Admin Module"
                AdminController
                AdminService
            end

            subgraph "QuizAttempt Module"
                QuizAttemptController
                QuizAttemptService
            end

            subgraph "Student Module"
                StudentController
                StudentService
            end

            subgraph "InstructorApp Module"
                InstructorAppController
                InstructorAppService
            end

            subgraph "Certificate Module"
                CertificateController
                CertificateService
            end

        end

        subgraph "ilimDeliveryServ"
            EmailController
            EmailService
            FilterConfig
            RateLimitingConfig
        end

        subgraph "ilimPaymentServ"
            PaymentService
            PaymentController
            KafkaConfig
            PaymentReturnController
            PaymentConfig
        end
    end

```