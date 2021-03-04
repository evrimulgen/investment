import React, { PureComponent } from "react";
import Grid from "@material-ui/core/Grid";

import { getPrices, getAccountSnapshots } from "../service/DataService";
import { getDate, isValidDate } from "../common/TimeUtil";
import DatesPicker from "../component/DatesPicker";
import InstrumentChart from "../component/InstrumentChart";
import InvestmentChart from "../component/InvestmentChart";

class App extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      startDate: new Date("2000-07-01"),
      endDate: new Date("2012-02-01"),
    };

    this.handleStartDateChange = this.handleStartDateChange.bind(this);
    this.handleEndDateChange = this.handleEndDateChange.bind(this);
  }

  handleStartDateChange(date) {
    this.setState({ startDate: date }, () => this.updateData());
  }

  handleEndDateChange(date) {
    this.setState({ endDate: date }, () => this.updateData());
  }

  async updateData() {
    if (
      !isValidDate(this.state.startDate) ||
      !isValidDate(this.state.endDate)
    ) {
      return;
    }

    const startDate = getDate(this.state.startDate);
    const endDate = getDate(this.state.endDate);
    const prices = await getPrices(startDate, endDate);

    const maxProfitRate1 = 0.15;
    const maxProfitRate2 = 0.20;
    const maxProfitRate3 = 0.25;
    const maxProfitRate4 = 0.30;
    const maxProfitRate5 = 0.35;

    const snapshotList1 = await getAccountSnapshots(
      startDate,
      endDate,
      maxProfitRate1,
      5000
    );

    const snapshotList2 = await getAccountSnapshots(
      startDate,
      endDate,
      maxProfitRate2,
      5000
    );

    const snapshotList3 = await getAccountSnapshots(
      startDate,
      endDate,
      maxProfitRate3,
      5000
    );

    const snapshotList4 = await getAccountSnapshots(
      startDate,
      endDate,
      maxProfitRate4,
      5000
    );

    const snapshotList5 = await getAccountSnapshots(
      startDate,
      endDate,
      maxProfitRate5,
      5000
    );

    const returnRates = [
      {
        number: maxProfitRate1,
        accountSnapshots: snapshotList1,
        color: "#0bb530",
      },
      {
        number: maxProfitRate2,
        accountSnapshots: snapshotList2,
        color: "#0687d1",
      },
      {
        number: maxProfitRate3,
        accountSnapshots: snapshotList3,
        color: "#9e0eab",
      },
      {
        number: maxProfitRate4,
        accountSnapshots: snapshotList4,
        color: "#c5cf0a",
      },
      {
        number: maxProfitRate5,
        accountSnapshots: snapshotList5,
        color: "#c43a31",
      },
    ];

    this.setState({ prices, returnRates });
  }

  componentDidMount() {
    this.updateData();
  }

  render() {
    const { startDate, endDate, prices, returnRates } = this.state;

    if (!prices || !returnRates) {
      return null;
    }

    return (
      <Grid container>
        <DatesPicker
          startDate={startDate}
          endDate={endDate}
          handleStartDateChange={this.handleStartDateChange}
          handleEndDateChange={this.handleEndDateChange}
        />
        <InstrumentChart prices={prices} />
        <InvestmentChart returnRates={returnRates} />
      </Grid>
    );
  }
}

export default App;
