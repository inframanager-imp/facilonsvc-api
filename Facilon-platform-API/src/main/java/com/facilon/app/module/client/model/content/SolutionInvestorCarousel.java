package com.facilon.app.module.client.model.content;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Carousel/slider content for solution pages.
 * Global CMS content.
 */
@Entity
@Table(name = "solution_investor_corosal")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolutionInvestorCarousel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "solution_id")
    private Integer solutionId;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "short_desc", columnDefinition = "TEXT")
    private String shortDesc;

    @Column(name = "image_upload", columnDefinition = "TEXT")
    private String imageUpload;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1;
}
