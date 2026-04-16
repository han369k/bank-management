package com.ispan.bankmanagement.stock.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockService {
    @Autowired
    private StockRepository sRepos;

    public Stock insert(Stock s) {
        return sRepos.save(s);
    }

    public Stock findById(Integer id) {
        Optional<Stock> stock = sRepos.findById(id);
        return stock.orElse(null);
    }

    public List<Stock> findAll() {
        return sRepos.findAll();
    }

    public void deleteById(Integer id) {
        sRepos.deleteById(id);
    }

    @Transactional
    public Stock update(Integer id, Boolean status) {
        return sRepos.findById(id).map(stock -> {
            stock.setStatus(status);
            return sRepos.save(stock);
        }).orElseThrow(() -> new RuntimeException("找不到資料"));
    }
}
