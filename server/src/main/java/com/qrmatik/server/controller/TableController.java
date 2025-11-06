package com.qrmatik.server.controller;

import com.qrmatik.server.dto.TableDto;
import com.qrmatik.server.dto.TableInsertRequest;
import com.qrmatik.server.model.TableEntity;
import com.qrmatik.server.service.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public ResponseEntity<List<TableDto>> list() {
        List<TableEntity> list = tableService.listForCurrentTenant();
        return ResponseEntity.ok(list.stream().map(TableDto::fromEntity).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TableInsertRequest req) {
        try {
            Optional<TableEntity> e = tableService.createForCurrentTenant(req);
            if (e.isEmpty())
                return ResponseEntity.badRequest().body("Invalid or duplicate table code");
            return ResponseEntity.ok(TableDto.fromEntity(e.get()));
        } catch (com.qrmatik.server.service.PlanLimitExceededException ex) {
            return ResponseEntity.status(402).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") UUID id, @RequestBody TableInsertRequest req) {
        Optional<TableEntity> e = tableService.updateForCurrentTenant(id, req);
        if (e.isEmpty())
            return ResponseEntity.badRequest().body("Invalid table or duplicate code");
        return ResponseEntity.ok(TableDto.fromEntity(e.get()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        boolean ok = tableService.deleteForCurrentTenant(id);
        if (!ok)
            return ResponseEntity.badRequest().body("Invalid table");
        return ResponseEntity.noContent().build();
    }
}
