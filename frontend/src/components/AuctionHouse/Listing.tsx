import React from "react";
import type { AuctionRecord } from "~/types/AuctionHouse";
import ItemImage from "./ItemImage";
import ItemStar from "./ItemStar";
import TimeLeft from "./TimeLeft";
import ItemVariants from "./ItemVariants";
import CrownPrice from "./CrownPrice";
import UpdatedTime from "./UpdatedTime";
import ItemCount from "./ItemCount";

interface Props {
  auction: AuctionRecord;
}

const Listing = (props: Props) => {
  const { auction } = props;
  return (
    <div
      className={`relative rounded-md ${
        auction.featured
          ? "bg-slate-500 ring-1 ring-yellow-500"
          : "bg-slate-700"
      } p-2`}
    >
      {auction.featured ? (
        <div className="absolute -top-2 left-0 rounded-md rounded-bl-none bg-slate-500 px-1 text-center text-[0.65rem] text-slate-200 ring-1 ring-yellow-500">
          Featured Auction
        </div>
      ) : (
        <></>
      )}
      <div className="absolute -bottom-2 -left-2 shadow-xl">
        <ItemStar star={auction.item.star} />
      </div>
      <div className="flex w-full flex-row">
        <div className="relative z-10 w-10 shrink-0 self-center">
          <ItemImage
            icon={auction.item.icon}
            colorizations={auction.item.colorization}
          />
        </div>
        <div className="z-10 flex flex-col px-4">
          <div className="flex items-center text-xs font-medium text-white">
            <ItemCount count={auction.count} />
            {auction.item.name}
          </div>
          <div className="font-light italic text-white">
            <TimeLeft
              timeLeftString={auction.timeLeft}
              endDateTimeUnix={auction.estimatedEndTime}
            />
          </div>
        </div>
        <div className="ml-auto flex flex-col items-end">
          <CrownPrice price={auction.bidPrice} status={auction.bidStatus} />
          {auction.buyPrice > 0 && (
            <CrownPrice price={auction.buyPrice} status={"BUY_NOW"} />
          )}
        </div>
      </div>
      <ItemVariants variants={auction.variant} />
      <div className="absolute right-0 bottom-0 px-1 opacity-50">
        <UpdatedTime lastDateUpdatedUnix={auction.updatedTime} />
      </div>
    </div>
  );
};

export default Listing;
