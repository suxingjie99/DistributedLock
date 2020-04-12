package org.example.controller;


import org.example.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author su
 */
@RestController
@RequestMapping("/goods")
public class SeckillController {

    @Autowired
    SeckillService seckillService;


    @GetMapping("/seckill")
    public void seckill(){
        seckillService.seckill();
    }

}
