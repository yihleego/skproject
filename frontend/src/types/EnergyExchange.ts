export type ExchangeRecord = {
  id: number;
  last_price: number;
  buy_offers: string;
  sell_offers: string;
  created_time: string;
};

export type ExchangeOffer = {
  price: number;
  volume: number;
};
