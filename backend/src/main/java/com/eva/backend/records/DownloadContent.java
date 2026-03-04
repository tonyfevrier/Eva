package com.eva.backend.records;

import org.springframework.http.HttpHeaders;

public record DownloadContent(HttpHeaders headers, byte[] fileBytes) {}
