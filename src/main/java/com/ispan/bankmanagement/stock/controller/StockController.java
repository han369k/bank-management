package com.ispan.bankmanagement.stock.controller;

import com.ispan.bankmanagement.stock.model.Stock;
import com.ispan.bankmanagement.stock.model.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService sService;

    public StockController(StockService sService) {
        this.sService = sService;
    }

    @GetMapping
    public List<Stock> getAll() {
        return sService.findAll();
    }

    @GetMapping("/{id}")
    public Stock getById(@PathVariable Integer id) {
        return sService.findById(id);
    }

    @PostMapping
    public Stock insert(@RequestBody Stock stock) {
        return sService.insert(stock);
    }

    @PutMapping
    public Stock update(@RequestBody Stock stock) {
        return sService.update(stock);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        sService.deleteById(id);
    }
}
