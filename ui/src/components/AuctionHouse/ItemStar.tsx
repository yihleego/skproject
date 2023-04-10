import React from "react";
import { RiStarFill } from "react-icons/ri";
interface Props {
  star: number;
}

const ItemStar = (props: Props) => {
  const { star } = props;
  if (star <= 0) return <></>;
  return (
    <div className="relative">
      <div className="absolute inset-0 mt-[2px] flex items-center justify-center text-sm font-bold">
        {star}
      </div>
      <RiStarFill className="h-7 w-7 text-yellow-500" />
    </div>
  );
};

export default ItemStar;
