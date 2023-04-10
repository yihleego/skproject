import React from "react";
import type { AuctionRecord } from "~/types/AuctionHouse";
import { HiChevronLeft, HiChevronRight } from "react-icons/hi2";
import { HiFilter } from "react-icons/hi";
interface Props {
  total: number;
  page: number;
  pageSize: number;
  setPage: (page: number) => void;
  setAuctions: (auctions: AuctionRecord[]) => void;
  loading: boolean;
}

const Pagination = ({
  total,
  page,
  pageSize,
  setPage,
  setAuctions,
  loading,
}: Props) => {
  const handlePageChange = (page: number, clearAuctions: boolean) => {
    if (!loading) {
      setPage(page);
      if (clearAuctions) {
        setAuctions([]);
      }
    }
  };
  return (
    <div className="mt-2 flex gap-2">
      <Button onClick={() => handlePageChange(page - 1, true)}>
        <HiChevronLeft />
      </Button>
      <input
        className="h-8 w-8 rounded-md bg-white/10 text-center text-sm text-slate-200"
        value={page + 1}
        onFocus={(e) => e.currentTarget.select()}
        onInput={(e) => {
          const value = parseInt(e.currentTarget.value);
          if (!value || value > Math.ceil(total / pageSize) || value < 1) {
            handlePageChange(page, false);
            return;
          }
          handlePageChange(parseInt(e.currentTarget.value) - 1, true);
        }}
      />
      <Button onClick={() => handlePageChange(page + 1, true)}>
        <HiChevronRight />
      </Button>
    </div>
  );
};

const Filters = ({
  total,
  page,
  pageSize,
  setPage,
  setAuctions,
  loading,
}: Props) => {
  return (
    <div className="px-5">
      {" "}
      <div className="text-xs text-slate-200">
        Page <span className="font-bold">{page + 1}</span> out of{" "}
        <span className="font-bold">{Math.ceil(total / pageSize)}</span>
      </div>
      <div className="flex items-center justify-between">
        <Pagination
          total={total}
          page={page}
          pageSize={pageSize}
          setPage={setPage}
          setAuctions={setAuctions}
          loading={loading}
        />
        <Button
          onClick={() => {
            console.log("yo");
          }}
        >
          <HiFilter />
        </Button>
      </div>
    </div>
  );
};

const Button = ({
  onClick,
  children,
}: {
  onClick: () => void;
  children: React.ReactNode;
}) => {
  return (
    <button
      className="flex h-8 w-8 items-center justify-center rounded-md bg-white/10 text-sm text-slate-200"
      onClick={onClick}
    >
      {children}
    </button>
  );
};

export default Filters;
export { Pagination };
