package com.facilon.app.module.client.model.content;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "investor_solution_banners")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorSolutionBanners {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "solution_id")
    private Integer solutionId;

    @Column(name = "image_upload", columnDefinition = "TEXT")
    private String imageUpload;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1;
}
