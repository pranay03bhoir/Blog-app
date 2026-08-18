package com.substring.blogapp.config;

import com.substring.blogapp.models.*;
import com.substring.blogapp.repositories.*;
import com.substring.blogapp.utils.ReadingTimeUtils;
import com.substring.blogapp.utils.SlugUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            TagRepository tagRepository,
            ArticleRepository articleRepository,
            CommentRepository commentRepository,
            ArticleLikeRepository articleLikeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.articleLikeRepository = articleLikeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded with users and initial content.");
            return;
        }

        log.info("Seeding initial demo data for BlogApp...");

        // 1. Create Users
        User admin = new User();
        admin.setName("Pranay Bhoir (Admin)");
        admin.setEmail("admin@blogapp.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setEnabled(true);
        admin.setTagline("Lead DevOps & Cloud Architect");
        admin.setBio("Passionate about microservices architecture, cloud computing, automated CI/CD pipelines, and high-performance Java systems.");
        admin = userRepository.save(admin);

        User author1 = new User();
        author1.setName("Alex Rivera");
        author1.setEmail("alex@blogapp.com");
        author1.setPassword(passwordEncoder.encode("alex123"));
        author1.setRole(Role.ROLE_USER);
        author1.setEnabled(true);
        author1.setTagline("Senior Backend Engineer");
        author1.setBio("Building scalable backend applications using modern Java, Spring Boot, Kafka, and Kubernetes.");
        author1 = userRepository.save(author1);

        User author2 = new User();
        author2.setName("Sophia Chen");
        author2.setEmail("sophia@blogapp.com");
        author2.setPassword(passwordEncoder.encode("sophia123"));
        author2.setRole(Role.ROLE_USER);
        author2.setEnabled(true);
        author2.setTagline("AI & Cloud Solutions Specialist");
        author2.setBio("Writing about generative AI agents, LLM architectures, distributed databases, and high-reliability software systems.");
        author2 = userRepository.save(author2);

        // 2. Create Categories
        Category catDevops = new Category(null, "Cloud & DevOps", "Kubernetes, Docker, CI/CD pipelines, and scalable cloud architectures.", "cloud-devops", null);
        catDevops = categoryRepository.save(catDevops);

        Category catJava = new Category(null, "Spring Boot & Java", "In-depth guides on Spring Boot, modern Java versions, and reactive programming.", "spring-boot-java", null);
        catJava = categoryRepository.save(catJava);

        Category catSystemDesign = new Category(null, "System Design & Microservices", "Distributed systems, architectural design patterns, caching, and resiliency.", "system-design", null);
        catSystemDesign = categoryRepository.save(catSystemDesign);

        Category catAI = new Category(null, "Artificial Intelligence & ML", "LLM integrations, agentic workflows, and machine learning infrastructure.", "ai-ml", null);
        catAI = categoryRepository.save(catAI);

        Category catWeb = new Category(null, "Fullstack & Web Dev", "Modern responsive web designs, APIs, and modern frontend tools.", "fullstack-web", null);
        catWeb = categoryRepository.save(catWeb);

        // 3. Create Tags
        Tag tagDocker = tagRepository.save(new Tag("Docker", "docker"));
        Tag tagK8s = tagRepository.save(new Tag("Kubernetes", "kubernetes"));
        Tag tagSpringBoot = tagRepository.save(new Tag("SpringBoot", "springboot"));
        Tag tagJava = tagRepository.save(new Tag("Java", "java"));
        Tag tagMicroservices = tagRepository.save(new Tag("Microservices", "microservices"));
        Tag tagSecurity = tagRepository.save(new Tag("Security", "security"));
        Tag tagAI = tagRepository.save(new Tag("AI", "ai"));

        // 4. Create Articles
        String article1Content = """
                # Architecting Cloud-Native Microservices with Spring Boot and Docker

                In modern cloud engineering, decomposing monolithic applications into **loosely coupled microservices** is the gold standard for achieving independent scalability, fault tolerance, and velocity.

                ---

                ## 1. Core Principles of Cloud-Native Services

                When architecting microservices, keep these core principles in mind:
                - **Single Responsibility Principle**: Each microservice must own its domain logic and database.
                - **API-First Design**: Design robust RESTful or gRPC contracts before implementation.
                - **Statelessness**: Store state externally in Redis or relational databases like MySQL/PostgreSQL.
                - **Containerization**: Standardize deployments using Docker containers and Kubernetes pods.

                ```yaml
                version: '3.8'
                services:
                  microservice-app:
                    image: my-app:latest
                    ports:
                      - "8080:8080"
                    environment:
                      SPRING_PROFILES_ACTIVE: prod
                      DB_HOST: mysql-cluster
                ```

                ## 2. Implementing Resiliency Patterns
                Transient failures are inevitable in distributed systems. Use circuit breakers, rate limiters, and retry logic to keep downstream services healthy.

                Happy coding and happy deploying!
                """;

        Article article1 = new Article();
        article1.setTitle("Architecting Cloud-Native Microservices with Spring Boot and Docker");
        article1.setSlug(SlugUtils.toSlug(article1.getTitle()));
        article1.setShortDesc("Explore how to build high-scale, resilient microservices using Spring Boot, Docker containerization, and distributed database clustering.");
        article1.setContent(article1Content);
        article1.setReadingMinutes(ReadingTimeUtils.calculateReadingMinutes(article1Content));
        article1.setCategory(catDevops);
        article1.setUser(admin);
        article1.setStatus(Status.PUBLISHED);
        article1.setPaid(false);
        article1.setPrice(0.0);
        article1.setViewsCount(420L);
        article1.setLikesCount(38L);
        article1.setPublishedAt(LocalDateTime.now().minusDays(2));
        article1.setTags(new HashSet<>(Set.of(tagDocker, tagSpringBoot, tagMicroservices)));
        article1 = articleRepository.save(article1);

        String article2Content = """
                # Mastering Spring Security with JWT Stateless Authentication

                Securing modern single-page applications and mobile backends requires a stateless authentication approach. In this guide, we dive into **JSON Web Tokens (JWT)**.

                ---

                ## Why Choose Stateless JWT?
                1. **No Session Storage on Server**: Servers do not need to keep session state in memory.
                2. **Cross-Domain & Microservice Friendly**: The same JWT can be verified across multiple backend services.
                3. **Role-Based Access Control**: Encapsulate user roles and claims directly inside the signed token payload.

                ### Securing Endpoints with Spring Security:
                ```java
                @Bean
                public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                    return http
                        .csrf(AbstractHttpConfigurer::disable)
                        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/v1/auth/**").permitAll()
                            .anyRequest().authenticated()
                        )
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                        .build();
                }
                ```
                """;

        Article article2 = new Article();
        article2.setTitle("Mastering Spring Security with JWT Stateless Authentication");
        article2.setSlug(SlugUtils.toSlug(article2.getTitle()));
        article2.setShortDesc("Step-by-step breakdown of implementing secure stateless JWT authentication, password hashing, and role permissions in Spring Boot.");
        article2.setContent(article2Content);
        article2.setReadingMinutes(ReadingTimeUtils.calculateReadingMinutes(article2Content));
        article2.setCategory(catJava);
        article2.setUser(author1);
        article2.setStatus(Status.PUBLISHED);
        article2.setPaid(false);
        article2.setPrice(0.0);
        article2.setViewsCount(680L);
        article2.setLikesCount(74L);
        article2.setPublishedAt(LocalDateTime.now().minusDays(5));
        article2.setTags(new HashSet<>(Set.of(tagSpringBoot, tagSecurity, tagJava)));
        article2 = articleRepository.save(article2);

        String article3Content = """
                # Building Next-Generation AI Agents with LLMs and Real-Time APIs

                Autonomous AI agents are transforming software engineering by bridging large language models with deterministic APIs, database execution, and web tools.

                ---

                ## The 3 Pillars of AI Agent Architecture:
                - **Reasoning Loop**: Decomposing high-level user intents into actionable sub-tasks.
                - **Tool Invocation**: Dynamically calling REST APIs, running SQL queries, and manipulating files.
                - **Context Memory**: Retaining conversational state, past executions, and system feedback.
                """;

        Article article3 = new Article();
        article3.setTitle("Building Next-Generation AI Agents with LLMs and Real-Time APIs");
        article3.setSlug(SlugUtils.toSlug(article3.getTitle()));
        article3.setShortDesc("Learn how AI agents reason, select tools, and interact with live cloud infrastructure in real-time.");
        article3.setContent(article3Content);
        article3.setReadingMinutes(ReadingTimeUtils.calculateReadingMinutes(article3Content));
        article3.setCategory(catAI);
        article3.setUser(author2);
        article3.setStatus(Status.PUBLISHED);
        article3.setPaid(false);
        article3.setPrice(0.0);
        article3.setViewsCount(890L);
        article3.setLikesCount(95L);
        article3.setPublishedAt(LocalDateTime.now().minusDays(1));
        article3.setTags(new HashSet<>(Set.of(tagAI, tagMicroservices)));
        article3 = articleRepository.save(article3);

        // 5. Add initial comments
        Comment comment1 = new Comment();
        comment1.setContent("Outstanding article! The breakdown of Docker configurations and stateless design helped clarify a lot of questions in our team.");
        comment1.setUser(author2);
        comment1.setArticle(article1);
        commentRepository.save(comment1);

        Comment reply1 = new Comment();
        reply1.setContent("Thank you Sophia! In the next post we'll dive into Kubernetes ingress and service meshes.");
        reply1.setUser(admin);
        reply1.setArticle(article1);
        reply1.setParentComment(comment1);
        commentRepository.save(reply1);

        // 6. Add initial likes
        ArticleLike like1 = new ArticleLike();
        like1.setUser(author1);
        like1.setArticle(article1);
        articleLikeRepository.save(like1);

        ArticleLike like2 = new ArticleLike();
        like2.setUser(author2);
        like2.setArticle(article1);
        articleLikeRepository.save(like2);

        log.info("Demo data initialized successfully with users, categories, tags, articles, and discussions!");
    }
}
