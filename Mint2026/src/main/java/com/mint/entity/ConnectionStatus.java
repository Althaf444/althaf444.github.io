package com.mint.entity;

/**
 * Enum representing the status of a bank connection.
 */
public enum ConnectionStatus {
    PENDING,    // Kết nối chưa được xác nhận
    LINKED,     // Kết nối thành công
    FAILED,     // Kết nối thất bại
    EXPIRED,    // Kết nối hết hạn
    SYNCING     // Đang đồng bộ dữ liệu
}

