import React from "react";
import type { AuctionItemColorization } from "~/types/AuctionHouse";

interface Props {
  icon: string;
  colorizations?: AuctionItemColorization[];
}

class Colorization {
  range: number[];
  offsets: number[];
  fhsv: number[];
  hsv: number[];

  constructor(color: number, range: number[], offsets: number[]) {
    this.range = range;
    this.offsets = offsets;
    this.hsv = Colorization.RGBtoHSB(
      (color >> 16) & 255,
      (color >> 8) & 255,
      color & 255
    );
    this.fhsv = Colorization.toFixedHSV(this.hsv);
  }

  recolorColor(hsv: number[]) {
    // @ts-expect-error i wanna kms, will get back to this later
    // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
    const hue = hsv[0] + this.offsets[0];
    // @ts-expect-error i wanna kms, will get back to this later
    // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
    const sat = Math.min(Math.max(hsv[1] + this.offsets[1], 0.0), 1.0);
    // @ts-expect-error i wanna kms, will get back to this later
    // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
    const val = Math.min(Math.max(hsv[2] + this.offsets[2], 0.0), 1.0);
    return Colorization.HSBtoRGB(hue, sat, val);
  }

  matches(hsv: number[], fhsv: number[]) {
    // @ts-expect-error i wanna kms, will get back to this later
    if (this.distance(fhsv[0], this.fhsv[0], 32767) > this.range[0] * 32767.0)
      return false;
    if (
      // @ts-expect-error i wanna kms, will get back to this later
      Math.abs(this.hsv[1] - hsv[1]) > this.range[1] ||
      // @ts-expect-error i wanna kms, will get back to this later
      Math.abs(this.hsv[2] - hsv[2]) > this.range[2]
    )
      return false;
    return true;
  }

  distance(a: number, b: number, N: number) {
    return a > b ? Math.min(a - b, b + N - a) : Math.min(b - a, a + N - b);
  }

  static RGBtoHSB(r: number, g: number, b: number) {
    let hue, saturation;
    const hsbvals = [0, 0, 0];
    let cmax = r > g ? r : g;
    if (b > cmax) cmax = b;
    let cmin = r < g ? r : g;
    if (b < cmin) cmin = b;

    const brightness = cmax / 255.0;
    if (cmax != 0) saturation = (cmax - cmin) / cmax;
    else saturation = 0;
    if (saturation == 0) hue = 0;
    else {
      const redc = (cmax - r) / (cmax - cmin);
      const greenc = (cmax - g) / (cmax - cmin);
      const bluec = (cmax - b) / (cmax - cmin);
      if (r == cmax) hue = bluec - greenc;
      else if (g == cmax) hue = 2.0 + redc - bluec;
      else hue = 4.0 + greenc - redc;
      hue = hue / 6.0;
      if (hue < 0) hue = hue + 1.0;
    }
    hsbvals[0] = hue;
    hsbvals[1] = saturation;
    hsbvals[2] = brightness;
    return hsbvals;
  }

  static HSBtoRGB(hue: number, saturation: number, brightness: number) {
    let r = 0,
      g = 0,
      b = 0;
    if (saturation == 0) {
      r = g = b = brightness * 255.0 + 0.5;
    } else {
      const h = (hue - Math.floor(hue)) * 6.0;
      const f = h - Math.floor(h);
      const p = brightness * (1.0 - saturation);
      const q = brightness * (1.0 - saturation * f);
      const t = brightness * (1.0 - saturation * (1.0 - f));
      switch (h | 0) {
        case 0:
          r = brightness * 255.0 + 0.5;
          g = t * 255.0 + 0.5;
          b = p * 255.0 + 0.5;
          break;
        case 1:
          r = q * 255.0 + 0.5;
          g = brightness * 255.0 + 0.5;
          b = p * 255.0 + 0.5;
          break;
        case 2:
          r = p * 255.0 + 0.5;
          g = brightness * 255.0 + 0.5;
          b = t * 255.0 + 0.5;
          break;
        case 3:
          r = p * 255.0 + 0.5;
          g = q * 255.0 + 0.5;
          b = brightness * 255.0 + 0.5;
          break;
        case 4:
          r = t * 255.0 + 0.5;
          g = p * 255.0 + 0.5;
          b = brightness * 255.0 + 0.5;
          break;
        case 5:
          r = brightness * 255.0 + 0.5;
          g = p * 255.0 + 0.5;
          b = q * 255.0 + 0.5;
          break;
      }
    }
    return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
  }

  static toFixedHSV(hsv: number[]) {
    const fhsv = [];
    // eslint-disable-next-line @typescript-eslint/no-for-in-array
    for (const i in hsv) {
      // @ts-expect-error i wanna kms, will get back to this later
      fhsv.push((hsv[i] * 32767.0) | 0);
    }
    return fhsv;
  }
}

const ItemImage = (props: Props) => {
  const { icon, colorizations } = props;
  const [image, setImage] = React.useState<string>(`${icon}`);
  const [colorized, setColorized] = React.useState<boolean>(false);
  return (
    // eslint-disable-next-line
    <img
      src={image}
      alt={icon}
      className={`h-auto w-full transition-opacity ${
        colorized ? "opacity-100" : "opacity-0"
      }`}
      crossOrigin="anonymous"
      onLoad={(e) => {
        // if colorization is not defined, return
        if (!colorizations || !image.endsWith(".png")) {
          setColorized(true);
          return;
        }

        const img = new Image();
        // @ts-expect-error i wanna kms, will get back to this later
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        img.src = e.target.src;
        img.crossOrigin = "anonymous";
        img.onload = () => {
          const canvas = document.createElement("canvas");
          const ctx = canvas.getContext("2d");
          if (!ctx) return;
          canvas.width = img.width;
          canvas.height = img.height;
          ctx.drawImage(img, 0, 0);
          const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
          const pixels = imageData.data;

          const colorizationObjects = [];
          // eslint-disable-next-line @typescript-eslint/no-for-in-array
          for (const i in colorizations) {
            const c = colorizations[i];
            if (!c) continue;
            colorizationObjects.push(
              new Colorization(c.color, c.range, c.offsets)
            );
          }

          for (let i = 0; i < pixels.length; i += 4) {
            const r = pixels[i];
            const g = pixels[i + 1];
            const b = pixels[i + 2];
            // @ts-expect-error i wanna kms, will get back to this later
            const hsv = Colorization.RGBtoHSB(r, g, b);
            const fhsv = Colorization.toFixedHSV(hsv);

            for (let j = 0; j < colorizationObjects.length; j++) {
              const colorization = colorizationObjects[j];
              if (colorization && colorization.matches(hsv, fhsv)) {
                const newRgb = colorization.recolorColor(hsv);
                pixels[i] = (newRgb >> 16) & 255;
                pixels[i + 1] = (newRgb >> 8) & 255;
                pixels[i + 2] = newRgb & 255;
                break;
              }
            }
          }
          ctx.putImageData(imageData, 0, 0);
          setImage(canvas.toDataURL());
          setColorized(true);
        };
      }}
    />
  );
};

export default ItemImage;
