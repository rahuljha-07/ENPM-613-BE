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
                AuthController[AuthController]
                AuthService[AuthService]
                CustomUserDetailsService[CustomUserDetailsService]
            end

            subgraph "User Module"
                UserController[UserController]
                UserService[UserService]
            end

            subgraph "Course Module"
                CourseController[CourseController]
                CourseService[CourseService]
                CoursePurchaseService[CoursePurchaseService]
            end

            subgraph "Module Module"
                ModuleController[ModuleController]
                ModuleService[ModuleService]
                ModuleItemService[ModuleItemService]
            end

            subgraph "Video Module"
                VideoController[VideoController]
                VideoService[VideoService]
            end

            subgraph "Quiz Module"
                QuizController[QuizController]
                QuizService[QuizService]
            end

            subgraph "QuizAttempt Module"
                QuizAttemptController[QuizAttemptController]
                QuizAttemptService[QuizAttemptService]
            end

            subgraph "Student Module"
                StudentController[StudentController]
                StudentService[StudentService]
            end

            subgraph "Admin Module"
                AdminController[AdminController]
                AdminService[AdminService]
            end

            subgraph "InstructorApp Module"
                InstructorAppController[InstructorAppController]
                InstructorAppService[InstructorAppService]
            end

            subgraph "Certificate Module"
                CertificateController[CertificateController]
                CertificateService[CertificateService]
            end

            AuthController --> AuthService
            AuthService --> CustomUserDetailsService

            UserController --> UserService

            CourseController --> CourseService
            CourseController --> UserService
            CourseService --> CoursePurchaseService

            ModuleController --> ModuleService
            ModuleController --> UserService
            ModuleService --> CourseService
            ModuleService --> ModuleItemService

            VideoController --> VideoService
            VideoController --> UserService
            VideoService --> ModuleService
            VideoService --> CourseService

            QuizController --> QuizService
            QuizController --> UserService
            QuizService --> ModuleService
            QuizService --> CourseService

            QuizAttemptController --> QuizAttemptService
            QuizAttemptController --> UserService
            QuizAttemptService --> QuizService

            StudentController --> StudentService
            StudentController --> UserService
            StudentService --> CourseService
            StudentService --> QuizAttemptService

            AdminController --> AdminService
            AdminController --> UserService
            AdminController --> CourseService
            AdminService --> UserService
            AdminService --> InstructorAppService

            InstructorAppController --> InstructorAppService
            InstructorAppController --> UserService
            InstructorAppService --> UserService

            CertificateController --> CertificateService
            CertificateController --> UserService
            CertificateService --> CourseService
            CertificateService --> StudentService

        end

        subgraph "ilimDeliveryServ"
            EmailController[EmailController]
            EmailService[EmailService]
            FilterConfig[FilterConfig]
            RateLimitingConfig[RateLimitingConfig]
            
            EmailController --> EmailService
            EmailService --> FilterConfig
            EmailService --> RateLimitingConfig
        end

        subgraph "ilimPaymentServ"
            PaymentService[PaymentService]
            PaymentController[PaymentController]
            KafkaConfig[KafkaConfig]
            PaymentReturnController[PaymentReturnController]
            PaymentConfig[PaymentConfig]
            
            PaymentController --> PaymentService
            PaymentService --> PaymentReturnController
            PaymentService --> PaymentConfig
            KafkaPublisher ---> KafkaConfig
            PaymentReturnController --> KafkaPublisher
        end

        UI --> PL
        PL --> AC
        UI --> ilim-backend
        
        ilim-backend --> ilimDeliveryServ
        ilimDeliveryServ --> EmailService
        
        ilim-backend --> ilimPaymentServ

    end
```