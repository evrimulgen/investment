package com.syp.investment.repository;

import com.google.common.collect.Lists;
import com.syp.investment.model.NasdaqPrice;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 26/2/2021
 */
@Repository
public class NasdaqRepository {

  private final List<NasdaqPrice> prices = Lists.newArrayListWithExpectedSize(13000);

  @SneakyThrows
  public NasdaqRepository() {
    URL resource = getClass().getClassLoader().getResource("datasource.csv");
    CSVParser records =
        CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .parse(new FileReader(new File(Objects.requireNonNull(resource).toURI())));
    records.forEach(
        record -> {
          LocalDate date = LocalDate.parse(record.get(0), DateTimeFormatter.ISO_DATE);
          double price = Double.parseDouble(record.get(4));
          prices.add(new NasdaqPrice(date, price));
        });
  }

  public List<NasdaqPrice> getNasdaqPrices() {
    return prices;
  }
}
