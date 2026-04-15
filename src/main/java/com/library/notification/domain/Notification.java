package com.library.notification.domain;

import com.library.shared.base.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    private String message;
}