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

    public Stock update(Stock stock) {
        return sRepos.save(stock);
    }
}
