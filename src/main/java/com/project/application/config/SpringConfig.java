package com.project.application.config;

import com.project.application.file.repository.FileRepository;
import com.project.application.file.service.FileService;
import com.project.application.file.service.FileServiceLocal;
import com.project.application.user.repository.UserRepository;
import com.project.application.user.service.UserService;
import com.project.application.user.service.UserServiceImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class SpringConfig {

    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory(){
        return new JPAQueryFactory(em);
    }

    @Bean
    public LayoutDialect layoutDialect(){
        return new LayoutDialect();
    }

//    @Bean
//    public BoardRepositoryCustom boardRepositoryCustom(){
//        return new BoardRepositoryCustomImpl(em, jpaQueryFactory());
//    }

    @Autowired private FileRepository fileRepository;

    @Bean
    public FileService fileService(){
        return new FileServiceLocal(fileRepository);
    }

    @Autowired private UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserService userService(){
        return new UserServiceImpl(userRepository, passwordEncoder());
    }
}
