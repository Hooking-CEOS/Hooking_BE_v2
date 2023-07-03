package shop.hooking.hooking.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import java.time.LocalDateTime;

public abstract class BaseEntity {
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedTime;
}
