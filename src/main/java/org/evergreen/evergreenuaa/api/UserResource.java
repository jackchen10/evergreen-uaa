package org.evergreen.evergreenuaa.api;


import lombok.Data;
import org.springframework.web.bind.annotation.*;

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

    @Data
    static class Profile {
        private String gender;
        private String idNo;
    }

}
