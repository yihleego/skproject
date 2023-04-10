import React from "react";

interface Props {
  count: number;
}

const ItemCount = (props: Props) => {
  const { count } = props;
  if (count <= 1) return <></>;
  return (
    <div className="relative mr-2 rounded-md bg-white/90 px-1 text-center text-xs font-medium text-slate-800">
      x{count}
    </div>
  );
};

export default ItemCount;
