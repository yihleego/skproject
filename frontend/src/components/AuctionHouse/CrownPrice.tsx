import React from "react";

import { default as enums } from "~/lib/enums";

interface Props {
  price: number;
  status: string;
}

const CrownPrice = (props: Props) => {
  const { price, status } = props;
  return (
    <div className="flex items-center whitespace-nowrap text-xs text-white">
      <img
        src="ui/icon/crown_32.png"
        width={12}
        height={12}
        className="mr-1"
        alt="cr."
      />
      {Intl.NumberFormat().format(price)}
      <span className="ml-1 rounded-md bg-slate-800/30 px-1 text-[0.6rem]">
        {enums.BidStatus.find((v) => v.key === status)?.value}
      </span>
    </div>
  );
};

export default CrownPrice;
