import Grid from "@material-ui/core/Grid";
import {
  VictoryChart,
  VictoryLine,
  VictoryAxis,
  VictoryTheme,
  VictoryZoomContainer,
} from "victory";

const lineStyle = {
  data: {
    stroke: "#c43a31",
    strokeWidth: 0.4,
  },
};

const xAxisStyle = { tickLabels: { padding: 5, fontSize: 3 } };
const yAxisStyle = {
  tickLabels: { padding: 1, fontSize: 3 },
  axisLabel: { fontSize: 5, padding: 20 },
};
const chartPadding = { top: 5, bottom: 20, left: 30, right: 5 };

export default function InstrumentChart(props) {
  return (
    <Grid item xs={12}>
      <VictoryChart
        height={100}
        theme={VictoryTheme.material}
        padding={chartPadding}
        containerComponent={<VictoryZoomContainer />}
      >
        <VictoryAxis fixLabelOverlap style={xAxisStyle} />
        <VictoryAxis dependentAxis style={yAxisStyle} label="NASDAQ Composite" />
        <VictoryLine style={lineStyle} data={props.prices} x="date" y="value" />
      </VictoryChart>
    </Grid>
  );
}
