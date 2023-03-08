package com.rbt.test.controller;

import com.rbt.test.model.dto.ImportResponse;
import com.rbt.test.service.DataImportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/data-import")
@RequiredArgsConstructor
@SecurityRequirement(name = "rbt")
public class DataImportController {
    private final DataImportService dataImportService;

    @PostMapping(value = "/profiles/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('write')")
    public ResponseEntity<ImportResponse> importProfiles(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(dataImportService.importProfiles(file));
    }

    @PostMapping(value = "/total-number-of-days/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('write')")
    public ResponseEntity<ImportResponse> importTotalNumberOfDays(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(dataImportService.importTotalNumberOfDays(file));
    }

    @PostMapping(value = "/used-days/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('write')")
    public ResponseEntity<ImportResponse> importUsedDays(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(dataImportService.importUsedDays(file));
    }
}
