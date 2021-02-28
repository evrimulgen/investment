package com.syp.investment.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 26/2/2021
 */
@Data
public class AccountSnapshot {

  private LocalDate date;
  private double price;
  private double units;
  private double principle;
  private double marketValue;
  private double returnRate;
  private double totalInvestment;
  private double totalGainOrLoss;
  private double currentGainOrLoss;

  public AccountSnapshot(
      LocalDate date,
      double price,
      double units,
      double principle,
      double totalGainOrLoss,
      double marketValue,
      double returnRate,
      double totalInvestment,
      double currentGainOrLoss) {
    this.date = date;
    this.price = price;
    this.units = units;
    this.principle = principle;
    this.marketValue = marketValue;
    this.returnRate = returnRate;
    this.totalInvestment = totalInvestment;
    this.totalGainOrLoss = totalGainOrLoss;
    this.currentGainOrLoss = currentGainOrLoss;
  }
}
