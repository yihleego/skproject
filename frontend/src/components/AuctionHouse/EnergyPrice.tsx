import React from "react";
import type { ExchangeOffer } from "~/types/EnergyExchange";

const EnergyPrice = () => {
  const [bidPrice, setBidPrice] = React.useState<string | number | undefined>(
    "-"
  );
  const [askPrice, setAskPrice] = React.useState<string | number | undefined>(
    "-"
  );
  React.useEffect(() => {
    const baseUrl = window.location.protocol.startsWith("https") ? "https://api.ball.one" : "http://47.99.139.229:10000";
    fetch(`${baseUrl}/exchanges/latest`)
      .then((res) => res.json())
      .then(
        (data: { buyOffers: ExchangeOffer[]; sellOffers: ExchangeOffer[] }) => {
          if (data.buyOffers.length > 0) {
            setBidPrice(data.buyOffers[0]?.price);
          }
          if (data.sellOffers.length > 0) {
            setAskPrice(data.sellOffers[0]?.price);
          }
        }
      )
      .catch((e) => console.log(e));
  }, []);
  return (
    <div className="text-xs text-slate-200">
      <div className="mt-2 text-sm">Energy Prices:</div>
      <div className="flex items-center">
        Bid:{" "}
        <img
          src="ui/icon/crown_32.png"
          width={12}
          height={12}
          className="mx-1"
          alt="cr."
        />
        <span className="font-bold">{bidPrice}</span>&nbsp;Ask:
        <img
          src="ui/icon/crown_32.png"
          width={12}
          height={12}
          className="mx-1"
          alt="cr."
        />
        <span className="font-bold">{askPrice}</span>
      </div>
    </div>
  );
};

export default EnergyPrice;
