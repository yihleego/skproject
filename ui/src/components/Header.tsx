import React from "react";
import EnergyPrice from "./AuctionHouse/EnergyPrice";

const Header = () => {
  return (
    <div className="p-5">
      <div className="flex items-center text-xl font-bold text-slate-200">
        <img
          src="ui/icon/inventory/guild/room/icon_room-auction.png"
          width={24}
          height={24}
          alt="auction house"
          className="mr-2"
        />
        Auction House
      </div>
      <EnergyPrice />
    </div>
  );
};

export default Header;
