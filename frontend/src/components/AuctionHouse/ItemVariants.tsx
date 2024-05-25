import React from "react";
import type { AuctionItemVariant } from "~/types/AuctionHouse";
import enums from "~/lib/enums";

interface Props {
  variants?: AuctionItemVariant[];
}

const ItemVariants = (props: Props) => {
  const { variants } = props;
  if (!variants || variants.length === 0) return <></>;
  return (
    <div className="my-2 text-xs italic text-yellow-500">
      {variants.map((v) => {
        return (
          <div key={`${v.name}-${v.value}`}>
            {enums.VariantName.find((k) => k.key === v.name)?.value}:{" "}
            {enums.VariantValue.find((k) => k.key === v.value)?.value}
          </div>
        );
      })}
    </div>
  );
};

export default ItemVariants;
