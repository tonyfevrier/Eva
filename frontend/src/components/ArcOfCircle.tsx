import type { PropsWithChildren } from "react";
import type { CircleArgs } from "../types/types";

type ArcOfCircleProps = PropsWithChildren<CircleArgs>;

export function ArcOfCircle({children, href, Id, circleDimensions,  color='blue'}: ArcOfCircleProps){
    const D0x = circleDimensions.startingPoint[0];
    const D0y = circleDimensions.startingPoint[1];
    const D1x = circleDimensions.endPoint[0];
    const D1y = circleDimensions.endPoint[1];
    const sense = circleDimensions.rotationSense;

    return (
        <g> {/* ✅ Groupe SVG */}
            <defs>
                <path id={Id} d={`M ${D0x} ${D0y} A 75 75 0 0 ${sense} ${D1x} ${D1y}`} 
                      stroke={color} strokeWidth="30" fill="none"/>
            </defs>
            
            <a id={`${Id}-link`} className="arc-circle" href={href}> {/* Lien SVG correct */}
                <use href={`#${Id}`} /> {/*  Utiliser le path défini */}
                <text className="arcCircleText" fontSize="10" fill="white">
                    <textPath href={`#${Id}`} startOffset="50%" textAnchor="middle" dominantBaseline="middle">
                        {children}
                    </textPath>
                </text>
            </a>
        </g>
    );
}