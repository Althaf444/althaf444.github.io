package com.mint.entity;

/**
 * Enum representing the status of a synchronization operation.
 */
public enum SyncStatus {
    IDLE,       // Không có hoạt động đồng bộ
    SYNCING,    // Đang đồng bộ
    SUCCESS,    // Đồng bộ thành công
    FAILED      // Đồng bộ thất bại
}

