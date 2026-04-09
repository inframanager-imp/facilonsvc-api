package com.facilon.app.module.client.model.content;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "investors_banners")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorsBanners {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "section_id")
    private Integer sectionId;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "image_upload", columnDefinition = "TEXT")
    private String imageUpload;

    @Column(name = "background_image", columnDefinition = "TEXT")
    private String backgroundImage;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1;
}
