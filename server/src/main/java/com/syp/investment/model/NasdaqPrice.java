package com.syp.investment.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 26/2/2021
 */
@Data
public class NasdaqPrice {
  private final LocalDate date;
  private final double value;
}
