package com.leyou.cart.controller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: taft
 * @Date: 2018-9-7 17:24
 */
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping()
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<Cart>> queryCarts(){
        List<Cart> carts = cartService.queryCarts();

        if (null==carts||0==carts.size()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(carts);
    }

    @PutMapping
    public ResponseEntity<Void> updateCart(
            @RequestParam("skuId") String skuId,
            @RequestParam("num") Integer num){
        this.cartService.updateNum(skuId,num);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCartById(@PathVariable("skuId")Long skuId){

        this.cartService.deleteCartById(skuId);
        return ResponseEntity.ok().build();
    }
}
