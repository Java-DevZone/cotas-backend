package com.javadevzone.cotas.entity;

import com.javadevzone.cotas.entity.enums.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Asset {

    @Id
    private String ticket;

    @Enumerated(EnumType.STRING)
    private AssetType type;

    private Integer quantity;

}
