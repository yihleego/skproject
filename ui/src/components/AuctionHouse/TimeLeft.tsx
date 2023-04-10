import React from "react";

import { default as enums } from "~/lib/enums";

interface Props {
  timeLeftString: string;
  endDateTimeUnix: number;
}

const TimeLeft = (props: Props) => {
  const { timeLeftString, endDateTimeUnix } = props;
  const currentTime = new Date();
  const endDateTimeObject = new Date(endDateTimeUnix);
  const timeLeftMS = Math.floor(
    (endDateTimeObject.getTime() - currentTime.getTime()) / 1000
  );
  const endingInString = `${Math.floor(timeLeftMS / 3600)}h${Math.floor(
    (timeLeftMS % 3600) / 60
  )}m`.replace("0h", "");
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

export default TimeLeft;
