package org.evergreen.evergreenuaa.api;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserResource {

    @GetMapping("/greeting1")
    public String greeting1() {
        return "Hello, Spring Security~";
    }

    @PostMapping("/greeting2")
    public String greeting2(@RequestParam String name) {
        return "Hello, Spring Security~~~" + name;
    }

    @PutMapping ("/greeting3/{name}")
    public String makeGreeting(@PathVariable String name) {
        return "Hello, Spring Security~~~" + name;
    }

    @PostMapping("/greeting4")
    public String greeting4(@RequestParam String name, @RequestBody Profile profile) {
        return "Hello, Spring Security~~\n姓名: " + name  + "\n性别：" + profile.getGender()  + "\nIdNo：" + profile.getIdNo();
    }

   /* @GetMapping("/principal")
    public Authentication getPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }*/

    @GetMapping("/principal")
    public String getCurrentPrincipalName(Principal principal) {
        log.info("进入/api/principal方法");
        return principal.getName();
    }

    @Data
    static class Profile {
        private String gender;
        private String idNo;
    }

}
