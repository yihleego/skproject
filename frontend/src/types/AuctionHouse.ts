export type AuctionRecord = {
  id: number;
  bidPrice: number;
  bidStatus: string;
  buyPrice: number;
  count: number;
  createdTime: number;
  estimatedEndTime: number;
  featured: boolean;
  timeLeft: string;
  updatedTime: number;
  item: AuctionItem;
  variant?: AuctionItemVariant[];
};

export type AuctionItem = {
  description: string;
  group: string;
  icon: string;
  level: boolean;
  name: string;
  star: number;
  colorization?: AuctionItemColorization[];
};

export type AuctionItemColorization = {
  color: number;
  offsets: number[];
  range: number[];
};

export type AuctionItemVariant = {
  name: string;
  value: string;
};
