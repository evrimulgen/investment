package com.syp.investment.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.syp.investment.model.AccountSnapshot;
import com.syp.investment.model.NasdaqPrice;
import com.syp.investment.model.TransactionMonth;
import com.syp.investment.repository.NasdaqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 26/2/2021
 */
@Service
public class InvestmentService {

  private final NasdaqRepository repository;

  @Autowired
  public InvestmentService(NasdaqRepository repository) {
    this.repository = repository;
  }

  public List<AccountSnapshot> findAccountSnapshots(
      double maxProfitRate, double investmentPerMonth, String startDateStr, String endDateStr) {
    checkArgument(maxProfitRate > 0, "maxProfitRate must > 0");
    checkArgument(investmentPerMonth > 0, "investmentPerMonth must > 0");
    checkArgument(!Strings.isNullOrEmpty(startDateStr), "startDateStr must not be blank");
    checkArgument(!Strings.isNullOrEmpty(endDateStr), "endDateStr must not be blank");

    LocalDate startDate = LocalDate.parse(startDateStr);
    LocalDate endDate = LocalDate.parse(endDateStr);
    List<NasdaqPrice> prices = repository.getNasdaqPrices();

    int totalMonths = (int) (ChronoUnit.MONTHS.between(startDate, endDate) + 1);
    int totalDays = (int) (ChronoUnit.DAYS.between(startDate, endDate) + 1);
    Set<TransactionMonth> transactionMonths = Sets.newHashSetWithExpectedSize(totalMonths);
    List<AccountSnapshot> snapshots = Lists.newArrayListWithExpectedSize(totalDays);

    AccountSnapshot initSnapshot =
        new AccountSnapshot(startDate.minusDays(1), 0, 0, 0, 0, 0, 0, 0, 0, 0);
    snapshots.add(initSnapshot);

    int finalIndex = 0;
    for (int index = 0; index < prices.size(); index++) {
      NasdaqPrice todayNasdaqPrice = prices.get(index);
      LocalDate today = todayNasdaqPrice.getDate();

      if (today == null || startDate.compareTo(today) > 0 || today.compareTo(endDate) > 0) {
        continue;
      }

      finalIndex = index;
      boolean alreadyCounted = false;
      double todayPrice = todayNasdaqPrice.getValue();
      AccountSnapshot yesterdaySnapshot = getLastSnapshot(snapshots);
      double todayProfitRate = calculateProfitRate(yesterdaySnapshot, todayPrice);

      if (todayProfitRate >= maxProfitRate) {
        stopProfit(snapshots, todayNasdaqPrice);
        alreadyCounted = true;
      }

      TransactionMonth currentMonth = new TransactionMonth(today.getYear(), today.getMonthValue());

      if (!transactionMonths.contains(currentMonth)) {
        buyFund(investmentPerMonth, transactionMonths, snapshots, currentMonth, todayNasdaqPrice);
      } else if (!alreadyCounted) {
        updateSnapshot(snapshots, todayNasdaqPrice);
      }
    }

    AccountSnapshot lastSnapshot = getLastSnapshot(snapshots);
    if (lastSnapshot.getUnits() > 0) {
      stopProfit(snapshots, prices.get(finalIndex + 1));
    }

    snapshots.remove(0);
    return snapshots;
  }

  public List<NasdaqPrice> findPrices(String startDateStr, String endDateStr) {
    checkArgument(!Strings.isNullOrEmpty(startDateStr), "startDateStr must not be blank");
    checkArgument(!Strings.isNullOrEmpty(endDateStr), "endDateStr must not be blank");

    LocalDate startDate = LocalDate.parse(startDateStr);
    LocalDate endDate = LocalDate.parse(endDateStr);
    List<NasdaqPrice> prices = repository.getNasdaqPrices();

    int totalDays = (int) (ChronoUnit.DAYS.between(startDate, endDate) + 1);
    List<NasdaqPrice> targetPrices = Lists.newArrayListWithExpectedSize(totalDays);

    for (NasdaqPrice price : prices) {
      LocalDate today = price.getDate();
      if (today == null || startDate.compareTo(today) > 0 || today.compareTo(endDate) > 0) {
        continue;
      }

      targetPrices.add(price);
    }

    return targetPrices;
  }

  /** Buy new fund for the first time in the current month */
  private void buyFund(
      double investmentPerMonth,
      Set<TransactionMonth> transactionMonths,
      List<AccountSnapshot> snapshots,
      TransactionMonth currentMonth,
      NasdaqPrice nasdaqPrice) {

    LocalDate today = nasdaqPrice.getDate();
    double todayPrice = nasdaqPrice.getValue();
    AccountSnapshot yesterdaySnapshot = getLastSnapshot(snapshots);

    double todayUnits =
        calculateUnits(yesterdaySnapshot.getUnits(), investmentPerMonth, todayPrice);
    double todayPrinciple =
        calculatePrinciple(yesterdaySnapshot.getPrinciple(), investmentPerMonth);
    double todayTotalInvestment =
        calculateTotalInvestment(yesterdaySnapshot.getTotalInvestment(), investmentPerMonth);
    double todayMarketValue = calculateMarketValue(todayUnits, todayPrice);
    double todayCurrentGainOrLoss = calculateCurrentGainOrLoss(yesterdaySnapshot, todayPrice);
    double todayTotalGainOrLoss =
        yesterdaySnapshot.getPreviousGainOrLoss() + todayCurrentGainOrLoss;
    double todayReturnRate = calculateReturnRate(todayTotalGainOrLoss, todayTotalInvestment);

    AccountSnapshot todaySnapshot =
        new AccountSnapshot(
            today,
            todayPrice,
            todayUnits,
            todayPrinciple,
            todayTotalGainOrLoss,
            todayMarketValue,
            todayReturnRate,
            todayTotalInvestment,
            todayCurrentGainOrLoss,
            yesterdaySnapshot.getPreviousGainOrLoss());
    snapshots.add(todaySnapshot);
    transactionMonths.add(currentMonth);
  }

  /** Calculate gain or loss since last selling */
  private double calculateCurrentGainOrLoss(AccountSnapshot yesterdaySnapshot, double todayPrice) {
    double todayMarketValue = calculateMarketValue(yesterdaySnapshot.getUnits(), todayPrice);
    return todayMarketValue - yesterdaySnapshot.getPrinciple();
  }

  private double calculateMarketValue(double units, double price) {
    return units * price;
  }

  private double calculatePrinciple(double yesterdayPrinciple, double todayInvestment) {
    return yesterdayPrinciple + todayInvestment;
  }

  private double calculateProfitRate(AccountSnapshot yesterdaySnapshot, double todayPrice) {
    double currentGainOrLoss = calculateCurrentGainOrLoss(yesterdaySnapshot, todayPrice);
    return currentGainOrLoss / yesterdaySnapshot.getPrinciple();
  }

  private double calculateReturnRate(double totalGainOrLoss, double totalInvestment) {
    return totalGainOrLoss / totalInvestment;
  }

  private double calculateTotalGainOrLoss(AccountSnapshot yesterdaySnapshot, double todayPrice) {
    double newGainOrLoss = calculateCurrentGainOrLoss(yesterdaySnapshot, todayPrice);
    return newGainOrLoss + yesterdaySnapshot.getPreviousGainOrLoss();
  }

  private double calculateTotalInvestment(double yesterdayTotalInvestment, double todayInvestment) {
    return yesterdayTotalInvestment + todayInvestment;
  }

  private double calculateUnits(double yesterdayUnits, double todayInvestment, double todayPrice) {
    return todayInvestment / todayPrice + yesterdayUnits;
  }

  private AccountSnapshot getLastSnapshot(List<AccountSnapshot> snapshots) {
    return snapshots.get(snapshots.size() - 1);
  }

  /** Stop profit and sell all units */
  private void stopProfit(List<AccountSnapshot> snapshots, NasdaqPrice todayNasdaqPrice) {
    double todayPrice = todayNasdaqPrice.getValue();
    LocalDate today = todayNasdaqPrice.getDate();
    AccountSnapshot yesterdaySnapshot = getLastSnapshot(snapshots);

    double totalGainOrLoss = calculateTotalGainOrLoss(yesterdaySnapshot, todayPrice);
    double returnRate =
        calculateReturnRate(totalGainOrLoss, yesterdaySnapshot.getTotalInvestment());

    AccountSnapshot todaySnapshot =
        new AccountSnapshot(
            today,
            todayPrice,
            0,
            0,
            totalGainOrLoss,
            0,
            returnRate,
            yesterdaySnapshot.getTotalInvestment(),
            0,
            totalGainOrLoss);
    snapshots.add(todaySnapshot);
  }

  /** No transaction today so update the snapshot */
  private void updateSnapshot(List<AccountSnapshot> snapshots, NasdaqPrice todayNasdaqPrice) {
    AccountSnapshot yesterdaySnapshot = getLastSnapshot(snapshots);
    double todayPrice = todayNasdaqPrice.getValue();
    LocalDate today = todayNasdaqPrice.getDate();

    double todayMarketValue = calculateMarketValue(yesterdaySnapshot.getUnits(), todayPrice);
    double todayCurrentGainOrLoss = calculateCurrentGainOrLoss(yesterdaySnapshot, todayPrice);
    double todayTotalGainOrLoss =
        yesterdaySnapshot.getPreviousGainOrLoss() + todayCurrentGainOrLoss;
    double todayReturnRate =
        calculateReturnRate(todayTotalGainOrLoss, yesterdaySnapshot.getTotalInvestment());

    AccountSnapshot todaySnapshot =
        new AccountSnapshot(
            today,
            todayPrice,
            yesterdaySnapshot.getUnits(),
            yesterdaySnapshot.getPrinciple(),
            todayTotalGainOrLoss,
            todayMarketValue,
            todayReturnRate,
            yesterdaySnapshot.getTotalInvestment(),
            todayCurrentGainOrLoss,
            yesterdaySnapshot.getPreviousGainOrLoss());
    snapshots.add(todaySnapshot);
  }
}
