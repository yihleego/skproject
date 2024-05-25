import React from "react";

import { default as enums } from "~/lib/enums";

interface Props {
  timeLeftString: string;
  endDateTimeUnix: number;
}

const TimeLeft = (props: Props) => {
  const { timeLeftString, endDateTimeUnix } = props;
  const endingInString = formatTimeDifference(endDateTimeUnix);
  return (
    <div
      className={`flex ${
        timeLeftString !== "VERY_SHORT" ? "flex-col" : "flex-row items-center"
      } text-xs text-white`}
    >
      <span>{enums.TimeLeft.find((v) => v.key === timeLeftString)?.value}</span>
      {timeLeftString !== "VERY_SHORT" ? (
        <span>~{endingInString} left</span>
      ) : (
        <img
          src="ui/icon/alert.png"
          width={12}
          height={12}
          className="mx-1"
          alt="cr."
        />
      )}
    </div>
  );
};

function formatTimeDifference(endDateTimeUnix: number) {
  let diff = endDateTimeUnix - new Date().getTime();

  if (diff <= 0) {
    return "0m";
  }

  const oneMinute = 1000 * 60;
  const oneHour = oneMinute * 60;
  const oneDay = oneHour * 24;

  const days = Math.floor(diff / oneDay);
  diff -= days * oneDay;

  const hours = Math.floor(diff / oneHour);
  diff -= hours * oneHour;

  const minutes = Math.floor(diff / oneMinute);

  let res = `${days}d${hours}h${minutes}m`;
  if (res.startsWith("0d")) {
    res = res.replace("0d", "");
  }
  if (res.startsWith("0h")) {
    res = res.replace("0h", "");
  }
  return res;
}

export default TimeLeft;
