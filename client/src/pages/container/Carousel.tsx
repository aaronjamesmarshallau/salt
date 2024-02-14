import { PropsWithChildren, TouchEventHandler } from "react";
import { Option, none, some } from "../../util/Option";

let trackedTouchIdentifier = none<number>();
let trackedTouchStartX = none<number>();
let carouselOffset = none<number>();

const onTouchStart = (evt: React.TouchEvent<HTMLDivElement>): void => {
  trackedTouchIdentifier = some(evt.targetTouches[0].identifier);
};

const onTouchEnd = (_evt: React.TouchEvent<HTMLDivElement>): void => {
  trackedTouchIdentifier = none();
};

const onTouchMove: TouchEventHandler<HTMLDivElement> = (
  evt: React.TouchEvent<HTMLDivElement>,
): void => {
  const dragRatio = 1;
  let relevantTouch = none<React.Touch>();

  for (let i = 0; i < evt.targetTouches.length; i++) {
    const currentTouch = evt.targetTouches[i];

    if (
      trackedTouchIdentifier
        .map((id) => id === currentTouch.identifier)
        .getOrElse(false)
    ) {
      relevantTouch = some(currentTouch);
    }
  }

  carouselOffset = relevantTouch.flatMap((touch) => {
    const currentX = touch.clientX;
    const maybeDeltaX = trackedTouchStartX.map((startX) => startX - currentX);

    return maybeDeltaX;
  });
};

interface CarouselProps {}

export const Carousel = (props: PropsWithChildren<CarouselProps>) => {
  const { children } = props;
  return (
    <div
      className="carousel"
      onTouchStart={onTouchStart}
      onTouchMove={onTouchMove}
      onTouchEnd={onTouchEnd}
    >
      {children}
    </div>
  );
};
