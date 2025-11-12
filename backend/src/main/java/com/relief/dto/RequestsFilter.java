package com.relief.dto;

import lombok.Data;

@Data
public class RequestsFilter {
    private String status; // new/assigned/...
    private String type; // food/water/...
    private Integer minSeverity;
    private String from; // ISO date
    private String to;   // ISO date
    private String bbox; // POLYGON WKT or bbox "minx,miny,maxx,maxy"
}





