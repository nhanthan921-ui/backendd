package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.entity.Bill;
import com.thantruongnhan.doanketthucmon.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/employee/bills")
public class BillController {

    private final BillService billService;

    @Autowired
    public BillController(BillService billService) {
        this.billService = billService;
    }

    // ADMIN và EMPLOYEE đều có thể xem tất cả hóa đơn
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<Bill> getAllBills() {
        return billService.getAllBills();
    }

    // ADMIN và EMPLOYEE đều có thể xem chi tiết hóa đơn
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Bill getBillById(@PathVariable Long id) {
        return billService.getBillById(id);
    }

    // EMPLOYEE có thể tạo hóa đơn (khi bán hàng)
    // ADMIN có thể tạo thủ công nếu cần
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Bill createBill(@RequestBody Bill bill) {
        return billService.createBill(bill);
    }

    // ADMIN và EMPLOYEE có thể cập nhật hóa đơn (ví dụ chỉnh lại tổng tiền, trạng
    // thái)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Bill updateBill(@PathVariable Long id, @RequestBody Bill bill) {
        return billService.updateBill(id, bill);
    }

    // Chỉ ADMIN được phép xóa hóa đơn
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
    }

    // Xuất hóa đơn ra file PDF (Admin + Nhân viên)
    @GetMapping("/{id}/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<byte[]> exportBill(@PathVariable Long id) {
        byte[] pdfBytes = billService.exportBillToPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=bill_" + id + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}
