import type { PropsWithChildren } from "react";
import type { CircleArgs } from "../types/types";

type ArcOfCircleProps = PropsWithChildren<CircleArgs>;

export function ArcOfCircle({children, href, id, circleDimensions,  color='blue'}: ArcOfCircleProps){
    const D0x = circleDimensions.startingPoint[0];
    const D0y = circleDimensions.startingPoint[1];
    const D1x = circleDimensions.endPoint[0];
    const D1y = circleDimensions.endPoint[1];
    const sense = circleDimensions.rotationSense;
    
    return <a className="arc-circle" href={href}>
               <path id={id} d={`M ${D0x} ${D0y} A 75 75 0 0 ${sense} ${D1x} ${D1y}`} 
                   stroke={color} stroke-width="30" fill="none"/>
               <text className="arcCircleText" font-size="10" fill="white">
                   <textPath href={`#${id}`} startOffset="50%" text-anchor="middle" dominantBaseline="middle">
                       {children}
                   </textPath>
               </text>
           </a>
}