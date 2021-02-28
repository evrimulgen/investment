package com.syp.investment.controller;

import com.syp.investment.model.AccountSnapshot;
import com.syp.investment.model.NasdaqPrice;
import com.syp.investment.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 26/2/2021
 */
@RestController
public class InvestmentController {

  private final InvestmentService service;

  @Autowired
  public InvestmentController(InvestmentService service) {
    this.service = service;
  }

  @GetMapping("/findAccountSnapshots")
  public List<AccountSnapshot> findAccountSnapshots(
      @RequestParam("maxProfitRate") double maxProfitRate,
      @RequestParam("investmentPerMonth") double investmentPerMonth,
      @RequestParam("startDate") String startDate,
      @RequestParam("endDate") String endDate) {
    return service.findAccountSnapshots(maxProfitRate, investmentPerMonth, startDate, endDate);
  }

  @GetMapping("/findPrices")
  public List<NasdaqPrice> findPrices(
      @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
    return service.findPrices(startDate, endDate);
  }
}
