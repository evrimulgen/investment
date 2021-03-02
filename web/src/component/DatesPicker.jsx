import React from "react";

import Grid from "@material-ui/core/Grid";
import DateFnsUtils from "@date-io/date-fns";
import {
  MuiPickersUtilsProvider,
  KeyboardDatePicker,
} from "@material-ui/pickers";

export default function DatesPicker(props) {
  return (
    <Grid container spacing={5}>
      <Grid container item xs={6} justify="flex-end" alignItems="center">
        <MuiPickersUtilsProvider utils={DateFnsUtils}>
          <KeyboardDatePicker
            disableToolbar
            variant="inline"
            format="yyyy/MM/dd "
            margin="normal"
            id="start-date-picker-inline"
            label="Start Date"
            value={props.startDate}
            onChange={props.handleStartDateChange}
            KeyboardButtonProps={{
              "aria-label": "start date",
            }}
          />
        </MuiPickersUtilsProvider>
      </Grid>

      <Grid container item xs={6} justify="flex-start" alignItems="center">
        <MuiPickersUtilsProvider utils={DateFnsUtils}>
          <KeyboardDatePicker
            disableToolbar
            variant="inline"
            format="yyyy/MM/dd "
            margin="normal"
            id="date-picker-inline"
            label="End Date"
            value={props.endDate}
            onChange={props.handleEndDateChange}
            KeyboardButtonProps={{
              "aria-label": "end date",
            }}
          />
        </MuiPickersUtilsProvider>
      </Grid>
    </Grid>
  );
}
