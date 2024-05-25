import React from "react";

interface Props {
  lastDateUpdatedUnix: number;
}

const UpdatedTime = (props: Props) => {
  const { lastDateUpdatedUnix } = props;
  const currentTime = new Date();
  const updatedTime = new Date(lastDateUpdatedUnix);
  const updatedTimeMS = Math.floor(
    (currentTime.getTime() - updatedTime.getTime()) / 1000
  );
  const updatedTimeString = `${Math.floor(updatedTimeMS / 3600)}h${Math.floor(
    (updatedTimeMS % 3600) / 60
  )}m`.replace("0h", "");
  return (
    <div className="text-xs font-light italic text-white">
      Last activity{" "}
      {updatedTimeString != "0m" ? `${updatedTimeString} ago` : "now"}
    </div>
  );
};

export default UpdatedTime;
