import axios from "axios";
import config from "../common/Config";

export const getPrices = async (startDate, endDate) => {
  try {
    const res = await axios.get(
      `${config.apiServerUrl}/findPrices?startDate=${startDate}&endDate=${endDate}`
    );
    return res.data;
  } catch (error) {
    console.error(error);
  }
};

export const getAccountSnapshots = async (
  startDate,
  endDate,
  maxProfitRate,
  investmentPerMonth
) => {
  try {
    const res = await axios.get(
      `${config.apiServerUrl}/findAccountSnapshots?startDate=${startDate}&endDate=${endDate}&maxProfitRate=${maxProfitRate}&investmentPerMonth=${investmentPerMonth}`
    );
    return res.data;
  } catch (error) {
    console.error(error);
  }
};
