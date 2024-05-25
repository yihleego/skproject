import React from "react";
import type { AuctionRecord } from "~/types/AuctionHouse";
import Listing from "./AuctionHouse/Listing";
import Header from "./Header";
import Filters, { Pagination } from "./AuctionHouse/Filters";

const AuctionHouse = () => {
  const [auctions, setAuctions] = React.useState<AuctionRecord[]>([]);
  const [page, setPage] = React.useState<number>(0);
  const [pageSize, setPageSize] = React.useState<number>(20);
  const [total, setTotal] = React.useState<number>(0);
  const [loading, setLoading] = React.useState<boolean>(false);
  const [search, setSearch] = React.useState<string>("");
  const query = () => {
    const baseUrl = window.location.protocol.startsWith("https") ? "https://api.ball.one" : "http://47.99.139.229:10000";
    setLoading(true);
    fetch(
      `${baseUrl}/auctions?timeLeft=VERY_SHORT,SHORT,MEDIUM,LONG,VERY_LONG&sort=estimatedEndTime:asc&page=${page}&size=${pageSize}&name=${search}`
    )
      .then((res) => res.json())
      .then(
        (data: {
          list: AuctionRecord[];
          page: number;
          pages: number;
          size: number;
          total: number;
        }) => {
          if (data.list) {
            setAuctions(data.list);
            setPage(data.page);
            setPageSize(data.size);
            setTotal(data.total);
          }
        }
      )
      .finally(() => setLoading(false))
      .catch((e) => console.log(e));
  };
  React.useEffect(() => {
      query();
  }, [page, pageSize, search]);

  return (
    <div className="flex min-h-screen flex-col overflow-x-hidden bg-gray-800">
      <div className="relative w-screen">
        <div className="xl:fixed">
          <Header />
          <Filters
            total={total}
            page={page}
            pageSize={pageSize}
            setPage={setPage}
            setAuctions={setAuctions}
            loading={loading}
          />
          <div className="px-5">
            <input type="text" placeholder="Search" className="font-bold"
                   value={search} onChange={(e) => {
              setSearch(e.target.value);
              query();
            }} />
          </div>
        </div>
        {loading ? (
          <div className="flex h-full min-h-[50vh] flex-row items-center justify-center">
            <div className="h-32 w-32 animate-spin rounded-full border-b-2 border-white"></div>
          </div>
        ) : (
          <>
            <div className="mx-auto flex max-w-[750px] flex-col gap-y-5 p-5">
              {auctions.map((auction) => {
                return <Listing auction={auction} key={auction.id} />;
              })}
            </div>
            <div className="mb-6 flex items-center justify-center">
              <Pagination
                total={total}
                page={page}
                pageSize={pageSize}
                setPage={setPage}
                setAuctions={setAuctions}
                loading={loading}
              />
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default AuctionHouse;
