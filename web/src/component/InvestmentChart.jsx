import Grid from "@material-ui/core/Grid";

import { VictoryChart, VictoryLine, VictoryAxis, VictoryTheme } from "victory";

const chartPadding = { top: 5, bottom: 20, left: 30, right: 5 };
const xAxisStyle = { tickLabels: { padding: 5, fontSize: 3 } };
const yAxisStyle = {
  tickLabels: { padding: 1, fontSize: 3 },
  axisLabel: { fontSize: 5, padding: 20 },
};

function getLineStyle(color) {
  return {
    data: {
      stroke: color,
      strokeWidth: 0.4,
    },
  };
}

function getDivStyle(color) {
  return {
    color: "white",
    padding: "10px",
    textAlign: "center",
    borderRadius: "6px",
    backgroundColor: color,
  };
}

export default function InvestmentChart(props) {
  return (
    <Grid container>
      <Grid item xs={12}>
        <VictoryChart
          height={100}
          theme={VictoryTheme.material}
          padding={chartPadding}
        >
          <VictoryAxis fixLabelOverlap style={xAxisStyle} />
          <VictoryAxis
            dependentAxis
            style={yAxisStyle}
            label="Cumulative Return"
          />

          {props.returnRates.map((returnRate) => {
            return (
              <VictoryLine
                key={returnRate.number}
                style={getLineStyle(returnRate.color)}
                data={returnRate.accountSnapshots}
                x="date"
                y="totalGainOrLoss"
              />
            );
          })}
        </VictoryChart>
      </Grid>

      <Grid container item spacing={2} justify="center" alignItems="center">
        {props.returnRates.map((returnRate) => {
          return (
            <Grid item key={returnRate.number}>
              <div style={getDivStyle(returnRate.color)}>
                {returnRate.number}
              </div>
            </Grid>
          );
        })}
      </Grid>
    </Grid>
  );
}
