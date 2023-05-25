package com.miron.springcourse.FirstSecurityClass.config;

import com.miron.springcourse.FirstSecurityClass.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

//тут после выхода видео немного поменялось https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
//Тут настраивается аутентификация и авторизация в дальнейшем.
//@EnableWebSecurity - дает понять что это конфигурационный класс Spring security
//extends WebSecurityConfigurerAdapter Наследуемся. В дальнешем обновить
@Configuration
@EnableWebSecurity
//ДЛя работы PreAuthorize - ограничения к коду
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final PersonDetailsService personDetailsService;

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

//    Настраиваем аунтефикацию
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(personDetailsService);
}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //Конфигурируем сам Spring Security
        //Конфигурируем авторизацию
        //Какую страница отвечает за вход, какая за ошибки.
        //Дает доступ к страницам в разных случаях (админ или пользователь; аутентифицированныцй или нет)
        // Спринг сам будет ждать логин и пароль по этому адресу
        //цепочка через "." удобно наращивается.
        //.csrf().disable() //Отключаем защиту от межсайтовой подделки запросов (отдельный урок). По умолчанию включена

        http
                .authorizeRequests()
                .requestMatchers("/auth/login","/auth/registration", "/error").permitAll()
                //Все остальные запросы не пускаем
                // .anyRequest().authenticated()
                //На все остальные страницы пускаем ROLE_USER и ROLE_ADMIN
                .anyRequest().hasAnyRole("USER","ADMIN")
                //После настроек доступа возвращаемя к настройке страницы для авторизации
                .and()
                //formLogin().loginPage("/auth/login") - говорим, что у нас собственная форма аутентификации.
                .formLogin().loginPage("/auth/login")
                //loginProcessingUrl("/process_login") - саму форму process_login мы не реализуем.
                .loginProcessingUrl("/process_login")
                //.defaultSuccessUrl("/hello",true);- после успешной авторизации перенаправить на страницу hello, true - без этого иногда не перенаправляет
                .defaultSuccessUrl("/hello", true)
                //.failureUrl("/auth/login?error"); -  при ошибке авторизации перенаправлять на /auth/login и
                //передавать в параметры ошибку
                .failureUrl("/auth/login?error")
                //Добавляем разлогирование
                .and()
                //если человек перешел на страницу /logout, то он разлогинится
                //Реализовывать саму страницу logout не нужно, Spring Security поймает обращение сам
                .logout().logoutUrl("/logout").logoutSuccessUrl("/auth/login");
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, PersonDetailsService personDetailsService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(personDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}