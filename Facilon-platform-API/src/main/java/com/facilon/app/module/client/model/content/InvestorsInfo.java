package com.facilon.app.module.client.model.content;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "investors_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorsInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "image_upload", columnDefinition = "TEXT")
    private String imageUpload;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "slug_url", columnDefinition = "TEXT")
    private String slugUrl;

    @Column(name = "status")
    @Builder.Default
    private Integer status = 1;
}
