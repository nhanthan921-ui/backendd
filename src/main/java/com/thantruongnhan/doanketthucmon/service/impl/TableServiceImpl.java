package com.thantruongnhan.doanketthucmon.service.impl;

import com.thantruongnhan.doanketthucmon.entity.TableEntity;
import com.thantruongnhan.doanketthucmon.entity.enums.Status;
import com.thantruongnhan.doanketthucmon.repository.TableRepository;
import com.thantruongnhan.doanketthucmon.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private TableRepository tableRepository;

    @Override
    public List<TableEntity> getAllTables() {
        return tableRepository.findAll();
    }

    @Override
    public TableEntity getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn có id = " + id));
    }

    @Override
    public TableEntity createTable(TableEntity table) {
        table.setCreatedAt(LocalDateTime.now());
        table.setStatus(Status.FREE); // bàn mới mặc định là trống
        return tableRepository.save(table);
    }

    @Override
    public TableEntity updateTable(Long id, TableEntity table) {
        TableEntity existing = getTableById(id);
        existing.setNumber(table.getNumber());
        existing.setCapacity(table.getCapacity());
        existing.setStatus(table.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        return tableRepository.save(existing);
    }

    @Override
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    @Override
    public TableEntity occupyTable(Long id) {
        TableEntity table = getTableById(id);
        if (table.getStatus() == Status.OCCUPIED) {
            throw new RuntimeException("Bàn này đang được sử dụng!");
        }
        table.setStatus(Status.OCCUPIED);
        table.setUpdatedAt(LocalDateTime.now());
        return tableRepository.save(table);
    }

    @Override
    public TableEntity freeTable(Long id) {
        TableEntity table = getTableById(id);
        if (table.getStatus() == Status.FREE) {
            throw new RuntimeException("Bàn này đã trống rồi!");
        }
        table.setStatus(Status.FREE);
        table.setUpdatedAt(LocalDateTime.now());
        return tableRepository.save(table);
    }
}
