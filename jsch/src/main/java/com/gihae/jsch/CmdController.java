package com.gihae.jsch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class CmdController {

    private final CmdService cmdService;

    @PostMapping("/create")
    public void create(@RequestParam String password){
        cmdService.create(password);
    }

    @PostMapping("/access")
    public void access(@RequestParam String key){
        cmdService.access(key);
    }

    @PostMapping("/save")
    public void save(@RequestParam String key){
        cmdService.save(key);
    }

    @PostMapping("/load")
    public void load(@RequestParam String password, @RequestParam String key){
        cmdService.load(password, key);
    }

    @PostMapping("/delete")
    public void delete(@RequestParam String key){
        cmdService.delete(key);
    }
}
